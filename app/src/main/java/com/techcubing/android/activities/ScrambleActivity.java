package com.techcubing.android.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.android.util.EncodingUtil;
import com.techcubing.android.util.RequestContextBuilder;
import com.techcubing.android.util.Stubs;
import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.ScorecardProto;
import com.techcubing.proto.services.GetScrambleProto.GetScrambleRequest;
import com.techcubing.proto.services.GetScrambleProto.GetScrambleResponse;
import com.techcubing.proto.services.TechCubingServiceGrpc;
import com.techcubing.proto.wcif.WcifPerson;
import com.techcubing.proto.wcif.WcifRound;

import java.util.Locale;

import javax.annotation.Nullable;

public class ScrambleActivity extends AppCompatActivity {
    private static final String TAG = "TCScrambleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scramble);

        ScorecardProto.Scorecard scorecard =
                ActiveState.getActive(ActiveState.SCORECARD, this);
        Integer attemptNumber = ActiveState.getActive(ActiveState.ATTEMPT_NUMBER, this);
        WcifPerson competitor = ActiveState.getActive(ActiveState.COMPETITOR, this);
        WcifRound round = ActiveState.getActive(ActiveState.ROUND, this);
        Device device = ActiveState.getActive(ActiveState.DEVICE, this);

        if (scorecard == null || attemptNumber == null || competitor == null || round == null) {
            // There's something wrong.  Release our scorecard.
            Intent intent = new Intent(this, ReleaseScorecardActivity.class);
            intent.putExtra(
                    ReleaseScorecardActivity.EXTRA_OUTCOME,
                    ScorecardProto.AttemptPartOutcome.PROTOCOL_FAILURE_VALUE);
            startActivity(intent);
            return;
        }
        ScorecardProto.Attempt attempt = scorecard.getAttempts(attemptNumber);

        String headerText = String.format(
                Locale.US, "%s %s attempt %d", competitor.getName(), round.getId(), attemptNumber);
        TextView header = findViewById(R.id.scramble_header);
        header.setText(headerText);

        // Check if the scramble is cached already.
        GetScrambleResponse scrambleResponse =
                ActiveState.readFromCache(
                        ActiveState.SCRAMBLE, attempt.getScrambleId(), this);
        if (scrambleResponse != null) {
            AsyncTask.execute(() -> {
                onScrambleReady(scrambleResponse, device);
            });
        } else {
            // Get the scramble from the server.
            TechCubingServiceGrpc.TechCubingServiceFutureStub stub =
                    Stubs.futureStub(this, getApplicationContext());
            GetScrambleRequest.Builder requestBuilder =
                    GetScrambleRequest.newBuilder().setId(attempt.getScrambleId());
            requestBuilder.setContext(RequestContextBuilder.signRequest(requestBuilder, this));

            Futures.addCallback(
                    stub.getScramble(requestBuilder.build()),
                    new FutureCallback<GetScrambleResponse>() {
                        @Override
                        public void onSuccess(@Nullable GetScrambleResponse response) {
                            if (response == null) {
                                ScrambleActivity.this.onFailure(
                                        "No response from the server");
                                return;
                            }
                            ActiveState.writeToCache(
                                    ActiveState.SCRAMBLE, attempt.getScrambleId(), response,
                                    ScrambleActivity.this);

                            ScrambleActivity.this.runOnUiThread(() -> {
                                onScrambleReady(response, device);
                            });
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ScrambleActivity.this.onFailure(t.getMessage());
                        }
                    }
            );
        }
    }

    private void onFailure(String failureReason) {
        Log.e(TAG, failureReason);
        runOnUiThread(() -> {
            setContentView(R.layout.generic_failure);
            TextView failureDescription = findViewById(R.id.failure_description);
            failureDescription.setText("Failed to load scramble from server.");
            TextView failureReasonView = findViewById(R.id.failure_reason);
            failureReasonView.setText(failureReason);
            Button button = findViewById(R.id.failure_button);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(
                        ScrambleActivity.this, ReleaseScorecardActivity.class);
                intent.putExtra(
                        ReleaseScorecardActivity.EXTRA_OUTCOME,
                        ScorecardProto.AttemptPartOutcome.PROTOCOL_FAILURE_VALUE);
                startActivity(intent);
            });
        });
    }

    private void onScrambleReady(GetScrambleResponse response, Device device) {
        ActiveState.setActive(ActiveState.SCRAMBLE, response, this);

        String scramble;
        try {
            scramble = new String(EncodingUtil.decode(
                    response.getEncryptedScrambleSequence().toByteArray(), device));
        } catch (Exception e) {
            Log.e(TAG, "Error decoding scramble", e);
            onFailure("Failed to read scramble from server");
            return;
        }

        TextView textView = findViewById(R.id.scramble_text_view);
        textView.setText(scramble);
        Button button = findViewById(R.id.scramble_check_button);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(
                    ScrambleActivity.this,
                    ScrambleCheckActivity.class);
            startActivity(intent);
        });
    }
}
