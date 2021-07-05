package io.chocorean.authmod.core.exception;

public class SamePasswordError extends ChangePasswordError {

  @Override
  public String getTranslationKey() {
    return "authmod.changepassword.samePassword";
  }

}
