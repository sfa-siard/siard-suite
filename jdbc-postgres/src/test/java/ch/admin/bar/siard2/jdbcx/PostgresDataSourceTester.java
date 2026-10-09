package ch.admin.bar.siard2.jdbcx;

import ch.admin.bar.siard2.jdbc.PostgresConnection;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Assertions;

public class PostgresDataSourceTester {
    // see https://jdbc.postgresql.org/documentation/head/connect.html
    private static final PostgreSQLContainer<?> _pg = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    private static String _sDB_URL;
    private static String _sDB_USER;
    private static String _sDB_PASSWORD;

    @BeforeAll
    public static void setUpClass() {
        _pg.start();
        _sDB_URL = "jdbc:postgresql://" + _pg.getHost() + ":" + _pg.getFirstMappedPort() + "/" + _pg.getDatabaseName();
        _sDB_USER = _pg.getUsername();
        _sDB_PASSWORD = _pg.getPassword();
    }

    @AfterAll
    public static void tearDownClass() {
        _pg.stop();
    }

    private PostgresDataSource _dsPostgres = null;
    private Connection _conn = null;

    @BeforeEach
    public void setUp() {
        _dsPostgres = new PostgresDataSource();
    }

    @AfterEach
    public void tearDown() {
        try {
            if ((_conn != null) && (!_conn.isClosed()))
                _conn.close();
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testWrapper() {
        try {
            Assertions.assertSame(true, _dsPostgres.isWrapperFor(DataSource.class), "Invalid wrapper!");
            DataSource dsWrapped = _dsPostgres.unwrap(DataSource.class);
            assertSame(org.postgresql.ds.PGSimpleDataSource.class, dsWrapped.getClass(), "Invalid wrapped class!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testGetConnection() {
        _dsPostgres.setUrl(_sDB_URL);
        _dsPostgres.setUser(_sDB_USER);
        _dsPostgres.setPassword(_sDB_PASSWORD);
        try {
            _conn = _dsPostgres.getConnection();
            if (_conn.unwrap(Connection.class) instanceof PostgresConnection)
                fail("Double wrap!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

}
