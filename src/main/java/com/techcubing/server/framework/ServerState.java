package com.techcubing.server.framework;

import com.android.ddmlib.AndroidDebugBridge;
import freemarker.template.Configuration;
import java.net.URI;

import com.techcubing.proto.WcaEnvironmentProto.WcaEnvironment;

public class ServerState {
  private Configuration templateConfig;
  private String accessToken = null;
  private String accessTokenGuard = "";
  private int port;
  private int grpcPort;
  private ProtoDb protoDb;
  private WcaEnvironment wcaEnvironment = WcaEnvironment.PROD;
  private ProtoRegistry protoRegistry;
  private String competitionId;
  private AndroidDebugBridge androidDebugBridge;

  ServerState setTemplateConfig(Configuration templateConfig) {
    this.templateConfig = templateConfig;
    return this;
  }

  public Configuration getTemplateConfig() {
    return templateConfig;
  }

  ServerState setPort(int port) {
    this.port = port;
    return this;
  }

  public int getPort() {
    return port;
  }

  ServerState setGrpcPort(int port) {
    this.grpcPort = port;
    return this;
  }

  public int getGrpcPort() {
    return grpcPort;
  }

  ServerState setProtoDb(ProtoDb protoDb) {
    this.protoDb = protoDb;
    return this;
  }

  public ProtoDb getProtoDb() {
    return protoDb;
  }

  ServerState setWcaEnvironment(WcaEnvironment wcaEnvironment) {
    this.wcaEnvironment = wcaEnvironment;
    return this;
  }

  public WcaEnvironment getWcaEnvironment() {
    return wcaEnvironment;
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

  ServerState setProtoRegistry(ProtoRegistry protoRegistry) {
    this.protoRegistry = protoRegistry;
    return this;
  }

  public ProtoRegistry getProtoRegistry() {
    return protoRegistry;
  }

  ServerState setCompetitionId(String competitionId) {
    this.competitionId = competitionId;
    return this;
  }

  public String getCompetitionId() {
    return competitionId;
  }

  ServerState setAndroidDebugBridge(AndroidDebugBridge androidDebugBridge) {
    this.androidDebugBridge = androidDebugBridge;
    return this;
  }

  public AndroidDebugBridge getAndroidDebugBridge() {
    return androidDebugBridge;
  }
}
