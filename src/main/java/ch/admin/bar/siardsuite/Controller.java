package ch.admin.bar.siardsuite;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.database.DatabaseConnectionFactory;
import ch.admin.bar.siardsuite.database.DatabaseLoadService;
import ch.admin.bar.siardsuite.database.DatabaseUploadService;
import ch.admin.bar.siardsuite.model.Model;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Pair;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;

public class Controller {

  private final Model model;

  private DatabaseLoadService databaseLoadService;
  private DatabaseUploadService databaseUploadService;

  private Workflow workflow;

  public String recentDatabaseConnection;

  public Controller(Model model) {
    this.model = model;
  }

  public void setDatabaseType(String databaseType) {
    model.setDatabaseType(databaseType);
  }

  public void loadDatabase(boolean onlyMetaData,EventHandler<WorkerStateEvent> onSuccess,
                           EventHandler<WorkerStateEvent> onFailure
                           ) throws SQLException {
    final Archive archive = model.initArchive();
    this.databaseLoadService = DatabaseConnectionFactory.getInstance(model).createDatabaseLoader(archive, onlyMetaData);
    this.onDatabaseLoadSuccess(onSuccess);
    this.onDatabaseLoadFailed(onFailure);
    this.databaseLoadService.start();
  }
  public void loadDatabase(File target, boolean onlyMetaData, EventHandler<WorkerStateEvent> onSuccess,
                           EventHandler<WorkerStateEvent> onFailure) throws SQLException {
    final Archive archive = model.initArchive(target);
    this.databaseLoadService = DatabaseConnectionFactory.getInstance(model).createDatabaseLoader(archive, onlyMetaData);
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

  public void updateSchemaMap(Map schemaMap) {
    this.model.setSchemaMap(schemaMap);
  }

  public void onDatabaseLoadSuccess(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
    this.databaseLoadService.setOnSucceeded(workerStateEventEventHandler);
  }

  public void onDatabaseLoadFailed(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
    this.databaseLoadService.setOnFailed(workerStateEventEventHandler);
  }

  public void addDatabaseLoadingValuePropertyListener(ChangeListener<ObservableList<Pair<String, Long>>> listener) {
    this.databaseLoadService.valueProperty().addListener(listener);
  }

  public void addDatabaseLoadingProgressPropertyListener(ChangeListener<Number> listener )  {
    this.databaseLoadService.progressProperty().addListener(listener);
  }

  public void onDatabaseUploadSuccess(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
    this.databaseUploadService.setOnSucceeded(workerStateEventEventHandler);
  }

  public void onDatabaseUploadFailed(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
    this.databaseUploadService.setOnFailed(workerStateEventEventHandler);
  }

  public void addDatabaseUploadingValuePropertyListener(ChangeListener<String> listener) {
    this.databaseUploadService.valueProperty().addListener(listener);
  }

  public void addDatabaseUploadingProgressPropertyListener(ChangeListener<Number> listener )  {
    this.databaseUploadService.progressProperty().addListener(listener);
  }

  public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  public void uploadArchive(EventHandler<WorkerStateEvent> onSuccess, EventHandler<WorkerStateEvent> onFailure) throws SQLException {
    this.databaseUploadService = DatabaseConnectionFactory.getInstance(model).createDatabaseUploader();
    this.onDatabaseUploadSuccess(onSuccess);
    this.onDatabaseUploadFailed(onFailure);
    this.databaseUploadService.start();
  }
}
