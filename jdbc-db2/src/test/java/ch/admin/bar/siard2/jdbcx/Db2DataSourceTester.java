package ch.admin.bar.siard2.jdbcx;

import java.sql.*;
import javax.sql.*;

import static org.junit.Assert.*;

import org.junit.*;
import org.testcontainers.containers.Db2Container;

public class Db2DataSourceTester {

    @ClassRule
    public static Db2Container db2 = new Db2Container("ibmcom/db2:11.5.7.0").acceptLicense();

    private Db2DataSource _dsDb2 = null;
    private Connection _conn = null;

    @Before
    public void setUp() {
        try {
            _dsDb2 = new Db2DataSource();
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* setUp */

    @After
    public void tearDown() {
        try {
            if ((_conn != null) && (!_conn.isClosed())) _conn.close();
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* tearDown */

    @Test
    public void testWrapper() {
        try {
            Assert.assertSame("Invalid wrapper!", true, _dsDb2.isWrapperFor(DataSource.class));
            DataSource dsWrapped = _dsDb2.unwrap(DataSource.class);
            assertSame("Invalid wrapped class!", com.ibm.db2.jcc.DB2SimpleDataSource.class, dsWrapped.getClass());
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* testWrapper */

    @Test
    public void testLoginTimeout() {
        try {
            int iLoginTimeout = _dsDb2.getLoginTimeout();
            assertSame("Unexpected login timeout " + String.valueOf(iLoginTimeout) + "!", 0, iLoginTimeout);
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* testLoginTimeout */

    @Test
    public void testConnection() {
        _dsDb2.setUrl(db2.getJdbcUrl());
        _dsDb2.setUser(db2.getUsername());
        _dsDb2.setPassword(db2.getPassword());
        try {
            _conn = _dsDb2.getConnection();
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* testConnection */

}
