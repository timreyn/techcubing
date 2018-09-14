package com.techcubing.android.util;

import com.techcubing.proto.DeviceProto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncodingUtil {
    public static byte[] encode(byte[] input, DeviceProto.Device device)
            throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(
                Cipher.ENCRYPT_MODE,
                new SecretKeySpec(device.getSecretKey().toByteArray(), "AES"),
                new IvParameterSpec(device.getIv().toByteArray()));
        return cipher.doFinal(input);
    }

    public static byte[] decode(byte[] input, DeviceProto.Device device)
            throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(device.getSecretKey().toByteArray(), "AES"),
                new IvParameterSpec(device.getIv().toByteArray()));
        return cipher.doFinal(input);
    }
}