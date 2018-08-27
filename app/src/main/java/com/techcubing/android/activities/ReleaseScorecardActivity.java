package com.techcubing.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.android.util.Stubs;
import com.techcubing.proto.DeviceProto;
import com.techcubing.proto.DeviceTypeProto;
import com.techcubing.proto.RequestContextProto;
import com.techcubing.proto.ScorecardProto;
import com.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardRequest;
import com.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardResponse;
import com.techcubing.proto.services.TechCubingServiceGrpc;

import javax.annotation.Nullable;


public class ReleaseScorecardActivity extends AppCompatActivity {
    private static final String TAG = "TCReleaseScorecard";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        TechCubingServiceGrpc.TechCubingServiceFutureStub stub =
                Stubs.futureStub(this, getApplicationContext());
        @Nullable ScorecardProto.Scorecard scorecard =
                ActiveState.getActive(ActiveState.SCORECARD, this);
        if (scorecard == null) {
            onFailure("You don't currently have a competitor.");
            return;
        }

        @Nullable DeviceProto.Device device =
                ActiveState.getActive(ActiveState.DEVICE, this);
        if (device == null) {
            onFailure("Device not initialized.");
            return;
        }

        ReleaseScorecardRequest.Builder requestBuilder = ReleaseScorecardRequest.newBuilder()
                .setScorecardId(scorecard.getId())
                .setContext(RequestContextProto.RequestContext.newBuilder().setDeviceId(
                        device.getId()))
                .setAttemptNumber(ActiveState.getActive(ActiveState.ATTEMPT_NUMBER, this))
                .setOutcome(ScorecardProto.AttemptPartOutcome.OK);

        if (device.getType() == DeviceTypeProto.DeviceType.JUDGE) {
            requestBuilder.setResult(
                    ScorecardProto.AttemptResult.newBuilder().setFinalTime(8000));
        }

        Futures.addCallback(
                stub.releaseScorecard(requestBuilder.build()),
                new FutureCallback<ReleaseScorecardResponse>() {
                    @Override
                    public void onSuccess(@Nullable ReleaseScorecardResponse response) {
                        if (response == null) {
                            ReleaseScorecardActivity.this.onFailure(
                                    "Null response from server!");
                            return;
                        }
                        switch (response.getStatus()) {
                            case OK:
                                Log.i(TAG, "Successfully released scorecard " +
                                        scorecard.getId());
                                ActiveState.setActive(
                                        ActiveState.ATTEMPT_NUMBER, null,
                                        ReleaseScorecardActivity.this);
                                ActiveState.setActive(
                                        ActiveState.SCORECARD, null,
                                        ReleaseScorecardActivity.this);
                                startActivity(new Intent(
                                        ReleaseScorecardActivity.this,
                                        LobbyActivity.class));
                                return;
                            default:
                                ReleaseScorecardActivity.this.onFailure(
                                        response.getStatus().toString());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        ReleaseScorecardActivity.this.onFailure(throwable.getMessage());
                    }
                }
        );

        ActiveState.setActive(ActiveState.SCORECARD, null, this);
        startActivity(new Intent(this, LobbyActivity.class));
    }

    private void onFailure(String failureReason) {
        Log.e(TAG, failureReason);
        runOnUiThread(() -> {
            setContentView(R.layout.activity_release_scorecard_failure);
            TextView textView = findViewById(R.id.release_scorecard_failure_reason);
            textView.setText(failureReason);
            Button button = findViewById(R.id.release_scorecard_failure_button);
            button.setOnClickListener(view -> {
                startActivity(new Intent(
                        ReleaseScorecardActivity.this,
                        LobbyActivity.class));
            });
        });
    }
}
