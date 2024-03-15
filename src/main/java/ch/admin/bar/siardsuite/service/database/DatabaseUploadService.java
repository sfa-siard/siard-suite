package ch.admin.bar.siardsuite.service.database;

import ch.admin.bar.siard2.api.Archive;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DatabaseUploadService extends Service<String> {

  private final Supplier<Connection> connectionSupplier;
  private final Archive archive;
  private final Map<String, String> schemaNameMappings;

  @Override
  protected Task<String> createTask() {
    return new DatabaseUploadTask(connectionSupplier, archive, schemaNameMappings);
  }
}
