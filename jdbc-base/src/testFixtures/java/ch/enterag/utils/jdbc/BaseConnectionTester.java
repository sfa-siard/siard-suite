package ch.enterag.utils.jdbc;

import java.sql.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;

public abstract class BaseConnectionTester
{
  protected static final String _sSQL = "SELECT * FROM INFORMATION_SCHEMA.TABLES";
  private Connection _conn = null;
  protected Connection getConnection() { return _conn; }
  protected BaseConnection getBaseConnection() { return (BaseConnection)_conn; }
  /* setUp must open the connection and call this method */
  protected void setConnection(Connection conn) { _conn = conn; }

  private String getCallingMethod(int iDepth)
  {
    String sCallingMethod = null;
    
    StackTraceElement[] asSte = Thread.currentThread().getStackTrace();
    sCallingMethod = asSte[iDepth].getMethodName();
    return sCallingMethod;
  } /* getCallingMethod */
  
  protected void enter()
  {
    System.out.println(getCallingMethod(3));
    System.out.flush();
  } /* enter */
  
  @After
  public void tearDown()
  {
    try
    {
      if ((_conn != null) && (!_conn.isClosed()))
      {
        _conn.commit();
        _conn.close();
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* tearDown */
  
  /*--------------------------------------------------------------------
  Base tests for all database connections extending BaseConnection.
  --------------------------------------------------------------------*/
  @Test
  public void testCreateStatement()
  {
    enter();
    try { _conn.createStatement(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCreateStatement */
  
  @Test
  public void testPrepareStatement()
  {
    enter();
    try { _conn.prepareStatement(_sSQL); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareStatement */
  
  @Test
  public void testPrepareCall()
  {
    enter();
    try { _conn.prepareCall(_sSQL); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareCall*/
  
  @Test
  public void testNativeSql()
  {
    enter();
    try { _conn.nativeSQL(_sSQL); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testNativeSql */
  @Test
  public void testSetAutoCommit()
  {
    enter();
    try { _conn.setAutoCommit(false); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetAutoCommit */
  
  @Test
  public void testGetAutoCommit()
  {
    enter();
    try { _conn.getAutoCommit(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetAutoCommit */
  
  @Test
  public void testCommit()
  {
    enter();
    try { _conn.commit(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCommit */
  
  @Test
  public void testRollback()
  {
    enter();
    try { _conn.rollback(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testRollback */
  
  @Test
  public void testClose()
  {
    enter();
    try { _conn.close(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testClose */
  
  @Test
  public void testIsClosed()
  {
    enter();
    try { assertSame("Connection is closed!", false, _conn.isClosed()); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testIsClosed */
  
  @Test
  public void testGetMetadata()
  {
    enter();
    try { _conn.getMetaData(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetMetadata */
  
  @Test
  public void testSetReadOnly()
  {
    enter();
    try { _conn.setReadOnly(false); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetReadOnly */
  
  @Test
  public void testIsReadOnly()
  {
    enter();
    try { _conn.isReadOnly(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testIsReadOnly */
  
  @Test
  public void testSetCatalog()
  {
    enter();
    try { _conn.setCatalog("TEST_CATALOG"); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetCatalog */
  
  @Test
  public void testGetCatalog()
  {
    enter();
    try { _conn.getCatalog(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetCatalog */
  
  @Test
  public void testSetTransactionIsolation()
  {
    enter();
    try { _conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetTransactionIsolation */
  
  @Test
  public void testGetTransactionIsolation()
  {
    enter();
    try { _conn.getTransactionIsolation(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTransactionIsolation */
  
  @Test
  public void testGetWarnings()
  {
    enter();
    try { _conn.getWarnings(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetWarnings */
  
  @Test
  public void testClearWarnings()
  {
    enter();
    try { _conn.clearWarnings(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testClearWarnings */
  
  @Test
  public void testCreateStatement_Int_Int()
  {
    enter();
    try 
    { 
      _conn.createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE, 
        ResultSet.CONCUR_READ_ONLY);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCreateStatement_Int_Int */
  
  @Test
  public void testPrepareStatement_String_Int_Int()
  {
    enter();
    try 
    { 
      _conn.prepareStatement(_sSQL,
        ResultSet.TYPE_SCROLL_INSENSITIVE, 
        ResultSet.CONCUR_READ_ONLY);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareStatement_String_Int_Int */
  
  @Test
  public void testPrepareCall_String_Int_Int()
  {
    enter();
    try 
    { 
      _conn.prepareCall(_sSQL,
        ResultSet.TYPE_SCROLL_INSENSITIVE, 
        ResultSet.CONCUR_READ_ONLY);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareCall_String_Int_Int */
  
  @Test
  public void testGetTypeMap()
  {
    enter();
    try { _conn.getTypeMap(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTypeMap */
  
  @Test
  public void testSetTypeMap()
  {
    enter();
    try { _conn.setTypeMap(new HashMap<String, Class<?>>()); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetTypeMap */
  
  @Test
  public void testSetHoldability()
  {
    enter();
    try { _conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetHoldability */
  
  @Test
  public void testGetHoldability()
  {
    enter();
    try { _conn.getHoldability(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetHoldability */
  
  @Test
  public void testSetSavepoint()
  {
    enter();
    try { _conn.setSavepoint(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetSavepoint */
  
  @Test
  public void testSetSavepoint_String()
  {
    enter();
    try { _conn.setSavepoint("TEST_SAVEPOINT"); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetSavepoint_String */
  
  @Test
  public void testRollback_Savepoint()
  {
    enter();
    try
    {
      Savepoint sp = _conn.setSavepoint();
      _conn.rollback(sp);
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
      Savepoint sp = _conn.setSavepoint();
      _conn.releaseSavepoint(sp);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testReleaseSavePoint */
  
  @Test
  public void testCreateStatement_Int_Int_Int()
  {
    enter();
    try 
    { 
      _conn.createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE, 
        ResultSet.CONCUR_READ_ONLY,
        ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCreateStatement_Int_Int_Int */
  
  @Test
  public void testPrepareStatement_String_Int_Int_Int()
  {
    enter();
    try 
    { 
      _conn.prepareStatement(_sSQL,
        ResultSet.TYPE_SCROLL_INSENSITIVE, 
        ResultSet.CONCUR_READ_ONLY,
        ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareStatement_String_Int_Int_Int */
  
  @Test
  public void testPrepareCall_String_Int_Int_Int()
  {
    enter();
    try 
    { 
      _conn.prepareCall(_sSQL,
        ResultSet.TYPE_SCROLL_INSENSITIVE, 
        ResultSet.CONCUR_READ_ONLY,
        ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareCall_String_Int_Int_Int */
  
  @Test
  public void testPrepareStatement_String_Int()
  {
    enter();
    try { _conn.prepareStatement(_sSQL, Statement.NO_GENERATED_KEYS); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareStatement_String_Int */
  
  @Test
  public void testPrepareStatement_String_AInt()
  {
    enter();
    try { _conn.prepareStatement(_sSQL, new int[] {1,2}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareStatement_String_AInt */
  
  @Test
  public void testPrepareStatement_String_AString()
  {
    enter();
    try { _conn.prepareStatement(_sSQL,new String[]{"COL_A", "COL_B"}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrepareStatement_String_AString */
  
  @Test
  public void testCreateClob()
  {
    enter();
    try
    {
      Clob clob = _conn.createClob();
      assertEquals(0,clob.length());
      clob.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCreateClob */
  
  @Test
  public void testCreateBlob()
  {
    enter();
    try
    {
      Blob blob = _conn.createBlob();
      assertEquals(0, blob.length());
      blob.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCreateBlob */
  
  @Test
  public void testCreateNClob()
  {
    enter();
    try
    { 
      NClob nclob = _conn.createNClob(); 
      assertEquals(0, nclob.length());
      nclob.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCreateNClob */
  
  @Test
  public void testCreateSqlXml()
  {
    enter();
    try 
    { 
      SQLXML sqlxml = _conn.createSQLXML(); 
      sqlxml.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCreateSqlXml */

  @Test
  public void testValid()
  {
    enter();
    try 
    { 
      int iTimeoutSec = 30;
      assertSame("Connection is not valid!",true,_conn.isValid(iTimeoutSec)); 
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testValid */

  @Test
  public void testSetClientInfo_String_String()
  {
    enter();
    try { _conn.setClientInfo("name","value"); }
    catch(SQLClientInfoException scie) { System.out.println(EU.getExceptionMessage(scie)); }
  } /* testSetClientInfo_String_String */
  
  @Test
  public void testSetClientInfo()
  {
    enter();
    try
    {
      Properties props = new Properties();
      _conn.setClientInfo(props);
    }
    catch(SQLClientInfoException scie) { System.out.println(EU.getExceptionMessage(scie)); }
  } /* testSetClientInfo */
  
  @Test
  public void testGetClientInfo_String()
  {
    enter();
    try { _conn.getClientInfo("TEST_NAME"); }
    catch(SQLClientInfoException scie) { System.out.println(EU.getExceptionMessage(scie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetClientInfo_String */
  
  @Test
  public void testGetClientInfo()
  {
    enter();
    try { _conn.getClientInfo(); }
    catch(SQLClientInfoException scie) { System.out.println(EU.getExceptionMessage(scie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetClientInfo */

  @Test
  public void testCreateArrayOf()
  {
    enter();
    try 
    { 
      Array array = _conn.createArrayOf("VARCHAR(255)", new String[] {"a", "bc", "abc"});
      array.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* createArrayOf */
  
  @Test
  public void testCreateStruct()
  {
    enter();
    try { _conn.createStruct("TEST_SCHEMA.TEST_STRUCT_TYPE", new String[] {"a", "b", "c"}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testCreateStruct */
  
  @Test
  public void testSetSchema()
  {
    enter();
    try { _conn.setSchema("TEST_SCHEMA"); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetSchema */
  
  @Test
  public void testGetSchema()
  {
    enter();
    try { _conn.getSchema(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetSchema */

  @Test
  public void testAbort()
  {
    enter();
    try { _conn.abort(null); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
    catch(SecurityException se) { fail(EU.getExceptionMessage(se)); }
  } /* testAbort */
  
  @Test
  public void testSetNetworkTimeout()
  {
    enter();
    try
    {
      int iTimeoutMs = 10000;
      _conn.setNetworkTimeout(null, iTimeoutMs);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(SecurityException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetNetworkTimeout */
  
  @Test
  public void testGetNetworkTimeout()
  {
    enter();
    try
    {
      int iTimeoutMs = _conn.getNetworkTimeout();
      System.out.println("Timeout [ms]: "+String.valueOf(iTimeoutMs));
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetNetworkTimeout */

} /* BaseConnectionTester */
