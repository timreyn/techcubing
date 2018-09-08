package com.techcubing.server.util;

import java.util.Arrays;

public class ScrambleMain {
  public static void main(String args[]) {
    if (args.length < 2) {
      printUsage();
      return;
    }
    Puzzle puzzle = Puzzle.getPuzzleForEvent(args[0]);
    if (puzzle == null) {
      printUsage();
      return;
    }

    System.out.println(Arrays.deepToString(puzzle.scramble(args[1])));
  }

  private static void printUsage() {
    System.out.println("Usage:");
    System.out.println("Scramble <event_id> \"<scramble_sequence>\"");
    System.out.println("");
    System.out.println("Example:");
    System.out.println("Scramble 333 \"L U2 D R' F2 R2 L2 U R' F' D2 F2 D2 B U L2 B2\"");
  }
}
