package ch.admin.bar.siardsuite;

import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.service.DatabaseLoadService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Controller {

  private final Model model;
  private DatabaseLoadService databaseLoadService;

  public Controller(Model model) {
    this.model = model;
  }

  public void setDatabaseType(String databaseType) {
    // TODO dbDriver should be loaded allready here - if it fails -> Toast it ...
    model.setDatabaseType(databaseType);
  }


  public DatabaseLoadService loadDatabase() {
    Connection connection = null;
    try {
      loadDriver(model.getConnectionUrl().get());
      connection = DriverManager.getConnection(model.getConnectionUrl().get() + "?useInformationSchema=false", model.getDatabaseUsername().get(), model.getDatabasePassword());
    } catch (SQLException e) {
      // TODO should notify user about error and navigate back to last view
      throw new RuntimeException(e);
    }
    databaseLoadService = new DatabaseLoadService(connection, model.getArchiveImpl());
    databaseLoadService.start();
    databaseLoadService.setOnSucceeded(event -> model.setDatabaseData(databaseLoadService.valueProperty()));
    return databaseLoadService;
  }

  private void loadDriver(String connectionUrl) {
    Properties properties = new Properties();
    try (InputStream is = getClass().getResourceAsStream("application.properties")) {
      properties.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (connectionUrl.startsWith("jdbc")) {
      String sSubScheme = connectionUrl.substring("jdbc".length() + 1);
      int iSubScheme = sSubScheme.indexOf(":");
      if (iSubScheme >= 0) {
        sSubScheme = sSubScheme.substring(0, iSubScheme);
        String sJdbcDriverClass = properties.getProperty(sSubScheme);
        if (sJdbcDriverClass != null) {
          try {
            Class.forName(sJdbcDriverClass);
          } catch (ClassNotFoundException var7) {
            throw new RuntimeException("Driver " + sJdbcDriverClass + " could not be loaded!");
          }
        } else {
          throw new RuntimeException("No driver found for sub scheme \"" + sSubScheme + "\"!");
        }
      }
    }
  }

  public void updateConnectionData(String connectionUrl, String username, String databaseName, String password) {
    this.model.setConnectionUrl(connectionUrl);
    this.model.setDatabaseName(databaseName);
    this.model.setUsername(username);
    this.model.setPassword(password);
  }
}
