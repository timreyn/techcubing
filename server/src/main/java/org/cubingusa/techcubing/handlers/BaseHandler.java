package org.cubingusa.techcubing.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import org.cubingusa.techcubing.framework.ServerState;

public abstract class BaseHandler implements HttpHandler {
  protected ServerState serverState;

  public BaseHandler(ServerState serverState) {
    this.serverState = serverState;
  }

  void writeResponse(Map<String, Object> model, String template, HttpExchange t) throws IOException {
    try {
      Template temp = serverState.templateConfig.getTemplate(template);
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
