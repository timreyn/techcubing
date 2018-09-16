package com.techcubing.server.services;

import com.google.protobuf.Message;
import java.io.IOException;
import java.sql.SQLException;

import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ServerState;
import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.services.ReleaseDeviceProto.ReleaseDeviceRequest;
import com.techcubing.proto.services.ReleaseDeviceProto.ReleaseDeviceResponse;

class ReleaseDeviceImpl {
  ServerState serverState;

  public ReleaseDeviceImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public ReleaseDeviceResponse releaseDevice(
      ReleaseDeviceRequest request) {
    ReleaseDeviceResponse.Builder responseBuilder =
      ReleaseDeviceResponse.newBuilder();

    String deviceId = request.getContext().getDeviceId();

    try {
      // Try to atomically release the scorecard.
      ProtoDb.UpdateResult updateResult = serverState.getProtoDb().atomicUpdate(
          Device.class, deviceId,
          new ProtoDb.ProtoUpdate<Device.Builder>() {
            @Override
            public boolean update(Device.Builder builder) {
              builder.clearPersonId();
              builder.clearAcquired();
              return true;
            }
          });

      switch (updateResult) {
        case ID_NOT_FOUND:
        case RETRIES_EXCEEDED:
          responseBuilder.setStatus(
              ReleaseDeviceResponse.Status.INTERNAL_ERROR);
          break;
        case DECLINED:
        case OK:
          break;
      }
    } catch (SQLException | IOException e) {
      responseBuilder.setStatus(
          ReleaseDeviceResponse.Status.INTERNAL_ERROR);
      e.printStackTrace();
    }

    return responseBuilder.build();
  }
}
