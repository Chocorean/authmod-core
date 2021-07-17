package io.chocorean.authmod.core.datasource;

import io.chocorean.authmod.core.datasource.db.ConnectionFactory;
import io.chocorean.authmod.core.datasource.db.ConnectionFactoryInterface;
import io.chocorean.authmod.core.exception.AuthmodError;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.Map;

public class FileDataSourceStrategy implements DataSourceStrategyInterface {

  private final PasswordHashInterface passwordHash;
  private final ConnectionFactoryInterface connectionFactory;
  private final DatabaseStrategy strategy;

  public FileDataSourceStrategy(File file, PasswordHashInterface passwordHash) throws SQLException, ClassNotFoundException {
    this.passwordHash = passwordHash;
    this.connectionFactory = new ConnectionFactory(String.format("jdbc:sqlite:%s", file.getAbsolutePath()), "org.sqlite.JDBC");
    this.createTable();
    this.strategy = new DatabaseStrategy(this.connectionFactory);
  }

  public FileDataSourceStrategy(File file) throws SQLException, ClassNotFoundException {
    this(file, new BcryptPasswordHash());
  }

  @Override
  public DataSourcePlayerInterface findByUsername(String username) throws AuthmodError {
    return this.strategy.findByUsername(username);
  }

  @Override
  public boolean add(DataSourcePlayerInterface player) throws AuthmodError {
    return this.strategy.add(player);
  }

  @Override
  public boolean exist(DataSourcePlayerInterface player) throws AuthmodError {
    return this.findByUsername(player.getUsername()) != null;
  }

  @Override
  public boolean updatePassword(DataSourcePlayerInterface player) throws AuthmodError {
    return this.strategy.updatePassword(player);
  }

  @Override
  public boolean resetPlayer(DataSourcePlayerInterface player) throws AuthmodError {
    return this.strategy.resetPlayer(player);
  }

  @Override
  public PasswordHashInterface getHashPassword() {
    return this.passwordHash;
  }


  private void createTable() throws SQLException {
    try(
      Connection connection = this.connectionFactory.getConnection();
      Statement stmt = connection.createStatement()) {
      Map<DatabaseStrategy.Column, String> columns = new EnumMap<>(DatabaseStrategy.Column.class);
      for (DatabaseStrategy.Column c : DatabaseStrategy.Column.values()) {
        switch (c) {
          case USERNAME:
            columns.put(c, String.format("%s varchar(255) NOT NULL", c.name().toLowerCase()));
            break;
          case PASSWORD:
          case UUID:
            columns.put(c, String.format("%s varchar(255)", c.name().toLowerCase()));
            break;
          case BANNED:
            columns.put(c, String.format("%s BOOLEAN DEFAULT 0", c.name().toLowerCase()));
        }
      }
      stmt.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %s (%s," +
          "id integer PRIMARY KEY," +
          "UNIQUE (uuid)," +
          "UNIQUE (username));",
        DatabaseStrategy.DEFAULT_TABLE,
        String.join(", ", columns.values())));
    }
  }

}
