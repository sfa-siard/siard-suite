package ch.admin.bar.siardsuite.service.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.util.ThrowingSupplier;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DatabaseLoadService extends Service<ObservableList<Pair<String, Long>>> {

  private final ThrowingSupplier<Connection> connectionSupplier;
  private final Consumer<Archive> resultConsumer;
  private final Archive archive;
  private final boolean onlyMetaData;
  private final boolean viewsAsTables;

  @Override
  protected Task<ObservableList<Pair<String, Long>>> createTask() {
    return new DatabaseLoadTask(connectionSupplier, resultConsumer, archive, onlyMetaData, viewsAsTables);
  }
}
