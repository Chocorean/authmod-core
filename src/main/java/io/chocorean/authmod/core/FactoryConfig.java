package io.chocorean.authmod.core;

import static io.chocorean.authmod.core.datasource.DataSourceStrategyInterface.Strategy.FILE;

import io.chocorean.authmod.core.datasource.DataSourceStrategyInterface;
import io.chocorean.authmod.core.datasource.DatabaseStrategy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

public class FactoryConfig {

  private DataSourceStrategyInterface.Strategy strategy = FILE;
  private String dialect = "";
  private String host = "";
  private int port;
  private String database = "";
  private String table = "";
  private String user = "";
  private String password = "";
  private String driver = "";
  private Map<DatabaseStrategy.Column, String> columns = new EnumMap<>(DatabaseStrategy.Column.class);
  private Path configDirectory = Paths.get(System.getProperty("java.io.tmpdir"));

  public String getDialect() {
    return dialect;
  }

  public FactoryConfig setDialect(String dialect) {
    this.dialect = dialect.trim();
    return this;
  }

  public String getHost() {
    return host;
  }

  public FactoryConfig setHost(String host) {
    this.host = host.trim();
    return this;
  }

  public int getPort() {
    return port;
  }

  public FactoryConfig setPort(int port) {
    this.port = port;
    return this;
  }

  public String getDatabase() {
    return database;
  }

  public FactoryConfig setDatabase(String database) {
    this.database = database.trim();
    return this;
  }

  public String getTable() {
    return table;
  }

  public FactoryConfig setTable(String table) {
    this.table = table.trim();
    return this;
  }

  public String getUser() {
    return user;
  }

  public FactoryConfig setUser(String user) {
    this.user = user.trim();
    return this;
  }

  public String getPassword() {
    return password;
  }

  public FactoryConfig setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getDriver() {
    return driver;
  }

  public FactoryConfig setDriver(String driver) {
    this.driver = driver.trim();
    return this;
  }

  public Map<DatabaseStrategy.Column, String> getColumns() {
    return columns;
  }

  public FactoryConfig setColumns(Map<DatabaseStrategy.Column, String> columns) {
    this.columns = columns;
    return this;
  }

  public DataSourceStrategyInterface.Strategy getStrategy() {
    return strategy;
  }

  public FactoryConfig setStrategy(DataSourceStrategyInterface.Strategy strategy) {
    this.strategy = strategy;
    return this;
  }

  public Path getConfigDirectory() {
    return configDirectory;
  }

  public FactoryConfig setConfigDirectory(Path configDirectory) {
    this.configDirectory = configDirectory;
    return this;
  }
}
