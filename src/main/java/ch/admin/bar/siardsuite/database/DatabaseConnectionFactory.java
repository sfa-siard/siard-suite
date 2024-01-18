package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DatabaseConnectionFactory {

    public Connection createConnection(final DbmsConnectionData dbmsConnectionData) {
        val options = UserPreferences.getStoredOptions();

        loadDriverByClassName(dbmsConnectionData.getDbms().getDriverClassName());
        DriverManager.setLoginTimeout(options.getLoginTimeout());

        try {
            return DriverManager.getConnection(
                    dbmsConnectionData.getJdbcConnectionString(),
                    dbmsConnectionData.getUser(),
                    dbmsConnectionData.getPassword());
        } catch (SQLException e) {
            throw new DatabaseConnectionException(
                    String.format("Failed to create connection '%s'", dbmsConnectionData),
                    e);
        }
    }

    private static void loadDriverByClassName(String jdbcDriverClass) {
        try {
            Class.forName(jdbcDriverClass);
        } catch (ClassNotFoundException var7) {
            throw new DatabaseConnectionException("Driver " + jdbcDriverClass + " could not be loaded!");
        }
    }

    private static class DatabaseConnectionException extends RuntimeException {
        public DatabaseConnectionException(String message) {
            super(message);
        }

        public DatabaseConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
