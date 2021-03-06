package com.techcubing.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.jakewharton.disklrucache.DiskLruCache;
import com.techcubing.android.BuildConfig;
import com.techcubing.proto.DeviceConfigProto;
import com.techcubing.proto.DeviceProto;
import com.techcubing.proto.ScorecardProto;
import com.techcubing.proto.services.GetByIdProto.GetByIdRequest;
import com.techcubing.proto.services.GetByIdProto.GetByIdResponse;
import com.techcubing.proto.services.GetScrambleProto.GetScrambleResponse;
import com.techcubing.proto.services.TechCubingServiceGrpc;
import com.techcubing.proto.wcif.WcifCompetition;
import com.techcubing.proto.wcif.WcifEvent;
import com.techcubing.proto.wcif.WcifPerson;
import com.techcubing.proto.wcif.WcifRound;

import java.io.File;
import java.io.IOException;

public class ActiveState {
    private static final String TAG = "TCActiveState";

    public static final ProtoStateKey<DeviceProto.Device> DEVICE =
            new ProtoStateKey<>("DEVICE", DeviceProto.Device.parser(), "techcubing.Device");

    public static final ProtoStateKey<ScorecardProto.Scorecard> SCORECARD =
            new ProtoStateKey<>(
                    "SCORECARD", ScorecardProto.Scorecard.parser(), "techcubing.Scorecard");

    public static final ProtoStateKey<DeviceConfigProto.DeviceConfig> DEVICE_CONFIG =
            new ProtoStateKey<>(
                    "DEVICE_CONFIG", DeviceConfigProto.DeviceConfig.parser(),
                    "techcubing.DeviceConfig");

    public static final ProtoStateKey<WcifCompetition> COMPETITION =
            new ProtoStateKey<>(
                    "COMPETITION", WcifCompetition.parser(), "techcubing.wcif.WcifCompetition");

    public static final ProtoStateKey<WcifRound> ROUND =
            new ProtoStateKey<>("ROUND", WcifRound.parser(), "techcubing.wcif.WcifRound");

    public static final ProtoStateKey<WcifEvent> EVENT =
            new ProtoStateKey<>("EVENT", WcifEvent.parser(), "techcubing.wcif.WcifEvent");

    public static final ProtoStateKey<WcifPerson> COMPETITOR =
            new ProtoStateKey<>(
                    "COMPETITOR", WcifPerson.parser(), "techcubing.wcif.WcifPerson");

    public static final ProtoStateKey<WcifPerson> STAFF =
            new ProtoStateKey<>(
                    "STAFF", WcifPerson.parser(), "techcubing.wcif.WcifPerson");

    public static final ProtoStateKey<GetScrambleResponse> SCRAMBLE =
            new ProtoStateKey<>(
                    "SCRAMBLE", GetScrambleResponse.parser(),
                    "techcubing.GetScrambleResponse");

    public static final StateKey<Integer> ATTEMPT_NUMBER = new IntStateKey("ATTEMPT_NUMBER");

    private static final StateKey[] ALL_KEYS = new StateKey[]{
            DEVICE, SCORECARD, DEVICE_CONFIG, COMPETITION, ROUND, EVENT, COMPETITOR, ATTEMPT_NUMBER};

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

    @Nullable
    public static <E extends MessageLite> E setActiveById(
            ProtoStateKey<E> key, String id, Context context, Context applicationContext) {
        E cachedE = readFromCache(key, id, context);
        if (cachedE != null) {
            setActive(key, cachedE, context);
            return cachedE;
        }

        TechCubingServiceGrpc.TechCubingServiceBlockingStub stub =
                Stubs.blockingStub(context, applicationContext);
        GetByIdRequest.Builder requestBuilder =
                GetByIdRequest.newBuilder()
                    .setId(id)
                    .setProtoType(key.name);
        requestBuilder.setContext(RequestContextBuilder.signRequest(requestBuilder, context));

        GetByIdResponse response = stub.getById(requestBuilder.build());

        E e;
        try {
            e = key.parser.parseFrom(response.getEntity().getValue().toByteArray());
            setActive(key, e, context);
        } catch (InvalidProtocolBufferException ex) {
            return null;
        }
        writeToCache(key, id, e, context);
        return e;
    }

    public static void clearState(Context context) {
        for (StateKey key : ALL_KEYS) {
            setActive(key, null, context);
        }
        clearCache(context);
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
        private final String name;

        ProtoStateKey(String key, Parser<E> parser, String name) {
            super(key);
            this.parser = parser;
            this.name = name;
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

    private static DiskLruCache getCache(Context context) throws IOException {
        return DiskLruCache.open(
                new File(context.getFilesDir(), "cache"),
                BuildConfig.VERSION_CODE, 1, 32 * 1024);
    }

    public static synchronized <E extends MessageLite> void writeToCache(
            ProtoStateKey<E> protoKey, String key, E e, Context context) {
        DiskLruCache cache = null;
        String fullKey = (protoKey.key + "__" + key).toLowerCase();
        try {
            cache = getCache(context);
            DiskLruCache.Editor editor = cache.edit(fullKey);
            editor.set(0, Base64.encodeToString(e.toByteArray(), Base64.URL_SAFE));
            editor.commit();
        } catch (IOException ex) {
            Log.e(TAG, "Failed to write to cache for key " + fullKey, ex);
        } finally {
            if (cache != null) {
                try {
                    cache.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Failed to close cache.", ex);
                }
            }
        }
    }

    @Nullable
    public static synchronized <E extends MessageLite> E readFromCache(
            ProtoStateKey<E> protoKey, String key, Context context) {
        DiskLruCache cache = null;
        String fullKey = (protoKey.key + "__" + key).toLowerCase();
        try {
            cache = getCache(context);
            DiskLruCache.Snapshot snapshot = cache.get(fullKey);
            if (snapshot == null) {
                cache.close();
                Log.i(TAG, "Cache miss for " + fullKey);
                return null;
            }
            E e = protoKey.parser.parseFrom(
                    Base64.decode(snapshot.getString(0), Base64.URL_SAFE));
            cache.close();
            Log.i(TAG, "Cache hit for " + fullKey);
            return e;
        } catch (IOException ex) {
            Log.e(TAG, "Failed to read cache for key " + fullKey, ex);
            tryDeleteFromCache(protoKey, key, context);
            return null;
        } finally {
            if (cache != null) {
                try {
                    cache.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Failed to close cache.", ex);
                }
            }
        }
    }

    public static synchronized <E extends MessageLite> void tryDeleteFromCache(
            ProtoStateKey<E> protoKey, String key, Context context) {
        DiskLruCache cache = null;
        String fullKey = (protoKey.key + "__" + key).toLowerCase();
        try {
            cache = getCache(context);
            cache.remove(fullKey);
        } catch (IOException ex) {
            Log.e(TAG, "Failed to delete from cache " + fullKey, ex);
        } finally {
            if (cache != null) {
                try {
                    cache.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Failed to close cache.", ex);
                }
            }
        }
    }

    public static synchronized void clearCache(Context context) {
        DiskLruCache cache = null;
        try {
            cache = getCache(context);
            cache.delete();
        } catch (IOException ex) {
            Log.e(TAG, "Failed to clear cache.", ex);
        } finally {
            if (cache != null) {
                try {
                    cache.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Failed to close cache.", ex);
                }
            }
        }
    }
}
