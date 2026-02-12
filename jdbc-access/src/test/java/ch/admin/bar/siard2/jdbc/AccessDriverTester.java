package ch.admin.bar.siard2.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.*;
import java.util.Properties;

import static org.junit.Assert.*;

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

    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        Class.forName(_sDRIVER_CLASS);
        _driver = DriverManager.getDriver(_sDB_URL);
        _conn = DriverManager.getConnection(_sDB_URL, _sDB_USER, _sDB_PASSWORD);
    }

    @After
    public void tearDown() throws SQLException {
        if ((_conn != null) && (!_conn.isClosed())) _conn.close();
        else fail("Connection cannot be closed!");
    }

    @Test
    public void testWrapping() {
        assertSame("Registration of driver wrapper failed!", AccessDriver.class, _driver.getClass());
        assertSame("Choice of connection wrapper failed!", AccessConnection.class, _conn.getClass());
    }

    @Test
    public void testCompliant() {
        assertSame("Access driver not JDBC compliant!", true, _driver.jdbcCompliant());
    }

    @Test
    public void testAcceptsURL() throws SQLException {
        assertSame("Valid Access URL not accepted!", true, _driver.acceptsURL(_sDB_URL));
        assertSame("Invalid Access URL accepted!", false, _driver.acceptsURL(_sINVALID_ACCESS_URL));
    }

    @Test
    public void testVersion() {
        int iMajorVersion = _driver.getMajorVersion();
        int iMinorVersion = _driver.getMinorVersion();
        String sVersion = iMajorVersion + "." + iMinorVersion;
        assertEquals("Wrong Access Driver version " + sVersion + " found!", AccessDriver.sVERSION, sVersion);
    }

    @Test
    public void testDriverProperties() throws SQLException {
        Properties props = new Properties();
        props.setProperty(AccessDriver.sPROP_USER, "TheUser");
        props.setProperty(AccessDriver.sPROP_PASSWORD, "ThePassword");
        props.setProperty(AccessDriver.sPROP_READ_ONLY, "false");
        DriverPropertyInfo[] apropInfo = _driver.getPropertyInfo(_sDB_URL, props);
        for (DriverPropertyInfo dpi : apropInfo)
            System.out.println(dpi.name + ": " + dpi.value + " (" + dpi.description + ")");
        assertSame("Unexpected driver properties!", 3, apropInfo.length);
    }

}
