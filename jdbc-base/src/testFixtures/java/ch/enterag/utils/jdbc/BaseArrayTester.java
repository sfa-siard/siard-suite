package ch.enterag.utils.jdbc;

import static org.junit.Assert.*;
import org.junit.*;
import java.sql.*;
import java.util.*;
import ch.enterag.utils.*;

public abstract class BaseArrayTester
{
  private Array _array = null;
  
  protected static void printExceptionMessage(Exception e)
  {
    System.err.println("  "+EU.getExceptionMessage(e));
    System.err.flush();
  } /* printExceptionMessage */
  
  protected static final void print(ResultSet rs)
    throws SQLException
  {
    if ((rs != null) && (!rs.isClosed()))
    {
      ResultSetMetaData rsmd = rs.getMetaData();
      int iColumns = rsmd.getColumnCount();
      List<String> listColumns = new ArrayList<String>();
      StringBuilder sbLine = new StringBuilder();
      for (int iColumn = 0; iColumn < iColumns; iColumn++)
      {
        if (iColumn > 0)
          sbLine.append("\t");
        String sColumnName = rsmd.getColumnName(iColumn+1);
        sbLine.append(sColumnName);
        listColumns.add(sColumnName);
      }
      System.out.println(sbLine.toString());
      sbLine.setLength(0);
      while (rs.next())
      {
        for (int iColumn = 0; iColumn < iColumns; iColumn++)
        {
          if (iColumn > 0)
            sbLine.append("\t");
          String sValue = String.valueOf(rs.getObject(listColumns.get(iColumn)));
          sbLine.append(sValue);
        }
        System.out.println(sbLine.toString());
        sbLine.setLength(0);
      }
      rs.close();
    }
    else 
      fail("Invalid meta data result set!");
  }
  
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

  /* setUp must create the array instance and call this method */
  protected void setArray(Array array)
  {
    _array = (BaseArray)array;
  } /* setArray */

  @After
  public void tearDown()
  {
    try
    {
      if (_array != null)
        _array.free();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* tearDown */
  
  /*--------------------------------------------------------------------
  Base tests for all arrays extending BaseArray.
  --------------------------------------------------------------------*/
  @Test
  public void testGetBaseTypeName()
  {
    enter();
    try { System.out.println(_array.getBaseTypeName()); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBaseTypeName */

  @Test
  public void testGetBaseType()
  {
    enter();
    try { System.out.println(String.valueOf(_array.getBaseType())); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBaseType */

  @Test
  public void testGetArray()
  {
    enter();
    try { _array.getArray(); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetArray */

  @Test
  public void testGetArray_Long_Int()
  {
    enter();
    try { _array.getArray(1l,1); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetArray_Long_Int */

  @Test
  public void testGetResultSet()
  {
    enter();
    try { print(_array.getResultSet()); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetResultSet */

  @Test
  public void testGetResultSet_Long_Int()
  {
    enter();
    try { print(_array.getResultSet(1l,2)); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetResultSet_Long_int */

} /* BaseArrayTester */
