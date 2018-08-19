package org.cubingusa.techcubing.framework;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;
import org.cubingusa.techcubing.proto.OptionsProto;
import org.cubingusa.techcubing.util.ProtoUtil;

public class ProtoDb {
  public static String getTable(
      Descriptor descriptor, ServerState serverState) {
    String competitionId = serverState.getCompetitionId();
    if (competitionId == null) {
      return null;
    }

    String tableName = descriptor.getOptions().getExtension(OptionsProto.mysqlTableName);
    if (tableName == null) {
      return null;
    }
    return competitionId + "__" + tableName;
  }

  public static void initializeCompetition(
      String competitionId,
      ServerState serverState) throws SQLException {
    MysqlConnection connection = serverState.getMysqlConnection();
    serverState.setCompetitionId(competitionId);

    for (Descriptor descriptor : serverState.getProtoRegistry().allProtos()) {
      String tableName = getTable(descriptor, serverState);
      if (tableName == null) {
        continue;
      }
      System.out.println("Preparing table " + tableName);
      connection.prepareStatement(
          "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
          "  id VARCHAR(50) PRIMARY KEY UNIQUE, " +
          "  data BLOB, " +
          "  last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP " +
          ")").executeUpdate();

      // Add other columns that might be needed.
      for (FieldDescriptor field : descriptor.getFields()) {
        String mysqlColumnName =
          field.getOptions().getExtension(OptionsProto.mysqlColumnName);
        if (mysqlColumnName.isEmpty()) {
          continue;
        }
        String columnType = "";
        switch (field.getType()) {
          case STRING:
            columnType = "VARCHAR (50)";
            break;
          case FIXED32:
          case FIXED64:
          case INT32:
          case INT64:
          case UINT32:
          case UINT64:
            columnType = "INT";
            break;
          default:
            System.out.println(
                "Unsupported column type for " + field.getName() +
                ".  Skipping.");
            continue;
        }
        maybeAddColumn(tableName, mysqlColumnName, columnType, connection);
      }
    }
  }

  private static void maybeAddColumn(
      String tableName, String columnName, String columnType,
      MysqlConnection connection) throws SQLException {
    // First check if the column already exists.
    PreparedStatement statement = connection.prepareStatement(
        "SELECT column_name FROM information_schema.columns " +
        "WHERE table_name = ? AND column_name = ? AND " +
        "table_schema = 'techcubing'");
    statement.setString(1, tableName);
    statement.setString(2, columnName);
    ResultSet resultSet = statement.executeQuery();
    if (resultSet.next()) {
      // The column already exists!
      return;
    }

    connection.prepareStatement(
        "ALTER TABLE " + tableName + " ADD COLUMN " +
        columnName + " " + columnType).executeUpdate();
  }

  public static void recursivelyWrite(
      Message wcifProto, ServerState serverState) throws SQLException {
    List<Message> toWrite = new ArrayList<>();
    recursivelyCollectMessagesToWrite(
        wcifProto.toBuilder(), serverState, null, toWrite);
    for (Message message : toWrite) {
      write(message, serverState);
    }
  }

  private static void recursivelyCollectMessagesToWrite(
      Message.Builder builder,
      ServerState serverState,
      String parentId,
      List<Message> toWrite) {
    String id = ProtoUtil.getId(builder);
    
    Descriptor descriptor = builder.getDescriptorForType();

    // Iterate through submessages.
    Queue<Message.Builder> queue = new LinkedList<>();
    queue.add(builder);
    while (!queue.isEmpty()) {
      Message.Builder activeBuilder = queue.remove();
      Descriptor activeDescriptor = activeBuilder.getDescriptorForType();
      List<FieldDescriptor> fieldsToClear = new ArrayList<>();

      for (FieldDescriptor field : activeDescriptor.getFields()) {
        // Check if this field is an ID for another field.  If so, store the ID
        // of that field, and clear the other field.
        //
        // This is used when, for example, Event contains a repeated Round, but
        // Round is stored in a separate table.  The Event table doesn't contain
        // the full Round protos, just pointers to them.
        String idFor = field.getOptions().getExtension(OptionsProto.idFor);
        if (!idFor.isEmpty()) {
          FieldDescriptor fieldToClear =
            activeDescriptor.findFieldByName(idFor);
          if (fieldToClear != null) {
            fieldsToClear.add(fieldToClear);
            for (Object messageObject :
                 ProtoUtil.getList(activeBuilder, fieldToClear)) {
              ProtoUtil.setOrAdd(
                  activeBuilder, field, ProtoUtil.getId((Message) messageObject));
            }
          }
        }
        // Check if this field is a submessage which we need to search.
        if (field.getType() == FieldDescriptor.Type.MESSAGE) {
          Descriptor subMessageType = field.getMessageType();
          for (Message.Builder subBuilder :
               ProtoUtil.getBuilderList(activeBuilder, field)) {
            if (subMessageType
                .getOptions()
                .getExtension(OptionsProto.mysqlTableName)
                .isEmpty()) {
              queue.add(subBuilder);
            } else {
              recursivelyCollectMessagesToWrite(
                  subBuilder, serverState, id, toWrite);
            }
          }
        }
        // Check if this field is a reference to the parent entity.
        if (field.getOptions().getExtension(OptionsProto.parentRef)) {
          activeBuilder.setField(field, parentId);
        }
      }

      // Clear out fields we don't need to keep.
      for (FieldDescriptor field : fieldsToClear) {
        activeBuilder.clearField(field);
      }
    }
    toWrite.add(builder.build());
  }

  public static void write(Message message, ServerState serverState)
      throws SQLException {
    Descriptor descriptor = message.getDescriptorForType();
    String id = ProtoUtil.getId(message);
    String tableName = getTable(descriptor, serverState);
    PreparedStatement statement = serverState.getMysqlConnection().prepareStatement(
        "INSERT INTO " + tableName + " (id, data) VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE data = VALUES(data)");
    statement.setString(1, id);
    statement.setBlob(2, new SerialBlob(message.toByteArray()));
    statement.executeUpdate();

    writeAdditionalColumns(message, serverState);
  }

  private static void writeAdditionalColumns(
      Message message, ServerState serverState)
      throws SQLException {
    Descriptor descriptor = message.getDescriptorForType();
    String id = ProtoUtil.getId(message);
    String tableName = getTable(descriptor, serverState);
    for (FieldDescriptor field : descriptor.getFields()) {
      String mysqlColumnName =
        field.getOptions().getExtension(OptionsProto.mysqlColumnName);
      if (mysqlColumnName.isEmpty()) {
        continue;
      }
      PreparedStatement updateStatement =
        serverState.getMysqlConnection().prepareStatement(
            "UPDATE " + tableName + " SET " + mysqlColumnName + " = ? WHERE id = ?");
      switch (field.getType()) {
        case STRING:
          updateStatement.setString(1, (String) message.getField(field));
          break;
        case FIXED32:
        case FIXED64:
        case INT32:
        case INT64:
        case UINT32:
        case UINT64:
          updateStatement.setInt(1, (int) message.getField(field));
          break;
      }
      updateStatement.setString(2, id);
      updateStatement.executeUpdate();
    }
  }

  public static Message getById(
      String id, Message.Builder tmpl, ServerState serverState)
      throws SQLException, IOException {
    tmpl.clear();
    String tableName = getTable(tmpl.getDescriptorForType(), serverState);
    if (tableName == null) {
      return null;
    }
    PreparedStatement statement = serverState.getMysqlConnection().prepareStatement(
        "SELECT data FROM " + tableName + " WHERE id = ?");
    statement.setString(1, id);
    ResultSet results = statement.executeQuery();
    if (results.next()) {
      tmpl.mergeFrom(results.getBlob("data").getBinaryStream());
      return tmpl.build();
    } else {
      return null;
    }
  }

  public static List<Message> getAll(
      Message.Builder tmpl, ServerState serverState)
      throws SQLException, IOException {
    tmpl.clear();
    List<Message> values = new ArrayList<>();
    String tableName = getTable(tmpl.getDescriptorForType(), serverState);
    if (tableName == null) {
      return values;
    }
    ResultSet results = serverState.getMysqlConnection().prepareStatement(
        "SELECT data FROM " + tableName)
      .executeQuery();
    while (results.next()) {
      Message.Builder value = (Message.Builder) tmpl.clone();
      value.mergeFrom(results.getBlob("data").getBinaryStream());
      values.add(value.build());
    }
    return values;
  }

  // Used to atomically update a message in the database, and ensure that no
  // intervening updates occur.  Updates can return false to decline to make this
  // update.
  public interface ProtoUpdate {
    public boolean update(Message.Builder builder);
  }
  public enum UpdateResult {
    OK, ID_NOT_FOUND, DECLINED, RETRIES_EXCEEDED
  };
  public static UpdateResult atomicUpdate(
      Message.Builder tmpl, String id, ProtoUpdate update,
      ServerState serverState) throws SQLException, IOException {
    String tableName = getTable(tmpl.getDescriptorForType(), serverState);
    for (int i = 0; i < 10; i++) {
      tmpl.clear();
      PreparedStatement statement = serverState.getMysqlConnection().prepareStatement(
          "SELECT data, last_update FROM " + tableName + " WHERE id = ?");
      statement.setString(1, id);
      ResultSet results = statement.executeQuery();
      if (!results.next()) {
        return UpdateResult.ID_NOT_FOUND;
      }
      tmpl.mergeFrom(results.getBlob("data").getBinaryStream());

      if (!update.update(tmpl)) {
        return UpdateResult.DECLINED;
      }

      statement = serverState.getMysqlConnection().prepareStatement(
          "UPDATE " + tableName + " SET data = ? WHERE id = ? AND last_update = ?");
      statement.setBlob(1, new SerialBlob(tmpl.build().toByteArray()));
      statement.setString(2, id);
      statement.setTimestamp(3, results.getTimestamp("last_update"));
      if (statement.executeUpdate() == 1) {
        // Note that columns other than data are *not* updated atomically.
        writeAdditionalColumns(tmpl.build(), serverState);

        return UpdateResult.OK;
      }
      System.out.println(
          "Failed to atomically update " + tableName + " row " + id + " " +
          (i + 1) + " times.");
    }

    return UpdateResult.RETRIES_EXCEEDED;
  }
}
