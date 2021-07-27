package io.chocorean.authmod.core.exception;

public class WrongRegisterUsageError extends RegistrationError {

  @Override
  public String getTranslationKey() {
    return "authmod.register.usage";
  }
}
