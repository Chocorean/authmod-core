package io.chocorean.authmod.core.validator;

import io.chocorean.authmod.core.PayloadInterface;
import io.chocorean.authmod.core.exception.AuthmodError;
import io.chocorean.authmod.core.exception.WrongLoginUsageError;

public class LoginValidator implements ValidatorInterface {

  public LoginValidator() { }

  @Override
  public boolean validate(PayloadInterface payload) throws AuthmodError {
    int numberOfArgs = 1;
    if (payload.getArgs().length != numberOfArgs) {
      throw new WrongLoginUsageError();
    }
    return true;
  }
}
