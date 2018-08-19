package org.cubingusa.techcubing.services;

import io.grpc.stub.StreamObserver;
import org.cubingusa.techcubing.framework.ServerState;
import org.cubingusa.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardRequest;
import org.cubingusa.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardResponse;
import org.cubingusa.techcubing.proto.services.ListPersonsProto.ListPersonsRequest;
import org.cubingusa.techcubing.proto.services.ListPersonsProto.ListPersonsResponse;
import org.cubingusa.techcubing.proto.services.GetByIdProto.GetByIdRequest;
import org.cubingusa.techcubing.proto.services.GetByIdProto.GetByIdResponse;
import org.cubingusa.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardRequest;
import org.cubingusa.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardResponse;
import org.cubingusa.techcubing.proto.services.TechCubingServiceGrpc.TechCubingServiceImplBase;

public class TechCubingServiceImpl extends TechCubingServiceImplBase {
  private AcquireScorecardImpl acquireScorecardImpl;
  private ReleaseScorecardImpl releaseScorecardImpl;
  private ListPersonsImpl listPersonsImpl;
  private GetByIdImpl getByIdImpl;

  public TechCubingServiceImpl(ServerState serverState) {
    listPersonsImpl = new ListPersonsImpl(serverState);
    getByIdImpl = new GetByIdImpl(serverState);
    acquireScorecardImpl = new AcquireScorecardImpl(serverState);
    releaseScorecardImpl = new ReleaseScorecardImpl(serverState);
  }

  @Override
  public void listPersons(ListPersonsRequest request,
      StreamObserver<ListPersonsResponse> response) {
    response.onNext(listPersonsImpl.listPersons(request));
    response.onCompleted();
  }

  @Override
  public void getById(GetByIdRequest request,
      StreamObserver<GetByIdResponse> response) {
    response.onNext(getByIdImpl.getById(request));
    response.onCompleted();
  }

  @Override
  public void acquireScorecard(AcquireScorecardRequest request,
      StreamObserver<AcquireScorecardResponse> response) {
    response.onNext(acquireScorecardImpl.acquireScorecard(request));
    response.onCompleted();
  }

  @Override
  public void releaseScorecard(ReleaseScorecardRequest request,
      StreamObserver<ReleaseScorecardResponse> response) {
    response.onNext(releaseScorecardImpl.releaseScorecard(request));
    response.onCompleted();
  }
}
