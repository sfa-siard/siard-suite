package ch.admin.bar.siard2.jdbcx;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.Db2Container;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Assertions;

@Testcontainers
public class Db2DataSourceTester {

    @Container
    public static Db2Container db2 = new Db2Container("ibmcom/db2:11.5.7.0").acceptLicense();

    private Db2DataSource _dsDb2 = null;
    private Connection _conn = null;

    @BeforeEach
    public void setUp() {
        try {
            _dsDb2 = new Db2DataSource();
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            if ((_conn != null) && (!_conn.isClosed())) _conn.close();
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testWrapper() {
        try {
            Assertions.assertSame(true, _dsDb2.isWrapperFor(DataSource.class), "Invalid wrapper!");
            DataSource dsWrapped = _dsDb2.unwrap(DataSource.class);
            assertSame(com.ibm.db2.jcc.DB2SimpleDataSource.class, dsWrapped.getClass(), "Invalid wrapped class!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testLoginTimeout() {
        try {
            int iLoginTimeout = _dsDb2.getLoginTimeout();
            assertSame(0, iLoginTimeout, "Unexpected login timeout " + String.valueOf(iLoginTimeout) + "!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

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
    }

}
