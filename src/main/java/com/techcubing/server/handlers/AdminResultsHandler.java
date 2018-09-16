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

@Handler(path = "/admin_results")
public class AdminResultsHandler extends BaseHandler {
  public AdminResultsHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    ProtoDb protoDb = serverState.getProtoDb();
    Map<String, Object> model = new HashMap<>();

    Map<String, WcifEvent> events = new HashMap<>();
    for (Message eventMessage : protoDb.getAll(WcifEvent.newBuilder())) {
      WcifEvent event = (WcifEvent) eventMessage;
      events.put(event.getId(), event);
    }
    model.put("events", events);

    WcifRound activeRound = null;
    List<WcifRound> rounds = new ArrayList<>();
    for (Message roundMessage : protoDb.getAll(WcifRound.newBuilder())) {
      WcifRound round = (WcifRound) roundMessage;
      rounds.add(round);
      if (round.getId().equals(queryParams.get("r"))) {
        activeRound = round;
      }
    }
    Collections.sort(rounds, (WcifRound roundA, WcifRound roundB) -> {
      WcifEvent eventA = events.get(roundA.getEventId());
      WcifEvent eventB = events.get(roundB.getEventId());
      if (eventA != eventB) {
        return eventA.getEventPriority() - eventB.getEventPriority();
      } else {
        return roundA.getRoundNumber() - roundB.getRoundNumber();
      }
    });
    model.put("rounds", rounds);

    Map<String, WcifPerson> people = new HashMap<>();
    for (Message personMessage :
         protoDb.getAll(WcifPerson.newBuilder())) {
      WcifPerson person = (WcifPerson) personMessage;
      people.put(ProtoUtil.getId(person), person);
    }
    model.put("persons", people);

    Map<String, Device> devices = new HashMap<>();
    for (Message deviceMessage :
         protoDb.getAll(Device.newBuilder())) {
      Device device = (Device) deviceMessage;
      devices.put(device.getId(), device);
    }
    model.put("devices", devices);

    if (activeRound != null) {
      model.put("activeRound", activeRound);
      WcifEvent event = events.get(activeRound.getEventId());
      model.put("activeEvent", event);
      List<Message> scorecardMessages =
        protoDb.getAllMatching(Scorecard.newBuilder(), "round_id", activeRound.getId());

      List<Scorecard> scorecards = new ArrayList<>();
      for (Message message : scorecardMessages) {
        scorecards.add((Scorecard) message);
      }
      model.put("scorecards", scorecards);
    }
    model.put("competitionId", serverState.getCompetitionId());
    writeResponse(model, "admin_results.html", t);
  }
}
