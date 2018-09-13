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
import com.techcubing.android.util.RequestContextBuilder;
import com.techcubing.android.util.Stubs;
import com.techcubing.proto.DeviceProto;
import com.techcubing.proto.DeviceTypeProto;
import com.techcubing.proto.ScorecardProto;
import com.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardRequest;
import com.techcubing.proto.services.ReleaseScorecardProto.ReleaseScorecardResponse;
import com.techcubing.proto.services.TechCubingServiceGrpc;

import javax.annotation.Nullable;


public class ReleaseScorecardActivity extends AppCompatActivity {
    private static final String TAG = "TCReleaseScorecard";

    public static final String EXTRA_OUTCOME = "com.techcubing.OUTCOME";

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
                .setAttemptNumber(ActiveState.getActive(ActiveState.ATTEMPT_NUMBER, this));

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_OUTCOME)) {
            requestBuilder.setOutcome(ScorecardProto.AttemptPartOutcome.forNumber(
                    intent.getIntExtra(EXTRA_OUTCOME, -1)));
        } else {
            requestBuilder.setOutcome(ScorecardProto.AttemptPartOutcome.OK);
        }

        if (device.getType() == DeviceTypeProto.DeviceType.JUDGE) {
            requestBuilder.setResult(
                    ScorecardProto.AttemptResult.newBuilder().setFinalTime(8000));
        }
        requestBuilder.setContext(RequestContextBuilder.signRequest(requestBuilder, this));

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
            setContentView(R.layout.generic_failure);
            TextView failureDescription = findViewById(R.id.failure_description);
            failureDescription.setText("Failed to release scorecard.");
            TextView failureReasonView = findViewById(R.id.failure_reason);
            failureReasonView.setText(failureReason);
            Button button = findViewById(R.id.failure_button);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(
                        ReleaseScorecardActivity.this, LobbyActivity.class);
                startActivity(intent);
            });
        });
    }
}
