package com.techcubing.android.util;

import android.graphics.Color;
import android.graphics.Point;

class CubePuzzle extends Puzzle {
    private static final String TAG = "TCCubePuzzle";
    private final int dimen;

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

    @Override
    protected Point[] getBoundsForSticker(
            int stickerNum, int imageWidth, int leftOffset, int topOffset) {
        int col = stickerNum % dimen;
        int row = stickerNum / dimen;

        int topBound = imageWidth / dimen * (dimen - row - 1) + topOffset;
        int leftBound = imageWidth / dimen * col + leftOffset;
        int rightBound = leftBound + imageWidth / dimen;
        int bottomBound = topBound + imageWidth / dimen;

        return new Point[]{
                new Point(leftBound, topBound),
                new Point(rightBound, topBound),
                new Point(rightBound, bottomBound),
                new Point(leftBound, bottomBound)};
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
}
