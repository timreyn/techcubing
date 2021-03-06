package com.techcubing.server.handlers;

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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.techcubing.server.framework.ServerState;
import com.techcubing.server.util.OAuth;
import com.techcubing.server.util.QueryParser;

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

  protected boolean requiresActiveCompetition() {
    return true;
  }

  protected boolean shouldParseBody() {
    return true;
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
      respondWithStatus(500, t);
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
    respondWithStatus(302, t);
  }

  // Core implementation:
  @Override
  public void handle(HttpExchange t) throws IOException {
    try {
      System.out.println(t.getRequestURI().toString());
      if (!supportedMethods().contains(t.getRequestMethod())) {
        respondWithStatus(501, t);
        return;
      }

      if (requiresActiveCompetition() && serverState.getCompetitionId() == null) {
        redirectTo(URI.create("/competitions"), t);
        return;
      }

      if (requiresOAuthToken()) {
        this.accessToken = serverState.takeAccessToken();
        if (this.accessToken == null) {
          redirectTo(OAuth.redirectUri(t.getRequestURI(), serverState), t);
          return;
        }
      }

      // Parse the request body and query params.
      String requestBody = "";
      if (shouldParseBody()) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = t.getRequestBody().read(buffer)) != -1) {
          baos.write(buffer, 0, length);
        }
        requestBody = baos.toString(StandardCharsets.UTF_8.name());
      }
      queryParams = QueryParser.parseQuery(t.getRequestURI(), requestBody);

      handleImpl(t);
    } catch (Exception e) {
      e.printStackTrace();
      respondWithStatus(500, t);
    }
  }

  protected void respondWithStatus(int status, HttpExchange t) throws IOException {
    t.sendResponseHeaders(status, 0);
    t.getResponseBody().close();
  }
}
