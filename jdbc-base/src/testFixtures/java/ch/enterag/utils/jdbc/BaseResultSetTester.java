package ch.enterag.utils.jdbc;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.math.*;
import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.sqlparser.*;

public abstract class BaseResultSetTester
{
  private ResultSet _rs = null;
  public ResultSet getResultSet() { return _rs; }
  public BaseResultSet getBaseResultSet() { return (BaseResultSet)_rs; }
  /* setUp must create the result set and call this method */
  protected void setResultSet(ResultSet rs) { _rs = rs; }
  
  protected static void printExceptionMessage(Exception e)
  {
    System.err.println("  "+EU.getExceptionMessage(e));
    System.err.flush();
  } /* printExceptionMessage */
  
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
      Connection conn = null;
      Statement stmt = null;
      if (_rs != null)
        stmt = _rs.getStatement();
      if ((stmt != null) && (!stmt.isClosed()))
        conn = stmt.getConnection();
      if ((_rs != null) && (!_rs.isClosed()))
        _rs.close();
      if ((stmt != null) && (!stmt.isClosed()))
        stmt.close();
      if ((conn != null) && (!conn.isClosed()))
      {
        conn.commit();
        conn.close();
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* tearDown */
  
  /*--------------------------------------------------------------------
  Base tests for all database statements extending BaseResultSet.
  --------------------------------------------------------------------*/
  @Test
  public void testGetStatement()
  {
    enter();
    try { _rs.getStatement(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetStatement */

  @Test
  public void testGetWarnings()
  {
    enter();
    try { _rs.getWarnings(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetWarnings */
  
  @Test
  public void testClearWarnings()
  {
    enter();
    try { _rs.clearWarnings(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testClearWarnings */
  
  @Test
  public void testGetCursorName()
  {
    enter();
    try { _rs.getCursorName(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetCursorName */
  
  @Test
  public void testClose()
  {
    enter();
    try 
    {
      if (!_rs.isClosed())
      {
        Statement stmt = _rs.getStatement();
        Connection conn = stmt.getConnection();
        _rs.close();
        _rs = null;
        if (!stmt.isClosed())
          stmt.close();
        if (!conn.isClosed())
        {
          conn.rollback();
          conn.close();
        }
      }
      else
        System.out.println("Already closed!");
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testClose */
  
  @Test
  public void testIsClosed()
  {
    enter();
    try { _rs.isClosed(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testIsClosed */
  
  @Test
  public void testIsBeforeFirst()
  {
    enter();
    try { _rs.isBeforeFirst(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testIsBeforeFirst */
  
  @Test
  public void testIsAfterLast()
  {
    enter();
    try { _rs.isAfterLast(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testIsAfterLast */
  
  @Test
  public void testIsFirst()
  {
    enter();
    try { _rs.isFirst(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testIsFirst */
  
  @Test
  public void testIsLast()
  {
    enter();
    try { _rs.isLast(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testIsLast */
  
  @Test
  public void testBeforeFirst()
  {
    enter();
    try { _rs.beforeFirst(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testBeforeFirst */
  
  @Test
  public void testAfterLast()
  {
    enter();
    try { _rs.afterLast(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testAfterLast */
  
  @Test
  public void testGetRow()
  {
    enter();
    try { _rs.getRow(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetRow */
  
  @Test
  public void testAbsolute()
  {
    enter();
    try { _rs.absolute(1); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testAbsolute */
  
  @Test
  public void testRelative()
  {
    enter();
    try { _rs.relative(1); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testRelative */
  
  @Test
  public void testPrevious()
  {
    enter();
    try 
    {
      _rs.next();
      _rs.previous();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrevious */
  
  @Test
  public void testFirst()
  {
    enter();
    try { _rs.first(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testFirst */
  
  @Test
  public void testLast()
  {
    enter();
    try { _rs.last(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testLast */
  
  @Test
  public void testNext()
  {
    enter();
    try { _rs.next(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrevious */
  
  @Test
  public void testMoveToInsertRow() throws SQLException
  {
    enter();
    try { _rs.moveToInsertRow(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMoveToInsertRow */

  @Test
  public void testMoveToCurrentRow() throws SQLException
  {
    enter();
    try { _rs.moveToCurrentRow(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMoveToCurrentRow */
  
  @Test
  public void testGetMetaData()
  {
    enter();
    try { _rs.getMetaData(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetMetaData */
  
  @Test
  public void testGetHoldability()
  {
    enter();
    try { _rs.getHoldability(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetHoldability */
  
  @Test
  public void testSetFetchDirection()
  {
    enter();
    try { _rs.setFetchDirection(ResultSet.FETCH_UNKNOWN); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetFetchDirection */
  
  @Test
  public void testGetFetchDirection()
  {
    enter();
    try { _rs.getFetchDirection(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetFetchDirection */
  
  @Test
  public void testSetFetchSize()
  {
    enter();
    try { _rs.setFetchSize(20); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetFetchSize */
  
  @Test
  public void testGetFetchSize()
  {
    enter();
    try { _rs.getFetchSize(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetFetchSize */
  
  @Test
  public void testGetType()
  {
    enter();
    try { _rs.getType(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetType */
  
  @Test
  public void testGetConcurrency()
  {
    enter();
    try { _rs.getConcurrency(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetConcurrency */
  
  @Test
  public void testFindColumn()
  {
    enter();
    try { _rs.findColumn("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testFindColumn */
  
  @Test
  public void testWasNull()
  {
    enter();
    try 
    {
      _rs.getObject(1);
      _rs.wasNull();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testWasNull */

  /* as BaseResultSet maps all column labels to column indexes using 
   * findColumn() it is sufficient to test the columnLabel versions ... 
   */
  @Test
  public void testUpdateNull()
  {
    enter();
    try { _rs.updateNull("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNull */
  
  @Test
  public void testGetString()
  {
    enter();
    try { _rs.getString("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetString */
  
  @Test
  public void testUpdateString()
  {
    enter();
    try { _rs.updateString("COL","Auää"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateString */
  
  @Test
  public void testGetNString()
  {
    enter();
    try { _rs.getNString("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetString */
  
  @Test
  public void testUpdateNString()
  {
    enter();
    try { _rs.updateNString("COL","Auää"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNString */
  
  @Test
  public void testGetBoolean()
  {
    enter();
    try { _rs.getBoolean("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBoolean */
  
  @Test
  public void testUpdateBoolean()
  {
    enter();
    try { _rs.updateBoolean("COL",true); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBoolean */
  
  @Test
  public void testGetByte()
  {
    enter();
    try { _rs.getByte("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetByte */
  
  @Test
  public void testUpdateByte()
  {
    enter();
    try { _rs.updateByte("COL",(byte)100); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateByte */
  
  @Test
  public void testGetShort()
  {
    enter();
    try { _rs.getShort("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetShort */
  
  @Test
  public void testUpdateShort()
  {
    enter();
    try { _rs.updateShort("COL",(short)10000); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateShort */
  
  @Test
  public void testGetInt()
  {
    enter();
    try { _rs.getInt("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetInt */
  
  @Test
  public void testUpdateInt()
  {
    enter();
    try { _rs.updateInt("COL",100000000); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateInt */
  
  
  @Test
  public void testGetLong()
  {
    enter();
    try { _rs.getLong("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetLong */
  
  @Test
  public void testUpdateLong()
  {
    enter();
    try { _rs.updateLong("COL",1000000000000l); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateLong */
  
  @Test
  public void testGetFloat()
  {
    enter();
    try { _rs.getFloat("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetFloat */
  
  @Test
  public void testUpdateFloat()
  {
    enter();
    try { _rs.updateFloat("COL",1.123456789f); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateFloat */
  
  @Test
  public void testGetDouble()
  {
    enter();
    try { _rs.getDouble("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetDouble */
  
  @Test
  public void testUpdateDouble()
  {
    enter();
    try { _rs.updateDouble("COL",1.1234567890123456789); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateDouble */
  
  @Test
  public void testGetBigDecimal()
  {
    enter();
    try { _rs.getBigDecimal("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBigDecimal */
  
  @Test
  public void testUpdateBigDecimal()
  {
    enter();
    try { _rs.updateBigDecimal("COL",BigDecimal.TEN); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBigDecimal */
  
  @Test
  @SuppressWarnings("deprecation")
  public void testGetBigDecimal_Int()
  {
    enter();
    try { _rs.getBigDecimal("COL",3); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBigDecimal_String_Int */
  
  @Test
  public void testGetBytes()
  {
    enter();
    try { _rs.getBytes("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBytes */
  
  @Test
  public void testUpdateBytes()
  {
    enter();
    try { _rs.updateBytes("COL",new byte[] {-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9}); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBytes */
  
  @Test
  public void testGetDate()
  {
    enter();
    try { _rs.getDate("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetDate */
  
  @Test
  public void testUpdateDate()
  {
    enter();
    try { _rs.updateDate("COL",new java.sql.Date((new java.util.Date()).getTime())); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateDate */
  
  @Test
  public void testGetDate_Calendar()
  {
    enter();
    Calendar cal = new GregorianCalendar();
    try { _rs.getDate("COL",cal); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetDate_Calendar */
  
  @Test
  public void testGetTime()
  {
    enter();
    try { _rs.getTime("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTime */
  
  @Test
  public void testUpdateTime()
  {
    enter();
    try { _rs.updateTime("COL",new java.sql.Time(12*60*60*1000)); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateTime */
  
  @Test
  public void testGetTime_Calendar()
  {
    enter();
    Calendar cal = new GregorianCalendar();
    try { _rs.getTime("COL",cal); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTime_Calendar */
  
  @Test
  public void testGetTimestamp()
  {
    enter();
    try { _rs.getTimestamp("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTimestamp */
  
  @Test
  public void testUpdateTimestamp()
  {
    enter();
    try { _rs.updateTimestamp("COL",new java.sql.Timestamp((new java.util.Date()).getTime())); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateTimestamp */
  
  @Test
  public void testGetTimestamp_Calendar()
  {
    enter();
    Calendar cal = new GregorianCalendar();
    try { _rs.getTimestamp("COL",cal); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTimestamp_Calendar */

  @Test
  public void testGetDuration()
  {
    enter();
    try { ((BaseResultSet)_rs).getDuration("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testUpdateDuration()
  {
    enter();
    Interval iv = new Interval(1,3,2);
    try { ((BaseResultSet)_rs).updateDuration("COL",iv.toDuration()); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateDuration */
  
  @Test
  public void testGetAsciiStream()
  {
    enter();
    try { _rs.getAsciiStream("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetAsciiStream */
  
  @Test
  public void testUpdateAsciiStream()
  {
    enter();
    try { _rs.updateAsciiStream("COL",new ByteArrayInputStream("abc".getBytes())); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateAsciiStream */
  
  @Test
  public void testUpdateAsciiStream_Int()
  {
    enter();
    try { _rs.updateAsciiStream("COL",new ByteArrayInputStream("abc".getBytes()),2); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateAsciiStream_Int */
  
  @Test
  public void testUpdateAsciiStream_Long()
  {
    enter();
    try { _rs.updateAsciiStream("COL",new ByteArrayInputStream("abc".getBytes()),2l); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateAsciiStream_Long */
  
  @Test
  @SuppressWarnings("deprecation")
  public void testGetUnicodeStream()
  {
    enter();
    try { _rs.getUnicodeStream("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetUnicodeStream */
  
  @Test
  public void testGetCharacterStream()
  {
    enter();
    try { _rs.getCharacterStream("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetCharacterStream */
  
  @Test
  public void testUpdateCharacterStream()
  {
    enter();
    try { _rs.updateCharacterStream("COL",new StringReader("auää")); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateCharacterStream */
  
  @Test
  public void testUpdateCharacterStream_Int()
  {
    enter();
    try { _rs.updateCharacterStream("COL",new StringReader("auää"),2); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateCharacterStream_Int */
  
  @Test
  public void testUpdateCharacterStream_Long()
  {
    enter();
    try { _rs.updateCharacterStream("COL",new StringReader("auää"),2l); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateCharacterStream_Long */
  
  @Test
  public void testGetNCharacterStream()
  {
    enter();
    try { _rs.getNCharacterStream("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetNCharacterStream */
  
  @Test
  public void testUpdateNCharacterStream()
  {
    enter();
    try { _rs.updateNCharacterStream("COL",new StringReader("auää")); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNCharacterStream */
  
  @Test
  public void testUpdateNCharacterStream_Int()
  {
    enter();
    try { _rs.updateNCharacterStream("COL",new StringReader("auää"),2); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNCharacterStream_String_Int */
  
  @Test
  public void testUpdateNCharacterStream_Long()
  {
    enter();
    try { _rs.updateNCharacterStream("COL",new StringReader("auää"),2l); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNCharacterStream_String_Long */
  
  @Test
  public void testGetBinaryStream()
  {
    enter();
    try { _rs.getBinaryStream("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBinaryStream */
  
  @Test
  public void testUpdateBinaryStream()
  {
    enter();
    try { _rs.updateBinaryStream("COL",new ByteArrayInputStream(new byte[] {-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9})); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBinaryStream */
  
  @Test
  public void testUpdateBinaryStream_Int()
  {
    enter();
    try { _rs.updateBinaryStream("COL",new ByteArrayInputStream(new byte[] {-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9}),2); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBinaryStream_Int */
  
  @Test
  public void testUpdateBinaryStream_Long()
  {
    enter();
    try { _rs.updateBinaryStream("COL",new ByteArrayInputStream(new byte[] {-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9}),2l); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBinaryStream_Long */
  
  @Test
  public void testGetObject()
  {
    enter();
    try { _rs.getObject("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObject */
  
  @Test
  public void testUpdateObject()
  {
    enter();
    try { _rs.updateObject("COL",Integer.valueOf(12345)); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateObject */
  
  @Test
  public void testUpdateObject_Int()
  {
    enter();
    try { _rs.updateObject("COL",Integer.valueOf(12345),2); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateObject_String_Int */
  
  @Test
  public void testGetObject_Map()
  {
    enter();
    Map<String,Class<?>> map = new HashMap<String,Class<?>>();
    map.put(String.class.getName(), String.class);
    try { _rs.getObject("COL",map); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObject_Map */
  
  @Test
  public void testGetObject_Class()
  {
    enter();
    try { _rs.getObject("COL",String.class); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObject_Class */
  
  @Test
  public void testGetRef()
  {
    enter();
    try { _rs.getRef("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetRef */
  
  @Test
  public void testUpdateRef()
  {
    enter();
    try 
    {
      Ref ref = _rs.getRef("COL");
      _rs.updateRef(1,ref); 
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateRef */
  
  @Test
  public void testGetBlob()
  {
    enter();
    try { _rs.getBlob("COL"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBlob */
  
  @Test
  public void testUpdateBlob()
  {
    enter();
    try 
    { 
      Blob blob = _rs.getStatement().getConnection().createBlob();
      blob.setBytes(1l, new byte[]{1,2,3});
      _rs.updateBlob("COL",blob);
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBlob */
  
  @Test
  public void testUpdateBlob_InputStream()
  {
    enter();
    try { _rs.updateBlob("COL",new ByteArrayInputStream(new byte[]{1,2,3})); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBlob_InputStream */
  
  @Test
  public void testUpdateBlob_InputStream_Long()
  {
    enter();
    try { _rs.updateBlob("COL",new ByteArrayInputStream(new byte[]{1,2,3}),3l); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBlob_String_InputStream_Long */
  
  @Test
  public void testGetClob()
  {
    enter();
    try 
    { 
      Clob clob = _rs.getClob("COL");
      clob.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetClob */
  
  @Test
  public void testUpdateClob()
  {
    enter();
    try 
    { 
      Clob clob = _rs.getStatement().getConnection().createClob();
      clob.setString(1l, "abcd");
      _rs.updateClob("COL",clob);
      clob.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateClob */
  
  @Test
  public void testUpdateClob_Reader()
  {
    enter();
    try 
    { 
      _rs.updateClob("COL",new StringReader("auää"));
      _rs.getClob("COL").free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateClob_Reader */
  
  @Test
  public void testUpdateClob_Reader_Long()
  {
    enter();
    try 
    { 
      _rs.updateClob("COL",new StringReader("auää"),4l); 
      _rs.getClob("COL").free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateClob_Reader_Long */
  
  @Test
  public void testGetNClob()
  {
    enter();
    try 
    { 
      NClob nclob = _rs.getNClob("COL");
      nclob.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetNClob */
  
  @Test
  public void testUpdateNClob()
  {
    enter();
    try 
    {
      NClob nclob = _rs.getStatement().getConnection().createNClob();
      nclob.setString(1l, "auää");
      _rs.updateNClob("COL",nclob);
      nclob.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNClob */
  
  @Test
  public void testUpdateNClob_Reader()
  {
    enter();
    try 
    { 
      _rs.updateNClob("COL",new StringReader("auää"));
      _rs.getNClob("COL").free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNClob_Reader */
  
  @Test
  public void testUpdateNClob_Reader_Long()
  {
    enter();
    try 
    { 
      _rs.updateNClob("COL",new StringReader("auää"),2l);
      _rs.getNClob("COL").free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNClob_Reader_Long */
  
  @Test
  public void testGetSqlXml()
  {
    enter();
    try 
    { 
      SQLXML sqlxml = _rs.getSQLXML("COL");
      sqlxml.free();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetSqlXml */
  
  @Test
  public void testUpdateSqlXml()
  {
    enter();
    try 
    { 
      SQLXML sqlxml = _rs.getSQLXML(1);
      _rs.updateSQLXML("COL",sqlxml);
      sqlxml.free();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateSqlXml */
  
  @Test
  public void testGetArray()
  {
    enter();
    try 
    { 
      Array array = _rs.getArray("COL");
      array.free();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetArray */
  
  @Test
  public void testUpdateArray()
  {
    enter();
    try 
    { 
      Array array = _rs.getArray(1);
      _rs.updateArray("COL",array);
      array.free();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateArray */
  
  @Test
  public void testGetRowId()
  {
    enter();
    try { _rs.getRowId("COL"); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetRowId */
  
  @Test
  public void testUpdateRowId()
  {
    enter();
    try 
    { 
      RowId rowid = _rs.getRowId(1);
      _rs.updateRowId("COL",rowid); 
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateRowId */

  @Test
  public void testGetUrl() throws MalformedURLException, SQLException {
    enter();
    _rs.getURL("COL");
  }
  
  @Test
  public void testInsertRow() throws SQLException
  {
    enter();
    try 
    {
      _rs.moveToInsertRow();
      // value updates go here 
      _rs.insertRow(); 
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testInsertRow */

  @Test
  public void testUpdateRow() throws SQLException
  {
    enter();
    try { _rs.updateRow(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateRow */

  @Test
  public void testDeleteRow() throws SQLException
  {
    enter();
    try { _rs.deleteRow(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testDeleteRow */

  @Test
  public void testRefreshRow() throws SQLException
  {
    enter();
    try { _rs.refreshRow(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* refreshRow */

  @Test
  public void testRowUpdated()
  {
    enter();
    try { _rs.rowUpdated(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testRowUpdated */
  
  @Test
  public void testRowInserted()
  {
    enter();
    try { _rs.rowInserted(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testRowInserted */
  
  @Test
  public void testRowDeleted()
  {
    enter();
    try { _rs.rowDeleted(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testRowDeleted */

  @Test
  public void testCancelRowUpdates() throws SQLException
  {
    enter();
    try { _rs.cancelRowUpdates(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
  } /* testCancelRowUpdates */

}
