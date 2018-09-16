package com.techcubing.server.handlers;

import com.google.protobuf.Message;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.ScorecardProto.Scorecard;
import com.techcubing.proto.wcif.WcifEvent;
import com.techcubing.proto.wcif.WcifPerson;
import com.techcubing.proto.wcif.WcifRound;
import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ServerState;
import com.techcubing.server.util.ProtoUtil;

@Handler(path = "/scorecard")
public class ScorecardHandler extends BaseHandler {
  public ScorecardHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    Map<String, Object> model = new HashMap<>();
    ProtoDb protoDb = serverState.getProtoDb();

    Scorecard scorecard = protoDb.getById(
        queryParams.get("id"), Scorecard.newBuilder());
    WcifPerson person = protoDb.getIdField(scorecard, "person_id");
    WcifRound round = protoDb.getIdField(scorecard, "round_id");
    WcifEvent event = protoDb.getIdField(round, "event_id");
    model.put("scorecard", scorecard);
    model.put("person", person);
    model.put("round", round);
    model.put("event", event);

    Map<String, WcifPerson> people = new HashMap<>();
    for (Message personMessage :
         protoDb.getAll(WcifPerson.newBuilder())) {
      WcifPerson nextPerson = (WcifPerson) personMessage;
      people.put(ProtoUtil.getId(nextPerson), nextPerson);
    }
    model.put("persons", people);

    Map<String, Device> devices = new HashMap<>();
    for (Message deviceMessage :
         protoDb.getAll(Device.newBuilder())) {
      Device device = (Device) deviceMessage;
      devices.put(device.getId(), device);
    }
    model.put("devices", devices);

    model.put(
        "qrCodeValue",
        "techcubing://scorecard/" + serverState.getCompetitionId() + "/" + scorecard.getId());

    model.put("competitionId", serverState.getCompetitionId());
    writeResponse(model, "scorecard.html", t);
  }
}
