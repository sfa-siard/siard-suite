package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.util.Map;

@RequiredArgsConstructor
public class DatabaseUploadService extends Service<String> {

  private final Connection connection;
  private final Archive archive;
  private final Map<String, String> schemaNameMappings;

  @Override
  protected Task<String> createTask() {
    return new DatabaseUploadTask(connection, archive, schemaNameMappings);
  }
}
