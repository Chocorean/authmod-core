package io.chocorean.authmod.core;

public class ImARobotGuard implements GuardInterface {

  @Override
  public boolean authenticate(PayloadInterface payload) {
    return String.join(" ", payload.getArgs()).equals("I'm not a robot");
  }

  @Override
  public boolean register(PayloadInterface payload) {
    return true;
  }

  @Override
  public boolean updatePassword(PayloadInterface oldPayload) {
    return true;
  }
}
