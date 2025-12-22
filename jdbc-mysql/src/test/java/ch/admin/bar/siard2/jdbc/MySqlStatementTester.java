package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.mysql.*;

public class MySqlStatementTester extends BaseStatementTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = MySqlDriver.getUrl(_cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog(),true);
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();

  private MySqlStatement _stmtMySql = null;

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
      throws SQLException
    {
      try 
      { 
        getStatement().executeUpdate(_sSQL_CLEAN);
        getStatement().getConnection().commit();
      }
      catch(SQLException se) { getStatement().getConnection().rollback(); }
    } /* clean */
    
  @BeforeClass 
  public static void setUpClass()
  {
    try
    {
      MySqlDataSource dsMySql = new MySqlDataSource();
      dsMySql.setUrl(_sDB_URL);
      dsMySql.setUser(_sDBA_USER);
      dsMySql.setPassword(_sDBA_PASSWORD);
      MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
      new TestSqlDatabase(connMySql);
      TestMySqlDatabase.grantSchemaUser(connMySql, 
        TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
      new TestMySqlDatabase(connMySql);
      TestMySqlDatabase.grantSchemaUser(connMySql, 
        TestMySqlDatabase._sTEST_SCHEMA, _sDB_USER);
      connMySql.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUpClass */
  
  @Before
  public void setUp()
  {
    try 
    { 
      MySqlDataSource dsMySql = new MySqlDataSource();
      dsMySql.setUrl(_sDB_URL);
      dsMySql.setUser(_sDB_USER);
      dsMySql.setPassword(_sDB_PASSWORD);
      MySqlConnection connMySql = (MySqlConnection)dsMySql.getConnection();
      connMySql.setAutoCommit(false);
      _stmtMySql = (MySqlStatement)connMySql.createStatement();
      setStatement(_stmtMySql);
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* setUp */
  
  @Test
  public void testClass()
  {
    assertEquals("Wrong statement class!", MySqlStatement.class, _stmtMySql.getClass());
  } /* testClass */

  @Test
  @Override
  public void testExecuteUpdate()
  {
    enter();
    try 
    {
      clean();
      _stmtMySql.executeUpdate(_sSQL_DDL); 
    }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    finally
    {
      try { clean(); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    }
  } /* testExecuteUpdate */
  
  @Test
  @Override
  public void testExecuteUpdate_String_int()
  {
    enter();
    try 
    { 
      clean();
      _stmtMySql.executeUpdate(_sSQL_DDL, Statement.NO_GENERATED_KEYS); 
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    finally
    {
      try { clean(); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    }
  } /* testExecuteUpdate_String_int */
  
  @Test
  @Override
  public void testExecuteUpdate_String_AInt()
  {
    enter();
    try 
    {
      clean();
      _stmtMySql.executeUpdate(_sSQL_DDL, new int[] {1});
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    finally
    {
      try { clean(); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    }
  } /* testExecuteUpdate_String_AInt */
  
  @Test
  @Override
  public void testExecuteUpdate_String_AString()
  {
    enter();
    try 
    { 
      clean();
      _stmtMySql.executeUpdate(_sSQL_DDL, new String[]{"COL_A"});
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    finally
    {
      try { clean(); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    }
  } /* testExecuteUpdate */

  @Test
  @Override
  public void testGetGeneratedKeys()
  {
    enter();
    try { _stmtMySql.getGeneratedKeys(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* testGetGeneratedKeys */
  
  @Test
  @Override
  public void testExecute_String_AInt()
  {
    enter();
    try { _stmtMySql.execute(_sSQL_QUERY, new int[] {1}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testExecute_String_AInt */
  
  @Test
  @Override
  public void testExecute_String_AString()
  {
    enter();
    try { _stmtMySql.execute(_sSQL_QUERY, new String[]{"COL_A"}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testExecute_String_AString */
  
  @Test
  public void testExecuteQuery()
  {
    enter();
    try { _stmtMySql.executeQuery("SELECT OCTET_LENGTH(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES"); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testExecuteQuery */
  
  @Test
  public void testExecuteQueryWithUpdate()
  {
    enter();
    try { _stmtMySql.executeUpdate(_sSQL_CLEAN); }
    catch(SQLException se) {}
    try 
    { 
      int iResult = _stmtMySql.executeUpdate("CREATE TABLE TESTTABLE(ID INTEGER, CVARCHAR VARCHAR(255))");
      assertEquals("CREATE TABLE failed!",0,iResult);
      Statement stmt = _stmtMySql.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE,ResultSet.HOLD_CURSORS_OVER_COMMIT);
      ResultSet rs = stmt.executeQuery("SELECT ID, CVARCHAR FROM TESTTABLE");
      rs.moveToInsertRow();
      rs.updateInt("ID", 2);
      rs.updateString("CVARCHAR", "TESTSTRING");
      rs.insertRow(); // this fails without primary keys ...
      rs.close();
    }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetResultSet()
  {
    enter();
    try 
    {
      _stmtMySql.execute("SELECT OCTET_LENGTH(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES");
      _stmtMySql.getResultSet(); 
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetResultSet */

  @Test
  public void testExecuteSelectSizes()
  {
    StringBuilder sbSql = new StringBuilder("SELECT COUNT(*) AS RECORDS");
    for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++)
    {
      TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
      if (tcd.getType().startsWith("BLOB") ||
        tcd.getType().startsWith("CLOB") ||
        tcd.getType().startsWith("NCLOB"))
      {
        sbSql.append(",\r\n  SUM(OCTET_LENGTH(");
        sbSql.append(SqlLiterals.formatId(tcd.getName()));
        sbSql.append(")) AS ");
        sbSql.append(SqlLiterals.formatId(tcd.getName()+"_SIZE"));
      }
    }
    sbSql.append("\r\nFROM ");
    sbSql.append(TestSqlDatabase.getQualifiedSimpleTable().format());
    try
    {
      ResultSet rs = _stmtMySql.executeQuery(sbSql.toString());
      ResultSetMetaData rsmd = rs.getMetaData();
      while(rs.next())
      {
        for (int iColumn = 0; iColumn < rsmd.getColumnCount(); iColumn++)
        {
          String sColumnName = rsmd.getColumnLabel(iColumn+1);
          long lValue = rs.getLong(iColumn+1);
          System.out.println(sColumnName+": "+String.valueOf(lValue));
        }
      }
      rs.close();
    }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* testExecuteSelectSize */

  @Test
  public void testDropTableCascade()
  {
    enter();
    try 
    {
      _stmtMySql.executeUpdate("DROP TABLE "+TestSqlDatabase.getQualifiedSimpleTable().format()+" CASCADE"); 
      // restore the database
      tearDown();
      setUpClass();
    }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testCreateTableLarge()
  {
    enter();
    try { _stmtMySql.executeUpdate("DROP TABLE BUG462 CASCADE"); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
    try
    {
      String sSql = "CREATE TABLE BUG462(\"JournaleintragRefId\" INTEGER,\r\n" + 
        "\"Beschreibung\" NCHAR VARYING(2147483647),\r\n" + 
        "\"Dateiname\" NCHAR VARYING(2147483647),\r\n" + 
        "\"DokumentInhalt\" VARBINARY(8000))";
      _stmtMySql.executeUpdate(sSql);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testCreateTable()
  {
    enter();
    try { _stmtMySql.executeUpdate("DROP TABLE BUG479 CASCADE"); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
    try 
    {
      String sSql = "CREATE TABLE BUG479(ID INT, TEXT VARCHAR)";
      int iResult = _stmtMySql.executeUpdate(sSql);
      System.out.println("Result: "+String.valueOf(iResult));
    }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    finally
    {
      try { clean(); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    }
  } /* testCreateTable */
  
}
