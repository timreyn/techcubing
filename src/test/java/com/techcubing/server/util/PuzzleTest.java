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

  @Test
  public void testMegaminxOneTurnScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("minx");
    assertEquals(
        puzzle.scramble("R++"),
        new int[][]{
          new int[]{10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},  // D
          new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},  // DBR
          new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6},  // DFR
          new int[]{7, 7, 4, 4, 4, 7, 7, 7, 7, 7, 7},  // DFL
          new int[]{9, 9, 9, 9, 5, 9, 9, 5, 9, 9, 5},  // DBL
          new int[]{11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11},  // DB
          new int[]{12, 12, 12, 12, 7, 12, 12, 7, 12, 12, 7},  // UBL
          new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},  // UFL
          new int[]{4, 4, 4, 4, 4, 4, 4, 4, 9, 9, 9},  // U
          new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},  // UBR
          new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},  // UR
          new int[]{5, 5, 5, 5, 12, 5, 5, 12, 5, 5, 12}});  // UF
  }

  @Test
  public void testMegaminxFullScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("minx");
    // The first scramble from Juan Pablo Huanqui's 32.33 average.
    // Because I took the time to find the right set of scrambles for his WR
    // single, but did the wrong scramble.
    assertEquals(
        puzzle.scramble(
          "R-- D++ R-- D-- R++ D++ R++ D++ R++ D-- U' " +
          "R++ D++ R++ D++ R-- D++ R-- D++ R++ D++ U " +
          "R-- D-- R++ D-- R-- D-- R++ D-- R++ D++ U " +
          "R++ D-- R-- D++ R-- D++ R-- D-- R-- D-- U' " +
          "R-- D++ R++ D-- R-- D++ R++ D++ R-- D-- U' " +
          "R-- D-- R++ D++ R++ D-- R-- D-- R-- D++ U " +
          "R++ D-- R-- D++ R++ D-- R++ D-- R++ D++ U"),
        new int[][]{
          new int[]{5, 7, 5, 8, 6, 11, 12, 7, 12, 8, 7},  // D
          new int[]{10, 3, 7, 11, 12, 5, 8, 4, 5, 9, 7},  // DBR
          new int[]{6, 11, 1, 2, 3, 4, 4, 1, 2, 1, 9},  // DFR
          new int[]{1, 3, 11, 10, 12, 9, 3, 3, 1, 2, 2},  // DFL
          new int[]{3, 5, 2, 7, 12, 10, 11, 12, 4, 11, 2},  // DBL
          new int[]{6, 8, 10, 2, 1, 7, 9, 8, 11, 6, 1},  // DB
          new int[]{3, 4, 5, 6, 10, 5, 10, 6, 3, 6, 9},  // UBL
          new int[]{7, 2, 9, 5, 8, 10, 2, 7, 11, 9, 3},  // UFL
          new int[]{8, 11, 2, 8, 4, 1, 6, 4, 9, 10, 10},  // U
          new int[]{4, 6, 6, 3, 8, 12, 7, 9, 7, 12, 11},  // UBR
          new int[]{4, 9, 8, 4, 12, 10, 5, 2, 10, 12, 5},  // UR
          new int[]{4, 1, 6, 12, 9, 3, 1, 1, 8, 5, 11}});  // UF
  }

  @Test
  public void testClockOneTurnScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("clock");
    assertEquals(
        puzzle.scramble("UR2+"),
        new int[][]{
          new int[]{10, 12, 12, 12, 12, 12, 12, 12, 12},  // Back
          new int[]{12, 2, 2, 12, 2, 2, 12, 12, 12}});  // Front
  }

  @Test
  public void testClockFullScramble() {
    Puzzle puzzle = Puzzle.getPuzzleForEvent("clock");
    // Nathaniel Berg's 3.73 scramble.
    assertEquals(
        puzzle.scramble("UR1+ DR1+ DL5- UL3+ U3- " +
                        "R6+ D4+ L2+ ALL4+ y2 " +
                        "U0+ R0+ D2+ L0+ ALL6+ " +
                        "UR DR DL UL"),
        new int[][]{
          new int[]{12, 1, 2, 5, 1, 1, 9, 12, 7},  // Back
          new int[]{10, 6, 12, 8, 8, 8, 5, 8, 3}});  // Front
  }
}
