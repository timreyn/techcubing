package org.cubingusa.techcubing.handlers;

import com.google.protobuf.util.JsonFormat;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import org.cubingusa.techcubing.framework.MysqlConnection;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.proto.PersonProto.Person;
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

    MysqlConnection connection = serverState.getMysqlConnection();
    connection.initializeCompetition(competitionId, false);

    for (Object personJson : (JSONArray) response.get("persons")) {
      Person.Builder personBuilder = Person.newBuilder();
      JsonFormat.parser().ignoringUnknownFields().merge(personJson.toString(), personBuilder);
      Person person = personBuilder.build();
      connection.putProto(person, person.getWcaUserId(), connection.getPersonsTable());
    }

    writeResponse(response, "set_competition.ftlh", t);
  }

  @Override
  protected boolean requiresOAuthToken() {
    return true;
  }
}
