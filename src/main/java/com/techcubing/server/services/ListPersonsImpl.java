package com.techcubing.server.services;

import com.google.protobuf.Message;
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
      for (Message person :
           ProtoDb.getAll(WcifPerson.newBuilder(), serverState)) {
        responseBuilder.addPersons((WcifPerson) person);
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      responseBuilder.clearPersons();
    }
    return responseBuilder.build();
  }
}
