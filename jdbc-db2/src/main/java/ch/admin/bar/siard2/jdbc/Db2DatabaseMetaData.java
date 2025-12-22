/*======================================================================
Db2DatabaseMetaData implements wrapped DB/2 DatabaseMetaData.
Version     : $Id: $
Application : SIARD2
Description : Db2DatabaseMetaData implements wrapped DB/2 DatabaseMetaData.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 07.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import ch.admin.bar.siard2.db2.datatype.Db2PredefinedType;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.utils.jdbc.*;

/*====================================================================*/
/** Db2DatabaseMetaData implements wrapped DB/2 DatabaseMetaData.
 * @author Hartwig Thomas
 */
public class Db2DatabaseMetaData
  extends BaseDatabaseMetaData
  implements DatabaseMetaData
{
  private static Map<String,Integer> mapDataType = new HashMap<String,Integer>();
  static
  {
    mapDataType.put("CHAR",Integer.valueOf(Types.CHAR));
    mapDataType.put("VARCHAR",Integer.valueOf(Types.VARCHAR));
    mapDataType.put("CLOB",Integer.valueOf(Types.CLOB));
    mapDataType.put("GRAPHIC",Integer.valueOf(Types.NCHAR));
    mapDataType.put("VARGRAPHIC",Integer.valueOf(Types.NVARCHAR));
    mapDataType.put("DBCLOB",Integer.valueOf(Types.NCLOB));
    mapDataType.put("CHAR () FOR BIT DATA",Integer.valueOf(Types.BINARY));
    mapDataType.put("BINARY",Integer.valueOf(Types.BINARY));
    mapDataType.put("VARCHAR () FOR BIT DATA",Integer.valueOf(Types.VARBINARY));
    mapDataType.put("VARBINARY", Types.VARBINARY);
    mapDataType.put("BLOB",Integer.valueOf(Types.BLOB));
    mapDataType.put("SMALLINT",Integer.valueOf(Types.SMALLINT));
    mapDataType.put("INTEGER",Integer.valueOf(Types.INTEGER));
    mapDataType.put("BIGINT",Integer.valueOf(Types.BIGINT));
    mapDataType.put("DECIMAL",Integer.valueOf(Types.DECIMAL));
    mapDataType.put("DECFLOAT",Integer.valueOf(Types.FLOAT));
    mapDataType.put("REAL",Integer.valueOf(Types.REAL));
    mapDataType.put("DOUBLE",Integer.valueOf(Types.REAL));
    mapDataType.put("DATE",Integer.valueOf(Types.DATE));
    mapDataType.put("TIME",Integer.valueOf(Types.TIME));
    mapDataType.put("TIMESTAMP",Integer.valueOf(Types.TIMESTAMP));
    mapDataType.put("XML",Integer.valueOf(Types.SQLXML));
    mapDataType.put("DATALINK",Integer.valueOf(Types.BLOB));
  }

  private static Map<String,Class<?>> mapClass = new HashMap<String,Class<?>>();
  static
  {
    mapClass.put("CHAR",String.class);
    mapClass.put("VARCHAR",String.class);
    mapClass.put("CLOB",Clob.class);
    mapClass.put("GRAPHIC",String.class);
    mapClass.put("VARGRAPHIC",String.class);
    mapClass.put("DBCLOB",Clob.class);
    mapClass.put("CHAR () FOR BIT DATA",byte[].class);
    mapClass.put("BINARY",byte[].class);
    mapClass.put("VARCHAR () FOR BIT DATA",byte[].class);
    mapClass.put("VARBINARY", byte[].class);
    mapClass.put("BLOB",Blob.class);
    mapClass.put("SMALLINT",Integer.class);
    mapClass.put("INTEGER",Integer.class);
    mapClass.put("BIGINT",Long.class);
    mapClass.put("DECIMAL",BigDecimal.class);
    mapClass.put("DECFLOAT",BigDecimal.class);
    mapClass.put("REAL",Float.class);
    mapClass.put("DOUBLE",Double.class);
    mapClass.put("DATE",Date.class);
    mapClass.put("TIME",Time.class);
    mapClass.put("TIMESTAMP",Timestamp.class);
    mapClass.put("XML",String.class);
    mapClass.put("DATALINK", Blob.class);
  }

  /*------------------------------------------------------------------*/
  /** constructor
   * @param dmdWrapped database meta data to be wrapped.
   */
  public Db2DatabaseMetaData(DatabaseMetaData dmdWrapped)
  {
    super(dmdWrapped);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Connection getConnection() throws SQLException
  {
    return new Db2Connection(super.getConnection());
  } /* getConnection */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSet getTypeInfo() throws SQLException
  {
    return super.getTypeInfo();
  } /* getTypeInfo */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Use MsSqlMetaColumn for data type translation.
   */
  @Override
  public ResultSet getColumns(String catalog, String schemaPattern,
    String tableNamePattern, String columnNamePattern)
    throws SQLException
  {
    return new Db2MetaColumns(super.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern),
      5,6,7,7);
  } /* getColumns */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSet getProcedureColumns(String catalog,
    String schemaPattern, String procedureNamePattern,
    String columnNamePattern) throws SQLException
  {
    return new Db2MetaColumns(
        super.getProcedureColumns(catalog, schemaPattern,procedureNamePattern, columnNamePattern),
        6,7,8,9);
  } /* getProcedureColumns */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSet getAttributes(String catalog, String schemaPattern,
    String typeNamePattern, String attributeNamePattern)
    throws SQLException
  {
    return new Db2MetaColumns(
      super.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern), 
      5,6,7,7);
  } /* getAttributes */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   */
  @Override
  public ResultSet getTables(String catalog, String schemaPattern,
    String tableNamePattern, String[] types) throws SQLException
  {
    Set<String> setDb2Types = new HashSet<String>(Arrays.asList("T", "V", "A"));
    boolean bSystem = true;
    if (types != null)
    {
      Set <String> setTypes = new HashSet<String>(Arrays.asList(types));
      if (!setTypes.contains("ALIAS"))
        setDb2Types.remove("A");
      if (!(setTypes.contains("TABLE") || setTypes.contains("SYSTEM_TABLE")))
        setDb2Types.remove("T");
      if (!(setTypes.contains("TABLE") && setTypes.contains("SYSTEM_TABLE")))
        bSystem = false;
      if (!setTypes.contains("VIEW"))
        setDb2Types.remove("V");
    }
    StringBuilder sbDb2Types = new StringBuilder();
    for (Iterator<String> iterDb2Type = setDb2Types.iterator(); iterDb2Type.hasNext(); )
    {
      String sDb2Type = iterDb2Type.next();
      if (sbDb2Types.length() > 0)
        sbDb2Types.append(", ");
      sbDb2Types.append("'"+sDb2Type+"'");
    }
    StringBuilder sbCondition = new StringBuilder("T.TABNAME LIKE ");
    sbCondition.append(SqlLiterals.formatStringLiteral(tableNamePattern));
    sbCondition.append(" ESCAPE ");
    sbCondition.append(SqlLiterals.formatStringLiteral(getSearchStringEscape()));
    sbCondition.append("\r\n");
    if (schemaPattern != null)
    {
      sbCondition.append("AND TRIM(TRAILING FROM T.TABSCHEMA) LIKE ");
      sbCondition.append(SqlLiterals.formatStringLiteral(schemaPattern));
      sbCondition.append(" ESCAPE ");
      sbCondition.append(SqlLiterals.formatStringLiteral(getSearchStringEscape()));
      sbCondition.append("\r\n");
    }
    sbCondition.append("AND T.TYPE IN (");
    sbCondition.append(sbDb2Types.toString());
    sbCondition.append(")\r\n");
    if (!bSystem)
      sbCondition.append("AND NOT T.TABSCHEMA LIKE 'SYS%'\r\n");
    StringBuilder sbSql = new StringBuilder("SELECT\r\n");
    sbSql.append("  NULL AS TABLE_CAT,\r\n");
    sbSql.append("  TRIM(TRAILING FROM T.TABSCHEMA) AS TABLE_SCHEM,\r\n");
    sbSql.append("  T.TABNAME AS TABLE_NAME,\r\n");
    sbSql.append("  CASE T.TYPE\r\n");
    sbSql.append("    WHEN 'T' THEN 'TABLE'\r\n");
    sbSql.append("    WHEN 'V' THEN 'VIEW'\r\n");
    sbSql.append("    WHEN 'A' THEN 'ALIAS'\r\n");
    sbSql.append("    ELSE NULL\r\n");
    sbSql.append("  END AS TABLE_TYPE,\r\n");
    sbSql.append("  T.REMARKS AS REMARKS,\r\n");
    sbSql.append("  NULL AS TYPE_CAT,\r\n");
    sbSql.append("  NULL AS TYPE_SCHEM,\r\n");
    sbSql.append("  NULL AS TYPE_NAME,\r\n");
    sbSql.append("  NULL AS SELF_REFERENCING_COL_NAME,\r\n");
    sbSql.append("  V.TEXT AS "+_sQUERY_TEXT+"\r\n");
    sbSql.append("FROM SYSCAT.TABLES T\r\n");
    sbSql.append("     LEFT JOIN SYSCAT.VIEWS V\r\n");
    sbSql.append("       ON (T.TABSCHEMA = V.VIEWSCHEMA AND T.TABNAME = V.VIEWNAME)\r\n");
    sbSql.append("WHERE ");
    sbSql.append(sbCondition.toString());
    sbSql.append("ORDER BY TABLE_TYPE, TABLE_CAT, TABLE_SCHEM, TABLE_NAME");
    ResultSet rsTables = null;
    Connection conn = getConnection();
    PreparedStatement pstmt = conn.unwrap(Connection.class).prepareStatement(sbSql.toString(),ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
    rsTables = pstmt.executeQuery();
    rsTables = new Db2ResultSet(rsTables);
    return rsTables;
  } /* getTables */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Function columns are not reasonably supported by JDBC driver of DB/2:
   * Column labels "PARAMETER_NAME" and "PARAMETER_TYPE" are used instead of
   * "COLUMN_NAME" and "COLUMN_TYPE". Parameter names are often (all?) null
   * and parameter types equal Types.NULL. 
   */
  @Override
  public ResultSet getFunctions(String catalog, String schemaPattern,
    String functionNamePattern) throws SQLException
  {
      throw new SQLFeatureNotSupportedException("Function metadata are nor supported by DB/2!");
  } /* getFunctions */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} 
   * Function columns are not reasonably supported by JDBC driver of DB/2:
   * Column labels "PARAMETER_NAME" and "PARAMETER_TYPE" are used instead of
   * "COLUMN_NAME" and "COLUMN_TYPE". Parameter names are often (all?) null
   * and parameter types equal Types.NULL. 
   */
  @Override
  public ResultSet getFunctionColumns(String catalog,
    String schemaPattern, String functionNamePattern,
    String columnNamePattern) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Function metadata are nor supported by DB/2!");
  } /* getFunctionColumns */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSet getUDTs(String catalog, String schemaPattern,
    String typeNamePattern, int[] types) 
    throws SQLException
  {
    StringBuilder sbCondition = new StringBuilder("WHERE (");
    if (types != null)
    {
      for (int i = 0; i < types.length; i++)
      {
        int iType = types[i];
        if ((iType == Types.DISTINCT) || (iType == Types.STRUCT))
        {
          if (i > 0)
            sbCondition.append(" OR ");
          if (iType == Types.STRUCT) 
            sbCondition.append("METATYPE = 'R'");
          else if (iType == Types.DISTINCT)
            sbCondition.append("METATYPE = 'T'");
        }
      }
      sbCondition.append(")");
    }
    if (sbCondition.toString().equals("WHERE ("))
      sbCondition.append("METATYPE = 'R' OR METATYPE = 'T')");
    if (catalog != null)
      sbCondition.append("\r\n  AND 0 <> 0");
    if (schemaPattern != null)
      sbCondition.append("\r\n  AND TRIM(TYPESCHEMA) LIKE '"+schemaPattern+"' ESCAPE '"+getSearchStringEscape()+"'");
    if (typeNamePattern != null)
      sbCondition.append("\r\n  AND TYPENAME LIKE '"+typeNamePattern+"' ESCAPE '"+getSearchStringEscape()+"'");
    sbCondition.append("\r\n  AND TYPENAME <> '"+Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE+"'");
    sbCondition.append("\r\n  AND TYPENAME <> '"+Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE+"'");
    StringBuilder sbClassCase = new StringBuilder("CASE METATYPE WHEN 'R' THEN '");
    sbClassCase.append(Object.class.getName());
    sbClassCase.append("' ELSE CASE SOURCENAME\r\n    ");
    for (Iterator<String> iterClass = mapClass.keySet().iterator(); iterClass.hasNext(); )
    {
      String sTypeName = iterClass.next();
      String sClassName = mapClass.get(sTypeName).getName();
      sbClassCase.append("WHEN '");
      sbClassCase.append(sTypeName);
      sbClassCase.append("' THEN '");
      sbClassCase.append(sClassName);
      sbClassCase.append("'\r\n    ");
    }
    sbClassCase.append("ELSE '");
    sbClassCase.append(Object.class.getName());
    sbClassCase.append("'\r\n    END");
    sbClassCase.append("\r\n  END");
    StringBuilder sbBaseTypeCase = new StringBuilder("CASE METATYPE WHEN 'R' THEN ");
    sbBaseTypeCase.append(String.valueOf(Types.NULL));
    sbBaseTypeCase.append(" ELSE CASE SOURCENAME\r\n    ");
    for (Iterator<String> iterDataType = mapDataType.keySet().iterator(); iterDataType.hasNext(); )
    {
      String sTypeName = iterDataType.next();
      Integer iBaseType = mapDataType.get(sTypeName);
      sbBaseTypeCase.append("WHEN '");
      sbBaseTypeCase.append(sTypeName);
      sbBaseTypeCase.append("' THEN ");
      sbBaseTypeCase.append(String.valueOf(iBaseType));
      sbBaseTypeCase.append("\r\n    ");
    }
    sbBaseTypeCase.append("ELSE ");
    sbBaseTypeCase.append(String.valueOf(Types.OTHER));
    sbBaseTypeCase.append(" END");
    sbBaseTypeCase.append("\r\n  END");
    StringBuilder sbDataTypeCase = new StringBuilder("CASE METATYPE\r\n    ");
    sbDataTypeCase.append("WHEN 'R' THEN ");
    sbDataTypeCase.append(String.valueOf(Types.STRUCT));
    sbDataTypeCase.append("\r\n    ");
    sbDataTypeCase.append("WHEN 'T' THEN ");
    sbDataTypeCase.append(String.valueOf(Types.DISTINCT));
    sbDataTypeCase.append("\r\n    ");
    sbDataTypeCase.append("END");
    StringBuilder sbSql = new StringBuilder("SELECT\r\n  ");
    sbSql.append("NULL AS TYPE_CAT,\r\n  ");
    sbSql.append("TRIM(TYPESCHEMA) AS TYPE_SCHEM,\r\n  ");
    sbSql.append("TYPENAME AS TYPE_NAME,\r\n  ");
    sbSql.append(sbClassCase.toString()+" AS CLASS_NAME,\r\n  ");
    sbSql.append(sbDataTypeCase.toString()+" AS DATA_TYPE,\r\n  ");
    sbSql.append("REMARKS AS REMARKS,\r\n  ");
    sbSql.append(sbBaseTypeCase.toString()+" AS BASE_TYPE\r\n");
    sbSql.append("FROM SYSCAT.DATATYPES\r\n");
    sbSql.append(sbCondition.toString());
    Statement stmt = getConnection().unwrap(Connection.class).createStatement();
    ResultSet rs = stmt.executeQuery(sbSql.toString());
    return rs;
  } /* getUDTs */

} /* class Db2DatabaseMetaData */
