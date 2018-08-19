package org.cubingusa.techcubing.handlers;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Message;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.util.List;
import java.util.Random;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.framework.ProtoDb;
import org.cubingusa.techcubing.proto.DeviceProto.Device;
import org.cubingusa.techcubing.proto.DeviceProto.DeviceType;
import org.cubingusa.techcubing.util.ProtoUtil;

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

    ProtoDb.write(deviceBuilder.build(), serverState);
    redirectTo(URI.create("/manage_devices"), t);
  }

  @Override
  protected List<String> supportedMethods() {
    return List.of("POST");
  }
}
