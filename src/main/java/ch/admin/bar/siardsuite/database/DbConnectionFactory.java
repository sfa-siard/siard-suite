package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.Model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnectionFactory {
  private static DbConnectionFactory instance;
  private static Connection connection;
  private static Model model;

  private DbConnectionFactory(Model model) {
    DbConnectionFactory.model = model;
    loadDriver(DbConnectionFactory.model.getDatabaseProps().product());
    try {
      connection = DriverManager.getConnection(model.getConnectionUrl().get(),
              model.getDatabaseUsername().get(), model.getDatabasePassword());
    } catch (SQLException e) {
      // TODO should notify user about any error and navigate back to last view - Toast it # CR 458
      throw new RuntimeException(e);
    }
  }

  public static DbConnectionFactory getInstance(Model model) {
    try {
      if (instance == null || connection.isClosed()) {
        instance = new DbConnectionFactory(model);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return instance;
  }

  public DatabaseLoadService createDatabaseLoader(final Archive archive) {
    return new DatabaseLoadService(connection, model, archive);
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