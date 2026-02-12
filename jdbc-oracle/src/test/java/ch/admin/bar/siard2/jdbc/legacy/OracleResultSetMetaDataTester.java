package ch.admin.bar.siard2.jdbc.legacy;

import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;

import ch.admin.bar.siard2.jdbc.OracleConnection;
import ch.admin.bar.siard2.jdbc.OracleResultSetMetaData;
import ch.admin.bar.siard2.oracle.legacy.TestOracleDatabase;
import ch.admin.bar.siard2.oracle.legacy.TestSqlDatabase;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.jdbcx.*;
import org.testcontainers.containers.OracleContainer;

public class OracleResultSetMetaDataTester extends BaseResultSetMetaDataTester {

    @ClassRule
    public final static OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");

    private static final String _sDBA_USER = "SYSTEM";
    private static final String _sDBA_PASSWORD = "test";
    private static final String _sDB_USER = "test";
    private static final String _sDB_PASSWORD = "test";

    private static String getTableQuery(QualifiedId qiTable, List<TestColumnDefinition> listCd) {
        StringBuilder sbSql = new StringBuilder("SELECT\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            sbSql.append(tcd.getName());
        }
        sbSql.append("\r\nFROM ");
        sbSql.append(qiTable.format());
        return sbSql.toString();
    }

    private static String _sNativeQuerySimple = getTableQuery(TestOracleDatabase.getQualifiedSimpleTable(), TestOracleDatabase._listCdSimple);
    private static String _sNativeQueryComplex = getTableQuery(TestOracleDatabase.getQualifiedComplexTable(), TestOracleDatabase._listCdComplex);
    private static String _sSqlQuerySimple = getTableQuery(TestSqlDatabase.getQualifiedSimpleTable(), TestSqlDatabase._listCdSimple);
    private static String _sSqlQueryComplex = getTableQuery(TestSqlDatabase.getQualifiedComplexTable(), TestSqlDatabase._listCdComplex);

    @BeforeClass
    public static void setUpClass() {
        try {
            OracleDataSource dsOracle = new OracleDataSource();
            dsOracle.setUrl(db.getJdbcUrl());
            dsOracle.setUser(_sDBA_USER);
            dsOracle.setPassword(_sDBA_PASSWORD);
            OracleConnection connOracle = (OracleConnection) dsOracle.getConnection();
            new TestSqlDatabase(connOracle);
            TestOracleDatabase.grantSchema(connOracle, TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
            new TestOracleDatabase(connOracle);
            TestOracleDatabase.grantSchema(connOracle, TestOracleDatabase._sTEST_SCHEMA, _sDB_USER);
            connOracle.close();
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

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
    public void setUp() {
        try {
            OracleDataSource dsOracle = new OracleDataSource();
            dsOracle.setUrl(db.getJdbcUrl());
            dsOracle.setUser(_sDB_USER);
            dsOracle.setPassword(_sDB_PASSWORD);
            Connection conn = dsOracle.getConnection();
            conn.setAutoCommit(false);
            openResultSet(conn, _sNativeQuerySimple);
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testClass() {
        assertEquals("Wrong result set metadata class!", OracleResultSetMetaData.class, getResultSetMetaData().getClass());
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
