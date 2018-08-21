package org.cubingusa.techcubing.framework;

import com.android.ddmlib.AndroidDebugBridge;
import freemarker.template.Configuration;
import java.net.URI;

public class ServerState {
  private Configuration templateConfig;
  private String accessToken = null;
  private String accessTokenGuard = "";
  private int port;
  private int grpcPort;
  private MysqlConnection mysqlConnection;
  private WcaEnvironment wcaEnvironment = WcaEnvironment.PROD;
  private ProtoRegistry protoRegistry;
  private String competitionId;
  private AndroidDebugBridge androidDebugBridge;

  public ServerState setTemplateConfig(Configuration templateConfig) {
    this.templateConfig = templateConfig;
    return this;
  }

  public Configuration getTemplateConfig() {
    return templateConfig;
  }

  public ServerState setPort(int port) {
    this.port = port;
    return this;
  }

  public int getPort() {
    return port;
  }

  public ServerState setGrpcPort(int port) {
    this.grpcPort = port;
    return this;
  }

  public int getGrpcPort() {
    return grpcPort;
  }

  public ServerState setMysqlConnection(MysqlConnection connection) {
    this.mysqlConnection = connection;
    return this;
  }

  public MysqlConnection getMysqlConnection() {
    return mysqlConnection;
  }

  public enum WcaEnvironment {
    PROD, STAGING, DEV
  }

  public ServerState setWcaEnvironment(WcaEnvironment wcaEnvironment) {
    this.wcaEnvironment = wcaEnvironment;
    return this;
  }

  public URI getWcaSite() {
    switch (wcaEnvironment) {
      case PROD:
        return URI.create("https://www.worldcubeassociation.org/");
      case STAGING:
        return URI.create("https://staging.worldcubeassociation.org/");
      case DEV:
        return URI.create("http://localhost:3000/");
    }
    // Unreachable.
    return null;
  }

  public String getOAuthClientId() {
    switch (wcaEnvironment) {
      case PROD:
        return "ca87aa523ea5400742a6160af46c27f725484972a0ad796c2610cf88bef4e0f1";
      case STAGING:
      case DEV:
        return "example-application-id";
    }
    // Unreachable.
    return null;
  }

  public void storeAccessToken(String token) {
    synchronized(this.accessTokenGuard) {
      this.accessToken = token;
    }
  }

  public String takeAccessToken() {
    synchronized(this.accessTokenGuard) {
      String token = this.accessToken;
      this.accessToken = null;
      return token;
    }
  }

  public ServerState setProtoRegistry(ProtoRegistry protoRegistry) {
    this.protoRegistry = protoRegistry;
    return this;
  }

  public ProtoRegistry getProtoRegistry() {
    return protoRegistry;
  }

  public ServerState setCompetitionId(String competitionId) {
    this.competitionId = competitionId;
    return this;
  }

  public String getCompetitionId() {
    return competitionId;
  }

  public ServerState setAndroidDebugBridge(AndroidDebugBridge androidDebugBridge) {
    this.androidDebugBridge = androidDebugBridge;
    return this;
  }

  public AndroidDebugBridge getAndroidDebugBridge() {
    return androidDebugBridge;
  }
}
