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
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(device.getSecretKey().toByteArray(), "AES"),
                    new IvParameterSpec(device.getIv().toByteArray()));
            builder.setSignedRequest(
                    ByteString.copyFrom(cipher.doFinal(request.build().toByteArray())));
            return builder.build();
        } catch (Exception e) {
            Log.e(TAG, "Failed to sign request", e);
            return null;
        }
    }
}
