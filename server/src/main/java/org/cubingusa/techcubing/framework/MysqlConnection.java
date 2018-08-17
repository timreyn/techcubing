package org.cubingusa.techcubing.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class MysqlConnection {
  private Connection connection;
  private String competitionId;

  public MysqlConnection() throws SQLException {
    try {
      connection = DriverManager.getConnection(
          "jdbc:mysql://localhost/techcubing?user=techcubing&password=techcubing");
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
          "mysql> GRANT ALL PRIVILEGES ON techcubing TO 'techcubing' IDENTIFIED BY 'techcubing';");
      throw e;
    }
  }

  public String getPersonsTable() {
    return competitionId + "__persons";
  }

  public void initializeCompetition(
      String competitionId, boolean destructive) throws SQLException {
    this.competitionId = competitionId;

    // Set up tables:
    // Persons table.
    if (destructive) {
      connection.prepareStatement("DROP TABLE IF EXISTS " + getPersonsTable() + ";")
        .executeQuery();
    }
    connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS " + getPersonsTable() + " (" +
        "  wcaUserId INT PRIMARY KEY, " +
        "  personData BLOB, " +
        ")").executeQuery();
  }

  public PreparedStatement prepareStatement(String statement) throws SQLException {
    return connection.prepareStatement(statement);
  }
}
