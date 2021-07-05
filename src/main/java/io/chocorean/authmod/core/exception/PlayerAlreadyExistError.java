package io.chocorean.authmod.core.exception;

public class PlayerAlreadyExistError extends RegistrationError {

  @Override
  public String getTranslationKey() {
    return "authmod.register.exist";
  }

}
