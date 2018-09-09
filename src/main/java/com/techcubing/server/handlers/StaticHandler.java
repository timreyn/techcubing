package com.techcubing.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;

import com.techcubing.server.framework.ServerState;

@Handler(path = "/static")
public class StaticHandler extends BaseHandler {
  public StaticHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    String uriPath = t.getRequestURI().getPath();
    // Prevent access to directories outside where we store data.
    if (uriPath.contains("..")) {
      respondWithStatus(404, t);
      return;
    }
    String filepath;
    if (uriPath.startsWith("/static/external")) {
      filepath = uriPath.replace("/static/external", "third_party");
    } else {
      filepath = "src/main/java/com/techcubing" + uriPath;
    }
    Path path = FileSystems.getDefault().getPath(filepath);
    File file = path.toFile();
    if (!file.isFile()) {
      respondWithStatus(404, t);
      return;
    }
    t.getResponseHeaders().set("Content-Type", Files.probeContentType(path));
    t.sendResponseHeaders(200, 0);

    OutputStream os = t.getResponseBody();
    FileInputStream fs = new FileInputStream(file);
    byte[] buffer = new byte[4096];
    int count = 0;
    while ((count = fs.read(buffer)) >= 0) {
      os.write(buffer,0,count);
    }
    fs.close();
    os.close();
  }
}
