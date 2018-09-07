package com.techcubing.server.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.util.Arrays;

public class PuzzleTest {
  @Test
  public void test3x3EmptyScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("333");
    assertEquals(
        puzzle.scramble(""),
        new int[][]{
          new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1},  // D
          new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2},  // B
          new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3},  // U
          new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4},  // R
          new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5},  // F
          new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6}});  // L
  }

  @Test
  public void test3x3OneTurnScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("333");
    assertEquals(
        puzzle.scramble("R"),
        new int[][]{
          new int[]{1, 1, 2, 1, 1, 2, 1, 1, 2},  // D
          new int[]{2, 2, 3, 2, 2, 3, 2, 2, 3},  // B
          new int[]{3, 3, 5, 3, 3, 5, 3, 3, 5},  // U
          new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4},  // R
          new int[]{1, 1, 1, 5, 5, 5, 5, 5, 5},  // F
          new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6}});  // L
  }

  @Test
  public void test3x3FullScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("333");
    // Feliks Zemdegs' 4.22 scramble.
    assertEquals(
        puzzle.scramble("L U2 D R' F2 R2 L2 U R' F' D2 F2 D2 B U L2 B2"),
        new int[][]{
          new int[]{1, 6, 4, 1, 1, 1, 1, 5, 5},  // D
          new int[]{6, 6, 3, 3, 2, 2, 3, 2, 5},  // B
          new int[]{6, 6, 1, 3, 3, 4, 4, 3, 4},  // U
          new int[]{4, 3, 6, 2, 4, 5, 3, 5, 3},  // R
          new int[]{2, 4, 5, 6, 5, 1, 2, 2, 6},  // F
          new int[]{1, 1, 2, 4, 6, 4, 2, 5, 5}});  // L
  }

  // TODO: add more cube tests.

  @Test
  public void testPyraminxOneTurnScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("pyram");
    assertEquals(
        puzzle.scramble("R"),
        new int[][]{
          new int[]{1, 1, 3, 3, 3, 3, 1, 1, 1},  // D
          new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2},  // L
          new int[]{3, 3, 3, 3, 3, 4, 4, 4, 4},  // R
          new int[]{4, 4, 1, 1, 1, 1, 4, 4, 4}});  // F
  }

  @Test
  public void testPyraminxFullScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("pyram");
    // Benjamin Kyle's 1.28 scramble.
    assertEquals(
        puzzle.scramble("B' U' B' U' L R B L' U R' L' u' b'"),
        new int[][]{
          new int[]{2, 2, 3, 1, 1, 4, 2, 3, 3},  // D
          new int[]{4, 4, 1, 3, 1, 3, 4, 3, 2},  // L
          new int[]{2, 4, 4, 1, 2, 2, 3, 3, 1},  // R
          new int[]{3, 2, 4, 4, 4, 2, 1, 1, 1}});  // F
  }

  @Test
  public void testSkewbOneTurnScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("skewb");
    assertEquals(
        puzzle.scramble("R"),
        new int[][]{
          new int[]{1, 2, 2, 2, 2},  // D
          new int[]{4, 4, 4, 2, 4},  // B
          new int[]{3, 5, 3, 3, 3},  // U
          new int[]{1, 1, 1, 4, 1},  // R
          new int[]{5, 6, 5, 5, 5},  // F
          new int[]{6, 6, 6, 6, 3}});  // L
  }

  @Test
  public void testSkewbFullScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("skewb");
    // Jonatan Kłosko's 1.10 scramble.
    assertEquals(
        puzzle.scramble("L R B' R U' R' U L R B R"),
        new int[][]{
          new int[]{5, 2, 5, 4, 1},  // D
          new int[]{1, 2, 6, 6, 1},  // B
          new int[]{2, 6, 1, 5, 3},  // U
          new int[]{2, 4, 4, 4, 3},  // R
          new int[]{5, 4, 2, 6, 6},  // F
          new int[]{3, 1, 3, 3, 5}});  // L
  }
}
