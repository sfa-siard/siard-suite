package ch.admin.bar.siard2.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class Db2DriverTester {

    @Container
    public static Db2Container db2 = new Db2Container("ibmcom/db2:11.5.7.0").acceptLicense();


    private static final String sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.Db2Driver";
    private static final String sTEST_DB2_URL = Db2Driver.getUrl("localhost/testdb");
    private static final String sINVALID_ORACLE_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";

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
            _driver = DriverManager.getDriver(sTEST_DB2_URL);
            _conn = DriverManager.getConnection(db2.getJdbcUrl(), db2.getUsername(), db2.getPassword());
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
        assertSame(Db2Driver.class, _driver.getClass(), "Registration of driver wrapper failed!");
        assertSame(Db2Connection.class, _conn.getClass(), "Choice of connection wrapper failed!");
    }

    @Test
    public void testCompliant() {
        assertSame(true, _driver.jdbcCompliant(), "DB/2 driver not JDBC compliant!");
    }

    @Test
    public void testAcceptsURL() {
        try {
            assertSame(true, _driver.acceptsURL(db2.getJdbcUrl()), "Valid DB/2 URL not accepted!");
            assertSame(false, _driver.acceptsURL(sINVALID_ORACLE_URL), "Invalid DB/2 URL accepted!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testVersion() {
        // delegation check only — don't pin the vendor driver's version
        assertTrue(_driver.getMajorVersion() > 0, "Driver reports no major version!");
        assertTrue(_driver.getMinorVersion() >= 0, "Driver reports no minor version!");
    }

    @Test
    public void testDriverProperties() throws SQLException {
        DriverPropertyInfo[] apropInfo = _driver.getPropertyInfo(db2.getJdbcUrl(), new Properties());
        assertTrue(apropInfo.length > 0, "Driver reports no properties!");
    }

}
