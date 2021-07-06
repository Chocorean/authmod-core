package io.chocorean.authmod.core;

import io.chocorean.authmod.core.datasource.BcryptPasswordHash;
import io.chocorean.authmod.core.datasource.DataSourceStrategyInterface;
import io.chocorean.authmod.core.datasource.DatabaseStrategy;
import io.chocorean.authmod.core.datasource.FileDataSourceStrategy;
import io.chocorean.authmod.core.datasource.db.ConnectionFactory;
import io.chocorean.authmod.core.datasource.db.ConnectionFactoryInterface;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

public class GuardFactory {

  private GuardFactory() {}

  public static GuardInterface createFromConfig(FactoryConfig config) throws ClassNotFoundException, SQLException, IOException {
    DataSourceStrategyInterface datasource;
    switch (config.getStrategy()) {
      case DATABASE:
        ConnectionFactoryInterface connectionFactory = new ConnectionFactory(
          config.getDialect(),
          config.getHost(),
          config.getPort(),
          config.getDatabase(),
          config.getUser(),
          config.getPassword(),
          config.getDriver()
        );
        datasource = new DatabaseStrategy(config.getTable(), connectionFactory, config.getColumns(), new BcryptPasswordHash());
        break;
      case FILE:
      default:
        datasource = new FileDataSourceStrategy(Paths.get(config.getConfigDirectory().toString(), "authmod.csv").toFile());
    }
    return new DataSourceGuard(datasource, config.isIdentifierRequired());
  }
}
