package io.chocorean.authmod.core.datasource.db;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelpers {

  public static File file;

  public static void initDatabaseFile() throws IOException {
    if (DBHelpers.file != null) {
      Files.deleteIfExists(DBHelpers.file.toPath());
    }
    DBHelpers.file = Files.createTempFile(DBHelpers.class.getSimpleName(), "authmod.db").toFile();
  }

  private static String getCreationTableQuery() {
    return (
      "CREATE TABLE players (" +
      "id integer PRIMARY KEY," +
      "identifier varchar(255) NOT NULL," +
      "password varchar(255)," +
      "uuid varchar(255), " +
      "username varchar(255) NOT NULL," +
      "banned INTEGER DEFAULT 0," +
      "UNIQUE (identifier)," +
      "UNIQUE (uuid)," +
      "UNIQUE (username)" +
      ");"
    );
  }

  public static void banPlayer(ConnectionFactoryInterface connectionFactory, String identifier) throws Exception {
    Connection connection = connectionFactory.getConnection();
    if (connection != null) {
      PreparedStatement stmt = connection.prepareStatement("UPDATE players SET banned = true WHERE identifier = ?");
      stmt.setString(1, identifier);
      stmt.executeUpdate();
      connection.close();
    }
  }

  private static void initTable(ConnectionFactoryInterface connectionFactory) throws SQLException {
    Connection connection = connectionFactory.getConnection();
    Statement stmt = connection.createStatement();
    stmt.executeUpdate(getCreationTableQuery());
    connection.close();
  }

  public static ConnectionFactoryInterface initDatabase(String identifier)
    throws SQLException, IOException, ClassNotFoundException {
    initDatabaseFile();
    ConnectionFactory connectionFactory = new ConnectionFactory("jdbc:sqlite:" + file.getAbsolutePath(), "org.sqlite.JDBC");
    Connection connection = connectionFactory.getConnection();
    Statement stmt = connection.createStatement();
    stmt.executeUpdate(getCreationTableQuery().replace("identifier", identifier));
    connection.close();
    return connectionFactory;
  }

  public static ConnectionFactoryInterface initDatabase() throws SQLException, IOException, ClassNotFoundException {
    return initDatabase("identifier");
  }
}
