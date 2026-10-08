package ch.admin.bar.siard2.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MsSqlDriverTest {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @Container
    public static MSSQLServerContainer<?> mssqlContainer = new MSSQLServerContainer<>(MSSQL_IMAGE)
            .acceptLicense()
            .withPassword(SA_PASSWORD)
            .withUrlParam("trustServerCertificate", "true");

    private static String _sDB_URL;
    private static final String sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.MsSqlDriver";
    private static final String sTEST_MSSQL_URL = "jdbc:sqlserver://localhost";
    private static final String sINVALID_MSSQL_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";

    private Driver _driver = null;
    private Connection _conn = null;

    @BeforeEach
    public void setUp() {
        try {
            Class.forName(sDRIVER_CLASS);
        } catch (ClassNotFoundException cnfe) {
            fail(cnfe.getClass()
                     .getName() + ": " + cnfe.getMessage());
        }
        try {
            _sDB_URL = mssqlContainer.getJdbcUrl();
            _driver = DriverManager.getDriver(sTEST_MSSQL_URL);
            _conn = DriverManager.getConnection(_sDB_URL, mssqlContainer.getUsername(), mssqlContainer.getPassword());
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
        assertSame(MsSqlDriver.class, _driver.getClass(), "Registration of driver wrapper failed!");
        assertSame(MsSqlConnection.class, _conn.getClass(), "Choice of connection wrapper failed!");
    }

    @Test
    public void testCompliant() {
        assertSame(true, _driver.jdbcCompliant(), "MSSQL driver not JDBC compliant!");
    }

    @Test
    public void testAcceptsURL() {
        try {
            assertSame(true, _driver.acceptsURL(_sDB_URL), "Valid MSSQL URL not accepted!");
            assertSame(false, _driver.acceptsURL(sINVALID_MSSQL_URL), "Invalid MSSQL URL accepted!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }
}
