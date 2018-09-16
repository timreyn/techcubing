package com.techcubing.server.services;

import java.io.IOException;
import java.sql.SQLException;

import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ServerState;
import com.techcubing.proto.wcif.WcifPerson;
import com.techcubing.proto.services.ListPersonsProto.ListPersonsRequest;
import com.techcubing.proto.services.ListPersonsProto.ListPersonsResponse;

class ListPersonsImpl {
  ServerState serverState;

  public ListPersonsImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public ListPersonsResponse listPersons(ListPersonsRequest request) {
    ListPersonsResponse.Builder responseBuilder = ListPersonsResponse.newBuilder();
    try {
      responseBuilder.addAllPersons(
           serverState.getProtoDb().getAll(WcifPerson.class));
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      responseBuilder.clearPersons();
    }
    return responseBuilder.build();
  }
}
