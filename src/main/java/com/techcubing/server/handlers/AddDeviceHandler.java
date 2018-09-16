package com.techcubing.server.handlers;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.URI;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import javax.crypto.KeyGenerator;

import com.techcubing.server.framework.ServerState;
import com.techcubing.server.framework.ProtoDb;
import com.techcubing.proto.DeviceConfigProto.DeviceConfig;
import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.DeviceTypeProto.DeviceType;
import com.techcubing.server.util.ProtoUtil;

@Handler(path = "/add_device")
public class AddDeviceHandler extends BaseHandler {
  public AddDeviceHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    KeyGenerator keygen = KeyGenerator.getInstance("AES");
    keygen.init(256);
    byte[] secretKey = keygen.generateKey().getEncoded();

    SecureRandom rnd = new SecureRandom();
    byte[] iv = new byte[16];
    rnd.nextBytes(secretKey);

    Device device =
      Device.newBuilder()
          .setId(String.valueOf(new Random().nextInt(90000000) + 10000000))
          .setVisibleName(queryParams.get("deviceName"))
          .setActivated(ProtoUtil.getCurrentTime())
          .setType(DeviceType.valueOf(Integer.valueOf(queryParams.get("deviceType"))))
          .setSerialNumber(queryParams.get("deviceSerialNumber"))
          .setSecretKey(ByteString.copyFrom(secretKey))
          .setIv(ByteString.copyFrom(iv))
          .build();

    String hostAddress = "";

    for (Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
         networks.hasMoreElements();) {
      NetworkInterface network = networks.nextElement();
      if (network.isLoopback()) {
        continue;
      }
      Enumeration<InetAddress> addresses = network.getInetAddresses();
      while (addresses.hasMoreElements()) {
        InetAddress address = addresses.nextElement();
        if (address instanceof Inet4Address) {
          hostAddress = address.getHostAddress();
          break;
        }
      }
      if (!hostAddress.isEmpty()) {
        break;
      }
    }

    // Set up the device.
    DeviceConfig config =
      DeviceConfig.newBuilder()
          .setDevice(device)
          .setServerHost(hostAddress)
          .setServerPort(serverState.getGrpcPort())
          .setWcaEnvironment(serverState.getWcaEnvironment())
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
              serverState.getProtoDb().write(device);
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
