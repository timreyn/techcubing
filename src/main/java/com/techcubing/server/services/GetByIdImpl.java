package com.techcubing.server.services;

import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import java.io.IOException;
import java.sql.SQLException;

import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ProtoRegistry;
import com.techcubing.server.framework.ServerState;
import com.techcubing.proto.OptionsProto;
import com.techcubing.proto.services.GetByIdProto.GetByIdRequest;
import com.techcubing.proto.services.GetByIdProto.GetByIdResponse;

class GetByIdImpl {
  ServerState serverState;

  public GetByIdImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public GetByIdResponse getById(GetByIdRequest request) {
    GetByIdResponse.Builder responseBuilder = GetByIdResponse.newBuilder();
    ProtoRegistry protoRegistry = serverState.getProtoRegistry();
    Class clazz = protoRegistry.getClassForName(request.getProtoType());
    if (clazz == null) {
      responseBuilder.setStatus(GetByIdResponse.Status.PROTO_NOT_FOUND);
      return responseBuilder.build();
    }
    Descriptor descriptor = protoRegistry.getDescriptorForType(clazz);
    if (descriptor.getOptions().getExtension(OptionsProto.disableGetById)) {
      responseBuilder.setStatus(GetByIdResponse.Status.METHOD_DISABLED);
      return responseBuilder.build();
    }
    try {
      Message message = serverState.getProtoDb().getById(request.getId(), clazz);
      if (message != null) {
        Message.Builder builder = message.toBuilder();
        for (FieldDescriptor field : descriptor.getFields()) {
          if (field.getOptions().getExtension(OptionsProto.clearForGetById)) {
            builder.clearField(field);
          }
        }
        responseBuilder.setEntity(Any.pack(builder.build()));
      } else {
        responseBuilder.setStatus(GetByIdResponse.Status.ENTITY_NOT_FOUND);
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
    return responseBuilder.build();
  }
}
