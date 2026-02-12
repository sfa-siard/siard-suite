package ch.admin.bar.siard2.db2;

import java.io.*;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;

@SuppressWarnings("deprecation")
public class TestDb2Database
{
  private static class ColumnDefinition extends TestColumnDefinition
  {
    @Override
    public String getValueLiteral()
    {
      String sValueLiteral = "NULL";
      if (_oValue != null)
      {
        if (_oValue instanceof byte[])
          sValueLiteral = "BINARY("+SqlLiterals.formatBytesLiteral((byte[])_oValue)+")";
        else if (_oValue instanceof Date)
          sValueLiteral = SqlLiterals.formatDateLiteral((Date)_oValue).substring("DATE".length());
        else if (_oValue instanceof Time)
          sValueLiteral = SqlLiterals.formatTimeLiteral((Time)_oValue).substring("TIME".length());
        else if (_oValue instanceof Timestamp)
          sValueLiteral = SqlLiterals.formatTimestampLiteral((Timestamp)_oValue).substring("TIMESTAMP".length());
        else if (_oValue instanceof List)
        {
          @SuppressWarnings("unchecked")
          List<ColumnDefinition> listCd = (List<ColumnDefinition>)_oValue;
          StringBuilder sbStructValue = new StringBuilder(getType()+"()");
          for (int iAttribute = 0; iAttribute < listCd.size(); iAttribute++)
          {
            ColumnDefinition tcd = listCd.get(iAttribute);
            sbStructValue.append(" ..");
            sbStructValue.append(tcd.getName());
            sbStructValue.append("(");
            sValueLiteral = tcd.getValueLiteral();
            if (sValueLiteral.length() < 1000)
              sbStructValue.append(tcd.getValueLiteral());
            else
              sbStructValue.append("?");
            sbStructValue.append(")");
          }
          sValueLiteral = sbStructValue.toString();
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
  
  public static final String _sTEST_SCHEMA = "TESTDB2SCHEMA";
  private static final String _sTEST_TABLE_SIMPLE = "TDB2SIMPLE";
  public static QualifiedId getQualifiedSimpleTable() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TABLE_SIMPLE); }
  private static final String _sTEST_TABLE_COMPLEX = "TDB2COMPLEX";
  public static QualifiedId getQualifiedComplexTable() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TABLE_COMPLEX); }
  private static final String _sTEST_TYPE_DISTINCT = "TDB2DISTINCT";
  public static QualifiedId getQualifiedDistinctType() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TYPE_DISTINCT); }
  private static final String _sTEST_TYPE_STRUCT = "TDB2STRUCT";
  public static QualifiedId getQualifiedStructType() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TYPE_STRUCT); }
  private Connection _conn = null;
  private String _sTestUser = null;

  // http://www.ibm.com/support/knowledgecenter/SSEPEK_10.0.0/intro/src/tpc/db2z_datatypes.html
  public static List<TestColumnDefinition> _listCdSimple = new ArrayList<TestColumnDefinition>();
  static
  {
    _listCdSimple.add(new ColumnDefinition("CCHARACTER_5","CHARACTER(5)","Abcd"));
    _listCdSimple.add(new ColumnDefinition("CVARCHAR_255","VARCHAR(255)",TestUtils.getString(196)));
    _listCdSimple.add(new ColumnDefinition("CVARCHAR_255_MIXED","VARCHAR(255) FOR MIXED DATA",TestUtils.getString(196)));
    // _listCd.add(new ColumnDefinition("CLONGVARCHAR_4000","LONG VARCHAR(4000)",TestUtils.getString(2048)));
    _listCdSimple.add(new ColumnDefinition("CCLOB_2M","CLOB(2097152)",TestUtils.getString(2000000)));
    _listCdSimple.add(new ColumnDefinition("CGRAPHIC_5","GRAPHIC(5)","Äbcd"));
    _listCdSimple.add(new ColumnDefinition("CVARGRAPHIC_127","VARGRAPHIC(127)",TestUtils.getNString(92)));
    // _listCd.add(new ColumnDefinition("CLONGVARGRAPHIC_2000","LONG VARGRAPHIC(2000)",TestUtils.getNString(1024)));
    _listCdSimple.add(new ColumnDefinition("CDBCLOB_1M","DBCLOB",TestUtils.getNString(1000000)));
    _listCdSimple.add(new ColumnDefinition("CCHARACTER_BIT","CHARACTER FOR BIT DATA",new Byte((byte)27)));
    _listCdSimple.add(new ColumnDefinition("CBINARY_5","BINARY(5)",new byte[] {1,-2,3,-4} ));
    _listCdSimple.add(new ColumnDefinition("CVARBINARY_255","VARBINARY(255)",TestUtils.getBytes(196) ));
    // _listCd.add(new ColumnDefinition("CLONGVARCHARFORBITDATA_4000","LONG VARCHAR(4000) FOR BIT DATA",TestUtils.getBytes(1024) ));
    _listCdSimple.add(new ColumnDefinition("CBLOB_2M","BLOB(2097152)",TestUtils.getBytes(2000000)));
    _listCdSimple.add(new ColumnDefinition("CSMALLINT","SMALLINT",new Short((short)-32000)));
    _listCdSimple.add(new ColumnDefinition("CINTEGER","INTEGER",new Integer(1234567890)));
    _listCdSimple.add(new ColumnDefinition("CBIGINT","BIGINT",new Long(-123456789012345678l)));
    _listCdSimple.add(new ColumnDefinition("CDECIMAL_10_5","DECIMAL(10,5)",new BigDecimal(BigInteger.valueOf(3141592653l),5)));
    _listCdSimple.add(new ColumnDefinition("CNUMERIC_31","NUMERIC(31)",BigInteger.valueOf(1234567890123456789l)));
    _listCdSimple.add(new ColumnDefinition("CDECFLOAT","DECFLOAT",new Double(Math.E*1000*1000*1000*1000*1000*1000*1000*1000*1000*1000)));
    _listCdSimple.add(new ColumnDefinition("CREAL","REAL",new Float(Math.PI)));
    _listCdSimple.add(new ColumnDefinition("CDOUBLE","DOUBLE",new Double(Math.E)));
    _listCdSimple.add(new ColumnDefinition("CDATE","DATE",new Date(2016-1900,11,07)));
    _listCdSimple.add(new ColumnDefinition("CTIME","TIME",new Time(13,45,28)));
    _listCdSimple.add(new ColumnDefinition("CTIMESTAMP","TIMESTAMP(9)",new Timestamp(2016-1900,11,07,13,45,28,123456789)));
    _listCdSimple.add(new ColumnDefinition("CXML","XML","<a>foöäpwkfèégopàèwerkgvoperkv &lt; and &amp; ifjeifj</a>"));
  }

  public static String _sDISTINCT_BASE = "GRAPHIC(10)";
  
  public static List<TestColumnDefinition> _listAdStruct = new ArrayList<TestColumnDefinition>();
  static
  {
    _listAdStruct.add(new ColumnDefinition("AINTEGER","INTEGER",new Integer(987654321)));
    _listAdStruct.add(new ColumnDefinition("ACHAR_5","CHAR(5)","abcd"));
    // N.B.: XML is not allowed as an attribute in DB/2!
    // _listStruct.add(new ColumnDefinition("AXML","XML","<b>some XML attribute</b>"));
    // TODO: the current db2 instance can handle only up to 255 characters for DBCLOB (the method) - even though the default should be 1M
    _listAdStruct.add(new ColumnDefinition("ADBCLOB_1M","DBCLOB",TestUtils.getNString(255)));
  }
  
  public static List<TestColumnDefinition> _listCdComplex = new ArrayList<TestColumnDefinition>();
  static
  {
    _listCdComplex.add(new ColumnDefinition("CID","INTEGER",new Integer(987654321)));
    _listCdComplex.add(new ColumnDefinition("CDISTINCT",getQualifiedDistinctType().format(),"NIÑO"));
    _listCdComplex.add(new ColumnDefinition("CSTRUCT",getQualifiedStructType().format(),_listAdStruct));
  }
  
  public TestDb2Database(Connection conn, String sTestUser)
    throws SQLException
  {
    _conn = conn.unwrap(Connection.class);
    _conn.setAutoCommit(false);
    _sTestUser = sTestUser;
    drop();
    create();
  }
  
  private void drop()
  {
    dropTables();
    dropTypes();
    dropSchema();
  } /* drop */
  
  private void dropTypes()
  {
    dropType(getQualifiedDistinctType());
    dropType(getQualifiedStructType());
  } /* dropTypes */
  
  private void dropType(QualifiedId qiType)
  {
    String sSql = "DROP TYPE "+qiType.format();
    try
    {
      Statement stmt = _conn.createStatement();
      stmt.executeUpdate(sSql);
      stmt.close();
      _conn.commit();
    }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* dropType */
  
  private void create()
    throws SQLException
  {
    createSchema();
    createTypes();
    createTables();
    insertTables();
  } /* create */
  
  private void dropTables()
  {
    dropTable(getQualifiedSimpleTable());
    dropTable(getQualifiedComplexTable());
  } /* dropTables */
  
  private void dropTable(QualifiedId qiTable)
  {
    String sSql = "DROP TABLE "+qiTable.format();
    try
    {
      Statement stmt = _conn.createStatement();
      stmt.executeUpdate(sSql);
      stmt.close();
      _conn.commit();
    }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* dropTable */
  
  private void dropSchema()
  {
    String sSql = "DROP SCHEMA "+SqlLiterals.formatId(_sTEST_SCHEMA)+" RESTRICT";
    try
    {
      Statement stmt = _conn.createStatement();
      stmt.executeUpdate(sSql);
      stmt.close();
      _conn.commit();
    }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* dropSchema */
  
  private void createSchema()
    throws SQLException
  {
    String sSql = "CREATE SCHEMA "+SqlLiterals.formatId(_sTEST_SCHEMA)+" AUTHORIZATION "+SqlLiterals.formatId(_sTestUser);
    Statement stmt = _conn.createStatement();
    stmt.executeUpdate(sSql);
    stmt.close();
    _conn.commit();
  } /* createSchema */
  
  private void createTypes()
    throws SQLException
  {
    createDistinctType();
    createStructType();
  } /* createTypes */
  
  private void createDistinctType()
    throws SQLException
  {
    String sSql = "CREATE TYPE "+getQualifiedDistinctType().format()+" AS "+_sDISTINCT_BASE;
    Statement stmt = _conn.createStatement();
    stmt.executeUpdate(sSql);
    stmt.close();
    _conn.commit();
  } /* createDistinctType */
  
  private void createStructType()
    throws SQLException
  {
    StringBuilder sbSql = new StringBuilder("CREATE TYPE ");
    sbSql.append(getQualifiedStructType().format());
    sbSql.append(" AS (\r\n  ");
    for (int iAttribute = 0; iAttribute < _listAdStruct.size(); iAttribute++)
    {
      if (iAttribute > 0)
        sbSql.append(",\r\n  ");
      TestColumnDefinition cd = _listAdStruct.get(iAttribute);
      sbSql.append(cd.getName());
      sbSql.append(" ");
      sbSql.append(cd.getType());
    }
    sbSql.append("\r\n) MODE DB2SQL");
    Statement stmt = _conn.createStatement();
    stmt.executeUpdate(sbSql.toString());
    stmt.close();
    _conn.commit();
  } /* createStructType */
  
  private void createTables()
    throws SQLException
  {
    createSimpleTable();
    createComplexTable();
  } /* createTables */
  
  private void createSimpleTable()
    throws SQLException
  {
    StringBuilder sbSql = new StringBuilder("CREATE TABLE ");
    sbSql.append(getQualifiedSimpleTable().format());
    sbSql.append("\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < _listCdSimple.size(); iColumn++)
    {
      TestColumnDefinition cd = _listCdSimple.get(iColumn); 
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      sbSql.append(cd.getName());
      sbSql.append(" ");
      sbSql.append(cd.getType());
    }
    sbSql.append("\r\n) CCSID UNICODE");
    Statement stmt = _conn.createStatement();
    stmt.executeUpdate(sbSql.toString());
    String sSql = "GRANT ALL ON "+getQualifiedSimpleTable().format()+" TO USER "+_sTestUser;
    stmt.executeUpdate(sSql);
    stmt.close();
    _conn.commit();
  } /* createSimpleTable */
  
  private void createComplexTable()
    throws SQLException
  {
    StringBuilder sbSql = new StringBuilder("CREATE TABLE ");
    sbSql.append(getQualifiedComplexTable().format());
    sbSql.append("\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < _listCdComplex.size(); iColumn++)
    {
      TestColumnDefinition cd = _listCdComplex.get(iColumn); 
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      sbSql.append(cd.getName());
      sbSql.append(" ");
      sbSql.append(cd.getType());
    }
    sbSql.append("\r\n) CCSID UNICODE");
    Statement stmt = _conn.createStatement();
    stmt.executeUpdate(sbSql.toString());
    String sSql = "GRANT ALL ON "+getQualifiedComplexTable().format()+" TO USER "+_sTestUser;
    stmt.executeUpdate(sSql);
    stmt.close();
    _conn.commit();
  } /* createComplexTable */
  
  private void insertTables()
    throws SQLException
  {
    insertSimpleTable();
    insertComplexTable();
  } /* insertTables */
  
  private void insertSimpleTable()
    throws SQLException
  {
    StringBuilder sbSql = new StringBuilder("INSERT INTO ");
    sbSql.append(getQualifiedSimpleTable().format());
    sbSql.append("\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < _listCdSimple.size(); iColumn++)
    {
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      sbSql.append(_listCdSimple.get(iColumn).getName());
    }
    sbSql.append("\r\n)\r\nVALUES\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < _listCdSimple.size(); iColumn++)
    {
      TestColumnDefinition cd = _listCdSimple.get(iColumn); 
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      String sLiteral = cd.getValueLiteral();
      if (sLiteral.length() < 1000)
        sbSql.append(sLiteral);
      else
      {
        sbSql.append("?");
        /** 4: CCLOB_2M, 7: CDBCLOB_1M, 11: CBLOB_2M 
        System.out.println("Column "+String.valueOf(iColumn+1)+" "+tcd.getName()+" to be replaced.");
        **/
      }
    }
    sbSql.append("\r\n)");
    PreparedStatement pstmt = _conn.prepareStatement(sbSql.toString());
    Reader rdrClob = new StringReader((String)_listCdSimple.get(3).getValue());
    pstmt.setCharacterStream(1, rdrClob);
    Reader rdrDbClob = new StringReader((String)_listCdSimple.get(6).getValue());
    pstmt.setCharacterStream(2, rdrDbClob);
    InputStream isBlob = new ByteArrayInputStream((byte[])_listCdSimple.get(10).getValue());
    pstmt.setBinaryStream(3,isBlob);
    pstmt.executeUpdate();
    pstmt.close();
    _conn.commit();
  } /* insertSimpleTable */
  
  private void insertComplexTable()
    throws SQLException
  {
    StringBuilder sbSql = new StringBuilder("INSERT INTO ");
    sbSql.append(getQualifiedComplexTable().format());
    sbSql.append("\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < _listCdComplex.size(); iColumn++)
    {
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      sbSql.append(_listCdComplex.get(iColumn).getName());
    }
    sbSql.append("\r\n)\r\nVALUES\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < _listCdComplex.size(); iColumn++)
    {
      TestColumnDefinition cd = _listCdComplex.get(iColumn); 
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      String sLiteral = cd.getValueLiteral();
      if (sLiteral.length() < 1000)
        sbSql.append(sLiteral);
      else
      {
        // TODO: changed adblob to only 255 length value, see lines 119 and 391
        sbSql.append("?");
        System.out.println("Column "+String.valueOf(iColumn+1)+" "+cd.getName()+" to be replaced.");
      }
    }
    sbSql.append("\r\n)");
    PreparedStatement pstmt = _conn.prepareStatement(sbSql.toString());
    Reader rdrClob = new StringReader((String)_listAdStruct.get(2).getValue());
    // TODO: don't set parameter because of shorter value for adbclob_1m (only 255 characters) - see todo above
    //pstmt.setCharacterStream(1, rdrClob);
    pstmt.executeUpdate();
    pstmt.close();
    _conn.commit();
  } /* insertComplexTable */
  
} /* TestDb2Database */
