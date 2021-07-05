package io.chocorean.authmod.core;

import io.chocorean.authmod.core.datasource.DataSourceStrategyInterface;
import io.chocorean.authmod.core.datasource.db.DBHelpers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GuardFactoryTest {

  private FactoryConfig config;

  @BeforeEach
  void setup() throws IOException {
    this.config = new FactoryConfig()
      .setConfigDirectory(Files.createTempDirectory(GuardFactoryTest.class.getSimpleName()).toAbsolutePath());
  }

  @Test
  void testCreateGuardFile() throws Exception {
    GuardInterface guard = GuardFactory.createFromConfig(this.config);
    assertNotNull(guard);
  }

  @Test
  void testCreateGuardDatabase() throws Exception {
    DBHelpers.initDatabase();
    config
      .setDialect("sqlite")
      .setTable("players")
      .setDatabase(DBHelpers.file.getAbsolutePath())
      .setDriver("org.sqlite.JDBC")
      .setStrategy(DataSourceStrategyInterface.Strategy.DATABASE);
    GuardInterface guard = GuardFactory.createFromConfig(config);
    assertNotNull(guard);
  }

  private Path getTestingConfig(String file) {
    return Paths.get("src","test", "resources", file).toAbsolutePath();
  }

}
