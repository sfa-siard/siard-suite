package ch.admin.bar.siardsuite.service.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Slf4j
public class DatabaseConnectionFactory {
    public static final DatabaseConnectionFactory INSTANCE = new DatabaseConnectionFactory();

    private static final AtomicReference<EstablishedConnection> CONNECTION_CACHE = new AtomicReference();

    public DatabaseLoadService createDatabaseLoader(
            final DbmsConnectionData connectionData,
            final Consumer<Archive> resultConsumer,
            final Archive archive,
            final boolean onlyMetaData,
            final boolean viewsAsTables
    ) {
        return new DatabaseLoadService(
                () -> getOrCreateConnection(connectionData),
                resultConsumer,
                archive,
                onlyMetaData,
                viewsAsTables);
    }

    public DatabaseUploadService createDatabaseUploader(
            final DbmsConnectionData connectionData,
            final Archive archive,
            final Map<String, String> schemaNameMappings) {
        return new DatabaseUploadService(
                () -> getOrCreateConnection(connectionData),
                archive,
                schemaNameMappings);
    }

    public static void disconnect() {
        CONNECTION_CACHE.updateAndGet(nullableEstablishedConnection -> {
            if (nullableEstablishedConnection != null) {
                nullableEstablishedConnection.close();
            }

            return null;
        });
    }

    private static Connection getOrCreateConnection(final DbmsConnectionData connectionData) {
        return CONNECTION_CACHE.updateAndGet(establishedConnection -> {
            try {
                if (establishedConnection != null) {
                    if (!establishedConnection.getConnection().isClosed() &&
                            establishedConnection.getDbmsConnectionData().equals(connectionData)) {
                        // connection can be re-used
                        log.info("Re-use previously established connection (Properties: {})", connectionData);
                        return establishedConnection;
                    } else {
                        establishedConnection.close();
                    }
                }

                return new EstablishedConnection(createConnection(connectionData), connectionData);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).getConnection();
    }

    private static Connection createConnection(final DbmsConnectionData connectionData) throws SQLException {
        log.info("Create new connection (Properties: {})", connectionData);

        loadDriver(connectionData.getDbms().getDriverClassName());

        val options = UserPreferences.INSTANCE.getStoredOptions();
        DriverManager.setLoginTimeout(options.getLoginTimeout());

        val connection = DriverManager.getConnection(
                connectionData.getJdbcConnectionString(),
                connectionData.getUser(),
                connectionData.getPassword());
        connection.setAutoCommit(false);

        return connection;
    }

    private static void loadDriver(String jdbcDriverClass) {
        try {
            Class.forName(jdbcDriverClass);
        } catch (ClassNotFoundException var7) {
            throw new RuntimeException("Driver " + jdbcDriverClass + " could not be loaded!");
        }
    }

    @Value
    private static class EstablishedConnection {
        @NonNull Connection connection;
        @NonNull DbmsConnectionData dbmsConnectionData;

        public void close() {
            try {
                log.info("Close previously established connection (Properties: {})", dbmsConnectionData);
                connection.close();
            } catch (SQLException e) {
                log.error("Can not close established connection for properties {} cause '{}'",
                        dbmsConnectionData,
                        e.getMessage());
            }
        }
    }
}
