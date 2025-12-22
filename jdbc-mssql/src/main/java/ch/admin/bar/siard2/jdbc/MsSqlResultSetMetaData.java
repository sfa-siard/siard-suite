/*======================================================================
MsSqlResultSetMetaData implements wrapped MSSQL ResultSetMetaData.
Application : SIARD2
Description : MsSqlResultSetMetaData implements wrapped MSSQL ResultSetMetaData.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 08.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import ch.enterag.utils.jdbc.*;

/*====================================================================*/
/** MsSqlResultSetMetaData implements wrapped MSSQL ResultSetMetaData.
 * @author Hartwig Thomas
 */
public class MsSqlResultSetMetaData
  extends BaseResultSetMetaData
  implements ResultSetMetaData
{
  private static Map<String,Class<?>> mapCLASS_MSSQL_TO_ISO = new HashMap<String,Class<?>>();
  static
  {
    mapCLASS_MSSQL_TO_ISO.put(byte[].class.getName(), byte[].class); // BLOB
    mapCLASS_MSSQL_TO_ISO.put(String.class.getName(), String.class); // CLOB or NCLOB or SQLXML!
    mapCLASS_MSSQL_TO_ISO.put(Boolean.class.getName(), Boolean.class);
    mapCLASS_MSSQL_TO_ISO.put(Short.class.getName(), Short.class);
    mapCLASS_MSSQL_TO_ISO.put(Integer.class.getName(), Integer.class);
    mapCLASS_MSSQL_TO_ISO.put(Long.class.getName(), Long.class);
    mapCLASS_MSSQL_TO_ISO.put(Float.class.getName(), Float.class);
    mapCLASS_MSSQL_TO_ISO.put(Double.class.getName(), Double.class);
    mapCLASS_MSSQL_TO_ISO.put(BigDecimal.class.getName(), BigDecimal.class);
    mapCLASS_MSSQL_TO_ISO.put(Date.class.getName(), Date.class);
    mapCLASS_MSSQL_TO_ISO.put(Time.class.getName(), Time.class);
    mapCLASS_MSSQL_TO_ISO.put(Timestamp.class.getName(), Timestamp.class);
    mapCLASS_MSSQL_TO_ISO.put(microsoft.sql.DateTimeOffset.class.getName(), String.class);
  }
  private Connection _conn = null;
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param rsWrapped result set to be wrapped.
   */
  public MsSqlResultSetMetaData(ResultSetMetaData rsmdWrapped, Connection conn)
  {
    super(rsmdWrapped);
    _conn = conn;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * fix invalid original class name.
   */
  @Override
  public String getColumnClassName(int column) throws SQLException
  {
    
    String sClassName = super.getColumnClassName(column);
    String sTypeName = super.getColumnTypeName(column);
    Class<?> cls = mapCLASS_MSSQL_TO_ISO.get(sClassName);
    if (sTypeName.equals("xml"))
      cls = SQLXML.class;
    else if (sTypeName.equals("text"))
      cls = Clob.class;
    else if (sTypeName.equals("ntext"))
      cls = NClob.class;
    else if (sTypeName.equals("image"))
      cls = Blob.class;
    return cls.getName();
  } /* getColumnClassName */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * map java.sql.Types type.
   */
  @Override
  public int getColumnType(int column) throws SQLException
  {
    return MsSqlMetaColumns.getDataType(
      super.getColumnType(column),
      super.getColumnTypeName(column),
      _conn,
      super.getCatalogName(column), 
      super.getSchemaName(column));
  } /* getColumnType */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * map java.sql.Types type.
   */
  @Override
  public int getPrecision(int column) throws SQLException
  {
    return (int)MsSqlMetaColumns.getColumnSize(
      super.getColumnType(column),
      super.getColumnTypeName(column),
      super.getPrecision(column), 
      _conn,
      super.getCatalogName(column), 
      super.getSchemaName(column));
  } /* getPrecision */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * map type name.
   */
  @Override
  public String getColumnTypeName(int column) throws SQLException
  {
    return MsSqlMetaColumns.getTypeName(
      super.getColumnTypeName(column),
      super.getPrecision(column),
      super.getScale(column),
      _conn,
      super.getCatalogName(column), 
      super.getSchemaName(column));
  } /* getColumnTypeName */
  
} /* class MsSqlResultSetMetaData */
