/*======================================================================
Application : SIARD2
Description : Db2ResultSet implements a wrapped DB/2 ResultSet.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 10.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.math.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.*;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;
import javax.xml.datatype.*;

import ch.enterag.utils.*;
import ch.enterag.utils.database.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.db2.*;

/*====================================================================*/
/** Db2ResultSet implements a wrapped DB/2 ResultSet.
 * @author Hartwig Thomas
 */
public class Db2ResultSet
  extends BaseResultSet
  implements ResultSet
{
  /** logger */
  @SuppressWarnings("unused")
  private static IndentLogger _il = IndentLogger.getIndentLogger(Db2ResultSet.class.getName());
  private static final int iMAX_INLINE_LENGTH = 164;
  private static final long _lMILLIS_PER_DAY = 1000l*60l*60l*24l;
  private ResultSetMetaData _rsmd = null;
  private List<Object> _listValues = null;
  private boolean _bNull = false;
  
  /*------------------------------------------------------------------*/
  public Db2ResultSet(ResultSet rs)
    throws SQLException
  {
    super(rs);
    _rsmd = getMetaData();
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Statement getStatement() throws SQLException
  {
    return new Db2Statement(super.getStatement());
  } /* getStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSetMetaData getMetaData() throws SQLException
  {
    return new Db2ResultSetMetaData(super.getMetaData(),getStatement().getConnection());
  } /* getMetaData */

  /*------------------------------------------------------------------*/
  /** compute the number of predefined columns in the struct.
   * @param struct
   * @return number of predefined columns in the struct.
   * @throws SQLException
   */
  private int getStructSize(Struct struct)
    throws SQLException
  {
    int iStructSize = 0;
    for (int i = 0; i < struct.getAttributes().length; i++)
    {
      Object o = struct.getAttributes()[i];
      if (o instanceof Struct)
      {
        Struct structAttr = (Struct)o;
        iStructSize = iStructSize + getStructSize(structAttr);
      }
      else
        iStructSize++;
    }
    return iStructSize;
  } /* getStructSize */
  
  /*------------------------------------------------------------------*/
  /** create a Struct from the values starting a iColumn (1-based).
   * @param iColumn column position.
   * @param sTypeName type name of Struct.
   */
  private Struct getStruct(int iColumn, String sTypeName)
    throws SQLException
  {
    Struct struct = null;
    try
    {
      List<Object> listAttributes = new ArrayList<Object>();
      QualifiedId qiType = new QualifiedId(sTypeName);
      Db2DatabaseMetaData dmd = (Db2DatabaseMetaData)getStatement().getConnection().getMetaData();
      ResultSet rs = dmd.getAttributes(qiType.getCatalog(),
        dmd.toPattern(qiType.getSchema()),
        dmd.toPattern(qiType.getName()), null);
      while (rs.next())
      {
        int iDataType = rs.getInt("DATA_TYPE");
        String sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
        if (iDataType != Types.STRUCT)
        {
          Object o = null;
          if (!isBeforeFirst())
          {
            try { o = super.getObject(iColumn); }
            catch(SQLException se) {} // on insert row!
          }
          o = mapValue(iDataType,o);
          listAttributes.add(o);
          iColumn++;
        }
        else
        {
          Struct structAttr = getStruct(iColumn, sAttrTypeName);
          iColumn = iColumn+getStructSize(structAttr);
          listAttributes.add(structAttr);
        }
      }
      rs.close();
      struct = getStatement().getConnection().createStruct(sTypeName, listAttributes.toArray());
    }
    catch(ParseException pe) { throw new SQLException("Type name \""+sTypeName+"\" could not be parsed!",pe); }
    return struct;
  } /* getStruct */

  /*------------------------------------------------------------------*/
  /** map value of predefined data type.
   * @param iDataType type.
   * @param o value.
   * @return mapped value.
   */
  private Object mapValue(int iDataType, Object o)
  {
    // map types
    if (o instanceof Byte)
    {
      Byte by = (Byte)o;
      byte[] buf = new byte[] { by.byteValue() };
      o = buf;
    }
    else if ((iDataType == Types.SMALLINT) && (o instanceof Integer))
    {
      Integer i = (Integer)o;
      Short sh = Short.valueOf(i.shortValue());
      o = sh;
    }
    else if ((iDataType == Types.OTHER) && (o instanceof Long))
    {
      Long l = (Long)o;
      int iSign = 0;
      if (l > 0)
        iSign = 1;
      else if (l < 0)
        iSign = -1;
      l = iSign*l;
      int iMonths = (int)(l % 12);
      int iYears = (int)(l / 12);
      Interval iv = new Interval(iSign,iYears,iMonths);
      o = iv.toDuration();
    }
    else if ((iDataType == Types.OTHER) && (o instanceof BigDecimal))
    {
      BigDecimal bd = (BigDecimal)o;
      int iSign = bd.signum();
      bd = bd.setScale(9);
      BigInteger bi = bd.unscaledValue();
      if (iSign < 0)
        bi = bi.negate();
      BigInteger biBillion = BigInteger.valueOf(1000000000l);
      long lNanos = bi.mod(biBillion).longValue();
      bi = bi.divide(biBillion);
      BigInteger biSixty = BigInteger.valueOf(60);
      int iSeconds = bi.mod(biSixty).intValue();
      bi = bi.divide(biSixty);
      int iMinutes = bi.mod(biSixty).intValue();
      bi = bi.divide(biSixty);
      BigInteger biTwentyFour = BigInteger.valueOf(24);
      int iHours = bi.mod(biTwentyFour).intValue();
      bi = bi.divide(biTwentyFour);
      int iDays = bi.intValue();
      Interval iv = new Interval(iSign,iDays,iHours,iMinutes,iSeconds,lNanos);
      o = iv.toDuration();
    }
    return o;    
  } /* mapValue */

  /*------------------------------------------------------------------*/
  private void freeRecord()
    throws SQLException
  {
    if (_listValues != null)
    {
      for (int iColumn = 0; iColumn < _listValues.size(); iColumn++)
      {
        Object oValue = _listValues.get(iColumn);
        if (oValue instanceof Blob)
        {
          Blob blob = (Blob)oValue;
          blob.free();
        }
        else if (oValue instanceof Clob)
        {
          Clob clob = (Clob)oValue;
          clob.free();
        }
        else if (oValue instanceof NClob)
        {
          NClob nclob = (NClob)oValue;
          nclob.free();
        }
        else if (oValue instanceof SQLXML)
        {
          SQLXML sqlxml = (SQLXML)oValue;
          sqlxml.free();
        }
      }
    }
  } /* freeRecord */
  
  /*------------------------------------------------------------------*/
  /** load the record set into the object list.
   * @throws SQLException
   */
  private void loadRecord() throws SQLException {
    freeRecord();
    _listValues = new ArrayList<>();
    int iColumn = 1;
    for (int iValue = 1; iValue <= _rsmd.getColumnCount(); iValue++) {
      int iDataType = getDataType(iValue);
      if (iDataType != Types.STRUCT) {
        Object o = null;
        if (iDataType == Types.DATALINK) {
          o = getURL(iColumn);
        } else if (!isBeforeFirst()) {
          try {
            o = super.getObject(iColumn);
          } catch (SQLException se) { } // on insert row!
        }
        o = mapValue(iDataType, o);
        _listValues.add(o);
        iColumn++;
      } else {
        Struct struct = getStruct(iColumn, _rsmd.getColumnTypeName(iValue));
        iColumn = iColumn + getStructSize(struct);
        _listValues.add(struct);
      }
    }
  }

  /*------------------------------------------------------------------*/
  /** update the record set from the Struct starting with iColumn.
   * @param iColumn column position.
   * @param struct Struct.
   * @throws SQLException
   */
  private void setStruct(int iColumn, Struct struct)
    throws SQLException
  {
    for (int iAttribute = 0; iAttribute < struct.getAttributes().length; iAttribute++)
    {
      Object oValue = struct.getAttributes()[iAttribute];
      if (oValue instanceof Struct)
      {
        Struct structAttr = (Struct)oValue;
        setStruct(iColumn,structAttr);
        iColumn = iColumn+getStructSize(struct);
      }
      else
      {
        super.updateObject(iColumn,oValue);
        iColumn++;
      }
    }
  } /* setStruct */
  
  /*------------------------------------------------------------------*/
  /** update the record set from the object list.
   * @throws SQLException
   */
  private void updateRecord()
    throws SQLException
  {
    int iColumn = 1;
    for (int iValue = 0; iValue < _listValues.size(); iValue++)
    {
      Object oValue = _listValues.get(iValue);
      if (oValue instanceof Struct)
      {
        Struct struct = (Struct)oValue;
        setStruct(iColumn,struct);
        iColumn = iColumn+getStructSize(struct);
      }
      else if (oValue instanceof Duration)
      {
        Duration duration = (Duration)oValue;
        Interval iv = Interval.fromDuration(duration);
        if ((iv.getYears() != 0) || (iv.getMonths() != 0))
        {
          long lMonths = (long)iv.getYears();
          lMonths = 12*lMonths + iv.getMonths();
          lMonths = iv.getSign()*lMonths;
          super.updateLong(iColumn,lMonths);
          iColumn++;
        }
        else
        {
          BigInteger biNanos = BigInteger.valueOf(iv.getSign());
          biNanos = biNanos.multiply(BigInteger.valueOf(iv.getDays()));
          biNanos = biNanos.multiply(BigInteger.valueOf(24)).add(BigInteger.valueOf(iv.getHours()));
          biNanos = biNanos.multiply(BigInteger.valueOf(60)).add(BigInteger.valueOf(iv.getMinutes()));
          biNanos = biNanos.multiply(BigInteger.valueOf(60)).add(BigInteger.valueOf(iv.getSeconds()));
          biNanos = biNanos.multiply(BigInteger.valueOf(1000000000)).add(BigInteger.valueOf(iv.getNanoSeconds()));
          super.updateBigDecimal(iColumn,new BigDecimal(biNanos,9));
          iColumn++;
        }
      }
      else
      {
        super.updateObject(iColumn,oValue);
        iColumn++;
      }
    }
  } /* updateRecord */

  /*------------------------------------------------------------------*/
  private String getLiteral(Object oValue, List<Object> listLobs)
    throws SQLException
  {
    String sLiteral = null;
    if (oValue != null)
    {
      if (oValue instanceof String)
      {
        String sValue = (String)oValue;
        if (sValue.length() < iMAX_INLINE_LENGTH)
          sLiteral = Db2Literals.formatStringLiteral(sValue);
        else
        {
          sLiteral = "?";
          listLobs.add(oValue);
        }
      }
      else if (oValue instanceof byte[])
      {
        byte[] bufValue = (byte[])oValue;
        if (bufValue.length < iMAX_INLINE_LENGTH)
          sLiteral = Db2Literals.formatBytesLiteral(bufValue);
        else
        {
          sLiteral = "?";
          listLobs.add(oValue);
        }
      }
      else if (oValue instanceof BigDecimal)
      {
        BigDecimal bdValue = (BigDecimal)oValue;
        sLiteral = Db2Literals.formatExactLiteral(bdValue);
      }
      else if (oValue instanceof Short)
      {
        Short shValue = (Short)oValue;
        sLiteral = String.valueOf(shValue.shortValue());
      }
      else if (oValue instanceof Integer)
      {
        Integer iValue = (Integer)oValue;
        sLiteral = String.valueOf(iValue.intValue());
      }
      else if (oValue instanceof Long)
      {
        Long lValue = (Long)oValue;
        sLiteral = String.valueOf(lValue.longValue());
      }
      else if (oValue instanceof Float)
      {
        Float fValue = (Float)oValue;
        sLiteral = Db2Literals.formatApproximateLiteral(fValue.doubleValue());
      }
      else if (oValue instanceof Double)
      {
        Double dValue = (Double)oValue;
        sLiteral = Db2Literals.formatApproximateLiteral(dValue.doubleValue());
      }
      else if (oValue instanceof Boolean)
      {
        Boolean bValue = (Boolean)oValue;
        sLiteral = Db2Literals.formatBooleanLiteral(bValue.booleanValue()?BooleanLiteral.TRUE:BooleanLiteral.FALSE);
      }
      else if (oValue instanceof Date)
      {
        Date dateValue = (Date)oValue;
        sLiteral = Db2Literals.formatDateLiteral(dateValue);
      }
      else if (oValue instanceof Time)
      {
        Time timeValue = (Time)oValue;
        sLiteral = Db2Literals.formatTimeLiteral(timeValue);
      }
      else if (oValue instanceof Timestamp)
      {
        Timestamp tsValue = (Timestamp)oValue;
        sLiteral = Db2Literals.formatTimestampLiteral(tsValue);
      }
      else if (oValue instanceof Duration)
      {
        Duration duration = (Duration)oValue;
        Interval ivValue = Interval.fromDuration(duration);
        sLiteral = Db2Literals.formatIntervalLiteral(ivValue);
      }
      else if ((oValue instanceof Blob) ||
               (oValue instanceof Clob) ||
               (oValue instanceof NClob) ||
               (oValue instanceof SQLXML))
      {
        sLiteral = "?";
        listLobs.add(oValue);
      }
      else if (oValue instanceof Struct)
      {
        Struct struct = (Struct)oValue;
        // turn it into NEW type(attr1 value1, attr2 value2, ...)
        sLiteral = formatStructValue(struct, listLobs);
      }
    }
    return sLiteral;
  } /* getLiteral */
  
  /*------------------------------------------------------------------*/
  private String formatStructValue(Struct struct, List<Object> listLobs)
    throws SQLException
  {
    StringBuilder sbSql = new StringBuilder();
    try
    {
      QualifiedId qiType = new QualifiedId(struct.getSQLTypeName());
      Db2DatabaseMetaData dmd = (Db2DatabaseMetaData)getStatement().getConnection().getMetaData();
      ResultSet rs = dmd.getAttributes(
        qiType.getCatalog(), 
        dmd.toPattern(qiType.getSchema()), 
        dmd.toPattern(qiType.getName()), 
        "%");
      for (int iAttribute = 0; iAttribute < struct.getAttributes().length; iAttribute++)
      {
        rs.next();
        Object oValue = struct.getAttributes()[iAttribute];
        oValue = getLiteral(oValue,listLobs);
        if (oValue != null)
        {
          String sAttrName = rs.getString("ATTR_NAME");
          sbSql.append(" ..");
          sbSql.append(sAttrName);
          sbSql.append("(");
          sbSql.append(oValue);
          sbSql.append(")");
        }
      }
      rs.close();
      if (sbSql.length() > 0)
        sbSql.insert(0,qiType.format()+"()");
    }
    catch(ParseException pe) { throw new SQLException("Type "+struct.getSQLTypeName()+" could not be parsed!"); }
    String sSql = sbSql.toString();
    if (sSql.length() == 0)
      sSql = null;
    return sbSql.toString();
  } /* formatStructValue */
  
  /*------------------------------------------------------------------*/
  private void insertRecord()
    throws SQLException
  {
    // original insertRow fails for column names with special characters!
    // super.insertRow();
    StringBuilder sbColumns = new StringBuilder();
    StringBuilder sbValues = new StringBuilder();
    List<Object> listLobs = new ArrayList<Object>();
    for (int iColumn = 0; iColumn < _listValues.size(); iColumn++)
    {
      Object oValue = _listValues.get(iColumn);
      if (oValue != null)
        oValue = getLiteral(oValue,listLobs);
      if (oValue != null)
      {
        if (sbColumns.length() > 0)
          sbColumns.append(",\r\n");
        if (sbValues.length() > 0)
          sbValues.append(",\r\n");
        Identifier idColumn = new Identifier(_rsmd.getColumnLabel(iColumn+1));
        sbColumns.append(idColumn.format());
        sbValues.append(oValue);
      }
    }
    QualifiedId qiTable = new QualifiedId();
    qiTable.setCatalog(null);
    qiTable.setSchema(_rsmd.getSchemaName(1));
    qiTable.setName(_rsmd.getTableName(1));
    StringBuilder sbSql = new StringBuilder("INSERT INTO ");
    sbSql.append(qiTable.format());
    sbSql.append("(\r\n");
    sbSql.append(sbColumns.toString());
    sbSql.append("\r\n) VALUES (\r\n");
    sbSql.append(sbValues.toString());
    sbSql.append(")");
    // create a prepared statement with ? for all LOBs
    PreparedStatement pstmt = getStatement().getConnection().unwrap(Connection.class).prepareStatement(sbSql.toString());
    for (int iLob = 0; iLob < listLobs.size(); iLob++)
    {
      Object oValue = listLobs.get(iLob);
      if (oValue instanceof String)
      {
        Reader rdrClob = new StringReader((String)oValue);
        pstmt.setCharacterStream(iLob+1, rdrClob);
      }
      else if (oValue instanceof byte[])
      {
        InputStream isBlob = new ByteArrayInputStream((byte[])oValue);
        pstmt.setBinaryStream(iLob+1, isBlob);
      }
      else if (oValue instanceof Blob)
      {
        Blob blob = (Blob)oValue;
        pstmt.setBinaryStream(iLob+1, blob.getBinaryStream());
      }
      else if (oValue instanceof Clob)
      {
        Clob clob = (Clob)oValue;
        pstmt.setCharacterStream(iLob+1, clob.getCharacterStream());
        
      }
      else if (oValue instanceof NClob)
      {
        NClob nclob = (NClob)oValue;
        pstmt.setCharacterStream(iLob+1, nclob.getCharacterStream());
      }
      else if (oValue instanceof SQLXML)
      {
        SQLXML sqlxml = (SQLXML)oValue;
        pstmt.setCharacterStream(iLob+1, sqlxml.getCharacterStream());
      }
    }
    // execute it
    int iResult = pstmt.executeUpdate();
    pstmt.close();
    if (iResult != 1)
      throw new SQLException("Insert into table "+qiTable.format()+" failed!");
  } /* insertRecord */
  
  /*------------------------------------------------------------------*/
  /** for debugging */
  @SuppressWarnings("unused")
  private void printState()
    throws SQLException
  {
    String s = null;
    switch(super.getType())
    {
      case ResultSet.TYPE_FORWARD_ONLY: s = "FORWARD_ONLY"; break;
      case ResultSet.TYPE_SCROLL_INSENSITIVE: s = "SCROLL_INSENSITIVE"; break;
      case ResultSet.TYPE_SCROLL_SENSITIVE: s = "SCROLL_SENSITIVE"; break;
    }
    s = s + ", ";
    switch(super.getConcurrency())
    {
      case ResultSet.CONCUR_READ_ONLY: s = s + "CONCUR_READ_ONLY"; break;
      case ResultSet.CONCUR_UPDATABLE: s = s + "CONCUR_UPDATABLE"; break;
    }
    s = s + ", ";
    switch(super.getHoldability())
    {
      case ResultSet.HOLD_CURSORS_OVER_COMMIT: s = s + "HOLD_CURSORS_OVER_COMMIT"; break;
      case ResultSet.CLOSE_CURSORS_AT_COMMIT: s = s + "CLOSE_CURSORS_OVER_COMMIT"; break;
    }
    System.out.println(s);
  } /* printState */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean absolute(int row) throws SQLException
  {
    boolean bPositioned = super.absolute(row);
    if (bPositioned)
      loadRecord();
    return bPositioned;
  } /* absolute */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean relative(int row) throws SQLException
  {
    boolean bPositioned = super.relative(row);
    if (bPositioned)
      loadRecord();
    return bPositioned;
  } /* relative */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean first() throws SQLException
  {
    boolean bPositioned = super.first();
    if (bPositioned)
      loadRecord();
    return bPositioned;
  } /* first */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean last() throws SQLException
  {
    boolean bPositioned = super.last();
    if (bPositioned)
      loadRecord();
    return bPositioned;
  } /* last */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean next() throws SQLException
  {
    boolean bPositioned = super.next();
    if (bPositioned)
      loadRecord();
    return bPositioned;
  } /* next */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean previous() throws SQLException
  {
    boolean bPositioned = super.previous();
    if (bPositioned)
      loadRecord();
    return bPositioned;
  } /* previous */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void moveToInsertRow() throws SQLException
  {
    super.moveToInsertRow();
    loadRecord();
  } /* moveToInsertRow */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void moveToCurrentRow() throws SQLException
  {
    super.moveToCurrentRow();
    loadRecord();
  } /* moveToCurrentRow */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void refreshRow() throws SQLException
  {
    super.refreshRow();
    loadRecord();
  } /* refreshRow */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void insertRow() throws SQLException
  {
    insertRecord();
  } /* insertRow */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateRow() throws SQLException
  {
    updateRecord();
    super.updateRow();
  } /* updateRow */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean isBeforeFirst() throws SQLException
  {
    return (getRow() == 0);
  } /* isBeforeFirst */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int findColumn(String sColumnLabel)
    throws SQLException
  {
    int iFound = -1;
    for (int iValue = 1; (iFound < 0) && (iValue <= _rsmd.getColumnCount()); iValue++)
    {
      if (sColumnLabel.equals(_rsmd.getColumnLabel(iValue)))
        iFound = iValue;
    }
    return iFound;
  } /* findColumn */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean wasNull() throws SQLException
  {
    return _bNull;
  } /* wasNull */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNull(int columnIndex) throws SQLException
  {
    _listValues.set(columnIndex+1,null);
  } /* updateNull */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Object getObject(int columnIndex) throws SQLException
  {
    Object o = _listValues.get(columnIndex-1);
    _bNull = (o == null);
    return o;
  } /* getObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateObject(int columnIndex, Object oValue) throws SQLException
  {
    _listValues.set(columnIndex-1,oValue);
  } /* updateObject */

  /*------------------------------------------------------------------*/
  private int getDataType(int columnIndex)
    throws SQLException
  {
    int iDataType = _rsmd.getColumnType(columnIndex);
    if (iDataType == Types.DISTINCT)
    {
      String sTypeName = _rsmd.getColumnTypeName(columnIndex);
      try
      {
        Db2DatabaseMetaData dmd = (Db2DatabaseMetaData)getStatement().getConnection().getMetaData();
        QualifiedId qiType = new QualifiedId(sTypeName);
        ResultSet rs = dmd.getUDTs(
          qiType.getCatalog(), 
          qiType.getSchema(),
          qiType.getName(), 
          new int[] {Types.DISTINCT});
        rs.next();
        iDataType = rs.getInt("BASE_TYPE");
        rs.close();
      }
      catch (ParseException pe) { throw new SQLException("DISTINCT type "+sTypeName+" could not be parsed!"); }
    }
    return iDataType;
  } /* getDataType */
    
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean getBoolean(int columnIndex) throws SQLException
  {
    boolean b = false;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.SMALLINT: b = ((Short)o).shortValue() != 0; break;
        case Types.INTEGER: b = ((Integer)o).intValue() != 0; break;
        case Types.BIGINT: b = ((Long)o).longValue() != 0; break;
        case Types.REAL: b = ((Float)o).floatValue() != 0.0; break;
        case Types.FLOAT:
        case Types.DOUBLE: b = ((Double)o).doubleValue() != 0.0; break;
        case Types.NUMERIC:
        case Types.DECIMAL: b = !((BigDecimal)o).equals(BigDecimal.ZERO); break;
        case Types.BOOLEAN: b = ((Boolean)o).booleanValue(); break;
        case Types.CHAR:
        case Types.VARCHAR:
          String s = (String)o;
          if (s.equalsIgnoreCase("true") ||
              s.equalsIgnoreCase("yes") ||
              s.startsWith("y") ||
              s.startsWith("Y") ||
              s.equals("1"))
            b = true;
          else if (s.equalsIgnoreCase("false") ||
                   s.equalsIgnoreCase("no") ||
                   s.startsWith("n") ||
                   s.startsWith("N") ||
                   s.equals("0"))
            b = false;
          else
            throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to boolean!");
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to boolean!");
      }
    }
    return b;
  } /* getBoolean */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBoolean(int columnIndex, boolean x)
    throws SQLException
  {
    Object o = null;
    int iDataType = getDataType(columnIndex);
    switch (iDataType)
    {
      case Types.SMALLINT: o = Short.valueOf(x?(short)1:(short)0); break;
      case Types.INTEGER: o = Integer.valueOf(x?1:0); break;
      case Types.BIGINT: o = Long.valueOf(x?1l:0l); break;
      case Types.REAL: o = Float.valueOf(x?1.0f:0.0f); break;
      case Types.FLOAT: 
      case Types.DOUBLE: o = Double.valueOf(x?1.0:0.0); break;
      case Types.NUMERIC:
      case Types.DECIMAL: o = x?BigDecimal.ONE:BigDecimal.ZERO; break;
      case Types.BOOLEAN: o = Boolean.valueOf(x); break;
      case Types.CHAR:
      case Types.VARCHAR: o = x?"true":"false"; break;
      default:
        throw new SQLException("Boolean cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
    }
    updateObject(columnIndex, o);
  } /* updateBoolean */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public byte getByte(int columnIndex) throws SQLException
  {
    byte by = 0;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.SMALLINT: by = ((Short)o).byteValue(); break;
        case Types.INTEGER: by = ((Integer)o).byteValue(); break;
        case Types.BIGINT: by = ((Long)o).byteValue(); break;
        case Types.REAL: by = ((Float)o).byteValue(); break;
        case Types.FLOAT:
        case Types.DOUBLE: by = ((Double)o).byteValue(); break;
        case Types.NUMERIC:
        case Types.DECIMAL: by = ((BigDecimal)o).byteValue(); break;
        case Types.BOOLEAN: by = ((Boolean)o).booleanValue()?(byte)1:(byte)0; break;
        case Types.CHAR:
        case Types.VARCHAR: by = Byte.parseByte((String)o); break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to byte!");
      }
    }
    return by;
  } /* getByte */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateByte(int columnIndex, byte x) throws SQLException
  {
    Object o = null;
    Byte by = Byte.valueOf(x);
    int iDataType = getDataType(columnIndex);
    switch (iDataType)
    {
      case Types.SMALLINT: o = Short.valueOf(by.shortValue()); break;
      case Types.INTEGER: o = Integer.valueOf(by.intValue()); break;
      case Types.BIGINT: o = Long.valueOf(by.longValue()); break;
      case Types.REAL: o = Float.valueOf(by.floatValue()); break;
      case Types.FLOAT: 
      case Types.DOUBLE: o = Double.valueOf(by.doubleValue()); break;
      case Types.NUMERIC:
      case Types.DECIMAL: o = BigDecimal.valueOf(by.longValue()); break;
      case Types.BOOLEAN: o = Boolean.valueOf(by.intValue() != 0); break;
      case Types.CHAR:
      case Types.VARCHAR: o = String.valueOf(x); break;
      default:
        throw new SQLException("Byte cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
    }
    updateObject(columnIndex, o);
  } /* updateByte */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public short getShort(int columnIndex) throws SQLException
  {
    short sh = 0;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.SMALLINT: sh = ((Short)o).shortValue(); break;
        case Types.INTEGER: sh = ((Integer)o).shortValue(); break;
        case Types.BIGINT: sh = ((Long)o).shortValue(); break;
        case Types.REAL: sh = ((Float)o).shortValue(); break;
        case Types.FLOAT: 
        case Types.DOUBLE: sh = ((Double)o).shortValue(); break;
        case Types.NUMERIC:
        case Types.DECIMAL: sh = ((BigDecimal)o).shortValue(); break;
        case Types.BOOLEAN: sh = ((Boolean)o).booleanValue()?(short)1:(short)0; break;
        case Types.CHAR:
        case Types.VARCHAR: sh = Short.parseShort((String)o); break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to short!");
      }
    }
    return sh;
  } /* getShort */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateShort(int columnIndex, short x) throws SQLException
  {
    Object o = null;
    Short sh = Short.valueOf(x);
    int iDataType = getDataType(columnIndex);
    switch (iDataType)
    {
      case Types.SMALLINT: o = sh; break;
      case Types.INTEGER: o = Integer.valueOf(sh.intValue()); break;
      case Types.BIGINT: o = Long.valueOf(sh.longValue()); break;
      case Types.REAL: o = Float.valueOf(sh.floatValue()); break;
      case Types.FLOAT:
      case Types.DOUBLE: o = Double.valueOf(sh.doubleValue()); break;
      case Types.NUMERIC:
      case Types.DECIMAL: o = BigDecimal.valueOf(sh.longValue()); break;
      case Types.BOOLEAN: o = Boolean.valueOf(x != 0); break;
      case Types.CHAR:
      case Types.VARCHAR: o = String.valueOf(x); break;
      default:
        throw new SQLException("Short cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
    }
    updateObject(columnIndex, o);
  } /* updateShort */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getInt(int columnIndex) throws SQLException
  {
    int  i = 0;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.SMALLINT: i = ((Short)o).intValue(); break;
        case Types.INTEGER: i = ((Integer)o).intValue(); break;
        case Types.BIGINT: i = ((Long)o).intValue(); break;
        case Types.REAL: i = ((Float)o).intValue(); break;
        case Types.FLOAT:
        case Types.DOUBLE: i = ((Double)o).intValue(); break;
        case Types.NUMERIC:
        case Types.DECIMAL: i = ((BigDecimal)o).intValue(); break;
        case Types.BOOLEAN: i = ((Boolean)o).booleanValue()?1:0; break;
        case Types.CHAR:
        case Types.VARCHAR: i = Integer.parseInt((String)o); break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to int!");
      }
    }
    return i;
  } /* getInt */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateInt(int columnIndex, int x) throws SQLException
  {
    Object o = null;
    Integer i = Integer.valueOf(x);
    int iDataType = getDataType(columnIndex);
    switch (iDataType)
    {
      case Types.SMALLINT: o = Short.valueOf(i.shortValue()); break;
      case Types.INTEGER: o = i; break;
      case Types.BIGINT: o = Long.valueOf(i.longValue()); break;
      case Types.REAL: o = Float.valueOf(i.floatValue()); break;
      case Types.FLOAT:
      case Types.DOUBLE: o = Double.valueOf(i.doubleValue()); break;
      case Types.NUMERIC:
      case Types.DECIMAL: o = BigDecimal.valueOf(x); break;
      case Types.BOOLEAN: o = Boolean.valueOf(x != 0); break;
      case Types.CHAR:
      case Types.VARCHAR: o = String.valueOf(x); break;
      default:
        throw new SQLException("Integer cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
    }
    updateObject(columnIndex, o);
  } /* updateInt */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long getLong(int columnIndex) throws SQLException
  {
    long l = 0;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.SMALLINT: l = ((Short)o).longValue(); break;
        case Types.INTEGER: l = ((Integer)o).longValue(); break;
        case Types.BIGINT: l = ((Long)o).longValue(); break;
        case Types.REAL: l = ((Float)o).longValue(); break;
        case Types.FLOAT: 
        case Types.DOUBLE: l = ((Double)o).longValue(); break;
        case Types.NUMERIC:
        case Types.DECIMAL: l = ((BigDecimal)o).longValue(); break;
        case Types.BOOLEAN: l = ((Boolean)o).booleanValue()?1l:0l; break;
        case Types.CHAR:
        case Types.VARCHAR: l = Long.parseLong((String)o); break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to long!");
      }
    }
    return l;
  } /* getLong */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateLong(int columnIndex, long x) throws SQLException
  {
    Object o = null;
    Long l = Long.valueOf(x);
    int iDataType = getDataType(columnIndex);
    switch (iDataType)
    {
      case Types.SMALLINT: o = Short.valueOf(l.shortValue()); break;
      case Types.INTEGER: o = Integer.valueOf(l.intValue()); break;
      case Types.BIGINT: o = l; break;
      case Types.REAL: o = Float.valueOf(l.floatValue()); break;
      case Types.FLOAT:
      case Types.DOUBLE: o = Double.valueOf(l.doubleValue()); break;
      case Types.NUMERIC:
      case Types.DECIMAL: o = BigDecimal.valueOf(x); break;
      case Types.BOOLEAN: o = Boolean.valueOf(x != 0); break;
      case Types.CHAR:
      case Types.VARCHAR: o = String.valueOf(x); break;
      default:
        throw new SQLException("Long cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
    }
    updateObject(columnIndex, o);
  } /* updateLong */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public float getFloat(int columnIndex) throws SQLException
  {
    float f = 0.0f;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.SMALLINT: f = ((Short)o).floatValue(); break;
        case Types.INTEGER: f = ((Integer)o).floatValue(); break;
        case Types.BIGINT: f = ((Long)o).floatValue(); break;
        case Types.REAL: f = ((Float)o).floatValue(); break;
        case Types.FLOAT:
        case Types.DOUBLE: f = ((Double)o).floatValue(); break;
        case Types.NUMERIC:
        case Types.DECIMAL: f = ((BigDecimal)o).floatValue(); break;
        case Types.BOOLEAN: f = ((Boolean)o).booleanValue()?1.0f:0.0f; break;
        case Types.CHAR:
        case Types.VARCHAR: f = Float.parseFloat((String)o); break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to float!");
      }
    }
    return f;
  } /* getFloat */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException
  {
    Object o = null;
    Float f = Float.valueOf(x);
    int iDataType = getDataType(columnIndex);
    switch (iDataType)
    {
      case Types.SMALLINT: o = Short.valueOf(f.shortValue()); break;
      case Types.INTEGER: o = Integer.valueOf(f.intValue()); break;
      case Types.BIGINT: o = Long.valueOf(f.longValue()); break;
      case Types.REAL: o = f; break;
      case Types.FLOAT:
      case Types.DOUBLE: o = Double.valueOf(f.doubleValue()); break;
      case Types.NUMERIC:
      case Types.DECIMAL: o = BigDecimal.valueOf(f.doubleValue()); break;
      case Types.BOOLEAN: o = Boolean.valueOf(x != 0.0f); break;
      case Types.CHAR:
      case Types.VARCHAR: o = String.valueOf(x); break;
      default:
        throw new SQLException("Float cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
    }
    updateObject(columnIndex, o);
  } /* updateFloat */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public double getDouble(int columnIndex) throws SQLException
  {
    double d = 0.0d;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.SMALLINT: d = ((Short)o).doubleValue(); break;
        case Types.INTEGER: d = ((Integer)o).doubleValue(); break;
        case Types.BIGINT: d = ((Long)o).doubleValue(); break;
        case Types.REAL: d = ((Float)o).doubleValue(); break;
        case Types.FLOAT:
        case Types.DOUBLE: d = ((Double)o).doubleValue(); break;
        case Types.NUMERIC:
        case Types.DECIMAL: d = ((BigDecimal)o).doubleValue(); break;
        case Types.BOOLEAN: d = ((Boolean)o).booleanValue()?1.0:0.0; break;
        case Types.CHAR:
        case Types.VARCHAR: d = Double.parseDouble((String)o); break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to double!");
      }
    }
    return d;
  } /* getDouble */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateDouble(int columnIndex, double x) throws SQLException
  {
    Object o = null;
    Double d = Double.valueOf(x);
    int iDataType = getDataType(columnIndex);
    switch (iDataType)
    {
      case Types.SMALLINT: o = Short.valueOf(d.shortValue()); break;
      case Types.INTEGER: o = Integer.valueOf(d.intValue()); break;
      case Types.BIGINT: o = Long.valueOf(d.longValue()); break;
      case Types.REAL: o = Float.valueOf(d.floatValue()); break;
      case Types.FLOAT:
      case Types.DOUBLE: o = d; break;
      case Types.NUMERIC:
      case Types.DECIMAL: o = BigDecimal.valueOf(x); break;
      case Types.BOOLEAN: o = Boolean.valueOf(x != 0.0); break;
      case Types.CHAR:
      case Types.VARCHAR: o = String.valueOf(x); break;
      default:
        throw new SQLException("Double cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
    }
    updateObject(columnIndex, o);
  } /* updateDouble */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException
  {
    BigDecimal bd = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.SMALLINT: bd = new BigDecimal(((Short)o).intValue()); break;
        case Types.INTEGER: bd = new BigDecimal(((Integer)o).intValue()); break;
        case Types.BIGINT: bd = new BigDecimal(((Long)o).longValue()); break;
        case Types.REAL: bd = new BigDecimal(((Float)o).doubleValue()); break;
        case Types.FLOAT:
        case Types.DOUBLE: bd = new BigDecimal(((Double)o).doubleValue()); break;
        case Types.NUMERIC:
        case Types.DECIMAL: bd = (BigDecimal)o; break;
        case Types.BOOLEAN: bd = ((Boolean)o).booleanValue()?BigDecimal.ONE:BigDecimal.ZERO; break;
        case Types.CHAR:
        case Types.VARCHAR: bd = new BigDecimal((String)o); break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to big decimal!");
      }
    }
    return bd;
  } /* getBigDecimal */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.SMALLINT: o = Short.valueOf(x.shortValue()); break;
        case Types.INTEGER: o = Integer.valueOf(x.intValue()); break;
        case Types.BIGINT: o = Long.valueOf(x.longValue()); break;
        case Types.REAL: o = Float.valueOf(x.floatValue()); break;
        case Types.FLOAT:
        case Types.DOUBLE: o = Double.valueOf(x.doubleValue()); break;
        case Types.NUMERIC:
        case Types.DECIMAL: o = x; break;
        case Types.BOOLEAN: o = Boolean.valueOf(!BigDecimal.ZERO.equals(x)); break;
        case Types.CHAR:
        case Types.VARCHAR: o = x.toPlainString(); break;
        default:
          throw new SQLException("BigDecimal cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateBigDecimal */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getString(int columnIndex) throws SQLException
  {
    String s = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      s = String.valueOf(o);
      switch(iDataType)
      {
        case Types.SMALLINT:
        case Types.INTEGER:
        case Types.BIGINT:
        case Types.REAL:
        case Types.FLOAT:
        case Types.DOUBLE:
        case Types.BOOLEAN:
        case Types.CHAR:
        case Types.NCHAR:
        case Types.VARCHAR:
        case Types.NVARCHAR:
        case Types.DATE:
        case Types.TIME:
        case Types.TIMESTAMP: s = o.toString(); break;
        case Types.NUMERIC:
        case Types.DECIMAL: s = ((BigDecimal)o).toPlainString(); break;
        case Types.BINARY:
        case Types.VARBINARY:
          try { s = new String((byte[])o,"ISO-8859-1"); }
          catch(UnsupportedEncodingException uee) { throw new SQLException("ISO-8859-1 is an unsupported encoding!",uee); }
          break;
        case Types.CLOB:
        case Types.NCLOB:
          Clob clob = (Clob)o;
          s = clob.getSubString(1l, (int)clob.length());
          break;
        case Types.SQLXML:
          SQLXML sqlxml = (SQLXML)o;
          s = sqlxml.getString();
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to string!");
      }
    }
    return s;
  } /* getString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateString(int columnIndex, String x)
    throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.SMALLINT: o = Short.valueOf(Short.parseShort(x)); break;
        case Types.INTEGER: o = Integer.valueOf(Integer.parseInt(x)); break;
        case Types.BIGINT: o = Long.valueOf(Long.parseLong(x)); break;
        case Types.REAL: o = Float.valueOf(Float.parseFloat(x)); break;
        case Types.FLOAT:
        case Types.DOUBLE: o = Double.valueOf(Double.parseDouble(x)); break;
        case Types.NUMERIC:
        case Types.DECIMAL: o = new BigDecimal(x); break;
        case Types.BOOLEAN: 
          if (x.equalsIgnoreCase("true") ||
            x.equalsIgnoreCase("yes") ||
            x.startsWith("y") ||
            x.startsWith("Y") ||
            x.equals("1"))
            o = Boolean.TRUE;
          else if (x.equalsIgnoreCase("false") ||
            x.equalsIgnoreCase("no") ||
            x.startsWith("n") ||
            x.startsWith("N") ||
            x.equals("0"))
            o = Boolean.FALSE;
          else
            throw new SQLException("String \""+x+"\" cannot be converted to boolean!");
          break;
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.NCHAR:
        case Types.NVARCHAR: o = x; break;
        case Types.CLOB:
        case Types.NCLOB:
          Clob clob = getStatement().getConnection().createClob();
          clob.setString(1, x);
          o = clob;
          break;
        case Types.SQLXML:
          SQLXML sqlxml = getStatement().getConnection().createSQLXML();
          sqlxml.setString(x);
          o = sqlxml;
          break;
        default:
          throw new SQLException("String cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getNString(int columnIndex) throws SQLException
  {
    return getString(columnIndex);
  } /* getString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNString(int columnIndex, String x)
    throws SQLException
  {
    updateString(columnIndex, x);
  } /* updateString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public byte[] getBytes(int columnIndex) throws SQLException
  {
    byte[] buf = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.BINARY:
        case Types.VARBINARY: buf = (byte[])o; break;
        case Types.BLOB:
          Blob blob = (Blob)o;
          buf = blob.getBytes(1l, (int)blob.length());
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to byte array!");
      }
    }
    return buf;
  } /* getBytes */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBytes(int columnIndex, byte[] x)
    throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.BINARY:
        case Types.VARBINARY: o = x; break;
        case Types.BLOB:
          Blob blob = getStatement().getConnection().createBlob();
          blob.setBytes(1l, x);
          o = blob;
          break;
        default:
          throw new SQLException("Byte array cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateBytes */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Date getDate(int columnIndex) throws SQLException
  {
    Date date = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.DATE: date = (Date)o; break;
        case Types.TIME: date = new Date(((Time)o).getTime()); break;
        case Types.TIMESTAMP: date = new Date(((Timestamp)o).getTime()); break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to date!");
      }
    }
    return date;
  } /* getDate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateDate(int columnIndex, Date x) throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.DATE: o = x; break;
        case Types.TIME: o = new Time(x.getTime() % _lMILLIS_PER_DAY); break;
        case Types.TIMESTAMP: o = new Timestamp(x.getTime()); break;
        default:
          throw new SQLException("Date cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateDate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Time getTime(int columnIndex) throws SQLException
  {
    Time time = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.DATE: time = new Time(((Date)o).getTime() % _lMILLIS_PER_DAY); break;
        case Types.TIME: time = (Time)o; break;
        case Types.TIMESTAMP: time = new Time(((Timestamp)o).getTime() % _lMILLIS_PER_DAY); break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to time!");
      }
    }
    return time;
  } /* getTime */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateTime(int columnIndex, Time x) throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.DATE: o = new Date(x.getTime()); break;
        case Types.TIME: o = x; break;
        case Types.TIMESTAMP: o = new Timestamp(x.getTime()); break;
        default:
          throw new SQLException("Time cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateTime */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException
  {
    Timestamp ts = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.DATE: ts = new Timestamp(((Date)o).getTime()); break;
        case Types.TIME: ts = new Timestamp(((Time)o).getTime()); break;
        case Types.TIMESTAMP: ts = (Timestamp)o; break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to timestamp!");
      }
    }
    return ts;
  } /* getTimestamp */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.DATE: o = new Date(x.getTime()); break;
        case Types.TIME: o = new Time(x.getTime() % _lMILLIS_PER_DAY); break;
        case Types.TIMESTAMP: o = x; break;
        default:
          throw new SQLException("Timestamp cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateTimestamp */

  /*------------------------------------------------------------------*/
  public Duration getDuration(int columnIndex)
    throws SQLException
  {
    Duration duration = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      if (iDataType == Types.OTHER)
        duration = (Duration)o;
      else
        throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to duration!");
    }
    return duration;
  } /* getDuration */
  
  /*------------------------------------------------------------------*/
  public void updateDuration(int columnIndex, Duration x)
    throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      if (iDataType == Types.OTHER)
        o = x;
      else
        throw new SQLException("Duration cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
    }
    updateObject(columnIndex, o);
  } /* updateDuration */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getAsciiStream(int columnIndex)
    throws SQLException
  {
    InputStream is = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.CHAR:
        case Types.VARCHAR:
          String s = (String)o;
          byte[] bufString = s.getBytes(Charset.forName("ASCII"));
          is = new ByteArrayInputStream(bufString);
          break;
        case Types.CLOB:
        case Types.NCLOB:
          Clob clob = (Clob)o;
          is = clob.getAsciiStream();
          break;
        case Types.BINARY:
        case Types.VARBINARY:
          byte[] bufBinary = (byte[])o;
          is = new ByteArrayInputStream(bufBinary);
          break;
        case Types.BLOB:
          Blob blob = (Blob)o;
          is = blob.getBinaryStream();
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to ASCII stream!");
      }
    }
    return is;
  } /* getAsciiStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x)
    throws SQLException
  {
    updateAsciiStream(columnIndex, x, -1l);
  } /* updateAsciiStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length) 
    throws SQLException
  {
      updateAsciiStream(columnIndex, x, (long)length);
  } /* updateAsciiStream */

  /*------------------------------------------------------------------*/
  private byte[] readBytes(InputStream is)
    throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int iRead = is.read(); iRead != -1; iRead = is.read()) 
      baos.write(iRead);
    baos.close();
    is.close();
    return baos.toByteArray();
  } /* readBytes */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length)
    throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      try
      {
        switch (iDataType)
        {
          case Types.CHAR:
          case Types.VARCHAR:
            byte[] bufString = readBytes(x);
            if ((length > 0) && (bufString.length != (int)length))
              throw new SQLException("Length of ASCII stream did not match length argument!");
            String s = new String(bufString,"ASCII");
            o = s;
            break;
          case Types.CLOB:
          case Types.NCLOB:
            Clob clob = getStatement().getConnection().createClob();
            OutputStream osClob = clob.setAsciiStream(1l);
            for (int iRead = x.read(); iRead != -1; iRead = x.read())
              osClob.write(iRead);
            osClob.close();
            x.close();
            if ((length > 0) && (clob.length() != length))
              throw new SQLException("Length of ASCII stream did not match length argument!");
            o = clob;
            break;
          case Types.BINARY:
          case Types.VARBINARY:
            byte[] bufBinary = readBytes(x);
            if ((length > 0) && (bufBinary.length != (int)length))
              throw new SQLException("Length of ASCII stream did not match length argument!");
            o = bufBinary;
            break;
          case Types.BLOB:
            Blob blob = getStatement().getConnection().createBlob();
            OutputStream osBlob = blob.setBinaryStream(1);
            for (int iRead = x.read(); iRead != -1; iRead = x.read())
              osBlob.write(iRead);
            osBlob.close();
            x.close();
            if ((length > 0) && (blob.length() != length))
              throw new SQLException("Length of ASCII stream did not match length argument!");
            o = blob;
            break;
          default:
            throw new SQLException("Timestamp cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
        }
      }
      catch(IOException ie) { throw new SQLException("I/O error reading ASCII stream!",ie); }
    }
    updateObject(columnIndex, o);
  } /* updateAsciiStream */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getBinaryStream(int columnIndex)
    throws SQLException
  {
    InputStream is = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.BINARY:
        case Types.VARBINARY:
          byte[] bufBinary = (byte[])o;
          is = new ByteArrayInputStream(bufBinary);
          break;
        case Types.BLOB:
          Blob blob = (Blob)o;
          is = blob.getBinaryStream();
          break;
        case Types.SQLXML:
          SQLXML sqlxml = (SQLXML)o;
          is = sqlxml.getBinaryStream();
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to ASCII stream!");
      }
    }
    return is;
  } /* getBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x)
    throws SQLException
  {
    updateBinaryStream(columnIndex, x, -1l);
  } /* updateBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length)
    throws SQLException
  {
    updateBinaryStream(columnIndex, x, (long)length);
  } /* updateBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length)
    throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      try
      {
        switch (iDataType)
        {
          case Types.BINARY:
          case Types.VARBINARY:
            byte[] bufBinary = readBytes(x);
            if ((length > 0) && (bufBinary.length != (int)length))
              throw new SQLException("Length of binary stream did not match length argument!");
            o = bufBinary;
            break;
          case Types.BLOB:
            Blob blob = getStatement().getConnection().createBlob();
            OutputStream osBlob = blob.setBinaryStream(1);
            for (int iRead = x.read(); iRead != -1; iRead = x.read())
              osBlob.write(iRead);
            osBlob.close();
            x.close();
            if ((length > 0) && (blob.length() != length))
              throw new SQLException("Length of binary stream did not match length argument!");
            o = blob;
            break;
          case Types.SQLXML:
            SQLXML sqlxml = getStatement().getConnection().createSQLXML();
            long lLength = 0;
            OutputStream osSqlxml = sqlxml.setBinaryStream();
            for (int iRead = x.read(); iRead != -1; iRead = x.read())
            {
              lLength++;
              osSqlxml.write(iRead);
            }
            osSqlxml.close();
            x.close();
            if ((length > 0) && (lLength != length))
              throw new SQLException("Length of binary stream did not match length argument!");
            o = sqlxml;
            break;
          default:
            throw new SQLException("Timestamp cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
        }
      }
      catch(IOException ie) { throw new SQLException("I/O error reading ASCII stream!",ie); }
    }
    updateObject(columnIndex, o);
  } /* updateBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Reader getCharacterStream(int columnIndex) throws SQLException
  {
    Reader rdr = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.NCHAR:
        case Types.NVARCHAR:
          rdr = new StringReader((String)o);
          break;
        case Types.CLOB:
        case Types.NCLOB:
          Clob clob = (Clob)o;
          rdr = clob.getCharacterStream();
          break;
        case Types.SQLXML:
          SQLXML sqlxml = (SQLXML)o;
          rdr = sqlxml.getCharacterStream();
          break;
        case Types.BINARY:
        case Types.VARBINARY:
          byte[] buf = (byte[])o;
          InputStream isBytes = new ByteArrayInputStream(buf);
          rdr = new InputStreamReader(isBytes,Charset.forName(SU.sUTF8_CHARSET_NAME));
          break;
        case Types.BLOB:
          Blob blob = (Blob)o;
          InputStream isBlob = blob.getBinaryStream();
          rdr = new InputStreamReader(isBlob,Charset.forName(SU.sUTF8_CHARSET_NAME));
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to character stream!");
      }
    }
    return rdr;
  } /* getCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x)
    throws SQLException
  {
    updateCharacterStream(columnIndex, x, -1l);
  } /* updateCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length)
    throws SQLException
  {
    updateCharacterStream(columnIndex, x, (long)length);
  } /* updateCharacterStream */

  /*------------------------------------------------------------------*/
  private String readString(Reader rdr)
    throws IOException
  {
    StringWriter sw = new StringWriter();
    for (int iRead = rdr.read(); iRead != -1; iRead = rdr.read()) 
      sw.write(iRead);
    sw.close();
    rdr.close();
    return sw.toString();
  } /* readString */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length)
    throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      try
      {
        switch (iDataType)
        {
          case Types.CHAR:
          case Types.VARCHAR:
          case Types.NCHAR:
          case Types.NVARCHAR:
            String s = readString(x);
            if ((length > 0) && (s.length() != (int)length))
              throw new SQLException("Length of character stream did not match length argument!");
            o = s;
            break;
          case Types.CLOB:
          case Types.NCLOB:
            Clob clob = getStatement().getConnection().createClob();
            Writer wrClob = clob.setCharacterStream(1l);
            for (int iRead = x.read(); iRead != -1; iRead = x.read())
              wrClob.write(iRead);
            wrClob.close();
            x.close();
            if ((length > 0) && (clob.length() != length))
              throw new SQLException("Length of character stream did not match length argument!");
            o = clob;
            break;
          case Types.SQLXML:
            SQLXML sqlxml = getStatement().getConnection().createSQLXML();
            long lLengthXml = 0;
            Writer wrSqlxml = sqlxml.setCharacterStream();
            for (int iRead = x.read(); iRead != -1; iRead = x.read())
            {
              lLengthXml++;
              wrSqlxml.write(iRead);
            }
            wrSqlxml.close();
            x.close();
            if ((length > 0) && (lLengthXml != length))
              throw new SQLException("Length of character stream did not match length argument!");
            o = sqlxml;
            break;
          case Types.BINARY:
          case Types.VARBINARY:
            String sBinary = readString(x);
            byte[] bufBinary = sBinary.getBytes(Charset.forName(SU.sUTF8_CHARSET_NAME));
            if ((length > 0) && (sBinary.length() != (int)length))
              throw new SQLException("Length of character stream did not match length argument!");
            o = bufBinary;
            break;
          case Types.BLOB:
            Blob blob = getStatement().getConnection().createBlob();
            long lLength = 0;
            OutputStream osBlob = blob.setBinaryStream(1);
            for (int iRead = x.read(); iRead != -1; iRead = x.read())
            {
              char c = (char)iRead;
              String sBlob = new String(new char[]{c});
              byte[] bufChar = sBlob.getBytes(Charset.forName(SU.sUTF8_CHARSET_NAME));
              osBlob.write(bufChar);
              lLength++;
            }
            osBlob.close();
            x.close();
            if ((length > 0) && (lLength != length))
              throw new SQLException("Length of character stream did not match length argument!");
            o = blob;
            break;
          default:
            throw new SQLException("Character stream cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
        }
      }
      catch(IOException ie) { throw new SQLException("I/O error reading character stream!",ie); }
    }
    updateObject(columnIndex, o);
  } /* updateCharacterStream */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Reader getNCharacterStream(int columnIndex)
    throws SQLException
  {
    return getCharacterStream(columnIndex);
  } /* getNCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNCharacterStream(int columnIndex, Reader x)
    throws SQLException
  {
    updateCharacterStream(columnIndex, x);
  } /* updateNCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNCharacterStream(int columnIndex, Reader x, long length)
    throws SQLException
  {
    updateNCharacterStream(columnIndex, x, length);
  } /* updateNCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Clob getClob(int columnIndex) throws SQLException
  {
    Clob clob = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.CLOB:
        case Types.NCLOB:
          clob = (Clob)o;
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to clob!");
      }
    }
    return clob;
  } /* getClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateClob(int columnIndex, Clob x) throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.CLOB:
        case Types.NCLOB:
          o = x;
          break;
        default:
          throw new SQLException("Clob cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateClob(int columnIndex, Reader reader)
    throws SQLException
  {
    updateClob(columnIndex, reader, -1l);
  } /* updateClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateClob(int columnIndex, Reader reader, long length)
    throws SQLException
  {
    Object o = null;
    if (reader != null)
    {
      int iDataType = getDataType(columnIndex);
      try
      {
        switch (iDataType)
        {
          case Types.CLOB:
          case Types.NCLOB:
            Clob clob = getStatement().getConnection().createClob();
            Writer wrClob = clob.setCharacterStream(1l);
            for (int iRead = reader.read(); iRead != -1; iRead = reader.read())
              wrClob.write(iRead);
            wrClob.close();
            reader.close();
            if ((length > 0) && (clob.length() != length))
              throw new SQLException("Length of reader did not match length argument!");
            o = clob;
            break;
          default:
            throw new SQLException("Reader cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
        }
      }
      catch(IOException ie) { throw new SQLException("I/O error reading character stream!",ie); }
    }
    updateObject(columnIndex, o);
  } /* updateClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public NClob getNClob(int columnIndex) throws SQLException
  {
    NClob nclob = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.NCLOB:
          nclob = new Db2NClob((Clob)o);
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to NClob!");
      }
    }
    return nclob;
  } /* getNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNClob(int columnIndex, NClob x)
    throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.NCLOB:
          o = x;
          break;
        default:
          throw new SQLException("NClob cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNClob(int columnIndex, Reader reader)
    throws SQLException
  {
    updateNClob(columnIndex, reader, -1l);
  } /* updateNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNClob(int columnIndex, Reader reader, long length)
    throws SQLException
  {
    Object o = null;
    if (reader != null)
    {
      int iDataType = getDataType(columnIndex);
      try
      {
        switch (iDataType)
        {
          case Types.NCLOB:
            NClob nclob = getStatement().getConnection().createNClob();
            Writer wrClob = nclob.setCharacterStream(1l);
            for (int iRead = reader.read(); iRead != -1; iRead = reader.read())
              wrClob.write(iRead);
            wrClob.close();
            reader.close();
            if ((length > 0) && (nclob.length() != length))
              throw new SQLException("Length of reader did not match length argument!");
            o = nclob;
            break;
          default:
            throw new SQLException("Reader cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
        }
      }
      catch(IOException ie) { throw new SQLException("I/O error reading character stream!",ie); }
    }
    updateObject(columnIndex, o);
  } /* updateNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Blob getBlob(int columnIndex) throws SQLException
  {
    Blob blob = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.BLOB:
          blob = (Blob)o;
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to blob!");
      }
    }
    return blob;
  } /* getBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBlob(int columnIndex, Blob x) throws SQLException
  {
    Object o = null;
    if (x != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.BLOB:
          o = x;
          break;
        default:
          throw new SQLException("Blob cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBlob(int columnIndex, InputStream inputStream)
    throws SQLException
  {
    updateBlob(columnIndex, inputStream, -1l);
  } /* updateBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBlob(int columnIndex, InputStream inputStream, long length)
    throws SQLException
  {
    Object o = null;
    if (inputStream != null)
    {
      int iDataType = getDataType(columnIndex);
      try
      {
        switch (iDataType)
        {
          case Types.BLOB:
            Blob blob = getStatement().getConnection().createBlob();
            OutputStream osBlob = blob.setBinaryStream(1l);
            for (int iRead = inputStream.read(); iRead != -1; iRead = inputStream.read())
              osBlob.write(iRead);
            osBlob.close();
            inputStream.close();
            if ((length > 0) && (blob.length() != length))
              throw new SQLException("Length of reader did not match length argument!");
            o = blob;
            break;
          default:
            throw new SQLException("Reader cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
        }
      }
      catch(IOException ie) { throw new SQLException("I/O error reading character stream!",ie); }
    }
    updateObject(columnIndex, o);
  } /* updateBlob */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public SQLXML getSQLXML(int columnIndex) throws SQLException
  {
    SQLXML sqlxml = null;
    Object o = getObject(columnIndex);
    if (o != null)
    {
      int iDataType = getDataType(columnIndex);
      switch(iDataType)
      {
        case Types.SQLXML:
          sqlxml = (SQLXML)o;
          break;
        default:
          throw new SQLException("Column of type "+SqlTypes.getTypeName(iDataType)+" cannot be converted to SQLXML!");
      }
    }
    return sqlxml;
  } /* getSQLXML */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject)
    throws SQLException
  {
    Object o = null;
    if (xmlObject != null)
    {
      int iDataType = getDataType(columnIndex);
      switch (iDataType)
      {
        case Types.SQLXML:
          o = xmlObject;
          break;
        default:
          throw new SQLException("SQLXML cannot be converted to value of type "+SqlTypes.getTypeName(iDataType)+"!"); 
      }
    }
    updateObject(columnIndex, o);
  } /* updateSQLXML */

  @Override
  public URL getURL(int columnIndex) throws SQLException {
    try {
      return new URL(super.getString(columnIndex));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public URL updateURL(int columnIndex, URL url) throws SQLException {
    super.updateObject(columnIndex, url.getPath());
    return url;
  }

  public URL updateURL(String columnLabel, URL url) throws SQLException {
    updateURL(this.findColumn(columnLabel), url);
    return url;
  }
  
} /* class Db2ResultSet */
