package io.chocorean.authmod.core.datasource;

import io.chocorean.authmod.core.Player;
import io.chocorean.authmod.core.exception.AuthmodError;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileDataSourceStrategy implements DataSourceStrategyInterface {

  private static final String SEPARATOR = ",";
  private final File file;
  private final PasswordHashInterface passwordHash;
  private long lastModification;
  private final ArrayList<DataSourcePlayerInterface> players;

  public FileDataSourceStrategy(File file, PasswordHashInterface passwordHash) throws IOException {
    this.file = file;
    this.passwordHash = passwordHash;
    this.players = new ArrayList<>();
    this.readFile();
  }

  public FileDataSourceStrategy(File file) throws IOException {
    this(file, new BcryptPasswordHash());
  }

  @Override
  public DataSourcePlayerInterface find(String identifier) throws AuthmodError {
    try {
      this.reloadFile();
    } catch (IOException e) {
      throw new AuthmodError(e.getMessage());
    }
    return this.players.stream().filter(tmp -> tmp.getIdentifier().equals(identifier)).findFirst().orElse(null);
  }

  @Override
  public DataSourcePlayerInterface findByUsername(String username) throws AuthmodError {
    try {
      this.reloadFile();
    } catch (IOException e) {
      throw new AuthmodError(e.getMessage());
    }
    return this.players.stream().filter(tmp -> username.equals(tmp.getUsername())).findFirst().orElse(null);
  }

  @Override
  public boolean add(DataSourcePlayerInterface player) throws AuthmodError {
    if (!this.exist(player)) {
      this.players.add(player);
      try {
        this.saveFile();
        return true;
      } catch (IOException e) {
        throw new AuthmodError(e.getMessage());
      }
    }
    return false;
  }

  @Override
  public boolean exist(DataSourcePlayerInterface player) throws AuthmodError {
    return this.find(player.getIdentifier()) != null;
  }

  @Override
  public boolean updatePassword(DataSourcePlayerInterface player) throws AuthmodError {
    if (this.exist(player)) {
      this.players.remove(this.find(player.getIdentifier()));
      return this.add(player);
    }
    return false;
  }

  @Override
  public boolean resetPlayer(DataSourcePlayerInterface player) throws AuthmodError {
    if (this.exist(player)) {
      this.players.remove(this.find(player.getIdentifier()));
      try {
        this.saveFile();
        return true;
      } catch (IOException e) {
        throw new AuthmodError(e.getMessage());
      }
    }
    return false;
  }

  @Override
  public PasswordHashInterface getHashPassword() {
    return this.passwordHash;
  }

  private void saveFile() throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file, false))) {
      bw.write(String.join(SEPARATOR, "# Identifier", " username", " hashed password", " uuid", " is banned ?"));
      bw.newLine();
      for (DataSourcePlayerInterface entry : this.players) {
        bw.write(
          String.join(
            SEPARATOR,
            entry.getIdentifier(),
            entry.getUsername(),
            entry.getPassword(),
            entry.getUuid(),
            Boolean.toString(entry.isBanned())
          )
        );
        bw.newLine();
      }
      this.lastModification = Files.getLastModifiedTime(this.file.toPath()).toMillis();
    }
  }

  private void readFile() throws IOException {
    this.players.clear();
    this.file.createNewFile();      
    try (BufferedReader bf = new BufferedReader(new FileReader(this.file))) {
      String line;
      while ((line = bf.readLine()) != null) {
        if (!line.trim().startsWith("#")) {
          String[] parts = line.trim().split(SEPARATOR);
          if (parts.length == 5) {
            DataSourcePlayerInterface p = new DataSourcePlayer(new Player());
            p.setIdentifier(parts[0].trim())
              .setPassword(parts[2])
              .setBanned(Boolean.parseBoolean(parts[4].trim()))
              .setUuid(parts[3].trim())
              .setUsername(parts[1].trim());
            this.players.add(p);
          }
        }
      }
      this.lastModification = this.file.lastModified();
    }
  }

  private void reloadFile() throws IOException {
    if (this.lastModification != this.file.lastModified()) this.readFile();
  }
}
