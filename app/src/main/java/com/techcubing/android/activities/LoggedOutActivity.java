package com.techcubing.android.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.proto.DeviceConfigProto;
import com.techcubing.proto.wcif.WcifPerson;

public class LoggedOutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        WcifPerson person = ActiveState.getActive(ActiveState.STAFF, this);
        if (person != null) {
            startActivity(new Intent(this, LobbyActivity.class));
            return;
        }

        DeviceConfigProto.DeviceConfig deviceConfig =
                ActiveState.getActive(ActiveState.DEVICE_CONFIG, this);
        if (deviceConfig == null) {
            return;
        }

        setContentView(R.layout.activity_logged_out);

        if (checkSelfPermission(Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        Button button = findViewById(R.id.logged_out_scan_code_button);
        button.setOnClickListener(view -> {
            Intent barcodeScannerIntent =
                    new Intent(this, BarcodeScannerActivity.class);
            barcodeScannerIntent.putExtra(
                    BarcodeScannerActivity.EXTRA_EXPECTED_HOST, "acquire_device");
            barcodeScannerIntent.putExtra(
                    BarcodeScannerActivity.EXTRA_EXPECTED_PATH_PREFIX,
                    "/" + deviceConfig.getWcaEnvironment().toString().toLowerCase());
            this.startActivity(barcodeScannerIntent);
        });
    }
}
