package io.chocorean.authmod.core.datasource;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataSourcePlayerTest {

  private DataSourcePlayerInterface player;

  @BeforeEach
  void init() {
    this.player = new DataSourcePlayer();
    this.player.setUuid("7128022b-9195-490d-9bc8-9b42ebe2a8e3");
  }

  @Test
  void testSetUsername() {
    String username = "mcdostone";
    this.player.setUsername(username + ' ');
    assertEquals(username, this.player.getUsername());
  }

  @Test
  void testSetUsernameDownCase() {
    String username = "MCDOSTone";
    this.player.setUsername(username);
    assertEquals("mcdostone", this.player.getUsername());
  }

  @Test
  void testIsPremium() {
    assertTrue(this.player.isPremium());
  }

  @Test
  void testSetPassword() {
    String password = "rootroot ";
    this.player.setPassword(password);
    assertEquals(this.player.getPassword(), password);
  }

  @Test
  void testToString() {
    assertNotNull(this.player.toString());
  }
}
