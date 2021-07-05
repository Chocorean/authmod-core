package io.chocorean.authmod.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerDescriptorTest {

  private PlayerDescriptor descriptor;

  @BeforeEach
  void init() {
    this.descriptor = new PlayerDescriptor(1, 2, 3);
  }

  @Test
  void testGetX() {
    assertEquals(1, this.descriptor.getX());
  }

  @Test
  void testGetY() {
    assertEquals(2, this.descriptor.getY());
  }

  @Test
  void testGetZ() {
    assertEquals(3, this.descriptor.getZ());
  }

}
