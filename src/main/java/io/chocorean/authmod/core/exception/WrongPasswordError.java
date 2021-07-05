package io.chocorean.authmod.core.exception;

/*
 * When the login password is incorrect.
 */
public class WrongPasswordError extends LoginError {

  @Override
  public String getTranslationKey() {
    return "authmod.wrongPassword";
  }

}
