package ch.admin.bar.siard2.jdbcx;

import ch.admin.bar.siard2.jdbc.AccessConnection;
import ch.enterag.utils.FU;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assertions;

public class AccessDataSourceTester {
    private static final File fileTEST_DATABASE = new File("src/test/resources/testfiles/TestDataSource.accdb");
    private static final File fileBACKUP_DATABASE = new File("src/test/resources/tmp/TestDataSource.bak");
    private static final String sDESCRIPTION = "Test of AccessDataSource";
    private static final boolean bREAD_ONLY = true;
    private static final String sUSER = "Admin";
    private AccessDataSource _ds = null;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
        boolean bCopied = false;
        if (fileBACKUP_DATABASE.exists())
            FU.copy(fileBACKUP_DATABASE, fileTEST_DATABASE);
        else
            FU.copy(fileTEST_DATABASE, fileBACKUP_DATABASE);
        bCopied = true;
        if (!bCopied)
            fail("Initial copying failed!");
        _ds = new AccessDataSource();
        _ds.setDatabaseName(fileTEST_DATABASE.getAbsolutePath());
        _ds.setDescription(sDESCRIPTION);
        _ds.setReadOnly(bREAD_ONLY);
        _ds.setUser(sUSER);
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetDatabaseName() {
        System.out.println("\nGetDatabaseName");
        assertEquals(fileTEST_DATABASE.getAbsolutePath(), _ds.getDatabaseName(), "Invalid database name!");
    }

    @Test
    public void testGetUrl() {
        System.out.println("\nGetUrl");
        assertEquals("jdbc:access:" + fileTEST_DATABASE.getAbsolutePath(), _ds.getUrl(), "Invalid URL!");
    }

    @Test
    public void testGetDescription() {
        System.out.println("\nGetDescription");
        assertEquals(sDESCRIPTION, _ds.getDescription(), "Wrong description!");
    }

    @Test
    public void testGetUser() {
        System.out.println("\nGetUser");
        assertEquals(sUSER, _ds.getUser(), "Invalid user!");
        System.out.println("User: " + _ds.getUser());
    }

    @Test
    public void testGetReadOnly() {
        System.out.println("\nGetReadOnly");
        assertEquals(bREAD_ONLY, _ds.getReadOnly(), "Invalid read-only value!");
    }

    @SneakyThrows
    @Test
    public void testIsWrapperFor() {
        System.out.println("\nIsWrapperFor");
        assertTrue(_ds.isWrapperFor(DataSource.class), "IsWrapperFor failed!");
        assertFalse(_ds.isWrapperFor(Connection.class), "IsWrapperFor did not fail!");
    }

    @SneakyThrows
    @Test
    public void testUnwrap() {
        DataSource ds = _ds.unwrap(DataSource.class);
        assertNotNull(ds, "Unwrap failed!");
        assertThrows(SQLException.class, () -> _ds.unwrap(Connection.class));
    }

    @Test
    public void testGetLoginTimeout() throws SQLException {
        assertEquals(0, _ds.getLoginTimeout(), "Invalid login timeout!");
    }

    @Test
    public void testGetConnection() throws SQLException {
        Connection conn = _ds.getConnection();
        assertEquals(AccessConnection.class, conn.getClass(), "Invalid class!");
        conn.close();
    }

    @Test
    public void testGetConnectionStringString() throws SQLException {
        Connection conn = _ds.getConnection(sUSER, "");
        assertEquals(AccessConnection.class, conn.getClass(), "Invalid class!");
        conn.close();
    }

}
