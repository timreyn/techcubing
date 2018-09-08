package com.techcubing.server.util;

import java.util.HashMap;
import java.util.Map;

class ClockPuzzle extends Puzzle {
  @Override
  int sides() {
    return 2;
  }

  @Override
  int stickersPerSide() {
    return 9;
  }

  // Clocks ("stickers")
  private static final int TOP_LEFT = 0;
  private static final int TOP = 1;
  private static final int TOP_RIGHT = 2;
  private static final int LEFT = 3;
  private static final int CENTER = 4;
  private static final int RIGHT = 5;
  private static final int BOTTOM_LEFT = 6;
  private static final int BOTTOM = 7;
  private static final int BOTTOM_RIGHT = 8;

  // Sides
  private static final int BACK = 0;
  private static final int FRONT = 1;

  // Returns the sticker on the opposite side which should be affected (i.e. the
  // attached corner.)
  private static Integer getBackSticker(int sticker) {
    switch (sticker) {
      case TOP_LEFT:
        return TOP_RIGHT;
      case TOP_RIGHT:
        return TOP_LEFT;
      case BOTTOM_LEFT:
        return BOTTOM_RIGHT;
      case BOTTOM_RIGHT:
        return BOTTOM_LEFT;
      default:
        return null;
    }
  }

  private static Map<String, int[]> affectedStickers;

  static {
    affectedStickers = new HashMap<>();
    affectedStickers.put(
        "U", new int[]{TOP_LEFT, TOP, TOP_RIGHT, LEFT, CENTER, RIGHT});
    affectedStickers.put(
        "R", new int[]{TOP, TOP_RIGHT, CENTER, RIGHT, BOTTOM, BOTTOM_RIGHT});
    affectedStickers.put(
        "D", new int[]{LEFT, CENTER, RIGHT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT});
    affectedStickers.put(
        "L", new int[]{TOP_LEFT, TOP, LEFT, CENTER, BOTTOM_LEFT, BOTTOM});

    affectedStickers.put("UR", new int[]{TOP, TOP_RIGHT, CENTER, RIGHT});
    affectedStickers.put("UL", new int[]{TOP_LEFT, TOP, LEFT, CENTER});
    affectedStickers.put("DR", new int[]{CENTER, RIGHT, BOTTOM, BOTTOM_RIGHT});
    affectedStickers.put("DL", new int[]{LEFT, CENTER, BOTTOM_LEFT, BOTTOM});

    affectedStickers.put(
        "ALL", new int[]{TOP_LEFT, TOP, TOP_RIGHT, LEFT, CENTER, RIGHT,
                         BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT});
  }

  static class ClockTransformation implements Transformation {
    int[] affectedStickers;
    int numMoves;

    public ClockTransformation(int[] affectedStickers, int numMoves) {
      this.affectedStickers = affectedStickers;
      this.numMoves = numMoves;
    }

    private int add(int value, int toAdd) {
      int sum = (value + toAdd) % 12;
      if (sum < 0) {
        sum += 12;
      }
      if (sum == 0) {
        return 12;
      } else {
        return sum;
      }
    }

    @Override
    public void apply(int[][] state) {
      for (int sticker : affectedStickers) {
        state[FRONT][sticker] = add(state[FRONT][sticker], numMoves);
        Integer oppositeSticker = getBackSticker(sticker);
        if (oppositeSticker != null) {
          state[BACK][oppositeSticker] =
            add(state[BACK][oppositeSticker], 0 - numMoves);
        }
      }
    }
  }

  protected static final Transformation Y2 = new Transformation() {
    @Override
    public void apply(int[][] state) {
      int[] saved = state[0];
      state[0] = state[1];
      state[1] = saved;
    }
  };

  @Override
  Transformation getTransformationForMove(String move) {
    if (move.equals("y2")) {
      return Y2;
    }
    // We don't do pin positions as part of scramble checking.
    if (affectedStickers.containsKey(move)) {
      return EMPTY_TRANSFORMATION;
    }
    String subMove = move.substring(0, move.length() - 2);
    int numTimes = Integer.parseInt(String.valueOf(move.charAt(move.length() - 2)));
    if (move.charAt(move.length() - 1) == '-') {
      numTimes *= -1;
    }
    return new ClockTransformation(affectedStickers.get(subMove), numTimes);
  }

  @Override
  protected int[][] getStartingState() {
    int[][] state = new int[2][9];
    for (int face = 0; face < 2; face++) {
      for (int sticker = 0; sticker < 9; sticker++) {
        state[face][sticker] = 12;
      }
    }
    return state;
  }
}

