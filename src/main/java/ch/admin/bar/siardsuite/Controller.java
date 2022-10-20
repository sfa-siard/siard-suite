package ch.admin.bar.siardsuite;

import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.archive.ArchiveMetaDataVisitor;

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

  // TODO: maybe use some sort of visitor or provider or...
  public void updateArchiveMetaData(String description, String owner, String timeOfOrigin, String archiverName, String archiverContact) {
    this.model.getArchive().addArchiveMetaData(description, owner, timeOfOrigin, archiverName, archiverContact);
  }

  public void provideArchiveMetaData(ArchiveMetaDataVisitor visitor) {
    this.model.getArchive().getArchiveMetaData().accept(visitor);
  }
}
