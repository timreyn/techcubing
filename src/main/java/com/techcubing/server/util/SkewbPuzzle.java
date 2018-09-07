package com.techcubing.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SkewbPuzzle extends Puzzle {
  private static final int D_SIDE = 0;
  private static final int B_SIDE = 1;
  private static final int U_SIDE = 2;
  private static final int R_SIDE = 3;
  private static final int F_SIDE = 4;
  private static final int L_SIDE = 5;

  private static final int TOP_LEFT = 0;
  private static final int TOP_RIGHT = 1;
  private static final int CENTER = 2;
  private static final int BOTTOM_LEFT = 3;
  private static final int BOTTOM_RIGHT = 4;

  private static enum ReflectionDirection {
    LEFT_TO_RIGHT, TOP_TO_BOTTOM,
    TOP_RIGHT_TO_BOTTOM_LEFT, TOP_LEFT_TO_BOTTOM_RIGHT
  }

  private int oppositeCorner(int corner, ReflectionDirection reflection) {
    switch (reflection) {
      case LEFT_TO_RIGHT:
        switch (corner) {
          case TOP_LEFT:
            return TOP_RIGHT;
          case TOP_RIGHT:
            return TOP_LEFT;
          case BOTTOM_LEFT:
            return BOTTOM_RIGHT;
          case BOTTOM_RIGHT:
            return BOTTOM_LEFT;
        }
      case TOP_TO_BOTTOM:
        switch (corner) {
          case TOP_LEFT:
            return BOTTOM_LEFT;
          case TOP_RIGHT:
            return BOTTOM_RIGHT;
          case BOTTOM_LEFT:
            return TOP_LEFT;
          case BOTTOM_RIGHT:
            return TOP_RIGHT;
        }
      case TOP_RIGHT_TO_BOTTOM_LEFT:
        switch (corner) {
          case TOP_RIGHT:
            return BOTTOM_LEFT;
          case BOTTOM_LEFT:
            return TOP_RIGHT;
        }
        break;
      case TOP_LEFT_TO_BOTTOM_RIGHT:
        switch (corner) {
          case TOP_LEFT:
            return BOTTOM_RIGHT;
          case BOTTOM_RIGHT:
            return TOP_LEFT;
        }
        break;
    }
    return corner;
  }

  private static int nextCorner(int corner) {
    switch (corner) {
      case TOP_LEFT:
        return TOP_RIGHT;
      case TOP_RIGHT:
        return BOTTOM_RIGHT;
      case BOTTOM_RIGHT:
        return BOTTOM_LEFT;
      case BOTTOM_LEFT:
        return TOP_LEFT;
    }
    return -1;
  }

  private static int previousCorner(int corner) {
    switch (corner) {
      case TOP_LEFT:
        return BOTTOM_LEFT;
      case TOP_RIGHT:
        return TOP_LEFT;
      case BOTTOM_RIGHT:
        return TOP_RIGHT;
      case BOTTOM_LEFT:
        return BOTTOM_RIGHT;
    }
    return -1;
  }

  private static class SideAndCorner {
    final int side;
    final int corner;

    SideAndCorner(int side, int corner) {
      this.side = side;
      this.corner = corner;
    }
  }

  private static Map<Character, List<SideAndCorner>> turns;
  static {
    turns = new HashMap<>();
    turns.put('R', Arrays.asList(
          new SideAndCorner(R_SIDE, TOP_RIGHT),
          new SideAndCorner(B_SIDE, TOP_RIGHT),
          new SideAndCorner(D_SIDE, BOTTOM_RIGHT)));
    turns.put('L', Arrays.asList(
          new SideAndCorner(L_SIDE, TOP_RIGHT),
          new SideAndCorner(F_SIDE, BOTTOM_RIGHT),
          new SideAndCorner(D_SIDE, TOP_LEFT)));
    turns.put('U', Arrays.asList(
          new SideAndCorner(U_SIDE, TOP_LEFT),
          new SideAndCorner(L_SIDE, BOTTOM_LEFT),
          new SideAndCorner(B_SIDE, BOTTOM_LEFT)));
    turns.put('B', Arrays.asList(
          new SideAndCorner(B_SIDE, TOP_LEFT),
          new SideAndCorner(L_SIDE, BOTTOM_RIGHT),
          new SideAndCorner(D_SIDE, BOTTOM_LEFT)));
  }

  private static class OppositeSide {
    final int side;
    final ReflectionDirection reflection;

    OppositeSide(int side, ReflectionDirection reflection) {
      this.side = side;
      this.reflection = reflection;
    }
  }

  private static List<OppositeSide> oppositeSides =
    Arrays.asList(
        new OppositeSide(U_SIDE, ReflectionDirection.TOP_TO_BOTTOM),
        new OppositeSide(F_SIDE, ReflectionDirection.TOP_LEFT_TO_BOTTOM_RIGHT),
        new OppositeSide(D_SIDE, ReflectionDirection.TOP_TO_BOTTOM),
        new OppositeSide(L_SIDE, ReflectionDirection.TOP_TO_BOTTOM),
        new OppositeSide(B_SIDE, ReflectionDirection.TOP_LEFT_TO_BOTTOM_RIGHT),
        new OppositeSide(R_SIDE, ReflectionDirection.TOP_TO_BOTTOM));

  @Override
  int sides() {
    return 6;
  }

  @Override
  int stickersPerSide() {
    return 5;
  }

  Transformation getTransformationForListOfSides(List<SideAndCorner> sides) {
    int[][] stickersForFirstCorner = new int[3][];
    int[][] stickersForSecondCorner = new int[3][];
    int[][] stickersForThirdCorner = new int[3][];
    int[][] stickersForCenter = new int[3][];
    int[][] stickersForOppositeCorner = new int[3][];

    for (int i = 0; i < 3; i++) {
      SideAndCorner side = sides.get(i);
      stickersForFirstCorner[i] = new int[]{side.side, side.corner};
      stickersForSecondCorner[i] = new int[]{side.side, nextCorner(side.corner)};
      stickersForThirdCorner[i] = new int[]{side.side, previousCorner(side.corner)};
      stickersForCenter[i] = new int[]{side.side, CENTER};

      OppositeSide oppositeSide = oppositeSides.get(side.side);
      stickersForOppositeCorner[i] =
        new int[]{oppositeSide.side, oppositeCorner(side.corner, oppositeSide.reflection)};
    }

    return new CompoundTransformation(
        new StickerListTransformation(stickersForFirstCorner),
        new StickerListTransformation(stickersForSecondCorner),
        new StickerListTransformation(stickersForThirdCorner),
        new StickerListTransformation(stickersForCenter),
        new StickerListTransformation(stickersForOppositeCorner));
  }

  @Override
  Transformation getTransformationForMove(String move) {
    Transformation transformation =
      getTransformationForListOfSides(turns.get(move.charAt(0)));
    if (move.length() == 2 && move.charAt(1) == '\'') {
      return new CompoundTransformation(transformation, transformation);
    } else {
      return transformation;
    }
  }
}
