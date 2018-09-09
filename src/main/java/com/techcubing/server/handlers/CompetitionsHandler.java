package com.techcubing.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;

import com.techcubing.server.framework.ServerState;

@Handler(path = "/competitions")
public class CompetitionsHandler extends BaseHandler {
  public CompetitionsHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    ZonedDateTime fromTime = ZonedDateTime.now().minusMonths(1);
    JSONArray response = (JSONArray) getWcaApi(
        "/competitions?managed_by_me=true&start=" + fromTime.toString());

    Map<String, Object> model = new HashMap<>();
    model.put("competitions", response);

    writeResponse(model, "competitions.html", t);
  }

  @Override
  protected boolean requiresOAuthToken() {
    return true;
  }

  @Override
  protected boolean requiresActiveCompetition() {
    return false;
  }
}
