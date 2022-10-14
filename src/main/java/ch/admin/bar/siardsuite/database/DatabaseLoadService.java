package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;

public class DatabaseLoadService extends Service<ObservableList<DatabaseTable>> {

  private Connection connection;
  private Model model;
  private Archive archive;

  public DatabaseLoadService(Connection connection, Model model, Archive archive) {
    this.connection = connection;
    this.model = model;
    this.archive = archive;
  }

  @Override
  protected Task<ObservableList<DatabaseTable>> createTask() {
    return new DatabaseLoadTask(connection, model, archive);
  }
}
