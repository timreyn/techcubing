package com.techcubing.server.handlers;

import com.google.protobuf.util.JsonFormat;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ScorecardGenerator;
import com.techcubing.server.framework.ServerState;
import com.techcubing.server.util.WcifUtil;
import com.techcubing.proto.wcif.WcifCompetition;

@Handler(path = "/set_competition")
public class SetCompetitionHandler extends BaseHandler {
  public SetCompetitionHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    String competitionId = queryParams.get("id");
    if (competitionId == null) {
      redirectTo(URI.create("/competitions"), t);
      return;
    }
    JSONObject response = (JSONObject) getWcaApi(
        "/competitions/" + competitionId + "/wcif");

    serverState.getProtoDb().initializeCompetition(competitionId);

    WcifCompetition.Builder competitionBuilder = WcifCompetition.newBuilder();
    JsonFormat.parser().ignoringUnknownFields().merge(response.toString(), competitionBuilder);
    // Add some fields that aren't present in WCIF.
    WcifUtil.addExtraInfo(competitionBuilder);

    serverState.getProtoDb().recursivelyWrite(competitionBuilder.build());

    ScorecardGenerator.generateScorecards(serverState.getProtoDb());

    redirectTo(URI.create("/"), t);
  }

  @Override
  protected boolean requiresOAuthToken() {
    return true;
  }

  @Override
  protected boolean requiresActiveCompetition() {
    return false;
  }
}
