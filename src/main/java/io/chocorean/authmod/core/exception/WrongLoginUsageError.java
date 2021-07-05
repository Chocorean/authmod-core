package io.chocorean.authmod.core.exception;

public class WrongLoginUsageError extends AuthmodError {

  @Override
  public String getTranslationKey() {
    return "authmod.login.usage";
  }

}
