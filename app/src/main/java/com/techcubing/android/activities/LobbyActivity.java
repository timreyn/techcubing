package com.techcubing.android.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.techcubing.android.R;

public class LobbyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_lobby);

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

        startActivity(new Intent(this, ScrambleCheckActivity.class));

        Button button = findViewById(R.id.lobby_scan_scorecard_button);
        button.setOnClickListener(view -> {
            Intent barcodeScannerIntent =
                    new Intent(this, BarcodeScannerActivity.class);
            barcodeScannerIntent.putExtra(
                    BarcodeScannerActivity.EXTRA_EXPECTED_HOST, "scorecard");
            this.startActivity(barcodeScannerIntent);
        });
    }
}
