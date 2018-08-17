package org.cubingusa.techcubing.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.cubingusa.techcubing.framework.QueryParser;
import org.cubingusa.techcubing.framework.ServerState;

public class OAuthRedirectHandler extends BaseHandler {
  public OAuthRedirectHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    URI requestUri = t.getRequestURI();
    Map<String, String> query = QueryParser.parseQuery(requestUri);
    if (query.isEmpty()) {
      Map<String, Object> model = new HashMap<>();
      writeResponse(model, "oauth_redirect.ftlh", t);
      return;
    }
    
    serverState.storeAccessToken(query.get("access_token"));

    redirectTo(URI.create(query.get("state")), t);
  }
}
