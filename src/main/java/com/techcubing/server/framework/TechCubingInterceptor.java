package com.techcubing.server.framework;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.RequestContextProto.RequestContext;

public class TechCubingInterceptor implements ServerInterceptor {
  private ProtoDb protoDb;

  public TechCubingInterceptor(ServerState serverState) {
    this.protoDb = serverState.getProtoDb();
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
    ServerCall<ReqT, RespT> call,
    final Metadata requestHeaders,
    ServerCallHandler<ReqT, RespT> next) {

    return new SimpleForwardingServerCallListener<ReqT>(
        next.startCall(call, requestHeaders)) {
      @Override
      public void onMessage(ReqT messageT) {
        try {
          Message message = (Message) messageT;
          FieldDescriptor fieldDescriptor =
            message.getDescriptorForType().findFieldByName("context");
          RequestContext context =
            (RequestContext) message.getField(fieldDescriptor);
          Device device =
            (Device) protoDb.getById(context.getDeviceId(), Device.newBuilder());
          if (device == null) {
            throw new RuntimeException("Unknown device ID");
          }
          // Confirm the signature of the request.
          Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
          cipher.init(
              Cipher.DECRYPT_MODE,
              new SecretKeySpec(device.getSecretKey().toByteArray(), "AES"),
              new IvParameterSpec(device.getIv().toByteArray()));
          byte[] decryptedBytes = cipher.doFinal(context.getSignedRequest().toByteArray());

          byte[] cleanRequestBytes =
            message.toBuilder()
                .clearField(fieldDescriptor)
                .build()
                .toByteArray();

          if (!Arrays.equals(cleanRequestBytes, decryptedBytes)) {
            throw new RuntimeException("Failed to verify signature of request.");
          }
          super.onMessage(messageT);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }
}
