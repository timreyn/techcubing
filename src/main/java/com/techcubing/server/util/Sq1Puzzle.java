package com.techcubing.server.util;

import java.util.HashMap;
import java.util.Map;

class Sq1Puzzle extends Puzzle {
  @Override
  int sides() {
    return 6;
  }

  @Override
  int stickersPerSide() {
    // Variable stickers per side.  This is not used.
    // U and D: 12 stickers, going clockwise from the /
    // B R F L: 8 stickers.  0, 1, and 2 on U; 3 and 4 on E; 5, 6, and 7 on D.
    // A lot of these "stickers" are bandaged pieces.
    return -1;
  }

  private static final int D_SIDE = 0;
  private static final int B_SIDE = 1;
  private static final int R_SIDE = 2;
  private static final int F_SIDE = 3;
  private static final int L_SIDE = 4;
  private static final int U_SIDE = 5;

  private static final Transformation U = new CompoundTransformation(
      new StickerListTransformation(new int[][]{
        new int[]{U_SIDE, 0},
        new int[]{U_SIDE, 1},
        new int[]{U_SIDE, 2},
        new int[]{U_SIDE, 3},
        new int[]{U_SIDE, 4},
        new int[]{U_SIDE, 5},
        new int[]{U_SIDE, 6},
        new int[]{U_SIDE, 7},
        new int[]{U_SIDE, 8},
        new int[]{U_SIDE, 9},
        new int[]{U_SIDE, 10},
        new int[]{U_SIDE, 11}}),
      new StickerListTransformation(new int[][]{
        new int[]{F_SIDE, 2},
        new int[]{F_SIDE, 1},
        new int[]{F_SIDE, 0},
        new int[]{L_SIDE, 2},
        new int[]{L_SIDE, 1},
        new int[]{L_SIDE, 0},
        new int[]{B_SIDE, 2},
        new int[]{B_SIDE, 1},
        new int[]{B_SIDE, 0},
        new int[]{R_SIDE, 2},
        new int[]{R_SIDE, 1},
        new int[]{R_SIDE, 0}}));

  private static final Transformation D = new CompoundTransformation(
      new StickerListTransformation(new int[][]{
        new int[]{D_SIDE, 0},
        new int[]{D_SIDE, 1},
        new int[]{D_SIDE, 2},
        new int[]{D_SIDE, 3},
        new int[]{D_SIDE, 4},
        new int[]{D_SIDE, 5},
        new int[]{D_SIDE, 6},
        new int[]{D_SIDE, 7},
        new int[]{D_SIDE, 8},
        new int[]{D_SIDE, 9},
        new int[]{D_SIDE, 10},
        new int[]{D_SIDE, 11}}),
      new StickerListTransformation(new int[][]{
        new int[]{F_SIDE, 5},
        new int[]{F_SIDE, 6},
        new int[]{F_SIDE, 7},
        new int[]{R_SIDE, 5},
        new int[]{R_SIDE, 6},
        new int[]{R_SIDE, 7},
        new int[]{B_SIDE, 5},
        new int[]{B_SIDE, 6},
        new int[]{B_SIDE, 7},
        new int[]{L_SIDE, 5},
        new int[]{L_SIDE, 6},
        new int[]{L_SIDE, 7}}));

  private static final Transformation SLASH = new CompoundTransformation(
      // U and D swaps.
      new StickerListTransformation(
        new int[][]{new int[]{U_SIDE, 0}, new int[]{D_SIDE, 0}}),
      new StickerListTransformation(
        new int[][]{new int[]{U_SIDE, 1}, new int[]{D_SIDE, 1}}),
      new StickerListTransformation(
        new int[][]{new int[]{U_SIDE, 2}, new int[]{D_SIDE, 2}}),
      new StickerListTransformation(
        new int[][]{new int[]{U_SIDE, 3}, new int[]{D_SIDE, 3}}),
      new StickerListTransformation(
        new int[][]{new int[]{U_SIDE, 4}, new int[]{D_SIDE, 4}}),
      new StickerListTransformation(
        new int[][]{new int[]{U_SIDE, 5}, new int[]{D_SIDE, 5}}),
      // E swap.
      new StickerListTransformation(
        new int[][]{new int[]{F_SIDE, 4}, new int[]{B_SIDE, 3}}),
      // Swaps along the edge.
      new StickerListTransformation(
        new int[][]{new int[]{F_SIDE, 1}, new int[]{B_SIDE, 5}}),
      new StickerListTransformation(
        new int[][]{new int[]{F_SIDE, 2}, new int[]{R_SIDE, 7}}),
      new StickerListTransformation(
        new int[][]{new int[]{R_SIDE, 0}, new int[]{R_SIDE, 6}}),
      new StickerListTransformation(
        new int[][]{new int[]{R_SIDE, 1}, new int[]{R_SIDE, 5}}),
      new StickerListTransformation(
        new int[][]{new int[]{R_SIDE, 2}, new int[]{F_SIDE, 7}}),
      new StickerListTransformation(
        new int[][]{new int[]{B_SIDE, 0}, new int[]{F_SIDE, 6}}));

  @Override
  Transformation getTransformationForMove(String move) {
    if (move.equals("/")) {
      return SLASH;
    }
    String[] moveSplit = move.substring(1, move.length() - 1).split(",");
    int u = Integer.parseInt(moveSplit[0]);
    int d = Integer.parseInt(moveSplit[1]);
    if (u < 0) {
      u += 12;
    }
    if (d < 0) {
      d += 12;
    }
    return new CompoundTransformation(
        new RepeatTransformation(U, u),
        new RepeatTransformation(D, d));
  }

  @Override
  protected int[][] getStartingState() {
    int[][] state = new int[6][];
    for (int face = 0; face < 6; face++) {
      int stickersOnFace = 8;
      if (face == U_SIDE || face == D_SIDE) {
        stickersOnFace = 12;
      }
      state[face] = new int[stickersOnFace];
      for (int sticker = 0; sticker < stickersOnFace; sticker++) {
        state[face][sticker] = face + 1;
      }
    }
    return state;
  }
}

