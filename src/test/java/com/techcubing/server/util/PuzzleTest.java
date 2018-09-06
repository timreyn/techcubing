package com.techcubing.server.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PuzzleTest {
  @Test
  public void test3x3EmptyScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("333");
    assertEquals(
        puzzle.scramble(""),
        new int[][]{
          new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1},
          new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2},
          new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3},
          new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4},
          new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5},
          new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6}});
  }
}
