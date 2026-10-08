package ch.admin.bar.siard2.jdbcx.legacy;

import ch.admin.bar.siard2.jdbcx.OracleDataSource;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.OracleContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Assertions;

@Testcontainers
public class OracleDataSourceTester {

    @Container
    public final static OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");

    private static final String _sDB_USER = "SYSTEM";
    private static final String _sDB_PASSWORD = "test";

    private OracleDataSource _dsOracle = null;
    private Connection _conn = null;

    @BeforeEach
    public void setUp() {
        try {
            _dsOracle = new OracleDataSource();
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
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

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
    }

    @Test
    public void testWrapper() {
        try {
            Assertions.assertSame(true, _dsOracle.isWrapperFor(DataSource.class), "Invalid wrapper!");
            DataSource dsWrapped = _dsOracle.unwrap(DataSource.class);
            assertSame(oracle.jdbc.pool.OracleDataSource.class, dsWrapped.getClass(), "Invalid wrapped class!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }


    @Test
    public void testLoginTimeout() {
        try {
            int iLoginTimeout = _dsOracle.getLoginTimeout();
            assertSame(0, iLoginTimeout, "Unexpected login timeout " + String.valueOf(iLoginTimeout) + "!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }
}
