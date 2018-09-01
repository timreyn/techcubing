package com.techcubing.server.services;

import java.io.IOException;
import java.sql.SQLException;

import com.techcubing.proto.DeviceProto.Device;
import com.techcubing.proto.DeviceTypeProto.DeviceType;
import com.techcubing.proto.ScrambleProto.Scramble;
import com.techcubing.proto.ScrambleProto.ScrambleSet;
import com.techcubing.proto.services.GetScrambleProto.GetScrambleRequest;
import com.techcubing.proto.services.GetScrambleProto.GetScrambleResponse;
import com.techcubing.proto.wcif.WcifRound;
import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ServerState;
import com.techcubing.server.util.Puzzle;

class GetScrambleImpl {
  ServerState serverState;

  public GetScrambleImpl(ServerState serverState) {
    this.serverState = serverState;
  }

  public GetScrambleResponse getScramble(GetScrambleRequest request) {
    GetScrambleResponse.Builder responseBuilder = GetScrambleResponse.newBuilder();
    try {
      Device device = ProtoDb.getIdField(
          request.getContext(), "device_id", serverState);
      if (device == null || device.getType() != DeviceType.SCRAMBLER) {
        responseBuilder.setStatus(GetScrambleResponse.Status.NOT_PERMITTED);
        return responseBuilder.build();
      }
      // TODO: check whether this person is allowed to see this scramble.

      Scramble scramble = (Scramble) ProtoDb.getById(
          request.getId(), Scramble.newBuilder(), serverState);
      if (scramble == null) {
        responseBuilder.setStatus(GetScrambleResponse.Status.SCRAMBLE_NOT_FOUND);
        return responseBuilder.build();
      }
      String scrambleSequence = scramble.getScrambleSequence();
      if (scramble.getMultiScrambleSequencesList().size() > 0) {
        scrambleSequence = scramble.getMultiScrambleSequences(request.getScrambleIndex());
      }
      // TODO: encrypt scrambles over RPC.
      responseBuilder.setEncryptedScrambleSequence(scrambleSequence);

      ScrambleSet scrambleSet = ProtoDb.getIdField(
          scramble, "scramble_set_id", serverState);
      WcifRound round = ProtoDb.getIdField(
          scrambleSet, "round_id", serverState);
      String eventId = round.getEventId();

      Puzzle puzzle = Puzzle.getPuzzleForEvent(eventId);
      if (puzzle != null) {
        int[][] scrambleState = puzzle.scramble(scrambleSequence);
        StringBuilder stateBuilder =
          new StringBuilder(scrambleState.length * (scrambleState[0].length + 1));
        for (int[] face : scrambleState) {
          for (int color : face) {
            stateBuilder.append((char) (color + 64));
          }
          stateBuilder.append("|");
        }
        // TODO: encrypt scramble state over RPC.
        responseBuilder.setEncryptedScrambleState(stateBuilder.toString());
      }
    } catch (IOException | SQLException e) {
      e.printStackTrace();
      responseBuilder.setStatus(GetScrambleResponse.Status.INTERNAL_ERROR);
    }

    return responseBuilder.build();
  }
}
