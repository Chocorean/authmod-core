package io.chocorean.authmod.core.exception;

public class AuthmodError extends Exception {

  public static final String DEFAULT_KEY = "authmod.error";

  public AuthmodError() {
    super();
  }

  public AuthmodError(String message) {
    super(message);
  }

  public String getTranslationKey() {
    return DEFAULT_KEY;
  }
}
