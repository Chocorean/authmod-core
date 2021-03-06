package io.chocorean.authmod.core;

import static org.junit.jupiter.api.Assertions.*;

import io.chocorean.authmod.core.datasource.DataSourceStrategyInterface;
import io.chocorean.authmod.core.datasource.DatabaseStrategy;
import io.chocorean.authmod.core.datasource.FileDataSourceStrategy;
import io.chocorean.authmod.core.datasource.db.ConnectionFactory;
import io.chocorean.authmod.core.datasource.db.ConnectionFactoryInterface;
import io.chocorean.authmod.core.datasource.db.DBHelpers;
import io.chocorean.authmod.core.exception.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DataSourceGuardTest {

  private GuardInterface guard;
  private final String password = "my-very-l0ng-password";
  private static File FILE;

  static {
    try {
      FILE = Files.createTempFile(DataSourceGuardTest.class.getSimpleName(), "authmod.sqlite").toFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static final ConnectionFactoryInterface[] connectionFactories = new ConnectionFactory[2];
  private PlayerInterface player;
  private DataSourceStrategyInterface dataSourceStrategy;
  private PayloadInterface registrationPayload;
  private PayloadInterface loginPayload;

  private void init(DataSourceStrategyInterface impl) throws Exception {
    connectionFactories[0] = new ConnectionFactory(String.format("jdbc:sqlite:%s", FILE.getAbsolutePath(), "org.sqlite.JDBC"));
    for(ConnectionFactoryInterface c: connectionFactories) {
      Connection conn = c.getConnection();
      conn.createStatement().execute("DELETE FROM players");
      conn.close();
    }
    this.dataSourceStrategy = impl;
    this.guard = new DataSourceGuard(dataSourceStrategy);
    this.player = new Player("fsociety", null);
    this.registrationPayload = new Payload(this.player, new String[] { password, password });
    this.loginPayload = new Payload(this.player, new String[] { password });
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testRegister(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    assertTrue(this.guard.register(this.registrationPayload));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testAuthenticate(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    this.guard.register(registrationPayload);
    PayloadInterface payload = new Payload(this.player, new String[] { this.password });
    assertTrue(this.guard.authenticate(payload));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testAuthenticateWrongPassword(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    this.guard.register(registrationPayload);
    this.loginPayload = new Payload(this.player, new String[] { "qwertyqwerty" });
    assertThrows(WrongPasswordError.class, () -> this.guard.authenticate(loginPayload));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testAuthenticateUnknownPlayer(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    PayloadInterface payload = new Payload(this.player.setUsername("admin"), new String[] { "rootroot" });
    assertThrows(PlayerNotFoundError.class, () -> this.guard.authenticate(payload));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testAuthenticateBanned(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    this.guard.register(registrationPayload);
    for(ConnectionFactoryInterface c: connectionFactories) {
      DBHelpers.banPlayer(c, registrationPayload.getPlayer().getUsername());
    }
    this.dataSourceStrategy.findByUsername(registrationPayload.getPlayer().getUsername()).setBanned(true);
    assertThrows(BannedPlayerError.class, () -> this.guard.authenticate(this.loginPayload));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testAuthenticateNullParam(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    assertThrows(Exception.class, () -> this.guard.authenticate(null));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testRegisterNullParam(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    assertThrows(Exception.class, () -> this.guard.authenticate(null));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testRegisterPlayerAlreadyExist(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    this.guard.register(this.registrationPayload);
    assertThrows(PlayerAlreadyExistError.class, () -> this.guard.register(this.registrationPayload));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testChangePassword(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    this.guard.register(registrationPayload);
    PayloadInterface payload = new Payload(this.player, new String[] { this.password });
    assertTrue(this.guard.authenticate(payload));
    String newPassword = "qwertyazerty";
    assertTrue(this.guard.updatePassword(new Payload(this.player, new String[] { this.password, newPassword, newPassword })));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testChangeNotExist(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    this.guard.register(registrationPayload);
    PayloadInterface payload = new Payload(this.player, new String[] { this.password });
    assertTrue(this.guard.authenticate(payload));
    String newPassword = "qwertyazerty";
    assertFalse(this.guard.updatePassword(new Payload(new Player(), new String[] { this.password, newPassword, newPassword })));
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testChangePasswordSamePassword(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    this.guard.register(registrationPayload);
    PayloadInterface payload = new Payload(this.player, new String[] { this.password });
    assertTrue(this.guard.authenticate(payload));
    assertThrows(
      SamePasswordError.class,
      () -> this.guard.updatePassword(new Payload(this.player, new String[] { this.password, this.password, this.password }))
    );
  }

  @ParameterizedTest(name = "with {0}")
  @MethodSource("parameters")
  void testChangePasswordWrongConfirmation(DataSourceStrategyInterface impl) throws Exception {
    init(impl);
    this.guard.register(registrationPayload);
    PayloadInterface payload = new Payload(this.player, new String[] { this.password });
    assertTrue(this.guard.authenticate(payload));
    assertThrows(
      WrongPasswordConfirmationError.class,
      () ->
        this.guard.updatePassword(
            new Payload(this.player, new String[] { this.password, "Provencal le gaulois", "Perceval le gallois" })
          )
    );
  }

  static Stream<Arguments> parameters() throws Exception {
    connectionFactories[1] = DBHelpers.initDatabase();
    return Stream.of(Arguments.of(new FileDataSourceStrategy(FILE)), Arguments.of(new DatabaseStrategy(connectionFactories[1])));
  }
}
