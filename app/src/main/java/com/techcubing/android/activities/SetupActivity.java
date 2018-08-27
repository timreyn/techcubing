package com.techcubing.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.util.Base64;

import com.google.protobuf.InvalidProtocolBufferException;
import com.techcubing.android.util.SharedPreferenceKeys;
import com.techcubing.proto.DeviceConfigProto.DeviceConfig;
import com.techcubing.android.R;

public class SetupActivity extends AppCompatActivity {

    private static final String EXTRA_SETUP_DETAILS = "com.techcubing.SETUP_DETAILS";
    private static final String TAG = "TCSetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Intent intent = getIntent();

        if (intent != null && intent.getAction().equals("com.techcubing.SETUP_APP")) {
            try {
                DeviceConfig deviceConfig =
                        DeviceConfig.parseFrom(
                                Base64.getUrlDecoder().decode(
                                        intent.getStringExtra(EXTRA_SETUP_DETAILS)));
                SharedPreferences.Editor sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(this).edit();
                sharedPreferences.putString(
                        SharedPreferenceKeys.DEVICE_ID,
                        deviceConfig.getDevice().getId());
                sharedPreferences.putInt(
                        SharedPreferenceKeys.DEVICE_TYPE,
                        deviceConfig.getDevice().getTypeValue());
                sharedPreferences.putString(
                        SharedPreferenceKeys.SERVER_HOST,
                        deviceConfig.getServerHost());
                sharedPreferences.putInt(
                        SharedPreferenceKeys.SERVER_PORT,
                        deviceConfig.getServerPort());
                sharedPreferences.commit();

                TextView textView = (TextView) findViewById(R.id.setup_activity_content);
                textView.setText(deviceConfig.getDevice().getVisibleName());
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "Failed to parse DeviceConfig proto!", e);
            }
        }

        startActivity(new Intent(this, BarcodeScannerActivity.class));

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .contains(SharedPreferenceKeys.DEVICE_ID)) {
            startActivity(new Intent(this, LobbyActivity.class));
        }
    }
}