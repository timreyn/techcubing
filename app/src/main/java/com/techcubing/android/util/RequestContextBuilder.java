package com.techcubing.android.util;

import android.content.Context;

import com.google.protobuf.MessageLite;
import com.techcubing.proto.DeviceProto;
import com.techcubing.proto.RequestContextProto.RequestContext;

public class RequestContextBuilder {
    public static RequestContext build(
            MessageLite.Builder messageBuilder,
            Context context) {
        DeviceProto.Device device = ActiveState.getActive(ActiveState.DEVICE, context);
        RequestContext.Builder builder = RequestContext.newBuilder();
        if (device != null) {
            builder.setDeviceId(device.getId());
        }
        return builder.build();
    }
}
