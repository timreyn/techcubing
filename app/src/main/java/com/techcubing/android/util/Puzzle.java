package com.techcubing.android.util;

public abstract class Puzzle {
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
            default:
                return null;
        }
    }

    public abstract int sides();
    public abstract int stickersPerSide();

    // Returns a 3D array.
    // First dimension: the sticker number.  Must have length equal to stickersPerSide().
    // Second dimension: the pixel number.
    // Third dimension: 0 (x value) or 1 (y value).
    public abstract int[][][] pixelsToRead(int imageDimen);

    // Returns the string instructing the user how to rotate the puzzle for the next side.
    // TODO: consider switching this to a resource ID.
    public abstract String nextSideInstruction(int sideJustRead);
}
