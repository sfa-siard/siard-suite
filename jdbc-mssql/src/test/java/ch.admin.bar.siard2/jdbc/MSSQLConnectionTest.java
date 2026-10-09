package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.jdbcx.MsSqlDataSource;
import ch.admin.bar.siard2.mssql.TestMsSqlDatabase;
import ch.admin.bar.siard2.mssql.TestSqlDatabase;
import ch.enterag.utils.jdbc.BaseConnectionTester;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MSSQLConnectionTest extends BaseConnectionTester {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @Container
    public static MSSQLServerContainer<?> mssqlContainer = new MSSQLServerContainer<>(MSSQL_IMAGE).acceptLicense()
                                                                                                  .withPassword(SA_PASSWORD)
                                                                                                  .withUrlParam("trustServerCertificate", "true");

    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    private MsSqlConnection msSqlConnection = null;

    @BeforeAll
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

    @BeforeEach
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
        assertEquals(MsSqlConnection.class, msSqlConnection.getClass(), "Wrong connection class!");
    }


    @Test
    @Override
    public void testCreateArrayOf() {
        assertThrows(SQLFeatureNotSupportedException.class,
                     () -> msSqlConnection.createArrayOf("VARCHAR(256)", new String[]{"a", "b", "c"}));
    }


    @Test
    @Override
    @SneakyThrows
    public void testCreateStatement() {
        Statement stmt = msSqlConnection.createStatement();
        assertEquals(MsSqlStatement.class, stmt.getClass(), "Wrong statement class!");
    }

    @Test
    @Override
    @SneakyThrows
    public void testGetMetadata() {
        DatabaseMetaData dmd = msSqlConnection.getMetaData();
        assertEquals(MsSqlDatabaseMetaData.class, dmd.getClass(), "Wrong metadata class!");
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
            assertEquals(0, rs.getInt(1), "Table should be empty after rollback");
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
        assertNotNull(sp, "Savepoint should not be null");
    }

    @Test
    @Override
    @SneakyThrows
    public void testSetSavepoint_String() {
        Savepoint sp = msSqlConnection.setSavepoint("TEST_SAVEPOINT");
        assertNotNull(sp, "Savepoint should not be null");
        assertEquals("TEST_SAVEPOINT", sp.getSavepointName(), "Savepoint name should match");
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
        assertNotNull(sp, "Savepoint should not be null");

        // Insert more data
        stmt.execute("INSERT INTO test_sp_rollback VALUES (2)");

        // Rollback to savepoint - should keep first insert, remove second
        msSqlConnection.rollback(sp);

        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_sp_rollback");
        rs.next();
        assertEquals(1, rs.getInt(1), "Should have 1 row after rollback to savepoint");
        rs.close();

        // Clean up
        msSqlConnection.rollback();
        stmt.close();
    }

    @Test
    @Override
    public void testReleaseSavePoint() {
        assertThrows(SQLFeatureNotSupportedException.class,
                     () -> {
                         Savepoint sp = msSqlConnection.setSavepoint();
                         msSqlConnection.releaseSavepoint(sp);
                     });
    }

    @Test
    @Override
    @SneakyThrows
    public void testPrepareStatement_String_AInt() {
        PreparedStatement pstmt = msSqlConnection.prepareStatement(_sSQL, new int[]{1});
        assertNotNull(pstmt, "PreparedStatement should not be null");
        assertTrue(pstmt instanceof PreparedStatement, "Should be instance of PreparedStatement");
        pstmt.close();
    }

    @Test
    @Override
    @SneakyThrows
    public void testPrepareStatement_String_AString() {
        PreparedStatement pstmt = msSqlConnection.prepareStatement(_sSQL, new String[]{"COL_A"});
        assertNotNull(pstmt, "PreparedStatement should not be null");
        assertTrue(pstmt instanceof PreparedStatement, "Should be instance of PreparedStatement");
        pstmt.close();
    }


    @Test
    @Override
    @SneakyThrows
    public void testSetCatalog() {
        String originalCatalog = msSqlConnection.getCatalog();

        msSqlConnection.setCatalog("master");
        assertEquals("master", msSqlConnection.getCatalog(), "Catalog should be set to master");

        // Restore original catalog
        if (originalCatalog != null) {
            msSqlConnection.setCatalog(originalCatalog);
        }
    }

    @Test
    @Override
    public void testCreateStruct() {
        assertThrows(SQLServerException.class,
                     () -> msSqlConnection.createStruct("TEST_SCHEMA.TEST_STRUCT_TYPE", new String[]{"a", "b", "c"}));
    }
}
