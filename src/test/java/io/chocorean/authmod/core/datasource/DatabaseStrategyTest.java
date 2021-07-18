package io.chocorean.authmod.core.datasource;

import static org.junit.jupiter.api.Assertions.*;

import io.chocorean.authmod.core.Player;
import io.chocorean.authmod.core.datasource.db.ConnectionFactory;
import io.chocorean.authmod.core.datasource.db.ConnectionFactoryInterface;
import io.chocorean.authmod.core.datasource.db.DBHelpers;
import io.chocorean.authmod.core.exception.AuthmodError;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatabaseStrategyTest {

  private ConnectionFactoryInterface connectionFactory;
  private DataSourceStrategyInterface dataSource;
  private DataSourcePlayerInterface player;

  @BeforeEach
  void init() throws Exception {
    this.connectionFactory = DBHelpers.initDatabase();
    this.dataSource = new DatabaseStrategy(this.connectionFactory);
    this.player = new DataSourcePlayer(new Player().setUsername("Whitney"));
  }

  @Test
  void testConstructor() {
    assertDoesNotThrow(() -> new DatabaseStrategy(this.connectionFactory));
  }

  @Test
  void testConstructorCannotConnect() {
    this.connectionFactory = new ConnectionFactory("jdbc:mongodb:");
    Assertions.assertThrows(SQLException.class, () -> new DatabaseStrategy(this.connectionFactory));
  }

  @Test
  void testConstructorRenameColumns() throws Exception {
    this.connectionFactory = DBHelpers.initDatabase();
    Map<DatabaseStrategy.Column, String> columns = new HashMap<>();
    this.dataSource = new DatabaseStrategy("players", this.connectionFactory, columns, new BcryptPasswordHash());
    assertTrue(this.dataSource.add(this.player));
  }

  @Test
  void testAdd() throws AuthmodError {
    assertTrue(this.dataSource.add(this.player));
  }

  @Test
  void testAddSQLError() throws Exception {
    this.dataSource.add(this.player);
    Files.deleteIfExists(Paths.get(this.connectionFactory.getURL().split("sqlite:")[1]));
    assertThrows(AuthmodError.class, () -> this.dataSource.findByUsername("test@test.com"), "The player should not exist");
  }

  @Test
  void testAddPremiumAccount() throws AuthmodError {
    this.player.setUuid("7128022b-9195-490d-9bc8-9b42ebe2a8e3");
    assertTrue(this.dataSource.add(this.player));
  }

  @Test
  void testAddDuplicate() throws AuthmodError {
    assertTrue(this.dataSource.add(this.player));
    assertThrows(AuthmodError.class, () -> this.dataSource.add(this.player));
  }
  @Test
  void testFindByUsername() throws AuthmodError {
    this.dataSource.add(this.player);
    assertNotNull(this.dataSource.findByUsername(this.player.getUsername()), "The player should be found");
  }

  @Test
  void testExist() throws AuthmodError {
    this.dataSource.add(this.player);
    assertTrue(this.dataSource.exist(this.player), "The player should exist");
  }

  @Test
  void testFindNotExist() throws AuthmodError {
    this.dataSource.add(this.player);
    assertNull(this.dataSource.findByUsername("test@test.com"), "The player should not exist");
  }

  @Test
  void testFindByUsernameNotExist() throws AuthmodError {
    this.dataSource.add(this.player);
    assertNull(this.dataSource.findByUsername("Eddy le quartier"), "The player should not exist");
  }

  @Test
  void testFindByUsernameNullParams() throws AuthmodError {
    this.dataSource.add(this.player);
    assertNull(this.dataSource.findByUsername(null), "It should return null");
  }

  @Test
  void testUpdateSQLError() throws Exception {
    this.dataSource.add(this.player);
    Files.deleteIfExists(Paths.get(this.connectionFactory.getURL().split("sqlite:")[1]));
    assertThrows(AuthmodError.class, () -> this.dataSource.updatePassword(this.player));
  }

  @Test
  void testResetPlayer() throws AuthmodError {
    this.dataSource.add(this.player);
    assertTrue(this.dataSource.resetPlayer(this.player));
    assertNull(this.dataSource.findByUsername(this.player.getUsername()));
  }

  @Test
  void testResetPlayerNotExist() throws AuthmodError {
    assertFalse(this.dataSource.resetPlayer(this.player));
  }

  @Test
  void findUsernameUpperCase() throws AuthmodError, SQLException {
    String username = "JUNGLE";
    try (
      Connection c = this.connectionFactory.getConnection();
      PreparedStatement stmt = c.prepareStatement("INSERT INTO players(username, password) VALUES(?, ?)");
      PreparedStatement stmt2 = c.prepareStatement("SELECT username FROM players WHERE username = ?")) {
        stmt.setString(1, username);
        stmt.execute();
        assertNotNull(this.dataSource.findByUsername(username));
        stmt2.setString(1, username);
        ResultSet rs = stmt2.executeQuery();
        rs.next();
        assertEquals(username, rs.getString(1));
    }
  }
}
