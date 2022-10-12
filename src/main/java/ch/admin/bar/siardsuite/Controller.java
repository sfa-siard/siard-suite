package ch.admin.bar.siardsuite;

import ch.admin.bar.siardsuite.model.Model;

public class Controller {

  private final Model model;

  public Controller(Model model) {
    this.model = model;
  }

  public void setDatabaseType(String databaseType) {
    model.setDatabaseType(databaseType);
  }


  public void loadDatabase() {
    model.loadDatabase();
  }

  public void closeDbConnection() {
    model.closeDbConnection();
  }

  public void updateConnectionData(String connectionUrl, String username, String databaseName, String password) {
    this.model.setConnectionUrl(connectionUrl);
    this.model.setDatabaseName(databaseName);
    this.model.setUsername(username);
    this.model.setPassword(password);
  }
}
