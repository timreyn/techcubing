package com.techcubing.android.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.min;

public abstract class Puzzle {
    protected String[] scrambleState;
    protected Map<Character, List<Integer>> identifiedColors;
    protected int sidesChecked;
    protected PuzzleView expectedView;
    protected PuzzleView actualView;
    protected PuzzleView guideView;

    private static final String TAG = "TCPuzzle";

    public static final int UNIDENTIFIED_COLOR = 0xdddddd00;

    Puzzle() {
        sidesChecked = 0;
        identifiedColors = new HashMap<>();
    }

    // Set to true to display the pixels that we're reading in the guide.
    private boolean showPixelsRead() {
        return true;
    }

    // Get the color that should be shown the first time we see a particular color.
    protected abstract int getDefaultColor(char colorCode);

    // Get the bounds of a particular sticker.
    protected abstract Point[] getBoundsForSticker(
            int stickerNumber, int imageWidth, int leftOffset, int topOffset);

    public abstract int sides();
    public abstract int stickersPerSide();

    public boolean hasMoreSides() {
        return sidesChecked < sides();
    }

    public static Puzzle getPuzzleForEvent(String eventId) {
        switch (eventId) {
            case "333":
            case "333oh":
            case "333ft":
            case "333fm":
            case "333bf":
                return new CubePuzzle(3);
            case "444":
            case "444bf":
                return new CubePuzzle(4);
            case "555":
            case "555bf":
                return new CubePuzzle(5);
            case "222":
                return new CubePuzzle(2);
            case "666":
                return new CubePuzzle(6);
            case "777":
                return new CubePuzzle(7);
            case "skewb":
                return new SkewbPuzzle();
            default:
                return null;
        }
    }

    // Returns the string instructing the user how to rotate the puzzle for the next side.
    // TODO: consider switching this to a resource ID.
    public abstract String nextSideInstruction();

    public int distance(int colorA, int colorB) {
        int redDifference = Color.red(colorA) - Color.red(colorB);
        int greenDifference = Color.green(colorA) - Color.green(colorB);
        int blueDifference = Color.blue(colorA) - Color.blue(colorB);

        // This should be an improved distance metric over just using R^2 + G^2 + B^2, as it
        // correlates better with human perception and separates similar colors like orange and red.
        // Further reading: https://en.wikipedia.org/wiki/Color_difference
        return 2 * redDifference * redDifference +
                4 * greenDifference * greenDifference +
                3 * blueDifference * blueDifference;
    }

    public boolean checkSide(int[] colors) {
        Map<Character, List<Integer>> identifiedColorsWorking = new HashMap<>(identifiedColors);

        Set<Integer> missedStickers = new HashSet<>();
        Set<Integer> unidentifiedStickers = new HashSet<>();

        int[] expectedColors = new int[stickersPerSide()];

        for (int stickerNumber = 0; stickerNumber < colors.length; stickerNumber++) {
            char expectedColorCode = scrambleState[sidesChecked].charAt(stickerNumber);
            int colorRead = colors[stickerNumber];

            if (identifiedColors.containsKey(expectedColorCode)) {
                expectedColors[stickerNumber] = identifiedColors.get(expectedColorCode).get(0);
            } else {
                expectedColors[stickerNumber] = getDefaultColor(expectedColorCode);
            }

            if (colorRead == UNIDENTIFIED_COLOR) {
                unidentifiedStickers.add(stickerNumber);
                continue;
            }

            Integer nearestDistance = null;
            Character nearestColorCode = 0;
            for (char colorCode : identifiedColorsWorking.keySet()) {
                for (int color : identifiedColorsWorking.get(colorCode)) {
                    int distance = distance(color, colorRead);
                    if (nearestDistance == null || distance < nearestDistance) {
                        nearestColorCode = colorCode;
                        nearestDistance = distance;
                    }
                }
            }
            if (identifiedColorsWorking.containsKey(expectedColorCode)) {
                if (nearestColorCode != expectedColorCode) {
                    missedStickers.add(stickerNumber);
                } else {
                    identifiedColorsWorking.get(expectedColorCode).add(colorRead);
                }
            } else {
                if (nearestDistance != null && nearestDistance < 3200) {
                    missedStickers.add(stickerNumber);
                } else {
                    identifiedColorsWorking.put(expectedColorCode, new ArrayList<>());
                    identifiedColorsWorking.get(expectedColorCode).add(colorRead);
                }
            }
        }

        // We allow one unidentified color per side because logos don't have a single color.
        boolean success = missedStickers.isEmpty() && unidentifiedStickers.size() <= 1;

        if (success) {
            sidesChecked++;
            identifiedColors = identifiedColorsWorking;
        }
        displayColors(expectedColors, colors, missedStickers, unidentifiedStickers);

        return success;
    }


    // Returns a 3D array.
    // First dimension: the sticker number.  Must have length equal to stickersPerSide().
    // Second dimension: the pixel number.
    // Third dimension: 0 (x value) or 1 (y value).
    public int[][][] pixelsToRead(int imageWidth) {
        int[][][] out = new int[stickersPerSide()][][];
        for (int i = 0; i < stickersPerSide(); i++) {
            List<int[]> pixelsToRead = new ArrayList<>();
            Point[] boundaryPoints = getBoundsForSticker(i, imageWidth, 0, 0);
            for (int triangleNum = 1; triangleNum < boundaryPoints.length - 1; triangleNum++) {
                Point pointA = boundaryPoints[0];
                Point pointB = boundaryPoints[triangleNum];
                Point pointC = boundaryPoints[triangleNum + 1];

                // We select points of the form X_B * (B - A) + X_C * (C - A), where:
                // 0.2 <= X_B if (B-A) is an external edge; 0 otherwise
                // 0.8 >= X_B + X_C

                for (int factorB = 0; factorB <= 8; factorB++) {
                    if (triangleNum != 1 && factorB < 2) {
                        continue;
                    }
                    for (int factorC = 0; factorC <= 8 - factorB; factorC++) {
                        if (triangleNum != boundaryPoints.length - 2 && factorC < 2) {
                            continue;
                        }
                        pixelsToRead.add(new int[]{
                                pointA.x + (int) ((factorB / 10.0) * (pointB.x - pointA.x)) +
                                        (int) ((factorC / 10.0) * (pointC.x - pointA.x)),
                                pointA.y + (int) ((factorB / 10.0) * (pointB.y - pointA.y)) +
                                        (int) ((factorC / 10.0) * (pointC.y - pointA.y))});
                    }
                }
            }
            out[i] = new int[pixelsToRead.size()][];
            pixelsToRead.toArray(out[i]);
        }
        return out;
    }

    public void setScrambleState(String[] colorsPerSide) {
        this.scrambleState = colorsPerSide;
    }

    public View getExpectedView(Context context) {
        if (expectedView == null) {
            Paint edgePaint = new Paint();
            edgePaint.setStyle(Paint.Style.STROKE);
            edgePaint.setColor(Color.BLACK);
            edgePaint.setStrokeWidth(5);

            expectedView = new PuzzleView(context, edgePaint, CenterMode.CENTER);
        }
        return expectedView;
    }

    public View getActualView(Context context) {
        if (actualView == null) {
            Paint edgePaint = new Paint();
            edgePaint.setStyle(Paint.Style.STROKE);
            edgePaint.setColor(Color.BLACK);
            edgePaint.setStrokeWidth(5);

            actualView = new PuzzleView(context, edgePaint, CenterMode.CENTER);
        }
        return actualView;
    }

    public View getGuideView(Context context) {
        if (guideView == null) {
            Paint edgePaint = new Paint();
            edgePaint.setStyle(Paint.Style.STROKE);
            edgePaint.setColor(Color.RED);
            edgePaint.setStrokeWidth(10);
            guideView = new PuzzleView(context, edgePaint, CenterMode.ALIGN_TOP_LEFT);
        }
        return guideView;
    }

    // Update the expected and actual view with newly-read colors.
    protected void displayColors(
            int[] expectedColors, int[] actualColors, Set<Integer> missedStickers,
            Set<Integer> unidentifiedStickers) {
        Set<Integer> allMissedStickers = new HashSet<>(missedStickers);
        for (int sticker : unidentifiedStickers) {
            allMissedStickers.add(sticker);
        }
        expectedView.setColors(expectedColors, allMissedStickers);
        actualView.setColors(actualColors, allMissedStickers);
        displayGuideView();
    }

    public void displayGuideView() {
        int[] colors = new int[stickersPerSide()];
        Arrays.fill(colors, UNIDENTIFIED_COLOR);
        guideView.setColors(colors, new HashSet<>());
        if (showPixelsRead()) {
            guideView.showPixelsRead();
        }
    }

    enum CenterMode { CENTER, ALIGN_TOP_LEFT }

    // A view to display the state of a scrambled puzzle on the screen.
    private class PuzzleView extends View {
        private List<Path> paths;
        private List<Paint> paints;
        private List<Point[]> xsToDraw;
        private Paint edgePaint;
        private CenterMode centerMode;

        PuzzleView(Context context, Paint edgePaint, CenterMode centerMode) {
            super(context);
            this.edgePaint = edgePaint;
            this.centerMode = centerMode;
        }

        void showPixelsRead() {
            int[][][] pixels = pixelsToRead(getMeasuredWidth());
            for (int[][] pixelList : pixels) {
                for (int[] pixel : pixelList) {
                    xsToDraw.add(
                            new Point[]{
                                    new Point(pixel[0] + 2, pixel[1] + 2),
                                    new Point(pixel[0] - 2, pixel[1] - 2),
                                    new Point(pixel[0] + 2, pixel[1] - 2),
                                    new Point(pixel[0] - 2, pixel[1] + 2)});
                }
            }
        }

        void setColors(int[] colors, Set<Integer> missedStickers) {
            paths = new ArrayList<>();
            paints = new ArrayList<>();
            xsToDraw = new ArrayList<>();

            int height = getHeight();
            int width = getWidth();
            int imageWidth = 0;
            int topOffset = 0;
            int leftOffset = 0;

            switch (centerMode) {
                case CENTER:
                    imageWidth = (int) (0.8 * min(height, width));
                    topOffset = (height - imageWidth) / 2;
                    leftOffset = (width - imageWidth) / 2;
                    break;
                case ALIGN_TOP_LEFT:
                    imageWidth = min(height, width);
                    topOffset = 0;
                    leftOffset = 0;
                    break;
            }
            for (int i = 0; i < stickersPerSide(); i++) {
                Point[] points = getBoundsForSticker(i, imageWidth, leftOffset, topOffset);
                if (centerMode == CenterMode.ALIGN_TOP_LEFT) {
                    Log.i(TAG, Arrays.deepToString(points));
                }
                Path path = new Path();
                path.moveTo(points[points.length - 1].x, points[points.length - 1].y);
                for (Point point : points) {
                    path.lineTo(point.x, point.y);
                }
                paths.add(path);

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                if (colors[i] == Puzzle.UNIDENTIFIED_COLOR) {
                    paint.setAlpha(0);
                } else {
                    paint.setColor(colors[i]);
                }
                paints.add(paint);

                if (missedStickers.contains(i)) {
                    Point[] xPoints;
                    if (points.length == 3) {
                        xPoints = new Point[4];
                        xPoints[0] = points[0];
                        xPoints[1] = new Point(
                                (points[1].x + points[2].x) / 2,
                                (points[1].y + points[2].y) / 2);
                        xPoints[2] = new Point(
                                (points[0].x + points[2].x) / 2,
                                (points[0].y + points[2].y) / 2);
                        xPoints[3] = new Point(
                                (points[0].x + points[1].x) / 2,
                                (points[0].y + points[1].y) / 2);
                    } else {
                        xPoints = new Point[]{points[0], points[2], points[1], points[3]};
                    }
                    xsToDraw.add(xPoints);
                }
            }
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (paths == null || paints == null) {
                return;
            }
            for (int i = 0; i < stickersPerSide(); i++) {
                canvas.drawPath(paths.get(i), paints.get(i));
                canvas.drawPath(paths.get(i), edgePaint);
            }
            for (Point[] x : xsToDraw) {
                canvas.drawLine(x[0].x, x[0].y, x[1].x, x[1].y, edgePaint);
                canvas.drawLine(x[2].x, x[2].y, x[3].x, x[3].y, edgePaint);
            }
        }
    }

}
