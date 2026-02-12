/*======================================================================
Db2MetaColumn implements data type mapping from DB/2 to ISO SQL.
Application : SIARD2
Description : Db2MetaColumn implements data type mapping from DB/2 to ISO SQL.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 19.05.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.text.*;

import ch.admin.bar.siard2.db2.datatype.*;
import ch.enterag.utils.database.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.identifier.*;

/*====================================================================*/
/** Db2MetaColumn implements data type mapping from DB/2 to ISO SQL.
 * N.B.: column TYPE_NAME (6) has the original MSSQL data type name,
 * except for ARRAYs, where it has the ISO SQL base type.
 * @author Hartwig Thomas
 */
public class Db2MetaColumns
  extends Db2ResultSet
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(Db2MetaColumns.class.getName());

  private int _iDataType = -1;
  private int _iTypeName = -1;
  private int _iPrecision = -1;
  private int _iLength = -1;
  private ResultSet _rsUnwrapped = null;
  
  /*------------------------------------------------------------------*/
  static int getDataType(int iDataType, String sTypeName)
    throws SQLException
  {
    _il.enter(SqlTypes.getTypeName(iDataType), sTypeName);
    if (sTypeName.equals("GRAPHIC"))
      iDataType = Types.NCHAR;
    else if (sTypeName.equals("VARGRAPHIC"))
      iDataType = Types.NVARCHAR;
    else if (sTypeName.equals("DBCLOB"))
      iDataType = Types.NCLOB;
    else if (sTypeName.equals("CHAR () FOR BIT DATA"))
      iDataType = Types.BINARY;
    else if (sTypeName.equals("BINARY"))
      iDataType = Types.BINARY;
    else if (sTypeName.equals("VARCHAR () FOR BIT DATA"))
      iDataType = Types.VARBINARY;
    else if (sTypeName.equals("DECFLOAT"))
      iDataType = Types.DECIMAL;
    else if (sTypeName.equals("XML"))
      iDataType = Types.SQLXML;
    else if (iDataType == Types.DISTINCT)
    {
      try
      {
        QualifiedId qiType = new QualifiedId(sTypeName);
        if (qiType.getName().equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE) ||
            qiType.getName().equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
          iDataType = Types.OTHER;
      }
      catch(ParseException pe) { throw new SQLException("Type name \""+sTypeName+"\" could not be parsed!",pe); }
    }
    _il.exit(SqlTypes.getTypeName(iDataType));
    return iDataType;
  } /* getDataType */
  
  /*------------------------------------------------------------------*/
  static String getTypeName(String sTypeName, int iDataType)
    throws SQLException
  {
    if (sTypeName != null)
    {
      if (iDataType == Types.DISTINCT)
      {
        try
        {
          QualifiedId qiType = new QualifiedId(sTypeName);
          if (qiType.getName().equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE))
            sTypeName = "INTERVAL YEAR TO MONTH";
          else if (qiType.getName().equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
            sTypeName = "INTERVAL DAY TO SECOND";
        }
        catch(ParseException pe) { throw new SQLException("Type name \""+sTypeName+"\" could not be parsed!",pe); }
      }
    }
    return sTypeName;
  }
  /*------------------------------------------------------------------*/
  static long getColumnSize(long lColumnSize, String sTypeName)
    throws SQLException
  {
    _il.enter(String.valueOf(lColumnSize), sTypeName);
    if ((lColumnSize > 0) &&
        (lColumnSize < 0x00000000FFFFFFFFl))
    {
      if (sTypeName.equals("GRAPHIC") ||
          sTypeName.equals("VARGRAPHIC") ||
          sTypeName.equals("DBCLOB") ||
          sTypeName.equals("XML"))
        lColumnSize = lColumnSize / 2;
    }
    else
      lColumnSize = -1;
    _il.exit(String.valueOf(lColumnSize));
    return lColumnSize;
  } /* getColumnSize */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param rsWrapped DatabaseMetaData.getColumns() result set to be wrapped.
   */
  public Db2MetaColumns(ResultSet rsWrapped, int iDataType, int iTypeName, 
    int iPrecision, int iLength)
    throws SQLException
  {
    super(rsWrapped);
    _iDataType = iDataType;
    _iTypeName = iTypeName;
    _iPrecision = iPrecision;
    _iLength = iLength;
    _rsUnwrapped = unwrap(ResultSet.class);
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Mapped java.sql.Types type is returned in DATA_TYPE.
   * Original java.sql.Types type can be retrieved by using unwrap.
   * Column size is adjusted to CHARS rather than BYTES.  
   */
  @Override
  public int getInt(int columnIndex) throws SQLException
  {
    int iResult = -1;
    if (columnIndex == _iDataType)
    {
      iResult = getDataType(
        _rsUnwrapped.getInt(_iDataType), 
        _rsUnwrapped.getString(_iTypeName));
    }
    else if ((columnIndex == _iPrecision) ||
             (columnIndex == _iLength))
    {
      int iLength = _rsUnwrapped.getInt(_iPrecision);
      if (iLength <= 0)
        iLength = _rsUnwrapped.getInt(_iLength);
      iResult = (int)getColumnSize(
        iLength,
        _rsUnwrapped.getString(_iTypeName));
    }
    else
      iResult = _rsUnwrapped.getInt(columnIndex);
    return iResult;
  } /* getInt */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Mapped java.sql.Types type is returned in DATA_TYPE.
   * Original java.sql.Types type can be retrieved by using unwrap. 
   * Column size is adjusted to CHARS rather than BYTES.  
   */
  @Override
  public long getLong(int columnIndex) throws SQLException
  {
    long lResult = -1;
    if ((columnIndex == _iPrecision) ||
        (columnIndex == _iLength))
    {
      long lLength = _rsUnwrapped.getLong(_iPrecision);
      if (lLength <= 0)
        lLength = _rsUnwrapped.getLong(_iLength);
      lResult = getColumnSize(
        lLength,
        _rsUnwrapped.getString(_iTypeName));
    }
    else if (columnIndex == _iDataType)
    {
      lResult = getDataType(
        _rsUnwrapped.getInt(_iDataType), 
        _rsUnwrapped.getString(_iTypeName));
    }
    else
      lResult = _rsUnwrapped.getLong(columnIndex);
    return lResult;
  } /* getInt */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Type name (mapped to ISO SQL) is returned in TYPE_NAME.
   * Original type name can be retrieved by using unwrap. 
   */
  @Override
  public String getString(int columnIndex) throws SQLException
  {
    String sResult = _rsUnwrapped.getString(columnIndex);
    if (columnIndex == _iTypeName)
    {
      sResult = getTypeName(
        sResult,
        _rsUnwrapped.getInt(_iDataType));
    }
    return sResult;
  } /* getString */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Mapped java.sql.Types type is returned in DATA_TYPE.
   * Original java.sql.Types type can be retrieved by using unwrap. 
   * Column size is adjusted to CHARS rather than BYTES.  
   */
  @Override
  public Object getObject(int columnIndex) throws SQLException
  {
    Object oResult = _rsUnwrapped.getObject(columnIndex);
    if (oResult instanceof Integer)
      oResult = Integer.valueOf(getInt(columnIndex));
    else if (oResult instanceof Long)
      oResult = Long.valueOf(getLong(columnIndex));
    else if (oResult instanceof String)
      oResult = getString(columnIndex);
    return oResult;
  } /* getObject */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean next() throws SQLException
  {
    return _rsUnwrapped.next();
  } /* next */
  
} /* Db2MetaColumns */
