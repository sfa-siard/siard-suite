package ch.admin.bar.siard2.jdbcx;

import ch.admin.bar.siard2.jdbc.AccessConnection;
import ch.enterag.utils.FU;
import lombok.SneakyThrows;
import org.junit.*;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class AccessDataSourceTester {
    private static final File fileTEST_DATABASE = new File("src/test/resources/testfiles/TestDataSource.accdb");
    private static final File fileBACKUP_DATABASE = new File("src/test/resources/tmp/TestDataSource.bak");
    private static final String sDESCRIPTION = "Test of AccessDataSource";
    private static final boolean bREAD_ONLY = true;
    private static final String sUSER = "Admin";
    private AccessDataSource _ds = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
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

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetDatabaseName() {
        System.out.println("\nGetDatabaseName");
        assertEquals("Invalid database name!", fileTEST_DATABASE.getAbsolutePath(), _ds.getDatabaseName());
    }

    @Test
    public void testGetUrl() {
        System.out.println("\nGetUrl");
        assertEquals("Invalid URL!", "jdbc:access:" + fileTEST_DATABASE.getAbsolutePath(), _ds.getUrl());
    }

    @Test
    public void testGetDescription() {
        System.out.println("\nGetDescription");
        assertEquals("Wrong description!", sDESCRIPTION, _ds.getDescription());
    }

    @Test
    public void testGetUser() {
        System.out.println("\nGetUser");
        assertEquals("Invalid user!", sUSER, _ds.getUser());
        System.out.println("User: " + _ds.getUser());
    }

    @Test
    public void testGetReadOnly() {
        System.out.println("\nGetReadOnly");
        assertEquals("Invalid read-only value!", bREAD_ONLY, _ds.getReadOnly());
    }

    @SneakyThrows
    @Test
    public void testIsWrapperFor() {
        System.out.println("\nIsWrapperFor");
        assertTrue("IsWrapperFor failed!", _ds.isWrapperFor(DataSource.class));
        assertFalse("IsWrapperFor did not fail!", _ds.isWrapperFor(Connection.class));
    }

    @SneakyThrows
    @Test(expected = SQLException.class)
    public void testUnwrap() {
        DataSource ds = _ds.unwrap(DataSource.class);
        assertNotNull("Unwrap failed!", ds);
        Connection conn = _ds.unwrap(Connection.class);
    }

    @Test
    public void testGetLoginTimeout() throws SQLException {
        assertEquals("Invalid login timeout!", 0, _ds.getLoginTimeout());
    }

    @Test
    public void testGetConnection() throws SQLException {
        Connection conn = _ds.getConnection();
        assertEquals("Invalid class!", AccessConnection.class, conn.getClass());
        conn.close();
    }

    @Test
    public void testGetConnectionStringString() throws SQLException {
        Connection conn = _ds.getConnection(sUSER, "");
        assertEquals("Invalid class!", AccessConnection.class, conn.getClass());
        conn.close();
    }

}
