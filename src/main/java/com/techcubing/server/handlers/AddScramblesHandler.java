package com.techcubing.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.techcubing.proto.ScrambleProto.Scramble;
import com.techcubing.proto.ScrambleProto.ScrambleSet;
import com.techcubing.server.framework.ProtoDb;
import com.techcubing.server.framework.ServerState;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

@Handler(path = "/add_scrambles")
public class AddScramblesHandler extends BaseHandler {
  public AddScramblesHandler(ServerState serverState) {
    super(serverState);
  }

  @Override
  protected boolean shouldParseBody() {
    return false;
  }

  @Override
  protected void handleImpl(HttpExchange t) throws Exception {
    DiskFileItemFactory d = new DiskFileItemFactory();

    ServletFileUpload up = new ServletFileUpload(d);
    List<FileItem> result = up.parseRequest(new RequestContext() {
      @Override
      public String getCharacterEncoding() {
        return "UTF-8";
      }

      @Override
      public int getContentLength() {
        return 0;
      }

      @Override
      public String getContentType() {
        return t.getRequestHeaders().getFirst("Content-type");
      }

      @Override
      public InputStream getInputStream() throws IOException {
        return t.getRequestBody();
      }
    });
    for (FileItem fileItem : result) {
      ZipInputStream inputStream = new ZipInputStream(fileItem.getInputStream());
      ZipEntry entry;
      while ((entry = inputStream.getNextEntry()) != null) {
        if (!entry.getName().startsWith("pdf/") &&
            !entry.getName().startsWith("txt/") &&
            entry.getName().endsWith(".json")) {
          JSONObject jsonObject = (JSONObject) JSONValue.parse(
            IOUtils.toString(inputStream, StandardCharsets.UTF_8));
          for (Object sheetObject : (JSONArray) jsonObject.get("sheets")) {
            JSONObject sheet = (JSONObject) sheetObject;
            ScrambleSet.Builder scrambleSetBuilder =
              ScrambleSet.newBuilder()
              .setId(String.valueOf(new Random().nextInt(90000000) + 10000000))
              .setFilename(entry.getName())
              .setRoundId(sheet.get("event") + "-r" + sheet.get("round"));

            for (Object scrambleObject : (JSONArray) sheet.get("scrambles")) {
              String scrambleString = (String) scrambleObject;
              Scramble scramble = buildScramble(
                  scrambleString, scrambleSetBuilder.getId());
              ProtoDb.write(scramble, serverState);
              scrambleSetBuilder.addScrambleId(scramble.getId());
            }

            for (Object scrambleObject : (JSONArray) sheet.get("extraScrambles")) {
              String scrambleString = (String) scrambleObject;
              Scramble scramble = buildScramble(
                  scrambleString, scrambleSetBuilder.getId());
              ProtoDb.write(scramble, serverState);
              scrambleSetBuilder.addExtraScrambleId(scramble.getId());
            }

            ProtoDb.write(scrambleSetBuilder.build(), serverState);
          }
        }
      }
    }

    redirectTo(URI.create("/manage_scrambles"), t);
  }

  private Scramble buildScramble(String scrambleString, String scrambleSetId) {
    Scramble.Builder scrambleBuilder =
      Scramble.newBuilder()
        .setScrambleSetId(scrambleSetId)
        .setId(String.valueOf(new Random().nextInt(90000000) + 10000000));
    if (scrambleString.contains("\\n")) {
      for (String scramble : scrambleString.split("\\n")) {
        scrambleBuilder.addMultiScrambleSequences(scramble);
      }
    } else {
      scrambleBuilder.setScrambleSequence(scrambleString);
    }
    return scrambleBuilder.build();
  }

  @Override
  protected List<String> supportedMethods() {
    return List.of("POST");
  }
}
