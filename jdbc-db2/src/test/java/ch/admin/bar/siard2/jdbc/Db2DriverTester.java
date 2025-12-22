package ch.admin.bar.siard2.jdbc;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.*;

import org.testcontainers.containers.Db2Container;

public class Db2DriverTester {

    @ClassRule
    public static Db2Container db2 = new Db2Container("ibmcom/db2:11.5.7.0").acceptLicense();


    private static final String sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.Db2Driver";
    private static final String sTEST_DB2_URL = Db2Driver.getUrl("localhost/testdb");
    private static final String sINVALID_ORACLE_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";

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
            _driver = DriverManager.getDriver(sTEST_DB2_URL);
            _conn = DriverManager.getConnection(db2.getJdbcUrl(), db2.getUsername(), db2.getPassword());
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* setUp */

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
    } /* tearDown */

    @Test
    public void testWrapping() {
        assertSame("Registration of driver wrapper failed!", Db2Driver.class, _driver.getClass());
        assertSame("Choice of connection wrapper failed!", Db2Connection.class, _conn.getClass());
    } /* testWrapping */

    @Test
    public void testCompliant() {
        assertSame("DB/2 driver not JDBC compliant!", true, _driver.jdbcCompliant());
    } /* testCompliant */

    @Test
    public void testAcceptsURL() {
        try {
            assertSame("Valid DB/2 URL not accepted!", true, _driver.acceptsURL(db2.getJdbcUrl()));
            assertSame("Invalid DB/2 URL accepted!", false, _driver.acceptsURL(sINVALID_ORACLE_URL));
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* testAcceptsURL */

    @Test
    public void testVersion() {
        int iMajorVersion = _driver.getMajorVersion();
        int iMinorVersion = _driver.getMinorVersion();
        String sVersion = String.valueOf(iMajorVersion) + "." + String.valueOf(iMinorVersion);
        assertEquals("Wrong DB/2 version " + sVersion + " found!", "4.31", sVersion);
    } /* testVersion */

    @Test
    public void testDriverProperties() {
        try {
            DriverPropertyInfo[] apropInfo = _driver.getPropertyInfo(db2.getJdbcUrl(), new Properties());
            for (DriverPropertyInfo dpi : apropInfo)
                System.out.println(dpi.name + ": " + dpi.value + " (" + String.valueOf(dpi.description) + ")");
            assertSame("Unexpected driver properties!", 2, apropInfo.length);
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* testDriverProperties */

}
