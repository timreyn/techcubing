package org.cubingusa.techcubing.handlers;

import com.google.protobuf.util.JsonFormat;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import org.cubingusa.techcubing.framework.ServerState;
//import org.cubingusa.techcubing.framework.ProtoDb;
import org.cubingusa.techcubing.proto.wcif.WcifCompetition;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    //MysqlConnection connection = serverState.getMysqlConnection();
    //ProtoDb.initializeCompetition(competitionId, serverState);

    WcifCompetition.Builder competitionBuilder = WcifCompetition.newBuilder();
    JsonFormat.parser().ignoringUnknownFields().merge(response.toString(), competitionBuilder);
    //ProtoDb.recursivelyWrite(competitionBuilder.build(), serverState);

    writeResponse(response, "set_competition.ftlh", t);
  }

  @Override
  protected boolean requiresOAuthToken() {
    return true;
  }
}
