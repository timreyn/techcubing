package com.techcubing.android.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.techcubing.proto.ScorecardProto;
import com.techcubing.proto.services.ReleaseDeviceProto.ReleaseDeviceRequest;
import com.techcubing.proto.services.ReleaseDeviceProto.ReleaseDeviceResponse;
import com.techcubing.proto.services.TechCubingServiceGrpc;

import javax.annotation.Nullable;


public class ReleaseDeviceActivity extends AppCompatActivity {
    private static final String TAG = "TCReleaseDevice";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        TechCubingServiceGrpc.TechCubingServiceFutureStub stub =
                Stubs.futureStub(this, getApplicationContext());
        @Nullable ScorecardProto.Scorecard scorecard =
                ActiveState.getActive(ActiveState.SCORECARD, this);
        if (scorecard != null) {
            onFailure("Please finish the current competitor.");;
            return;
        }

        ReleaseDeviceRequest.Builder requestBuilder = ReleaseDeviceRequest.newBuilder();

        requestBuilder.setContext(RequestContextBuilder.signRequest(requestBuilder, this));

        Futures.addCallback(
                stub.releaseDevice(requestBuilder.build()),
                new FutureCallback<ReleaseDeviceResponse>() {
                    @Override
                    public void onSuccess(@Nullable ReleaseDeviceResponse response) {
                        if (response == null) {
                            ReleaseDeviceActivity.this.onFailure(
                                    "Null response from server!");
                            return;
                        }
                        switch (response.getStatus()) {
                            case OK:
                                Log.i(TAG, "Successfully signed out.");
                                ActiveState.setActive(
                                        ActiveState.STAFF, null,
                                        ReleaseDeviceActivity.this);
                                ActiveState.clearCache(ReleaseDeviceActivity.this);
                                startActivity(new Intent(
                                        ReleaseDeviceActivity.this,
                                        LoggedOutActivity.class));
                                return;
                            default:
                                ReleaseDeviceActivity.this.onFailure(
                                        response.getStatus().toString());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        ReleaseDeviceActivity.this.onFailure(throwable.getMessage());
                    }
                }
        );

        ActiveState.setActive(ActiveState.SCORECARD, null, this);
        startActivity(new Intent(this, LoggedOutActivity.class));
    }

    private void onFailure(String failureReason) {
        Log.e(TAG, failureReason);
        runOnUiThread(() -> {
            setContentView(R.layout.generic_failure);
            TextView failureDescription = findViewById(R.id.failure_description);
            failureDescription.setText("Failed to sign out.");
            TextView failureReasonView = findViewById(R.id.failure_reason);
            failureReasonView.setText(failureReason);
            Button button = findViewById(R.id.failure_button);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(
                        ReleaseDeviceActivity.this, LobbyActivity.class);
                startActivity(intent);
            });
        });
    }
}
