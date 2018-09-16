package com.techcubing.server.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
import com.techcubing.server.util.ProtoUtil;
import com.techcubing.server.util.WcifUtil;

class AcquireScorecardImpl {
  ServerState serverState;

  public AcquireScorecardImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public AcquireScorecardResponse acquireScorecard(
      AcquireScorecardRequest request) {
    ProtoDb protoDb = serverState.getProtoDb();

    AcquireScorecardResponse.Builder responseBuilder =
      AcquireScorecardResponse.newBuilder();
    String scorecardId = request.getScorecardId();

    try {
      final Device device = protoDb.getById(
          Device.class, request.getContext().getDeviceId());

      // Check if the device already has a scorecard.
      List<Scorecard> scorecards = protoDb.getAllMatching(
          Scorecard.class, "active_device_id", device.getId());
      if (!scorecards.isEmpty()) {
        responseBuilder.getScorecardBuilder().mergeFrom(scorecards.get(0));
        responseBuilder.setStatus(
            AcquireScorecardResponse.Status.ALREADY_HAVE_A_SCORECARD);
        return responseBuilder.build();
      }

      // Try to atomically acquire the scorecard.
      ProtoDb.UpdateResult updateResult = protoDb.atomicUpdate(
          Scorecard.class, scorecardId,
          new ProtoDb.ProtoUpdate<Scorecard.Builder>() {
            @Override
            public boolean update(Scorecard.Builder builder) {
              if (!builder.getActiveDeviceId().isEmpty()) {
                responseBuilder.setStatus(
                    AcquireScorecardResponse.Status.SCORECARD_NOT_AVAILABLE);
                return false;
              }

              // Check which attempt the competitor is on, and whether they get
              // more.
              WcifRound round = null;
              try {
                round = protoDb.getIdField(builder, "round_id");
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

              for (int i = 0; i < builder.getAttemptsList().size();
                   i++) {
                Attempt.Builder attemptBuilder =
                  builder.getAttemptsBuilder(i);
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
                builder.getAttemptsBuilder(nextAttemptNumber - 1);

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
              nextPart.setAcquireTimestamp(ProtoUtil.getCurrentTime());
              nextPart.setPersonId(device.getPersonId());
              
              // Assign scrambles, if this wasn't already assigned.
              try {
                maybeAssignScrambles(builder);
              } catch (SQLException | IOException e) {
                responseBuilder.setStatus(
                    AcquireScorecardResponse.Status.INTERNAL_ERROR);
                e.printStackTrace();
                return false;
              }

              // TODO: Check whether this judge/scrambler is allowed to see
              // this scramble.
              builder.setActiveDeviceId(device.getId());
              responseBuilder.setAttemptNumber(nextAttemptNumber);
              return true;
            }
          });

      switch (updateResult) {
        case ID_NOT_FOUND:
        case RETRIES_EXCEEDED:
          responseBuilder.setStatus(
              AcquireScorecardResponse.Status.SCORECARD_NOT_FOUND);
          break;
        case DECLINED:
          break;
        case OK:
          Scorecard scorecard = protoDb.getById(Scorecard.class, scorecardId);
          responseBuilder.setScorecard(scorecard);
          break;
      }
    } catch (SQLException | IOException e) {
      responseBuilder.setStatus(
          AcquireScorecardResponse.Status.INTERNAL_ERROR);
      e.printStackTrace();
    }

    return responseBuilder.build();
  }

  private void maybeAssignScrambles(Scorecard.Builder builder)
      throws IOException, SQLException {
    ScrambleSet scrambleSet = null;
    if (builder.getScrambleSetId().isEmpty()) {
      // TODO: do this more systematically, based on group assignments.
      List<ScrambleSet> scrambles = serverState.getProtoDb().getAllMatching(
          ScrambleSet.class, "round_id", builder.getRoundId());
      scrambleSet = scrambles.get(0);
    }
    // TODO: assign scrambles for extra attempts.
    for (int attemptNumber = 0;
         attemptNumber < builder.getAttemptsList().size();
         attemptNumber++) {
      Attempt.Builder attemptBuilder = builder.getAttemptsBuilder(attemptNumber);
      if (attemptBuilder.getScrambleId().isEmpty()) {
        if (scrambleSet == null) {
          scrambleSet =
            serverState.getProtoDb().getIdField(builder, "scramble_set_id");
        }
        attemptBuilder.setScrambleId(scrambleSet.getScrambleId(attemptNumber));
      }
    }
  }
}
