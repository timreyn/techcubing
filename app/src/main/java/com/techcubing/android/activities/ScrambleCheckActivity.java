package com.techcubing.android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ScrambleCheckActivity extends AppCompatActivity {
    private static final String TAG = "TCScrambleCheck";
    public static final String EXTRA_SCRAMBLE_STATE = "com.techcubing.SCRAMBLE_STATE";

    private CameraView cameraView;
    private final Semaphore cameraCloseSemaphore = new Semaphore(1, true);

    private Puzzle puzzle;
    private TextView nextFaceInstructions;
    private ImageView guideView;
    private Handler handler;

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

                int guideSizePixels = guideView.getMeasuredHeight();
                int guidePadding = guideView.getPaddingTop();

                int cameraHeight = cameraView.getMeasuredHeight();

                // Transform the location and size of the guide square from preview space to image space.
                int imagePadding = (guidePadding * bitmap.getHeight()) / cameraHeight;
                int fullImageSize = (guideSizePixels * bitmap.getHeight()) / cameraHeight;
                int croppedImageSize = fullImageSize - 2 * imagePadding;

                int[][] pixelsGrid = new int[croppedImageSize][croppedImageSize];

                for (int row = 0; row < croppedImageSize; row++) {
                    System.arraycopy(pixels, bitmap.getWidth() * (row + imagePadding) + imagePadding,
                            pixelsGrid[row], 0, croppedImageSize);
                }

                int[] colors = new int[puzzle.stickersPerSide()];
                int[][][] pixelsToRead = puzzle.pixelsToRead(croppedImageSize);

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

        guideView = findViewById(R.id.scramble_check_guide);
        guideView.setImageDrawable(
                getResources().getDrawable(puzzle.getGuideDrawable(), getTheme()));
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
