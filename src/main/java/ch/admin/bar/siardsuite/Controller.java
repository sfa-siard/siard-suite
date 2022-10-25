package ch.admin.bar.siardsuite;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.database.DatabaseLoadService;
import ch.admin.bar.siardsuite.database.DbConnectionFactory;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveVisitor;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

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

  public void loadDatabase(boolean onlyMetaData,EventHandler<WorkerStateEvent> onSuccess,
                           EventHandler<WorkerStateEvent> onFailure
                           ) {
    final Archive archive = model.initArchive();
    this.databaseLoadService = DbConnectionFactory.getInstance(model).createDatabaseLoader(archive, onlyMetaData);
    this.onDatabaseLoadSuccess(onSuccess);
    this.onDatabaseLoadFailed(onFailure);
    this.databaseLoadService.start();

  }
  public void loadDatabase(File target, boolean onlyMetaData, EventHandler<WorkerStateEvent> onSuccess,
                           EventHandler<WorkerStateEvent> onFailure) {
    final Archive archive = model.initArchive(target);
    this.databaseLoadService = DbConnectionFactory.getInstance(model).createDatabaseLoader(archive, onlyMetaData);
    this.onDatabaseLoadSuccess(onSuccess);
    this.onDatabaseLoadFailed(onFailure);
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

  public void onDatabaseLoadSuccess(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
    this.databaseLoadService.setOnSucceeded(workerStateEventEventHandler);
  }

  public void onDatabaseLoadFailed(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
    this.databaseLoadService.setOnFailed(workerStateEventEventHandler);
  }

  public void addDatabaseLoadingValuePropertyListener(ChangeListener<ObservableList<DatabaseTable>> listener) {
    this.databaseLoadService.valueProperty().addListener(listener);
  }

  public void addDatabaseLoadingProgressPropertyListener(ChangeListener<Number> listener )  {
    this.databaseLoadService.progressProperty().addListener(listener);
  }

  // TODO: maybe use some sort of visitor or provider or...
  public void updateArchiveMetaData(String siardFormatVersion, String dbName, String dbProduct, String connection,
                                    String dbUser, String description, String owner, String databaseCreationDate,
                                    String archivingDate, String archiverName, String archiverContact, File targetArchive) {
    this.model.getArchive().addArchiveMetaData(siardFormatVersion, dbName, dbProduct, connection, dbUser,
            description, owner, databaseCreationDate, archivingDate, archiverName, archiverContact, targetArchive);
  }

  public void provideDatabaseArchive(DatabaseArchiveVisitor visitor) {
    if (model.getArchive() != null) {
      model.getArchive().accept(visitor);
    }
  }

  public void provideDatabaseArchiveMetaData(DatabaseArchiveMetaDataVisitor visitor) {
    if (model.getArchive() != null) {
      model.getArchive().accept(visitor);
    }
  }

}
