package com.techcubing.android.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Puzzle {
    protected String[] scrambleState;
    protected Map<Character, List<Integer>> identifiedColors;
    protected int sidesChecked;

    public static final int UNIDENTIFIED_COLOR = 0xffffff00;

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
                if (nearestDistance != null && nearestDistance < 1600) {
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

    public void setScrambleState(String[] colorsPerSide) {
        this.scrambleState = colorsPerSide;
    }

    // Diagrams that show the state of the scramble versus what's expected.
    public abstract View getExpectedView(Context context);
    public abstract View getActualView(Context context);

    // Update the expected and actual view with newly-read colors.
    protected abstract void displayColors(
            int[] expectedColors, int[] actualColors, Set<Integer> missedStickers,
            Set<Integer> unidentifiedStickers);

    // Get the color that should be shown the first time we see a particular color.
    protected abstract int getDefaultColor(char colorCode);
}
