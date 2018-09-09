package com.techcubing.server;

import com.sun.net.httpserver.HttpServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.net.InetSocketAddress;

import com.techcubing.server.framework.ServerState;
import com.techcubing.server.framework.ServerStateInitializer;
import com.techcubing.server.handlers.Handlers;
import com.techcubing.server.services.TechCubingServiceImpl;

public class Main {
  public static void main(String args[]) {
    try {
      ServerState serverState = ServerStateInitializer.createServerState();

      HttpServer server = HttpServer.create(
          new InetSocketAddress(serverState.getPort()), 50);
      Handlers.registerHandlers(server, serverState);
      server.start();
      System.out.println("TechCubing is running!");
      System.out.println("Visit http://localhost:8118 in a browser to get started.");

      TechCubingServiceImpl grpcImpl = new TechCubingServiceImpl(serverState);
      Server grpcServer = ServerBuilder
        .forPort(serverState.getGrpcPort())
        .addService(grpcImpl)
        .addService(ProtoReflectionService.newInstance())
        .build();
      grpcServer.start();
      System.out.println("gRPC service listening on port 8119.");
      grpcServer.awaitTermination();
    } catch (Exception e) {
      System.out.println("Failed to start the server!");
      e.printStackTrace();
    }
  }
}
