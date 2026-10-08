package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.jdbcx.MsSqlDataSource;
import ch.admin.bar.siard2.mssql.TestMsSqlDatabase;
import ch.admin.bar.siard2.mssql.TestSqlDatabase;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.jdbc.BaseResultSetMetaDataTester;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Assertions;

@Testcontainers
public class MsSqlResultSetMetaDataTest
        extends BaseResultSetMetaDataTester {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @Container
    public static MSSQLServerContainer<?> mssqlContainer = new MSSQLServerContainer<>(MSSQL_IMAGE)
            .acceptLicense()
            .withPassword(SA_PASSWORD)
            .withUrlParam("trustServerCertificate", "true");

    private static String _sDB_URL;
    private static String _sDB_USER;
    private static String _sDB_PASSWORD;

    private static String getTableQuery(QualifiedId qiTable, List<TestColumnDefinition> listCd) {
        StringBuilder sbSql = new StringBuilder("SELECT\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0)
                sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            sbSql.append(tcd.getName());
        }
        sbSql.append("\r\nFROM ");
        sbSql.append(qiTable.format());
        return sbSql.toString();
    }

    private static String _sNativeQuerySimple = getTableQuery(TestMsSqlDatabase.getQualifiedSimpleTable(), TestMsSqlDatabase._listCdSimple);
    private static String _sNativeQueryComplex = getTableQuery(TestMsSqlDatabase.getQualifiedComplexTable(), TestMsSqlDatabase._listCdComplex);
    private static String _sSqlQuerySimple = getTableQuery(TestSqlDatabase.getQualifiedSimpleTable(), TestSqlDatabase._listCdSimple);
    private static String _sSqlQueryComplex = getTableQuery(TestSqlDatabase.getQualifiedComplexTable(), TestSqlDatabase._listCdComplex);

    @BeforeAll
    public static void setUpClass() {
        try {
            _sDB_URL = mssqlContainer.getJdbcUrl();
            _sDB_USER = mssqlContainer.getUsername();
            _sDB_PASSWORD = mssqlContainer.getPassword();

            MsSqlDataSource dsMsSql = new MsSqlDataSource();
            dsMsSql.setUrl(_sDB_URL);
            dsMsSql.setUser(_sDB_USER);
            dsMsSql.setPassword(_sDB_PASSWORD);
            MsSqlConnection connMsSql = (MsSqlConnection) dsMsSql.getConnection();
            /* drop and create the test databases */
            new TestMsSqlDatabase(connMsSql);
            new TestSqlDatabase(connMsSql);
            connMsSql.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    private Connection closeResultSet()
            throws SQLException {
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
    }

    private void openResultSet(Connection conn, String sQuery)
            throws SQLException {
        closeResultSet();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sQuery);
        ResultSetMetaData rsmd = rs.getMetaData();
        setResultSetMetaData(rsmd, rs);
    }

    @BeforeEach
    public void setUp() {
        try {
            MsSqlDataSource dsMsSql = new MsSqlDataSource();
            dsMsSql.setUrl(_sDB_URL);
            dsMsSql.setUser(_sDB_USER);
            dsMsSql.setPassword(_sDB_PASSWORD);
            Connection conn = dsMsSql.getConnection();
            conn.setAutoCommit(false);
            openResultSet(conn, _sNativeQuerySimple);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @AfterEach
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
    }

    @Test
    public void testClass() {
        assertEquals(MsSqlResultSetMetaData.class, getResultSetMetaData().getClass(), "Wrong result set metadata class!");
    }

    @Test
    public void testNativeSimple() {
        try {
            openResultSet(getResultSet().getStatement()
                                        .getConnection(), _sNativeQuerySimple);
            super.testAll();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testNativeComplex() {
        try {
            openResultSet(getResultSet().getStatement()
                                        .getConnection(), _sNativeQueryComplex);
            super.testAll();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSqlSimple() {
        try {
            openResultSet(getResultSet().getStatement()
                                        .getConnection(), _sSqlQuerySimple);
            super.testAll();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSqlComplex() {
        try {
            openResultSet(getResultSet().getStatement()
                                        .getConnection(), _sSqlQueryComplex);
            super.testAll();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

}
