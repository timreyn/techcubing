package org.cubingusa.techcubing.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class QueryParser {
  public static Map<String, String> parseQuery(URI uri, String requestBody) {
    Map<String, String> queryPairs = new HashMap<>();
    String query = requestBody;
    if (uri.getQuery() != null) {
      query = requestBody + "&" + uri.getQuery();
    }
    for (String pair : query.split("&")) {
      if (pair.isEmpty()) {
        continue;
      }
      int idx = pair.indexOf("=");
      try {
        queryPairs.put(
            URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
            URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
    return queryPairs;
  }
}
