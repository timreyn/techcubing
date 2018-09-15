package com.techcubing.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.proto.DeviceProto;
import com.techcubing.proto.ScorecardProto;
import com.techcubing.proto.wcif.WcifPerson;

public class LobbyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_lobby);

        WcifPerson staff =
                ActiveState.getActive(ActiveState.STAFF, this);
        if (staff == null) {
            startActivity(new Intent(this, LoggedOutActivity.class));
            return;
        }

        ScorecardProto.Scorecard scorecard =
                ActiveState.getActive(ActiveState.SCORECARD, this);
        DeviceProto.Device device = ActiveState.getActive(ActiveState.DEVICE, this);
        if (scorecard != null && device != null) {
            switch (device.getType()) {
                case JUDGE:
                    startActivity(new Intent(this, JudgeActivity.class));
                    return;
                case SCRAMBLER:
                    startActivity(new Intent(this, ScrambleActivity.class));
                    return;
            }
        }

        Button scanScorecardButton = findViewById(R.id.lobby_scan_scorecard_button);
        scanScorecardButton.setOnClickListener(view -> {
            Intent barcodeScannerIntent =
                    new Intent(this, BarcodeScannerActivity.class);
            barcodeScannerIntent.putExtra(
                    BarcodeScannerActivity.EXTRA_EXPECTED_HOST, "scorecard");
            barcodeScannerIntent.putExtra(
                    BarcodeScannerActivity.EXTRA_EXPECTED_PATH_PREFIX,
                    ActiveState.getActive(ActiveState.COMPETITION, this).getId());
            startActivity(barcodeScannerIntent);
        });

        Button logOutButton = findViewById(R.id.lobby_log_out_button);
        logOutButton.setOnClickListener(view -> {
            startActivity(new Intent(this, ReleaseDeviceActivity.class));
        });

        TextView staffNameTextView = findViewById(R.id.lobby_staff_name);
        staffNameTextView.setText("You're signed in as " + staff.getName());
    }
}
