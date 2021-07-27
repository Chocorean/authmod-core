package io.chocorean.authmod.core.exception;

public class WrongLoginUsageError extends LoginError {

  @Override
  public String getTranslationKey() {
    return "authmod.login.usage";
  }
}
