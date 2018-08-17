package org.cubingusa.techcubing.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.cubingusa.techcubing.framework.ServerState;
import org.json.simple.JSONArray;

public class CompetitionsHandler extends BaseHandler {
  public CompetitionsHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws IOException {
    ZonedDateTime fromTime = ZonedDateTime.now().minusMonths(1);
    JSONArray response = (JSONArray) getWcaApi(
        "/competitions?managed_by_me=true&start=" + fromTime.toString());

    Map<String, Object> model = new HashMap<>();
    model.put("competitions", response);

    writeResponse(model, "competitions.ftlh", t);
  }

  @Override
  protected boolean requiresOAuthToken() {
    return true;
  }
}
