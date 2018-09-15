package com.techcubing.android.activities;

import android.content.Intent;
import android.net.Uri;
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
import com.techcubing.proto.services.AcquireDeviceProto.AcquireDeviceRequest;
import com.techcubing.proto.services.AcquireDeviceProto.AcquireDeviceResponse;
import com.techcubing.proto.services.TechCubingServiceGrpc.TechCubingServiceFutureStub;

import javax.annotation.Nullable;


public class AcquireDeviceActivity extends AppCompatActivity {
    private static final String TAG = "TCAcquireDevice";
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.toUri(0));
        String code = uri.getPathSegments().get(1);

        TechCubingServiceFutureStub stub =
                Stubs.futureStub(this, getApplicationContext());

        AcquireDeviceRequest.Builder requestBuilder = AcquireDeviceRequest.newBuilder()
                .setAuthorizationCode(code);

        requestBuilder.setContext(RequestContextBuilder.signRequest(requestBuilder, this));

        Futures.addCallback(
                stub.acquireDevice(requestBuilder.build()),
                new FutureCallback<AcquireDeviceResponse>() {
                    @Override
                    public void onSuccess(@Nullable AcquireDeviceResponse response) {
                        if (response == null) {
                            AcquireDeviceActivity.this.onFailure(
                                    "Null response from acquireDevice");
                            return;
                        }
                        switch (response.getStatus()) {
                            case OK:
                                Log.i(TAG, "Logged in as " + response.getPerson().getName());
                                ActiveState.setActive(
                                        ActiveState.STAFF,
                                        response.getPerson(),
                                        AcquireDeviceActivity.this);
                                startActivity(new Intent(
                                        AcquireDeviceActivity.this,
                                        LobbyActivity.class));
                                return;
                            default:
                                AcquireDeviceActivity.this.onFailure(response.getStatus().name());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        AcquireDeviceActivity.this.onFailure(throwable.getMessage());
                    }
                }
        );
    }

    private void onFailure(String failureReason) {
        Log.e(TAG, failureReason);
        runOnUiThread(() -> {
            setContentView(R.layout.generic_failure);
            TextView failureDescription = findViewById(R.id.failure_description);
            failureDescription.setText("Failed to acquire device.");
            TextView failureReasonView = findViewById(R.id.failure_reason);
            failureReasonView.setText(failureReason);
            Button button = findViewById(R.id.failure_button);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(
                        AcquireDeviceActivity.this, LoggedOutActivity.class);
                startActivity(intent);
            });
        });
    }
}
