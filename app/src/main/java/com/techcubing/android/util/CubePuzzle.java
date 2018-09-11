package com.techcubing.android.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.min;

class CubePuzzle extends Puzzle {
    private static final String TAG = "TCCubePuzzle";
    private final int dimen;
    private CubePuzzleView expectedView;
    private CubePuzzleView actualView;

    CubePuzzle(int dimen) {
        this.dimen = dimen;
    }

    @Override
    public int stickersPerSide() {
        return dimen * dimen;
    }

    @Override
    public int sides() {
        return 6;
    }

    @Override
    public String nextSideInstruction() {
        if (sidesChecked == 3) {
            return "Do a z rotation";
        }
        return "Do an x rotation";
    }

    private Rect getBounds(int stickerNum, int imageDimen, int topOffset, int leftOffset) {
        int col = stickerNum / dimen;
        int row = stickerNum % dimen;

        int topBound = imageDimen / dimen * (dimen - 1 - col) + topOffset;
        int leftBound = imageDimen / dimen * row + leftOffset;
        int rightBound = leftBound + imageDimen / dimen;
        int bottomBound = topBound + imageDimen / dimen;
        return new Rect(leftBound, topBound, rightBound, bottomBound);
    }

    @Override
    public int[][][] pixelsToRead(int imageDimen) {
        // We check a 5x5 square of pixels within each sticker.
        final int pixelsPerSquare = 5;

        // We crop each sticker by 25% on each side to make sure we're reading the middle of the
        // sticker.
        final double cropAmount = 0.25;
        final int cropPixels = (int) ((imageDimen / dimen) * cropAmount);

        int[][][] pixelsToRead = new int[dimen * dimen][pixelsPerSquare * pixelsPerSquare][2];
        for (int stickerNum = 0; stickerNum < stickersPerSide(); stickerNum++) {
            Rect bounds = getBounds(stickerNum, imageDimen, 0, 0);
            int leftBound = bounds.left + cropPixels;
            int topBound = bounds.top + cropPixels;
            int rightBound = bounds.right - cropPixels;
            int bottomBound = bounds.bottom - cropPixels;
            for (int i = 0; i < pixelsPerSquare; i++) {
                for (int j = 0; j < pixelsPerSquare; j++) {
                    pixelsToRead[stickerNum][i * pixelsPerSquare + j][0] =
                            (topBound * i + bottomBound * (pixelsPerSquare - i - 1)) / (pixelsPerSquare - 1);
                    pixelsToRead[stickerNum][i * pixelsPerSquare + j][1] =
                            (leftBound * j + rightBound * (pixelsPerSquare - j - 1)) / (pixelsPerSquare - 1);
                }
            }
        }

        return pixelsToRead;
    }

    @Override
    public View getExpectedView(Context context) {
        if (expectedView == null) {
            expectedView = new CubePuzzleView(context);
        }
        return expectedView;
    }

    @Override
    public View getActualView(Context context) {
        if (actualView == null) {
            actualView = new CubePuzzleView(context);
        }
        return actualView;
    }

    @Override
    protected int getDefaultColor(char colorCode) {
        switch (colorCode) {
            case 'A':
                return Color.YELLOW;
            case 'B':
                return Color.BLUE;
            case 'C':
                return Color.WHITE;
            case 'D':
                return Color.RED;
            case 'E':
                return Color.GREEN;
            case 'F':
                return 0xffffa500;  // Orange.
            default:
                return Color.BLACK;
        }
    }

    @Override
    protected void displayColors(
            int[] expectedColors, int[] actualColors, Set<Integer> missedStickers,
            Set<Integer> unidentifiedStickers) {
        Set<Integer> allMissedStickers = new HashSet<>(missedStickers);
        for (int sticker : unidentifiedStickers) {
            allMissedStickers.add(sticker);
        }
        expectedView.setColors(expectedColors, allMissedStickers);
        actualView.setColors(actualColors, allMissedStickers);
    }

    private class CubePuzzleView extends View {
        private List<Rect> rectangles;
        private List<Paint> paints;
        private Paint edgePaint;
        private Set<Integer> missedStickers;

        CubePuzzleView(Context context) {
            super(context);
            edgePaint = new Paint();
            edgePaint.setStyle(Paint.Style.STROKE);
            edgePaint.setColor(Color.BLACK);
            edgePaint.setStrokeWidth(5);
        }

        void setColors(int[] colors, Set<Integer> missedStickers) {
            rectangles = new ArrayList<>();
            paints = new ArrayList<>();
            this.missedStickers = missedStickers;
            int height = getMeasuredHeight();
            int width = getMeasuredWidth();

            int squareSize = (int) (0.8 * min(height, width));
            int topOffset = (height - squareSize) / 2;
            int leftOffset = (width - squareSize) / 2;
            for (int i = 0; i < stickersPerSide(); i++) {
                rectangles.add(getBounds(i, squareSize, topOffset, leftOffset));
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                if (colors[i] == Puzzle.UNIDENTIFIED_COLOR) {
                    paint.setAlpha(0);
                } else {
                    paint.setColor(colors[i]);
                }
                paints.add(paint);
            }
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (rectangles == null) {
                return;
            }
            for (int i = 0; i < stickersPerSide(); i++) {
                Rect rectangle = rectangles.get(i);
                Paint paint = paints.get(i);
                canvas.drawRect(rectangle, paint);
                canvas.drawRect(rectangle, edgePaint);
            }
            for (int missedSticker : missedStickers) {
                Rect missed = rectangles.get(missedSticker);
                canvas.drawLine(missed.left, missed.top, missed.right, missed.bottom, edgePaint);
                canvas.drawLine(missed.right, missed.top, missed.left, missed.bottom, edgePaint);
            }
        }
    }
}
