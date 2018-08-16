package org.cubingusa.techcubing;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.cubingusa.techcubing.server.HomeHandler;
import org.cubingusa.techcubing.server.ServerState;

public class Main {
  public static void main(String args[]) {
    try {
      HttpServer server = HttpServer.create(new InetSocketAddress(8118), 50);
      ServerState serverState = new ServerState();
      server.createContext("/", new HomeHandler(serverState));
      server.start();
      System.out.println("TechCubing is running!");
      System.out.println("Visit http://localhost:8118 in a browser to get started.");
    } catch (IOException e) {
      System.out.println("Failed to start the server!");
      e.printStackTrace();
    }
  }
}
