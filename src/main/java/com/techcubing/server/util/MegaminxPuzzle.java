package com.techcubing.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MegaminxPuzzle extends Puzzle {
  // Rotations used in scramble checking:
  // f r' r' r' r' f f r' r' f f
  // Top corners are based on the order of rotation.
  private static final int D_SIDE = 0;  // Top corner: D/DFR/DFL
  private static final int DBR_SIDE = 1;  // Top corner: DBR/DFR/UFR
  private static final int DFR_SIDE = 2;  // Top corner: DFR/DFL/UF
  private static final int DFL_SIDE = 3;  // Top corner: DFL/DBL/UFL
  private static final int DBL_SIDE = 4;  // Top corner: DBL/DB/UBL
  private static final int DB_SIDE = 5;  // Top corner: DB/DBR/UBR
  private static final int UBL_SIDE = 6;  // Top corner: UBL/UBR/U
  private static final int UFL_SIDE = 7;  // Top corner: UFL/UF/U
  private static final int U_SIDE = 8;  // Top corner: U/UBR/UFR
  private static final int UBR_SIDE = 9;  // Top corner: UBR/DBR/DB
  private static final int UFR_SIDE = 10;  // Top corner: UFR/DFR/DBR
  private static final int UF_SIDE = 11;  // Top corner: UF/DFL/DFR
  
  private static final int TOP_LEFT_CORNER = 0;
  private static final int TOP_LEFT_EDGE = 1;
  private static final int TOP_CORNER = 2;
  private static final int TOP_RIGHT_EDGE = 3;
  private static final int TOP_RIGHT_CORNER = 4;
  private static final int LEFT_EDGE = 5;
  private static final int CENTER = 6;
  private static final int RIGHT_EDGE = 7;
  private static final int BOTTOM_LEFT_CORNER = 8;
  private static final int BOTTOM_EDGE = 9;
  private static final int BOTTOM_RIGHT_CORNER = 10;

  private static final int[] ALL_PIECES =
    new int[]{TOP_LEFT_CORNER, TOP_LEFT_EDGE, TOP_CORNER,
              TOP_RIGHT_EDGE, TOP_RIGHT_CORNER, LEFT_EDGE,
              CENTER, RIGHT_EDGE, BOTTOM_LEFT_CORNER,
              BOTTOM_EDGE, BOTTOM_RIGHT_CORNER};

  private static int rotateCounterClockwise(int sticker, int times) {
    if (times > 1) {
      return rotateCounterClockwise(rotateCounterClockwise(sticker, 1), times - 1);
    }
    if (times == 0) {
      return sticker;
    }
    switch (sticker) {
      case TOP_LEFT_CORNER:
        return BOTTOM_LEFT_CORNER;
      case TOP_LEFT_EDGE:
        return LEFT_EDGE;
      case TOP_CORNER:
        return TOP_LEFT_CORNER;
      case TOP_RIGHT_EDGE:
        return TOP_LEFT_EDGE;
      case TOP_RIGHT_CORNER:
        return TOP_CORNER;
      case LEFT_EDGE:
        return BOTTOM_EDGE;
      case CENTER:
        return CENTER;
      case RIGHT_EDGE:
        return TOP_RIGHT_EDGE;
      case BOTTOM_LEFT_CORNER:
        return BOTTOM_RIGHT_CORNER;
      case BOTTOM_EDGE:
        return RIGHT_EDGE;
      case BOTTOM_RIGHT_CORNER:
        return TOP_RIGHT_CORNER;
      default:
        return -1;
    }
  }

  // Represents (parts of) one face that gets turned onto another face.
  private static class TransformationPiece {
    final int startingFace;
    final int[] stickers;
    final int[][] facesAndRotations;

    TransformationPiece(
        int startingFace, int[] stickers, int[][] facesAndRotations) {
      this.startingFace = startingFace;
      this.stickers = stickers;
      this.facesAndRotations = facesAndRotations;
    }

    Transformation getTransformation() {
      List<Transformation> transformations = new ArrayList<>();
      for (int sticker : stickers) {
        int[][] stickerList = new int[facesAndRotations.length + 1][2];
        stickerList[0][0] = startingFace;
        stickerList[0][1] = sticker;
        for (int i = 0; i < facesAndRotations.length; i++) {
          stickerList[i + 1][0] = facesAndRotations[i][0];
          stickerList[i + 1][1] = rotateCounterClockwise(sticker, facesAndRotations[i][1]);
        }
        transformations.add(new StickerListTransformation(stickerList));
      }
      return new CompoundTransformation(transformations);
    }
  }

  private static Map<String, TransformationPiece[]> transformationPieces;
  private static Map<String, Integer> facesToRotate;

  static {
    transformationPieces = new HashMap<>();
    facesToRotate= new HashMap<>();

    transformationPieces.put(
        "U", new TransformationPiece[]{
          new TransformationPiece(
              UF_SIDE,
              new int[]{BOTTOM_LEFT_CORNER, BOTTOM_EDGE, BOTTOM_RIGHT_CORNER},
              new int[][]{
                new int[]{UFL_SIDE, 3},
                new int[]{UBL_SIDE, 2},
                new int[]{UBR_SIDE, 0},
                new int[]{UFR_SIDE, 0}})});
    facesToRotate.put("U", U_SIDE);

    transformationPieces.put(
        "R", new TransformationPiece[]{
          new TransformationPiece(
              UFR_SIDE,
              ALL_PIECES,
              new int[][]{
                new int[]{UBR_SIDE, 4},
                new int[]{DB_SIDE, 0},
                new int[]{D_SIDE, 3},
                new int[]{DFR_SIDE, 2}}),
          new TransformationPiece(
              U_SIDE,
              new int[]{TOP_LEFT_CORNER, TOP_LEFT_EDGE, TOP_CORNER,
                        TOP_RIGHT_EDGE, TOP_RIGHT_CORNER, LEFT_EDGE,
                        CENTER, RIGHT_EDGE},
              new int[][]{
                new int[]{UBL_SIDE, 1},
                new int[]{DBL_SIDE, 1},
                new int[]{DFL_SIDE, 2},
                new int[]{UF_SIDE, 1}})});
    facesToRotate.put("R", DBR_SIDE);

    transformationPieces.put(
        "D", new TransformationPiece[]{
          new TransformationPiece(
              DFR_SIDE,
              ALL_PIECES,
              new int[][]{
                new int[]{DBR_SIDE, 0},
                new int[]{DB_SIDE, 0},
                new int[]{DBL_SIDE, 0},
                new int[]{DFL_SIDE, 0}}),
          new TransformationPiece(
              UF_SIDE,
              new int[]{TOP_LEFT_CORNER, TOP_LEFT_EDGE, TOP_CORNER,
                        TOP_RIGHT_EDGE, TOP_RIGHT_CORNER, LEFT_EDGE,
                        CENTER, RIGHT_EDGE},
              new int[][]{
                new int[]{UFR_SIDE, 0},
                new int[]{UBR_SIDE, 0},
                new int[]{UBL_SIDE, 2},
                new int[]{UFL_SIDE, 3}})});
    facesToRotate.put("D", D_SIDE);
  }

  private Transformation getTransformationForFaceRotation(int face) {
    return new CompoundTransformation(
        new StickerListTransformation(
          new int[][]{
            new int[]{face, TOP_CORNER},
            new int[]{face, rotateCounterClockwise(TOP_CORNER, 4)},
            new int[]{face, rotateCounterClockwise(TOP_CORNER, 3)},
            new int[]{face, rotateCounterClockwise(TOP_CORNER, 2)},
            new int[]{face, rotateCounterClockwise(TOP_CORNER, 1)}}),
        new StickerListTransformation(
          new int[][]{
            new int[]{face, BOTTOM_EDGE},
            new int[]{face, rotateCounterClockwise(BOTTOM_EDGE, 4)},
            new int[]{face, rotateCounterClockwise(BOTTOM_EDGE, 3)},
            new int[]{face, rotateCounterClockwise(BOTTOM_EDGE, 2)},
            new int[]{face, rotateCounterClockwise(BOTTOM_EDGE, 1)}}));
  }

  @Override
  Transformation getTransformationForMove(String move) {
    String face = move.substring(0, 1);
    int moveTimes = 1;
    if (move.length() > 1) {
      switch (move.charAt(1)) {
        case '+':
          moveTimes = 2;
          break;
        case '\'':
          moveTimes = 4;
          break;
        case '-':
          moveTimes = 3;
          break;
      }
    }
    List<Transformation> transformations = new ArrayList<>();
    transformations.add(
        getTransformationForFaceRotation(facesToRotate.get(face)));
    for (TransformationPiece part : transformationPieces.get(face)) {
      transformations.add(part.getTransformation());
    }
    return new RepeatTransformation(
        new CompoundTransformation(transformations), moveTimes);
  }

  @Override
  int sides() {
    return 12;
  }

  @Override
  int stickersPerSide() {
    return 11;
  }
}
