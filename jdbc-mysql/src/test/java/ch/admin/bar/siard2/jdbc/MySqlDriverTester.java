package ch.admin.bar.siard2.jdbc;

import ch.enterag.utils.EU;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.MountableFile;

import java.sql.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assertions;

public class MySqlDriverTester {
    private static final MySQLContainer<?> _mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testschema")
            .withUsername("testuser")
            .withPassword("testpwd")
            .withCopyFileToContainer(MountableFile.forClasspathResource("zzz-test-overrides.cnf"), "/etc/mysql/conf.d/zzz-test-overrides.cnf");

    private static String _sDB_URL;
    private static String _sDB_USER;
    private static String _sDB_PASSWORD;
    private static final String _sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.MySqlDriver";
    private static final String _sINVALID_MYSQL_URL = "jdbc:oracle:thin:@//localhost";

    private Driver _driver = null;
    private Connection _conn = null;

    @BeforeAll
    public static void setUpClass() {
        _mysql.start();
        _sDB_URL = MySqlDriver.getUrl(_mysql.getHost() + ":" + _mysql.getFirstMappedPort() + "/" + _mysql.getDatabaseName(), true);
        _sDB_USER = _mysql.getUsername();
        _sDB_PASSWORD = _mysql.getPassword();
    }

    @AfterAll
    public static void tearDownClass() {
        _mysql.stop();
    }

    @BeforeEach
    public void setUp() {
        try {
            Class.forName(_sDRIVER_CLASS);
            _driver = DriverManager.getDriver(_sDB_URL);
            _conn = DriverManager.getConnection(_sDB_URL, _sDB_USER, _sDB_PASSWORD);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        } catch (ClassNotFoundException cnfe) {
            fail(EU.getExceptionMessage(cnfe));
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
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
        assertSame(MySqlDriver.class, _driver.getClass(), "Registration of driver wrapper failed!");
        assertSame(MySqlConnection.class, _conn.getClass(), "Registration of connection wrapper failed!");
    }

    @Test
    public void testCompliant() {
        assertSame(false, _driver.jdbcCompliant(), "MySql driver is suddenly JDBC compliant!");
    }

    @Test
    public void testAcceptsURL() {
        try {
            assertSame(true, _driver.acceptsURL(_sDB_URL), "Valid MySql URL not accepted!");
            assertSame(false, _driver.acceptsURL(_sINVALID_MYSQL_URL), "Invalid MySql URL accepted!");
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
        assertEquals("8.3", sVersion, "Wrong MySql version " + sVersion + " found!");
    }

    @Test
    public void testDriverProperties() {
        try {
            DriverPropertyInfo[] aPropInfo = _driver.getPropertyInfo(_sDB_URL, new Properties());
            for (DriverPropertyInfo propInfo : aPropInfo) {
                System.out.println(propInfo.name + ": " + propInfo.value + " (" + propInfo.description + ")");
            }
            assertEquals(211, aPropInfo.length, "Unexpected driver properties!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

}
