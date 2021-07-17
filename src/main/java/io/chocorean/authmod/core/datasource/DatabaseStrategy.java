package io.chocorean.authmod.core.datasource;

import io.chocorean.authmod.core.datasource.db.ConnectionFactoryInterface;
import io.chocorean.authmod.core.exception.AuthmodError;
import java.sql.*;
import java.util.EnumMap;
import java.util.Map;

public class DatabaseStrategy implements DataSourceStrategyInterface {

  public static final  String DEFAULT_TABLE = "players";
  private final String table;
  private final Map<Column, String> columns;

  public enum Column {
    USERNAME,
    UUID,
    PASSWORD,
    BANNED,
  }

  private final PasswordHashInterface passwordHash;
  private final ConnectionFactoryInterface connectionFactory;

  public DatabaseStrategy(
    String table,
    ConnectionFactoryInterface connectionFactory,
    Map<Column, String> customColumns,
    PasswordHashInterface passwordHash
  ) throws SQLException {
    this.table = table;
    this.connectionFactory = connectionFactory;
    this.columns = new EnumMap<>(Column.class);
    for (Column c : Column.values()) {
      this.columns.put(c, c.name().toLowerCase());
    }
    this.columns.putAll(customColumns);
    this.passwordHash = passwordHash;
    this.checkTable();
  }

  public DatabaseStrategy(ConnectionFactoryInterface connectionFactory) throws SQLException {
    this(DEFAULT_TABLE, connectionFactory, new EnumMap<>(Column.class), new BcryptPasswordHash());
  }

  @Override
  public DataSourcePlayerInterface findByUsername(String username) throws AuthmodError {
    return this.findBy(this.columns.get(Column.USERNAME), username);
  }

  @Override
  public boolean add(DataSourcePlayerInterface player) throws AuthmodError {
    String query = String.format(
      "INSERT INTO %s(%s, %s, %s) VALUES(?, ?, ?)",
      this.table,
      this.columns.get(Column.PASSWORD),
      this.columns.get(Column.USERNAME),
      this.columns.get(Column.UUID)
    );
    try (Connection conn = this.connectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, player.getPassword());
      stmt.setString(2, player.getUsername());
      if (player.isPremium()) stmt.setString(3, player.getUuid()); else stmt.setNull(3, Types.VARCHAR);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      throw new AuthmodError(e.getMessage());
    }
  }

  @Override
  public boolean exist(DataSourcePlayerInterface player) throws AuthmodError {
    return this.findByUsername(player.getUsername()) != null;
  }

  @Override
  public boolean updatePassword(DataSourcePlayerInterface player) throws AuthmodError {
    String query = String.format(
      "UPDATE %s SET %s = ? WHERE %s = ?;",
      this.table,
      this.columns.get(Column.PASSWORD),
      this.columns.get(Column.USERNAME)
    );
    try (Connection conn = this.connectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, player.getPassword());
      stmt.setString(2, player.getUsername());
      return stmt.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new AuthmodError(e.getMessage());
    }
  }

  @Override
  public boolean resetPlayer(DataSourcePlayerInterface player) throws AuthmodError {
    String query = String.format(
      "DELETE FROM %s WHERE %s = ?",
      this.table,
      this.columns.get(Column.USERNAME)
    );
    try (Connection conn = this.connectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, player.getUsername());
      return stmt.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new AuthmodError(e.getMessage());
    }
  }

  @Override
  public PasswordHashInterface getHashPassword() {
    return this.passwordHash;
  }

  private void checkTable() throws SQLException {
    try (
      Connection connection = this.connectionFactory.getConnection();
      PreparedStatement stmt = connection.prepareStatement(
        String.format(
          "SELECT %s,%s,%s,%s FROM %s",
          this.columns.get(Column.BANNED),
          this.columns.get(Column.PASSWORD),
          this.columns.get(Column.USERNAME),
          this.columns.get(Column.UUID),
          this.table
        )
      )
    ) {
      stmt.executeQuery();
    }
  }

  private DataSourcePlayerInterface findBy(String columnName, String value) throws AuthmodError {
    try (
      Connection conn = this.connectionFactory.getConnection();
      PreparedStatement stmt = conn.prepareStatement(String.format("SELECT * FROM %s WHERE %s = ?", this.table, columnName))
    ) {
      stmt.setString(1, value);
      ResultSet rs = stmt.executeQuery();
      return this.createPlayer(rs);
    } catch (SQLException e) {
      throw new AuthmodError(e.getMessage());
    }
  }

  private DataSourcePlayerInterface createPlayer(ResultSet rs) throws SQLException {
    DataSourcePlayerInterface player = null;
    if (rs != null && rs.next()) {
      player = new DataSourcePlayer();
      player.setBanned(rs.getInt(this.columns.get(Column.BANNED)) != 0);
      player.setPassword(rs.getString(this.columns.get(Column.PASSWORD)));
      player.setUsername(rs.getString(this.columns.get(Column.USERNAME)));
      player.setUuid(rs.getString(this.columns.get(Column.UUID)));
    }
    if (rs != null) {
      rs.close();
    }
    return player;
  }
}
