package com.techcubing.server.services;

import io.grpc.stub.StreamObserver;

import com.techcubing.server.framework.ServerState;
import com.techcubing.proto.services.AcquireDeviceProto.AcquireDeviceRequest;
import com.techcubing.proto.services.AcquireDeviceProto.AcquireDeviceResponse;
import com.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardRequest;
import com.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardResponse;
import com.techcubing.proto.services.ListPersonsProto.ListPersonsRequest;
import com.techcubing.proto.services.ListPersonsProto.ListPersonsResponse;
import com.techcubing.proto.services.GetByIdProto.GetByIdRequest;
import com.techcubing.proto.services.GetByIdProto.GetByIdResponse;
import com.techcubing.proto.services.GetScrambleProto.GetScrambleRequest;
import com.techcubing.proto.services.GetScrambleProto.GetScrambleResponse;
import com.techcubing.proto.services.ReleaseDeviceProto.ReleaseDeviceRequest;
import com.techcubing.proto.services.ReleaseDeviceProto.ReleaseDeviceResponse;
import com.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardRequest;
import com.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardResponse;
import com.techcubing.proto.services.TechCubingServiceGrpc.TechCubingServiceImplBase;

public class TechCubingServiceImpl extends TechCubingServiceImplBase {
  private AcquireDeviceImpl acquireDeviceImpl;
  private AcquireScorecardImpl acquireScorecardImpl;
  private ListPersonsImpl listPersonsImpl;
  private GetByIdImpl getByIdImpl;
  private GetScrambleImpl getScrambleImpl;
  private ReleaseDeviceImpl releaseDeviceImpl;
  private ReleaseScorecardImpl releaseScorecardImpl;

  public TechCubingServiceImpl(ServerState serverState) {
    acquireDeviceImpl = new AcquireDeviceImpl(serverState);
    acquireScorecardImpl = new AcquireScorecardImpl(serverState);
    getByIdImpl = new GetByIdImpl(serverState);
    getScrambleImpl = new GetScrambleImpl(serverState);
    listPersonsImpl = new ListPersonsImpl(serverState);
    releaseDeviceImpl = new ReleaseDeviceImpl(serverState);
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

  @Override
  public void getScramble(GetScrambleRequest request,
      StreamObserver<GetScrambleResponse> response) {
    response.onNext(getScrambleImpl.getScramble(request));
    response.onCompleted();
  }

  @Override
  public void acquireDevice(AcquireDeviceRequest request,
      StreamObserver<AcquireDeviceResponse> response) {
    response.onNext(acquireDeviceImpl.acquireDevice(request));
    response.onCompleted();
  }

  @Override
  public void releaseDevice(ReleaseDeviceRequest request,
      StreamObserver<ReleaseDeviceResponse> response) {
    response.onNext(releaseDeviceImpl.releaseDevice(request));
    response.onCompleted();
  }
}
