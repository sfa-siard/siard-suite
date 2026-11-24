package ch.admin.bar.siardsuite.localwebapp.connections;

import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link ConnectionStore} on a single SQLite file (design D3): one lazily opened connection is
 * enough for a local single-writer app, and a store that cannot be opened fails the request with
 * a problem-details 500 instead of keeping the agent down.
 * <p>
 * The database file defaults to the per-OS application-data directory ({@link ConnectionPaths});
 * {@code siard.connections.db} overrides it, which the tests use for a temp-dir database.
 */
@ApplicationScoped
public class SqliteConnectionStore implements ConnectionStore {

    private static final Logger LOG = LoggerFactory.getLogger(SqliteConnectionStore.class);

    // DriverManager's service loading races with the first connection in tests; registering the
    // driver explicitly makes "jdbc:sqlite:" resolvable deterministically
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS saved_connections (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                name        TEXT NOT NULL UNIQUE,
                dbms_id     TEXT NOT NULL,
                host        TEXT,
                port        TEXT,
                db_name     TEXT,
                user_name   TEXT,
                options     TEXT,
                file        TEXT,
                created_at  TEXT NOT NULL,
                updated_at  TEXT NOT NULL
            )""";

    private static final String COLUMNS =
            "id, name, dbms_id, host, port, db_name, user_name, options, file, created_at, updated_at";

    private final Path databaseFile;
    private final Clock clock;
    private Connection connection;

    @Inject
    public SqliteConnectionStore(
            @ConfigProperty(name = "siard.connections.db") Optional<String> databaseOverride,
            Clock clock) {
        this(databaseOverride.map(Path::of).orElseGet(SqliteConnectionStore::defaultDatabaseFile), clock);
    }

    /** Visible for tests: a store on an explicit database file. */
    public SqliteConnectionStore(Path databaseFile, Clock clock) {
        this.databaseFile = databaseFile.toAbsolutePath().normalize();
        this.clock = clock;
    }

    private static Path defaultDatabaseFile() {
        return ConnectionPaths.databaseFile(
                System.getProperty("os.name"),
                System.getenv(),
                Path.of(System.getProperty("user.home")));
    }

    /** Opens the database and creates the schema at startup; failures are logged, not fatal. */
    void initialize(@Observes StartupEvent event) {
        try {
            connection();
        } catch (RuntimeException e) {
            LOG.error("Could not initialize the connection store at {}", databaseFile, e);
        }
    }

    @PreDestroy
    void close() {
        synchronized (this) {
            closeQuietly();
        }
    }

    @Override
    public synchronized List<SavedConnection> list() {
        try (PreparedStatement statement = connection().prepareStatement(
                "SELECT " + COLUMNS + " FROM saved_connections ORDER BY name")) {
            try (ResultSet rows = statement.executeQuery()) {
                List<SavedConnection> connections = new ArrayList<>();
                while (rows.next()) {
                    connections.add(map(rows));
                }
                return connections;
            }
        } catch (SQLException e) {
            throw new ConnectionStoreException("Could not list saved connections", e);
        }
    }

    @Override
    public synchronized SavedConnection create(NewConnection connection) {
        Instant now = clock.instant();
        String insert = "INSERT INTO saved_connections (name, dbms_id, host, port, db_name,"
                + " user_name, options, file, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection().prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            bindFields(statement, connection);
            statement.setString(9, now.toString());
            statement.setString(10, now.toString());
            statement.executeUpdate();
            long id = generatedId(statement);
            return new SavedConnection(
                    id,
                    connection.name(),
                    connection.dbmsId(),
                    connection.host(),
                    connection.port(),
                    connection.dbName(),
                    connection.user(),
                    connection.options(),
                    connection.file(),
                    now,
                    now);
        } catch (SQLException e) {
            if (isDuplicateName(e)) {
                throw new DuplicateConnectionNameException(connection.name(), e);
            }
            throw new ConnectionStoreException("Could not save the connection", e);
        }
    }

    @Override
    public synchronized Optional<SavedConnection> update(long id, NewConnection connection) {
        String update = "UPDATE saved_connections SET name = ?, dbms_id = ?, host = ?, port = ?,"
                + " db_name = ?, user_name = ?, options = ?, file = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement statement = connection().prepareStatement(update)) {
            bindFields(statement, connection);
            statement.setString(9, clock.instant().toString());
            statement.setLong(10, id);
            if (statement.executeUpdate() == 0) {
                return Optional.empty();
            }
        } catch (SQLException e) {
            if (isDuplicateName(e)) {
                throw new DuplicateConnectionNameException(connection.name(), e);
            }
            throw new ConnectionStoreException("Could not update the connection", e);
        }
        return find(id);
    }

    private Optional<SavedConnection> find(long id) {
        try (PreparedStatement statement = connection().prepareStatement(
                "SELECT " + COLUMNS + " FROM saved_connections WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(map(rows)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ConnectionStoreException("Could not read the connection", e);
        }
    }

    @Override
    public synchronized boolean delete(long id) {
        try (PreparedStatement statement = connection().prepareStatement(
                "DELETE FROM saved_connections WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ConnectionStoreException("Could not delete the connection", e);
        }
    }

    /**
     * The lazily opened connection; the first call creates the directory, file and schema. The
     * field is only set once schema and permissions are in place, so a failed open is retried.
     */
    private Connection connection() {
        if (connection == null || isClosed()) {
            Connection opened = null;
            try {
                Path directory = databaseFile.getParent();
                if (directory != null) {
                    ConnectionPaths.createUserOnlyDirectory(directory);
                }
                opened = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
                try (Statement statement = opened.createStatement()) {
                    statement.execute(CREATE_TABLE);
                }
                ConnectionPaths.restrictFileToOwner(databaseFile);
                connection = opened;
            } catch (SQLException | IOException e) {
                closeQuietly(opened);
                throw new ConnectionStoreException("Could not open the connection store at " + databaseFile, e);
            }
        }
        return connection;
    }

    private boolean isClosed() {
        try {
            return connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    private long generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getLong(1);
            }
        }
        try (PreparedStatement lastId = connection().prepareStatement("SELECT last_insert_rowid()")) {
            try (ResultSet rows = lastId.executeQuery()) {
                return rows.next() ? rows.getLong(1) : -1;
            }
        }
    }

    /** Binds the eight profile columns shared by INSERT and UPDATE as parameters 1-8. */
    private static void bindFields(PreparedStatement statement, NewConnection connection) throws SQLException {
        statement.setString(1, connection.name());
        statement.setString(2, connection.dbmsId());
        statement.setString(3, connection.host());
        statement.setString(4, connection.port());
        statement.setString(5, connection.dbName());
        statement.setString(6, connection.user());
        statement.setString(7, connection.options());
        statement.setString(8, connection.file());
    }

    // name is the table's only UNIQUE column
    private static boolean isDuplicateName(SQLException e) {
        return e instanceof SQLiteException sqliteException
                && sqliteException.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE;
    }

    private static SavedConnection map(ResultSet rows) throws SQLException {
        return new SavedConnection(
                rows.getLong("id"),
                rows.getString("name"),
                rows.getString("dbms_id"),
                rows.getString("host"),
                rows.getString("port"),
                rows.getString("db_name"),
                rows.getString("user_name"),
                rows.getString("options"),
                rows.getString("file"),
                Instant.parse(rows.getString("created_at")),
                Instant.parse(rows.getString("updated_at")));
    }

    private void closeQuietly() {
        closeQuietly(connection);
        connection = null;
    }

    private static void closeQuietly(Connection toClose) {
        if (toClose != null) {
            try {
                toClose.close();
            } catch (SQLException e) {
                LOG.debug("Could not close the connection store", e);
            }
        }
    }
}
