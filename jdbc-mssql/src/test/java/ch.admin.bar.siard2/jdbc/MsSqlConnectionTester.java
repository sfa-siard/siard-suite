package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.admin.bar.siard2.mssql.*;
import ch.admin.bar.siard2.jdbcx.*;

public class MsSqlConnectionTester extends BaseConnectionTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = MsSqlDriver.getUrl(_cp.getHost()+":"+_cp.getPort()+";databaseName="+_cp.getCatalog());
  private static final String _sDB_CATALOG = _cp.getCatalog();
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();

  private MsSqlConnection _connMsSql = null;

  @BeforeClass
  public static void setUpClass()
  {
    try 
    { 
      MsSqlDataSource dsMsSql = new MsSqlDataSource();
      dsMsSql.setUrl(_sDB_URL);
      dsMsSql.setUser(_sDB_USER);
      dsMsSql.setPassword(_sDB_PASSWORD);
      MsSqlConnection connMsSql = (MsSqlConnection)dsMsSql.getConnection();
      /* drop and create the test databases */
      new TestSqlDatabase(connMsSql);
      new TestMsSqlDatabase(connMsSql);
      connMsSql.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUpClass */
  
  @Before
  public void setUp()
  {
    try 
    { 
      MsSqlDataSource dsMsSql = new MsSqlDataSource();
      dsMsSql.setUrl(_sDB_URL);
      dsMsSql.setUser(_sDB_USER);
      dsMsSql.setPassword(_sDB_PASSWORD);
      _connMsSql = (MsSqlConnection)dsMsSql.getConnection();
      _connMsSql.setAutoCommit(false);
      setConnection(_connMsSql);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUp */
  
  @Test
  public void testClass()
  {
    assertEquals("Wrong connection class!", MsSqlConnection.class, _connMsSql.getClass());
  } /* testClass */

  @Test
  @Override
  public void testCreateArrayOf()
  {
    enter();
    try 
    { 
      Array array = _connMsSql.createArrayOf("VARCHAR(256)", new String[] {"a", "b", "c"});
      array.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* createArrayOf */
  
  
  @Test
  @Override
  public void testCreateStatement()
  {
    enter();
    try 
    {
      Statement stmt = _connMsSql.createStatement();
      assertEquals("Wrong statement class!", MsSqlStatement.class, stmt.getClass());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCreateStatement */
  
  @Test
  @Override
  public void testGetMetadata()
  {
    enter();
    try 
    {
      DatabaseMetaData dmd = _connMsSql.getMetaData();
      assertEquals("Wrong metadata class!", MsSqlDatabaseMetaData.class, dmd.getClass());
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetMetadata */

  @Test
  @Override
  public void testRollback()
  {
    enter();
    try { _connMsSql.rollback(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testRollback */
  
  @Test
  public void testSetSavepoint()
  {
    enter();
    try { _connMsSql.setSavepoint(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetSavepoint */
  
  @Test
  public void testSetSavepoint_String()
  {
    enter();
    try { _connMsSql.setSavepoint("TEST_SAVEPOINT"); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetSavepoint_String */
  
  @Test
  public void testRollback_Savepoint()
  {
    enter();
    try
    {
      Savepoint sp = _connMsSql.setSavepoint();
      _connMsSql.rollback(sp);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testRollback */
  
  @Test
  public void testReleaseSavePoint()
  {
    enter();
    try 
    { 
      Savepoint sp = _connMsSql.setSavepoint();
      _connMsSql.releaseSavepoint(sp);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testReleaseSavePoint */
  
  @Test
  @Override
  public void testPrepareStatement_String_AInt()
  {
    enter();
    try { _connMsSql.prepareStatement(_sSQL, new int[] {1}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareStatement_String_AInt */
  
  @Test
  @Override
  public void testPrepareStatement_String_AString()
  {
    enter();
    try { _connMsSql.prepareStatement(_sSQL,new String[]{"COL_A"}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareStatement_String_AString */
  
  
  @Test
  public void testSetCatalog()
  {
    enter();
    try { _connMsSql.setCatalog(_sDB_CATALOG); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetCatalog */
  
} /* class MsSqlConnectionTester */
