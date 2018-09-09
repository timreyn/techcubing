package com.techcubing.server.util;

import java.util.HashMap;
import java.util.Map;

import com.techcubing.proto.wcif.WcifCompetition;
import com.techcubing.proto.wcif.WcifEvent;
import com.techcubing.proto.wcif.WcifRound;

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

  public static void addExtraInfo(WcifCompetition.Builder competitionBuilder) {
    for (WcifEvent.Builder eventBuilder : competitionBuilder.getEventsBuilderList()) {
      eventBuilder.setEventName(eventNames.get(eventBuilder.getId()));
      eventBuilder.setEventPriority(eventPriorities.get(eventBuilder.getId()));
      for (int i = 0; i < eventBuilder.getRoundsCount(); i++) {
        eventBuilder.getRoundsBuilder(i).setRoundNumber(i + 1);
      }
    }
  }

  private static Map<String, String> eventNames;
  private static Map<String, Integer> eventPriorities;
  static {
    eventNames = new HashMap<>();
    eventNames.put("333", "3x3x3 Cube");
    eventNames.put("222", "2x2x2 Cube");
    eventNames.put("444", "4x4x4 Cube");
    eventNames.put("555", "5x5x5 Cube");
    eventNames.put("666", "6x6x6 Cube");
    eventNames.put("777", "7x7x7 Cube");
    eventNames.put("333bf", "3x3x3 Blindfolded");
    eventNames.put("333fm", "3x3x3 Fewest Moves");
    eventNames.put("333oh", "3x3x3 One-Handed");
    eventNames.put("333ft", "3x3x3 With Feet");
    eventNames.put("clock", "Clock");
    eventNames.put("minx", "Megaminx");
    eventNames.put("pyram", "Pyraminx");
    eventNames.put("skewb", "Skewb");
    eventNames.put("sq1", "Square-1");
    eventNames.put("444bf", "4x4x4 Blindfolded");
    eventNames.put("555bf", "5x5x5 Blindfolded");
    eventNames.put("333mbf", "3x3x3 Multi-Blind");

    eventPriorities = new HashMap<>();
    eventPriorities.put("333", 1);
    eventPriorities.put("222", 2);
    eventPriorities.put("444", 3);
    eventPriorities.put("555", 4);
    eventPriorities.put("666", 5);
    eventPriorities.put("777", 6);
    eventPriorities.put("333bf", 7);
    eventPriorities.put("333fm", 8);
    eventPriorities.put("333oh", 9);
    eventPriorities.put("333ft", 10);
    eventPriorities.put("clock", 11);
    eventPriorities.put("minx", 12);
    eventPriorities.put("pyram", 13);
    eventPriorities.put("skewb", 14);
    eventPriorities.put("sq1", 15);
    eventPriorities.put("444bf", 16);
    eventPriorities.put("555bf", 17);
    eventPriorities.put("333mbf", 18);
  }
}
