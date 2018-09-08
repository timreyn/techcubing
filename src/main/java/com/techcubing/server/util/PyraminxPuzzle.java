package com.techcubing.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PyraminxPuzzle extends Puzzle {
  private static final int D_SIDE = 0;
  private static final int L_SIDE = 1;
  private static final int R_SIDE = 2;
  private static final int F_SIDE = 3;

  private static enum EdgeOfSide {
    LEFT, RIGHT, TOP
  };

  private EdgeOfSide nextEdge(EdgeOfSide edge) {
    switch (edge) {
      case LEFT:
        return EdgeOfSide.RIGHT;
      case RIGHT:
        return EdgeOfSide.TOP;
      case TOP:
        return EdgeOfSide.LEFT;
    }
    throw new RuntimeException("This is unreachable.");
  }

  private static class AdjacentSide {
    final int side;
    final EdgeOfSide edge;

    AdjacentSide(int side, EdgeOfSide edge) {
      this.side = side;
      this.edge = edge;
    }
  }

  // Each list is reading counter-clockwise around that side.
  private static List<List<AdjacentSide>> adjacentSides =
      Arrays.asList(
          // D_SIDE
          Arrays.asList(
              new AdjacentSide(F_SIDE, EdgeOfSide.TOP),
              new AdjacentSide(L_SIDE, EdgeOfSide.LEFT),
              new AdjacentSide(R_SIDE, EdgeOfSide.RIGHT)),
          // L_SIDE
          Arrays.asList(
              new AdjacentSide(D_SIDE, EdgeOfSide.TOP),
              new AdjacentSide(F_SIDE, EdgeOfSide.LEFT),
              new AdjacentSide(R_SIDE, EdgeOfSide.RIGHT)),
          // R_SIDE
          Arrays.asList(
              new AdjacentSide(L_SIDE, EdgeOfSide.TOP),
              new AdjacentSide(F_SIDE, EdgeOfSide.LEFT),
              new AdjacentSide(D_SIDE, EdgeOfSide.RIGHT)),
          // F_SIDE
          Arrays.asList(
              new AdjacentSide(R_SIDE, EdgeOfSide.TOP),
              new AdjacentSide(L_SIDE, EdgeOfSide.LEFT),
              new AdjacentSide(D_SIDE, EdgeOfSide.RIGHT)));

  PyraminxPuzzle() {}

  @Override
  int sides() {
    return 4;
  }

  @Override
  int stickersPerSide() {
    return 9;
  }

  // Sticker order:
  //  --TOP--
  //
  // 0---2---3
  //  -1---4-
  //   8---5
  // L  -7-  R
  //     6
  private int getTipStickerIndex(int side, EdgeOfSide oppositeEdge) {
    switch (oppositeEdge) {
      case TOP:
        return 6;
      case LEFT:
        return 3;
      case RIGHT:
        return 0;
      default:
        return -1;
    }
  }

  private int getCenterStickerIndex(int side, EdgeOfSide oppositeEdge) {
    return 1 + getTipStickerIndex(side, oppositeEdge);
  }

  private int getEdgeStickerIndex(int side, EdgeOfSide adjacentEdge) {
    switch (adjacentEdge) {
      case TOP:
        return 2;
      case RIGHT:
        return 5;
      case LEFT:
        return 8;
      default:
        return -1;
    }
  }

  private Transformation getTransformation(int oppositeSide, boolean isDeep) {
    int[][] tipStickers = new int[3][2];
    int[][] centerStickers = new int[3][2];
    int[][] firstEdgeStickers = new int[3][2];
    int[][] secondEdgeStickers = new int[3][2];

    for (int i = 0; i < 3; i++) {
      AdjacentSide side = adjacentSides.get(oppositeSide).get(i);
      tipStickers[i][0] = side.side;
      centerStickers[i][0] = side.side;
      firstEdgeStickers[i][0] = side.side;
      secondEdgeStickers[i][0] = side.side;
      for (AdjacentSide otherSide : adjacentSides.get(side.side)) {
        if (otherSide.side == oppositeSide) {
          tipStickers[i][1] = getTipStickerIndex(side.side, otherSide.edge);
          centerStickers[i][1] = getCenterStickerIndex(side.side, otherSide.edge);
          firstEdgeStickers[i][1] =
            getEdgeStickerIndex(side.side, nextEdge(otherSide.edge));
          secondEdgeStickers[i][1] =
            getEdgeStickerIndex(side.side, nextEdge(nextEdge(otherSide.edge)));
        }
      }
    }

    if (isDeep) {
      return new CompoundTransformation(
          new StickerListTransformation(tipStickers),
          new StickerListTransformation(centerStickers),
          new StickerListTransformation(firstEdgeStickers),
          new StickerListTransformation(secondEdgeStickers));
    } else {
      return new StickerListTransformation(tipStickers);
    }
  }

  @Override
  Transformation getTransformationForMove(String move) {
    int oppositeSide = -1;
    int moveTimes = 1;
    boolean isDeep = true;
    for (int i = 0 ; i < move.length(); i++) {
      char c = move.charAt(i);
      switch (c) {
        case 'l':
          isDeep = false;
        case 'L':
          oppositeSide = R_SIDE;
          break;
        case 'r':
          isDeep = false;
        case 'R':
          oppositeSide = L_SIDE;
          break;
        case 'u':
          isDeep = false;
        case 'U':
          oppositeSide = D_SIDE;
          break;
        case 'b':
          isDeep = false;
        case 'B':
          oppositeSide = F_SIDE;
          break;
        case '\'':
          moveTimes = 2;
          break;
      }
    }
    return new RepeatTransformation(getTransformation(oppositeSide, isDeep), moveTimes);
  }
}
