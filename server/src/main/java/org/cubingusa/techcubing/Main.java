package org.cubingusa.techcubing;

import com.sun.net.httpserver.HttpServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.framework.MysqlConnection;
import org.cubingusa.techcubing.handlers.CompetitionsHandler;
import org.cubingusa.techcubing.handlers.HomeHandler;
import org.cubingusa.techcubing.handlers.OAuthRedirectHandler;
import org.cubingusa.techcubing.handlers.SetCompetitionHandler;
import org.cubingusa.techcubing.services.TechCubingServiceImpl;

public class Main {
  public static void main(String args[]) {
    try {
      HttpServer server = HttpServer.create(new InetSocketAddress(8118), 50);
      ServerState serverState =
        new ServerState()
        .setWcaEnvironment(ServerState.WcaEnvironment.PROD)
        .setTemplateConfig(getTemplateConfig())
        .setMysqlConnection(new MysqlConnection())
        .setPort(8118)
        .setGrpcPort(8119);
      server.createContext("/oauth_redirect", new OAuthRedirectHandler(serverState));
      server.createContext("/competitions", new CompetitionsHandler(serverState));
      server.createContext("/set_competition", new SetCompetitionHandler(serverState));
      server.start();
      System.out.println("TechCubing is running!");
      System.out.println("Visit http://localhost:8118 in a browser to get started.");

      TechCubingServiceImpl grpcImpl = new TechCubingServiceImpl(serverState);
      Server grpcServer = ServerBuilder.forPort(8119).addService(grpcImpl).build();
      grpcServer.start();
      System.out.println("gRPC service listening on port 8119.");
      grpcServer.awaitTermination();
    } catch (Exception e) {
      System.out.println("Failed to start the server!");
      e.printStackTrace();
    }
  }

  static Configuration getTemplateConfig() throws IOException {
    Configuration templateConfig = new Configuration(Configuration.VERSION_2_3_28);
    templateConfig.setDirectoryForTemplateLoading(new File(
          "src/main/java/org/cubingusa/techcubing/templates"));
    templateConfig.setDefaultEncoding("UTF-8");
    templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    return templateConfig;
  }
}
