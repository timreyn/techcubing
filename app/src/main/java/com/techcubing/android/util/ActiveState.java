package com.techcubing.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.techcubing.proto.DeviceConfigProto;
import com.techcubing.proto.DeviceProto;
import com.techcubing.proto.ScorecardProto;
import com.techcubing.proto.wcif.WcifCompetition;

public class ActiveState {
    public static final StateKey<DeviceProto.Device> DEVICE =
            new ProtoStateKey<>("DEVICE", DeviceProto.Device.parser());

    public static final StateKey<ScorecardProto.Scorecard> SCORECARD =
            new ProtoStateKey<>("SCORECARD", ScorecardProto.Scorecard.parser());

    public static final StateKey<DeviceConfigProto.DeviceConfig> DEVICE_CONFIG =
            new ProtoStateKey<>("DEVICE_CONFIG", DeviceConfigProto.DeviceConfig.parser());

    public static final StateKey<WcifCompetition> COMPETITION =
            new ProtoStateKey<>("COMPETITION", WcifCompetition.parser());

    public static final StateKey<Integer> ATTEMPT_NUMBER = new IntStateKey("ATTEMPT_NUMBER");

    @Nullable
    public static <E> E getActive(StateKey<E> key, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedPreferences.contains(key.key)) {
            return null;
        }
        E active = key.getValue(sharedPreferences);
        if (active == null) {
            setActive(key, null, context);
        }
        return active;
    }

    public static <E> void setActive(
            StateKey<E> key, @Nullable E value, Context context) {
        SharedPreferences.Editor sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        if (value == null) {
            sharedPreferences.remove(key.key);
        } else {
            key.setValue(sharedPreferences, value);
        }
        sharedPreferences.commit();
    }

    private static abstract class StateKey<E> {
        String key;

        StateKey(String key) {
            this.key = "STATE__" + key;
        }
        final String key() {
            return key;
        }
        abstract @Nullable E getValue(SharedPreferences preferences);
        abstract void setValue(SharedPreferences.Editor preferences, @Nullable E e);
    }

    private static class ProtoStateKey<E extends MessageLite> extends StateKey<E> {
        private final Parser<E> parser;

        ProtoStateKey(String key, Parser<E> parser) {
            super(key);
            this.parser = parser;
        }

        @Override
        @Nullable
        E getValue(SharedPreferences preferences) {
            String encodedMessage = preferences.getString(key(), null);
            if (encodedMessage == null) {
                return null;
            }
            try {
                return parser.parseFrom(Base64.decode(encodedMessage, Base64.URL_SAFE));
            } catch (InvalidProtocolBufferException e) {
                return null;
            }
        }

        @Override
        void setValue(SharedPreferences.Editor preferences, @Nullable E e) {
            preferences.putString(
                    key,
                    Base64.encodeToString(e.toByteArray(), Base64.URL_SAFE));
        }
    }

    private static class IntStateKey extends StateKey<Integer> {
        IntStateKey(String key) {
            super(key);
        }
        @Override
        @Nullable
        Integer getValue(SharedPreferences preferences) {
            return preferences.getInt(key, -1);
        }

        @Override
        void setValue(SharedPreferences.Editor preferences, Integer value) {
            preferences.putInt(key, value);
        }
    }
}
