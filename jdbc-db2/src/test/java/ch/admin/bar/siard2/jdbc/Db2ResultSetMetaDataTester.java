package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.db2.TestDb2Database;
import ch.admin.bar.siard2.db2.TestSqlDatabase;
import ch.admin.bar.siard2.jdbcx.Db2DataSource;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.jdbc.BaseResultSetMetaDataTester;
import org.junit.*;
import org.testcontainers.containers.Db2Container;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Db2ResultSetMetaDataTester extends BaseResultSetMetaDataTester {

    @ClassRule
    public static Db2Container db2 = new Db2Container("ibmcom/db2:11.5.7.0").acceptLicense();


    private static final String TESTUSER = "TESTUSER";

    private static String _sNativeQuerySimple = null;
    private static String _sNativeQueryComplex = null;
    private static String _sSqlQuerySimple = null;
    private static String _sSqlQueryComplex = null;

    static {
        StringBuilder sbSql = new StringBuilder("SELECT\r\n  ");
        for (int iColumn = 0; iColumn < TestDb2Database._listCdSimple.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = TestDb2Database._listCdSimple.get(iColumn);
            sbSql.append(tcd.getName());
        }
        sbSql.append("\r\nFROM ");
        sbSql.append(TestDb2Database.getQualifiedSimpleTable()
                                    .format());
        _sNativeQuerySimple = sbSql.toString();

        sbSql = new StringBuilder("SELECT\r\n  ");
        for (int iColumn = 0; iColumn < TestDb2Database._listCdComplex.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = TestDb2Database._listCdComplex.get(iColumn);
            sbSql.append(tcd.getName());
        }
        sbSql.append("\r\nFROM ");
        sbSql.append(TestDb2Database.getQualifiedComplexTable()
                                    .format());
        _sNativeQueryComplex = sbSql.toString();

        sbSql = new StringBuilder("SELECT\r\n  ");
        for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
            sbSql.append(tcd.getName());
        }
        sbSql.append("\r\nFROM  ");
        sbSql.append(TestSqlDatabase.getQualifiedSimpleTable()
                                    .format());
        _sSqlQuerySimple = sbSql.toString();

        sbSql = new StringBuilder("SELECT\r\n  ");
        for (int iColumn = 0; iColumn < TestSqlDatabase._listCdComplex.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = TestSqlDatabase._listCdComplex.get(iColumn);
            sbSql.append(tcd.getName());
        }
        sbSql.append("\r\nFROM  ");
        sbSql.append(TestSqlDatabase.getQualifiedComplexTable()
                                    .format());
        _sSqlQueryComplex = sbSql.toString();
    }

    @BeforeClass
    public static void setUpClass() throws SQLException {
        Db2DataSource dsDb2 = new Db2DataSource();
        dsDb2.setUrl(db2.getJdbcUrl());
        dsDb2.setUser(db2.getUsername());
        dsDb2.setPassword(db2.getPassword());
        Db2Connection connDb2 = (Db2Connection) dsDb2.getConnection();
        /* drop and create the test database granting access to _sDB_USER */
        new TestSqlDatabase(connDb2, TESTUSER);
        new TestDb2Database(connDb2, TESTUSER);
        connDb2.close();
    } /* setUpClass */

    private Connection closeResultSet() throws SQLException {
        Connection conn = null;
        ResultSet rs = getResultSet();
        if (rs != null) {
            if (!rs.isClosed()) {
                Statement stmt = rs.getStatement();
                rs.close();
                setResultSetMetaData(null, null);
                if (!stmt.isClosed()) {
                    conn = stmt.getConnection();
                    stmt.close();
                }
            }
        }
        return conn;
    } /* closeResultSet */

    private void openResultSet(Connection conn, String sQuery) throws SQLException {
        closeResultSet();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sQuery);
        ResultSetMetaData rsmd = rs.getMetaData();
        setResultSetMetaData(rsmd, rs);
    } /* openResultSet */


    @Before
    public void setUp() throws SQLException {
        Db2DataSource dsDb2 = new Db2DataSource();
        dsDb2.setUrl(db2.getJdbcUrl());
        dsDb2.setUser(db2.getUsername());
        dsDb2.setPassword(db2.getPassword());
        Connection conn = dsDb2.getConnection();
        conn.setAutoCommit(false);
        openResultSet(conn, _sNativeQuerySimple);
    } /* setUp */

    @After
    @Override
    public void tearDown() {
        try {
            Connection conn = closeResultSet();
            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.commit();
                    conn.close();
                }
            }
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* tearDown */

    @Test
    public void testClass() {
        assertEquals("Wrong database meta data class!", Db2ResultSetMetaData.class, getResultSetMetaData().getClass());
    } /* testClass */

    @Test
    public void testNativeSimple() {
        try {
            openResultSet(getResultSet().getStatement()
                                        .getConnection(), _sNativeQuerySimple);
            super.testAll();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testNativeSimple */

    @Test
    public void testNativeComplex() {
        try {
            openResultSet(getResultSet().getStatement()
                                        .getConnection(), _sNativeQueryComplex);
            super.testAll();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testNativeComplex */

    @Test
    public void testSqlSimple() {
        try {
            openResultSet(getResultSet().getStatement()
                                        .getConnection(), _sSqlQuerySimple);
            super.testAll();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testSqlSimple */

    @Test
    public void testSqlComplex() {
        try {
            openResultSet(getResultSet().getStatement()
                                        .getConnection(), _sSqlQueryComplex);
            super.testAll();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testSqlComplex */

}
