package io.chocorean.authmod.core;

public class Payload implements PayloadInterface {

  private final PlayerInterface player;
  private final String[] arguments;

  public Payload(PlayerInterface player, String[] args) {
    this.player = player;
    this.arguments = args;
  }

  public Payload(PlayerInterface player) {
    this(player, new String[] {});
  }

  @Override
  public PlayerInterface getPlayer() {
    return this.player;
  }

  @Override
  public String[] getArgs() {
    return this.arguments;
  }
}
