package org.cubingusa.techcubing.framework;

import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

public class OAuth {
  public static String redirectUri(URI targetUri, ServerState serverState) {
    URI uri = UriBuilder.fromUri(serverState.getWcaSite())
      .path("/oauth/authorize")
      .queryParam("client_id", serverState.getOAuthClientId())
      .queryParam("redirect_uri", String.format("http://localhost:%d/oauth_redirect", serverState.getPort()))
      .queryParam("response_type", "token")
      .queryParam("scope", "public manage_competitions")
      .queryParam("state", targetUri.toString())
      .build();
    return uri.toString();
  }
}
