package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.jdbcx.MsSqlDataSource;
import ch.admin.bar.siard2.mssql.TestMsSqlDatabase;
import ch.admin.bar.siard2.mssql.TestSqlDatabase;
import ch.enterag.utils.jdbc.BaseConnectionTester;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.*;

import static org.junit.Assert.*;

public class MSSQLConnectionTests extends BaseConnectionTester {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @ClassRule
    public static MSSQLServerContainer<?> mssqlContainer = new MSSQLServerContainer<>(MSSQL_IMAGE).acceptLicense()
                                                                                                  .withPassword(SA_PASSWORD)
                                                                                                  .withUrlParam("encrypt", "false");

    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    private MsSqlConnection msSqlConnection = null;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        DB_URL = mssqlContainer.getJdbcUrl();
        DB_USER = mssqlContainer.getUsername();
        DB_PASSWORD = mssqlContainer.getPassword();

        MsSqlDataSource dsMsSql = new MsSqlDataSource();
        dsMsSql.setUrl(DB_URL);
        dsMsSql.setUser(DB_USER);
        dsMsSql.setPassword(DB_PASSWORD);
        MsSqlConnection connMsSql = (MsSqlConnection) dsMsSql.getConnection();
        /* drop and create the test databases */
        new TestSqlDatabase(connMsSql);
        new TestMsSqlDatabase(connMsSql);
        connMsSql.close();
    }

    @Before
    public void setUp() throws SQLException {
        MsSqlDataSource dataSource = new MsSqlDataSource();
        dataSource.setUrl(DB_URL);
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        msSqlConnection = (MsSqlConnection) dataSource.getConnection();
        msSqlConnection.setAutoCommit(false);
        setConnection(msSqlConnection);
    }

    @Test
    public void testClass() {
        assertEquals("Wrong connection class!", MsSqlConnection.class, msSqlConnection.getClass());
    }


    @Test(expected = SQLFeatureNotSupportedException.class)
    @Override
    @SneakyThrows
    public void testCreateArrayOf() {
        Array array = msSqlConnection.createArrayOf("VARCHAR(256)", new String[]{"a", "b", "c"});
        array.free();
    }


    @Test
    @Override
    @SneakyThrows
    public void testCreateStatement() {
        Statement stmt = msSqlConnection.createStatement();
        assertEquals("Wrong statement class!", MsSqlStatement.class, stmt.getClass());
    }

    @Test
    @Override
    @SneakyThrows
    public void testGetMetadata() {
        DatabaseMetaData dmd = msSqlConnection.getMetaData();
        assertEquals("Wrong metadata class!", MsSqlDatabaseMetaData.class, dmd.getClass());
    }

    @Test
    @Override
    @SneakyThrows
    public void testRollback() {
        // Create a table and insert data
        Statement stmt = msSqlConnection.createStatement();
        stmt.execute("CREATE TABLE test_rollback (id INT, name VARCHAR(50))");
        stmt.execute("INSERT INTO test_rollback VALUES (1, 'test')");

        // Verify data exists
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_rollback");
        rs.next();
        assertEquals(1, rs.getInt(1));
        rs.close();

        // Rollback should remove the data
        msSqlConnection.rollback();

        // After rollback, verify table is empty (table creation and insert were rolled back)
        try {
            rs = stmt.executeQuery("SELECT COUNT(*) FROM test_rollback");
            rs.next();
            assertEquals("Table should be empty after rollback", 0, rs.getInt(1));
            rs.close();
        } catch (SQLException e) {
            // Table doesn't exist after rollback - this is expected and correct
        }
        stmt.close();
    }

    @Test
    @Override
    @SneakyThrows
    public void testSetSavepoint() {
        Savepoint sp = msSqlConnection.setSavepoint();
        assertNotNull("Savepoint should not be null", sp);
    }

    @Test
    @Override
    @SneakyThrows
    public void testSetSavepoint_String() {
        Savepoint sp = msSqlConnection.setSavepoint("TEST_SAVEPOINT");
        assertNotNull("Savepoint should not be null", sp);
        assertEquals("Savepoint name should match", "TEST_SAVEPOINT", sp.getSavepointName());
    }

    @Test
    @Override
    @SneakyThrows
    public void testRollback_Savepoint() {
        Statement stmt = msSqlConnection.createStatement();
        stmt.execute("CREATE TABLE test_sp_rollback (id INT)");
        stmt.execute("INSERT INTO test_sp_rollback VALUES (1)");

        // Create savepoint after first insert
        Savepoint sp = msSqlConnection.setSavepoint();
        assertNotNull("Savepoint should not be null", sp);

        // Insert more data
        stmt.execute("INSERT INTO test_sp_rollback VALUES (2)");

        // Rollback to savepoint - should keep first insert, remove second
        msSqlConnection.rollback(sp);

        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_sp_rollback");
        rs.next();
        assertEquals("Should have 1 row after rollback to savepoint", 1, rs.getInt(1));
        rs.close();

        // Clean up
        msSqlConnection.rollback();
        stmt.close();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    @Override
    @SneakyThrows
    public void testReleaseSavePoint() {
        Savepoint sp = msSqlConnection.setSavepoint();
        msSqlConnection.releaseSavepoint(sp);
    }

    @Test
    @Override
    @SneakyThrows
    public void testPrepareStatement_String_AInt() {
        PreparedStatement pstmt = msSqlConnection.prepareStatement(_sSQL, new int[]{1});
        assertNotNull("PreparedStatement should not be null", pstmt);
        assertTrue("Should be instance of PreparedStatement", pstmt instanceof PreparedStatement);
        pstmt.close();
    }

    @Test
    @Override
    @SneakyThrows
    public void testPrepareStatement_String_AString() {
        PreparedStatement pstmt = msSqlConnection.prepareStatement(_sSQL, new String[]{"COL_A"});
        assertNotNull("PreparedStatement should not be null", pstmt);
        assertTrue("Should be instance of PreparedStatement", pstmt instanceof PreparedStatement);
        pstmt.close();
    }


    @Test
    @Override
    @SneakyThrows
    public void testSetCatalog() {
        String originalCatalog = msSqlConnection.getCatalog();

        msSqlConnection.setCatalog("master");
        assertEquals("Catalog should be set to master", "master", msSqlConnection.getCatalog());

        // Restore original catalog
        if (originalCatalog != null) {
            msSqlConnection.setCatalog(originalCatalog);
        }
    }
}
