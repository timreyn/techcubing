package org.cubingusa.techcubing.framework;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ServerStateInitializer {
  public static ServerState createServerState() throws IOException, SQLException {
    return new ServerState()
      .setWcaEnvironment(ServerState.WcaEnvironment.PROD)
      .setTemplateConfig(getTemplateConfig())
      .setMysqlConnection(new MysqlConnection())
      .setPort(8118)
      .setGrpcPort(8119);
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
