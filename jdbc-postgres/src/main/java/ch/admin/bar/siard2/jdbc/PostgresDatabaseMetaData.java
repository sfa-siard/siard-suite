/*======================================================================
PostgresDatabaseMetaData implements wrapped Postgres DatabaseMetaData.
Application : SIARD2
Description : PostgresDatabaseMetaData implements wrapped Postgres DatabaseMetaData.
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 25.07.2019, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import ch.admin.bar.siard2.postgres.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.logging.*;

/*====================================================================*/
/** PostgresDatabaseMetaData implements wrapped Postgres DatabaseMetaData.
 * @author Hartwig Thomas
 */
public class PostgresDatabaseMetaData
  extends BaseDatabaseMetaData
  implements DatabaseMetaData
{
  public static final String sRANGE_START = "range_start";
  public static final String sRANGE_END = "range_end";
  public static final String sRANGE_SIGNATURE = "range_signature";
  
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(PostgresDatabaseMetaData.class.getName());
  private static int iMAX_VAR_SIZE = 10*1024*1024;
  Connection _conn = null;
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param dmdWrapped database meta data to be wrapped.
   */
  public PostgresDatabaseMetaData(DatabaseMetaData dmdWrapped, Connection conn)
  {
    super(dmdWrapped);
    _conn = conn;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Connection getConnection() throws SQLException
  {
    return _conn;
  } /* getConnection */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Use PostgresMetaColumn for data type translation.
   */
  @Override
  public ResultSet getColumns(String catalog, String schemaPattern,
    String tableNamePattern, String columnNamePattern)
    throws SQLException
  {
    DatabaseMetaData dmd = unwrap(DatabaseMetaData.class);
    ResultSet rs = dmd.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
    PostgresStatement stmt = new PostgresStatement(rs.getStatement(),_conn);
    return new PostgresMetaColumns(new PostgresResultSet(rs,stmt),_conn,1,2,5,6,7,16,9,10,22);
  } /* getColumns */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSet getProcedureColumns(String catalog,
    String schemaPattern, String procedureNamePattern,
    String columnNamePattern) throws SQLException
  {
    DatabaseMetaData dmd = unwrap(DatabaseMetaData.class);
    ResultSet rs = dmd.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
    PostgresStatement stmt = new PostgresStatement(rs.getStatement(),_conn);
    return new PostgresMetaColumns(new PostgresResultSet(rs,stmt),_conn,1,2,6,7,8,9,10,-1,-1);
  } /* getProcedureColumns */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSet getFunctionColumns(String catalog,
    String schemaPattern, String functionNamePattern,
    String columnNamePattern) throws SQLException
  {
    DatabaseMetaData dmd = unwrap(DatabaseMetaData.class);
    ResultSet rs = dmd.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
    PostgresStatement stmt = new PostgresStatement(rs.getStatement(),_conn);
    return new PostgresMetaColumns(new PostgresResultSet(rs,stmt),_conn,1,2,6,7,8,9,10,-1,-1);
  } /* getFunctionColumns */
  
  /*------------------------------------------------------------------*/
  private String getTypeNamespace(String sPgType ,String sPgNamespace)
  {
    return "(pg_type "+ sPgType+" JOIN pg_namespace "+sPgNamespace+" ON "+sPgType+".typnamespace = "+sPgNamespace+".oid)";    
  } /* getTypeNamespace */
  
  /*------------------------------------------------------------------*/
  private String getQualifiedType(String sPgNamespace, String sPgType)
  {
    return "'\"' || " + sPgNamespace + ".nspname || '\".\"' || "+sPgType+".typname || '\"'";
  } /* getQualifiedType */
  
  /*------------------------------------------------------------------*/
  /** return CASE statement evaluating predefined type of expression.
   */
  private String getCasePredefinedType(String sPgType)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\r\n    CASE "+sPgType+".typname");
    for (PostgresType pgt : PostgresType.values())
    {
      PreType pt = pgt.getPreType();
      sb.append("\r\n      WHEN "+PostgresLiterals.formatStringLiteral(pgt.getKeyword())+
        " THEN "+String.valueOf(pt.getSqlType()));
      for (String sAlias : pgt.getAliases())
      {
        sb.append("\r\n      WHEN "+PostgresLiterals.formatStringLiteral(sAlias)+
          " THEN "+String.valueOf(pt.getSqlType()));
      }
    }
    sb.append("\r\n      ELSE "+String.valueOf(Types.NULL));
    sb.append("\r\n    END");
    return sb.toString();
  } /* getCasePredefinedType */
  
  /*------------------------------------------------------------------*/
  /** return the base type of domains, enums and ranges.
   * @return base type.
   */
  private String getCaseBaseType(String sPgType, String sPgTypeBase, String sPgNamespaceTypeBase)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\r\n  CASE "+sPgType+".typtype");
    sb.append("\r\n    WHEN "+PostgresLiterals.formatStringLiteral("e")+" THEN "+String.valueOf(Types.VARCHAR));
    sb.append("\r\n    WHEN "+PostgresLiterals.formatStringLiteral("d")+" THEN ");
    sb.append("\r\n      CASE "+sPgNamespaceTypeBase+".nspname");
    sb.append("\r\n        WHEN 'pg_catalog' THEN "+getCasePredefinedType(sPgTypeBase));
    sb.append("\r\n        ELSE "+String.valueOf(Types.NULL));
    sb.append("\r\n      END");
    sb.append("\r\n    ELSE "+String.valueOf(Types.NULL));
    sb.append("\r\n  END");
    return sb.toString();
  } /* getCaseBaseType */
  
  /*------------------------------------------------------------------*/
  private String getCaseDataType(String sDataType)
  {
    StringBuilder sbDataTypeCase = new StringBuilder("  CASE "+sDataType);
    for (PostgresType pgt : PostgresType.values())
    {
      sbDataTypeCase.append("\r\n    WHEN "+PostgresLiterals.formatStringLiteral(pgt.getKeyword())+
        " THEN "+String.valueOf(pgt.getPreType().getSqlType()));
      for (String sAlias : pgt.getAliases())
      {
        sbDataTypeCase.append("\r\n    WHEN "+PostgresLiterals.formatStringLiteral(sAlias)+
          " THEN "+String.valueOf(pgt.getPreType().getSqlType()));
      }
    }
    sbDataTypeCase.append("\r\n    ELSE "+String.valueOf(Types.STRUCT));
    sbDataTypeCase.append("\r\n  END");
    return sbDataTypeCase.toString();
  } /* getCaseDataType */
  
  /*------------------------------------------------------------------*/
  private String getClassWhen(String sCategory, Class<?> cls)
  {
    return "\r\n    WHEN "+ PostgresLiterals.formatStringLiteral(sCategory) +
      " THEN "+PostgresLiterals.formatStringLiteral(cls.getName());
  }
  /*------------------------------------------------------------------*/
  /** return class name for UDT.
   * (Could be improved by basing it on the base type for DISTINCT ...)
   * @return
   */
  private String getCaseClassName(String sTypExpression)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\r\n  CASE "+sTypExpression+".typcategory");
    sb.append(getClassWhen("A",java.sql.Array.class));
    sb.append(getClassWhen("B",java.lang.Boolean.class));
    sb.append(getClassWhen("C",java.sql.Struct.class));
    sb.append(getClassWhen("D",java.sql.Timestamp.class));
    sb.append(getClassWhen("E",java.lang.String.class));
    sb.append(getClassWhen("G",java.lang.String.class));
    sb.append(getClassWhen("I",(new byte[] {}).getClass()));
    sb.append(getClassWhen("N",java.math.BigDecimal.class));
    sb.append(getClassWhen("S",java.lang.String.class));
    sb.append(getClassWhen("T",java.time.Duration.class));
    sb.append(getClassWhen("U",java.sql.Struct.class));
    sb.append(getClassWhen("V",java.lang.String.class));
    sb.append("\r\n    ELSE "+PostgresLiterals.formatStringLiteral(java.lang.Object.class.getName()));
    sb.append("\r\n  END");
    return sb.toString();
  } /* getCaseClassName */
  
  /*------------------------------------------------------------------*/
  /** return the data type of the UDT.
   * @return STRUCT or DISTINCT.
   */
  private String getCaseUdtDataType(String sPgType)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\r\n  CASE "+sPgType+".typtype");
    sb.append("\r\n    WHEN "+PostgresLiterals.formatStringLiteral("c")+ " THEN "+String.valueOf(Types.STRUCT));
    sb.append("\r\n    WHEN "+PostgresLiterals.formatStringLiteral("r")+" THEN "+String.valueOf(Types.STRUCT));
    sb.append("\r\n    ELSE "+String.valueOf(Types.DISTINCT));
    sb.append("\r\n  END");
    return sb.toString();
  } /* getCaseUdtDataType */
  
  /*------------------------------------------------------------------*/
  /** return the FROM tables for the UDT query.
   * @return FROM tables.
   */
  private String getUdtFromTables(String sPgType, String sPgNamespaceType,
    String sPgClass, String sPgTypeBase, String sPgNamespaceTypeBase, String sPgDescription)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\r\n  "+getTypeNamespace(sPgType ,sPgNamespaceType));
    sb.append("\r\n  LEFT JOIN pg_class "+sPgClass+" ON "+sPgType+".oid = "+sPgClass+".reltype");
    sb.append("\r\n  LEFT JOIN "+getTypeNamespace(sPgTypeBase,sPgNamespaceTypeBase)+" ON "+sPgType+".typbasetype = "+sPgTypeBase+".oid");
    sb.append("\r\n  LEFT JOIN pg_description "+sPgDescription+" ON "+sPgType+".oid = "+sPgDescription+".objoid");
    return sb.toString();
  } /* getUdtFromTables */

  /*------------------------------------------------------------------*/
  private String getUdtCondition(String sPgType, String sPgNamespace, String sPgClass,
    String catalog, String schemaPattern, String typeNamePattern, int[] types)
  {
    if (types == null)
      types = new int[] {Types.STRUCT, Types.DISTINCT};
    StringBuilder sbTypes = new StringBuilder();
    String sPgTypeType = sPgType + ".typtype"; 
    for (int i = 0; i < types.length; i++)
    {
      String sTypeCondition = null; 
      if (types[i] == Types.DISTINCT)
      {
        sTypeCondition = sPgTypeType+" = 'd' OR "+sPgTypeType+" = 'e'";
      }
      else if (types[i] == Types.STRUCT)
        sTypeCondition = "("+sPgTypeType+" = 'c' AND ("+sPgClass+".relkind = 'c')) OR "+sPgTypeType+" = 'r'";
      else
        sTypeCondition = "FALSE";
      if ((sTypeCondition != null) && (sbTypes.length() > 0))
        sbTypes.append(" OR ");
      sbTypes.append(sTypeCondition);
    }
    StringBuilder sb = new StringBuilder();
    sb.append(sPgType+".typisdefined");
    if (sbTypes.length() > 0)
      sb.append(" AND\r\n  ("+sbTypes.toString()+")");
    if (catalog != null)
      sb.append(" AND\r\n  'postgres' = " + PostgresLiterals.formatStringLiteral(catalog));
    if (schemaPattern != null)
      sb.append(" AND\r\n  "+sPgNamespace+".nspname LIKE "+PostgresLiterals.formatStringLiteral(schemaPattern));
    if (typeNamePattern != null)
      sb.append(" AND\r\n  "+sPgType+".typname LIKE "+PostgresLiterals.formatStringLiteral(typeNamePattern));
    return sb.toString();
  } /* getUdtCondition */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSet getUDTs(String catalog, String schemaPattern,
    String typeNamePattern, int[] types) throws SQLException
  {
    String sPgType = "t";
    String sPgNamespace = "nt";
    String sPgClass = "c";
    String sPgTypeBase = "bt";
    String sPgNamespaceTypeBase = "nbt";
    String sPgDescription = "d";
    
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT");
    sb.append("\r\n  NULL AS TYPE_CAT,");
    sb.append("\r\n  "+sPgNamespace+".nspname AS TYPE_SCHEM,");
    sb.append("\r\n  "+sPgType+".typname AS TYPE_NAME,");
    sb.append("\r\n  "+getCaseClassName(sPgType)+" AS CLASS_NAME,");
    sb.append("\r\n  "+getCaseUdtDataType(sPgType)+" AS DATA_TYPE,");
    sb.append("\r\n  "+sPgDescription+".description AS REMARKS,");
    sb.append("\r\n  "+getCaseBaseType(sPgType,sPgTypeBase,sPgNamespaceTypeBase)+" AS BASE_TYPE");
    sb.append("\r\nFROM "+getUdtFromTables(sPgType,sPgNamespace,sPgClass,sPgTypeBase,sPgNamespaceTypeBase,sPgDescription));
    sb.append("\r\nWHERE "+getUdtCondition(sPgType,sPgNamespace,sPgClass,catalog,schemaPattern,typeNamePattern, types));
    sb.append("\r\nORDER BY 1 ASC, 2 ASC, 3 ASC");
    Statement stmt = getConnection().createStatement().unwrap(Statement.class);
    ResultSet rsUdts = stmt.executeQuery(sb.toString());
    return rsUdts;
  } /* getUDTs */

  /*------------------------------------------------------------------*/
  private String getAttributesFrom(String sPgTypeParent, String sPgNamespaceTypeParent, String sPgClass, 
    String sPgAttribute, String sPgTypeAttribute, String sPgNamespaceTypeAttribute, 
    String sPgTypeAttributeBase, String sPgNamespaceTypeAttributeBase, String sPgDescription,
    String sPgRange, String sPgTypeRange, String sPgNamespaceTypeRange, 
    String sPgTypeRangeBase, String sPgNamespaceTypeRangeBase, String sPgValues )
  {
    StringBuilder sb = new StringBuilder();
    sb.append(getTypeNamespace(sPgTypeParent,sPgNamespaceTypeParent));
    sb.append("\r\n  LEFT JOIN");
    sb.append("\r\n  (pg_class "+sPgClass+" JOIN pg_attribute "+sPgAttribute+" ON "+sPgClass+".oid = "+sPgAttribute+".attrelid");
    sb.append("\r\n    JOIN "+getTypeNamespace(sPgTypeAttribute,sPgNamespaceTypeAttribute)+ " ON "+sPgAttribute+".atttypid = "+sPgTypeAttribute+".oid");
    sb.append("\r\n    LEFT JOIN "+getTypeNamespace(sPgTypeAttributeBase,sPgNamespaceTypeAttributeBase)+" ON "+sPgTypeAttribute+".typbasetype = "+sPgTypeAttributeBase+".oid");
    sb.append("\r\n  ) ON " + sPgTypeParent+".oid = "+sPgClass+".reltype");
    
    sb.append("\r\n  LEFT JOIN");
    sb.append("\r\n  (pg_range "+sPgRange);
    sb.append("\r\n    JOIN "+getTypeNamespace(sPgTypeRange, sPgNamespaceTypeRange) + " ON "+sPgRange+".rngsubtype = "+sPgTypeRange+".oid");
    sb.append("\r\n    JOIN (VALUES");
    sb.append("\r\n      ('"+sRANGE_START+"',NULL,NULL,NULL,1,NULL,NULL),");
    sb.append("\r\n      ('"+sRANGE_END+"',NULL,NULL,NULL,2,NULL,NULL),");
    sb.append("\r\n      ('"+sRANGE_SIGNATURE+"','pg_catalog','char',2,3,'NO','NULL')");
    sb.append("\r\n     ) " +sPgValues+" (attname,nspname,typname,typlen,attnum,attnotnull,typbasetypename) ON TRUE");
    sb.append("\r\n    LEFT JOIN "+getTypeNamespace(sPgTypeRangeBase,sPgNamespaceTypeRangeBase)+" ON "+sPgTypeRange+".typbasetype = "+sPgTypeRangeBase+".oid");
    sb.append("\r\n  ) ON "+sPgTypeParent+".oid = "+sPgRange+".rngtypid");

    sb.append("\r\n    LEFT JOIN pg_description "+sPgDescription+" ON ("+sPgClass+".oid = "+sPgDescription+".objoid) OR ("+sPgRange+".rngsubtype = "+sPgDescription+".objoid)");
    return sb.toString();
  } /* getAttributesFrom */

  /*------------------------------------------------------------------*/
  private String getAttributesCondition(String sPgTypeParent, String sPgTypeNamespace, 
    String sPgClass, String sPgAttribute, String sPgValues, 
    String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern)
  {
    String sPgTypeType = sPgTypeParent + ".typtype"; 
    StringBuilder sb = new StringBuilder();
    sb.append("\r\n  "+sPgTypeParent+".typisdefined");
    sb.append(" AND (("+sPgTypeType+" = 'c' AND "+sPgClass+".relkind = 'c') OR ("+sPgTypeType+" = 'r'))");
    if (catalog != null)
      sb.append(" AND\r\n  "+PostgresLiterals.formatStringLiteral("postgres")+" = "+PostgresLiterals.formatStringLiteral(catalog));
    if (schemaPattern != null)
      sb.append(" AND\r\n  "+sPgTypeNamespace+".nspname LIKE "+PostgresLiterals.formatStringLiteral(schemaPattern));
    if (typeNamePattern != null)
      sb.append(" AND\r\n  "+sPgTypeParent+".typname LIKE "+PostgresLiterals.formatStringLiteral(typeNamePattern));
    if (attributeNamePattern != null)
      sb.append(" AND\r\n  "+getCaseAttributeName(sPgAttribute,sPgValues)+" LIKE "+PostgresLiterals.formatStringLiteral(attributeNamePattern));
    return sb.toString();
  } /* getAttributesCondition */
  
  private String getCaseAttributeName(String sPgAttribute, String sPgValues)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("CASE");
    sb.append("\r\n    WHEN "+sPgAttribute+".attname IS NULL THEN "+sPgValues+".attname");
    sb.append("\r\n    ELSE "+sPgAttribute+".attname");
    sb.append("\r\n  END");
    return sb.toString();
  } /* getCaseAttributeName */
  
  /*------------------------------------------------------------------*/
  private String getCaseTypeName(String sPgTypeAttribute, String sPgNamespaceTypeAttribute,
    String sPgTypeRange, String sPgNamespaceTypeRange, String sPgValues)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("CASE");
    sb.append("\r\n    WHEN "+sPgTypeAttribute+".typname IS NULL THEN ");
    sb.append("\r\n      CASE");
    sb.append("\r\n        WHEN "+sPgValues+".typname IS NULL THEN "+getQualifiedType(sPgNamespaceTypeRange,sPgTypeRange));
    sb.append("\r\n        ELSE '\"' || "+sPgValues+".nspname || '\".\"' || "+sPgValues+".typname || '\"'");
    sb.append("\r\n      END");
    sb.append("\r\n    ELSE "+getQualifiedType(sPgNamespaceTypeAttribute,sPgTypeAttribute));
    sb.append("\r\n  END");
    return sb.toString();
  } /* getCaseTypeName */
  
  private String getCaseAttrSize(String sPgTypeAttribute, String sPgTypeRange, String sPgValues)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("CASE");
    sb.append("\r\n    WHEN "+sPgTypeRange+".typlen IS NULL THEN");
    sb.append("\r\n      CASE "+sPgTypeAttribute+".typlen");
    sb.append("\r\n        WHEN -1 THEN "+String.valueOf(Integer.MAX_VALUE));
    sb.append("\r\n        ELSE "+sPgTypeAttribute+".typlen");
    sb.append("\r\n      END");
    sb.append("\r\n    ELSE");
    sb.append("\r\n      CASE");
    sb.append("\r\n        WHEN "+sPgValues+".typlen IS NULL THEN");
    sb.append("\r\n          CASE "+sPgTypeRange+".typlen");
    sb.append("\r\n            WHEN -1 THEN "+String.valueOf(Integer.MAX_VALUE));
    sb.append("\r\n            ELSE "+sPgTypeRange+".typlen");
    sb.append("\r\n          END");
    sb.append("\r\n        ELSE "+sPgValues+".typlen");
    sb.append("\r\n      END");
    sb.append("\r\n  END");
    return sb.toString();
  } /* getCaseAttrSize */
  
  private String getCaseOrdinalPosition(String sPgAttribute, String sPgValues)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("CASE");
    sb.append("\r\n    WHEN "+sPgValues+".attnum IS NULL THEN "+sPgAttribute+".attnum");
    sb.append("\r\n    ELSE "+sPgValues+".attnum");
    sb.append("\r\n  END");
    return sb.toString();
  } /* getCaseOrdinalPosition */
  
  private String getCaseNullable(String sPgClass, String sPgAttribute, String sPgTypeAttribute, String sPgTypeRange, String sPgValues)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("CASE");
    sb.append("\r\n    WHEN "+sPgClass+".oid IS NULL THEN");
    sb.append("\r\n      CASE");
    sb.append("\r\n        WHEN "+sPgValues+".attnotnull IS NULL THEN");
    sb.append("\r\n          CASE "+sPgTypeRange+".typnotnull");
    sb.append("\r\n            WHEN TRUE THEN "+String.valueOf(DatabaseMetaData.attributeNoNulls));
    sb.append("\r\n            ELSE "+String.valueOf(DatabaseMetaData.attributeNullable));
    sb.append("\r\n          END");
    sb.append("\r\n        ELSE");
    sb.append("\r\n          CASE "+sPgValues+".attnotnull");
    sb.append("                WHEN 'YES' THEN "+String.valueOf(DatabaseMetaData.attributeNoNulls));
    sb.append("\r\n            ELSE "+String.valueOf(DatabaseMetaData.attributeNullable));
    sb.append("\r\n          END");
    sb.append("\r\n      END");
    sb.append("\r\n    ELSE");
    sb.append("\r\n      CASE");
    sb.append("\r\n        WHEN ("+sPgAttribute+".attnotnull OR (("+sPgTypeAttribute+".typtype = 'd') AND "+sPgTypeAttribute+".typnotnull)) THEN "+String.valueOf(DatabaseMetaData.attributeNoNulls));
    sb.append("\r\n        ELSE "+String.valueOf(DatabaseMetaData.attributeNullable));
    sb.append("\r\n      END");
    sb.append("\r\n  END");
    return sb.toString();    
  } /* getCaseNullable */
  
  private String getCaseIsNullable(String sPgClass, String sPgAttribute, String sPgTypeAttribute, String sPgTypeRange, String sPgValues)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("CASE");
    sb.append("\r\n    WHEN "+sPgClass+".oid IS NULL THEN");
    sb.append("\r\n      CASE");
    sb.append("\r\n        WHEN "+sPgValues+".attnotnull IS NULL THEN");
    sb.append("\r\n          CASE "+sPgTypeRange+".typnotnull");
    sb.append("\r\n            WHEN TRUE THEN 'NO'");
    sb.append("\r\n            ELSE 'YES'");
    sb.append("\r\n          END");
    sb.append("\r\n        ELSE");
    sb.append("\r\n          CASE "+sPgValues+".attnotnull");
    sb.append("\r\n           WHEN 'YES' THEN 'NO'");
    sb.append("\r\n           ELSE 'YES'");
    sb.append("\r\n          END");
    sb.append("\r\n      END");
    sb.append("\r\n    ELSE");
    sb.append("\r\n      CASE");
    sb.append("\r\n        WHEN ("+sPgAttribute+".attnotnull OR (("+sPgTypeAttribute+".typtype = 'd') AND "+sPgTypeAttribute+".typnotnull)) THEN 'NO'");
    sb.append("\r\n        ELSE 'YES'");
    sb.append("\r\n      END");
    sb.append("\r\n  END");
    return sb.toString();    
  } /* getCaseNullable */
  
  private String getCaseSourceTypeName(String sPgClass, String sPgValues,
    String sPgTypeAttributeBase, String sPgNamespaceTypeAttributeBase,
    String sPgTypeRangeBase, String sPgNamespaceTypeRangeBase)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("CASE");
    sb.append("\r\n    WHEN "+sPgClass+".oid IS NULL THEN");
    sb.append("\r\n      CASE");
    sb.append("\r\n        WHEN "+sPgValues+".typbasetypename IS NULL THEN "+getQualifiedType(sPgNamespaceTypeRangeBase,sPgTypeRangeBase));
    sb.append("\r\n        ELSE '\"' || "+sPgValues+".nspname || '\".\"' || "+sPgValues+".typbasetypename || '\"'");
    sb.append("\r\n      END");
    sb.append("\r\n    ELSE "+getQualifiedType(sPgNamespaceTypeAttributeBase,sPgTypeAttributeBase));
    sb.append("\r\n  END");
    return sb.toString();
  } /* getCaseSourceTypeName */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSet getAttributes(String catalog, String schemaPattern,
    String typeNamePattern, String attributeNamePattern)
    throws SQLException
  {
    _il.enter(catalog,schemaPattern,typeNamePattern,attributeNamePattern);
    String sPgTypeParent = "pt"; 
    String sPgNamespaceTypeParent = "npt";
    String sPgClass = "c"; 
    String sPgAttribute = "a"; 
    String sPgTypeAttribute = "at";
    String sPgNamespaceTypeAttribute = "nat";
    String sPgTypeAttributeBase = "abt";
    String sPgNamespaceTypeAttributeBase = "nabt";
    String sPgRange = "r"; 
    String sPgTypeRange = "rt";
    String sPgNamespaceTypeRange = "nrt";
    String sPgTypeRangeBase = "rbt"; 
    String sPgNamespaceTypeRangeBase = "nrbt";
    String sPgValues = "v";
    String sPgDescription = "d";

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT");
    sb.append("\r\n  current_database() AS TYPE_CAT,");
    sb.append("\r\n  "+sPgNamespaceTypeParent+".nspname AS TYPE_SCHEM,");
    sb.append("\r\n  "+sPgTypeParent+".typname AS TYPE_NAME,");
    sb.append("\r\n  "+getCaseAttributeName(sPgAttribute,sPgValues)+" AS ATTR_NAME,");
    sb.append("\r\n  "+getCaseDataType(getCaseTypeName(sPgTypeAttribute,sPgNamespaceTypeAttribute,sPgTypeRange,sPgNamespaceTypeRange,sPgValues))+" AS DATA_TYPE,");
    sb.append("\r\n  "+getCaseTypeName(sPgTypeAttribute,sPgNamespaceTypeAttribute,sPgTypeRange,sPgNamespaceTypeRange,sPgValues)+" AS ATTR_TYPE_NAME,");
    sb.append("\r\n  "+getCaseAttrSize(sPgTypeAttribute,sPgTypeRange,sPgValues)+" AS ATTR_SIZE,");
    sb.append("\r\n  NULL AS DECIMAL_DIGITS,");
    sb.append("\r\n  10 AS NUM_PREC_RADIX,");
    sb.append("\r\n  "+getCaseNullable(sPgClass, sPgAttribute, sPgTypeAttribute, sPgTypeRange, sPgValues)+" AS NULLABLE,");
    sb.append("\r\n  "+sPgDescription+".description AS REMARKS,");
    sb.append("\r\n  NULL AS ATTR_DEF,");
    sb.append("\r\n  NULL AS SQL_DATA_TYPE,");
    sb.append("\r\n  NULL AS SQL_DATETIME_SUB,");
    sb.append("\r\n  "+String.valueOf(iMAX_VAR_SIZE)+" AS CHAR_OCTET_LENGTH,");
    sb.append("\r\n  "+getCaseOrdinalPosition(sPgAttribute, sPgValues)+" AS ORDINAL_POSITION,");
    sb.append("\r\n  "+getCaseIsNullable(sPgClass, sPgAttribute, sPgTypeAttribute, sPgTypeRange, sPgValues)+" AS IS_NULLABLE,");
    sb.append("\r\n  NULL AS SCOPE_CATALOG,");
    sb.append("\r\n  NULL AS SCOPE_SCHEMA,");
    sb.append("\r\n  NULL AS SCOPE_TABLE,");
    sb.append("\r\n  "+getCaseDataType(getCaseSourceTypeName(sPgClass, sPgValues, 
      sPgTypeAttributeBase, sPgNamespaceTypeAttributeBase, sPgTypeRangeBase, sPgNamespaceTypeRangeBase))+" AS SOURCE_DATA_TYPE");
    sb.append("\r\nFROM\r\n  "+getAttributesFrom(sPgTypeParent, sPgNamespaceTypeParent, sPgClass,
      sPgAttribute, sPgTypeAttribute, sPgNamespaceTypeAttribute, 
      sPgTypeAttributeBase, sPgNamespaceTypeAttributeBase, sPgDescription,
      sPgRange, sPgTypeRange, sPgNamespaceTypeRange, 
      sPgTypeRangeBase, sPgNamespaceTypeRangeBase, sPgValues));
    sb.append("\r\nWHERE\r\n  "+getAttributesCondition(sPgTypeParent, sPgNamespaceTypeParent, 
      sPgClass, sPgAttribute, sPgValues,
      catalog, schemaPattern, typeNamePattern, attributeNamePattern));
    sb.append("\r\nORDER BY 1 ASC, 2 ASC, 3 ASC, 16 ASC");
    
    Statement stmt = getConnection().createStatement();
    ResultSet rsAttributes = new PostgresResultSet(stmt.unwrap(Statement.class).executeQuery(sb.toString()),stmt);
    return new PostgresMetaColumns(rsAttributes,getConnection(), 1,2,5,6,7,7,8,9,21);
  } /* getAttributes */

} /* class PostgresDatabaseMetaData */
