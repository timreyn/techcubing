package com.techcubing.android.util;

import android.graphics.Color;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Puzzle {
    protected String[] scrambleState;
    protected Map<Character, Integer> identifiedColors;
    protected int sidesChecked;

    Puzzle() {
        sidesChecked = 0;
        identifiedColors = new HashMap<>();
    }

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
    public abstract String nextSideInstruction();

    public int distance(int colorA, int colorB) {
        int redDifference = Color.red(colorA) - Color.red(colorB);
        int greenDifference = Color.green(colorA) - Color.green(colorB);
        int blueDifference = Color.blue(colorA) - Color.blue(colorB);

        return redDifference * redDifference +
                greenDifference * greenDifference +
                blueDifference * blueDifference;
    }

    public boolean checkSide(int[] colors) {
        Map<Character, Integer> identifiedColorsWorking = new HashMap<>(identifiedColors);

        Set<Integer> missedStickers = new HashSet<>();
        Set<Integer> unidentifiedStickers = new HashSet<>();

        for (int stickerNumber = 0; stickerNumber < colors.length; stickerNumber++) {
            char expectedColorCode = scrambleState[sidesChecked].charAt(stickerNumber);
            int colorRead = colors[stickerNumber];
            if (colorRead == -1) {
                unidentifiedStickers.add(stickerNumber);
                continue;
            }

            if (identifiedColorsWorking.containsKey(expectedColorCode)) {
                for (char colorCode : identifiedColorsWorking.keySet()) {
                    if (distance(identifiedColorsWorking.get(colorCode), colorRead) <
                            distance(identifiedColorsWorking.get(expectedColorCode), colorRead)) {
                        missedStickers.add(stickerNumber);
                        break;
                    }
                }
            } else {
                for (char colorCode : identifiedColorsWorking.keySet()) {
                    int expectedColor = identifiedColorsWorking.get(colorCode);
                    if (distance(expectedColor, colorRead) < 1600) {
                        missedStickers.add(stickerNumber);
                        break;
                    }
                }
            }
            if (!missedStickers.contains(stickerNumber)) {
                identifiedColorsWorking.put(expectedColorCode, colorRead);
            }
        }

        // We allow one missed color per side (i.e. one color that we couldn't clearly tell what
        // it was) because logos don't have a single color.
        boolean success = missedStickers.isEmpty() && unidentifiedStickers.size() <= 1;

        if (success) {
            sidesChecked++;
            identifiedColors = identifiedColorsWorking;
        }

        return success;
    }

    public void setScrambleState(String[] colorsPerSide) {
        this.scrambleState = colorsPerSide;
    }
}
