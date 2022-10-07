package ch.admin.bar.siardsuite.model.service;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.DataTable;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;

public class DatabaseLoadService extends Service<ObservableList<DataTable>> {

  private Connection connection;
  private Archive archiveImpl;

  public DatabaseLoadService(Connection connection, Archive archiveImpl) {
    this.connection = connection;
    this.archiveImpl = archiveImpl;
  }

  @Override
  protected Task<ObservableList<DataTable>> createTask() {

    return new DatabaseLoadTask(connection, archiveImpl);
  }
}
