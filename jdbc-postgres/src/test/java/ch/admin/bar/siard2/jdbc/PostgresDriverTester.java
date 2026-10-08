package ch.admin.bar.siard2.jdbc;


import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assertions;

public class PostgresDriverTester {
    private static final PostgreSQLContainer<?> _pg = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    private static String _sDB_URL;
    private static String _sDB_USER;
    private static String _sDB_PASSWORD;
    private static final String sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.PostgresDriver";
    private static String sTEST_POSTGRES_URL;
    private static final String sINVALID_POSTGRES_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";

    private Driver _driver = null;
    private Connection _conn = null;

    @BeforeAll
    public static void setUpClass() {
        _pg.start();
        _sDB_URL = PostgresDriver.getUrl(_pg.getHost() + ":" + _pg.getFirstMappedPort() + "/" + _pg.getDatabaseName());
        _sDB_USER = _pg.getUsername();
        _sDB_PASSWORD = _pg.getPassword();
        sTEST_POSTGRES_URL = "jdbc:postgresql://" + _pg.getHost() + ":" + _pg.getFirstMappedPort();
    }

    @AfterAll
    public static void tearDownClass() {
        _pg.stop();
    }

    @BeforeEach
    public void setUp() {
        try {
            Class.forName(sDRIVER_CLASS);
        } catch (ClassNotFoundException cnfe) {
            fail(cnfe.getClass()
                     .getName() + ": " + cnfe.getMessage());
        }
        try {
            _driver = DriverManager.getDriver(sTEST_POSTGRES_URL);
            _conn = DriverManager.getConnection(_sDB_URL, _sDB_USER, _sDB_PASSWORD);
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            if ((_conn != null) && (!_conn.isClosed()))
                _conn.close();
            else
                fail("Connection cannot be closed!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testWrapping() {
        assertSame(PostgresDriver.class, _driver.getClass(), "Registration of driver wrapper failed!");
        assertSame(PostgresConnection.class, _conn.getClass(), "Choice of connection wrapper failed!");
    }

    @Test
    public void testCompliant() {
        assertSame(false, _driver.jdbcCompliant(), "Postgres driver is suddenly JDBC compliant!");
    }

    @Test
    public void testAcceptsURL() {
        try {
            assertSame(true, _driver.acceptsURL(_sDB_URL), "Valid Postgres URL not accepted!");
            assertSame(false, _driver.acceptsURL(sINVALID_POSTGRES_URL), "Invalid Postgres URL accepted!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testVersion() {
        int iMajorVersion = _driver.getMajorVersion();
        int iMinorVersion = _driver.getMinorVersion();
        String sVersion = iMajorVersion + "." + iMinorVersion;
        assertEquals("42.7", sVersion, "Wrong Postgres version " + sVersion + " found!");
    }
}
