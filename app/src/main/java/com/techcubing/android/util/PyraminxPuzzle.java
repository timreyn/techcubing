package com.techcubing.android.util;

import android.graphics.Color;
import android.graphics.Point;

class PyraminxPuzzle extends Puzzle {
    @Override
    public int stickersPerSide() {
        return 9;
    }

    @Override
    public int sides() {
        return 4;
    }

    @Override
    public String nextSideInstruction() {
        if (sidesChecked == 2) {
            return "Rotate around R";
        }
        return "Rotate around L'";
    }

    private class PointComputer {
        private int columnWidth;
        private int rowHeight;

        PointComputer(int imageWidth) {
            this.columnWidth = imageWidth / 6;
            this.rowHeight = (int) (columnWidth * Math.sqrt(3));
        }

        Point pt(int row, int col) {
            return new Point(col * columnWidth, row * rowHeight);
        }
    }

    @Override
    protected Point[] getBoundsForSticker(
            int stickerNum, int imageWidth, int leftOffset, int topOffset) {
        PointComputer cmp = new PointComputer(imageWidth);

        switch (stickerNum) {
            case 0:
                return new Point[]
                        {cmp.pt(3, 0), cmp.pt(2, 1), cmp.pt(3, 2)};
            case 1:
                return new Point[]
                        {cmp.pt(2, 1), cmp.pt(3, 2), cmp.pt(2, 3)};
            case 2:
                return new Point[]
                        {cmp.pt(3, 2), cmp.pt(2, 3), cmp.pt(3, 4)};
            case 3:
                return new Point[]
                        {cmp.pt(3, 4), cmp.pt(2, 5), cmp.pt(3, 6)};
            case 4:
                return new Point[]
                        {cmp.pt(2, 3), cmp.pt(3, 4), cmp.pt(2, 5)};
            case 5:
                return new Point[]
                        {cmp.pt(2, 3), cmp.pt(1, 4), cmp.pt(2, 5)};
            case 6:
                return new Point[]
                        {cmp.pt(1, 2), cmp.pt(0, 3), cmp.pt(1, 4)};
            case 7:
                return new Point[]
                        {cmp.pt(1, 2), cmp.pt(2, 3), cmp.pt(1, 4)};
            case 8:
                return new Point[]
                        {cmp.pt(2, 1), cmp.pt(1, 2), cmp.pt(2, 3)};
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
                return Color.RED;
            case 'C':
                return Color.BLUE;
            case 'D':
                return Color.GREEN;
            default:
                return Color.BLACK;
        }
    }
}
