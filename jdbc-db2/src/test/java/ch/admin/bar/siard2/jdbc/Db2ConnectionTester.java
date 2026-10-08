package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.db2.TestDb2Database;
import ch.admin.bar.siard2.db2.TestSqlDatabase;
import ch.admin.bar.siard2.jdbcx.Db2DataSource;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;
import ch.enterag.utils.jdbc.BaseConnectionTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Db2Container;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import static org.junit.jupiter.api.Assertions.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class Db2ConnectionTester extends BaseConnectionTester {

    @Container
    public static Db2Container db2 = new Db2Container()
            .acceptLicense();

    private static final ConnectionProperties _cp = new ConnectionProperties();
    private static final String TESTUSER = "TESTUSER";
    private static final String TESTUSERPWD = "testuserpwd";

    @BeforeAll
    public static void setUpClass() throws SQLException {
        Db2DataSource dsDb2 = new Db2DataSource();
        dsDb2.setUrl(db2.getJdbcUrl());
        dsDb2.setUser(db2.getUsername());
        dsDb2.setPassword(db2.getPassword());
        Db2Connection connDb2 = (Db2Connection) dsDb2.getConnection();
        /* drop and create the test database granting access to TESTUSER */
        new TestSqlDatabase(connDb2, TESTUSER);
        new TestDb2Database(connDb2, TESTUSER);
        connDb2.close();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        Db2DataSource dsDb2 = new Db2DataSource();
        dsDb2.setUrl(db2.getJdbcUrl());
        dsDb2.setUser(db2.getUsername());
        dsDb2.setPassword(db2.getPassword());
        Db2Connection connDb2 = (Db2Connection) dsDb2.getConnection();
        connDb2.setAutoCommit(false);
        setConnection(connDb2);
    }

    @Test
    public void testClass() {
        assertEquals(Db2Connection.class, getConnection().getClass(), "Wrong connection class!");
    }

    @Test
    public void testValid() {
        enter();
        try {
            int iTimeoutSec = 30;
            assertSame(true, getConnection().isValid(iTimeoutSec), "Connection is not valid!");
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

}
