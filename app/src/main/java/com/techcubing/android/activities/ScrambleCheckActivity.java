package com.techcubing.android.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.techcubing.android.R;
import com.techcubing.android.util.Puzzle;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ScrambleCheckActivity extends AppCompatActivity {
    private static final String TAG = "TCScrambleCheck";
    private CameraView cameraView;
    private final Semaphore cameraCloseSemaphore = new Semaphore(1, true);

    private Puzzle puzzle;
    private int facesRead;
    private int[][] colorsRead;
    private TextView nextFaceInstructions;
    private Handler handler;

    private boolean closeEnough(int colorA, int colorB) {
        int redDifference = Color.red(colorA) - Color.red(colorB);
        int greenDifference = Color.green(colorA) - Color.green(colorB);
        int blueDifference = Color.blue(colorA) - Color.blue(colorB);

        return redDifference * redDifference +
                greenDifference * greenDifference +
                blueDifference * blueDifference < 800;
    }

    private void callbackCompleted(int[] colorsOnSide) {
        boolean success = true;
        for (int color : colorsOnSide) {
            if (color == -1) {
                success = false;
            }
        }
        if (success) {
            colorsRead[facesRead] = colorsOnSide;
            facesRead++;
            if (facesRead < colorsRead.length) {
                runOnUiThread(() -> {
                    nextFaceInstructions.setText(
                            puzzle.nextSideInstruction(facesRead - 1));
                    nextFaceInstructions.setVisibility(View.VISIBLE);
                });
                handler.postDelayed(() -> {
                    runOnUiThread(() -> {
                        nextFaceInstructions.setVisibility(View.GONE);
                    });
                    acquireLockAndCaptureImage();
                }, 750);
            }
        } else {
            acquireLockAndCaptureImage();
        }
    }

    private CameraKitEventCallback<CameraKitImage> imageCallback =
            image -> {
                cameraCloseSemaphore.release();
                Bitmap bitmap = image.getBitmap();

                int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
                bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

                View guideView = findViewById(R.id.scramble_check_guide);
                int guideSizePixels = guideView.getMeasuredHeight();
                int guidePadding = guideView.getPaddingTop();

                int cameraHeight = cameraView.getMeasuredHeight();

                // Transform the location and size of the guide square from preview space to image space.
                int imagePadding = (guidePadding * bitmap.getHeight()) / cameraHeight;
                int fullImageSize = (guideSizePixels * bitmap.getHeight()) / cameraHeight;
                int croppedImageSize = fullImageSize - 2 * imagePadding;

                int[][] pixelsGrid = new int[croppedImageSize][croppedImageSize];

                for (int row = 0; row < croppedImageSize; row++) {
                    System.arraycopy(pixels, fullImageSize * (row + imagePadding) + imagePadding,
                            pixelsGrid[row], 0, croppedImageSize);
                }

                int[] colors = new int[puzzle.stickersPerSide()];
                int[][][] pixelsToRead = puzzle.pixelsToRead(croppedImageSize);

                for (int sticker = 0; sticker < puzzle.stickersPerSide(); sticker++) {
                    // Cluster the colors on this sticker.
                    List<List<Integer>> clusteredStickerColors = new ArrayList<>();
                    for (int[] coords : pixelsToRead[sticker]) {
                        int nextColor = pixelsGrid[coords[0]][coords[1]];
                        boolean added = false;
                        for (List<Integer> cluster : clusteredStickerColors) {
                            if (closeEnough(nextColor, cluster.get(0))) {
                                cluster.add(nextColor);
                                added = true;
                                break;
                            }
                        }
                        if (!added) {
                            List<Integer> newCluster = new ArrayList<>();
                            newCluster.add(nextColor);
                            clusteredStickerColors.add(newCluster);
                        }
                    }
                    // Check if any of the clusters have more than half of the pixels.  If so,
                    // we'll call that the color for this side.
                    colors[sticker] = -1;
                    for (List<Integer> cluster : clusteredStickerColors) {
                        if (cluster.size() > pixelsToRead[sticker].length / 2) {
                            colors[sticker] = cluster.get(0);
                            break;
                        }
                    }
                }

                for (int color : colors) {
                    if (color == -1) {
                        Log.i(TAG, "n/a");
                    } else {
                        Log.i(TAG, String.format("#%06X", (0xFFFFFF & color)));
                    }
                }
                callbackCompleted(colors);
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scramble_check);

        puzzle = Puzzle.getPuzzleForEvent("333");
        if (puzzle == null) {
            throw new RuntimeException("Invalid puzzle requested.");
        }

        cameraView = findViewById(R.id.scramble_check_camera);

        handler = new Handler();
        colorsRead = new int[puzzle.sides()][];
        facesRead = 0;
        nextFaceInstructions = findViewById(R.id.scramble_check_next_face_instructions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        handler.postDelayed(this::acquireLockAndCaptureImage, 100);
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
        } else {
            cameraCloseSemaphore.release();
        }
    }
}
