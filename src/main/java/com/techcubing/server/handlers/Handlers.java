package com.techcubing.server.handlers;

import com.sun.net.httpserver.HttpServer;

import com.techcubing.server.framework.ServerState;

public class Handlers {
  public static void registerHandlers(HttpServer server, ServerState serverState) {
    server.createContext("/", new IndexHandler(serverState));
    server.createContext("/oauth_redirect", new OAuthRedirectHandler(serverState));
    server.createContext("/competitions", new CompetitionsHandler(serverState));
    server.createContext("/set_competition", new SetCompetitionHandler(serverState));
    server.createContext("/manage_devices", new ManageDevicesHandler(serverState));
    server.createContext("/add_device", new AddDeviceHandler(serverState));
    server.createContext("/delete_device", new DeleteDeviceHandler(serverState));
    server.createContext("/manage_scrambles", new ManageScramblesHandler(serverState));
    server.createContext("/add_scrambles", new AddScramblesHandler(serverState));
    server.createContext("/admin_results", new AdminResultsHandler(serverState));
  }
}
