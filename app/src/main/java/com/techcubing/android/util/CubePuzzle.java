package com.techcubing.android.util;

class CubePuzzle extends Puzzle {
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
    public String nextSideInstruction(int sideJustRead) {
        if (sideJustRead == 2) {
            return "Do an x rotation";
        }
        return "Do a z rotation";
    }

    @Override
    public int[][][] pixelsToRead(int imageDimen) {
        // We check a 5x5 square of pixels within each sticker.
        final int pixelsPerSquare = 5;

        // We crop each sticker by 25% on each side to make sure we're reading the middle of the
        // sticker.
        final double cropAmount = 0.25;

        int squareDimen = imageDimen / dimen;
        int[][][] pixelsToRead = new int[dimen * dimen][pixelsPerSquare * pixelsPerSquare][2];
        for (int row = 0; row < dimen; row++) {
            for (int col = 0; col < dimen; col++) {
                int leftBound = squareDimen * col;
                int topBound = squareDimen * row;

                for (int i = 0; i < pixelsPerSquare; i++) {
                    for (int j = 0; j < pixelsPerSquare; j++) {
                        pixelsToRead[row * dimen + col][i * pixelsPerSquare + j][0] =
                                leftBound +
                                (int) (squareDimen * (cropAmount + ((1 - 2 * cropAmount) * i) /
                                        (pixelsPerSquare - 1)));
                        pixelsToRead[row * dimen + col][i * pixelsPerSquare + j][1] =
                                topBound +
                                (int) (squareDimen * (cropAmount + ((1 - 2 * cropAmount) * j) /
                                        (pixelsPerSquare - 1)));
                    }
                }
            }
        }
        return pixelsToRead;
    }
}
