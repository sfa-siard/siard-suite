package ch.admin.bar.siard2.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class AccessDriverTester {
    // private static final File fileTEST_EMPTY_DATABASE = new File(_cp.getInstance()+"/"+_cp.getCatalog());
    private static final File fileTEST_EMPTY_DATABASE = new File("src/test/resources/tmp/testempty.accdb");
    private static final String _sDB_URL = AccessDriver.getUrl(fileTEST_EMPTY_DATABASE.getPath());
    private static final String _sDB_USER = "Admin";
    private static final String _sDB_PASSWORD = "";
    private static final String _sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.AccessDriver";
    private static final String _sINVALID_ACCESS_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";

    private Driver _driver = null;
    private Connection _conn = null;

    @BeforeEach
    public void setUp() throws ClassNotFoundException, SQLException {
        Class.forName(_sDRIVER_CLASS);
        _driver = DriverManager.getDriver(_sDB_URL);
        _conn = DriverManager.getConnection(_sDB_URL, _sDB_USER, _sDB_PASSWORD);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if ((_conn != null) && (!_conn.isClosed())) _conn.close();
        else fail("Connection cannot be closed!");
    }

    @Test
    public void testWrapping() {
        assertSame(AccessDriver.class, _driver.getClass(), "Registration of driver wrapper failed!");
        assertSame(AccessConnection.class, _conn.getClass(), "Choice of connection wrapper failed!");
    }

    @Test
    public void testCompliant() {
        assertSame(true, _driver.jdbcCompliant(), "Access driver not JDBC compliant!");
    }

    @Test
    public void testAcceptsURL() throws SQLException {
        assertSame(true, _driver.acceptsURL(_sDB_URL), "Valid Access URL not accepted!");
        assertSame(false, _driver.acceptsURL(_sINVALID_ACCESS_URL), "Invalid Access URL accepted!");
    }

    @Test
    public void testVersion() {
        // delegation check only — don't pin the driver's version
        assertTrue(_driver.getMajorVersion() > 0, "Driver reports no major version!");
        assertTrue(_driver.getMinorVersion() >= 0, "Driver reports no minor version!");
    }

    @Test
    public void testDriverProperties() throws SQLException {
        Properties props = new Properties();
        props.setProperty(AccessDriver.sPROP_USER, "TheUser");
        props.setProperty(AccessDriver.sPROP_PASSWORD, "ThePassword");
        props.setProperty(AccessDriver.sPROP_READ_ONLY, "false");
        DriverPropertyInfo[] apropInfo = _driver.getPropertyInfo(_sDB_URL, props);
        assertTrue(Arrays.stream(apropInfo).anyMatch(p -> AccessDriver.sPROP_USER.equals(p.name)),
                   "Expected property " + AccessDriver.sPROP_USER + " missing!");
        assertTrue(Arrays.stream(apropInfo).anyMatch(p -> AccessDriver.sPROP_PASSWORD.equals(p.name)),
                   "Expected property " + AccessDriver.sPROP_PASSWORD + " missing!");
        assertTrue(Arrays.stream(apropInfo).anyMatch(p -> AccessDriver.sPROP_READ_ONLY.equals(p.name)),
                   "Expected property " + AccessDriver.sPROP_READ_ONLY + " missing!");
    }

}
