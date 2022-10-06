package ch.admin.bar.siardsuite;

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
    databaseLoadService = new DatabaseLoadService();
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
