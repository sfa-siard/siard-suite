/**
 *
 */
package ch.admin.bar.siard2.jdbc.legacy;

import ch.admin.bar.siard2.jdbc.OracleConnection;
import ch.admin.bar.siard2.jdbc.OracleDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.OracleContainer;

import java.sql.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author jutzs
 *
 */
@Testcontainers
public class OracleDriverTester {

    private static final String sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.OracleDriver";
    private static final String sTEST_ORACLE_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    ;
    private static final String sINVALID_ORACLE_URL = "jdbc:sqlserver://localhost";

    private Driver _driver = null;
    private Connection _conn = null;


    @Container
    public final static OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");

    @BeforeEach
    public void setUp() {
        try {
            Class.forName(sDRIVER_CLASS);
            _driver = DriverManager.getDriver(sTEST_ORACLE_URL);
            _conn = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword());
        } catch (ClassNotFoundException cnfe) {
            fail(cnfe.getClass()
                     .getName() + ": " + cnfe.getMessage());
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            if ((_conn != null) && (!_conn.isClosed())) {
                _conn.close();
            } else {
                fail("Connection cannot be closed!");
            }
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testWrapping() {
        assertSame(OracleDriver.class, _driver.getClass(), "Registration of driver wrapper failed!");
        assertSame(OracleConnection.class, _conn.getClass(), "Choice of connection wrapper failed!");
    }

    @Test
    public void testCompliant() {
        assertSame(true, _driver.jdbcCompliant(), "Oracle driver not JDBC compliant!");
    }

    @Test
    public void testAcceptsURL() {
        try {
            assertSame(true, _driver.acceptsURL(db.getJdbcUrl()), "Valid Oracle URL not accepted!");
            assertSame(false, _driver.acceptsURL(sINVALID_ORACLE_URL), "Invalid Oracle URL accepted!");
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
        assertEquals("12.1", sVersion, "Wrong Oracle version " + sVersion + " found!");
    }

    @Test
    public void testDriverProperties() {
        try {
            DriverPropertyInfo[] apropInfo = _driver.getPropertyInfo(db.getJdbcUrl(), new Properties());
            assertSame(99, apropInfo.length, "Unexpected driver properties!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }
}
