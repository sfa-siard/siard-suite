package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.db2.TestDb2Database;
import ch.admin.bar.siard2.db2.TestSqlDatabase;
import ch.admin.bar.siard2.jdbcx.Db2DataSource;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import ch.enterag.utils.jdbc.BaseStatementTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.Db2Container;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Db2StatementTester extends BaseStatementTester {

    @ClassRule
    public static Db2Container db2 = new Db2Container("ibmcom/db2:11.5.7.0").acceptLicense();

    private static final String TESTUSER = "TESTUSER";


    private static final String _sSQL_DDL = "CREATE TABLE TESTTABLESIMPLE(\r\n" + "CCHAR CHARACTER NOT NULL,\r\n" + "CVARCHAR VARCHAR(256),\r\n" + "CCLOB CLOB(4M),\r\n" + "CNCHAR NCHAR,\r\n" + "CNCHAR_VARYING NCHAR VARYING(256),\r\n" +
            // "CNCLOB NCLOB(4G),\r\n" +
            "CXML XML,\r\n" + "CBINARY BINARY,\r\n" +
            // "CVARBINARY VARBINARY(256),\r\n" +
            "CBLOB BLOB,\r\n" + "CNUMERIC NUMERIC(10, 3),\r\n" + "CDECIMAL DECIMAL,\r\n" + "CSMALLINT SMALLINT,\r\n" + "CINTEGER INTEGER NOT NULL,\r\n" + "CBIGINT BIGINT,\r\n" + "CFLOAT FLOAT(7),\r\n" + "CREAL REAL,\r\n" + "CDOUBLE DOUBLE PRECISION,\r\n" +
            // "CBOOLEAN BOOLEAN,\r\n" +
            "CDATE DATE,\r\n" +
            // "CTIME TIME(3),\r\n" +
            "CTIMESTAMP TIMESTAMP(9),\r\n" +
            // "CINTERVALYEAR INTERVAL YEAR(2) TO MONTH,\r\n" +
            // "CINTERVALDAY INTERVAL DAY TO MINUTE,\r\n" +
            // "CINTERVALSECOND INTERVAL SECOND(2, 5),\r\n" +
            "PRIMARY KEY(CINTEGER),\r\n" + "UNIQUE(CCHAR))";
    private static final String _sSQL_CLEAN = "DROP TABLE TESTTABLESIMPLE CASCADE";
    private static final String _sSQL_QUERY = "SELECT * FROM SYSIBM.TABLES";

    private Db2Statement _stmtDb2 = null;

    protected void clean() throws SQLException {
        try {
            getStatement().executeUpdate(_sSQL_CLEAN);
            getStatement().getConnection()
                          .commit();
        } catch (SQLException se) {
            getStatement().getConnection()
                          .rollback();
        }
    } /* clean */

    @BeforeClass
    public static void setUpClass() throws SQLException {
        Db2DataSource dsDb2 = new Db2DataSource();
        dsDb2.setUrl(db2.getJdbcUrl());
        dsDb2.setUser(db2.getUsername());
        dsDb2.setPassword(db2.getPassword());
        Db2Connection connDb2 = (Db2Connection) dsDb2.getConnection();
        connDb2.setReadOnly(false);
        /* drop and create the test database */
        new TestSqlDatabase(connDb2, TESTUSER);
        new TestDb2Database(connDb2, TESTUSER);
        connDb2.close();
    } /* setUpClass */

    @Before
    public void setUp() throws SQLException {
        Db2DataSource dsDb2 = new Db2DataSource();
        dsDb2.setUrl(db2.getJdbcUrl());
        dsDb2.setUser(db2.getUsername());
        dsDb2.setPassword(db2.getPassword());
        Db2Connection connDb2 = (Db2Connection) dsDb2.getConnection();
        connDb2.setAutoCommit(false);
        _stmtDb2 = (Db2Statement) connDb2.createStatement();
        setStatement(_stmtDb2);
    } /* setUp */

    private void changeUser(String sUser, String sPassword) {
        try {
            Connection conn = getStatement().getConnection();
            conn.commit();
            getStatement().close();
            conn.close();
            Db2DataSource dsDb2 = new Db2DataSource();
            dsDb2.setUrl(db2.getJdbcUrl());
            dsDb2.setUser(sUser);
            dsDb2.setPassword(sPassword);
            Db2Connection connDb2 = (Db2Connection) dsDb2.getConnection();
            connDb2.setAutoCommit(false);
            connDb2.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
            _stmtDb2 = (Db2Statement) connDb2.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, ResultSet.HOLD_CURSORS_OVER_COMMIT);
            setStatement(_stmtDb2);
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testClass() {
        assertEquals("Wrong statement class!", Db2Statement.class, _stmtDb2.getClass());
    } /* testClass */

    /*------------------------------------------------------------------*/

    /** check, whether table exists in the database.
     * @param sMangledSchema schema name.
     * @param sMangledTable table name.
     * @return true, if table exists.
     * @throws SQLException if a database error occurred.
     */
    private boolean existsTable(String sMangledSchema, String sMangledTable) throws SQLException {
        boolean bExists = false;
        DatabaseMetaData dmd = _stmtDb2.getConnection()
                                       .getMetaData();
        ResultSet rs = dmd.getTables(null, ((BaseDatabaseMetaData) dmd).toPattern(sMangledSchema), ((BaseDatabaseMetaData) dmd).toPattern(sMangledTable), new String[]{"TABLE"});
        if (rs.next()) bExists = true;
        rs.close();
        return bExists;
    } /* existsTable */


    @Test
    @Override
    public void testExecuteUpdate() {
        enter();
        try {
            _stmtDb2.getConnection()
                    .setAutoCommit(true);
            if (existsTable(null, "TESTTABLESIMPLE")) {
                System.out.println("Executing " + _sSQL_CLEAN);
                _stmtDb2.executeUpdate(_sSQL_CLEAN);
            }
            System.out.println("Executing " + _sSQL_DDL);
            _stmtDb2.executeUpdate(_sSQL_DDL);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        } finally {
            try {
                clean();
            } catch (SQLException se) {
                fail(EU.getExceptionMessage(se));
            }
        }
    } /* testExecuteUpdate */

    @Test
    @Override
    public void testExecuteUpdate_String_int() {
        enter();
        /***
         try { _stmtDb2.executeUpdate(_sSQL_CLEAN); }
         catch(SQLException se) {}
         try { _stmtDb2.executeUpdate(_sSQL_DDL, Statement.NO_GENERATED_KEYS); }
         catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
         catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
         catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
         ***/
    } /* testExecuteUpdate_String_int */

    @Test
    @Override
    public void testExecuteUpdate_String_AInt() {
        enter();
        /***
         try { _stmtDb2.executeUpdate(_sSQL_CLEAN); }
         catch(SQLException se) {}
         try { _stmtDb2.executeUpdate(_sSQL_DDL, new int[] {1}); }
         catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
         catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
         catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
         ***/
    } /* testExecuteUpdate_String_AInt */

    @Test
    @Override
    public void testExecuteUpdate_String_AString() {
        enter();
        /***
         try { _stmtDb2.executeUpdate(_sSQL_CLEAN); }
         catch(SQLException se) {}
         try { _stmtDb2.executeUpdate(_sSQL_DDL, new String[]{"COL_A"}); }
         catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
         catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
         catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
         ***/
    } /* testExecuteUpdate */

    @Test
    @Override
    public void testGetGeneratedKeys() {
        enter();
        try {
            ResultSet rs = _stmtDb2.getGeneratedKeys();
            while (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    if (i > 0) System.out.print(", ");
                    System.out.print(rs.getString(i + 1));
                }
                System.out.println();
            }
            rs.close();
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        }
    } /* testGetGeneratedKeys */

    @Test
    @Override
    public void testExecute_String_AInt() {
        enter();
        /***
         try { _stmtDb2.execute(_sSQL_QUERY, new int[] {1}); }
         catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
         catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
         catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
         ***/
    } /* testExecute_String_AInt */

    @Test
    @Override
    public void testExecute_String_AString() {
        enter();
        /***
         try { _stmtDb2.execute(_sSQL_QUERY, new String[]{"COL_A"}); }
         catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
         catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
         catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
         ***/
    } /* testExecute_String_AString */

    @Test
    public void testExecuteQuery() {
        enter();
        changeUser(db2.getUsername(), db2.getPassword());
        try {
            if (existsTable("TESTDB2SCHEMA", "TCOMPLEX")) {
                String sSql = "DROP TABLE TESTDB2SCHEMA.TCOMPLEX CASCADE";
                _stmtDb2.executeUpdate(sSql);
                System.out.println("Dropped TESTDB2SCHEMA.TCOMPLEX");
            }
            String sSql = "CREATE TABLE TESTDB2SCHEMA.TCOMPLEX(" + "  CID INTEGER NOT NULL,\r\n" + "  CDISTINCT TESTDB2SCHEMA.TDB2DISTINCT,\r\n" +
                    //"  CUDTS TUDTS,\r\n" +
                    "  \"CARRAY.CARRAY[1]\" VARCHAR(256),\r\n" + "  \"CARRAY.CARRAY[2]\" VARCHAR(256),\r\n" + "  \"CARRAY.CARRAY[3]\" VARCHAR(256),\r\n" + "  \"CARRAY.CARRAY[4]\" VARCHAR(256),\r\n" +
                    //"  CUDTC TUDTC,\r\n" +
                    "  PRIMARY KEY(CID))";
            _stmtDb2.executeUpdate(sSql);
            System.out.println("Created TESTDB2SCHEMA.TCOMPLEX");
            sSql = "SELECT\r\n" + "  CID,\r\n" + "  CDISTINCT,\r\n" +
                    //"  CUDTS,\r\n"+
                    "  \"CARRAY.CARRAY[1]\",\r\n" + "  \"CARRAY.CARRAY[2]\",\r\n" + "  \"CARRAY.CARRAY[3]\",\r\n" + "  \"CARRAY.CARRAY[4]\"\r\n" +
                    //"  CUDTC\r\n"+
                    " FROM TESTDB2SCHEMA.TCOMPLEX";
            ResultSet rs = _stmtDb2.executeQuery(sSql);
            if (!rs.next()) System.out.println("Empty Table");
            rs.close();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        } finally {
            changeUser(db2.getUsername(), db2.getPassword());
        }
    } /* testExecuteQuery */

    @Test
    public void testExecute() {
        enter();
        try {
            _stmtDb2.execute(_sSQL_QUERY);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testExecute */

    @Test
    public void testExecute_String_int() {
        enter();
        try {
            _stmtDb2.execute(_sSQL_QUERY, Statement.NO_GENERATED_KEYS);
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testExecute_String_int */

    @Test
    public void testExecuteSelectSizes() {
        StringBuilder sbSql = new StringBuilder("SELECT COUNT(*) AS RECORDS");
        for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++) {
            TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
            if (tcd.getType()
                   .startsWith("BLOB") || tcd.getType()
                                             .startsWith("CLOB") || tcd.getType()
                                                                       .startsWith("NCLOB")) {
                sbSql.append(",\r\n  SUM(OCTET_LENGTH(");
                sbSql.append(SqlLiterals.formatId(tcd.getName()));
                sbSql.append(")) AS ");
                sbSql.append(SqlLiterals.formatId(tcd.getName() + "_SIZE"));
            }
        }
        sbSql.append("\r\nFROM ");
        sbSql.append(TestSqlDatabase.getQualifiedSimpleTable()
                                    .format());
        try {
            ResultSet rs = _stmtDb2.executeQuery(sbSql.toString());
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int iColumn = 0; iColumn < rsmd.getColumnCount(); iColumn++) {
                    String sColumnName = rsmd.getColumnLabel(iColumn + 1);
                    long lValue = rs.getLong(iColumn + 1);
                    System.out.println(sColumnName + ": " + String.valueOf(lValue));
                }
            }
            rs.close();
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        }
    } /* testExecuteSelectSize */

    @Test
    public void testDropTableCascade() {
        enter();
        try {
            _stmtDb2.executeUpdate("DROP TABLE " + TestSqlDatabase.getQualifiedSimpleTable()
                                                                  .format() + " CASCADE");
            // restore the database
            tearDown();
            setUpClass();
            setUp();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

}  /* class Db2StatementTester */
