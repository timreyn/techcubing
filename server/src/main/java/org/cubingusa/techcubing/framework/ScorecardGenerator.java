package org.cubingusa.techcubing.framework;

import com.google.protobuf.Message;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.cubingusa.techcubing.proto.ScorecardProto.Scorecard;
import org.cubingusa.techcubing.proto.wcif.WcifEvent;
import org.cubingusa.techcubing.proto.wcif.WcifPerson;
import org.cubingusa.techcubing.proto.wcif.WcifRegistration;
import org.cubingusa.techcubing.proto.wcif.WcifRound;
import org.cubingusa.techcubing.util.WcifUtil;

public class ScorecardGenerator {
  static public void generateScorecards(ServerState serverState)
      throws IOException, SQLException {
    // TODO: Currently this is generating scorecards based on event registrations.
    // In the future this should use group assignments.
    Map<String, Scorecard> scorecardsByPersonAndRound = new HashMap<>();

    List<Scorecard> newScorecards = new ArrayList<>();

    // Read existing scorecards.
    for (Message scorecardMessage :
         ProtoDb.getAll(Scorecard.newBuilder(), serverState)) {
      Scorecard scorecard = (Scorecard) scorecardMessage;
      String key = scorecard.getPersonId() + "|" + scorecard.getRoundId();
      scorecardsByPersonAndRound.put(key, scorecard);
    }
    // Read all rounds from the database.
    Map<String, WcifRound> rounds = new HashMap<>();
    for (Message roundMessage :
         ProtoDb.getAll(WcifRound.newBuilder(), serverState)) {
      WcifRound round = (WcifRound) roundMessage;
      rounds.put(round.getId(), round);
    }
    // Figure out what scorecards we need, and generate the ones we don't have.
    for (Message personMessage :
         ProtoDb.getAll(WcifPerson.newBuilder(), serverState)) {
      WcifPerson person = (WcifPerson) personMessage;
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
                .setId(String.valueOf(new Random().nextInt()))
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
    // Write new scorecards to the database.
    for (Scorecard scorecard : newScorecards) {
      ProtoDb.write(scorecard, serverState);
    }
    // Delete scorecards that we no longer need.  We don't actually delete any
    // scorecards, we just mark them as "deleted".
    for (Scorecard scorecard : scorecardsByPersonAndRound.values()) {
      ProtoDb.write(scorecard.toBuilder().setDeleted(true).build(), serverState);
    }
  }
}
