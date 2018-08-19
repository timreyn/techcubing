package org.cubingusa.techcubing.handlers;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Message;
import com.sun.net.httpserver.HttpExchange;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.framework.ProtoDb;
import org.cubingusa.techcubing.proto.DeviceProto.Device;
import org.cubingusa.techcubing.proto.DeviceTypeProto.DeviceType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageDevicesHandler extends BaseHandler {
  public ManageDevicesHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    List<Message> deviceMessages =
      ProtoDb.getAll(Device.newBuilder(), serverState);
    Map<String, List<Device>> devicesByType = new HashMap<>();

    for (EnumValueDescriptor enumValue : DeviceType.getDescriptor().getValues()) {
      devicesByType.put(enumValue.getName(), new ArrayList<Device>());
    }

    for (Message deviceMessage : deviceMessages) {
      Device device = (Device) deviceMessage;
      String deviceTypeName = device.getType().getValueDescriptor().getName();
      devicesByType.get(deviceTypeName).add(device);
    }
    Map<String, Object> model = new HashMap<>();
    model.put("devicesByType", devicesByType);
    writeResponse(model, "manage_devices.html", t);
  }
}
