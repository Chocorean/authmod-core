package io.chocorean.authmod.core.exception;

public class WrongRegisterUsageError extends AuthmodError {

  @Override
  public String getTranslationKey() {
    return "authmod.register.usage";
  }
}
