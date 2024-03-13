package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.Model;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DatabaseLoadService extends Service<ObservableList<Pair<String, Long>>> {

  private final Supplier<Connection> connectionSupplier;
  private final Model model;
  private final String dbName;
  private final Archive archive;
  private final boolean onlyMetaData;
  private final boolean viewsAsTables;

  @Override
  protected Task<ObservableList<Pair<String, Long>>> createTask() {
    return new DatabaseLoadTask(connectionSupplier, model, dbName, archive, onlyMetaData, viewsAsTables);
  }
}
