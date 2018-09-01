package com.techcubing.server.services;

import com.google.protobuf.Message;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ServerState;
import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.DeviceTypeProto.DeviceType;
import com.techcubing.proto.ScorecardProto.Attempt;
import com.techcubing.proto.ScorecardProto.AttemptPart;
import com.techcubing.proto.ScorecardProto.AttemptPartOutcome;
import com.techcubing.proto.ScorecardProto.Scorecard;
import com.techcubing.proto.ScrambleProto.Scramble;
import com.techcubing.proto.ScrambleProto.ScrambleSet;
import com.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardRequest;
import com.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardResponse;
import com.techcubing.proto.wcif.WcifCutoff;
import com.techcubing.proto.wcif.WcifRound;
import com.techcubing.proto.wcif.WcifTimeLimit;
import com.techcubing.server.util.WcifUtil;

class AcquireScorecardImpl {
  ServerState serverState;

  public AcquireScorecardImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public AcquireScorecardResponse acquireScorecard(
      AcquireScorecardRequest request) {
    AcquireScorecardResponse.Builder responseBuilder =
      AcquireScorecardResponse.newBuilder();
    String scorecardId = request.getScorecardId();

    try {
      final Device device = (Device) ProtoDb.getById(
          request.getContext().getDeviceId(), Device.newBuilder(), serverState);

      // Check if the device already has a scorecard.
      PreparedStatement statement =
        serverState.getMysqlConnection().prepareStatement(
            "SELECT data FROM " +
            ProtoDb.getTable(Scorecard.getDescriptor(), serverState) +
            " where active_device_id = ?");
      statement.setString(1, device.getId());

      ResultSet results = statement.executeQuery();
      if (results.next()) {
        responseBuilder.getScorecardBuilder().mergeFrom(
            results.getBlob("data").getBinaryStream());
        responseBuilder.setStatus(
            AcquireScorecardResponse.Status.ALREADY_HAVE_A_SCORECARD);
        return responseBuilder.build();
      }

      // Try to atomically acquire the scorecard.
      ProtoDb.UpdateResult updateResult = ProtoDb.atomicUpdate(
          Scorecard.newBuilder(), scorecardId,
          new ProtoDb.ProtoUpdate() {
            @Override
            public boolean update(Message.Builder builder) {
              Scorecard.Builder scorecardBuilder = (Scorecard.Builder) builder;
              if (!scorecardBuilder.getActiveDeviceId().isEmpty()) {
                responseBuilder.setStatus(
                    AcquireScorecardResponse.Status.SCORECARD_NOT_AVAILABLE);
                return false;
              }

              // Check which attempt the competitor is on, and whether they get
              // more.
              WcifRound round = null;
              try {
                round = ProtoDb.getIdField(
                    scorecardBuilder, "round_id", serverState);
              } catch (SQLException | IOException e) {
                e.printStackTrace();
                responseBuilder.setStatus(
                    AcquireScorecardResponse.Status.INTERNAL_ERROR);
                return false;
              }
              int attempts = WcifUtil.attemptsForRound(round);
              WcifCutoff cutoff = round.getCutoff();
              boolean madeCutoff = cutoff.getNumberOfAttempts() == 0;
              int totalElapsedTime = 0;
              int nextAttemptNumber = 0;

              for (int i = 0; i < scorecardBuilder.getAttemptsList().size();
                   i++) {
                Attempt.Builder attemptBuilder =
                  scorecardBuilder.getAttemptsBuilder(i);
                if (attemptBuilder.getResult().getFinalTime() > 0) {
                  totalElapsedTime += attemptBuilder.getResult().getFinalTime();
                  if (!attemptBuilder.getResult().getIsDnf() &&
                      i < cutoff.getNumberOfAttempts() &&
                      attemptBuilder.getResult().getFinalTime() <
                      cutoff.getAttemptResult()) {
                    madeCutoff = true;
                  }
                } else {
                  nextAttemptNumber = i + 1;
                  break;
                }
              }

              if (nextAttemptNumber == 0) {
                responseBuilder.setStatus(
                    AcquireScorecardResponse.Status.ROUND_COMPLETED);
                return false;
              }

              if (nextAttemptNumber > cutoff.getNumberOfAttempts() &&
                  !madeCutoff) {
                responseBuilder.setStatus(
                    AcquireScorecardResponse.Status.MISSED_CUTOFF);
                return false;
              }

              // TODO: Support cross-round cumulative limits.
              WcifTimeLimit timeLimit = round.getTimeLimit();
              if (timeLimit.getCumulativeRoundIdsList().size() > 0 &&
                  totalElapsedTime >= timeLimit.getCentiseconds()) {
                responseBuilder.setStatus(
                    AcquireScorecardResponse.Status.CUMULATIVE_TIME_LIMIT);
                return false;
              }

              // Check whether everything is in the right order.
              Attempt.Builder attemptBuilder =
                scorecardBuilder.getAttemptsBuilder(nextAttemptNumber - 1);

              DeviceType lastDeviceType = DeviceType.UNKNOWN_DEVICE;
              for (AttemptPart part : attemptBuilder.getPartsList()) {
                if (part.getOutcome() == AttemptPartOutcome.OK &&
                    part.getDeviceType() != DeviceType.ADMIN) {
                  lastDeviceType = part.getDeviceType();
                }
              }
              if ((device.getType() == DeviceType.SCRAMBLER &&
                   lastDeviceType != DeviceType.UNKNOWN_DEVICE) ||
                  (device.getType() == DeviceType.JUDGE &&
                   lastDeviceType != DeviceType.SCRAMBLER)) {
                responseBuilder.setStatus(
                    AcquireScorecardResponse.Status.OUT_OF_ORDER_REQUEST);
                return false;
              }
              AttemptPart.Builder nextPart = attemptBuilder.addPartsBuilder();
              nextPart.setDeviceType(device.getType());
              nextPart.setDeviceId(device.getId());
              // TODO: store person ID.
              
              // Assign scrambles, if this wasn't already assigned.
              try {
                maybeAssignScrambles(scorecardBuilder, serverState);
              } catch (SQLException | IOException e) {
                responseBuilder.setStatus(
                    AcquireScorecardResponse.Status.INTERNAL_ERROR);
                e.printStackTrace();
                return false;
              }

              // TODO: Check whether this judge/scrambler is allowed to see
              // this scramble.
              scorecardBuilder.setActiveDeviceId(device.getId());
              responseBuilder.setAttemptNumber(nextAttemptNumber);
              return true;
            }
          }, serverState);

      switch (updateResult) {
        case ID_NOT_FOUND:
        case RETRIES_EXCEEDED:
          responseBuilder.setStatus(
              AcquireScorecardResponse.Status.SCORECARD_NOT_FOUND);
          break;
        case DECLINED:
          break;
        case OK:
          responseBuilder.setScorecard((Scorecard) ProtoDb.getById(
                scorecardId, Scorecard.newBuilder(), serverState));
          break;
      }
    } catch (SQLException | IOException e) {
      responseBuilder.setStatus(
          AcquireScorecardResponse.Status.INTERNAL_ERROR);
      e.printStackTrace();
    }

    return responseBuilder.build();
  }

  private void maybeAssignScrambles(
      Scorecard.Builder scorecardBuilder, ServerState serverState) 
      throws IOException, SQLException {
    ScrambleSet scrambleSet = null;
    if (scorecardBuilder.getScrambleSetId().isEmpty()) {
      // TODO: do this more systematically, based on group assignments.
      String tableName = ProtoDb.getTable(ScrambleSet.getDescriptor(), serverState);
      PreparedStatement statement = serverState.getMysqlConnection().prepareStatement(
          "SELECT data FROM " + tableName + " WHERE round_id = ? LIMIT 1");
      statement.setString(1, scorecardBuilder.getRoundId());
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        scrambleSet = ScrambleSet.parseFrom(results.getBlob("data").getBinaryStream());
      }
    }
    // TODO: assign scrambles for extra attempts.
    for (int attemptNumber = 0;
         attemptNumber < scorecardBuilder.getAttemptsList().size();
         attemptNumber++) {
      Attempt.Builder attemptBuilder = scorecardBuilder.getAttemptsBuilder(attemptNumber);
      if (attemptBuilder.getScrambleId().isEmpty()) {
        if (scrambleSet == null) {
          scrambleSet = ProtoDb.getIdField(
              scorecardBuilder, "scramble_set_id", serverState);
        }
        attemptBuilder.setScrambleId(scrambleSet.getScrambleId(attemptNumber));
      }
    }
  }
}
