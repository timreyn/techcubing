package org.cubingusa.techcubing.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.util.OAuth;
import org.cubingusa.techcubing.util.QueryParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public abstract class BaseHandler implements HttpHandler {
  protected ServerState serverState;
  private String accessToken;
  protected Map<String, String> queryParams;

  public BaseHandler(ServerState serverState) {
    this.serverState = serverState;
  }

  // Methods to be implemented by subclasses:
  protected abstract void handleImpl(HttpExchange t) throws Exception;

  protected boolean requiresOAuthToken() {
    return false;
  }

  protected List<String> supportedMethods() {
    return List.of("GET");
  }

  // Methods to be called by subclasses:
  protected void writeResponse(
      Map<String, Object> model, String template, HttpExchange t)
      throws IOException {
    try {
      Template temp = serverState.getTemplateConfig().getTemplate(template);
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

  protected Object getWcaApi(String path) throws IOException {
    if (this.accessToken == null) {
      return null;
    }
    URI uri = URI.create(serverState.getWcaSite() + "/api/v0" + path);
    HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Authorization", "Bearer " + this.accessToken);

    BufferedReader in = new BufferedReader(
      new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();

    this.accessToken = null;

    return JSONValue.parse(content.toString());
  }

  protected void redirectTo(URI target, HttpExchange t) throws IOException {
    t.getResponseHeaders().add("Location", target.toString());
    t.sendResponseHeaders(302, 0);
    t.getResponseBody().close();
  }

  // Core implementation:
  @Override
  public void handle(HttpExchange t) throws IOException {
    try {
      System.out.println(t.getRequestURI().toString());
      if (!supportedMethods().contains(t.getRequestMethod())) {
        t.sendResponseHeaders(501, 0);
        t.getResponseBody().close();
        return;
      }

      if (requiresOAuthToken()) {
        this.accessToken = serverState.takeAccessToken();
        if (this.accessToken == null) {
          redirectTo(OAuth.redirectUri(t.getRequestURI(), serverState), t);
          return;
        }
      }

      queryParams = QueryParser.parseQuery(t.getRequestURI());

      handleImpl(t);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
