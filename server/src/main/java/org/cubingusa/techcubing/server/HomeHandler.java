package org.cubingusa.techcubing.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class HomeHandler implements HttpHandler {
  private ServerState serverState;

  public HomeHandler(ServerState serverState) {
    this.serverState = serverState;
  }

  @Override
  public void handle(HttpExchange t) throws IOException {
    Map<String, Object> model = new HashMap<>();
    try {
      Template temp = serverState.templateConfig.getTemplate("home.ftlh");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      temp.process(model, new OutputStreamWriter(baos));
      t.sendResponseHeaders(200, baos.size());
      OutputStream os = t.getResponseBody();
      baos.writeTo(os);
      os.close();
    } catch (TemplateException e) {
      e.printStackTrace();
      t.sendResponseHeaders(500, 0);
    }
  }
}
