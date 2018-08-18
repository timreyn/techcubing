package org.cubingusa.techcubing.framework;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.cubingusa.techcubing.proto.wcif.WcifCompetition;
import org.cubingusa.techcubing.proto.wcif.WcifEvent;
import org.cubingusa.techcubing.proto.wcif.WcifPerson;
import org.cubingusa.techcubing.proto.wcif.WcifRound;

public class ServerStateInitializer {
  public static ServerState createServerState() throws IOException, SQLException {
    return new ServerState()
      .setWcaEnvironment(ServerState.WcaEnvironment.PROD)
      .setTemplateConfig(getTemplateConfig())
      .setMysqlConnection(new MysqlConnection())
      .setPort(8118)
      .setGrpcPort(8119)
      .setProtoRegistry(getProtoRegistry());
  }

  static Configuration getTemplateConfig() throws IOException {
    Configuration templateConfig = new Configuration(Configuration.VERSION_2_3_28);
    templateConfig.setDirectoryForTemplateLoading(new File(
          "src/main/java/org/cubingusa/techcubing/templates"));
    templateConfig.setDefaultEncoding("UTF-8");
    templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    return templateConfig;
  }

  static ProtoRegistry getProtoRegistry() {
    ProtoRegistry registry = new ProtoRegistry();
    registry.registerProto(WcifCompetition.getDescriptor());
    registry.registerProto(WcifEvent.getDescriptor());
    registry.registerProto(WcifPerson.getDescriptor());
    registry.registerProto(WcifRound.getDescriptor());
    return registry;
  }
}
