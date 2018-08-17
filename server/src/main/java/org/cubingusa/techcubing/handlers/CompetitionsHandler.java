package org.cubingusa.techcubing.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
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
    JSONArray response = (JSONArray) getWcaApi("/competitions?managed_by_me=true");
    System.out.println(response.toString());

    Map<String, Object> model = new HashMap<>();
    writeResponse(model, "competitions.ftlh", t);
  }

  @Override
  protected boolean requiresOAuthToken() {
    return true;
  }
}
