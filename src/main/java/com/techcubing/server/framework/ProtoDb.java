package com.techcubing.server.framework;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

import com.techcubing.proto.OptionsProto;
import com.techcubing.server.util.ProtoUtil;

public class ProtoDb {
  MysqlConnection connection;
  ServerState serverState;
  ProtoRegistry protoRegistry;

  public ProtoDb(MysqlConnection connection, ServerState serverState) {
    this.connection = connection;
    this.serverState = serverState;
    this.protoRegistry = serverState.getProtoRegistry();
  }

  private <T extends Message> String getTable(Class<T> clazz) {
    String competitionId = serverState.getCompetitionId();
    if (competitionId == null) {
      return null;
    }

    String wcaEnvironment = serverState.getWcaEnvironment().toString();

    Descriptor descriptor = protoRegistry.get(clazz).descriptor;
    String tableName = descriptor.getOptions().getExtension(OptionsProto.mysqlTableName);
    if (tableName == null) {
      return null;
    }
    return competitionId + "__" + wcaEnvironment + "__" + tableName;
  }

  public void initializeCompetition(String competitionId) throws SQLException {
    serverState.setCompetitionId(competitionId);

    connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS __ActiveCompetition (" +
        "  env VARCHAR(10) PRIMARY KEY UNIQUE, " +
        "  competitionId VARCHAR(50))").executeUpdate();
    PreparedStatement statement = connection.prepareStatement(
        "INSERT INTO __ActiveCompetition VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE competitionId = ?");
    statement.setString(1, serverState.getWcaEnvironment().toString());
    statement.setString(2, competitionId);
    statement.setString(3, competitionId);
    statement.executeUpdate();

    for (Class<Message> clazz : protoRegistry.allProtos()) {
      String tableName = getTable(clazz);
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
      for (FieldDescriptor field :
           protoRegistry.get(clazz).descriptor.getFields()) {
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
        maybeAddColumn(tableName, mysqlColumnName, columnType);
      }
    }
  }

  private void maybeAddColumn(
      String tableName, String columnName, String columnType) throws SQLException {
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

  public void recursivelyWrite(Message wcifProto) throws SQLException {
    List<Message> toWrite = new ArrayList<>();
    recursivelyCollectMessagesToWrite(
        wcifProto.toBuilder(), null, toWrite);
    for (Message message : toWrite) {
      write(message);
    }
  }

  private void recursivelyCollectMessagesToWrite(
      Message.Builder builder,
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
              recursivelyCollectMessagesToWrite(subBuilder, id, toWrite);
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

  public void write(Message message) throws SQLException {
    Descriptor descriptor = message.getDescriptorForType();
    String id = ProtoUtil.getId(message);
    String tableName = getTable(message.getClass());
    PreparedStatement statement = connection.prepareStatement(
        "INSERT INTO " + tableName + " (id, data) VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE data = VALUES(data)");
    statement.setString(1, id);
    statement.setBlob(2, new SerialBlob(message.toByteArray()));
    statement.executeUpdate();

    writeAdditionalColumns(message);
  }

  private void writeAdditionalColumns(Message message) throws SQLException {
    Descriptor descriptor = message.getDescriptorForType();
    String id = ProtoUtil.getId(message);
    String tableName = getTable(message.getClass());
    for (FieldDescriptor field : descriptor.getFields()) {
      String mysqlColumnName =
        field.getOptions().getExtension(OptionsProto.mysqlColumnName);
      if (mysqlColumnName.isEmpty()) {
        continue;
      }
      PreparedStatement updateStatement = connection.prepareStatement(
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

  public <T extends Message> T getById(Class<T> clazz, String id)
      throws SQLException, IOException {
    Parser<T> parser = protoRegistry.get(clazz).parser;
    String tableName = getTable(clazz);
    if (tableName == null) {
      return null;
    }
    PreparedStatement statement = connection.prepareStatement(
        "SELECT data FROM " + tableName + " WHERE id = ?");
    statement.setString(1, id);
    ResultSet results = statement.executeQuery();
    if (results.next()) {
      return parser.parseFrom(results.getBlob("data").getBinaryStream());
    } else {
      return null;
    }
  }

  public <T extends Message> List<T> getAll(Class<T> clazz)
      throws SQLException, IOException {
    Parser<T> parser = protoRegistry.get(clazz).parser;
    List<T> values = new ArrayList<>();
    String tableName = getTable(clazz);
    if (tableName == null) {
      return values;
    }
    ResultSet results = connection.prepareStatement(
        "SELECT data FROM " + tableName)
      .executeQuery();
    while (results.next()) {
      values.add(parser.parseFrom(results.getBlob("data").getBinaryStream()));
    }
    return values;
  }

  public <T extends Message> List<T> getAllMatching(
      Class<T> clazz, String fieldName, String fieldValue)
      throws SQLException, IOException {
    Parser<T> parser = protoRegistry.get(clazz).parser;
    List<T> values = new ArrayList<>();
    String tableName = getTable(clazz);
    if (tableName == null) {
      return values;
    }
    FieldDescriptor field =
      protoRegistry.get(clazz).descriptor.findFieldByName(fieldName);
    if (field == null) {
      return values;
    }
    String filterColumnName =
      field.getOptions().getExtension(OptionsProto.mysqlColumnName);

    PreparedStatement statement = connection.prepareStatement(
        "SELECT data FROM " + tableName + " WHERE " + filterColumnName + " = ?");
    statement.setString(1, fieldValue);

    ResultSet results = statement.executeQuery();
    while (results.next()) {
      values.add(parser.parseFrom(results.getBlob("data").getBinaryStream()));
    }
    return values;
  }

  // Used to atomically update a message in the database, and ensure that no
  // intervening updates occur.  Updates can return false to decline to make this
  // update.
  public interface ProtoUpdate<B extends Message.Builder> {
    public boolean update(B builder);
  }
  public enum UpdateResult {
    OK, ID_NOT_FOUND, DECLINED, RETRIES_EXCEEDED, INVALID_PROTO
  };
  public <T extends Message, B extends Message.Builder>
  UpdateResult atomicUpdate(Class<T> clazz, String id, ProtoUpdate<B> update)
      throws SQLException, IOException {
    Parser<T> parser = protoRegistry.get(clazz).parser;
    String tableName = getTable(clazz);
    if (tableName == null) {
      return UpdateResult.INVALID_PROTO;
    }
    for (int i = 0; i < 10; i++) {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT data, last_update FROM " + tableName + " WHERE id = ?");
      statement.setString(1, id);
      ResultSet results = statement.executeQuery();
      if (!results.next()) {
        return UpdateResult.ID_NOT_FOUND;
      }

      B builder =
        (B) parser.parseFrom(results.getBlob("data").getBinaryStream()).toBuilder();

      if (!update.update(builder)) {
        return UpdateResult.DECLINED;
      }

      T message = (T) builder.build();

      statement = connection.prepareStatement(
          "UPDATE " + tableName + " SET data = ? WHERE id = ? AND last_update = ?");
      statement.setBlob(1, new SerialBlob(message.toByteArray()));
      statement.setString(2, id);
      statement.setTimestamp(3, results.getTimestamp("last_update"));
      if (statement.executeUpdate() == 1) {
        // Note that columns other than data are *not* updated atomically.
        writeAdditionalColumns(message);

        return UpdateResult.OK;
      }
      System.out.println(
          "Failed to atomically update " + tableName + " row " + id + " " +
          (i + 1) + " times.");
    }

    return UpdateResult.RETRIES_EXCEEDED;
  }

  public <T extends Message> T getIdField(MessageOrBuilder message, String fieldName)
      throws SQLException, IOException {
    FieldDescriptor field =
      message.getDescriptorForType().findFieldByName(fieldName);
    String messageType = field.getOptions().getExtension(OptionsProto.messageType);
    Class<T> clazz = (Class<T>) protoRegistry.get(messageType).clazz;
    return getById(clazz, (String) message.getField(field));
  }
}
