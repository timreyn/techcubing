package com.techcubing.server.services;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Message;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ServerState;
import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.DeviceTypeProto.DeviceType;
import com.techcubing.proto.ScorecardProto;
import com.techcubing.proto.ScorecardProto.Attempt;
import com.techcubing.proto.ScorecardProto.AttemptPart;
import com.techcubing.proto.ScorecardProto.Scorecard;
import com.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardRequest;
import com.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardResponse;
import com.techcubing.server.util.ProtoUtil;

class ReleaseScorecardImpl {
  ServerState serverState;

  public ReleaseScorecardImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public ReleaseScorecardResponse releaseScorecard(
      ReleaseScorecardRequest request) {
    ReleaseScorecardResponse.Builder responseBuilder =
      ReleaseScorecardResponse.newBuilder();
    String scorecardId = request.getScorecardId();

    try {
      final Device device = (Device) serverState.getProtoDb().getById(
          request.getContext().getDeviceId(), Device.newBuilder());

      // Try to atomically release the scorecard.
      ProtoDb.UpdateResult updateResult = serverState.getProtoDb().atomicUpdate(
          Scorecard.newBuilder(), scorecardId,
          new ProtoDb.ProtoUpdate() {
            @Override
            public boolean update(Message.Builder builder) {
              Scorecard.Builder scorecardBuilder = (Scorecard.Builder) builder;
              if (!scorecardBuilder.getActiveDeviceId().equals(device.getId())) {
                responseBuilder.setStatus(
                    ReleaseScorecardResponse.Status.SCORECARD_NOT_HELD_BY_DEVICE);
                return false;
              }

              // Merge data from the request.
              Attempt.Builder attemptBuilder =
                  scorecardBuilder.getAttemptsBuilder(
                      request.getAttemptNumber() - 1);

              AttemptPart.Builder lastPartBuilder =
                attemptBuilder.getPartsBuilder(
                    attemptBuilder.getPartsList().size() - 1);
              lastPartBuilder.addAllEvents(request.getEventsList());
              lastPartBuilder.setOutcome(request.getOutcome());
              if (!lastPartBuilder.getDeviceId().equals(device.getId())) {
                responseBuilder.setStatus(
                    ReleaseScorecardResponse.Status.SCORECARD_NOT_HELD_BY_DEVICE);
                return false;
              }
              lastPartBuilder.setReleaseTimestamp(ProtoUtil.getCurrentTime());

              // Store the result.
              if (device.getType() == DeviceType.JUDGE) {
                if (request.getResult().getFinalTime() == 0 &&
                    !request.getResult().getIsDnf()) {
                  responseBuilder.setStatus(
                      ReleaseScorecardResponse.Status.TIME_NOT_SET);
                  return false;
                }
                attemptBuilder.setResult(request.getResult());
              }
              scorecardBuilder.setActiveDeviceId("");
              return true;
            }
          });

      switch (updateResult) {
        case ID_NOT_FOUND:
        case RETRIES_EXCEEDED:
          responseBuilder.setStatus(
              ReleaseScorecardResponse.Status.SCORECARD_NOT_FOUND);
          break;
        case DECLINED:
        case OK:
          break;
      }
    } catch (SQLException | IOException e) {
      responseBuilder.setStatus(
          ReleaseScorecardResponse.Status.INTERNAL_ERROR);
      e.printStackTrace();
    }

    return responseBuilder.build();
  }
}
