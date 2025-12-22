package ch.enterag.utils.jdbc;

import java.sql.*;
import java.util.*;
import static org.junit.Assert.fail;
import org.junit.*;

import ch.enterag.utils.*;
import ch.enterag.utils.database.*;
import ch.enterag.sqlparser.identifier.*;

public abstract class BaseResultSetMetaDataTester
{
  private ResultSet _rs = null;
  public ResultSet getResultSet() { return _rs; }
  private ResultSetMetaData _rsmd = null;
  public ResultSetMetaData getResultSetMetaData() { return _rsmd; }
  public BaseResultSetMetaData getBaseResultSetMetaData() { return (BaseResultSetMetaData)_rsmd; }
  /* setUp must create the database meta data and call this method */
  protected void setResultSetMetaData(ResultSetMetaData rsmd, ResultSet rs)
  {
    _rs = rs;
    _rsmd = rsmd;
  } /* setResultSetMetaData */
  
  protected static void printExceptionMessage(Exception e)
  {
    System.err.println("  "+EU.getExceptionMessage(e));
    System.err.flush();
  } /* printExceptionMessage */
  
  protected static void println(String s)
  {
    System.out.println("  "+s);
  } /* println */
  
  protected static void println(List<String> list)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < list.size(); i++)
    {
      if (i > 0)
        sb.append("\t");
      sb.append(String.valueOf(list.get(i)));
    }
    System.out.println(sb.toString());
  } /* println */
  
  protected static void print(ResultSet rs)
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
  Base tests for all database statements extending BaseResultSetMetaData.
  --------------------------------------------------------------------*/
  @Test
  public void testGetColumnCount()
  {
    enter();
    try { println(String.valueOf(_rsmd.getColumnCount())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetColumnCount */

  @Test
  public void testGetCatalogName()
  {
    enter();
    try
    {
      List<String> listCatalog = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listCatalog.add(_rsmd.getCatalogName(i));
      println(listCatalog);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetCatalogName */

  @Test
  public void testGetSchemaName()
  {
    enter();
    try
    {
      List<String> listSchema = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listSchema.add(_rsmd.getSchemaName(i));
      println(listSchema);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetSchemaName */

  @Test
  public void testGetTableName()
  {
    enter();
    try
    {
      List<String> listTable = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listTable.add(_rsmd.getTableName(i));
      println(listTable);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTableName */

  @Test
  public void testGetColumnName()
  {
    enter();
    try
    {
      List<String> listColumn = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listColumn.add(_rsmd.getColumnName(i));
      println(listColumn);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetColumnName */

  @Test
  public void testGetColumnLabel()
  {
    enter();
    try
    {
      List<String> listColumnLabel = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listColumnLabel.add(_rsmd.getColumnLabel(i));
      println(listColumnLabel);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetColumnLabel */

  @Test
  public void testGetColumnClassName()
  {
    enter();
    try
    {
      List<String> listColumnClassName = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listColumnClassName.add(_rsmd.getColumnLabel(i));
      println(listColumnClassName);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetColumnClassName */

  @Test
  public void testGetColumnType()
  {
    enter();
    try
    {
      List<String> listColumnType = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listColumnType.add(SqlTypes.getTypeName(_rsmd.getColumnType(i)));
      println(listColumnType);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetColumnType */

  @Test
  public void testGetColumnTypeName()
  {
    enter();
    try
    {
      List<String> listColumnTypeName = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listColumnTypeName.add(_rsmd.getColumnTypeName(i));
      println(listColumnTypeName);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetColumnTypeName */

  @Test
  public void testGetColumnDisplaySize()
  {
    enter();
    try
    {
      List<String> listColumnDisplaySize = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listColumnDisplaySize.add(String.valueOf(_rsmd.getColumnDisplaySize(i)));
      println(listColumnDisplaySize);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetColumnDisplaySize */

  @Test
  public void testGetPrecision()
  {
    enter();
    try
    {
      List<String> listPrecision = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listPrecision.add(String.valueOf(_rsmd.getPrecision(i)));
      println(listPrecision);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetPrecision */

  @Test
  public void testGetScale()
  {
    enter();
    try
    {
      List<String> listScale = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listScale.add(String.valueOf(_rsmd.getScale(i)));
      println(listScale);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetPrecision */

  @Test
  public void testIsAutoIncrement()
  {
    enter();
    try
    {
      List<String> listIsAutoIncrement = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listIsAutoIncrement.add(String.valueOf(_rsmd.isAutoIncrement(i)));
      println(listIsAutoIncrement);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetIsAutoIncrement */

  @Test
  public void testIsCaseSensitive()
  {
    enter();
    try
    {
      List<String> listIsCaseSensitive = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listIsCaseSensitive.add(String.valueOf(_rsmd.isCaseSensitive(i)));
      println(listIsCaseSensitive);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetIsCaseSensitive */

  @Test
  public void testIsSearchable()
  {
    enter();
    try
    {
      List<String> listIsSearchable = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listIsSearchable.add(String.valueOf(_rsmd.isSearchable(i)));
      println(listIsSearchable);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetIsSearchable */

  @Test
  public void testIsCurrency()
  {
    enter();
    try
    {
      List<String> listIsCurrency = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listIsCurrency.add(String.valueOf(_rsmd.isCurrency(i)));
      println(listIsCurrency);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetIsCurrency */

  @Test
  public void testIsNullable()
  {
    enter();
    try
    {
      List<String> listIsNullable = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listIsNullable.add(String.valueOf(_rsmd.isNullable(i)));
      println(listIsNullable);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetIsNullable */

  @Test
  public void testIsSigned()
  {
    enter();
    try
    {
      List<String> listIsSigned = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listIsSigned.add(String.valueOf(_rsmd.isSigned(i)));
      println(listIsSigned);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetIsSigned */

  @Test
  public void testIsReadOnly()
  {
    enter();
    try
    {
      List<String> listIsReadOnly = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listIsReadOnly.add(String.valueOf(_rsmd.isReadOnly(i)));
      println(listIsReadOnly);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetIsReadOnly */

  @Test
  public void testIsWritable()
  {
    enter();
    try
    {
      List<String> listIsWritable = new ArrayList<String>();
      for (int i = 1; i<= _rsmd.getColumnCount(); i++)
        listIsWritable.add(String.valueOf(_rsmd.isWritable(i)));
      println(listIsWritable);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetIsWritable */

  @Test
  public void testIsDefinitelyWritable()
  {
    enter();
    try
    {
      List<String> listIsDefinitelyWritable = new ArrayList<String>();
      for (int i = 1; i <= _rsmd.getColumnCount(); i++)
        listIsDefinitelyWritable.add(String.valueOf(_rsmd.isDefinitelyWritable(i)));
      println(listIsDefinitelyWritable);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetIsDefinitelyWritable */

  @Test
  public void testAll()
  {
    enter();
    List<String> list = new ArrayList<String>();
    list.add("Column");
    list.add("Label");
    list.add("Class");
    list.add("Type");
    list.add("TypeName");
    list.add("Size");
    list.add("Precision");
    list.add("Scale");
    list.add("AutoIncrement");
    list.add("CaseSensitive");
    list.add("Searchable");
    list.add("Currency");
    list.add("Nullable");
    list.add("Signed");
    list.add("ReadOnly");
    list.add("Writable");
    list.add("DefinitelyWritable");
    println(list);
    try
    {
      for (int i = 1; i <= _rsmd.getColumnCount(); i++)
      {
        list.clear();
        String sColumn = _rsmd.getColumnName(i);
        String sCatalogName = _rsmd.getCatalogName(i);
        if ((sCatalogName != null) && (sCatalogName.length() == 0))
          sCatalogName = null;
        String sSchemaName = _rsmd.getSchemaName(i);
        if ((sSchemaName != null) && (sSchemaName.length() == 0))
          sSchemaName = null;
        String sTableName = _rsmd.getTableName(i);
        if ((sTableName != null) && (sTableName.length() == 0))
          sTableName = null;
        QualifiedId qid = new QualifiedId(sCatalogName,sSchemaName,sTableName);
        if (qid.isSet())
          sColumn = qid.format() + "." + sColumn;
        list.add(sColumn);
        list.add(_rsmd.getColumnLabel(i));
        list.add(_rsmd.getColumnClassName(i));
        list.add(SqlTypes.getTypeName(_rsmd.getColumnType(i)));
        list.add(_rsmd.getColumnTypeName(i));
        list.add(String.valueOf(_rsmd.getColumnDisplaySize(i)));
        list.add(String.valueOf(_rsmd.getPrecision(i)));
        list.add(String.valueOf(_rsmd.getScale(i)));
        list.add(String.valueOf(_rsmd.isAutoIncrement(i)));
        list.add(String.valueOf(_rsmd.isCaseSensitive(i)));
        list.add(String.valueOf(_rsmd.isSearchable(i)));
        list.add(String.valueOf(_rsmd.isCurrency(i)));
        String sNullable = null;
        switch (_rsmd.isNullable(i))
        {
          case ResultSetMetaData.columnNoNulls:
            sNullable = "NoNulls";
            break;
          case ResultSetMetaData.columnNullable:
            sNullable = "Nullable";
            break;
          case ResultSetMetaData.columnNullableUnknown:
            sNullable = "NullableUnknown";
            break;
        }
        list.add(String.valueOf(sNullable));
        list.add(String.valueOf(_rsmd.isSigned(i)));
        list.add(String.valueOf(_rsmd.isReadOnly(i)));
        list.add(String.valueOf(_rsmd.isWritable(i)));
        list.add(String.valueOf(_rsmd.isDefinitelyWritable(i)));
        println(list);
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(Exception e) { fail(EU.getExceptionMessage(e)); }
  } /* testAll */
  
}  /* class BaseResultSetMetaDataTester */
