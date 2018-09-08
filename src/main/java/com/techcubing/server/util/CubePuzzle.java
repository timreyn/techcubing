package com.techcubing.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CubePuzzle extends Puzzle {
  private int size;

  private static final int D_SIDE = 0;
  private static final int B_SIDE = 1;
  private static final int U_SIDE = 2;
  private static final int R_SIDE = 3;
  private static final int F_SIDE = 4;
  private static final int L_SIDE = 5;

  private static enum EdgeOfSide {
    TOP, RIGHT, BOTTOM, LEFT
  };

  private static class AdjacentSide {
    final int side;
    final EdgeOfSide edge;

    AdjacentSide(int side, EdgeOfSide edge) {
      this.side = side;
      this.edge = edge;
    }
  }

  private static List<List<AdjacentSide>> adjacentSides =
      Arrays.asList(
          // D_SIDE
          Arrays.asList(
              new AdjacentSide(F_SIDE, EdgeOfSide.RIGHT),
              new AdjacentSide(R_SIDE, EdgeOfSide.RIGHT),
              new AdjacentSide(B_SIDE, EdgeOfSide.TOP),
              new AdjacentSide(L_SIDE, EdgeOfSide.RIGHT)),
          // B_SIDE
          Arrays.asList(
              new AdjacentSide(D_SIDE, EdgeOfSide.BOTTOM),
              new AdjacentSide(R_SIDE, EdgeOfSide.TOP),
              new AdjacentSide(U_SIDE, EdgeOfSide.TOP),
              new AdjacentSide(L_SIDE, EdgeOfSide.BOTTOM)),
          // U_SIDE
          Arrays.asList(
              new AdjacentSide(F_SIDE, EdgeOfSide.LEFT),
              new AdjacentSide(L_SIDE, EdgeOfSide.LEFT),
              new AdjacentSide(B_SIDE, EdgeOfSide.BOTTOM),
              new AdjacentSide(R_SIDE, EdgeOfSide.LEFT)),
          // R_SIDE
          Arrays.asList(
              new AdjacentSide(U_SIDE, EdgeOfSide.RIGHT),
              new AdjacentSide(B_SIDE, EdgeOfSide.RIGHT),
              new AdjacentSide(D_SIDE, EdgeOfSide.RIGHT),
              new AdjacentSide(F_SIDE, EdgeOfSide.TOP)),
          // F_SIDE
          Arrays.asList(
              new AdjacentSide(U_SIDE, EdgeOfSide.BOTTOM),
              new AdjacentSide(R_SIDE, EdgeOfSide.BOTTOM),
              new AdjacentSide(D_SIDE, EdgeOfSide.TOP),
              new AdjacentSide(L_SIDE, EdgeOfSide.TOP)),
          // L_SIDE
          Arrays.asList(
              new AdjacentSide(U_SIDE, EdgeOfSide.LEFT),
              new AdjacentSide(F_SIDE, EdgeOfSide.BOTTOM),
              new AdjacentSide(D_SIDE, EdgeOfSide.LEFT),
              new AdjacentSide(B_SIDE, EdgeOfSide.LEFT)));

  CubePuzzle(int size) {
    this.size = size;
  }

  @Override
  int sides() {
    return 6;
  }

  @Override
  int stickersPerSide() {
    return size * size;
  }

  private Transformation getFaceRotation(int side) {
    List<Transformation> transformations = new ArrayList<>();
    for (int idx = 0; idx < (size + 1) / 2; idx++) {
      for (int depth = 0; depth < size / 2; depth++) {
        int[][] stickers = new int[][] {
            {side, getStickerIndex(EdgeOfSide.TOP, idx, depth)},
            {side, getStickerIndex(EdgeOfSide.RIGHT, idx, depth)},
            {side, getStickerIndex(EdgeOfSide.BOTTOM, idx, depth)},
            {side, getStickerIndex(EdgeOfSide.LEFT, idx, depth)},
        };
        transformations.add(new StickerListTransformation(stickers));
      }
    }
    return new CompoundTransformation(transformations);
  }

  private Transformation getSliceRotation(int side, int depth) {
    List<AdjacentSide> adjacentSideList = adjacentSides.get(side);
    List<Transformation> transformations = new ArrayList<>();
    for (int idx = 0; idx < size; idx++) {
      int[][] stickers = new int[4][size];
      for (int sideNum = 0; sideNum < 4; sideNum++) {
        AdjacentSide adjacentSide = adjacentSideList.get(sideNum);
        stickers[sideNum] = new int[] {
            adjacentSide.side, getStickerIndex(adjacentSide.edge, idx, depth)};
      }
      transformations.add(new StickerListTransformation(stickers));
    }
    return new CompoundTransformation(transformations);
  }

  private int getStickerIndex(EdgeOfSide edge, int idx, int depth) {
    switch (edge) {
      case TOP:
        return depth * size + idx;
      case RIGHT:
        return idx * size + (size - 1 - depth);
      case BOTTOM:
        return (size - 1 - depth) * size + (size - 1 - idx);
      case LEFT:
        return (size - 1 - idx) * size + depth;
      default:
        return -1;
    }
  }

  @Override
  Transformation getTransformationForMove(String move) {
    int side = -1;
    int depth = 1;
    int moveTimes = 1;
    for (int i = 0 ; i < move.length(); i++) {
      char c = move.charAt(i);
      switch (c) {
        case 'U':
          side = U_SIDE;
          break;
        case 'D':
          side = D_SIDE;
          break;
        case 'R':
          side = R_SIDE;
          break;
        case 'L':
          side = L_SIDE;
          break;
        case 'F':
          side = F_SIDE;
          break;
        case 'B':
          side = B_SIDE;
          break;
        case '\'':
          moveTimes = 3;
          break;
        case '2':
          // 2 can mean two different things, depending on where in the move
          // it is.
          if (i == 0) {
            depth = 2;
          } else {
            moveTimes = 2;
          }
          break;
        case '3':
          depth = 3;
          break;
        case 'w':
          // If we haven't already seen a depth, make this depth 2.
          if (depth == 1) {
            depth = 2;
          }
          break;
      }
    }
    List<Transformation> transformations = new ArrayList<>();
    transformations.add(getFaceRotation(side));
    for (int activeDepth = 0; activeDepth < depth; activeDepth++) {
      transformations.add(getSliceRotation(side, activeDepth));
    }
    Transformation transformation = new CompoundTransformation(transformations);
    
    return new RepeatTransformation(transformation, moveTimes);
  }
}
