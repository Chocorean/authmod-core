package io.chocorean.authmod.core.datasource;

import io.chocorean.authmod.core.exception.AuthmodError;

public interface DataSourceStrategyInterface {
  enum Strategy {
    FILE,
    DATABASE,
  }

  DataSourcePlayerInterface find(String identifier) throws AuthmodError;

  DataSourcePlayerInterface findByUsername(String username) throws AuthmodError;

  boolean add(DataSourcePlayerInterface player) throws AuthmodError;

  boolean exist(DataSourcePlayerInterface player) throws AuthmodError;

  boolean updatePassword(DataSourcePlayerInterface player) throws AuthmodError;

  boolean resetPlayer(DataSourcePlayerInterface player) throws AuthmodError;

  PasswordHashInterface getHashPassword();
}
