package io.chocorean.authmod.core.datasource;

import static org.junit.jupiter.api.Assertions.*;

import io.chocorean.authmod.core.Player;
import io.chocorean.authmod.core.PlayerInterface;
import io.chocorean.authmod.core.datasource.db.DBHelpers;
import io.chocorean.authmod.core.exception.AuthmodError;
import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DataSourceTest {

  private PlayerInterface player;

  @BeforeEach
  void init() {
    this.player = new Player().setUsername("Tyler");
  }

  private static Stream<Arguments> provideDataSources() throws Exception {
    File sqlite = Files.createTempFile("testing", "authmod.sqlite").toFile();
    DataSourceStrategyInterface file = new FileDataSourceStrategy(sqlite);
    DataSourceStrategyInterface database = new DatabaseStrategy(DBHelpers.initDatabase());

    return Stream.of(Arguments.of(database), Arguments.of(file));
  }

  public boolean registerPlayer(DataSourceStrategyInterface dataSource, PlayerInterface player) throws AuthmodError {
    DataSourcePlayerInterface playerToRegister = new DataSourcePlayer(player);
    return dataSource.add(playerToRegister);
  }

  @ParameterizedTest
  @MethodSource("provideDataSources")
  void testRegisterPlayer(DataSourceStrategyInterface dataSource) throws AuthmodError {
    assertTrue(this.registerPlayer(dataSource, this.player));
  }

  @ParameterizedTest
  @MethodSource("provideDataSources")
  void testRegisterPlayerTwice(DataSourceStrategyInterface dataSource) throws AuthmodError {
    assertTrue(this.registerPlayer(dataSource, this.player));
    assertThrows(AuthmodError.class, () -> this.registerPlayer(dataSource, this.player));
  }

  @ParameterizedTest
  @MethodSource("provideDataSources")
  void testFindUsername(DataSourceStrategyInterface dataSource) throws AuthmodError {
    this.registerPlayer(dataSource, this.player);
    assertNotNull(dataSource.findByUsername(this.player.getUsername()), "The player should be found");
  }

  @ParameterizedTest
  @MethodSource("provideDataSources")
  void testExist(DataSourceStrategyInterface dataSource) throws AuthmodError {
    this.registerPlayer(dataSource, this.player);
    assertTrue(dataSource.exist(new DataSourcePlayer(this.player)), "The player should exist");
  }

  @ParameterizedTest
  @MethodSource("provideDataSources")
  void testFindByUsernameNotExist(DataSourceStrategyInterface dataSource) throws AuthmodError {
    this.registerPlayer(dataSource, this.player);
    assertNull(dataSource.findByUsername("Eddy le quartier"), "The player should not exist");
  }

  @ParameterizedTest
  @MethodSource("provideDataSources")
  void testFindByUsernameNullParams(DataSourceStrategyInterface dataSource) throws AuthmodError {
    this.registerPlayer(dataSource, this.player);
    assertNull(dataSource.findByUsername(null), "It should return null");
  }

  @ParameterizedTest
  @MethodSource("provideDataSources")
  void testUpdateNotExist(DataSourceStrategyInterface dataSource) throws AuthmodError {
    boolean res = dataSource.updatePassword(new DataSourcePlayer());
    assertFalse(res);
  }

  @ParameterizedTest
  @MethodSource("provideDataSources")
  void testUpdate(DataSourceStrategyInterface dataSource) throws AuthmodError {
    this.registerPlayer(dataSource, this.player);
    boolean res = dataSource.updatePassword(new DataSourcePlayer(this.player).setPassword("rootme"));
    assertTrue(res);
  }
}
