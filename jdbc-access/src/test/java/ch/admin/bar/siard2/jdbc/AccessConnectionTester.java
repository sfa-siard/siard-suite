package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.access.TestAccessDatabase;
import ch.admin.bar.siard2.access.TestSqlDatabase;
import ch.admin.bar.siard2.jdbcx.AccessDataSource;
import ch.enterag.utils.FU;
import ch.enterag.utils.jdbc.BaseConnectionTester;
import ch.enterag.utils.lang.Execute;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class AccessConnectionTester extends BaseConnectionTester {
    private static final File fileTEST_EMPTY_DATABASE = new File("src/test/resources/testfiles/testempty.accdb");
    private static final File fileTEST_ACCESS_SOURCE = new File("src/test/resources/testfiles/testaccess.accdb");
    private static final File fileTEST_ACCESS_DATABASE = new File("src/test/resources/tmp/testaccess.accdb");
    private static final File fileTEST_SQL_DATABASE = new File("src/test/resources/tmp/testsql.accdb");
    private static final String sUSER = "Admin";
    private static final String sPASSWORD = "";

    @BeforeClass
    public static void setUpClass() throws IOException, SQLException {
        FU.copy(fileTEST_EMPTY_DATABASE, fileTEST_ACCESS_DATABASE);
        /* The JDBC-ODBC bridge could still be used until JAVA 8 using
         * an extract from the JAVA 7 run-time library and the JdbcOdbc.dll.
         * Now that is blocked by the split packages prohibition.
         * So we use the test database originally created under JAVA 8.
         * If we ever want more controlled features in the test database
         * we shall be in trouble ... (have to use JAVA 7 or 8!)
         */
        if (Execute.isOsWindows() && Execute.isJavaVersionLessThan("9"))
            new TestAccessDatabase(fileTEST_ACCESS_DATABASE);
        else FU.copy(fileTEST_ACCESS_SOURCE, fileTEST_ACCESS_DATABASE);
        FU.copy(fileTEST_EMPTY_DATABASE, fileTEST_SQL_DATABASE);
        AccessDataSource dsAccess = new AccessDataSource();
        dsAccess.setDatabaseName(fileTEST_SQL_DATABASE.getAbsolutePath());
        dsAccess.setDescription("SQL data base");
        dsAccess.setReadOnly(false);
        dsAccess.setUser(sUSER);
        dsAccess.setPassword(sPASSWORD);
        AccessConnection connAccess = (AccessConnection) dsAccess.getConnection();
        new TestSqlDatabase(connAccess);
        connAccess.close();
    }

    @Before
    public void setUp() throws SQLException {
        AccessDataSource dsAccess = new AccessDataSource();
        dsAccess.setDatabaseName(fileTEST_SQL_DATABASE.getAbsolutePath());
        dsAccess.setDescription("SQL data base");
        dsAccess.setReadOnly(false);
        dsAccess.setUser(sUSER);
        dsAccess.setPassword(sPASSWORD);
        AccessConnection connAccess = (AccessConnection) dsAccess.getConnection();
        connAccess.setAutoCommit(false);
        setConnection(connAccess);
    }

    @Test
    public void testClass() {
        assertEquals("Wrong connection class!", AccessConnection.class, getConnection().getClass());
    }

    @SneakyThrows
    @Override
    @Test(expected = SQLException.class)
    public void testPrepareCall() {
        getConnection().prepareCall(_sSQL);
    }

    @SneakyThrows
    @Override
    @Test
    public void testSetTransactionIsolation() {
        getConnection().setTransactionIsolation(Connection.TRANSACTION_NONE);
    }

}
