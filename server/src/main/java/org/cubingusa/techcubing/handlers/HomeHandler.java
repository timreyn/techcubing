package org.cubingusa.techcubing.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;
import org.cubingusa.techcubing.framework.ServerState;

public class HomeHandler extends BaseHandler {
  public HomeHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    // This is the default handler for any unmatched path.  If it's not /, return.
    if (!t.getRequestURI().getPath().equals("/")) {
      t.sendResponseHeaders(404, 0);
      t.getResponseBody().close();
      return;
    }
    Map<String, Object> model = new HashMap<>();
    model.put("competitionId", serverState.getCompetitionId());
    model.put("hasCompetitionId", serverState.getCompetitionId() != null);
    writeResponse(model, "home.ftlh", t);
  }
}
