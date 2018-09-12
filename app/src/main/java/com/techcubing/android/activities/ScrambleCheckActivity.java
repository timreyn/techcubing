package com.techcubing.android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.android.util.Puzzle;
import com.techcubing.proto.ScorecardProto;
import com.techcubing.proto.wcif.WcifEvent;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ScrambleCheckActivity extends AppCompatActivity {
    private static final String TAG = "TCScrambleCheck";
    public static final String EXTRA_SCRAMBLE_STATE = "com.techcubing.SCRAMBLE_STATE";

    private CameraView cameraView;
    private final Semaphore cameraCloseSemaphore = new Semaphore(1, true);

    private Puzzle puzzle;
    private TextView nextFaceInstructions;
    private View guideView;
    private Handler handler;

    // For debugging.  Set this to true to inspect the images that are read by this activity.
    // You can then fetch the image with
    // $ adb pull /storage/emulated/0/Documents/techcubing/img
    private boolean saveImages() {
        return false;
    }

    // For clustering pixels on the same sticker.
    private boolean colorsMatchStrict(int colorA, int colorB) {
        return puzzle.distance(colorA, colorB) < 1600;
    }

    private void checkFace(int[] colorsOnSide) {
        if (puzzle.checkSide(colorsOnSide)) {
            if (puzzle.hasMoreSides()) {
                runOnUiThread(() -> {
                    nextFaceInstructions.setText(puzzle.nextSideInstruction());
                    nextFaceInstructions.setVisibility(View.VISIBLE);
                });
                handler.postDelayed(() -> {
                    runOnUiThread(() -> {
                        nextFaceInstructions.setVisibility(View.GONE);
                    });
                    acquireLockAndCaptureImage();
                }, 750);
            } else {
                runOnUiThread(() -> {
                    nextFaceInstructions.setText("Scramble checked!");
                    nextFaceInstructions.setVisibility(View.VISIBLE);
                });
                handler.postDelayed(() -> {
                    Intent intent = new Intent(ScrambleCheckActivity.this, ReleaseScorecardActivity.class);
                    intent.putExtra(
                            ReleaseScorecardActivity.EXTRA_OUTCOME,
                            ScorecardProto.AttemptPartOutcome.OK_VALUE);
                    startActivity(intent);
                }, 750);
            }
        } else {
            // TODO: After trying to check for 2-3 seconds, give the option to restart.
            acquireLockAndCaptureImage();
        }
    }

    private CameraKitEventCallback<CameraKitImage> imageCallback =
            image -> {
                cameraCloseSemaphore.release();
                Bitmap bitmap = image.getBitmap();

                int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
                bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

                int guideWidthPixels = guideView.getMeasuredWidth();
                int guideHeightPixels = guideView.getMeasuredWidth();
                int guideMargin =
                        ((LinearLayout.LayoutParams) guideView.getLayoutParams()).topMargin;

                int cameraHeight = cameraView.getMeasuredHeight();
                int cameraWidth = cameraView.getMeasuredWidth();

                // Transform the location and size of the guide square from preview space to image space.
                int imagePadding = (guideMargin * bitmap.getWidth()) / cameraWidth;
                int fullImageWidth = (guideWidthPixels * bitmap.getWidth()) / cameraWidth;
                int fullImageHeight = (guideHeightPixels * bitmap.getHeight()) / cameraHeight;
                int croppedImageWidth = fullImageWidth - 2 * imagePadding;
                int croppedImageHeight = fullImageHeight - 2 * imagePadding;

                int[][] pixelsGrid = new int[croppedImageHeight][croppedImageWidth];

                for (int row = 0; row < croppedImageHeight; row++) {
                    System.arraycopy(
                            pixels, bitmap.getWidth() * (row + imagePadding) + imagePadding,
                            pixelsGrid[row], 0, croppedImageWidth);
                }

                if (saveImages()) {
                    writeImageDataToDisk(pixelsGrid);
                }

                int[] colors = new int[puzzle.stickersPerSide()];
                int[][][] pixelsToRead = puzzle.pixelsToRead(croppedImageWidth);

                for (int sticker = 0; sticker < puzzle.stickersPerSide(); sticker++) {
                    // Cluster the colors on this sticker.
                    List<List<Integer>> clusteredStickerColors = new ArrayList<>();
                    for (int[] coords : pixelsToRead[sticker]) {
                        int nextColor = pixelsGrid[coords[1]][coords[0]];
                        boolean added = false;
                        for (List<Integer> cluster : clusteredStickerColors) {
                            if (colorsMatchStrict(nextColor, cluster.get(0))) {
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
                    colors[sticker] = Puzzle.UNIDENTIFIED_COLOR;
                    for (List<Integer> cluster : clusteredStickerColors) {
                        if (cluster.size() > pixelsToRead[sticker].length / 2) {
                            colors[sticker] = cluster.get(0);
                            break;
                        }
                    }
                }

                checkFace(colors);
            };

    private void writeImageDataToDisk(int[][] pixelsGrid) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "techcubing");
        directory.mkdirs();
        File file = new File(directory.getPath(), "img");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(Arrays.deepToString(pixelsGrid).getBytes());
            outputStream.close();
            Log.i(TAG, "Wrote image to " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Error saving image to disk", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scramble_check);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        WcifEvent event = ActiveState.getActive(ActiveState.EVENT, this);
        if (event == null) {
            throw new RuntimeException("No event");
        }
        puzzle = Puzzle.getPuzzleForEvent(event.getId());
        if (puzzle == null) {
            throw new RuntimeException("Invalid puzzle requested.");
        }

        cameraView = findViewById(R.id.scramble_check_camera);

        handler = new Handler();
        puzzle.setScrambleState(
                getIntent().getStringExtra(EXTRA_SCRAMBLE_STATE).split("\\|"));
        nextFaceInstructions = findViewById(R.id.scramble_check_next_face_instructions);

        LinearLayout diagramContainer = findViewById(R.id.scramble_check_diagram_container);

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, 1);

        View actualView = puzzle.getActualView(this);
        actualView.setLayoutParams(layoutParams);
        diagramContainer.addView(actualView);

        View expectedView = puzzle.getExpectedView(this);
        expectedView.setLayoutParams(layoutParams);
        diagramContainer.addView(expectedView);

        LinearLayout.LayoutParams guideLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 0);
        guideLayoutParams.setMargins(50, 50, 50, 50);
        guideView = puzzle.getGuideView(this);
        guideView.setLayoutParams(guideLayoutParams);

        LinearLayout guideContainer = findViewById(R.id.scramble_check_guide_container);
        guideContainer.addView(guideView);
        puzzle.displayGuideView();
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
