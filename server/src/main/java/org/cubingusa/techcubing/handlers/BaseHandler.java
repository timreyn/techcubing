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
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.framework.OAuth;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public abstract class BaseHandler implements HttpHandler {
  protected ServerState serverState;
  private String accessToken;

  public BaseHandler(ServerState serverState) {
    this.serverState = serverState;
  }

  void writeResponse(Map<String, Object> model, String template, HttpExchange t)
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

  @Override
  public void handle(HttpExchange t) throws IOException {
    try {
      System.out.println(t.getRequestURI().toString());
      if (requiresOAuthToken()) {
        this.accessToken = serverState.takeAccessToken();
        if (this.accessToken == null) {
          t.getResponseHeaders().add(
              "Location", OAuth.redirectUri(t.getRequestURI(), serverState));
          t.sendResponseHeaders(302, 0);
          t.getResponseBody().close();
          return;
        }
      }
      handleImpl(t);
    } catch (Exception e) {
      e.printStackTrace();
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

  protected abstract void handleImpl(HttpExchange t) throws IOException;

  protected boolean requiresOAuthToken() {
    return false;
  }
}

