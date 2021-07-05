package io.chocorean.authmod.core.exception;

public class PlayerNotFoundError extends LoginError {

  @Override
  public String getTranslationKey() {
    return "authmod.login.notFound";
  }

}
