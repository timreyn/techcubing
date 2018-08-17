package org.cubingusa.techcubing.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cubingusa.techcubing.framework.ServerState;

public class HomeHandler extends BaseHandler {
  public HomeHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws IOException {
    Map<String, Object> model = new HashMap<>();
    writeResponse(model, "home.ftlh", t);
  }
}
