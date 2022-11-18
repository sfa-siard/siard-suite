package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.Model;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.sql.Connection;

public class DatabaseLoadService extends Service<ObservableList<Pair<String, Long>>> {

  private final Connection connection;
  private final Model model;
  private final Archive archive;
  private final boolean onlyMetaData;

  public DatabaseLoadService(Connection connection, Model model, Archive archive, boolean onlyMetaData) {
    this.connection = connection;
    this.model = model;
    this.archive = archive;
    this.onlyMetaData = onlyMetaData;
  }

  @Override
  protected Task<ObservableList<Pair<String, Long>>> createTask() {
    return new DatabaseLoadTask(connection, model, archive, onlyMetaData);
  }
}
