package io.chocorean.authmod.core.exception;

public class BannedPlayerError extends LoginError {

  @Override
  public String getTranslationKey() {
    return "authmod.banned";
  }
}
