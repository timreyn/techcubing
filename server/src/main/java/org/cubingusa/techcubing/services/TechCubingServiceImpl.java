package org.cubingusa.techcubing.services;

import io.grpc.stub.StreamObserver;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.proto.services.ListPersonsProto.ListPersonsRequest;
import org.cubingusa.techcubing.proto.services.ListPersonsProto.ListPersonsResponse;
import org.cubingusa.techcubing.proto.services.TechCubingServiceGrpc.TechCubingServiceImplBase;

public class TechCubingServiceImpl extends TechCubingServiceImplBase {
  private ListPersonsImpl listPersonsImpl;

  public TechCubingServiceImpl(ServerState serverState) {
    listPersonsImpl = new ListPersonsImpl(serverState);
  }

  @Override
  public void listPersons(ListPersonsRequest request,
      StreamObserver<ListPersonsResponse> response) {
    response.onNext(listPersonsImpl.listPersons(request));
    response.onCompleted();
  }
}
