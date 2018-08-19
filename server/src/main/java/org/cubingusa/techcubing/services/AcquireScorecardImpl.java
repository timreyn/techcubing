package org.cubingusa.techcubing.services;

import com.google.protobuf.Message;
import java.io.IOException;
import java.sql.SQLException;
import org.cubingusa.techcubing.framework.ProtoDb;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.proto.DeviceProto.Device;
import org.cubingusa.techcubing.proto.ScorecardProto.Scorecard;
import org.cubingusa.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardRequest;
import org.cubingusa.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardResponse;

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
              // TODO: Check for whether this device is allowed to acquire this
              // competitor:
              // -is it the right device type? (judge/scrambler)
              // -is the judge allowed to see this scramble?
              // -does the competitor have more attempts?
              scorecardBuilder.setActiveDeviceId(device.getId());
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
    }

    return responseBuilder.build();
  }
}
