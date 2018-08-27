package com.techcubing.android.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.techcubing.android.util.Constants;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.util.concurrent.Semaphore;
import com.techcubing.android.R;

public class BarcodeScannerActivity extends AppCompatActivity {
    private static final String TAG = "TCBarcodeScanner";
    // Extras to be added to intents to BarcodeScannerActivity.
    public static final String EXTRA_EXPECTED_HOST = "com.techcubing.EXPECTED_HOST";

    private Intent intent = null;

    private static CameraView cameraView = null;
    private final Semaphore cameraCloseSemaphore = new Semaphore(1, true);

    private CameraKitEventCallback<CameraKitImage> imageCallback =
            image -> {
                          // Create a bitmap
                FirebaseVisionImage firebaseImage =
                        FirebaseVisionImage.fromBitmap(image.getBitmap());

                FirebaseVisionBarcodeDetectorOptions options =
                        new FirebaseVisionBarcodeDetectorOptions.Builder().build();

                FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                        .getVisionBarcodeDetector(options);
                detector.detectInImage(firebaseImage)
                        .addOnSuccessListener(firebaseVisionBarcodes -> {
                            cameraCloseSemaphore.release();
                            if (firebaseVisionBarcodes.size() != 1) {
                                // We didn't find a barcode here.
                                acquireLockAndCaptureImage();
                                return;
                            }
                            Uri uri = Uri.parse(firebaseVisionBarcodes.get(0).getRawValue());
                            Log.i(TAG, uri.toString());
                            String expectedHost = intent.getStringExtra(
                                    EXTRA_EXPECTED_HOST);
                            if (uri.getHost() != null &&
                                    uri.getScheme().equals("techcubing") &&
                                    uri.getHost().equals(expectedHost)) {
                                try {
                                    Intent newIntent = new Intent(Constants.ACTION_SCAN_BARCODE, uri);
                                    startActivity(newIntent);
                                    return;
                                } catch (ActivityNotFoundException e) {
                                    Log.e(TAG, "Unrecognized URI " + uri.toString());
                                }
                            }
                            acquireLockAndCaptureImage();
                        });
            };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_barcode_scanner);
        cameraView = (CameraView) findViewById(R.id.barcode_scanner_camera);
        intent = getIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        new Handler().postDelayed(() -> acquireLockAndCaptureImage(), 100);
    }

    @Override
    protected void onPause() {
        cameraCloseSemaphore.acquireUninterruptibly();
        cameraView.stop();
        cameraCloseSemaphore.release();
        super.onPause();
    }

    private void acquireLockAndCaptureImage() {
        cameraCloseSemaphore.acquireUninterruptibly();
        if (cameraView.isStarted()) {
            cameraView.captureImage(imageCallback);
        }
    }
}