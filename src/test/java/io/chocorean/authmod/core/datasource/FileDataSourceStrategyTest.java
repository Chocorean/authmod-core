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
    this.dataFile = File.createTempFile("test", "authmod.csv");
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
    assertTrue(this.dataFile.exists(), "The strategy should create a CSV file automatically");
  }

  @Test
  void testAdd() throws AuthmodError {
    assertTrue(this.registerPlayer(this.player));
  }

  @Test
  void testAddDoublon() throws AuthmodError {
    assertTrue(this.registerPlayer(this.player));
    assertFalse(this.registerPlayer(this.player));
  }

  @Test
  void testFileModified() throws Exception {
    this.registerPlayer(this.player);
    Thread.sleep(1000);
    BufferedWriter writer = new BufferedWriter(new FileWriter(this.dataFile, true));
    writer.append("mcdostone, mcdostone, password, false");
    writer.flush();
    writer.close();
    assertNotNull(this.dataSource.find("mcdostone"));
  }

  @Test
  void testFind() throws AuthmodError {
    this.registerPlayer(this.player);
    assertNotNull(dataSource.find(this.player.getIdentifier()), "The player should be found");
  }

  @Test
  void testFindNotExist() throws AuthmodError {
    this.registerPlayer(this.player);
    assertNull(dataSource.find("test@test.com"), "The player should not exist");
  }

  @Test
  void testFindNullParams() throws AuthmodError {
    this.registerPlayer(this.player);
    assertNull(dataSource.find(null), "It should return null");
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
    this.dataSource = new FileDataSourceStrategy(this.dataFile);
    assertFalse(this.dataSource.exist(new DataSourcePlayer(new Player("test", null))));
  }
}
