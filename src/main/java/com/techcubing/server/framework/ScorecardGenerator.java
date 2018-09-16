package com.techcubing.server.framework;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.techcubing.proto.ScorecardProto.Scorecard;
import com.techcubing.proto.wcif.WcifEvent;
import com.techcubing.proto.wcif.WcifPerson;
import com.techcubing.proto.wcif.WcifRegistration;
import com.techcubing.proto.wcif.WcifRound;
import com.techcubing.server.util.ProtoUtil;
import com.techcubing.server.util.WcifUtil;

public class ScorecardGenerator {
  static public void generateScorecards(ProtoDb protoDb)
      throws IOException, SQLException {
    System.out.println("Generating scorecards");
    // TODO: Currently this is generating scorecards based on event registrations.
    // In the future this should use group assignments.
    Map<String, Scorecard> scorecardsByPersonAndRound = new HashMap<>();

    List<Scorecard> newScorecards = new ArrayList<>();

    // Read existing scorecards.
    for (Scorecard scorecard : protoDb.getAll(Scorecard.class)) {
      String key = scorecard.getPersonId() + "|" + scorecard.getRoundId();
      scorecardsByPersonAndRound.put(key, scorecard);
    }
    // Read all rounds from the database.
    Map<String, WcifRound> rounds = new HashMap<>();
    for (WcifRound round : protoDb.getAll(WcifRound.class)) {
      rounds.put(round.getId(), round);
    }
    // Figure out what scorecards we need, and generate the ones we don't have.
    for (WcifPerson person : protoDb.getAll(WcifPerson.class)) {
      String personId = ProtoUtil.getId(person);
      WcifRegistration registration = person.getRegistration();
      if (registration.getStatus() != WcifRegistration.Status.accepted) {
        continue;
      }
      for (String eventId : registration.getEventIdsList()) {
        WcifRound round = rounds.get(eventId + "-r1");
        String key = personId + "|" + round.getId();
        Scorecard scorecard = scorecardsByPersonAndRound.remove(key);
        if (scorecard == null) {
          Scorecard.Builder scorecardBuilder =
            Scorecard.newBuilder()
                .setId(String.valueOf(
                      String.valueOf(new Random().nextInt(90000000) + 10000000)))
                .setPersonId(personId)
                .setRoundId(round.getId());
          int numAttempts = WcifUtil.attemptsForRound(round);
          for (int i = 0; i < numAttempts; i++) {
            scorecardBuilder.addAttemptsBuilder();
          }
          newScorecards.add(scorecardBuilder.build());
        }
      }
    }
    System.out.println("Writing " + newScorecards.size() + " new scorecards.");
    System.out.println("Deleting " + scorecardsByPersonAndRound.size() + " scorecards.");
    // Write new scorecards to the database.
    for (Scorecard scorecard : newScorecards) {
      protoDb.write(scorecard);
    }
    // Delete scorecards that we no longer need.  We don't actually delete any
    // scorecards, we just mark them as "deleted".
    for (Scorecard scorecard : scorecardsByPersonAndRound.values()) {
      protoDb.write(scorecard.toBuilder().setDeleted(true).build());
    }
  }
}
