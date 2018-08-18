package org.cubingusa.techcubing.services;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import java.io.IOException;
import java.sql.SQLException;
import org.cubingusa.techcubing.framework.ProtoDb;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.proto.services.GetByIdProto.GetByIdRequest;
import org.cubingusa.techcubing.proto.services.GetByIdProto.GetByIdResponse;

class GetByIdImpl {
  ServerState serverState;

  public GetByIdImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public GetByIdResponse getById(GetByIdRequest request) {
    GetByIdResponse.Builder responseBuilder = GetByIdResponse.newBuilder();
    Message.Builder builder =
      serverState.getProtoRegistry().getBuilder(request.getProtoType());
    if (builder == null) {
      responseBuilder.setStatus(GetByIdResponse.Status.PROTO_NOT_FOUND);
      return responseBuilder.build();
    }
    try {
      Message message = ProtoDb.getById(request.getId(), builder, serverState);
      if (message != null) {
        responseBuilder.setEntity(Any.pack(message));
      } else {
        responseBuilder.setStatus(GetByIdResponse.Status.ENTITY_NOT_FOUND);
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
    return responseBuilder.build();
  }
}
