package ch.admin.bar.siard2.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.*;
import java.util.Properties;

import static org.junit.Assert.*;

public class MsSqlDriverTester {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @ClassRule
    public static MSSQLServerContainer<?> mssqlContainer = new MSSQLServerContainer<>(MSSQL_IMAGE)
            .acceptLicense()
            .withPassword(SA_PASSWORD)
            .withUrlParam("encrypt", "false");

    private static String _sDB_URL;
    private static final String sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.MsSqlDriver";
    private static final String sTEST_MSSQL_URL = "jdbc:sqlserver://localhost";
    private static final String sINVALID_MSSQL_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";

    private Driver _driver = null;
    private Connection _conn = null;

    @Before
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

    @After
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
        assertSame("Registration of driver wrapper failed!", MsSqlDriver.class, _driver.getClass());
        assertSame("Choice of connection wrapper failed!", MsSqlConnection.class, _conn.getClass());
    }

    @Test
    public void testCompliant() {
        assertSame("MSSQL driver not JDBC compliant!", true, _driver.jdbcCompliant());
    }

    @Test
    public void testAcceptsURL() {
        try {
            assertSame("Valid MSSQL URL not accepted!", true, _driver.acceptsURL(_sDB_URL));
            assertSame("Invalid MSSQL URL accepted!", false, _driver.acceptsURL(sINVALID_MSSQL_URL));
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testVersion() {
        int iMajorVersion = _driver.getMajorVersion();
        int iMinorVersion = _driver.getMinorVersion();
        String sVersion = String.valueOf(iMajorVersion) + "." + String.valueOf(iMinorVersion);
        assertEquals("Wrong MSSQL version " + sVersion + " found!", "4.2", sVersion);
    }

    @Test
    public void testDriverProperties() {
        try {
            DriverPropertyInfo[] apropInfo = _driver.getPropertyInfo(_sDB_URL, new Properties());
            for (DriverPropertyInfo dpi : apropInfo)
                System.out.println(dpi.name + ": " + dpi.value + " (" + String.valueOf(dpi.description) + ")");
            assertSame("Unexpected driver properties!", 29, apropInfo.length);
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

}
