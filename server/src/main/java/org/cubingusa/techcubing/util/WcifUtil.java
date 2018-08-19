package org.cubingusa.techcubing.util;

import org.cubingusa.techcubing.proto.wcif.WcifRound;

public class WcifUtil {
  public static int attemptsForRound(WcifRound round) {
    switch (round.getFormat()) {
      case "1":
        return 1;
      case "2":
        return 2;
      case "3":
        return 3;
      case "m":
        return 3;
      case "a":
        return 5;
      default:
        throw new RuntimeException(
            "Bad format " + round.getFormat() + " for round " + round.getId());
    }
  }
}
