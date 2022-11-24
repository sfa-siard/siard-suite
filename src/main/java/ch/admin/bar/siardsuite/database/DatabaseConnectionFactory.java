package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.UserPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.LOGIN_TIMEOUT;
import static ch.admin.bar.siardsuite.util.UserPreferences.NodePath.OPTIONS;

public class DatabaseConnectionFactory {
  private static DatabaseConnectionFactory instance;
  private static Connection connection;
  private static Model model;

  private DatabaseConnectionFactory(Model model) throws SQLException {
    DatabaseConnectionFactory.model = model;
    loadDriver(DatabaseConnectionFactory.model.getDatabaseProps().product());
    DriverManager.setLoginTimeout(Integer.parseInt(UserPreferences.node(OPTIONS).get(LOGIN_TIMEOUT.name(), "0")));
    connection = DriverManager.getConnection(model.getConnectionUrl().get(),
            model.getDatabaseUsername().get(), model.getDatabasePassword());
  }

  public static DatabaseConnectionFactory getInstance(Model model) throws SQLException {
    if (instance == null || connection.isClosed()) {
      instance = new DatabaseConnectionFactory(model);
    }
    return instance;
  }

  public DatabaseLoadService createDatabaseLoader(final Archive archive, boolean onlyMetaData) {
    return new DatabaseLoadService(connection, model, archive, onlyMetaData);
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

  private void loadDriver(String product) {
    Properties properties = new Properties();
    try (InputStream is = getClass().getResourceAsStream("driver.properties")) {
      properties.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String jdbcDriverClass = properties.getProperty(product);
    if (jdbcDriverClass != null) {
      try {
        Class.forName(jdbcDriverClass);
      } catch (ClassNotFoundException var7) {
        throw new RuntimeException("Driver " + jdbcDriverClass + " could not be loaded!");
      }
    } else {
      throw new RuntimeException("No driver found for sub scheme \"" + product + "\"!");
    }

  }

}
