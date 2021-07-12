package io.chocorean.authmod.core.datasource.db;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class ConnectionFactoryTest {

  private final String driver = "org.sqlite.JDBC";

  @Test
  void testConstructorUrl() throws Exception {
    ConnectionFactoryInterface connectionFactory = DBHelpers.initDatabase();
    Connection connection = connectionFactory.getConnection();
    assertNotNull(connection, "Connection should be configured correctly");
    connection.close();
  }

  @Test
  void testConstructorUrlWithUser() throws Exception {
    DBHelpers.initDatabaseFile();
    ConnectionFactoryInterface connectionFactory = new ConnectionFactory(
      "sqlite",
      "",
      0,
      DBHelpers.file.getAbsolutePath(),
      "root",
      "rootroot",
      driver
    );
    Connection connection = connectionFactory.getConnection();
    assertNotNull(connection, "Connection should be configured correctly");
    connection.close();
  }

  @Test
  void testConstructorParamsSqlite() throws Exception {
    DBHelpers.initDatabaseFile();
    ConnectionFactoryInterface connectionFactory = new ConnectionFactory(
      "sqlite",
      "",
      0,
      DBHelpers.file.getAbsolutePath(),
      null,
      null,
      driver
    );
    Connection connection = connectionFactory.getConnection();
    assertNotNull(connection, "Connection should be configured correctly");
    assertEquals(
      "jdbc:sqlite:" + DBHelpers.file.getAbsolutePath(),
      connection.getMetaData().getURL(),
      "JDBC URL is malformed for SQLite"
    );
    connection.close();
  }

  @Test
  void testConstructorParamsMariadb() throws Exception {
    ConnectionFactoryInterface connectionFactory = new ConnectionFactory(
      "mariadb",
      "localhost",
      3306,
      "minecraft",
      null,
      null,
      driver
    );
    assertEquals("jdbc:mariadb://localhost:3306/minecraft", connectionFactory.getURL(), "JDBC URL is malformed for mariadb");
  }

  @Test
  void testGetConnectionSQLError() throws Exception {
    ConnectionFactoryInterface connectionFactory = new ConnectionFactory(
      "unknown-dialect",
      "localhost",
      3306,
      "minecraft",
      "awesome",
      "password",
      driver
    );
    assertThrows(SQLException.class, connectionFactory::getConnection);
  }
}
