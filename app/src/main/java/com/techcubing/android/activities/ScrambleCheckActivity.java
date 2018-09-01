package com.techcubing.android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ScrambleCheckActivity extends AppCompatActivity {
    private static final String TAG = "TCScrambleCheck";
    public static final String EXTRA_SCRAMBLE_STATE = "com.techcubing.SCRAMBLE_STATE";

    private CameraView cameraView;
    private final Semaphore cameraCloseSemaphore = new Semaphore(1, true);

    private Puzzle puzzle;
    private Map<Character, Integer> colorsRead;
    private String[] expectedColors;
    private int facesRead;
    private TextView nextFaceInstructions;
    private Handler handler;

    private int distance(int colorA, int colorB) {
        int redDifference = Color.red(colorA) - Color.red(colorB);
        int greenDifference = Color.green(colorA) - Color.green(colorB);
        int blueDifference = Color.blue(colorA) - Color.blue(colorB);

        return redDifference * redDifference +
                greenDifference * greenDifference +
                blueDifference * blueDifference;

    }

    // For clustering pixels on the same sticker.
    private boolean colorsMatchStrict(int colorA, int colorB) {
        return distance(colorA, colorB) < 800;
    }

    // For clustering pixels on different stickers.
    private boolean colorsMatchLenient(int colorA, int colorB) {
        return distance(colorA, colorB) < 1600;
    }

    private String toString(int color) {
        if (color == -1) {
            return "n/a";
        } else {
            return String.format("#%06X", (0xFFFFFF & color));
        }
    }

    private String toString(int[] colors) {
        String[] colorHexCodes = new String[colors.length];

        for (int i = 0; i < colors.length; i++) {
            colorHexCodes[i] = "\"" + toString(colors[i]) + "\"";
        }
        return Arrays.toString(colorHexCodes);
    }

    private String toString(List<Integer> colors) {
        String[] colorHexCodes = new String[colors.size()];

        for (int i = 0; i < colors.size(); i++) {
            colorHexCodes[i] = "\"" + toString(colors.get(i)) + "\"";
        }
        return Arrays.toString(colorHexCodes);
    }

    private void checkFace(int[] colorsOnSide) {
        boolean success = true;
        int missedColors = 0;
        Map<Character, Integer> colorsReadWorking = new HashMap<>(colorsRead);
        Log.i(TAG, "checkFace:");
        Log.i(TAG, "actual = " + toString(colorsOnSide));
        String[] expectedColorsHex = new String[puzzle.stickersPerSide()];
        for (int i = 0; i < puzzle.stickersPerSide(); i++) {
            char expectedColorCode = expectedColors[facesRead].charAt(i);
            if (colorsReadWorking.containsKey(expectedColorCode)) {
                expectedColorsHex[i] =
                        String.format("\"#%06X\"", (0xFFFFFF & colorsReadWorking.get(expectedColorCode)));
            } else {
                expectedColorsHex[i] = "\"n/a\"";
            }
        }
        Log.i(TAG, "expected = " + Arrays.toString(expectedColorsHex));
        Log.i(TAG, "colorCodes = \"" + expectedColors[facesRead] + "\"");

        for (int stickerNumber = 0; stickerNumber < colorsOnSide.length; stickerNumber++) {
            char expectedColorCode = expectedColors[facesRead].charAt(stickerNumber);
            int colorRead = colorsOnSide[stickerNumber];
            if (colorRead == -1) {
                missedColors += 1;
                continue;
            }

            if (colorsReadWorking.containsKey(expectedColorCode)) {
                for (char colorCode : colorsReadWorking.keySet()) {
                    if (distance(colorsReadWorking.get(colorCode), colorRead) <
                            distance(colorsReadWorking.get(expectedColorCode), colorRead)) {
                        success = false;
                        Log.i(TAG, "Sticker " + stickerNumber + " closer to " + colorCode + " than " + expectedColorCode);
                        break;
                    }
                }
            } else {
                for (char colorCode : colorsReadWorking.keySet()) {
                    int expectedColor = colorsReadWorking.get(colorCode);
                    if (colorsMatchLenient(expectedColor, colorRead)) {
                        Log.i(TAG, "Sticker " + stickerNumber + " matched with color " + colorCode + ", distance " + distance(colorRead, expectedColor));
                        success = false;
                        break;
                    }
                }
            }
            if (!success) {
                break;
            }
            colorsReadWorking.put(expectedColorCode, colorRead);
        }
        // We allow one missed color per side (i.e. one color that we couldn't clearly tell what
        // it was) because logos don't have a single color.
        if (missedColors > 1) {
            success = false;
        }
        if (success) {
            colorsRead = colorsReadWorking;
            Log.i(TAG, colorsRead.toString());
            facesRead++;
            if (facesRead < puzzle.sides()) {
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
                    colors[sticker] = -1;
                    for (List<Integer> cluster : clusteredStickerColors) {
                        if (cluster.size() > pixelsToRead[sticker].length / 2) {
                            colors[sticker] = cluster.get(0);
                            break;
                        }
                    }
                    if (colors[sticker] == -1) {
                        Log.i(TAG, "Failed to cluster sticker " + sticker);
                        for (List<Integer> cluster : clusteredStickerColors) {
                            Log.i(TAG, "cluster " + toString(cluster.get(0)) + " size " + cluster.size());
                        }
                    }
                }

                checkFace(colors);
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scramble_check);

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
        colorsRead = new HashMap<>();
        expectedColors = getIntent().getStringExtra(EXTRA_SCRAMBLE_STATE).split("\\|");
        Log.i(TAG, Arrays.toString(expectedColors));
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
