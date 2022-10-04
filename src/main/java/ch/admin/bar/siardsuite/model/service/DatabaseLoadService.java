package ch.admin.bar.siardsuite.model.service;

import ch.admin.bar.siardsuite.model.DataTable;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DatabaseLoadService extends Service<ObservableList<DataTable>> {
  @Override
  protected Task<ObservableList<DataTable>> createTask() {
    return new DatabaseLoadTask();
  }
}
