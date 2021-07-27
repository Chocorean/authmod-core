<div align="center">
<br>
<img
    alt="Authmod logo"
    src="./src/main/resources/logo.png"
    width=200px
/>
<br/>
<h1>Authmod Core</h1>
<strong>Core library of <a href="https://github.com/Chocorean/authmod">authmod</a>.</strong>
</div>
<br/>
<p align="center">
<a href="https://img.shields.io/badge/java-1.8-blue.svg" target="_blank">
    <img src="https://img.shields.io/badge/java-1.8-blue.svg" alt="java version" />
</a>
<a href="https://github.com/Chocorean/authmod/actions" target="_blank">
    <img src="https://github.com/Chocorean/authmod/workflows/build/badge.svg?branch=master" alt="build status"/>
</a>
<a href="https://sonarcloud.io/dashboard?id=Chocorean_authmod-core" target="_blank">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=Chocorean_authmod-core&metric=bugs" alt="bugs"/>
</a>
<a href="https://sonarcloud.io/dashboard?id=Chocorean_authmod-core" target="_blank">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=Chocorean_authmod-core&metric=code_smells" alt="code smells"/>
</a>
<a href="https://sonarcloud.io/dashboard?id=Chocorean_authmod-core" target="_blank">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=Chocorean_authmod-core&metric=sqale_rating" alt="maintainability" />
</a>
<a href="https://sonarcloud.io/dashboard?id=Chocorean_authmod-core" target="_blank">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=Chocorean_authmod-core&metric=vulnerabilities" alt="vulnerabilities" />
</a>
<a href="https://lgtm.com/projects/g/Chocorean/authmod-core/alerts/" target="_blank">
    <img src="https://img.shields.io/lgtm/alerts/g/Chocorean/authmod-core.svg?logo=lgtm&logoWidth=18" alt="vulnerabilities" />
</a>
</p>

## Data sources

Data can be stored in either a SQL database or a sqlite file.

| Features | Registration  | Authentication  | Password change |
| ---------| :-----------: | :-------------: | :-------------: |
| [File strategy](https://github.com/Chocorean/authmod-core/blob/main/src/main/java/io/chocorean/authmod/core/datasource/FileDataSourceStrategy.java) | **✔** | **✔** | **✔** |
| [Database strategy](https://github.com/Chocorean/authmod-core/blob/main/src/main/java/io/chocorean/authmod/core/datasource/DatabaseStrategy.java) | **✔** | **✔** | **✔** |

## Example

```java
import io.chocorean.authmod.core.*;
import io.chocorean.authmod.core.datasource.DataSourceStrategyInterface;
import io.chocorean.authmod.core.datasource.FileDataSourceStrategy;
import io.chocorean.authmod.core.exception.AuthmodError;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

  public static void main(String[] args) throws Exception {
    GuardInterface guard = createGuard();

    // Creates a new player and a payload for its registration
    PlayerInterface player = new Player().setUsername("chocorean");
    PayloadInterface payload = new Payload(player,new String[]{"my-passw0rd", "my-passw0rd"});
    guard.register(payload);

    try {
      // Authenticate that same player
      guard.authenticate(new Payload(player, new String[]{"wrong-passw0rd"}));
      System.out.println(String.format("Hello %s", player.getUsername()));
    } catch(AuthmodError e) {
      System.out.println(String.format("Oups, we cannot authenticate you: %s", e.getMessage()));
    }
  }

  /** initializes a guard that stores data in a sqlite file. */
  private static GuardInterface createGuard() throws Exception {
    File sqliteFile = Files.createTempFile(Main.class.getSimpleName(), "players.sqlite").toFile();
    DataSourceStrategyInterface strategy = new FileDataSourceStrategy(sqliteFile);
    return new DataSourceGuard(strategy);
  }
}
```

## Internationalization

Pull requests for adding i18n are more than welcomed. Please make sure to:
- create ` src/main/resources/assets/authmod/lang/XX_YY.json`.
- update the enum `Language` in [ServerLanguageMap.java](https://github.com/Chocorean/authmod-core/blob/main/src/main/java/io/chocorean/authmod/core/i18n/ServerLanguageMap.java#L22).
- update the `README.md` file with your pseudo (optional but strongly recommended!)

Thank you!


## Contributors

- Baptiste Chocot ([@Chocorean](https://www.github.com/Chocorean/)) or `Sunser#7808` (Discord)
- Yann Prono ([@Mcdostone](https://www.github.com/Mcdostone/))
- [weffermiguel](https://www.curseforge.com/members/weffermiguel) for spanish i18n
- Leonardo Braz ([@lhleonardo](https://www.github.com/lhleonardo)) for brazilian i18n
