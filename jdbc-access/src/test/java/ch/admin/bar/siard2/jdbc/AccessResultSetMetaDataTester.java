package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.access.TestAccessDatabase;
import ch.admin.bar.siard2.access.TestSqlDatabase;
import ch.admin.bar.siard2.jdbcx.AccessDataSource;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.FU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.jdbc.BaseResultSetMetaDataTester;
import ch.enterag.utils.lang.Execute;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AccessResultSetMetaDataTester extends BaseResultSetMetaDataTester {
    private static final String _sNativeQuerySimple = getTableQuery(TestAccessDatabase.getQualifiedSimpleTable(), TestAccessDatabase._listCdSimple);
    private static final String _sNativeQueryComplex = getTableQuery(TestAccessDatabase.getQualifiedComplexTable(), TestAccessDatabase._listCdComplex);
    private static final String _sSqlQuerySimple = getTableQuery(TestSqlDatabase.getQualifiedSimpleTable(), TestSqlDatabase._listCdSimple);
    private static final File fileTEST_EMPTY_DATABASE = new File("src/test/resources/testfiles/testempty.accdb");
    private static final File fileTEST_ACCESS_SOURCE = new File("src/test/resources/testfiles/testaccess.accdb");
    private static final File fileTEST_ACCESS_DATABASE = new File("src/test/resources/tmp/testaccess.accdb");
    private static final File fileTEST_SQL_DATABASE = new File("src/test/resources/tmp/testsql.accdb");
    private static final String sUSER = "Admin";
    private static final String sPASSWORD = "";

    private static String getTableQuery(QualifiedId qiTable, List<TestColumnDefinition> listCd) {
        StringBuilder sbSql = new StringBuilder("SELECT\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            sbSql.append(SqlLiterals.formatId(tcd.getName()));
        }
        sbSql.append("\r\nFROM ");
        sbSql.append(qiTable.format());
        return sbSql.toString();
    }

    @BeforeClass
    public static void setUpClass() throws SQLException, IOException {
        FU.copy(fileTEST_EMPTY_DATABASE, fileTEST_ACCESS_DATABASE);
        /* The JDBC-ODBC bridge could still be used until JAVA 1.8 using
         * an extract from the JAVA 7 run-time library and the JdbcOdbc.dll.
         * Now that is blocked by the split packages prohibition.
         * So we use the test database originally created under JAVA 1.8.
         * If we ever want more controlled features in the test database
         * we shall be in trouble ... (have to use JAVA 1.7 or 1.8!)
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

    private void openResultSet(boolean bSql, String sQuery) throws SQLException {
        tearDown();
        AccessDataSource dsAccess = new AccessDataSource();
        if (bSql) {
            dsAccess.setDatabaseName(fileTEST_SQL_DATABASE.getAbsolutePath());
            dsAccess.setDescription("SQL data base");
        } else {
            dsAccess.setDatabaseName(fileTEST_ACCESS_DATABASE.getAbsolutePath());
            dsAccess.setDescription("Access data base");
        }
        dsAccess.setReadOnly(false);
        dsAccess.setUser(sUSER);
        dsAccess.setPassword(sPASSWORD);
        AccessConnection connAccess = (AccessConnection) dsAccess.getConnection();
        connAccess.setAutoCommit(false);
        Statement stmt = connAccess.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery(sQuery);
        ResultSetMetaData rsmd = rs.getMetaData();
        setResultSetMetaData(rsmd, rs);
    }

    @Before
    public void setUp() throws SQLException {
        openResultSet(true, _sSqlQuerySimple);
    }

    @Test
    public void testClass() {
        assertEquals("Wrong database meta data class!", AccessResultSetMetaData.class, getResultSetMetaData().getClass());
    }

    @Test
    public void testNativeSimple() throws SQLException {
        openResultSet(false, _sNativeQuerySimple);
        super.testAll();
    }

    @Test
    public void testNativeComplex() throws SQLException {
        openResultSet(false, _sNativeQueryComplex);
        super.testAll();
    }

    @Test
    public void testSqlSimple() throws SQLException {
        openResultSet(true, _sSqlQuerySimple);
        super.testAll();
    }

}
