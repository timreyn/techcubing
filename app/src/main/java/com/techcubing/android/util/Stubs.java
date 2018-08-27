package com.techcubing.android.util;

import android.content.Context;

import com.techcubing.proto.DeviceConfigProto;
import com.techcubing.proto.services.TechCubingServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.android.AndroidChannelBuilder;

public class Stubs {
    public static TechCubingServiceGrpc.TechCubingServiceBlockingStub blockingStub(
            Context context, Context applicationContext) {
        return TechCubingServiceGrpc.newBlockingStub(getChannel(context, applicationContext));
    }

    public static TechCubingServiceGrpc.TechCubingServiceFutureStub futureStub(
            Context context, Context applicationContext) {
        return TechCubingServiceGrpc.newFutureStub(getChannel(context, applicationContext));
    }

    private static ManagedChannel getChannel(Context context, Context applicationContext) {
        DeviceConfigProto.DeviceConfig deviceConfig =
                ActiveState.getActive(ActiveState.DEVICE_CONFIG, context);

        return AndroidChannelBuilder.forAddress(
                deviceConfig.getServerHost(), deviceConfig.getServerPort())
                .usePlaintext()
                .context(applicationContext)
                .build();
    }
}
