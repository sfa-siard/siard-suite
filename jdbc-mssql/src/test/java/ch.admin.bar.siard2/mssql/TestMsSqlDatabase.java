package ch.admin.bar.siard2.mssql;

import java.io.*;
import java.math.*;
import java.nio.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import static org.junit.Assert.*;
import microsoft.sql.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.jdbc.*;

public class TestMsSqlDatabase
{
  public static final String _sTEST_SCHEMA = "TESTMSSQLSCHEMA";
  private static final String _sTEST_TABLE_SIMPLE = "TMSSQLSIMPLE";
  public static QualifiedId getQualifiedSimpleTable() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TABLE_SIMPLE); }
  private static final String _sTEST_TABLE_COMPLEX = "TMSSQLCOMPLEX";
  public static QualifiedId getQualifiedComplexTable() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TABLE_COMPLEX); }
  private static final String _sTEST_TYPE_DISTINCT = "TMSSQLDIST";
  public static QualifiedId getQualifiedDistinctType() { return new QualifiedId(null,_sTEST_SCHEMA, _sTEST_TYPE_DISTINCT); }
  
  /* this just converts the UUID into a big-endian byte buffer */
  public static byte[] convertUuidToByteArray(UUID uuid)
  {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    byte[] buf = bb.array();
    // System.out.println(BU.toHex(buf));
    return buf;
  }
  /* this converts the big-endian byte buffer into a MS byte buffer,
   * taking the first 4 bytes as a little-endian integer,
   * the next 2 bytes as a little-endian word,
   * the next 2 bytes as a little-endian word,
   * and the final 8 bytes into a straight (big endian) byte buffer. 
   */
  public static UUID convertByteArrayToUuid(byte[] buf)
  {
    byte[] b = new byte[16];
    for (int i = 0; i < 4; i++)
      b[i] = buf[3-i];
    for (int i = 0; i < 2; i++)
      b[4+i] = buf[5-i];
    for (int i = 0; i < 2; i++)
      b[6+i] = buf[7-i];
    for (int i = 0; i < 8; i++)
      b[8+i] = buf[8+i];
    ByteBuffer bb = ByteBuffer.wrap(b);
    UUID uuid = new UUID(bb.getLong(),bb.getLong());
    return uuid;
  }
  private static class ColumnDefinition extends TestColumnDefinition
  {
    @Override
    public String getValueLiteral()
    {
      String sValueLiteral = "NULL";
      if (_oValue != null)
      {
        if (_oValue instanceof byte[])
          sValueLiteral = MsSqlLiterals.formatBytesLiteral((byte[])_oValue);
        else if (_oValue instanceof UUID)
          sValueLiteral = MsSqlLiterals.formatBytesLiteral(convertUuidToByteArray((UUID)_oValue));
        else if (_oValue instanceof String)
          sValueLiteral = MsSqlLiterals.formatStringLiteral((String)_oValue);
        else if (_oValue instanceof Boolean)
          sValueLiteral = String.valueOf(((Boolean)_oValue)?1:0);
        else if (_oValue instanceof BigDecimal)
          sValueLiteral = MsSqlLiterals.formatExactLiteral((BigDecimal)_oValue);
        else if (_oValue instanceof Double)
          sValueLiteral = MsSqlLiterals.formatApproximateLiteral(((Double)_oValue).doubleValue());
        else if (_oValue instanceof Date)
          sValueLiteral = MsSqlLiterals.formatDateLiteral((Date)_oValue);
        else if (_oValue instanceof Time)
          sValueLiteral = MsSqlLiterals.formatTimeLiteral((Time)_oValue);
        else if (_oValue instanceof Timestamp)
          sValueLiteral = MsSqlLiterals.formatTimestampLiteral((Timestamp)_oValue);
        else if (_oValue instanceof DateTimeOffset)
          sValueLiteral = MsSqlLiterals.formatStringLiteral(String.valueOf((DateTimeOffset)_oValue));
        else if (_oValue instanceof List<?>)
        {
          @SuppressWarnings("unchecked")
          List<TestColumnDefinition> listBase = (List<TestColumnDefinition>)_oValue;
          // only distinct types with single element in list
          sValueLiteral = listBase.get(0).getValueLiteral();
        }
        else
          sValueLiteral = super.getValueLiteral();
      }
      return sValueLiteral;
    }
    public ColumnDefinition(String sName, String sType, Object oValue)
    {
      super(sName,sType,oValue);
    }
  } /* ColumnDefinition */
  
  /* Output of DatabaseMetaData.getTypeInfo: 
TYPE_NAME DATA_TYPE PRECISION LITERAL_PREFIX  LITERAL_SUFFIX  CREATE_PARAMS NULLABLE  CASE_SENSITIVE  SEARCHABLE  UNSIGNED_ATTRIBUTE  FIXED_PREC_SCALE  AUTO_INCREMENT  LOCAL_TYPE_NAME MINIMUM_SCALE MAXIMUM_SCALE SQL_DATA_TYPE SQL_DATETIME_SUB  NUM_PREC_RADIX  INTERVAL_PRECISION  USERTYPE
ok datetimeoffset  -155  34  ' ' scale 1 0 3 null  0 null  datetimeoffset  0 7 -155  0 null  null  0
ok time  92  16  ' ' scale 1 0 3 null  0 null  time  0 7 -154  0 null  null  0
ok xml -16 0 N'  ' null  1 1 0 null  0 null  xml null  null  -152  null  null  null  0
ok sql_variant -150  8000  null  null  null  1 0 2 null  0 null  sql_variant 0 0 -150  null  10  null  0
ok uniqueidentifier  1 36  ' ' null  1 0 2 null  0 null  uniqueidentifier  null  null  -11 null  null  null  0
ok ntext -16 1073741823  N'  ' null  1 0 1 null  0 null  ntext null  null  -10 null  null  null  0
ok nvarchar  -9  4000  N'  ' max length  1 0 3 null  0 null  nvarchar  null  null  -9  null  null  null  0
ok sysname -9  128 N'  ' null  0 0 3 null  0 null  sysname null  null  -9  null  null  null  18
ok nchar -15 4000  N'  ' length  1 0 3 null  0 null  nchar null  null  -8  null  null  null  0
ok bit -7  1 null  null  null  1 0 2 null  0 null  bit 0 0 -7  null  null  null  16
ok tinyint -6  3 null  null  null  1 0 2 1 0 0 tinyint 0 0 -6  null  10  null  5
ok tinyint identity  -6  3 null  null  null  0 0 2 1 0 1 tinyint identity  0 0 -6  null  10  null  5
ok bigint  -5  19  null  null  null  1 0 2 0 0 0 bigint  0 0 -5  null  10  null  0
ok bigint identity -5  19  null  null  null  0 0 2 0 0 1 bigint identity 0 0 -5  null  10  null  0
ok image -4  2147483647  0x  null  null  1 0 0 null  0 null  image null  null  -4  null  null  null  20
ok varbinary -3  8000  0x  null  max length  1 0 2 null  0 null  varbinary null  null  -3  null  null  null  4
ok binary  -2  8000  0x  null  length  1 0 2 null  0 null  binary  null  null  -2  null  null  null  3
ok timestamp -2  8 0x  null  null  0 0 2 null  0 null  timestamp null  null  -2  null  null  null  80
ok text  -1  2147483647  ' ' null  1 0 1 null  0 null  text  null  null  -1  null  null  null  19
ok char  1 8000  ' ' length  1 0 3 null  0 null  char  null  null  1 null  null  null  1
ok numeric 2 38  null  null  precision,scale 1 0 2 0 0 0 numeric 0 38  2 null  10  null  10
ok numeric() identity  2 38  null  null  precision 0 0 2 0 0 1 numeric() identity  0 0 2 null  10  null  10
ok decimal 3 38  null  null  precision,scale 1 0 2 0 0 0 decimal 0 38  3 null  10  null  24
ok money 3 19  $ null  null  1 0 2 0 1 0 money 4 4 3 null  10  null  11
ok smallmoney  3 10  $ null  null  1 0 2 0 1 0 smallmoney  4 4 3 null  10  null  21
ok decimal() identity  3 38  null  null  precision 0 0 2 0 0 1 decimal() identity  0 0 3 null  10  null  24
ok int 4 10  null  null  null  1 0 2 0 0 0 int 0 0 4 null  10  null  7
ok int identity  4 10  null  null  null  0 0 2 0 0 1 int identity  0 0 4 null  10  null  7
ok smallint  5 5 null  null  null  1 0 2 0 0 0 smallint  0 0 5 null  10  null  6
ok smallint identity 5 5 null  null  null  0 0 2 0 0 1 smallint identity 0 0 5 null  10  null  6
ok float 8 53  null  null  null  1 0 2 0 0 0 float null  null  6 null  2 null  8
ok real  7 24  null  null  null  1 0 2 0 0 0 real  null  null  7 null  2 null  23
ok varchar 12  8000  ' ' max length  1 0 3 null  0 null  varchar null  null  12  null  null  null  2
ok date  91  10  ' ' null  1 0 3 null  0 null  date  null  0 9 1 null  null  0
ok datetime2 93  27  ' ' scale 1 0 3 null  0 null  datetime2 0 7 9 3 null  null  0
ok datetime  93  23  ' ' null  1 0 3 null  0 null  datetime  3 3 9 3 null  null  12
ok smalldatetime 93  16  ' ' null  1 0 3 null  0 null  smalldatetime 0 0 9 3 null  null  22
See also: https://msdn.microsoft.com/en-us/library/ms187752.aspx
  */
  public static int _iPrimarySimple = -1;
  public static int _iCandidateSimple = -1;
  @SuppressWarnings("deprecation")
  private static List<TestColumnDefinition> getCdSimple() 
  {
    List<TestColumnDefinition> listCdSimple = new ArrayList<TestColumnDefinition>();
    /* binary */
    listCdSimple.add(new ColumnDefinition("CBINARY_255","binary(255)",TestUtils.getBytes(50)));
    listCdSimple.add(new ColumnDefinition("CVARBINARY_1000","varbinary(1000)",TestUtils.getBytes(500)));
    listCdSimple.add(new ColumnDefinition("CVARBINARY_MAX","varbinary(max)",TestUtils.getBytes(50000)));
    listCdSimple.add(new ColumnDefinition("CIMAGE","image",TestUtils.getBytes(5000)));
    /* other */
    // synonym rowversion is preferred, is NOT an ISO TIMESTAMP!
    listCdSimple.add(new ColumnDefinition("CTIMESTAMP","timestamp",null));
    // see https://msdn.microsoft.com/en-us/library/bb677290.aspx
    listCdSimple.add(new ColumnDefinition("CHIERARCHYID","hierarchyid","/0.3.-7/0.1/"));
    // a 16-byte GUID
    listCdSimple.add(new ColumnDefinition("CUNIQUEIDENTIFIER","uniqueidentifier",UUID.randomUUID()));
    /*** https://msdn.microsoft.com/de-de/library/ms378878(v=sql.110).aspx:
     * Der SQL Server-Datentyp "sql_variant" wird vom JDBC-Treiber zurzeit nicht unterstützt. 
     * Wenn Daten mit einer Abfrage aus einer Tabelle mit einer Spalte abgerufen werden, 
     * die den sqlvariant-Datentyp enthält, wird eine Ausnahme ausgegeben.
     */
    /* character */
    listCdSimple.add(new ColumnDefinition("CCHAR_255","char(255)",TestUtils.getString(50)));
    listCdSimple.add(new ColumnDefinition("CVARCHAR_64","varchar(64)",TestUtils.getString(48)));
    listCdSimple.add(new ColumnDefinition("CTEXT","text",TestUtils.getString(5000)));
    listCdSimple.add(new ColumnDefinition("CNCHAR_255","nchar(255)",TestUtils.getString(50)));
    listCdSimple.add(new ColumnDefinition("CNVARCHAR_1000","nvarchar(1000)",TestUtils.getString(500)));
    listCdSimple.add(new ColumnDefinition("CNVARCHAR_MAX","nvarchar(max)",TestUtils.getString(50000)));
    listCdSimple.add(new ColumnDefinition("CNTEXT","ntext",TestUtils.getString(5000)));
    listCdSimple.add(new ColumnDefinition("CSYSNAME","sysname",TestUtils.getNString(31)));
    // see https://msdn.microsoft.com/en-us/library/ms187339.aspx
    listCdSimple.add(new ColumnDefinition("CXML","xml","<a>Ein schöööönes XML Fragment</a>"));
    /* exact (we do not need the IDENTITY types, which must be handled just like the base type) */
    listCdSimple.add(new ColumnDefinition("CBIT","bit",Boolean.TRUE));
    // MSSQL: TINYINT = unsigned byte value!
    listCdSimple.add(new ColumnDefinition("CTINYINT","tinyint",Short.valueOf((short)192)));
    _iCandidateSimple = listCdSimple.size(); // next column will be primary key column 
    listCdSimple.add(new ColumnDefinition("CSMALLINT","smallint",Short.valueOf((short)31000)));
    _iPrimarySimple = listCdSimple.size(); // next column will be primary key column 
    listCdSimple.add(new ColumnDefinition("CINT","int",Integer.valueOf(1000000)));
    listCdSimple.add(new ColumnDefinition("CBIGINT","bigint",Long.valueOf(8000000000l)));
    listCdSimple.add(new ColumnDefinition("CSMALLMONEY","smallmoney",BigDecimal.valueOf(1234567890l, 4)));
    listCdSimple.add(new ColumnDefinition("CMONEY","money",BigDecimal.valueOf(12345678901234l, 4)));
    listCdSimple.add(new ColumnDefinition("CDECIMAL_15_5","decimal(15,5)",BigDecimal.valueOf(314159l, 5)));
    listCdSimple.add(new ColumnDefinition("CNUMERIC_31","numeric(31)",BigDecimal.valueOf(31415901234l)));
    /* approximate */
    listCdSimple.add(new ColumnDefinition("CREAL","real",Float.valueOf(1.2345678901234E-17f)));
    listCdSimple.add(new ColumnDefinition("CFLOAT","float",Double.valueOf(1.23456789012345678901234567890E-17)));
    /* date and time */
    listCdSimple.add(new ColumnDefinition("CDATE","date",new Date(2016-1900,6,8)));
    listCdSimple.add(new ColumnDefinition("CTIME_2","time(2)",new Time((new Time(18,50,23)).getTime()+120)));
    // 1900-01-01 through 2079-06-06 without seconds
    listCdSimple.add(new ColumnDefinition("CSMALLDATETIME","smalldatetime",new Timestamp(2016-1900,6,24,15,27,0,0)));
    // January 1, 1753, through December 31, 9999
    listCdSimple.add(new ColumnDefinition("CDATETIME","datetime",new Timestamp(1820-1900,6,24,15,27,0,123000000)));
    // 0001-01-01 through 9999-12-31 and 00:00:00 through 23:59:59.9999999 with and 7 decimals for seconds
    listCdSimple.add(new ColumnDefinition("CDATETIME2_7","datetime2(7)",new Timestamp(820-1900,6,24,15,27,0,123456789)));
    // similar to datetime2 but with time zone and only 7 decimals for seconds
    listCdSimple.add(new ColumnDefinition("CDATETIMEOFFSET_5","datetimeoffset(5)",DateTimeOffset.valueOf(new Timestamp(1920-1900,6,24,15,27,0,123456789),120)));
    listCdSimple.add(new ColumnDefinition("CCOMPUTED_DATE","AS LEFT(CONVERT(VARCHAR, CDATETIME2_7, 120), 10)",null));
    return listCdSimple;
  }
  public static List<TestColumnDefinition> _listCdSimple = getCdSimple();

  private static List<TestColumnDefinition> getListBaseDistinct()
  {
    List<TestColumnDefinition> listBaseDistinct = new ArrayList<TestColumnDefinition>();
    listBaseDistinct.add(new ColumnDefinition(getQualifiedDistinctType().format(),"int",Integer.valueOf(999)));
    return listBaseDistinct;
  }
  public static List<TestColumnDefinition> _listBaseDistinct = getListBaseDistinct();
  
  private static List<TestColumnDefinition> getCdComplex() 
  {
    List<TestColumnDefinition> listCdComplex = new ArrayList<TestColumnDefinition>();
    /* spatial */
    listCdComplex.add(new ColumnDefinition("CGEOMETRY","geometry","POLYGON ((0 0, 150 0, 150 150, 0 150, 0 0))"));
    listCdComplex.add(new ColumnDefinition("CGEOGRAPHY","geography","LINESTRING (-122.36 47.656, -122.343 47.656)"));
    /* complex types */
    listCdComplex.add(new ColumnDefinition("CINT_DISTINCT",getQualifiedDistinctType().format(),_listBaseDistinct));
    return listCdComplex;
  }
  public static List<TestColumnDefinition> _listCdComplex = getCdComplex();
  
  private Connection _conn = null;
  
  public TestMsSqlDatabase(MsSqlConnection connMsSql)
    throws SQLException
  {
    _conn = connMsSql.unwrap(Connection.class);
    _conn.setAutoCommit(false);
    drop();
    create();
  } /* constructor */

  private void drop()
  {
    deleteTables();
    dropTables();
    dropTypes();
    dropSchema();
  } /* drop */
  
  private void executeDrop(String sSql)
  {
    try
    {
      Statement stmt = _conn.createStatement();
      stmt.executeUpdate(sSql);
      stmt.close();
      _conn.commit();
    }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* executeDrop */

  private void deleteTables()
  {
    deleteTable(getQualifiedSimpleTable());
    deleteTable(getQualifiedComplexTable());
  } /* deleteTables */
  
  private void deleteTable(QualifiedId qiTable)
  {
    executeDrop("DELETE FROM "+qiTable.format());
  } /* deleteTable */
  
  private void dropTables()
  {
    dropTable(getQualifiedSimpleTable());
    dropTable(getQualifiedComplexTable());
  } /* dropTables */
  
  private void dropTable(QualifiedId qiTable)
  {
    executeDrop("DROP TABLE "+qiTable.format());
  } /* dropTable */

  private void dropTypes()
  {
    dropType(getQualifiedDistinctType());
  } /* dropTypes */
  
  private void dropType(QualifiedId qiType)
  {
    executeDrop("DROP TYPE "+qiType.format()); 
  } /* dropType */
  
  private void dropSchema()
  {
    executeDrop("DROP SCHEMA "+SqlLiterals.formatId(_sTEST_SCHEMA));
  } /* dropSchema */

  private void create()
    throws SQLException
  {
    createSchema();
    createTypes();
    createTables();
    insertTables();
  } /* create */

  private void executeCreate(String sSql)
    throws SQLException
  {
    Statement stmt = _conn.createStatement();
    stmt.executeUpdate(sSql);
    stmt.close();
    _conn.commit();
  } /* executeCreate */

  private void createSchema()
    throws SQLException
  {
    SchemaId sid = new SchemaId(null,_sTEST_SCHEMA);
    executeCreate("CREATE SCHEMA "+sid.format());
  } /* createSchema */
  
  private void createTypes()
    throws SQLException
  {
    createType(getQualifiedDistinctType(),_listBaseDistinct);
  } /* createTypes */

  private void createType(QualifiedId qiType, List<TestColumnDefinition> listBase)
    throws SQLException
  {
    TestColumnDefinition tcd = listBase.get(0);
    executeCreate("CREATE TYPE "+qiType.format()+" FROM "+tcd.getType());
  } /* createType */

  private void createTables()
    throws SQLException
  {
    createTable(getQualifiedSimpleTable(),_listCdSimple,
      Arrays.asList(new String[] {_listCdSimple.get(_iPrimarySimple).getName()}),
      Arrays.asList(new String[] {_listCdSimple.get(_iCandidateSimple).getName()}));
    createTable(getQualifiedComplexTable(),_listCdComplex,null,null);
  } /* createTables */
  
  private void createTable(QualifiedId qiTable, List<TestColumnDefinition> listCd,
    List<String> listPrimary, List<String> listUnique)
    throws SQLException
  {
    StringBuilder sbSql = new StringBuilder("CREATE TABLE ");
    sbSql.append(qiTable.format());
    sbSql.append("\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      TestColumnDefinition tcd = listCd.get(iColumn); 
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      sbSql.append(tcd.getName());
      sbSql.append(" ");
      sbSql.append(tcd.getType());
    }
    if (listPrimary != null)
    {
      sbSql.append(",\r\n  CONSTRAINT PK");
      sbSql.append(qiTable.getName());
      sbSql.append(" PRIMARY KEY(");
      sbSql.append(SqlLiterals.formatIdentifierCommaList(listPrimary));
      sbSql.append(")");
    }
    if (listUnique != null)
    {
      sbSql.append(",\r\n  CONSTRAINT UK");
      sbSql.append(qiTable.getName());
      sbSql.append(" UNIQUE(");
      sbSql.append(SqlLiterals.formatIdentifierCommaList(listUnique));
      sbSql.append(")");
    }
    sbSql.append("\r\n)");
    executeCreate(sbSql.toString());
  } /* createTable */
  
  private void insertTables()
    throws SQLException
  {
    insertTable(getQualifiedSimpleTable(),_listCdSimple);
    insertTable(getQualifiedComplexTable(),_listCdComplex);
  } /* insertTables */
  
  private void insertTable(QualifiedId qiTable, List<TestColumnDefinition> listCd)
    throws SQLException
  {
    StringBuilder sbSql = new StringBuilder("INSERT INTO ");
    sbSql.append(qiTable.format());
    sbSql.append("\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      TestColumnDefinition tcd = listCd.get(iColumn); 
      if (tcd.getValue() != null)
      {
        if (iColumn > 0)
          sbSql.append(",\r\n  ");
        sbSql.append(tcd.getName());
      }
    }
    sbSql.append("\r\n)\r\nVALUES\r\n(\r\n  ");
    List<Object> listLobs = new ArrayList<Object>();
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      TestColumnDefinition tcd = listCd.get(iColumn);
      if (tcd.getValue() != null)
      {
        if (iColumn > 0)
          sbSql.append(",\r\n  ");
        String sLiteral = tcd.getValueLiteral();
        if (sLiteral.length() < 1000)
          sbSql.append(sLiteral);
        else
        {
          sbSql.append("?");
          listLobs.add(tcd.getValue());
        }
      }
    }
    sbSql.append("\r\n)");
    PreparedStatement pstmt = _conn.prepareStatement(sbSql.toString());
    for (int iLob = 0; iLob < listLobs.size(); iLob++)
    {
      Object o = listLobs.get(iLob);
      if (o instanceof String)
      {
        Reader rdrClob = new StringReader((String)o);
        pstmt.setCharacterStream(iLob+1, rdrClob);
        
      }
      else if (o instanceof byte[])
      {
        InputStream isBlob = new ByteArrayInputStream((byte[])o);
        pstmt.setBinaryStream(iLob+1, isBlob);
      }
      else
        throw new SQLException("Invalid LOB type "+o.getClass().getName()+"!");
    }
    int iResult = pstmt.executeUpdate();
    assertSame("Insert failed!",1,iResult);
    pstmt.close();
    _conn.commit();
  } /* insertTable */

} /* class TestMsSqlDatabase */
