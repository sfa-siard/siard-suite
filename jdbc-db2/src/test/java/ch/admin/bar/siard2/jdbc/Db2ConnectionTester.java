package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.db2.TestDb2Database;
import ch.admin.bar.siard2.db2.TestSqlDatabase;
import ch.admin.bar.siard2.jdbcx.Db2DataSource;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;
import ch.enterag.utils.jdbc.BaseConnectionTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.Db2Container;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import static org.junit.Assert.*;

public class Db2ConnectionTester extends BaseConnectionTester {

    @ClassRule
    public static Db2Container db2 = new Db2Container()
            .acceptLicense();

    private static final ConnectionProperties _cp = new ConnectionProperties();
    private static final String TESTUSER = "TESTUSER";
    private static final String TESTUSERPWD = "testuserpwd";

    @BeforeClass
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

    @Before
    public void setUp() throws SQLException {
        Db2DataSource dsDb2 = new Db2DataSource();
        dsDb2.setUrl(db2.getJdbcUrl());
        dsDb2.setUser(db2.getUsername());
        dsDb2.setPassword(db2.getPassword());
        Db2Connection connDb2 = (Db2Connection) dsDb2.getConnection();
        connDb2.setAutoCommit(false);
        setConnection(connDb2);
    } /* setUp */

    @Test
    public void testClass() {
        assertEquals("Wrong connection class!", Db2Connection.class, getConnection().getClass());
    } /* testClass */

    @Test
    public void testValid() {
        enter();
        try {
            int iTimeoutSec = 30;
            assertSame("Connection is not valid!", true, getConnection().isValid(iTimeoutSec));
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testValid */

}
