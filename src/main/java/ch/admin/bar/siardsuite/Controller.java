package ch.admin.bar.siardsuite;

import java.sql.*;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.service.DatabaseLoadService;

public class Controller {

  private final Model model;
  private DatabaseLoadService databaseLoadService;

  public Controller(Model model) {
    this.model = model;
  }

  public void setDatabaseType(String databaseType) {
    model.setDatabaseType(databaseType);
  }


  public DatabaseLoadService loadDatabase() {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(model.getConnectionUrl().get(), model.getDatabaseUsername().get(), model.getDatabasePassword());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    databaseLoadService = new DatabaseLoadService(connection, model.getArchiveImpl());
    databaseLoadService.start();
    databaseLoadService.setOnSucceeded(event -> model.setDatabaseData(databaseLoadService.valueProperty()));
    return databaseLoadService;
  }

  public void updateConnectionData(String connectionUrl, String username, String databaseName, String password) {
    this.model.setConnectionUrl(connectionUrl);
    this.model.setDatabaseName(databaseName);
    this.model.setUsername(username);
    this.model.setPassword(password);
  }
}
