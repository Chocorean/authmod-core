package io.chocorean.authmod.core.i18n;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServerLanguageMapTest {

  private final String key = "authmod.welcome";

  @BeforeEach
  void setup() throws NoSuchFieldException, IllegalAccessException {
    Field instance = ServerLanguageMap.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
  }

  @Test
  void testConstructor() {
    ServerLanguageMap.loadTranslations();
    assertNotNull(ServerLanguageMap.getInstance().getOrDefault(key));
  }

  @ParameterizedTest
  @EnumSource(ServerLanguageMap.Language.class)
  void getValueForAMonth_IsAlwaysBetweenOneAndTwelve(ServerLanguageMap.Language lang) {
    ServerLanguageMap.loadTranslations(lang.name());
    assertNotNull(ServerLanguageMap.getInstance().getOrDefault(key));
  }

  @Test
  void testReplaceAll() {
    ServerLanguageMap.loadTranslations();
    String welcome = "Bonsoir Eliot!";
    Map<String, String> newTranslations = new HashMap<>();
    newTranslations.put(key, welcome);
    ServerLanguageMap.replaceTranslations(newTranslations);
    assertEquals(welcome, ServerLanguageMap.getInstance().getOrDefault(key));
  }

  @Test
  void testReplaceAllNoInstance() {
    String welcome = "Bonsoir Elliot!";
    Map<String, String> newTranslations = new HashMap<>();
    newTranslations.put(key, welcome);
    ServerLanguageMap.replaceTranslations(newTranslations);
    assertNotEquals(welcome, ServerLanguageMap.getInstance().getOrDefault(key));
  }

  @Test
  void testGetInstance() {
    assertNotNull(ServerLanguageMap.getInstance());
  }

  @Test
  void testHas() {
    ServerLanguageMap.loadTranslations();
    assertTrue(ServerLanguageMap.getInstance().has(key));
  }

  @Test
  void testLoadTranslationsWithInstance() {
    ServerLanguageMap.getInstance();
    ServerLanguageMap.loadTranslations("fr_fr");
    assertEquals("oui", ServerLanguageMap.getInstance().getOrDefault("authmod.logged.yes"));
  }


}
