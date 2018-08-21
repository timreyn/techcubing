package com.techcubing.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.techcubing.server.framework.ServerState;

public class OAuthRedirectHandler extends BaseHandler {
  public OAuthRedirectHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    URI requestUri = t.getRequestURI();
    if (queryParams.isEmpty()) {
      Map<String, Object> model = new HashMap<>();
      writeResponse(model, "oauth_redirect.html", t);
      return;
    }
    
    serverState.storeAccessToken(queryParams.get("access_token"));

    redirectTo(URI.create(queryParams.get("state")), t);
  }

  @Override
  protected boolean requiresActiveCompetition() {
    return false;
  }
}
