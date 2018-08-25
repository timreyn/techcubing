package com.techcubing.android.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;
import java.util.List;
import java.util.concurrent.Semaphore;

public class BarcodeScannerActivity extends AppCompatActivity {
    private static final String TAG = "BarcodeScanner";
    private static CameraView cameraView = null;
    private final Semaphore cameraCloseSemaphore = new Semaphore(1, true);

    private CameraKitEventCallback<CameraKitImage> imageCallback =
            new CameraKitEventCallback<CameraKitImage>() {
      @Override
      public void callback(CameraKitImage image) {
                    // Create a bitmap
          FirebaseVisionImage firebaseImage =
                  FirebaseVisionImage.fromBitmap(image.getBitmap());

          FirebaseVisionBarcodeDetectorOptions options =
                  new FirebaseVisionBarcodeDetectorOptions.Builder().build();

          FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                  .getVisionBarcodeDetector(options);
          detector.detectInImage(firebaseImage)
                  .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                      @Override
                      public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                          cameraCloseSemaphore.release();
                          Log.i(TAG, "onSuccess" + firebaseVisionBarcodes.size());
                          if (firebaseVisionBarcodes.size() != 1) {
                              // We didn't find a barcode here.
                              cameraCloseSemaphore.acquireUninterruptibly();
                              if (cameraView.isStarted()) {
                                  cameraView.captureImage(imageCallback);
                              }
                              return;
                          }
                          String barcodeValue = firebaseVisionBarcodes.get(0).getRawValue();
                          Log.i(TAG, barcodeValue);
                      }
                  });
      }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_barcode_scanner);
        cameraView = (CameraView) findViewById(R.id.barcode_scanner_camera);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cameraView.captureImage(imageCallback);
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        cameraCloseSemaphore.acquireUninterruptibly();
        cameraView.stop();
        cameraCloseSemaphore.release();
        super.onPause();
    }

}