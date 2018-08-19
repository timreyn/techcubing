package org.cubingusa.techcubing.services;

import com.google.protobuf.Message;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.cubingusa.techcubing.framework.ProtoDb;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.proto.DeviceProto.Device;
import org.cubingusa.techcubing.proto.ScorecardProto.Scorecard;
import org.cubingusa.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardRequest;
import org.cubingusa.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardResponse;

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
      final Device device = (Device) ProtoDb.getById(
          request.getContext().getDeviceId(), Device.newBuilder(), serverState);

      // Try to atomically release the scorecard.
      ProtoDb.UpdateResult updateResult = ProtoDb.atomicUpdate(
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
              // TODO: check if there is any unfinished business on this attempt.
              scorecardBuilder.setActiveDeviceId("");
              return true;
            }
          }, serverState);

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
