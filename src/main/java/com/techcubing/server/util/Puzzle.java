package com.techcubing.server.util;

import java.util.ArrayList;
import java.util.List;

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

  protected static interface Transformation {
    void apply(int[][] state);
  }

  protected static class CompoundTransformation implements Transformation {
    private List<Transformation> transformations;

    public CompoundTransformation(Transformation... transformations) {
      this.transformations = new ArrayList<>();
      for (Transformation transformation : transformations) {
        this.transformations.add(transformation);
      }
    }

    public CompoundTransformation(List<Transformation> transformations) {
      this.transformations = transformations;
    }

    @Override
    public void apply(int[][] state) {
      for (Transformation transformation : transformations) {
        transformation.apply(state);
      }
    }
  }

  protected static class StickerListTransformation implements Transformation {
    private int[][] stickerList;

    public StickerListTransformation(int[][] stickerList) {
      this.stickerList = stickerList;
    }

    @Override
    public void apply(int[][] state) {
      int[] lastSticker = stickerList[stickerList.length - 1];
      int saved = state[lastSticker[0]][lastSticker[1]];
      for (int i = stickerList.length - 2; i >= 0; i--) {
        state[stickerList[i + 1][0]][stickerList[i + 1][1]] =
          state[stickerList[i][0]][stickerList[i][1]];
      }
      state[stickerList[0][0]][stickerList[0][1]] = saved;
    }
  }

  abstract int sides();
  abstract int stickersPerSide();

  abstract Transformation getTransformationForMove(String move);

  public int[][] scramble(String scrambleSequence) {
    // Get the starting state.
    int[][] state = new int[sides()][stickersPerSide()];
    for (int i = 0; i < sides(); i++) {
      for (int j = 0; j < stickersPerSide(); j++) {
        state[i][j] = i + 1;
      }
    }

    for (String s : scrambleSequence.split(" ")) {
      if (!s.isEmpty()) {
        getTransformationForMove(s).apply(state);
      }
    }
    return state;
  }
}
