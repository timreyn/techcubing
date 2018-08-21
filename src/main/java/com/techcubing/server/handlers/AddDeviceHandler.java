package com.techcubing.server.handlers;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Message;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.util.List;
import java.util.Random;
import com.techcubing.server.framework.ServerState;
import com.techcubing.server.framework.ProtoDb;
import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.DeviceTypeProto.DeviceType;
import com.techcubing.server.util.ProtoUtil;

public class AddDeviceHandler extends BaseHandler {
  public AddDeviceHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    Device.Builder deviceBuilder =
      Device.newBuilder()
          .setId(String.valueOf(new Random().nextInt(90000000) + 10000000))
          .setVisibleName(queryParams.get("deviceName"))
          .setActivated(ProtoUtil.getCurrentTime());

    for (EnumValueDescriptor enumValue : DeviceType.getDescriptor().getValues()) {
      if (enumValue.getName().equals(queryParams.get("deviceType"))) {
        deviceBuilder.setType(DeviceType.valueOf(enumValue));
      }
    }
    deviceBuilder.setSerialNumber(queryParams.get("deviceSerialNumber"));

    ProtoDb.write(deviceBuilder.build(), serverState);
    redirectTo(URI.create("/manage_devices"), t);
  }

  @Override
  protected List<String> supportedMethods() {
    return List.of("POST");
  }
}
