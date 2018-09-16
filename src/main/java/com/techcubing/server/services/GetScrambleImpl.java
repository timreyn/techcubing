package com.techcubing.server.services;

import com.google.protobuf.ByteString;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
      Device device = serverState.getProtoDb().getIdField(
          request.getContext(), "device_id");
      if (device == null || device.getType() != DeviceType.SCRAMBLER) {
        responseBuilder.setStatus(GetScrambleResponse.Status.NOT_PERMITTED);
        return responseBuilder.build();
      }
      // TODO: check whether this person is allowed to see this scramble.

      Scramble scramble = serverState.getProtoDb().getById(
          Scramble.class, request.getId());
      if (scramble == null) {
        responseBuilder.setStatus(GetScrambleResponse.Status.SCRAMBLE_NOT_FOUND);
        return responseBuilder.build();
      }
      String scrambleSequence = scramble.getScrambleSequence();
      if (scramble.getMultiScrambleSequencesList().size() > 0) {
        scrambleSequence = scramble.getMultiScrambleSequences(request.getScrambleIndex());
      }
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(
          Cipher.ENCRYPT_MODE,
          new SecretKeySpec(device.getSecretKey().toByteArray(), "AES"),
          new IvParameterSpec(device.getIv().toByteArray()));

      responseBuilder.setEncryptedScrambleSequence(ByteString.copyFrom(
          cipher.doFinal(scrambleSequence.getBytes())));

      ScrambleSet scrambleSet =
          serverState.getProtoDb().getIdField(scramble, "scramble_set_id");
      WcifRound round = serverState.getProtoDb().getIdField(scrambleSet, "round_id");
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
        responseBuilder.setEncryptedScrambleState(ByteString.copyFrom(
            cipher.doFinal(stateBuilder.toString().getBytes())));
      }
    } catch (Exception e) {
      e.printStackTrace();
      responseBuilder.setStatus(GetScrambleResponse.Status.INTERNAL_ERROR);
    }

    return responseBuilder.build();
  }
}
