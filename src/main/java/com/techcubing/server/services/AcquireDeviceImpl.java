package com.techcubing.server.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ServerState;
import com.techcubing.server.util.ProtoUtil;
import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.services.AcquireDeviceProto.AcquireDeviceRequest;
import com.techcubing.proto.services.AcquireDeviceProto.AcquireDeviceResponse;
import com.techcubing.proto.wcif.WcifPerson;

class AcquireDeviceImpl {
  ServerState serverState;

  public AcquireDeviceImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public AcquireDeviceResponse acquireDevice(
      AcquireDeviceRequest request) {
    ProtoDb protoDb = serverState.getProtoDb();
    AcquireDeviceResponse.Builder responseBuilder =
      AcquireDeviceResponse.newBuilder();

    try {
      String deviceId = request.getContext().getDeviceId();

      JSONObject me = 
        (JSONObject) getPersonDetails(request.getAuthorizationCode()).get("me");

      WcifPerson person = getOrWritePerson(me);
      String personId = ProtoUtil.getId(person);

      // Check if the person already has a device.
      List<Device> devicesHeld =
        protoDb.getAllMatching(Device.class, "person_id", personId);
      if (!devicesHeld.isEmpty()) {
        responseBuilder.setStatus(AcquireDeviceResponse.Status.ALREADY_LOGGED_IN);
        return responseBuilder.build();
      }

      // Try to atomically acquire the device.
      ProtoDb.UpdateResult updateResult = protoDb.atomicUpdate(
          Device.class, deviceId,
          new ProtoDb.ProtoUpdate<Device.Builder>() {
            @Override
            public boolean update(Device.Builder builder) {
              builder.setPersonId(personId);
              builder.setAcquired(ProtoUtil.getCurrentTime());
              return true;
            }
          });

      switch (updateResult) {
        case ID_NOT_FOUND:
        case RETRIES_EXCEEDED:
          responseBuilder.setStatus(
              AcquireDeviceResponse.Status.INTERNAL_ERROR);
          break;
        case DECLINED:
          break;
        case OK:
          responseBuilder.setPerson(person);
          break;
      }
    } catch (SQLException | IOException e) {
      responseBuilder.setStatus(
          AcquireDeviceResponse.Status.INTERNAL_ERROR);
      e.printStackTrace();
    }

    return responseBuilder.build();
  }

  private JSONObject getPersonDetails(String code) throws IOException {
    URI uri = URI.create(serverState.getWcaSite() + "/api/v0/me");
    HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Authorization", "Bearer " + code);

    BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();

    return (JSONObject) JSONValue.parse(content.toString());
  }

  private WcifPerson getOrWritePerson(JSONObject jsonPerson)
      throws SQLException, IOException {
    String personId = String.valueOf((int) ((long) jsonPerson.get("id")));
    WcifPerson person =
      serverState.getProtoDb().getById(personId, WcifPerson.class);
    if (person != null) {
      return person;
    }
    WcifPerson.Builder builder = WcifPerson.newBuilder()
      .setRegistrantId(-1)
      .setName((String) jsonPerson.get("name"))
      .setWcaUserId((int) ((long) jsonPerson.get("id")));

    if (jsonPerson.containsKey("wca_id")) {
      builder.setWcaId((String) jsonPerson.get("wca_id"));
    }
    person = builder.build();
    serverState.getProtoDb().write(person);
    return person;
  }
}
