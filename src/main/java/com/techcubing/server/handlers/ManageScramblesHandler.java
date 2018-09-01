package com.techcubing.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;

import com.techcubing.server.framework.ServerState;

public class ManageScramblesHandler extends BaseHandler {
  public ManageScramblesHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    Map<String, Object> model = new HashMap<>();
    writeResponse(model, "manage_scrambles.html", t);
  }
}
