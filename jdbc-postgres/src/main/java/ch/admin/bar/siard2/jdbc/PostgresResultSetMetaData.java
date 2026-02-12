/*======================================================================
PostgresResultSetMetaData implements wrapped PostgreSQL ResultSetMetaData.
Application : SIARD2
Description : PostgresResultSetMetaData implements wrapped PostgreSQL ResultSetMetaData.
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 09.08.2019, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.text.*;

import ch.admin.bar.siard2.postgres.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;

/*====================================================================*/
/** PostgresResultSetMetaData implements wrapped PostgreSQL ResultSetMetaData.
 * @author Hartwig Thomas
 */
public class PostgresResultSetMetaData
  extends BaseResultSetMetaData
  implements ResultSetMetaData
{
  protected Statement _stmt = null;
  /*------------------------------------------------------------------*/
  /** constructor
   * @param rsWrapped result set to be wrapped.
   * @param wrapped statement.
   */
  public PostgresResultSetMetaData(ResultSetMetaData rsmdWrapped, Statement stmt)
  {
    super(rsmdWrapped);
    _stmt = stmt;
  } /* constructor */
  
  @Override
  public int getColumnType(int column) throws SQLException
  {
    int iType = super.getColumnType(column);
    String sTypeName = getColumnTypeName(column);
    PostgresType pgt = PostgresType.getByKeyword(sTypeName.toLowerCase());
    if (pgt != null)
    {
      PreType pt = pgt.getPreType();
      if (pt != null)
        iType = pt.getSqlType();
    }
    else if (sTypeName.startsWith("interval"))
      iType = PreType.INTERVAL.getSqlType();
    else if (iType != Types.ARRAY)
    {
      try
      {
        QualifiedId qiType = new QualifiedId(sTypeName);
        if ((qiType.getCatalog() == null) && (qiType.getSchema() == null))
          qiType.setName(sTypeName);
        PostgresDatabaseMetaData pdmd = (PostgresDatabaseMetaData)_stmt.getConnection().getMetaData();
        String sCatalog = pdmd.toPattern(qiType.getCatalog());
        String sSchema = pdmd.toPattern(qiType.getSchema());
        String sName = pdmd.toPattern(qiType.getName());
        ResultSet rsUdt = pdmd.getUDTs(sCatalog, sSchema, sName, null);
        if (rsUdt.next())
          iType = rsUdt.getInt("DATA_TYPE");
        rsUdt.close();
      }
      catch(ParseException pe) { throw new SQLException("Type name "+sTypeName+" could not be parsed ("+EU.getExceptionMessage(pe)+")!"); }
    }
    return iType;
  }
  
  @Override
  public String getColumnTypeName(int column) throws SQLException
  {
    String sTypeName = super.getColumnTypeName(column);
    String sTableName = super.getTableName(column);
    String sColumnName = super.getColumnName(column);
    String sSchemaName = super.getSchemaName(column);
    PostgresDatabaseMetaData pdmd = (PostgresDatabaseMetaData)_stmt.getConnection().getMetaData();
    if ((sTableName != null) && (sTableName.length() > 0))
    {
      ResultSet rsColumn = pdmd.getColumns(null, 
        pdmd.toPattern(sSchemaName), 
        pdmd.toPattern(sTableName),
        pdmd.toPattern(sColumnName));
      if (rsColumn.next())
        sTypeName = rsColumn.getString("TYPE_NAME");
      rsColumn.close();
    }
    return sTypeName;
  }
  
} /* PostgresResultSetMetaData */
