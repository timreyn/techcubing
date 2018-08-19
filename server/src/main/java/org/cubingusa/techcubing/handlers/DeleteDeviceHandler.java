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
    ProtoDb.UpdateResult result = ProtoDb.atomicUpdate(
        Device.newBuilder(), queryParams.get("id"), new ProtoDb.ProtoUpdate() {
          @Override
          public boolean update(Message.Builder builder) {
            ((Device.Builder) builder).setDeactivated(ProtoUtil.getCurrentTime());
            return true;
          }
        }, serverState);
    if (result == ProtoDb.UpdateResult.RETRIES_EXCEEDED) {
      throw new RuntimeException("Too many retries updating device.");
    }
    redirectTo(URI.create("/manage_devices"), t);
  }

  @Override
  protected List<String> supportedMethods() {
    return List.of("POST");
  }
}
