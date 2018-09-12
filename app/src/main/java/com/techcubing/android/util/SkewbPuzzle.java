package com.techcubing.android.util;

import android.graphics.Color;
import android.graphics.Point;

class SkewbPuzzle extends Puzzle {
    @Override
    public int stickersPerSide() {
        return 5;
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
        int xLeft = leftOffset;
        int xCenter = leftOffset + imageWidth / 2;
        int xRight = leftOffset + imageWidth;
        int yTop = topOffset;
        int yCenter = topOffset + imageWidth / 2;
        int yBottom = topOffset + imageWidth;

        switch (stickerNum) {
            case 0:  // Top-left corner
                return new Point[]{
                        new Point(xLeft, yBottom),
                        new Point(xCenter, yBottom),
                        new Point(xLeft, yCenter)};
            case 1:  // Top-right corner
                return new Point[]{
                        new Point(xRight, yBottom),
                        new Point(xCenter, yBottom),
                        new Point(xRight, yCenter)};
            case 2:  // Center
                return new Point[]{
                        new Point(xRight, yCenter),
                        new Point(xCenter, yTop),
                        new Point(xLeft, yCenter),
                        new Point(xCenter, yBottom)};
            case 3:  // Bottom-left corner
                return new Point[]{
                        new Point(xLeft, yTop),
                        new Point(xCenter, yTop),
                        new Point(xLeft, yCenter)};
            case 4:  // Bottom-right corner
                return new Point[]{
                        new Point(xRight, yTop),
                        new Point(xCenter, yTop),
                        new Point(xRight, yCenter)};
            default:
                return new Point[]{};
        }
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
