package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.jdbcx.MsSqlDataSource;
import ch.admin.bar.siard2.mssql.TestMsSqlDatabase;
import ch.admin.bar.siard2.mssql.TestSqlDatabase;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import ch.enterag.utils.jdbc.BaseStatementTester;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.*;

import static org.junit.Assert.*;

public class MsSqlStatementTester extends BaseStatementTester {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @ClassRule
    public static MSSQLServerContainer<?> mssqlContainer = new MSSQLServerContainer<>(MSSQL_IMAGE)
            .acceptLicense()
            .withPassword(SA_PASSWORD);

    private static String _sDB_URL;
    private static String _sDB_USER;
    private static String _sDB_PASSWORD;
    private MsSqlStatement _stmtMsSql = null;

    private static final String _sSQL_DDL = "CREATE TABLE TESTTABLE(CCHAR CHARACTER,\r\n" +
            "CVARCHAR VARCHAR(256),\r\n" +
            "CCLOB CLOB(4M),\r\n" +
            "CNCHAR NCHAR,\r\n" +
            "CNCHAR_VARYING NCHAR VARYING(256),\r\n" +
            "CNCLOB NCLOB(4G),\r\n" +
            "CXML XML,\r\n" +
            "CBINARY BINARY,\r\n" +
            "CVARBINARY VARBINARY(256),\r\n" +
            "CBLOB BLOB,\r\n" +
            "CNUMERIC NUMERIC(10, 3),\r\n" +
            "CDECIMAL DECIMAL,\r\n" +
            "CSMALLINT SMALLINT,\r\n" +
            "CINTEGER INTEGER,\r\n" +
            "CBIGINT BIGINT,\r\n" +
            "CFLOAT FLOAT(7),\r\n" +
            "CREAL REAL,\r\n" +
            "CDOUBLE DOUBLE PRECISION,\r\n" +
            "CBOOLEAN BOOLEAN,\r\n" +
            "CDATE DATE,\r\n" +
            "CTIME TIME(3),\r\n" +
            "CTIMESTAMP TIMESTAMP(9),\r\n" +
            "CINTERVALYEAR INTERVAL YEAR(2) TO MONTH,\r\n" +
            "CINTERVALDAY INTERVAL DAY TO MINUTE,\r\n" +
            "CINTERVALSECOND INTERVAL SECOND(2, 5),\r\n" +
            "PRIMARY KEY(CINTEGER),\r\n" +
            "UNIQUE(CCHAR))";
    private static final String _sSQL_CLEAN = "DROP TABLE TESTTABLE RESTRICT";
    private static final String _sSQL_QUERY = "SELECT * FROM INFORMATION_SCHEMA.TABLES";

    protected void clean()
            throws SQLException {
        try {
            getStatement().executeUpdate(_sSQL_CLEAN);
            getStatement().getConnection()
                          .commit();
        } catch (SQLException se) {
            getStatement().getConnection()
                          .rollback();
        }
    }

    @BeforeClass
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
            /* drop and create the test database */
            new TestSqlDatabase(connMsSql);
            new TestMsSqlDatabase(connMsSql);
            connMsSql.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Before
    public void setUp() {
        MsSqlDataSource dsMsSql = new MsSqlDataSource();
        dsMsSql.setUrl(_sDB_URL);
        dsMsSql.setUser(_sDB_USER);
        dsMsSql.setPassword(_sDB_PASSWORD);
        try {
            MsSqlConnection connMsSql = (MsSqlConnection) dsMsSql.getConnection();
            connMsSql.setAutoCommit(false);
            _stmtMsSql = (MsSqlStatement) connMsSql.createStatement();
            setStatement(_stmtMsSql);
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }


    /** @return unwrapped MSSQL DataSource
     */
    private SQLServerStatement getUnwrapped() {
        SQLServerStatement ssstmt = null;
        try {
            ssstmt = (SQLServerStatement) _stmtMsSql.unwrap(Statement.class);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
        return ssstmt;
    }

    @Test
    public void testClass() {
        assertEquals("Wrong statement class!", MsSqlStatement.class, _stmtMsSql.getClass());
        try {
            ResultSet rs = getUnwrapped().executeQuery("SELECT DB_NAME() AS DbName");
            if (rs.next()) {
                String sDbName = rs.getString("DbName");
                // Testcontainers uses 'master' as the default database
                assertNotNull("Database name should not be null", sDbName);
            }
            rs.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testExecuteUpdate() {
        enter();
        try {
            clean();
            _stmtMsSql.executeUpdate(_sSQL_DDL);
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
    }

    @Test
    @Override
    public void testExecuteUpdate_String_int() {
        enter();
        try {
            clean();
            _stmtMsSql.executeUpdate(_sSQL_DDL, Statement.NO_GENERATED_KEYS);
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
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
    }

    @Test
    @Override
    public void testExecuteUpdate_String_AInt() {
        enter();
        try {
            clean();
            _stmtMsSql.executeUpdate(_sSQL_DDL, new int[]{1});
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
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
    }

    @Test
    @Override
    public void testExecuteUpdate_String_AString() {
        enter();
        try {
            clean();
            _stmtMsSql.executeUpdate(_sSQL_DDL, new String[]{"COL_A"});
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
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
    }

    @Test
    @Override
    public void testGetGeneratedKeys() {
        enter();
        try {
            _stmtMsSql.getGeneratedKeys();
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testExecute_String_AInt() {
        enter();
        try {
            _stmtMsSql.execute(_sSQL_QUERY, new int[]{1});
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testExecute_String_AString() {
        enter();
        try {
            _stmtMsSql.execute(_sSQL_QUERY, new String[]{"COL_A"});
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecuteQuery() {
        enter();
        try {
            _stmtMsSql.executeQuery("SELECT OCTET_LENGTH(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES");
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecuteSelectSizes() {
        StringBuilder sbSql = new StringBuilder("SELECT COUNT(*) AS RECORDS");
        for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++) {
            TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
            if (tcd.getType()
                   .startsWith("BLOB") ||
                    tcd.getType()
                       .startsWith("CLOB") ||
                    tcd.getType()
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
            ResultSet rs = _stmtMsSql.executeQuery(sbSql.toString());
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
    }

    @Test
    public void testDropTableCascade() {
        enter();
        try {
            QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
            _stmtMsSql.executeUpdate("DROP TABLE " + TestSqlDatabase.getQualifiedSimpleTable()
                                                                    .format() + " CASCADE");
            // close the database
            tearDown();
            // check whether table was actually dropped
            MsSqlDataSource dsMsSql = new MsSqlDataSource();
            dsMsSql.setUrl(_sDB_URL);
            dsMsSql.setUser(_sDB_USER);
            dsMsSql.setPassword(_sDB_PASSWORD);
            MsSqlConnection connMsSql = (MsSqlConnection) dsMsSql.getConnection();
            connMsSql.setAutoCommit(false);
            BaseDatabaseMetaData bdmd = (BaseDatabaseMetaData) connMsSql.getMetaData();
            ResultSet rs = bdmd.getTables(
                    qiTable.getCatalog(),
                    bdmd.toPattern(qiTable.getSchema()),
                    bdmd.toPattern(qiTable.getName()),
                    new String[]{"TABLE"});
            if (rs.next())
                System.err.println("Table was not dropped successfully!");
            rs.close();
            connMsSql.close();
            // restore the database
            setUpClass();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testCreateTableLarge() {
        enter();
        try {
            _stmtMsSql.executeUpdate("DROP TABLE BUG462 CASCADE");
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        }
        try {
            String sSql = "CREATE TABLE BUG462(\"JournaleintragRefId\" INTEGER,\r\n" +
                    "\"Beschreibung\" NCHAR VARYING(2147483647),\r\n" +
                    "\"Dateiname\" NCHAR VARYING(2147483647),\r\n" +
                    "\"DokumentInhalt\" VARBINARY(8000))";
            _stmtMsSql.executeUpdate(sSql);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }


}
