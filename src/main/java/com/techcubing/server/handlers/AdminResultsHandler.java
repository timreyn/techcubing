package com.techcubing.server.handlers;

import com.google.protobuf.Message;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
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

public class AdminResultsHandler extends BaseHandler {
  public AdminResultsHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    Map<String, Object> model = new HashMap<>();

    Map<String, WcifRound> rounds = new HashMap<>();
    for (Message roundMessage : ProtoDb.getAll(WcifRound.newBuilder(), serverState)) {
      WcifRound round = (WcifRound) roundMessage;
      rounds.put(round.getId(), round);
    }
    model.put("rounds", rounds);

    Map<String, WcifEvent> events = new HashMap<>();
    for (Message eventMessage : ProtoDb.getAll(WcifEvent.newBuilder(), serverState)) {
      WcifEvent event = (WcifEvent) eventMessage;
      events.put(event.getId(), event);
    }
    model.put("events", events);

    Map<String, WcifPerson> people = new HashMap<>();
    for (Message personMessage :
         ProtoDb.getAll(WcifPerson.newBuilder(), serverState)) {
      WcifPerson person = (WcifPerson) personMessage;
      people.put(ProtoUtil.getId(person), person);
    }
    model.put("persons", people);

    Map<String, Device> devices = new HashMap<>();
    for (Message deviceMessage :
         ProtoDb.getAll(Device.newBuilder(), serverState)) {
      Device device = (Device) deviceMessage;
      devices.put(device.getId(), device);
    }
    model.put("devices", devices);

    WcifRound round = rounds.get(queryParams.get("r"));
    if (round != null) {
      model.put("activeRound", round);
      WcifEvent event = events.get(round.getEventId());
      model.put("activeEvent", event);
      List<Message> scorecardMessages =
        ProtoDb.getAllMatching(
            Scorecard.newBuilder(), "round_id", round.getId(), serverState);

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
