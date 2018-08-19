package org.cubingusa.techcubing.handlers;

import com.google.protobuf.Message;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.util.List;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.framework.ProtoDb;
import org.cubingusa.techcubing.proto.DeviceProto.Device;
import org.cubingusa.techcubing.util.ProtoUtil;

public class DeleteDeviceHandler extends BaseHandler {
  public DeleteDeviceHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    Device device = (Device) ProtoDb.getById(
        queryParams.get("id"), Device.newBuilder(), serverState);
    if (device != null && device.getDeactivated().getSeconds() == 0) {
      device = device.toBuilder().setDeactivated(ProtoUtil.getCurrentTime()).build();
      ProtoDb.write(device, serverState);
    }
    redirectTo(URI.create("/manage_devices"), t);
  }

  @Override
  protected List<String> supportedMethods() {
    return List.of("POST");
  }
}
