package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionFactory {
  private static DatabaseConnectionFactory instance;
  private static Connection connection;
  private static Model model;

  private final DbmsConnectionData connectionData;

  private DatabaseConnectionFactory(Model model, DbmsConnectionData connectionData) throws SQLException {
    this.connectionData = connectionData;

    DatabaseConnectionFactory.model = model;
    val options = UserPreferences.INSTANCE.getStoredOptions();

    loadDriver(connectionData.getDbms().getDriverClassName());

    DriverManager.setLoginTimeout(options.getLoginTimeout());
    connection = DriverManager.getConnection(
            connectionData.getJdbcConnectionString(),
            connectionData.getUser(),
            connectionData.getPassword());
  }

  public static DatabaseConnectionFactory getInstance(Model model, DbmsConnectionData connectionData) throws SQLException {
    if (instance == null || connection.isClosed()) {
      instance = new DatabaseConnectionFactory(model, connectionData);
    }
    return instance;
  }

  public DatabaseLoadService createDatabaseLoader(final Archive archive, boolean onlyMetaData, boolean viewsAsTables) {
    return new DatabaseLoadService(connection, model, connectionData.getDbName(), archive, onlyMetaData, viewsAsTables);
  }

  public DatabaseUploadService createDatabaseUploader() {
    return new DatabaseUploadService(connection, model);
  }

  public static void disconnect() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private void loadDriver(String jdbcDriverClass) {
    try {
      Class.forName(jdbcDriverClass);
    } catch (ClassNotFoundException var7) {
      throw new RuntimeException("Driver " + jdbcDriverClass + " could not be loaded!");
    }
  }
}
