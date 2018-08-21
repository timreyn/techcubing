package com.techcubing.server.util;

import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

import com.techcubing.server.framework.ServerState;

public class OAuth {
  public static URI redirectUri(URI targetUri, ServerState serverState) {
    return UriBuilder.fromUri(serverState.getWcaSite())
      .path("/oauth/authorize")
      .queryParam("client_id", serverState.getOAuthClientId())
      .queryParam("redirect_uri", String.format("http://localhost:%d/oauth_redirect", serverState.getPort()))
      .queryParam("response_type", "token")
      .queryParam("scope", "public manage_competitions")
      .queryParam("state", targetUri.toString())
      .build();
  }
}
