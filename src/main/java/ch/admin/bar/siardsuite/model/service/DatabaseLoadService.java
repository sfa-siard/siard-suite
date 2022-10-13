package ch.admin.bar.siardsuite.model.service;

import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DatabaseLoadService extends Service<ObservableList<DatabaseTable>> {
  @Override
  protected Task<ObservableList<DatabaseTable>> createTask() {
    return new DatabaseLoadTask();
  }
}
