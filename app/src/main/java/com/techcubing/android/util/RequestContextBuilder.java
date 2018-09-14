package com.techcubing.android.util;

import android.content.Context;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.techcubing.proto.DeviceProto;
import com.techcubing.proto.RequestContextProto.RequestContext;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RequestContextBuilder {
    private static final String TAG = "TCRequestContext";

    public static RequestContext signRequest(MessageLite.Builder request, Context context) {
        DeviceProto.Device device = ActiveState.getActive(ActiveState.DEVICE, context);
        try {
            RequestContext.Builder builder =
                    RequestContext.newBuilder().setDeviceId(device.getId());
            builder.setSignedRequest(
                    ByteString.copyFrom(
                            EncodingUtil.encode(request.build().toByteArray(), device)));
            return builder.build();
        } catch (Exception e) {
            Log.e(TAG, "Failed to sign request", e);
            return null;
        }
    }
}
