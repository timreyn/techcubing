package com.techcubing.server.handlers;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.techcubing.server.framework.ServerState;
import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.util.ProtoUtil;
import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.DeviceTypeProto.DeviceType;
import com.techcubing.proto.wcif.WcifPerson;

@Handler(path = "/manage_devices")
public class ManageDevicesHandler extends BaseHandler {
  public ManageDevicesHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    ProtoDb protoDb = serverState.getProtoDb();

    Map<DeviceType, List<Device>> devicesByType = new HashMap<>();
    for (EnumValueDescriptor enumValue : DeviceType.getDescriptor().getValues()) {
      DeviceType type = DeviceType.valueOf(enumValue);
      devicesByType.put(type, new ArrayList<Device>());
    }

    Map<String, Device> devicesBySerialNumber = new HashMap<>();
    for (Device device : protoDb.getAll(Device.class)) {
      devicesByType.get(device.getType()).add(device);
      devicesBySerialNumber.put(device.getSerialNumber(), device);
    }

    Map<String, WcifPerson> people = new HashMap<>();
    for (WcifPerson person : protoDb.getAll(WcifPerson.class)) {
      people.put(ProtoUtil.getId(person), person);
    }

    List<Object> attachedDevices = new ArrayList<>();
    AndroidDebugBridge bridge = serverState.getAndroidDebugBridge();
    for (IDevice iDevice : bridge.getDevices()) {
      String serialNumber = iDevice.getSerialNumber();
      String deviceString =
        iDevice.getProperty(IDevice.PROP_DEVICE_MANUFACTURER) + " " +
        iDevice.getProperty(IDevice.PROP_DEVICE_MODEL) + " v" +
        iDevice.getProperty(IDevice.PROP_BUILD_VERSION);
      Map<String, String> deviceInfo = new HashMap<>();
      deviceInfo.put("deviceString", deviceString);
      Device device = devicesBySerialNumber.get(serialNumber);
      if (device == null) {
        deviceInfo.put("deviceId", "");
      } else {
        deviceInfo.put("deviceId", "(" + device.getVisibleName() + ")");
      }
      deviceInfo.put("serialNumber", serialNumber);
      attachedDevices.add(deviceInfo);
    }

    Map<String, Object> model = new HashMap<>();
    model.put("persons", people);
    model.put("devicesByType", devicesByType);
    model.put("attachedDevices", attachedDevices);
    model.put("devicesBySerialNumber", devicesBySerialNumber);
    writeResponse(model, "manage_devices.html", t);
  }
}
