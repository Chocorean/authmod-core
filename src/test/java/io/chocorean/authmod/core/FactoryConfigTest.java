package io.chocorean.authmod.core;

import static org.junit.jupiter.api.Assertions.*;

import io.chocorean.authmod.core.datasource.DatabaseStrategy;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FactoryConfigTest {

  @Test
  void testConstructor() {
    FactoryConfig config = new FactoryConfig();
    assertEquals("", config.getDialect());
    assertEquals("", config.getDatabase());
    assertEquals("", config.getTable());
    assertEquals("", config.getPassword());
    assertEquals("", config.getUser());
    assertEquals(0, config.getPort());
    assertEquals("", config.getDialect());
    assertNotNull(config.getConfigDirectory());
  }

  @Test
  void testPort() {
    assertEquals(1000, new FactoryConfig().setPort(1000).getPort());
  }

  @Test
  void testIdentifierRequired() {
    assertTrue(new FactoryConfig().setIdentifierRequired(true).isIdentifierRequired());
  }

  @Test
  void testHost() {
    assertEquals("localhost", new FactoryConfig().setHost("localhost").getHost());
  }

  @Test
  void testUser() {
    assertEquals("postgres", new FactoryConfig().setUser("postgres").getUser());
  }

  @Test
  void testPassword() {
    assertEquals("root", new FactoryConfig().setPassword("root").getPassword());
  }

  @Test
  void testColumns() {
    Map<DatabaseStrategy.Column, String> columns = new HashMap<>();
    columns.put(DatabaseStrategy.Column.PASSWORD, "hashed_password");
    assertEquals(columns, new FactoryConfig().setColumns(columns).getColumns());
  }
}
