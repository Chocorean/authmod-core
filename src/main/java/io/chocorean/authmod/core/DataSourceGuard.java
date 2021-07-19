package io.chocorean.authmod.core;

import io.chocorean.authmod.core.datasource.DataSourcePlayer;
import io.chocorean.authmod.core.datasource.DataSourcePlayerInterface;
import io.chocorean.authmod.core.datasource.DataSourceStrategyInterface;
import io.chocorean.authmod.core.exception.*;
import io.chocorean.authmod.core.validator.ChangePasswordValidator;
import io.chocorean.authmod.core.validator.LoginValidator;
import io.chocorean.authmod.core.validator.RegistrationValidator;
import io.chocorean.authmod.core.validator.ValidatorInterface;

public class DataSourceGuard implements GuardInterface {

  private final DataSourceStrategyInterface datasource;

  public DataSourceGuard(DataSourceStrategyInterface dataSourceStrategy) {
    this.datasource = dataSourceStrategy;
  }

  @Override
  public boolean authenticate(PayloadInterface payload) throws AuthmodError {
    ValidatorInterface validator = new LoginValidator();
    validator.validate(payload);
    DataSourcePlayerInterface foundPlayer = this.datasource.findByUsername(payload.getPlayer().getUsername().toLowerCase());
    if (foundPlayer == null) throw new PlayerNotFoundError();
    if (foundPlayer.isBanned()) {
      throw new BannedPlayerError();
    }
    String password = payload.getArgs()[payload.getArgs().length - 1];
    if (!this.datasource.getHashPassword().check(foundPlayer.getPassword(), password)) {
      throw new WrongPasswordError();
    }
    return true;
  }

  @Override
  public boolean register(PayloadInterface payload) throws AuthmodError {
    ValidatorInterface validator = new RegistrationValidator();
    validator.validate(payload);
    DataSourcePlayerInterface playerProxy = new DataSourcePlayer(payload.getPlayer());
    if (this.datasource.exist(playerProxy)) throw new PlayerAlreadyExistError();
    this.hashPassword(playerProxy, payload.getArgs()[payload.getArgs().length - 1]);
    return this.datasource.add(playerProxy);
  }

  @Override
  public boolean updatePassword(PayloadInterface payload) throws AuthmodError {
    ValidatorInterface validator = new ChangePasswordValidator();
    validator.validate(payload);
    DataSourcePlayerInterface foundPlayer = this.datasource.findByUsername(payload.getPlayer().getUsername().toLowerCase());
    if (foundPlayer == null) return false;
    if (!this.datasource.getHashPassword().check(foundPlayer.getPassword(), payload.getArgs()[0])) throw new WrongPasswordError();
    this.hashPassword(foundPlayer, payload.getArgs()[payload.getArgs().length - 1]);
    return this.datasource.updatePassword(foundPlayer);
  }

  private void hashPassword(DataSourcePlayerInterface player, String password) {
    player.setPassword(this.datasource.getHashPassword().hash(password));
  }
}
