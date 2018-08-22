package com.techcubing.server.handlers;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Message;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import com.techcubing.server.framework.ServerState;
import com.techcubing.server.framework.ProtoDb;
import com.techcubing.proto.DeviceConfigProto.DeviceConfig;
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
    Device device = deviceBuilder.build();

    // Set up the device.
    DeviceConfig config =
      DeviceConfig.newBuilder()
          .setDevice(device)
          // TODO: this is hardcoded for emulators.
          .setServerHost("10.0.2.2")
          .setServerPort(serverState.getGrpcPort())
          .build();

    String configEncoded = Base64.getUrlEncoder().encodeToString(config.toByteArray());

    AndroidDebugBridge bridge = serverState.getAndroidDebugBridge();
    IDevice selectedDevice = null;
    for (IDevice iDevice : bridge.getDevices()) {
      if (iDevice.getSerialNumber().equals(device.getSerialNumber())) {
        selectedDevice = iDevice;
        break;
      }
    }
    if (selectedDevice == null) {
      redirectTo(URI.create("/manage_devices"), t);
      return;
    }
    selectedDevice.executeShellCommand(
        "am start -a 'com.techcubing.SETUP_APP' " +
        "-e 'com.techcubing.SETUP_DETAILS' '" + configEncoded + "'",
        new IShellOutputReceiver() {
          @Override
          public void addOutput(byte[] data, int offset, int length) {}

          // Called at the end of the stream.
          @Override
          public void flush() {
            try {
              ProtoDb.write(device, serverState);
              redirectTo(URI.create("/manage_devices"), t);
            } catch (IOException | SQLException e) {
              e.printStackTrace();
            }
          }

          @Override
          public boolean isCancelled() {
            return false;
          }
        }
    );
  }

  @Override
  protected List<String> supportedMethods() {
    return List.of("POST");
  }
}
