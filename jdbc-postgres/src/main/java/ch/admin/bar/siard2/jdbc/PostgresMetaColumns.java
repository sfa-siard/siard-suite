/*======================================================================
PostgresMetaColumns implements data type mapping from Postgres to ISO SQL.
Application : SIARD2
Description : PostgresMetaColumns implements data type mapping from Postgres to ISO SQL.
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 09.08.2019, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.text.*;

import ch.admin.bar.siard2.ColumnIdentifier;
import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.admin.bar.siard2.postgres.*;
import ch.admin.bar.siard2.postgres.identifier.*;

/*====================================================================*/
/** PostgresMetaColumns implements data type mapping from Postgres to ISO SQL.
 * @author Hartwig Thomas
 */
public class PostgresMetaColumns
  extends PostgresResultSet
{
  static final int iMAX_NUMERIC_PRECISION = 1000;
  static final int iMAX_VAR_LENGTH = 10485760;
  static final int iMAX_TEXT_LENGTH = 1024*1024*1024;
  private int _iDataType = -1;
  private int _iTypeName = -1;
  private int _iPrecision = -1;
  private int _iLength = -1;
  private int _iScale = -1;
  private int _iNumPrecRadix = -1;
  private int _iSourceDataType = -1;

  private Connection _conn;

  private PostgresQualifiedId parseTypeName(String sTypeName)
    throws ParseException, SQLException
  {
    PostgresQualifiedId pqiType = null;
    /* we need to prevent a parsing error due to reserved keywords by quoting */
    if (!(sTypeName.startsWith("\"") && sTypeName.endsWith("\"")))
    {
      String[] as = sTypeName.split("\\.");
      sTypeName = "\"" + String.join("\".\"", as) + "\"";
    }
    pqiType = new PostgresQualifiedId(sTypeName);
    if (pqiType.getSchema() == null)
    {
      BaseDatabaseMetaData bdmd = (BaseDatabaseMetaData)_conn.getMetaData();
      ResultSet rs = bdmd.getUDTs(null, "%", bdmd.toPattern(pqiType.getName()), null);
      if (rs.next())
        pqiType.setSchema(rs.getString("TYPE_SCHEM"));
      else
        pqiType.setSchema("pg_catalog");
      rs.close();
    }
    sTypeName = pqiType.format();
    return pqiType;
  } /* parse type name */

  private static String formatTypeName(PostgresQualifiedId pqiType)
  {
    String sTypeName = pqiType.format();
    /* we want qualified types to be quoted and simple types to be unquoted */
    if (pqiType.getSchema().equals("pg_catalog"))
    {
      if (!PostgresType.setBUILTIN_RANGES.contains(pqiType.getName()))
        sTypeName = pqiType.getName();
    }
    else if (pqiType.getSchema().equals("public") &&
      (pqiType.getName().equals("blob") || pqiType.getName().equals("clob")))
      sTypeName = pqiType.getName();
    return sTypeName;
  }

  private static int getAttTypMod(Connection conn, ColumnIdentifier columnIdentifier) throws SQLException {
    return getAttTypMod(conn, columnIdentifier.getSchemaName(), columnIdentifier.getTableName(), columnIdentifier.getColumnName());
  }

  private static int getAttTypMod(Connection conn, String sSchemaName, String sTableName, String sColumnName)
    throws SQLException
  {
    int iAttTypMod = -1;
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT a.atttypmod");
    sb.append("\r\nFROM pg_attribute a");
    sb.append("\r\n  JOIN pg_class c ON (a.attrelid = c.oid)");
    sb.append("\r\n  JOIN pg_namespace cn ON (c.relnamespace = cn.oid)");
    sb.append("\r\nWHERE c.relname = '");
    sb.append(sTableName);
    sb.append("' AND");
    sb.append("\r\n cn.nspname = '");
    sb.append(sSchemaName);
    sb.append("' AND");
    sb.append("\r\n a.attname = '");
    sb.append(sColumnName);
    sb.append("'");
    Connection connPostgres = conn.unwrap(Connection.class);
    Statement stmt = connPostgres.createStatement();
    ResultSet rs = stmt.executeQuery(sb.toString());
    if (rs.next())
      iAttTypMod = rs.getInt(1);
    rs.close();
    return iAttTypMod;
  } /* getAttTypMod */


  public static String getIntervalTypeName(Connection conn, ColumnIdentifier columnIdentifier, String sTypeName)
      throws SQLException
  {
    int iAttTypMod = getAttTypMod(conn,columnIdentifier);
    if (iAttTypMod <= 0)
      sTypeName = "VARCHAR";
    else
    {
      int iDecimals = iAttTypMod & 0x0000FFFF;
      boolean bYear = (iAttTypMod & 0x00040000) != 0;
      boolean bMonth = (iAttTypMod & 0x00020000) != 0;
      boolean bDay =  (iAttTypMod & 0x00080000) != 0;
      boolean bHour =  (iAttTypMod & 0x04000000) != 0;
      boolean bMinute =  (iAttTypMod & 0x08000000) != 0;
      boolean bSecond =  (iAttTypMod & 0x10000000) != 0;
      if (bYear)
      {
        if (bMonth)
          sTypeName = "interval year to month";
        else
          sTypeName = "interval year";
      }
      else if (bMonth)
        sTypeName = "interval month";
      else if (bDay)
      {
        if (bSecond)
          sTypeName = "interval day to second";
        else if (bMinute)
          sTypeName = "interval day to minute";
        else if (bHour)
          sTypeName = "interval day to hour";
        else
          sTypeName = "interval day";
      }
      else if (bHour)
      {
        if (bSecond)
          sTypeName = "interval hour to second";
        else if (bMinute)
          sTypeName = "interval hour to minute";
        else
          sTypeName = "interval hour";
      }
      else if (bMinute)
      {
        if (bSecond)
          sTypeName = "interval minute to second";
        else
          sTypeName = "interval minute";
      }
      else if (bSecond)
        sTypeName = "interval second";
      if (bSecond && (iDecimals > 0))
        sTypeName = sTypeName + "("+String.valueOf(iDecimals)+")";
    }
    return sTypeName;
  } /* getIntervalTypeName */

  /*------------------------------------------------------------------*/
  private String getTypeName(String sTypeName, int iType)
    throws SQLException
  {
    if (iType == Types.ARRAY)
    {
      // internal names starting with _ are used for array elements
      if (sTypeName.startsWith("_"))
        sTypeName = sTypeName.substring(1);
      PostgresType pgt = PostgresType.getByKeyword(sTypeName);
      if (pgt != null)
      {
        PreType pt = pgt.getPreType();
        sTypeName = pt.getKeyword();
      }
      sTypeName = sTypeName + " ARRAY["+String.valueOf(Integer.MAX_VALUE)+"]";
    }
    else
    {
      try
      {
        PostgresQualifiedId pqiType = parseTypeName(sTypeName);
        sTypeName = formatTypeName(pqiType);
        /**
        if (PostgresType.getByKeyword(sTypeName) == null)
        {
          BaseDatabaseMetaData bdmd = (BaseDatabaseMetaData)_conn.getMetaData();
          ResultSet rs = bdmd.getUDTs(null, "%", bdmd.toPattern(sTypeName), new int[] {Types.DISTINCT});
          String sCatalog = rs.getString("TYPE_CAT");
          String sSchemaName = rs.getString("TYPE_SCHEM");
          String sType = rs.getString("TYPE_NAME");
          qiType = new
          // get full type name
          rs.close();
        }
        ***/
        ColumnIdentifier columnIdentifier = new ColumnIdentifier(this);

          if (PostgresType.INTERVAL.getKeyword().equals(sTypeName)) {
              sTypeName = getIntervalTypeName(_conn, columnIdentifier, sTypeName);
          }

        sTypeName = addTypeLengthAndPrecision(sTypeName, iType, getAttTypMod(_conn, columnIdentifier));
      }
      catch(ParseException pe) { throw new SQLException("Parsing of "+sTypeName+" failed ("+EU.getExceptionMessage(pe)+")!"); }
    }
    return sTypeName;
  } /* getTypeName */

  private String addTypeLengthAndPrecision(String typeName, int type, int typePrecision) {
    if (typePrecision < 0) return typeName;

    String sPrecisionType = "_" + typePrecision;

    if (type == Types.VARCHAR || type == Types.CHAR) {
      typePrecision = typePrecision - 4;
      sPrecisionType = "_" + typePrecision;
    }

    if (type == Types.NUMERIC) {
      int[] decoded = decodeNumericTypmod(typePrecision);
      int precision = decoded[0];
      int scale = decoded[1];
      sPrecisionType = scale <= 0 ? "_" + precision : "_" + precision + "_" + scale;
    }

    return typeName + sPrecisionType;
  }

  private int[] decodeNumericTypmod(int atttypmod) {
    if (atttypmod < 0) return new int[]{-1, -1}; // no precision/scale
    int typmod = atttypmod - 4;
    int precision = typmod >> 16;
    int scale = typmod & 0xFFFF;
    return new int[]{precision, scale};
  }

  /*------------------------------------------------------------------*/
  private int getDataType(int iType, String sTypeName)
    throws SQLException
  {
    PostgresQualifiedId pqiType = null;
    try
    {
     pqiType = parseTypeName(sTypeName);
     sTypeName = formatTypeName(pqiType);
    }
    catch(ParseException pe) { throw new SQLException("Parsing of "+sTypeName+" failed ("+EU.getExceptionMessage(pe)+")!"); }
    PostgresType pgt = PostgresType.getByKeyword(sTypeName);
    if (pgt != null)
    {
      PreType pt = pgt.getPreType();
      if (pt != null)
        iType = pt.getSqlType();
      if ((iType == Types.OTHER) && (PostgresType.INTERVAL.getKeyword().equals(sTypeName)))
      {
        ColumnIdentifier columnIdentifier = new ColumnIdentifier(this);
        if (getAttTypMod(_conn, columnIdentifier) <= 0)
          iType = Types.VARCHAR;
      }
    }
    else
    {
      if (!sTypeName.startsWith("_"))
      {
        if ((pqiType.getSchema().equals("pg_catalog")) &&
          PostgresType.setBUILTIN_RANGES.contains(pqiType.getName()))
          iType = Types.STRUCT;
        else if (pqiType.getSchema().equals("public") && pqiType.getName().equals("clob"))
          iType = Types.CLOB;
        else if (pqiType.getSchema().equals("public") && pqiType.getName().equals("blob"))
          iType = Types.BLOB;
        else
        {
          BaseDatabaseMetaData bdmd = (BaseDatabaseMetaData)_conn.getMetaData();
          ResultSet  rs = bdmd.getUDTs(pqiType.getCatalog(),
            bdmd.toPattern(pqiType.getSchema()),
            bdmd.toPattern(pqiType.getName()),
            null);
          if (rs.next())
            iType = rs.getInt("DATA_TYPE");
          rs.close();
        }
      }
    }
    return iType;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  private int getPrecision(int iPrecision, int iType, String sTypeName)
    throws SQLException
  {
    PostgresQualifiedId pqiType = null;
    try
    {
     pqiType = parseTypeName(sTypeName);
     sTypeName = formatTypeName(pqiType);
    }
    catch(ParseException pe) { throw new SQLException("Parsing of "+sTypeName+" failed ("+EU.getExceptionMessage(pe)+")!"); }
    PreType pt = PreType.getBySqlType(iType);
    PostgresType pgt = PostgresType.getByKeyword(sTypeName);
    if ((pgt == null) && (pt != null) && (iType != Types.OTHER))
      pgt = PostgresType.getByPreType(pt);
    if (pgt != null)
    {
      switch (pgt)
      {
        case SMALLINT:
        case SMALLSERIAL:
          iPrecision = 5;
          break;
        case INTEGER:
        case SERIAL:
          iPrecision = 10;
          break;
        case BIGINT:
        case BIGSERIAL:
        case OID:
        case TXID:
          iPrecision = 19;
          break;
        case MONEY:
        case NUMERIC:
          if (iPrecision > iMAX_NUMERIC_PRECISION)
            iPrecision = 0;
          break;
        case DOUBLE:
          iPrecision = 17;
          break;
        case REAL:
          iPrecision = 8;
          break;
        case BOOLEAN:
          iPrecision = 1;
          break;
        case DATE:
          iPrecision = 13;
          break;
        case TIME:
          iPrecision = 15;
          break;
        case TIMETZ:
          iPrecision = 21;
          break;
        case TIMESTAMP:
          iPrecision = 29;
          break;
        case TIMESTAMPTZ:
          iPrecision = 29;
          break;
        case INTERVAL:
          iPrecision = 49; break;
        case CHAR:
        case VARCHAR:
        case TEXT:
        case JSON:
        case JSONB:
        case XML:
        case TSVECTOR:
        case TSQUERY:
        case POINT:
        case LINE:
        case LSEG:
        case BOX:
        case PATH:
        case POLYGON:
        case CIRCLE:
          if (iPrecision > iMAX_VAR_LENGTH)
            iPrecision = iMAX_VAR_LENGTH;
          break;
        case BYTEA:
          iPrecision = iMAX_TEXT_LENGTH;
          break;
        case BIT:
        case VARBIT:
          // Get the actual bit length directly from the system catalog
          ColumnIdentifier columnIdentifier = new ColumnIdentifier(this);
          int typmod = getAttTypMod(_conn, columnIdentifier);
          if (typmod > 0) {
            iPrecision = typmod;
          }
          break;
        case UUID:
          iPrecision = 16;
          break;
        case MACADDR:
          iPrecision = 6;
          break;
        case MACADDR8:
          iPrecision = 8;
          break;
        case NAME:
          iPrecision = 63;
          break;
        case INET:
        case CIDR:
          iPrecision = 16;
          break;
        case CLOB:
        case BLOB:
          iPrecision = Integer.MAX_VALUE;
          break;
      }
    }
    else if ((pqiType.getSchema().equals("pg_catalog")) &&
      PostgresType.setBUILTIN_RANGES.contains(pqiType.getName()))
      iPrecision = 0;
    else if (iType == Types.DISTINCT)
    {
      iPrecision = Integer.MAX_VALUE;
      if (_iSourceDataType > 0)  // for columns/attributes
      {
        int iSourceDataType = getInt(_iSourceDataType);
        PreType ptSource = PreType.getBySqlType(iSourceDataType);
        if (ptSource != null)
        {
          PostgresType pgtSource = PostgresType.getByPreType(ptSource);
          iPrecision = getPrecision(iPrecision, iSourceDataType, pgtSource.getKeyword());
        }
      }
    }
    else if (iType == Types.STRUCT)
      iPrecision = 0; // Integer.MAX_VALUE;
    else if (iType == Types.ARRAY)
      iPrecision = 0; // Integer.MAX_VALUE;
    return iPrecision;
  } /* getPrecision */

  /*------------------------------------------------------------------*/
  private int getScale(int iScale, int iType, String sTypeName)
  {
    PostgresType pgt = PostgresType.getByKeyword(sTypeName);
    if (pgt != null)
    {
      if ((pgt == PostgresType.INTERVAL) && (iScale > 6))
        iScale = 0;
    }
    else if ((iType == Types.ARRAY) || (iType == Types.STRUCT) || (iType == Types.DISTINCT))
      iScale = 0;
    return iScale;
  } /* getScale */

  /*------------------------------------------------------------------*/
  private int getNumPrecRadix(int iNumPrecRadix, int iType, String sTypeName)
  {
    return iNumPrecRadix;
  } /* getNumPrecRadix */


  /*------------------------------------------------------------------*/
  /** constructor
   * @param rsWrapped DatabaseMetaData.getColumns() result set to be wrapped.
   * @param iCatalog catalog column in wrapped result set.
   * @param iSchema schema column in wrapped result set.
   * @param iDataType data type column in wrapped result set.
   * @param iTypeName type name column in wrapped result set.
   * @param iPrecision precision column in wrapped result set.
   * @param iLength length column in wrapped result set.
   * @param iScale scale column in wrapped result set.
   * @param iNumPrecRadix radix for precision (2 or 10)
   */
  public PostgresMetaColumns(ResultSet rsWrapped,Connection conn,
    int iCatalog, int iSchema, int iDataType, int iTypeName,
    int iPrecision, int iLength, int iScale, int iNumPrecRadix,
    int iSourceDataType)
    throws SQLException
  {
    super(rsWrapped, rsWrapped.getStatement());
    _conn = conn;
    _iDataType = iDataType;
    _iTypeName = iTypeName;
    _iPrecision = iPrecision;
    _iLength = iLength;
    _iScale = iScale;
    _iNumPrecRadix = iNumPrecRadix;
    _iSourceDataType = iSourceDataType;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Type name (mapped to ISO SQL) is returned in TYPE_NAME.
   * Original type name can be retrieved by using unwrap.
   */
  @Override
  public String getString(int columnIndex) throws SQLException
  {
    String sResult = super.getString(columnIndex);
    if (columnIndex == _iTypeName)
    {
      int iLength = super.getInt(_iPrecision);
      if (iLength <= 0)
        iLength = super.getInt(_iLength);
      sResult = getTypeName(
        sResult,
        super.getInt(_iDataType));
    }
    return sResult;
  } /* getString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Mapped java.sql.Types type is returned in DATA_TYPE.
   * Original java.sql.Types type can be retrieved by using unwrap.
   */
  @Override
  public long getLong(int columnIndex) throws SQLException
  {
    int iResult = super.getInt(columnIndex);
    if (columnIndex == _iDataType)
    {
      iResult = getDataType(
        iResult,
        super.getString(_iTypeName)
        );
    }
    else if (columnIndex == _iPrecision)
    {
      iResult = getPrecision(
        iResult,
        super.getInt(_iDataType),
        super.getString(_iTypeName));
    }
    else if (columnIndex == _iScale)
    {
      iResult = getScale(
        iResult,
        super.getInt(_iDataType),
        super.getString(_iTypeName));
    }
    else if (columnIndex == _iLength)
    {
      iResult = getPrecision(
        iResult,
        super.getInt(_iDataType),
        super.getString(_iTypeName));
    }
    else if (columnIndex == _iNumPrecRadix)
    {
      iResult = getNumPrecRadix(
        iResult,
        super.getInt(_iDataType),
        super.getString(_iTypeName));
    }
    return iResult;
  } /* getLong */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Mapped java.sql.Types type is returned in DATA_TYPE.
   * Original java.sql.Types type can be retrieved by using unwrap.
   */
  @Override
  public int getInt(int columnIndex) throws SQLException
  {
    int iResult = (int)getLong(columnIndex);
    return iResult;
  } /* getInt */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Mapped java.sql.Types type is returned in DATA_TYPE.
   * Type name (mapped to ISO SQL) is returned in TYPE_NAME.
   */
  @Override
  public Object getObject(int columnIndex) throws SQLException
  {
    Object oResult = super.getObject(columnIndex);
    if (columnIndex == _iDataType)
    {
      int iResult = super.getInt(columnIndex); // maps null to 0
      oResult = getDataType(
        iResult,
        super.getString(_iTypeName));
    }
    else if (columnIndex == _iPrecision)
    {
      int iResult = super.getInt(columnIndex); // maps null to 0
      oResult = getPrecision(
        iResult,
        super.getInt(_iDataType),
        super.getString(_iTypeName));
    }
    else if (columnIndex == _iScale)
    {
      int iResult = super.getInt(columnIndex); // maps null to 0
      oResult = getScale(
        iResult,
        super.getInt(_iDataType),
        super.getString(_iTypeName));
    }
    else if (columnIndex == _iLength)
    {
      int iResult = super.getInt(columnIndex); // maps null to 0
      oResult = getPrecision(
        iResult,
        super.getInt(_iDataType),
        super.getString(_iTypeName));
    }
    else if (columnIndex == _iNumPrecRadix)
    {
      int iResult = super.getInt(columnIndex);
      oResult = getNumPrecRadix(
        iResult,
        super.getInt(_iDataType),
        super.getString(_iTypeName));
    }
    else if (columnIndex == _iTypeName)
    {
      int iLength = super.getInt(_iPrecision);
      if (iLength <= 0)
        iLength = super.getInt(_iLength);
      oResult = getTypeName(
        (String)oResult,
        super.getInt(_iDataType));
    }
    return oResult;
  } /* getObject */

} /* PostgresMetaColumns */
