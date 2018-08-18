package org.cubingusa.techcubing.services;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.cubingusa.techcubing.framework.MysqlConnection;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.proto.wcif.WcifPerson;
import org.cubingusa.techcubing.proto.services.ListPersonsProto.ListPersonsRequest;
import org.cubingusa.techcubing.proto.services.ListPersonsProto.ListPersonsResponse;

class ListPersonsImpl {
  ServerState serverState;

  public ListPersonsImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public ListPersonsResponse listPersons(ListPersonsRequest request) {
    ListPersonsResponse.Builder responseBuilder = ListPersonsResponse.newBuilder();
    try {
      MysqlConnection connection = serverState.getMysqlConnection();
      ResultSet results = connection.prepareStatement(
          "SELECT id, data FROM " + connection.getPersonsTable() + ";")
        .executeQuery();
      while (results.next()) {
        responseBuilder.addPersons(
            WcifPerson.parseFrom(results.getBlob("data").getBinaryStream()));
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      responseBuilder.clearPersons();
    }
    return responseBuilder.build();
  }
}
