package com.techcubing.server.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Calendar;

public class MysqlConnection {
  private Connection connection;

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
}
