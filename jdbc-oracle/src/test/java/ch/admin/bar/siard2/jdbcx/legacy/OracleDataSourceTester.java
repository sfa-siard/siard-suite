package ch.admin.bar.siard2.jdbcx.legacy;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import ch.admin.bar.siard2.jdbcx.OracleDataSource;
import org.junit.*;

import org.testcontainers.containers.OracleContainer;

public class OracleDataSourceTester {

    @ClassRule
    public final static OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");

    private static final String _sDB_USER = "SYSTEM";
    private static final String _sDB_PASSWORD = "test";

    private OracleDataSource _dsOracle = null;
    private Connection _conn = null;

    @Before
    public void setUp() {
        try {
            _dsOracle = new OracleDataSource();
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
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* tearDown */

    @Test
    public void testConnection() {
        _dsOracle.setUser(_sDB_USER);
        _dsOracle.setPassword(_sDB_PASSWORD);
        _dsOracle.setUrl(db.getJdbcUrl());
        try {
            _conn = _dsOracle.getConnection();
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* testConnection */

    @Test
    public void testWrapper() {
        try {
            Assert.assertSame("Invalid wrapper!", true, _dsOracle.isWrapperFor(DataSource.class));
            DataSource dsWrapped = _dsOracle.unwrap(DataSource.class);
            assertSame("Invalid wrapped class!", oracle.jdbc.pool.OracleDataSource.class, dsWrapped.getClass());
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* testWrapper */


    @Test
    public void testLoginTimeout() {
        try {
            int iLoginTimeout = _dsOracle.getLoginTimeout();
            assertSame("Unexpected login timeout " + String.valueOf(iLoginTimeout) + "!", 0, iLoginTimeout);
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    } /* testLoginTimeout */
}
