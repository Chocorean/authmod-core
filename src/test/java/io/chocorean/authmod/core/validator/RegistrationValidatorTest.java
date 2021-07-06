package io.chocorean.authmod.core.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.chocorean.authmod.core.Payload;
import io.chocorean.authmod.core.Player;
import io.chocorean.authmod.core.PlayerInterface;
import io.chocorean.authmod.core.exception.AuthmodError;
import io.chocorean.authmod.core.exception.WrongPasswordConfirmationError;
import io.chocorean.authmod.core.exception.WrongRegisterUsageError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrationValidatorTest {

  private ValidatorInterface validator;
  private PlayerInterface player;

  @BeforeEach
  void init() {
    this.validator = new RegistrationValidator(false);
    this.player = new Player(null, null);
  }

  @Test
  void testValidate() throws AuthmodError {
    assertTrue(this.validator.validate(new Payload(this.player, new String[] { "Tintin", "Tintin" })));
  }

  @Test
  void testValidateWrongNumberOfArgs() {
    assertThrows(WrongRegisterUsageError.class, () -> this.validator.validate(new Payload(this.player, new String[] {})));
  }

  @Test
  void testValidateWrongConfirmation() {
    assertThrows(
      WrongPasswordConfirmationError.class,
      () -> this.validator.validate(new Payload(this.player, new String[] { "qwerty", "azerty" }))
    );
  }
}
