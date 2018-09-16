package com.techcubing.server.framework;

import com.android.ddmlib.AndroidDebugBridge;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.ScorecardProto.Scorecard;
import com.techcubing.proto.ScrambleProto.Scramble;
import com.techcubing.proto.ScrambleProto.ScrambleSet;
import com.techcubing.proto.wcif.WcifCompetition;
import com.techcubing.proto.wcif.WcifEvent;
import com.techcubing.proto.wcif.WcifPerson;
import com.techcubing.proto.wcif.WcifRound;

public class ServerStateInitializer {
  public static ServerState createServerState(CommandLineFlags flags)
      throws IOException, SQLException {
    AndroidDebugBridge.init(false);

    ServerState serverState = new ServerState()
      .setWcaEnvironment(flags.wca)
      .setTemplateConfig(getTemplateConfig())
      .setPort(8118)
      .setGrpcPort(8119)
      .setProtoRegistry(getProtoRegistry())
      .setAndroidDebugBridge(AndroidDebugBridge.createBridge());

    MysqlConnection mysqlConnection = new MysqlConnection();

    serverState.setProtoDb(new ProtoDb(mysqlConnection, serverState));

    try {
      PreparedStatement statement =
        mysqlConnection.prepareStatement(
          "SELECT competitionId FROM __ActiveCompetition WHERE env = ?");
      statement.setString(1, flags.wca.toString());

      ResultSet results = statement.executeQuery();
      if (results.next()) {
        serverState.setCompetitionId(results.getString("competitionId"));
      }
    } catch (SQLException e) {
      // This just means we weren't able to read the active competition.  No
      // problem, we can continue anyway.
    }

    return serverState;
  }

  static Configuration getTemplateConfig() throws IOException {
    Configuration templateConfig = new Configuration(Configuration.VERSION_2_3_28);
    templateConfig.setDirectoryForTemplateLoading(new File(
          "src/main/java/com/techcubing/server/templates"));
    templateConfig.setDefaultEncoding("UTF-8");
    templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    return templateConfig;
  }

  static ProtoRegistry getProtoRegistry() {
    ProtoRegistry registry = new ProtoRegistry();
    registry.register(Device.getDefaultInstance());
    registry.register(Scorecard.getDefaultInstance());
    registry.register(Scramble.getDefaultInstance());
    registry.register(ScrambleSet.getDefaultInstance());
    registry.register(WcifCompetition.getDefaultInstance());
    registry.register(WcifEvent.getDefaultInstance());
    registry.register(WcifPerson.getDefaultInstance());
    registry.register(WcifRound.getDefaultInstance());
    return registry;
  }
}
