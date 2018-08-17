package org.cubingusa.techcubing.framework;

import com.google.protobuf.Message;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Calendar;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

public class MysqlConnection {
  private class Table {
    private String competitionId;
    private String name;

    public Table(String competitionId, String name) {
      this.competitionId = competitionId;
      this.name = name;
    }

    public String toString() {
      return competitionId + "__" + name;
    }
  }

  private Connection connection;
  private String competitionId;

  public MysqlConnection() throws SQLException {
    try {
      connection = DriverManager.getConnection(
          "jdbc:mysql://localhost/techcubing" +
          "?user=techcubing" +
          "&password=techcubing" +
          "&useSSL=false" +
          "&serverTimezone=" + Calendar.getInstance().getTimeZone().getID());
    } catch (SQLException e) {
      System.out.println(
          "Couldn't connect to local MYSQL server.  Make sure that you have a " +
          "user named `techcubing` with password `techcubing` in your local " +
          "MYSQL server, with access to write to a database called " +
          "`techcubing`.");
      System.out.println();
      System.out.println("You can run the following commands in a MYSQL shell:");
      System.out.println("mysql> CREATE DATABASE techcubing;");
      System.out.println(
          "mysql> GRANT ALL PRIVILEGES ON techcubing.* TO 'techcubing' IDENTIFIED BY 'techcubing';");
      throw e;
    }
  }

  public PreparedStatement prepareStatement(String statement) throws SQLException {
    return connection.prepareStatement(statement);
  }

  public Table getPersonsTable() {
    return new Table(competitionId, "persons");
  }

  public void initializeCompetition(
      String competitionId, boolean destructive) throws SQLException {
    this.competitionId = competitionId;

    // Set up tables:
    // Persons table.
    if (destructive) {
      prepareStatement("DROP TABLE IF EXISTS " + getPersonsTable().toString() + ";")
        .executeUpdate();
    }
    prepareStatement(
        "CREATE TABLE IF NOT EXISTS " + getPersonsTable().toString() + " (" +
        "  id INT PRIMARY KEY, " +
        "  data BLOB " +
        ")").executeUpdate();
  }

  public void putProto(Message proto, int id, Table table)
      throws SQLException, SerialException {
    PreparedStatement statement =
      prepareStatement(
          "INSERT INTO " + table.toString() + " (id, data) VALUES (?, ?)" +
          "ON DUPLICATE KEY UPDATE data=?");
    statement.setInt(1, id);
    statement.setBlob(2, new SerialBlob(proto.toByteArray()));
    statement.setBlob(3, new SerialBlob(proto.toByteArray()));
    statement.executeUpdate();
  }
}
