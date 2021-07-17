package io.chocorean.authmod.core.datasource;

import io.chocorean.authmod.core.Player;
import io.chocorean.authmod.core.PlayerInterface;

public class DataSourcePlayer implements DataSourcePlayerInterface {

  private String password;
  private boolean banned;
  private final PlayerInterface player;

  public DataSourcePlayer(PlayerInterface player) {
    this.player = player;
    this.password = "";
  }

  public DataSourcePlayer() {
    this(new Player());
  }

  @Override
  public boolean isBanned() {
    return this.banned;
  }

  @Override
  public DataSourcePlayerInterface setBanned(boolean ban) {
    this.banned = ban;
    return this;
  }

  @Override
  public DataSourcePlayerInterface setPassword(String password) {
    this.password = password;
    return this;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public PlayerInterface setUsername(String username) {
    return this.player.setUsername(username);
  }

  @Override
  public String getUsername() {
    return this.player.getUsername();
  }

  @Override
  public boolean isPremium() {
    return this.player.isPremium();
  }

  @Override
  public PlayerInterface setUuid(String uuid) {
    return this.player.setUuid(uuid);
  }

  @Override
  public String getUuid() {
    return this.player.getUuid();
  }

  @Override
  public String toString() {
    return this.player.toString();
  }
}
