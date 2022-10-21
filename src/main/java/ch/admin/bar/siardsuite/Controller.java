package ch.admin.bar.siardsuite;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.database.DatabaseLoadService;
import ch.admin.bar.siardsuite.database.DbConnectionFactory;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.archive.ArchiveMetaDataVisitor;

import java.io.File;

public class Controller {

  private final Model model;

  private DatabaseLoadService databaseLoadService;

  public Controller(Model model) {
    this.model = model;
  }

  public void setDatabaseType(String databaseType) {
    model.setDatabaseType(databaseType);
  }

  public void loadDatabase(boolean onlyMetaData) {
    final Archive archive = model.initArchive();
    this.databaseLoadService = DbConnectionFactory.getInstance(model).createDatabaseLoader(archive, onlyMetaData);
    this.databaseLoadService.start();
  }
  public void loadDatabase(File target, boolean onlyMetaData) {
    final Archive archive = model.initArchive(target);
    this.databaseLoadService = DbConnectionFactory.getInstance(model).createDatabaseLoader(archive, onlyMetaData);
    this.databaseLoadService.start();
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
  public void updateArchiveMetaData(String description, String owner, String timeOfOrigin, String archiverName, String archiverContact,
                                    File targetArchive) {
    this.model.getArchive().addArchiveMetaData(description, owner, timeOfOrigin, archiverName, archiverContact, targetArchive);
  }

  public void provideArchiveMetaData(ArchiveMetaDataVisitor visitor) {
    this.model.getArchive().getArchiveMetaData().accept(visitor);
  }

  public DatabaseLoadService getDatabaseLoadService() {
    return databaseLoadService;
  }
}
