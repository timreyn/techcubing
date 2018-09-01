package com.techcubing.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.android.util.RequestContextBuilder;
import com.techcubing.android.util.Stubs;
import com.techcubing.proto.DeviceProto;
import com.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardRequest;
import com.techcubing.proto.services.AcquireScorecardProto.AcquireScorecardResponse;
import com.techcubing.proto.services.TechCubingServiceGrpc.TechCubingServiceFutureStub;

import javax.annotation.Nullable;


public class AcquireScorecardActivity extends AppCompatActivity {
    private static final String TAG = "TCAcquireScorecard";
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.toUri(0));
        String scorecardId = uri.getPathSegments().get(0);

        TechCubingServiceFutureStub stub =
                Stubs.futureStub(this, getApplicationContext());

        DeviceProto.Device device = ActiveState.getActive(ActiveState.DEVICE, this);
        if (device == null) {
            onFailure("Device not initialized.");
            return;
        }

        AcquireScorecardRequest.Builder requestBuilder = AcquireScorecardRequest.newBuilder()
                .setScorecardId(scorecardId);

        requestBuilder.setContext(RequestContextBuilder.build(requestBuilder, this));

        Futures.addCallback(
                stub.acquireScorecard(requestBuilder.build()),
                new FutureCallback<AcquireScorecardResponse>() {
                    @Override
                    public void onSuccess(@Nullable AcquireScorecardResponse response) {
                        if (response == null) {
                            AcquireScorecardActivity.this.onFailure(
                                    "Null response from acquireScorecard");
                            return;
                        }
                        switch (response.getStatus()) {
                            case OK:
                                Log.i(TAG, "Successfully acquired scorecard " + scorecardId);
                                ActiveState.setActive(
                                        ActiveState.SCORECARD,
                                        response.getScorecard(),
                                        AcquireScorecardActivity.this);
                                ActiveState.setActive(
                                        ActiveState.ATTEMPT_NUMBER,
                                        response.getAttemptNumber(),
                                        AcquireScorecardActivity.this);

                                switch (device.getType()) {
                                    case JUDGE:
                                        startActivity(new Intent(
                                                AcquireScorecardActivity.this,
                                                JudgeActivity.class));
                                    case SCRAMBLER:
                                        startActivity(new Intent(
                                                AcquireScorecardActivity.this,
                                                ScrambleActivity.class));
                                }
                                return;
                            default:
                                AcquireScorecardActivity.this.onFailure(
                                        response.getStatus().name());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        AcquireScorecardActivity.this.onFailure(throwable.getMessage());
                    }
                }
        );
    }

    private void onFailure(String failureReason) {
        Log.e(TAG, failureReason);
        runOnUiThread(() -> {
            setContentView(R.layout.activity_acquire_scorecard_failure);
            TextView textView = findViewById(R.id.acquire_scorecard_failure_reason);
            textView.setText(failureReason);
            Button button = findViewById(R.id.acquire_scorecard_failure_button);
            button.setOnClickListener(view -> {
                startActivity(new Intent(
                        AcquireScorecardActivity.this,
                        LobbyActivity.class));
            });
            Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
        });
    }
}
