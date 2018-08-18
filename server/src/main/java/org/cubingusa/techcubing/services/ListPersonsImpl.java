package org.cubingusa.techcubing.services;

import java.io.IOException;
import java.sql.SQLException;
import org.cubingusa.techcubing.framework.ProtoDb;
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
      for (WcifPerson.Builder personBuilder :
           ProtoDb.getAll(WcifPerson.newBuilder(), serverState)) {
        responseBuilder.addPersons(personBuilder.build());
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      responseBuilder.clearPersons();
    }
    return responseBuilder.build();
  }
}
