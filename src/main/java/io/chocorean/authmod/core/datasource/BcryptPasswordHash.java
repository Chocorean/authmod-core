package io.chocorean.authmod.core.datasource;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptPasswordHash implements PasswordHashInterface {

  public String hash(String data) {
    return BCrypt.hashpw(data, BCrypt.gensalt());
  }

  @Override
  public boolean check(String hashedPassword, String password) {
    try {
      return BCrypt.checkpw(password, hashedPassword);
    } catch(Exception e) {
      return false;
    }
  }

}
