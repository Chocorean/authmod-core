package io.chocorean.authmod.core.datasource;

import static org.junit.jupiter.api.Assertions.*;

import io.chocorean.authmod.core.Player;
import io.chocorean.authmod.core.PlayerInterface;
import io.chocorean.authmod.core.exception.AuthmodError;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileDataSourceStrategyTest {

  private File dataFile;
  private DataSourcePlayerInterface player;
  private DataSourceStrategyInterface dataSource;

  @BeforeEach
  void init() throws Exception {
    this.dataFile = File.createTempFile("test", "authmod.sqlite");
    this.dataSource = new FileDataSourceStrategy(this.dataFile);
    this.player = new DataSourcePlayer(new Player().setUsername("Whitney"));
  }

  public boolean registerPlayer(PlayerInterface player) throws AuthmodError {
    DataSourcePlayerInterface playerToRegister = new DataSourcePlayer(player);
    return dataSource.add(playerToRegister);
  }

  @Test
  void testConstructor() throws Exception {
    new FileDataSourceStrategy(this.dataFile);
    assertTrue(this.dataFile.exists(), "The strategy should create a sqlite file automatically");
  }

  @Test
  void testAdd() throws AuthmodError {
    assertTrue(this.registerPlayer(this.player));
  }

  @Test
  void testAddDuplicate() throws AuthmodError {
    assertTrue(this.registerPlayer(this.player));
    assertThrows(AuthmodError.class, () -> this.registerPlayer(this.player));
  }

  @Test
  void testFindUsername() throws AuthmodError {
    this.registerPlayer(this.player);
    assertNotNull(dataSource.findByUsername(this.player.getUsername()), "The player should be found");
  }

  @Test
  void testFindUsernameNotExist() throws AuthmodError {
    this.registerPlayer(this.player);
    assertNull(dataSource.findByUsername("test@test.com"), "The player should not exist");
  }

  @Test
  void testFindUsernameNullParams() throws AuthmodError {
    this.registerPlayer(this.player);
    assertNull(dataSource.findByUsername(null), "It should return null");
  }

  @Test
  void testSaveFileFailed() {
    this.dataFile.delete();
    this.dataFile.mkdirs();
    assertThrows(AuthmodError.class, () -> this.registerPlayer(this.player));
  }

  @Test
  void testUpdateNotExist() throws AuthmodError {
    boolean res = this.dataSource.updatePassword(new DataSourcePlayer());
    assertFalse(res);
  }

  @Test
  void testUpdateFileFailed() throws Exception {
    this.registerPlayer(this.player);
    this.dataFile.delete();
    this.dataFile.mkdirs();
    assertThrows(AuthmodError.class, () -> this.dataSource.updatePassword(this.player));
  }

  @Test
  void testWrongDataFile() throws Exception {
    BufferedWriter bw = new BufferedWriter(new FileWriter(this.dataFile));
    bw.write("test, test");
    bw.close();
    bw.close();
    assertThrows(Exception.class, () -> new FileDataSourceStrategy(this.dataFile));

  }
}
