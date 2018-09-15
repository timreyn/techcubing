package com.techcubing.android.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.util.Base64;

import com.google.protobuf.InvalidProtocolBufferException;
import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.proto.DeviceConfigProto.DeviceConfig;
import com.techcubing.proto.DeviceProto;

public class SetupActivity extends AppCompatActivity {

    private static final String EXTRA_SETUP_DETAILS = "com.techcubing.SETUP_DETAILS";
    private static final String TAG = "TCSetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Intent intent = getIntent();

        if (intent != null && intent.getAction() != null &&
                intent.getAction().equals("com.techcubing.SETUP_APP")) {
            try {
                DeviceConfig deviceConfig =
                        DeviceConfig.parseFrom(Base64.decode(
                                intent.getStringExtra(EXTRA_SETUP_DETAILS), Base64.URL_SAFE));
                Log.i(TAG, deviceConfig.toString());
                ActiveState.clearState(this);
                ActiveState.setActive(ActiveState.DEVICE, deviceConfig.getDevice(), this);
                ActiveState.setActive(ActiveState.DEVICE_CONFIG, deviceConfig, this);
                ActiveState.setActive(
                        ActiveState.COMPETITION, deviceConfig.getCompetition(),this);

                TextView textView = findViewById(R.id.setup_activity_content);
                textView.setText(deviceConfig.getDevice().getVisibleName());
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "Failed to parse DeviceConfig proto!", e);
            }
        }

        DeviceProto.Device device = ActiveState.getActive(ActiveState.DEVICE, this);
        if (device != null) {
            startActivity(new Intent(this, LoggedOutActivity.class));
        }
    }
}