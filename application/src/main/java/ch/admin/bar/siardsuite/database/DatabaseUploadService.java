package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siardsuite.model.Model;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.Connection;

public class DatabaseUploadService extends Service<String> {

  private final Connection connection;
  private final Model model;

  public DatabaseUploadService(Connection connection, Model model) {
    this.connection = connection;
    this.model = model;
  }


  @Override
  protected Task<String> createTask() {
    return new DatabaseUploadTask(connection, model);
  }
}
