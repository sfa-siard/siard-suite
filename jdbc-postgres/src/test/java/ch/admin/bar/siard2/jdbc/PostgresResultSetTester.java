package ch.admin.bar.siard2.jdbc;

import org.junit.*;
import org.json.simple.*;
import org.postgresql.*;
import org.postgresql.geometric.*;
import org.postgresql.largeobject.*;

import static org.junit.Assert.*;

import java.io.*;
import java.math.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;
import javax.xml.datatype.*;

import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.database.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.enums.PreType;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.postgres.*;
import ch.admin.bar.siard2.postgres.identifier.*;

@Ignore
public class PostgresResultSetTester
  extends BaseResultSetTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = PostgresDriver.getUrl(_cp.getHost()+":"+_cp.getPort()+"/"+_cp.getCatalog());
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();

  private static String getTableQuery(QualifiedId qiTable, List<TestColumnDefinition> listCd)
  {
    StringBuilder sbSql = new StringBuilder("SELECT\r\n  ");
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      TestColumnDefinition tcd = listCd.get(iColumn);
      sbSql.append(tcd.getName());
    }
    sbSql.append("\r\nFROM ");
    sbSql.append(qiTable.format());
    return sbSql.toString();
  } /* getTableQuery */
  
  private static String _sNativeQuerySimple = getTableQuery(TestPostgresDatabase.getQualifiedSimpleTable(),TestPostgresDatabase._listCdSimple);
  private static String _sNativeQueryComplex = getTableQuery(TestPostgresDatabase.getQualifiedComplexTable(),TestPostgresDatabase._listCdComplex);
  private static String _sSqlQuerySimple = getTableQuery(TestSqlDatabase.getQualifiedSimpleTable(),TestSqlDatabase._listCdSimple);
  private static String _sSqlQueryComplex = getTableQuery(TestSqlDatabase.getQualifiedComplexTable(),TestSqlDatabase._listCdComplex);

  @SuppressWarnings("deprecation")
  private static List<TestColumnDefinition> getListCdSimple()
  {
    List<TestColumnDefinition> listCdSimple = new ArrayList<TestColumnDefinition>();
    listCdSimple.add(new TestColumnDefinition("CCHAR_5","CHAR(5)","!"));
    listCdSimple.add(new TestColumnDefinition("CVARCHAR_255","VARCHAR(255)",TestUtils.getString(92)));
    listCdSimple.add(new TestColumnDefinition("CNULL","VARCHAR(255)",TestUtils.getString(5)));
    listCdSimple.add(new TestColumnDefinition("CCLOB_2M","CLOB(2M)",TestUtils.getString(1000000)));
    listCdSimple.add(new TestColumnDefinition("CNCHAR_5","NCHAR(5)","Auää"));
    listCdSimple.add(new TestColumnDefinition("CNVARCHAR_127","NCHAR VARYING(127)",TestUtils.getNString(53)));
    listCdSimple.add(new TestColumnDefinition("CNCLOB_1M","NCLOB(1M)",TestUtils.getNString(500000)));
    listCdSimple.add(new TestColumnDefinition("CXML","XML","<a>foöäpwkfèégopàèwerkgv fviodsjv jdsjd idsjidsjsiudojiou operkv &lt; and &amp; ifjeifj</a>"));
    listCdSimple.add(new TestColumnDefinition("CBINARY_5","BINARY(5)",new byte[] {5,-4,3,-2} ));
    listCdSimple.add(new TestColumnDefinition("CVARBINARY_255","VARBINARY(255)",TestUtils.getBytes(76) ));
    listCdSimple.add(new TestColumnDefinition("CBLOB","BLOB",TestUtils.getBytes(500000)));
    listCdSimple.add(new TestColumnDefinition("CNUMERIC_31","NUMERIC(31)",new BigDecimal(BigInteger.valueOf(987654321098765432l),0)));
    listCdSimple.add(new TestColumnDefinition("CDECIMAL_15_5","DECIMAL(15,5)",new BigDecimal(BigInteger.valueOf(9876543210987l),5)));
    listCdSimple.add(new TestColumnDefinition("CSMALLINT","SMALLINT",Short.valueOf((short)23000)));
    listCdSimple.add(new TestColumnDefinition("CSMALLINT_BYTE","SMALLINT",Short.valueOf((short)23)));
    listCdSimple.add(new TestColumnDefinition("CINTEGER","INTEGER",Integer.valueOf(987654321)));
    listCdSimple.add(new TestColumnDefinition("CBIGINT","BIGINT",Long.valueOf(-987654321098765432l)));
    listCdSimple.add(new TestColumnDefinition("CFLOAT_10","FLOAT(10)",Float.valueOf((float)Math.PI)));
    listCdSimple.add(new TestColumnDefinition("CREAL","REAL",Float.valueOf((float)Math.E)));
    listCdSimple.add(new TestColumnDefinition("CDOUBLE","DOUBLE PRECISION",Double.valueOf(Math.PI)));
    listCdSimple.add(new TestColumnDefinition("CBOOLEAN","BOOLEAN",Boolean.valueOf(false)));
    listCdSimple.add(new TestColumnDefinition("CDATE","DATE",new Date(2016-1900,12,2)));
    listCdSimple.add(new TestColumnDefinition("CTIME","TIME",new Time(14,24,12)));
    listCdSimple.add(new TestColumnDefinition("CTIMESTAMP","TIMESTAMP(9)",new Timestamp(2016-1900,12,2,14,24,12,987654321)));
    listCdSimple.add(new TestColumnDefinition("CINTERVAL_YEAR_3_MONTH","INTERVAL YEAR(3) TO MONTH",new Interval(1,3,6)));
    listCdSimple.add(new TestColumnDefinition("CINTERVAL_DAY_2_SECONDS_6","INTERVAL DAY(2) TO SECOND(6)",new Interval(1,0,17,54,23,123456000l)));
    listCdSimple.add(new TestColumnDefinition(TestSqlDatabase.COLUMN_DATALINK,"BLOB", TestSqlDatabase.getCircleJpgBytes()));
    return listCdSimple;
  }

  public static List<TestColumnDefinition> _listCdSimple = getListCdSimple();
  
  private static List<TestColumnDefinition> getListAdSimple()
  {
    List<TestColumnDefinition> listAdSimple = new ArrayList<TestColumnDefinition>();
    listAdSimple.add(new TestColumnDefinition("TABLEID","INTEGER",Integer.valueOf(2)));
    listAdSimple.add(new TestColumnDefinition("TRANSCRIPTION","CLOB",TestUtils.getString(500000)));
    listAdSimple.add(new TestColumnDefinition("SOUND","BLOB",TestUtils.getBytes(500000)));
    return listAdSimple;
  }
  public static List<TestColumnDefinition> _listAdSimple = getListAdSimple();
  
  private static List<TestColumnDefinition> getListAdComplex()
  {
    List<TestColumnDefinition> listAdComplex = new ArrayList<TestColumnDefinition>();
    listAdComplex.add(new TestColumnDefinition("ID","INTEGER",Integer.valueOf(2)));
    listAdComplex.add(new TestColumnDefinition("NESTED_ROW",TestSqlDatabase.getQualifiedSimpleType().format(),getListAdSimple()));
    return listAdComplex;
  }
  public static List<TestColumnDefinition> _listAdComplex = getListAdComplex();

  private static List<TestColumnDefinition> getListBaseDistinct()
  {
    List<TestColumnDefinition> listBaseDistinct = new ArrayList<TestColumnDefinition>();
    listBaseDistinct.add(new TestColumnDefinition(TestSqlDatabase.getQualifiedDistinctType().format(),"NCHAR VARYING(10)","AuwäähNiño"));
    return listBaseDistinct;
  }
  public static List<TestColumnDefinition> _listBaseDistinct = getListBaseDistinct();
  
  private static List<TestColumnDefinition> getListCdArray()
  {
    List<TestColumnDefinition> listCd = new ArrayList<TestColumnDefinition>();
    listCd.add(new TestColumnDefinition("CARRAY[1]","VARCHAR(255)",TestUtils.getString(94)));
    listCd.add(new TestColumnDefinition("CARRAY[2]","VARCHAR(255)",TestUtils.getString(125)));
    listCd.add(new TestColumnDefinition("CARRAY[3]","VARCHAR(255)",TestUtils.getString(32)));
    listCd.add(new TestColumnDefinition("CARRAY[4]","VARCHAR(255)",TestUtils.getString(4)));
    return listCd;
  }
  public static List<TestColumnDefinition> _listCdArray = getListCdArray();

  private static List<TestColumnDefinition> getListCdComplex()
  {
    List<TestColumnDefinition> listCdComplex = new ArrayList<TestColumnDefinition>();
    listCdComplex.add(new TestColumnDefinition("CID","INT",Integer.valueOf(987654321)));
    listCdComplex.add(new TestColumnDefinition("COMPLETE",TestSqlDatabase.getQualifiedAllType().format(),_listCdSimple));
    listCdComplex.add(new TestColumnDefinition("CUDT",TestSqlDatabase.getQualifiedComplexType().format(),_listAdComplex));
    listCdComplex.add(new TestColumnDefinition("CDISTINCT",TestSqlDatabase.getQualifiedDistinctType().format(),_listBaseDistinct));
    listCdComplex.add(new TestColumnDefinition("CARRAY","VARCHAR(255) ARRAY[1000]",_listCdArray));
    return listCdComplex;
  }
  public static List<TestColumnDefinition> _listCdComplex = getListCdComplex();
  
  private static final int iBUFSIZ = 8192;
  
  @BeforeClass
  public static void setUpClass() 
  {
    try 
    {
      PostgresDataSource dsPostgres = new PostgresDataSource();
      dsPostgres.setUrl(_sDB_URL);
      dsPostgres.setUser(_sDBA_USER);
      dsPostgres.setPassword(_sDBA_PASSWORD);
      PostgresConnection connPostgres = (PostgresConnection)dsPostgres.getConnection();
      /* drop and create the test databases */
      new TestSqlDatabase(connPostgres,_sDB_USER);
      TestPostgresDatabase.grantSchemaUser(connPostgres, TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
      new TestPostgresDatabase(connPostgres,_sDB_USER);
      TestPostgresDatabase.grantSchemaUser(connPostgres, TestPostgresDatabase._sTEST_SCHEMA, _sDB_USER);
      connPostgres.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

  private Connection _conn = null;
  
  private Connection closeResultSet()
    throws SQLException
  {
    ResultSet rs = getResultSet();
    if (rs != null)
    {
      if (!rs.isClosed())
      {
        Statement stmt = rs.getStatement();
        rs.close();
        setResultSet(null);
        if (!stmt.isClosed())
          stmt.close();
      }
    }
    _conn.commit();
    return _conn;
  } /* closeResultSet */
  
  private void openResultSet(String sQuery, int iType, int iConcurrency)
    throws SQLException
  {
    closeResultSet();
    int iHoldability = ResultSet.HOLD_CURSORS_OVER_COMMIT;
    Statement stmt = _conn.createStatement(iType, iConcurrency, iHoldability);
    ResultSet rs = stmt.executeQuery(sQuery);
    setResultSet(rs);
    rs.next();
  } /* openResultSet */
  
  @Before
  public void setUp() {
    try 
    {
      PostgresDataSource dsPostgres = new PostgresDataSource();
      dsPostgres.setUrl(_sDB_URL);
      dsPostgres.setUser(_sDB_USER);
      dsPostgres.setPassword(_sDB_PASSWORD);
      _conn = (PostgresConnection)dsPostgres.getConnection();
      _conn.setAutoCommit(false);
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
    }
    catch (SQLException se) { fail(se.getClass().getName() + ": " + se.getMessage()); }
  }

  private TestColumnDefinition findColumnDefinition(List<TestColumnDefinition> listCd, String sName)
  {
    TestColumnDefinition tcd = null;
    for (Iterator<TestColumnDefinition> iterCd = listCd.iterator(); iterCd.hasNext(); )
    {
      TestColumnDefinition tcdTry = iterCd.next();
      if (sName.equals(tcdTry.getName()))
        tcd = tcdTry;
    }
    return tcd;
  } /* findColumnDefinition */
  
  @Test
  public void testClass() 
  {
    enter();
    assertEquals("Wrong result set class!", PostgresResultSet.class, getResultSet().getClass());
  } /* testClass */

  @Test
  @Override
  public void testAbsolute()
  {
    enter();
    try 
    { 
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      getResultSet().absolute(1); 
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testAbsolute */
  
  @Test
  @Override
  public void testRelative()
  {
    enter();
    try 
    { 
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      getResultSet().relative(1);
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testRelative */
  
  @Test
  @Override
  public void testFirst()
  {
    enter();
    try 
    { 
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      getResultSet().first();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testFirst */
  
  @Test
  @Override
  public void testLast()
  {
    enter();
    try 
    { 
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      getResultSet().last();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testLast */
  
  @Test
  @Override
  public void testPrevious()
  {
    enter();
    try 
    {
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      getResultSet().next();
      getResultSet().previous();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testPrevious */
  
  @Test
  @Override
  public void testBeforeFirst()
  {
    enter();
    try 
    { 
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      getResultSet().beforeFirst(); 
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testBeforeFirst */
  
  @Test
  @Override
  public void testAfterLast()
  {
    enter();
    try 
    {
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      getResultSet().afterLast(); 
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testAfterLast */
  
  @Test
  public void testSetFetchDirection()
  {
    enter();
    try 
    { 
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      getResultSet().setFetchDirection(ResultSet.FETCH_UNKNOWN); 
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetFetchDirection */
  
  @Test
  @Override
  public void testFindColumn()
  {
    enter();
    try { getResultSet().findColumn("CCHAR_5"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testFindColumn */
  
  @Test
  @Override
  public void testUpdateNull()
  {
    enter();
    try { getResultSet().updateNull("CCHAR_5"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNull */
  
  @Test
  @Override
  public void testGetString()
  {
    enter();
    try 
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CCHAR_5");
      String s = getResultSet().getString(tcd.getName());
      String sEx = (String)tcd.getValue();
      sEx = sEx + SU.repeat(s.length()-sEx.length(), ' ');
      assertEquals("Invalid string!",sEx,s);
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetString */
  
  @Test
  @Override
  public void testUpdateString()
  {
    enter();
    try 
    { 
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateString */
  
  @Test
  @Override
  public void testGetNString()
  {
    enter();
    try 
    { 
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CNCHAR_5");
      String s = getResultSet().getNString(tcd.getName());
      assertEquals("Invalid national string!",(String)tcd.getValue(),s);
      
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetString */
  
  @Test
  @Override
  public void testUpdateNString()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CNVARCHAR_127");
      getResultSet().updateNString(tcd.getName(),(String)tcd.getValue());
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNString */
  
  @Test
  @Override
  public void testGetBoolean()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CBOOLEAN");
      boolean b = getResultSet().getBoolean(tcd.getName());
      assertEquals("Invalid boolean!",((Boolean)tcd.getValue()).booleanValue(),b);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBoolean */
  
  @Test
  @Override
  public void testUpdateBoolean()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CBOOLEAN");
      getResultSet().updateBoolean(tcd.getName(),((Boolean)tcd.getValue()).booleanValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBoolean */
  
  @Test
  @Override
  public void testGetByte()
  {
    enter();
    try 
    { 
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CSMALLINT_BYTE");
      byte by = getResultSet().getByte(tcd.getName());
      assertEquals("Invalid bytes!",((Short)tcd.getValue()).byteValue(),by);
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetByte */

  @Test
  @Override
  public void testUpdateByte()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CSMALLINT_BYTE");
      getResultSet().updateByte(tcd.getName(),((Short)tcd.getValue()).byteValue());
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateByte */
  
  @Test
  @Override
  public void testGetShort()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CSMALLINT");
      short sh = getResultSet().getShort(tcd.getName());
      assertEquals("Invalid short!",((Short)tcd.getValue()).shortValue(),sh);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetShort */
  
  @Test
  @Override
  public void testUpdateShort()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CSMALLINT");
      getResultSet().updateShort(tcd.getName(),((Short)tcd.getValue()).shortValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateShort */
  
  @Test
  @Override
  public void testGetInt()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CINTEGER");
      int i = getResultSet().getInt(tcd.getName());
      assertEquals("Invalid int!",((Integer)tcd.getValue()).intValue(),i);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetInt */
  
  @Test
  @Override
  public void testUpdateInt()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CINTEGER");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateInt */
  
  @Test
  @Override
  public void testGetLong()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CBIGINT");
      long l = getResultSet().getLong(tcd.getName());
      assertEquals("Invalid long!",((Long)tcd.getValue()).longValue(),l);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetLong */
  
  @Test
  @Override
  public void testUpdateLong()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CBIGINT");
      getResultSet().updateLong(tcd.getName(),((Long)tcd.getValue()).longValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateLong */
  
  @Test
  @Override
  public void testGetFloat()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CREAL");
      float f = getResultSet().getFloat(tcd.getName());
      assertEquals("Invalid float!",(Float)tcd.getValue(),Float.valueOf(f));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetFloat */
  
  @Test
  @Override
  public void testUpdateFloat()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CREAL");
      getResultSet().updateFloat(tcd.getName(),((Float)tcd.getValue()).floatValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateFloat */
  
  @Test
  @Override
  public void testGetDouble()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CDOUBLE");
      double d = getResultSet().getDouble(tcd.getName());
      assertEquals("Invalid double!",(Double)tcd.getValue(),Double.valueOf(d));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetDouble */
  
  @Test
  @Override
  public void testUpdateDouble()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CDOUBLE");
      getResultSet().updateDouble(tcd.getName(),((Double)tcd.getValue()).doubleValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateDouble */
  
  @Test
  @Override
  public void testGetBigDecimal()
  {
    enter();
    try
    {
      /**
      openResultSet(_sNativeQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      TestColumnDefinition tcd = findColumnDefinition(TestPostgresDatabase._listCdSimple,"CMONEY");
      BigDecimal bd = getResultSet().getBigDecimal(tcd.getName());
      assertTrue("Invalid BigDecimal!",bd.compareTo((BigDecimal)tcd.getValue()) == 0);
      **/
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CBIGINT");
      BigDecimal bd = getResultSet().getBigDecimal(tcd.getName());
      assertEquals("Invalid BigDecimal!",new BigDecimal(((Long)tcd.getValue()).longValue()),bd);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBigDecimal */
  
  @Test
  @Override
  public void testUpdateBigDecimal()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CDECIMAL_15_5");
      getResultSet().updateBigDecimal(tcd.getName(),(BigDecimal)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBigDecimal */
  
  @Test
  @Override
  @SuppressWarnings("deprecation")
  public void testGetBigDecimal_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CDECIMAL_15_5");
      BigDecimal bd = getResultSet().getBigDecimal(tcd.getName(),5);
      assertEquals("Invalid BigDecimal!",((BigDecimal)tcd.getValue()).setScale(5,RoundingMode.DOWN),bd);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBigDecimal_String_Int */
  
  @Test
  @Override
  public void testGetBytes()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CVARBINARY_255");
      byte[] buf = getResultSet().getBytes(tcd.getName());
      assertTrue("Invalid byte array!",Arrays.equals((byte[])tcd.getValue(),buf));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBytes */

  @Test
  @Override
  public void testUpdateBytes()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CBINARY_5");
      getResultSet().updateBytes(tcd.getName(),(byte[])tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBytes */
  
  @Test
  @Override
  public void testGetDate()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CDATE");
      Date date = getResultSet().getDate(tcd.getName());
      assertEquals("Invalid Date!",(Date)tcd.getValue(),date);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetDate */
  
  @Test
  @Override
  public void testUpdateDate()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CDATE");
      getResultSet().updateDate(tcd.getName(),(Date)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateDate */

  @Test
  @Override
  public void testGetDate_Calendar()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CDATE");
      Calendar cal = new GregorianCalendar();
      Date date = getResultSet().getDate(tcd.getName(),cal);
      assertEquals("Invalid Date!",(Date)tcd.getValue(),date);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetDate_Calendar */

  @Test
  @Override
  public void testGetTime()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CTIME");
      Time time = getResultSet().getTime(tcd.getName());
      assertEquals("Invalid Time!",(Time)tcd.getValue(),time);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTime */
  
  
  @Test
  @Override
  public void testUpdateTime()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CTIME");
      getResultSet().updateTime(tcd.getName(),(Time)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateTime */
  
  @Test
  @Override
  public void testGetTime_Calendar()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CTIME");
      Calendar cal = new GregorianCalendar();
      Time time = getResultSet().getTime(tcd.getName(),cal);
      assertEquals("Invalid Time!",(Time)tcd.getValue(),time);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTime_Calendar */
  
  private Timestamp truncateToMicros(Timestamp ts)
  {
    int iNanos = ts.getNanos();
    ts.setNanos(1000*((iNanos+499)/1000));
    return ts;
  } /* truncateToMicros */
  
  @Test
  @Override
  public void testGetTimestamp()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CTIMESTAMP");
      Timestamp ts = getResultSet().getTimestamp(tcd.getName());
      Timestamp tsEx = (Timestamp)tcd.getValue();
      assertEquals("Invalid Timestamp!",truncateToMicros(tsEx),ts);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTimestamp */

  @Test
  @Override
  public void testUpdateTimestamp()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CTIMESTAMP");
      getResultSet().updateTimestamp(tcd.getName(),(Timestamp)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateTimestamp */

  @Test
  @Override
  public void testGetTimestamp_Calendar()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CTIMESTAMP");
      Calendar cal = new GregorianCalendar();
      Timestamp ts = getResultSet().getTimestamp(tcd.getName(),cal);
      assertEquals("Invalid Timestamp!",(Timestamp)tcd.getValue(),ts);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTimestamp_Calendar */
  
  @Test
  @Override
  public void testGetDuration()
  {
    enter();
    try 
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CINTERVAL_YEAR_3_MONTH");
      Duration duration = getBaseResultSet().getDuration(tcd.getName());
      assertEquals("Invalid Duration!",(Interval)tcd.getValue(),Interval.fromDuration(duration));
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  @Override
  public void testUpdateDuration()
  {
    enter();
    try 
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CINTERVAL_DAY_2_SECONDS_6");
      getBaseResultSet().updateDuration(tcd.getName(), ((Interval)tcd.getValue()).toDuration());
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateDuration */
  
  @Test
  @Override
  public void testGetAsciiStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CVARCHAR_255");
      InputStream is = getResultSet().getAsciiStream(tcd.getName());
      byte[] buf = new byte[((String)tcd.getValue()).length()];
      is.read(buf);
      if (is.read() != -1)
        fail("Invalid length of ASCII stream!");
      is.close();
      String s = new String(buf);
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetAsciiStream */
  
  @Test
  @Override
  public void testUpdateAsciiStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      InputStream is = new ByteArrayInputStream(((String)tcd.getValue()).getBytes());
      getResultSet().updateAsciiStream(tcd.getName(),is);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateAsciiStream */
  
  @Test
  @Override
  public void testUpdateAsciiStream_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      InputStream is = new ByteArrayInputStream(((String)tcd.getValue()).getBytes());
      getResultSet().updateAsciiStream(tcd.getName(),is,((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateAsciiStream_Int */
  
  @Test
  @Override
  public void testUpdateAsciiStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      InputStream is = new ByteArrayInputStream(((String)tcd.getValue()).getBytes());
      getResultSet().updateAsciiStream(tcd.getName(),is,(long)((String)tcd.getValue()).length());
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateAsciiStream_Long */
  
  @Test
  @Override
  @SuppressWarnings("deprecation")
  public void testGetUnicodeStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CNVARCHAR_127");
      Reader rdr = new InputStreamReader(getResultSet().getUnicodeStream(tcd.getName()),"UTF-8");
      char[] cbuf = new char[((String)tcd.getValue()).length()];
      int iLength = 0;
      for (int iRead = rdr.read(cbuf); (iRead != -1) && (iLength < cbuf.length); iRead = rdr.read(cbuf,iLength,cbuf.length-iLength))
        iLength = iLength + iRead;
      rdr.close();
      assertEquals("Invalid length of character stream!",cbuf.length,iLength);
      String s = new String(cbuf);
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetUnicodeStream */
  
  @Test
  @Override
  public void testGetCharacterStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CVARCHAR_255");
      Reader rdr = getResultSet().getCharacterStream(tcd.getName());
      char[] cbuf = new char[((String)tcd.getValue()).length()];
      rdr.read(cbuf);
      if (rdr.read() != -1)
        fail("Invalid length of character stream!");
      rdr.close();
      String s = new String(cbuf);
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetCharacterStream */
  
  @Test
  @Override
  public void testUpdateCharacterStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr);
    }
    catch(SQLFeatureNotSupportedException se) { System.out.println(EU.getExceptionMessage(se)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateCharacterStream */
  
  @Test
  @Override
  public void testUpdateCharacterStream_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr,((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateCharacterStream_Int */
  
  @Test
  @Override
  public void testUpdateCharacterStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARCHAR_255");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr,(long)((String)tcd.getValue()).length());
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateCharacterStream_Long */
  
  @Test
  @Override
  public void testGetNCharacterStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CNVARCHAR_127");
      Reader rdr = getResultSet().getNCharacterStream(tcd.getName());
      char[] cbuf = new char[((String)tcd.getValue()).length()];
      rdr.read(cbuf);
      if (rdr.read() != -1)
        fail("Invalid length of character stream!");
      rdr.close();
      String s = new String(cbuf);
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetNCharacterStream */
  
  @Test
  @Override
  public void testUpdateNCharacterStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CNVARCHAR_127");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNCharacterStream */
  
  @Test
  @Override
  public void testUpdateNCharacterStream_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CNVARCHAR_127");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr,((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNCharacterStream_String_Int */
  
  @Test
  @Override
  public void testUpdateNCharacterStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CNVARCHAR_127");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr,(long)((String)tcd.getValue()).length());
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNCharacterStream_String_Long */

  @Test
  @Override
  public void testGetBinaryStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CVARBINARY_255");
      InputStream is = getResultSet().getBinaryStream(tcd.getName());
      byte[] buf = new byte[((byte[])tcd.getValue()).length];
      is.read(buf);
      if (is.read() != -1)
        fail("Invalid length of binary stream!");
      is.close();
      assertTrue("Invalid byte array!",Arrays.equals((byte[])tcd.getValue(),buf));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetBinaryStream */
  
  @Test
  @Override
  public void testUpdateBinaryStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CVARBINARY_255");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBinaryStream(tcd.getName(),is);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBinaryStream */
  
  @Test
  @Override
  public void testUpdateBinaryStream_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CVARBINARY_255");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBinaryStream(tcd.getName(),is,((byte[])tcd.getValue()).length);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBinaryStream_Int */
  
  @Test
  @Override
  public void testUpdateBinaryStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CVARBINARY_255");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBinaryStream(tcd.getName(),is,(long)((byte[])tcd.getValue()).length);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBinaryStream_Long */
  
  @Test
  @Override
  public void testGetObject()
  {
    enter();
    try
    {
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CBIGINT");
      Object o = getResultSet().getObject(tcd.getName());
      assertEquals("Invalid BIGINT!",(Long)tcd.getValue(),(Long)o);
      /**
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdComplex,"CDISTINCT");
      @SuppressWarnings("unchecked")
      List<TestColumnDefinition> list = (List<TestColumnDefinition>)tcd.getValue();
      TestColumnDefinition tcdValue = list.get(0);
      Object o = getResultSet().getObject(tcd.getName());
      assertEquals("Invalid DISTINCT!",(String)tcdValue.getValue(),(String)o);
      **/
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObject */

  @Test
  @Override
  public void testUpdateObject()
  {
    enter();
    try
    {
      /*
      openResultSet(_sNativeQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      TestColumnDefinition tcd = findColumnDefinition(TestPostgresDatabase._listCdComplex,"CENUM_SUIT");
      getResultSet().updateObject(tcd.getName(),"diamonds");
      */
      /*
      openResultSet(_sSqlQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdComplex,"CDISTINCT");
      getResultSet().updateObject(tcd.getName(),"hello");
      */
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CBIGINT");
      getResultSet().updateObject(tcd.getName(),BigDecimal.valueOf(5l));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateObject */
  
  @Test
  @Override
  public void testUpdateObject_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CDECIMAL_15_5");
      getResultSet().updateObject(tcd.getName(),tcd.getValue(),3);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateObject_Int */
  
  @Test
  @Override
  public void testGetObject_Map()
  {
    enter();
    try
    {
      openResultSet(_sSqlQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdComplex,"CUDT");
      Map<String,Class<?>> map = new HashMap<String,Class<?>>();
      map.put(tcd.getType(), tcd.getValue().getClass());
      Object o = getResultSet().getObject(tcd.getName(),map);
      String sDataType = String.valueOf(Types.STRUCT)+" ("+SqlTypes.getTypeName(Types.STRUCT)+")";
      checkObject("  ",o,tcd,Types.STRUCT,tcd.getType(),sDataType);
    }
    catch(SQLFeatureNotSupportedException snse) { System.out.println("testGetObject_Map: "+EU.getExceptionMessage(snse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObject_Map */
  
  @Test
  @Override
  public void testGetObject_Class()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CDATE");
      Date date = getResultSet().getObject(tcd.getName(),Date.class);
      assertEquals("Invalid Date!",(Date)tcd.getValue(),date);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObject_Class */
  
  @Test
  @Override
  public void testGetRef()
  {
    enter();
  } /* testGetRef */
  
  @Test
  @Override
  public void testUpdateRef()
  {
    enter();
  } /* testUpdateRef */
  
  @Test
  @Override
  public void testGetBlob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CBLOB");
      Blob blob = getResultSet().getBlob(tcd.getName());
      byte[] buffer = blob.getBytes(1l, (int)blob.length());
      assertTrue("Invalid Blob!",Arrays.equals((byte[])tcd.getValue(),buffer));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    // catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetBlob */
  
  @Test
  @Override
  public void testUpdateBlob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CBLOB");
      Blob blob = getResultSet().getStatement().getConnection().createBlob();
      blob.setBytes(1, (byte[])tcd.getValue());
      getResultSet().updateBlob(tcd.getName(),blob);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBlob */

  @Test
  @Override
  public void testUpdateBlob_InputStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CBLOB");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBlob(tcd.getName(),is);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBlob_InputStream */
  
  @Override
  @Test
  public void testUpdateBlob_InputStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CBLOB");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBlob(tcd.getName(),is,((byte[])tcd.getValue()).length);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBlob_String_InputStream_Long */
  
  @Test
  @Override
  public void testGetClob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CCLOB_2M");
      Clob clob = getResultSet().getClob(tcd.getName());
      StringWriter sw = new StringWriter();
      Reader rdr = clob.getCharacterStream();
      char[] cbuf = new char[iBUFSIZ];
      for (int iRead = rdr.read(cbuf); iRead != -1; iRead = rdr.read(cbuf))
        sw.write(cbuf,0,iRead);
      rdr.close();
      sw.close();
      String s = sw.toString();
      String sEx = (String)tcd.getValue();
      assertEquals("Invalid Clob!",sEx,s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetClob */
  
  @Test
  @Override
  public void testUpdateClob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CCLOB_2M");
      Clob clob = getResultSet().getStatement().getConnection().createClob();
      clob.setString(1l,(String)tcd.getValue());
      getResultSet().updateClob(tcd.getName(),clob);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateClob */

  @Test
  @Override
  public void testUpdateClob_Reader()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CCLOB_2M");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateClob(tcd.getName(),rdr);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateClob_Reader */
  
  @Test
  @Override
  public void testUpdateClob_Reader_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CCLOB_2M");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateClob(tcd.getName(),rdr,((String)tcd.getValue()).length());
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateClob_Reader_Long */
  
  @Test
  @Override
  public void testGetNClob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CNCLOB_1M");
      NClob nclob = getResultSet().getNClob(tcd.getName());
      String s = nclob.getSubString(1l,(int)nclob.length());
      assertEquals("Invalid NClob!",(String)tcd.getValue(),s);
    }
    catch(SQLFeatureNotSupportedException fnse) { System.out.println("testNClob: "+EU.getExceptionMessage(fnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetNClob */
  
  @Test
  @Override
  public void testUpdateNClob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CNCLOB_1M");
      NClob nclob = getResultSet().getStatement().getConnection().createNClob();
      nclob.setString(1l, (String)tcd.getValue());
      getResultSet().updateNClob(tcd.getName(),nclob);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNClob */
  
  @Test
  @Override
  public void testUpdateNClob_Reader()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CNCLOB_1M");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateNClob(tcd.getName(),rdr);
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNClob_Reader */
  
  @Test
  @Override
  public void testUpdateNClob_Reader_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition( _listCdSimple,"CNCLOB_1M");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateNClob(tcd.getName(),rdr,((String)tcd.getValue()).length());
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNClob_Reader_Long */
  
  @Test
  @Override
  public void testGetSqlXml()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple,"CXML");
      SQLXML sqlxml = getResultSet().getSQLXML(tcd.getName());
      String s = sqlxml.getString().replaceAll("\\n\\s*","");
      assertEquals("Invalid SQLXML!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetSqlXml */
  
  @Test
  @Override
  public void testUpdateSqlXml()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CXML");
      SQLXML sqlxml = getResultSet().getStatement().getConnection().createSQLXML();
      sqlxml.setString((String)tcd.getValue());
      getResultSet().updateSQLXML(tcd.getName(),sqlxml);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateSqlXml */
  
  @Test
  @Override
  public void testGetArray()
  {
    enter();
    try 
    { 
      openResultSet(_sSqlQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdComplex,"CARRAY");
      Array array = getResultSet().getArray(tcd.getName());
      if (array != null)
        array.free();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetArray */
  
  @Test
  @Override
  public void testUpdateArray()
  {
    enter();
    try 
    { 
      openResultSet(_sSqlQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdComplex,"CARRAY");
      Array array = getResultSet().getArray(tcd.getName());
      getResultSet().updateArray(tcd.getName(),array);
      array.free();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateArray */
  
  @Test
  @Override
  public void testGetRowId()
  {
    enter();
    try { getResultSet().getRowId("CCHAR_5"); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetRowId */
  
  @Test
  @Override
  public void testUpdateRowId()
  {
    enter();
    try 
    { 
      RowId rowid = getResultSet().getRowId(1);
      getResultSet().updateRowId("CCHAR_5",rowid); 
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateRowId */


  @Override
  @Test
  public void testGetUrl() throws MalformedURLException, SQLException {
//    enter();
//
//    // given
//    openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
//    TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, TestSqlDatabase.COLUMN_DATALINK);
//
//    // when
//    PostgresResultSet rs = (PostgresResultSet) getResultSet();
//    URL url = rs.getURL(tcd.getName());
//
//    // then
//    assertEquals(new URL((String) tcd.getValue()), url);
  }

  @Test
  public void testUpdateUrl() throws MalformedURLException, SQLException {
//    enter();
//
//    // given
//    openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
//    TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, TestSqlDatabase.COLUMN_DATALINK);
//
//    // when
//    PostgresResultSet rs = (PostgresResultSet) getResultSet();
//    URL url = rs.updateURL(tcd.getName(), new URL((String) tcd.getValue()));
//
//    // then
//    assertEquals(new URL((String) tcd.getValue()), url);
  }
  
  @Test
  @Override
  public void testInsertRow() throws SQLException
  {
    enter();
    try 
    {
      openResultSet(_sNativeQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      getResultSet().moveToInsertRow();
      TestColumnDefinition tcd = findColumnDefinition(
        TestPostgresDatabase._listCdComplex,"CID");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue()+1);
      tcd = findColumnDefinition(
        TestPostgresDatabase._listCdComplex,"CENUM_SUIT");
      getResultSet().updateObject(tcd.getName(),"spades");
      getResultSet().insertRow();
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testInsertRow */
  
  @Test
  @Override
  public void testDeleteRow() throws SQLException
  {
    enter();
    try 
    {
      // cannot delete row in simple table because of foreign key
      openResultSet(_sSqlQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      getResultSet().deleteRow();
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(Exception e) { fail(EU.getExceptionMessage(e)); }
  } /* testDeleteRow */
   
  @Test
  @Override
  public void testUpdateRow() throws SQLException
  {
    enter();
    try 
    { 
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CXML");
      SQLXML sqlxml = getResultSet().getStatement().getConnection().createSQLXML();
      sqlxml.setString("<a>Konrad Zuse</a>");
      getResultSet().updateSQLXML(tcd.getName(), sqlxml);
      getResultSet().updateRow(); 
      sqlxml.free();
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(Exception e) { fail(EU.getExceptionMessage(e)); }
  } /* testUpdateRow */

  private void checkTsVector(String s, String sExpected)
  {
    String[] asTokens = sExpected.split(" ");
    Arrays.sort(asTokens);
    StringBuilder sbExpected = new StringBuilder();
    String sToken = null;
    for (int iToken = 0; iToken < asTokens.length; iToken++)
    {
      if (!asTokens[iToken].equals(sToken))
      {
        sToken = asTokens[iToken];
        if (sbExpected.length() > 0)
          sbExpected.append(" ");
        sbExpected.append("'");
        sbExpected.append(sToken);
        sbExpected.append("'");
      }
    }
    assertEquals("Invalid value for "+PostgresType.TSVECTOR.getKeyword()+"!",sbExpected.toString(),s);
  } /* checkTsVector */
  
  private void checkTsQuery(String s, String sExpected)
  {
    String[] asTokens = sExpected.split(" ");
    StringBuilder sbExpected = new StringBuilder();
    for (int iToken = 0; iToken < asTokens.length; iToken++)
    {
      if (sbExpected.length() > 0)
        sbExpected.append(" ");
      String sToken = asTokens[iToken];
      if (sToken.equals("&") || (sToken.equals("|") || (sToken.equals("!"))))
        sbExpected.append(sToken);
      else
      {
        String sWeight = null;
        int iWeight = sToken.indexOf(":");
        if (iWeight > 0)
        {
          sWeight = sToken.substring(iWeight+1).toUpperCase();
          sToken = sToken.substring(0,iWeight);
        }
        sbExpected.append("'");
        sbExpected.append(sToken);
        sbExpected.append("'");
        if (sWeight != null)
        {
          sbExpected.append(":");
          sbExpected.append(sWeight);
        }
      }
    }
    assertEquals("Invalid value for "+PostgresType.TSQUERY.getKeyword()+"!",sbExpected.toString(),s);
  } /* checkTsQuery */
  
  private void checkPoint(String s, String sExpected)
    throws SQLException
  {
    PGpoint pp = new PGpoint(s);
    PGpoint ppExpected = new PGpoint(sExpected);
    assertEquals("Invalid value for "+PostgresType.POINT.getKeyword()+"!",ppExpected,pp);
  } /* checkPoint */
  
  private void checkLine(String s, String sExpected)
    throws SQLException
  {
    PGline pl = new PGline(s);
    PGline plExpected = new PGline(sExpected);
    assertEquals("Invalid value for "+PostgresType.LINE.getKeyword()+"!",plExpected,pl);
  } /* checkLine */
  
  private void checkLseg(String s, String sExpected)
    throws SQLException
  {
    PGlseg pl = new PGlseg(s);
    PGlseg plExpected = new PGlseg(sExpected);
    assertEquals("Invalid value for "+PostgresType.LSEG.getKeyword()+"!",plExpected,pl);
  } /* checkLseg */
  
  private void checkBox(String s, String sExpected)
    throws SQLException
  {
    PGbox pb = new PGbox(s);
    PGbox pbExpected = new PGbox(sExpected.substring(1,sExpected.length()-1));
    assertEquals("Invalid value for "+PostgresType.BOX.getKeyword()+"!",pbExpected,pb);
  } /* checkBox */
  
  private void checkPath(String s, String sExpected)
    throws SQLException
  {
    PGpath pp = new PGpath(s);
    PGpath ppExpected = new PGpath(sExpected);
    assertEquals("Invalid value for "+PostgresType.PATH.getKeyword()+"!",ppExpected,pp);
  } /* checkPath */
  
  private void checkPolygon(String s, String sExpected)
    throws SQLException
  {
    PGpolygon pp = new PGpolygon(s);
    PGpolygon ppExpected = new PGpolygon(sExpected);
    assertEquals("Invalid value for "+PostgresType.POLYGON.getKeyword()+"!",ppExpected,pp);
  } /* checkPolygon */
  
  private void checkCircle(String s, String sExpected)
    throws SQLException
  {
    PGcircle pc = new PGcircle(s);
    PGcircle pcExpected = new PGcircle(sExpected);
    assertEquals("Invalid value for "+PostgresType.CIRCLE.getKeyword()+"!",pcExpected,pc);
  } /* checkPolygon */
  
  private void checkString(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof String)
    {
      String s = (String)o;
      String sExpected = (String)tcd.getValue();
      PostgresType pt = PostgresType.getByKeyword(sTypeName);
      if ((pt == PostgresType.JSONB) ||
        (pt == PostgresType.JSON))
      {
        Object oj  = JSONValue.parse(s);
        Object ojExpected = JSONValue.parse(sExpected);
        assertEquals("Invalid value for "+tcd.getType()+"!",ojExpected,oj);
      }
      else if (pt == PostgresType.TSVECTOR)
        checkTsVector(s, sExpected);
      else if (pt == PostgresType.TSQUERY)
        checkTsQuery(s, sExpected);
      else if (pt == PostgresType.POINT)
        checkPoint(s, sExpected);
      else if (pt == PostgresType.LINE)
        checkLine(s, sExpected);
      else if (pt == PostgresType.LSEG)
        checkLseg(s, sExpected);
      else if (pt == PostgresType.BOX)
        checkBox(s, sExpected);
      else if (pt == PostgresType.PATH)
        checkPath(s, sExpected);
      else if (pt == PostgresType.POLYGON)
        checkPolygon(s, sExpected);
      else if (pt == PostgresType.CIRCLE)
        checkCircle(s, sExpected);
      else
      {
        s = s.substring(0,((String)tcd.getValue()).length());
        if (!s.equals(sExpected))
        {
          System.out.println("expected: "+sExpected);
          System.out.println("found: "+s);
        }
        assertEquals("Invalid value for "+sTypeName+"!",sExpected,s);
      }
    }
    else
      fail("Type String expected for "+sDataType+"!");
  } /* checkString */
  
  private void checkClob(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Clob)
    {
      Clob clob = (Clob)o;
      assertEquals("Invalid value for "+sTypeName+"!",tcd.getValue(),clob.getSubString(1l,(int)clob.length()));
    }
    else
      fail("Type Clob expected for "+sDataType+"!");
  } /* checkClob */
  
  private void checkSqlXml(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof SQLXML)
    {
      SQLXML sqlxml = (SQLXML)o;
      assertEquals("Invalid value for "+sTypeName+"!",(String)tcd.getValue(),sqlxml.getString());
    }
    else
      fail("Type SQLXML expected for "+sDataType+"!");
  } /* checkSqlXml */
  
  private void checkBytes(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof byte[])
    {
      byte[] buf = (byte[])o;
      byte[] bufExpected = null;
      if (tcd.getValue() instanceof UUID)
      {
        UUID uuidExpected = (UUID)tcd.getValue();
        bufExpected = PostgresLiterals.convertUuidToByteArray(uuidExpected);
      }
      else if (tcd.getValue() instanceof String)
      {
        String sExpected = (String)tcd.getValue();
        PostgresType pt = PostgresType.getByKeyword(sTypeName);
        if ((pt == PostgresType.BIT) ||
          (pt  == PostgresType.VARBIT))
          bufExpected = PostgresLiterals.parseBitString(sExpected);
        else if ((pt == PostgresType.MACADDR) ||
          (pt == PostgresType.MACADDR8))
          bufExpected = PostgresLiterals.parseMacAddr(sExpected);
      }
      else
        bufExpected = (byte[])tcd.getValue();
      assertTrue("Invalid value for "+sTypeName+"!",Arrays.equals(bufExpected, buf));
    }
    else
      fail("Type byte[] expected for "+sDataType+"!");
  } /* checkBytes */
  
  private void checkBlob(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if ( o instanceof Blob)
    {
      Blob blob = (Blob)o;
      assertTrue("Invalid value for "+sTypeName+"!",Arrays.equals((byte[])tcd.getValue(),blob.getBytes(1l,(int)blob.length())));
    }
    else
      fail("Type Blob expected for "+"!");
  } /* checkBlob */
  
  private void checkBigDecimal(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof BigDecimal)
    {
      BigDecimal bd = (BigDecimal)o;
      BigDecimal bdExpected = null;
      Object oExpected = tcd.getValue();
      if (oExpected instanceof BigInteger)
      {
        BigInteger biExpected = (BigInteger)oExpected;
        bdExpected = new BigDecimal(biExpected);
      }
      else
        bdExpected = (BigDecimal)oExpected;
      assertEquals("Invalid value for "+sTypeName+"!",bdExpected,bd);
    }
    else
      fail("Type BigDecimal expected for "+"!");
  } /* checkBigDecimal */
  
  private void checkShort(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Short)
    {
      Short sh = (Short)o;
      assertEquals("Invalid value for "+sTypeName+"!",(Short)tcd.getValue(),sh);
    }
    else
      fail("Type Short expected for "+sDataType+"!");
  } /* checkShort */
  
  private void checkInteger(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Integer)
    {
      Integer i = (Integer)o;
      assertEquals("Invalid value for "+sTypeName+"!",(Integer)tcd.getValue(),i);
    }
    else
      fail("Type Integer expected for "+sDataType+"!");
  } /* checkInteger */

  private void checkOid(long l, byte[] bufExpected)
    throws SQLException
  {
    PGConnection pgconn = (PGConnection)getResultSet().getStatement().getConnection().unwrap(Connection.class);
    LargeObjectManager lobj = pgconn.getLargeObjectAPI();
    LargeObject lo = lobj.open(l);
    byte[] buf = new byte[lo.size()];
    lo.read(buf, 0, buf.length);
    lo.close();
    assertTrue("Invalid value for "+PostgresType.OID.getKeyword()+"!",Arrays.equals(bufExpected, buf));
  } /* checkOid */
  
  private void checkLong(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Long)
    {
      Long l = (Long)o;
      if (sTypeName.equals(PostgresType.OID.getKeyword()))
        checkOid(l,(byte[])tcd.getValue());
      else
        assertEquals("Invalid value for "+sTypeName+"!",(Long)tcd.getValue(),l);
    }
    else
      fail("Type Long expected for "+sDataType+"!");
  } /* checkLong */
  
  private void checkDouble(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Double)
    {
      Double d = (Double)o;
      Double dExpected = null;
      if (tcd.getValue() instanceof Float)
      {
        Float f = (Float)tcd.getValue();
        // truncate double from database
        d = Double.valueOf((double)d.floatValue());
        // expand float
        dExpected = Double.valueOf(f.doubleValue());
      }
      else
        dExpected = (Double)o;
      assertEquals("Invalid value for "+sTypeName+"!",dExpected,d);
    }
    else
      fail("Type Double expected for "+sDataType+"!");
  } /* checkDouble */
  
  private void checkFloat(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Float)
    {
      Float f = (Float)o;
      assertEquals("Invalid value for "+sTypeName+"!",(Float)tcd.getValue(),f);
    }
    else
      fail("Type Float expected for "+sDataType+"!");
  } /* checkFloat */
  
  private void checkBoolean(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Boolean)
    {
      Boolean b = (Boolean)o;
      assertEquals("Invalid value for "+sTypeName+"!",(Boolean)tcd.getValue(),b);
    }
    else
      fail("Type Boolean expected for "+sDataType+"!");
  } /* checkBoolean */
  
  private void checkDate(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Date)
    {
      Date d = (Date)o;
      assertEquals("Invalid value for "+sTypeName+"!",(Date)tcd.getValue(),d);
    }
    else
      fail("Type Date expected for "+sDataType+"!");
  } /* checkDate */
  
  private void checkTime(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Time)
    {
      Time t = (Time)o;
      assertEquals("Invalid value for "+sTypeName+"!",(Time)tcd.getValue(),t);
    }
    else
      fail("Type Time expected for "+sDataType+"!");
  } /* checkTime */
  
  private void checkTimestamp(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Timestamp)
    {
      Timestamp ts = (Timestamp)o;
      Timestamp tsExpected = (Timestamp)tcd.getValue();
      tsExpected = truncateToMicros(tsExpected);
      assertEquals("Invalid value for "+sTypeName+"!",tsExpected,ts);
    }
    else
      fail("Type Timestamp expected for "+sDataType+"!");
  } /* checkTimestamp */
  
  private void checkDuration(Object o, Object oExpected)
  {
    Duration d = (Duration)o;
    Interval iv = Interval.fromDuration(d);
    Interval ivExpected = (Interval)oExpected;
    long lNanoSeconds = ivExpected.getNanoSeconds();
    lNanoSeconds = (lNanoSeconds+500000)/1000000;
    lNanoSeconds = 1000000*lNanoSeconds;
    ivExpected.setNanoSeconds(lNanoSeconds);
    assertEquals("Invalid value for "+PostgresType.INTERVAL.getKeyword()+"!",ivExpected,iv);
  } /* checkDuration */
  
  private void checkDuration(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Duration)
      checkDuration(o,tcd.getValue());
    else
      fail("Type Duration expected for "+sTypeName+"!");
  } /* checkDuration */
  
  private void checkStruct(String sIndent, Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    try
    {
      if (o instanceof Struct)
      {
        Struct struct = (Struct)o;
        if (tcd.getValue() instanceof List<?>)
        {
          @SuppressWarnings("unchecked")
          List<TestColumnDefinition> listAttributes = (List<TestColumnDefinition>)tcd.getValue();
          // sTypeName holds name of type
          PostgresQualifiedId qiType = new PostgresQualifiedId(sTypeName);
          DatabaseMetaData dmd = getResultSet().getStatement().getConnection().getMetaData();
          ResultSet rsAttribute = dmd.getAttributes(
            qiType.getCatalog(), 
            qiType.getSchema(),
            qiType.getName(),
            "%");
          for (int iAttribute = 0; iAttribute < listAttributes.size(); iAttribute++)
          {
            TestColumnDefinition tcdAttribute = listAttributes.get(iAttribute);
            if (rsAttribute.next())
            {
              String sAttributeName = rsAttribute.getString("ATTR_NAME");
              int iDataType = rsAttribute.getInt("DATA_TYPE");
              String sAttributeDataType = String.valueOf(iDataType)+" ("+SqlTypes.getTypeName(iDataType)+")";
              String sAttributeTypeName = rsAttribute.getString("ATTR_TYPE_NAME");
              System.out.println(sIndent + sAttributeName + ": " +sAttributeDataType+" "+sAttributeTypeName);
              if (tcdAttribute.getName().toLowerCase().equals(sAttributeName))
              {
                Object oAttribute = struct.getAttributes()[iAttribute];
                checkObject(sIndent + "  ", oAttribute, tcdAttribute, iDataType, sAttributeTypeName, sAttributeDataType);
              }
              else
                fail("Invalid attribute found: "+tcdAttribute.getName());
            }
            else
              fail("Invalid attribute found: "+tcd.getName());
          }
          if (rsAttribute.next())
            fail("Too many attribute meta data found!");
          rsAttribute.close();
        }
        else
          fail("Value of UDT must be a list!");
      }
      else
        fail("Type Struct expected for "+sTypeName+"!");
    }
    catch(ParseException pe) { fail("Type name "+sTypeName+" could not be parsed!"); }
  } /* checkStruct */

  private List<TestColumnDefinition> flattenElements(List<TestColumnDefinition> listElements)
  {
    List<TestColumnDefinition> listFlat = new ArrayList<TestColumnDefinition>();
    for (int iElement = 0; iElement < listElements.size(); iElement++)
    {
      TestColumnDefinition tcd = listElements.get(iElement);
      if (tcd.getValue() instanceof List<?>)
      {
        @SuppressWarnings("unchecked")
        List<TestColumnDefinition> list = (List<TestColumnDefinition>)tcd.getValue();
        listFlat.addAll(flattenElements(list));
      }
      else
        listFlat.add(tcd);
    }
    return listFlat;
  } /* flattenElements */
  
  private void checkArray(String sIndent, Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
    if (o instanceof Array)
    {
      Array array = (Array)o;
      int iDataType = array.getBaseType();
      String sElementDataType = String.valueOf(iDataType)+" ("+SqlTypes.getTypeName(iDataType)+")";
      String sElementTypeName = array.getBaseTypeName();
      if (tcd.getValue() instanceof List<?>)
      {
        @SuppressWarnings("unchecked")
        List<TestColumnDefinition> listElements = (List<TestColumnDefinition>)tcd.getValue();
        listElements = flattenElements(listElements);
        Object[] ao = (Object[])array.getArray();
        if (ao.length == listElements.size())
        {
          for (int iElement = 0; iElement < listElements.size(); iElement++)
          {
            System.out.println(sIndent + tcd.getName() + "["+String.valueOf(iElement)+"]: " +sElementDataType+" "+sElementTypeName);
            TestColumnDefinition tcdElement = listElements.get(iElement);
            Object oElement = ao[iElement];
            checkObject(sIndent + "  ", oElement, tcdElement, iDataType, sElementTypeName, sElementDataType);
          }
        }
        else
          fail("Unexpected number of elements!");
      }
      else
        fail("Value of Array must be a list!");
    }
    else
      fail("Type Array expected for "+sTypeName+"!");
  } /* checkArray */
  
  private void checkDistinct(Object o, TestColumnDefinition tcd, String sTypeName, String sDataType)
    throws SQLException
  {
      if (tcd.getValue() instanceof List<?>)
      {
        @SuppressWarnings("unchecked")
        List<TestColumnDefinition> listElements = (List<TestColumnDefinition>)tcd.getValue();
        if (listElements.size() == 1)
        {
          tcd = listElements.get(0);
          assertEquals("Invalid value for "+sTypeName+"!",tcd.getValue(),o);
        }
        else
          fail("List with 1 element expected for DISTINCT values!");
      }
      else
        fail("List expected for DISTINCT values!");
    //}
    //catch(ParseException pe) { fail("Type name "+sTypeName+" could not be parsed!"); }
  } /* checkDistinct */

  private void checkObject(String sIndent, Object o, TestColumnDefinition tcd, int iDataType, String sTypeName, String sDataType)
    throws SQLException
  {
    if (tcd.getValue() != null)
    {
      switch(iDataType)
      {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.NCHAR:
        case Types.NVARCHAR:
          checkString(o,tcd,sTypeName,sDataType); break;
        case Types.CLOB:
        case Types.NCLOB:
          checkClob(o,tcd,sTypeName,sDataType); break;
        case Types.SQLXML: checkSqlXml(o,tcd,sTypeName,sDataType); break;
        case Types.BINARY:
        case Types.VARBINARY:
          checkBytes(o,tcd,sTypeName,sDataType); break;
        case Types.BLOB:
        case Types.DATALINK:
          checkBlob(o,tcd,sTypeName,sDataType); break;
        case Types.NUMERIC:
        case Types.DECIMAL:
          checkBigDecimal(o,tcd,sTypeName,sDataType); break;
        case Types.SMALLINT: checkShort(o,tcd,sTypeName,sDataType); break;
        case Types.INTEGER: checkInteger(o,tcd,sTypeName,sDataType); break;
        case Types.BIGINT: checkLong(o,tcd,sTypeName,sDataType); break;
        case Types.DOUBLE: checkDouble(o,tcd,sTypeName,sDataType); break;
        case Types.REAL: checkFloat(o,tcd,sTypeName,sDataType); break;
        case Types.BOOLEAN: checkBoolean(o,tcd,sTypeName,sDataType); break;
        case Types.DATE: checkDate(o,tcd,sTypeName,sDataType); break;
        case Types.TIME: checkTime(o,tcd,sTypeName,sDataType); break;
        case Types.TIMESTAMP: checkTimestamp(o,tcd,sTypeName,sDataType); break;
        case Types.OTHER: checkDuration(o,tcd,sTypeName,sDataType); break;
        case Types.STRUCT: checkStruct(sIndent, o,tcd,sTypeName,sDataType); break;
        case Types.DISTINCT: checkDistinct(o, tcd, sTypeName, sDataType); break;
        case Types.ARRAY: checkArray(sIndent, o,tcd,sTypeName,sDataType); break;
        default: fail("Invalid data type found: "+sDataType+"!");
      }
    }
    else if (o != null)
      fail("Expected NULL value not found!");
  } /* checkObject */

  @Test
  public void testGetObjectSqlSimple() throws SQLException {
    openResultSet(_sSqlQuerySimple, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    DatabaseMetaData dmd = getResultSet().getStatement().getConnection().getMetaData();
    ResultSet rsColumn = dmd.getColumns(null,
            TestSqlDatabase._sTEST_SCHEMA.toLowerCase(),
            TestSqlDatabase._sTEST_TABLE_SIMPLE.toLowerCase(),
            "%");
    for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++) {
      TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
      if (rsColumn.next()) {
        String sColumnName = rsColumn.getString("COLUMN_NAME");
        int iDataType = rsColumn.getInt("DATA_TYPE");
        String sDataType = String.valueOf(iDataType) + " (" + SqlTypes.getTypeName(iDataType) + ")";
        String sTypeName = rsColumn.getString("TYPE_NAME");
        System.out.println(sColumnName + ": " + sDataType + " " + sTypeName);
        if (tcd.getName().toLowerCase().equals(sColumnName)) {
          Object o = getResultSet().getObject(sColumnName);
          checkObject("  ", o, tcd, iDataType, sTypeName, sDataType);
        } else
          fail("Invalid column found: " + tcd.getName());
      } else
        fail("Column meta data not found!");
    }
    if (rsColumn.next())
      fail("Too many column meta data found!");
    rsColumn.close();
  }
    
  @Test
  public void testGetObjectNativeSimple()
  {
    try
    {
      openResultSet(_sNativeQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      DatabaseMetaData dmd = getResultSet().getStatement().getConnection().getMetaData();
      ResultSet rsColumn = dmd.getColumns(null, 
        TestPostgresDatabase._sTEST_SCHEMA.toLowerCase(),
        TestPostgresDatabase._sTEST_TABLE_SIMPLE.toLowerCase(),
        "%");
      for (int iColumn = 0; iColumn < TestPostgresDatabase._listCdSimple.size(); iColumn++)
      {
        TestColumnDefinition tcd = TestPostgresDatabase._listCdSimple.get(iColumn);
        if (rsColumn.next())
        {
          String sColumnName = rsColumn.getString("COLUMN_NAME");
          int iDataType = rsColumn.getInt("DATA_TYPE");
          String sDataType = String.valueOf(iDataType)+" ("+SqlTypes.getTypeName(iDataType)+")";
          String sTypeName = rsColumn.getString("TYPE_NAME");
          System.out.println(sColumnName + ": " +sDataType+" "+sTypeName);
          if (tcd.getName().toLowerCase().equals(sColumnName))
          {
            Object o = getResultSet().getObject(sColumnName);
            checkObject("  ",o, tcd, iDataType, sTypeName, sDataType);
          }
          else
            fail("Invalid column found: "+tcd.getName());
        }
        else
          fail("Column meta data not found!");
      }
      if (rsColumn.next())
        fail("Too many column meta data found!");
      rsColumn.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObjectNativeSimple */
  
  @Test
  public void testGetObjectSqlComplex()
  {
    try
    {
      openResultSet(_sSqlQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      DatabaseMetaData dmd = getResultSet().getStatement().getConnection().getMetaData();
      PostgresQualifiedId pqiTable = new PostgresQualifiedId(TestSqlDatabase.getQualifiedComplexTable().format());
      ResultSet rsColumn = dmd.getColumns(
        pqiTable.getCatalog(), 
        pqiTable.getSchema(),
        pqiTable.getName(),
        "%");
      for (int iColumn = 0; iColumn < TestSqlDatabase._listCdComplex.size(); iColumn++)
      {
        TestColumnDefinition tcd = TestSqlDatabase._listCdComplex.get(iColumn);
        if (rsColumn.next())
        {
          String sColumnName = rsColumn.getString("COLUMN_NAME");
          int iDataType = rsColumn.getInt("DATA_TYPE");
          String sDataType = String.valueOf(iDataType)+" ("+SqlTypes.getTypeName(iDataType)+")";
          String sTypeName = rsColumn.getString("TYPE_NAME");
          System.out.println(sColumnName + ": " +sDataType+" "+sTypeName);
          if (tcd.getName().toLowerCase().equals(sColumnName))
          {
            Object o = getResultSet().getObject(sColumnName);
            checkObject("  ",o, tcd, iDataType, sTypeName, sDataType);
          }
          else
            fail("Invalid column found: "+tcd.getName());
        }
        else
          fail("Column meta data not found!");
      }
      if (rsColumn.next())
        fail("Too many column meta data found!");
      rsColumn.close();
    }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObjectSqlComplex */
    
  @Test
  public void testGetObjectNativeComplex()
  {
    try
    {
      openResultSet(_sNativeQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      DatabaseMetaData dmd = getResultSet().getStatement().getConnection().getMetaData();
      ResultSet rsColumn = dmd.getColumns(null, 
        TestPostgresDatabase._sTEST_SCHEMA.toLowerCase(),
        TestPostgresDatabase._sTEST_TABLE_COMPLEX.toLowerCase(),
        "%");
      for (int iColumn = 0; iColumn < TestPostgresDatabase._listCdComplex.size(); iColumn++)
      {
        TestColumnDefinition tcd = TestPostgresDatabase._listCdComplex.get(iColumn);
        if (rsColumn.next())
        {
          String sColumnName = rsColumn.getString("COLUMN_NAME");
          int iDataType = rsColumn.getInt("DATA_TYPE");
          String sDataType = String.valueOf(iDataType)+" ("+SqlTypes.getTypeName(iDataType)+")";
          String sTypeName = rsColumn.getString("TYPE_NAME");
          System.out.println(sColumnName + ": " +sDataType+" "+sTypeName);
          if (tcd.getName().toLowerCase().equals(sColumnName))
          {
            Object o = getResultSet().getObject(sColumnName);
            checkObject("  ",o, tcd, iDataType, sTypeName, sDataType);
          }
          else
            fail("Invalid column found: "+tcd.getName());
        }
        else
          fail("Column meta data not found!");
      }
      if (rsColumn.next())
        fail("Too many column meta data found!");
      rsColumn.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObjectNativeComplex */
  
  @Test
  public void testInsertRowSimple() throws SQLException
  {
    enter();
    try 
    {
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      getResultSet().moveToInsertRow();
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CINTEGER");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
      tcd = findColumnDefinition(_listCdSimple,"CCHAR_5");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CNCHAR_5");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CNVARCHAR_127");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CCLOB_2M");
      Clob clob = getResultSet().getStatement().getConnection().createClob();
      clob.setString(1l, (String)tcd.getValue());
      getResultSet().updateClob(tcd.getName(), clob);
      tcd = findColumnDefinition(_listCdSimple,"CNCLOB_1M");
      NClob nclob = getResultSet().getStatement().getConnection().createNClob();
      nclob.setString(1l, (String)tcd.getValue());
      getResultSet().updateNClob(tcd.getName(), nclob);
      tcd = findColumnDefinition(_listCdSimple,"CXML");
      SQLXML sqlxml = getResultSet().getStatement().getConnection().createSQLXML();
      sqlxml.setString((String)tcd.getValue());
      getResultSet().updateSQLXML(tcd.getName(), sqlxml);
      tcd = findColumnDefinition(_listCdSimple,"CBINARY_5");
      getResultSet().updateBytes(tcd.getName(),(byte[])tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CVARBINARY_255");
      getResultSet().updateBytes(tcd.getName(),(byte[])tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CBLOB");
      Blob blob = getResultSet().getStatement().getConnection().createBlob();
      blob.setBytes(1l, (byte[])tcd.getValue());
      getResultSet().updateBlob(tcd.getName(), blob);
      tcd = findColumnDefinition(_listCdSimple,"CNUMERIC_31");
      getResultSet().updateBigDecimal(tcd.getName(),(BigDecimal)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CDECIMAL_15_5");
      getResultSet().updateBigDecimal(tcd.getName(),(BigDecimal)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CSMALLINT");
      getResultSet().updateShort(tcd.getName(),((Short)tcd.getValue()).shortValue());
      tcd = findColumnDefinition(_listCdSimple,"CINTEGER");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
      tcd = findColumnDefinition(_listCdSimple,"CBIGINT");
      getResultSet().updateBigDecimal(tcd.getName(),new BigDecimal(((Long)tcd.getValue()).longValue()));
      tcd = findColumnDefinition(_listCdSimple,"CFLOAT_10");
      getResultSet().updateDouble(tcd.getName(),((Float)tcd.getValue()).doubleValue());
      tcd = findColumnDefinition(_listCdSimple,"CREAL");
      getResultSet().updateFloat(tcd.getName(),((Float)tcd.getValue()).floatValue());
      tcd = findColumnDefinition(_listCdSimple,"CDOUBLE");
      getResultSet().updateDouble(tcd.getName(),((Double)tcd.getValue()).doubleValue());
      tcd = findColumnDefinition(_listCdSimple,"CBOOLEAN");
      getResultSet().updateBoolean(tcd.getName(),((Boolean)tcd.getValue()).booleanValue());
      tcd = findColumnDefinition(_listCdSimple,"CDATE");
      getResultSet().updateDate(tcd.getName(),(Date)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CTIME");
      getResultSet().updateTime(tcd.getName(),(Time)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CTIMESTAMP");
      getResultSet().updateTimestamp(tcd.getName(),(Timestamp)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CINTERVAL_YEAR_3_MONTH");
      ((BaseResultSet)getResultSet()).updateDuration(tcd.getName(),((Interval)tcd.getValue()).toDuration());
      tcd = findColumnDefinition(_listCdSimple,"CINTERVAL_DAY_2_SECONDS_6");
      ((BaseResultSet)getResultSet()).updateDuration(tcd.getName(),((Interval)tcd.getValue()).toDuration());
      getResultSet().insertRow();
      getResultSet().moveToCurrentRow();
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      tcd = findColumnDefinition(
        _listCdSimple,"CINTEGER");
      while ((getResultSet().getInt(tcd.getName()) != ((Integer)tcd.getValue()).intValue()) && 
        getResultSet().next()) {}

      tcd = findColumnDefinition(_listCdSimple,"CINTEGER");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Integer)tcd.getValue()).intValue(),
        getResultSet().getInt(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CCHAR_5");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        (getResultSet().getString(tcd.getName())).substring(0,((String)tcd.getValue()).length()));
      tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        getResultSet().getString(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CNCHAR_5");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        (getResultSet().getString(tcd.getName())).substring(0,((String)tcd.getValue()).length()));
      tcd = findColumnDefinition(_listCdSimple,"CNVARCHAR_127");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        getResultSet().getString(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CCLOB_2M");
      clob = getResultSet().getClob(tcd.getName());
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        clob.getSubString(1l, (int)clob.length()));
      tcd = findColumnDefinition(_listCdSimple,"CNCLOB_1M");
      nclob = getResultSet().getNClob(tcd.getName());
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        nclob.getSubString(1l, (int)nclob.length()));
      tcd = findColumnDefinition(_listCdSimple,"CXML");
      sqlxml = getResultSet().getSQLXML(tcd.getName());
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        sqlxml.getString().replaceAll("\\n\\s*",""));
      tcd = findColumnDefinition(_listCdSimple,"CBINARY_5");
      assertTrue("Insert of "+tcd.getType()+" failed!",
        Arrays.equals(
          (byte[])tcd.getValue(),
          Arrays.copyOf(
            getResultSet().getBytes(tcd.getName()),
            ((byte[])tcd.getValue()).length)));
      tcd = findColumnDefinition(_listCdSimple,"CVARBINARY_255");
      assertTrue("Insert of "+tcd.getType()+" failed!",
        Arrays.equals(
          (byte[])tcd.getValue(),
          getResultSet().getBytes(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CBLOB");
      blob = getResultSet().getBlob(tcd.getName());
      assertTrue("Insert of "+tcd.getType()+" failed!",
        Arrays.equals(
          (byte[])tcd.getValue(),
          blob.getBytes(1l,(int)blob.length())));
      tcd = findColumnDefinition(_listCdSimple,"CNUMERIC_31");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (BigDecimal)tcd.getValue(),
        getResultSet().getBigDecimal(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CDECIMAL_15_5");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (BigDecimal)tcd.getValue(),
        getResultSet().getBigDecimal(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CSMALLINT");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Short)tcd.getValue()).shortValue(),
        getResultSet().getShort(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CINTEGER");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Integer)tcd.getValue()).intValue(),
        getResultSet().getInt(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CBIGINT");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        new BigDecimal(((Long)tcd.getValue()).longValue()),
        getResultSet().getBigDecimal(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CFLOAT_10");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Float)tcd.getValue(),
        Float.valueOf(getResultSet().getFloat(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CREAL");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Float)tcd.getValue(),
        Float.valueOf(getResultSet().getFloat(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CDOUBLE");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Double)tcd.getValue(),
        Double.valueOf(getResultSet().getDouble(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CBOOLEAN");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Boolean)tcd.getValue(),
        Boolean.valueOf(getResultSet().getBoolean(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CDATE");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Date)tcd.getValue(),
        getResultSet().getDate(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CTIME");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Time)tcd.getValue(),
        getResultSet().getTime(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CTIMESTAMP");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        truncateToMicros((Timestamp)tcd.getValue()),
        getResultSet().getTimestamp(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CINTERVAL_YEAR_3_MONTH");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Interval)tcd.getValue(),
        Interval.fromDuration(((BaseResultSet)getResultSet()).getDuration(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CINTERVAL_DAY_2_SECONDS_6");
      Date dateZero = new Date(0l);
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Interval)tcd.getValue()).toDuration().getTimeInMillis(dateZero)/1000,
        ((BaseResultSet)getResultSet()).getDuration(tcd.getName()).getTimeInMillis(dateZero)/1000);
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  /** Permits comparison of object syntax from Postgres.
   * Mainly useful when print statements in PostgresObject are activated.
   * @throws SQLException
   */
  @Test
  public void testStruct() throws SQLException
  {
    enter();
    try 
    {
      openResultSet(_sSqlQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      Connection conn = getResultSet().getStatement().getConnection();
      DatabaseMetaData dmd = conn.getMetaData();
      PostgresQualifiedId pqiTable = new PostgresQualifiedId(TestSqlDatabase.getQualifiedComplexTable().format());
      ResultSet rsColumn = dmd.getColumns(
        pqiTable.getCatalog(), 
        pqiTable.getSchema(),
        pqiTable.getName(),
        "complete");
      if (rsColumn.next())
      {
        String sColumnName = rsColumn.getString("COLUMN_NAME");
        int iDataType = rsColumn.getInt("DATA_TYPE");
        String sDataType = String.valueOf(iDataType)+" ("+SqlTypes.getTypeName(iDataType)+")";
        String sTypeName = rsColumn.getString("TYPE_NAME");
        System.out.println(sColumnName + ": " +sDataType+" "+sTypeName);
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdComplex,"COMPLETE");
        if (tcd.getName().toLowerCase().equals(sColumnName))
        {
          Object o = getResultSet().getObject(sColumnName);
          if (o instanceof PostgresStruct)
          {
            PostgresStruct pstruct = (PostgresStruct)o;
            Struct struct = conn.createStruct(pstruct.getSQLTypeName(), pstruct.getAttributes());
            getResultSet().updateObject(sColumnName, struct);
          }
          else
            fail("Expected Struct not found!");
        }
        else
          fail("Invalid column found: "+tcd.getName());
        
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  }
  
  private boolean equalsStructValue(Struct struct,List<TestColumnDefinition> listAd)
    throws SQLException, ParseException
  {
    boolean bEqual = false;
    if (listAd.size() == struct.getAttributes().length)
    {
      bEqual = true;
      for (int iAttribute = 0; iAttribute < listAd.size(); iAttribute++)
      {
        TestColumnDefinition tad = listAd.get(iAttribute);
        Object o = struct.getAttributes()[iAttribute];
        if (o instanceof Clob)
        {
          Clob clob = (Clob)o;
          o = clob.getSubString(1l, (int)clob.length());
        }
        else if (o instanceof Blob)
        {
          Blob blob = (Blob)o;
          o = blob.getBytes(1l, (int)blob.length());
        }
        else if (o instanceof SQLXML)
        {
          SQLXML sqlxml = (SQLXML)o;
          o = sqlxml.getString();
        }
        if (o.getClass().equals(tad.getValue().getClass()))
        {
          if (o instanceof String)
          {
            String s = (String)o;
            String sExpected = (String)tad.getValue();
            o = s.substring(0,sExpected.length());
          }
          else if (o instanceof byte[])
          {
            byte[] buf = (byte[])o;
            byte[] bufExpected = (byte[])tad.getValue();
            assertTrue("Invalid value for "+tad.getType()+"!",Arrays.equals(bufExpected,buf));
          }
          else if (o instanceof Timestamp)
          {
            Timestamp ts = (Timestamp)o;
            Timestamp tsExpected = truncateToMicros((Timestamp)tad.getValue());
            assertEquals("Invalid value for "+tad.getType()+"!",tsExpected,ts);
          }
          else
            assertEquals("Invalid value for "+tad.getType()+"!",tad.getValue(),o);
        }
        else if ((o instanceof Duration) && (tad.getValue() instanceof Interval))
        {
          Duration duration = (Duration)o;
          Interval ivExpected = (Interval)tad.getValue();
          Duration durationExpected = ivExpected.toDuration();
          assertEquals("Invalid value for "+tad.getType()+"!",durationExpected,duration);
        }
        else if ((o instanceof Double) && (tad.getValue() instanceof Float))
        {
          Double d = (Double)o;
          Float fExpected = (Float)tad.getValue();
          Double dExpected = Double.valueOf(fExpected.doubleValue());
          assertEquals("Invalid value for "+tad.getType()+"!",dExpected,d);
        }
        else if ((o instanceof BigDecimal) && (tad.getValue() instanceof Integer))
        {
          BigDecimal bd = (BigDecimal)o;
          assertEquals("Invalid value for "+tad.getType()+"!",(Integer)tad.getValue(),Integer.valueOf(bd.intValueExact()));
        }
        else if ((o instanceof Struct) && (tad.getValue() instanceof List<?>))
        {
          Struct structSub = (Struct)o;
          PostgresQualifiedId pqiTypeExpected = new PostgresQualifiedId(tad.getType());
          QualifiedId qiTypeExpected = new QualifiedId(pqiTypeExpected.format());
          QualifiedId qiType = new QualifiedId(structSub.getSQLTypeName());
          assertEquals("Invalid value for Struct!",qiTypeExpected,qiType);
          @SuppressWarnings("unchecked")
          List<TestColumnDefinition> listAdSub = (List<TestColumnDefinition>)tad.getValue();
          assertTrue("Invalid value for "+tad.getType()+"!",equalsStructValue(structSub,listAdSub));
        }
        else
          fail("Error: "+tad.getType()+": "+tad.getValue().getClass().getName()+"/"+o.getClass().getName());
      }
    }
    return bEqual;
  } /* equalsStructValue */
  
  private boolean equalsArrayValue(Array array,List<TestColumnDefinition> listCd)
    throws SQLException, ParseException
  {
    boolean bEqual = false;
    Object[] aoElements = (Object[])array.getArray();
    if (listCd.size() == aoElements.length)
    {
      bEqual = true;
      for (int iElement = 0; iElement < listCd.size(); iElement++)
      {
        Object oElement = aoElements[iElement];
        TestColumnDefinition tcdElement = listCd.get(iElement);
        assertEquals("Invalid value for "+tcdElement.getType()+"!",tcdElement.getValue(),oElement);
      }
    }
    else
      fail("Array has unexpected length!");
    return bEqual;
  } /* equalsArrayValue */
  
  private Struct createStruct(TestColumnDefinition tcd)
    throws SQLException, ParseException
  {
    Struct struct = null;
    Connection conn = getResultSet().getStatement().getConnection();
    @SuppressWarnings("unchecked")
    List<TestColumnDefinition> listAttributes = (List<TestColumnDefinition>)tcd.getValue();
    Object[] aoAttribute = new Object[listAttributes.size()];
    for (int i = 0; i < listAttributes.size(); i++)
    {
      TestColumnDefinition tad = listAttributes.get(i);
      if (tad.getValue() instanceof List<?>)
        aoAttribute[i] = createStruct(tad);
      else
      {
        if (tad.getType().startsWith(PreType.BLOB.getKeyword()))
        {
          Blob blob = conn.createBlob();
          blob.setBytes(1, (byte[])tad.getValue());
          aoAttribute[i] = blob;
        }
        else if (tad.getType().startsWith(PreType.CLOB.getKeyword()))
        {
          Clob clob = conn.createClob();
          clob.setString(1, (String)tad.getValue());
          aoAttribute[i] = clob;
        }
        else if (tad.getType().startsWith(PreType.NCLOB.getKeyword()))
        {
          NClob nclob = conn.createNClob();
          nclob.setString(1, (String)tad.getValue());
          aoAttribute[i] = nclob;
        }
        else if (tad.getType().startsWith(PreType.XML.getKeyword()))
        {
          SQLXML sqlxml = conn.createSQLXML();
          sqlxml.setString((String)tad.getValueLiteral());
          aoAttribute[i] = sqlxml;
        }
        else
         aoAttribute[i] = tad.getValue();
      }
    }
    PostgresQualifiedId pqiType = new PostgresQualifiedId(tcd.getType());
    struct = conn.createStruct(pqiType.format(), aoAttribute);
    return struct;
  } /* createStruct */

  private Array createArray(TestColumnDefinition tcd)
    throws SQLException
  {
    Array array = null;
    Connection conn = getResultSet().getStatement().getConnection();
    @SuppressWarnings("unchecked")
    List<TestColumnDefinition> listElements = (List<TestColumnDefinition>)tcd.getValue();
    Object[] aoElement = new Object[listElements.size()];
    String sBaseTypeName = tcd.getType();
    int iArray = sBaseTypeName.lastIndexOf("ARRAY[");
    sBaseTypeName = sBaseTypeName.substring(0,iArray).trim();
    for (int iElement = 0; iElement < aoElement.length; iElement++)
    {
      TestColumnDefinition tcdElement = listElements.get(iElement);
      if (!sBaseTypeName.equals(tcdElement.getType()))
        throw new SQLException("Types of elements of array do not agree!");
      aoElement[iElement] = tcdElement.getValue();
    }
    array = conn.createArrayOf(sBaseTypeName, aoElement);
    return array;
  } /* createArray */
  
  @Test
  public void testInsertRowComplex() throws SQLException
  {
    enter();
    try 
    {
      openResultSet(_sSqlQuerySimple,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      // needed for foreign key constraint ...
      getResultSet().moveToInsertRow();
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CINTEGER");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
      tcd = findColumnDefinition(
        _listCdSimple,"CCHAR_5");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
      
      getResultSet().insertRow();
      getResultSet().moveToCurrentRow();
  
      openResultSet(_sSqlQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
      getResultSet().moveToInsertRow();
      tcd = findColumnDefinition(_listCdComplex,"CID");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
  
      tcd = findColumnDefinition(_listCdComplex,"COMPLETE");
      Struct struct = createStruct(tcd); 
      getResultSet().updateObject(tcd.getName(), struct);
      
      tcd = findColumnDefinition(_listCdComplex,"CUDT");
      struct = createStruct(tcd); 
      getResultSet().updateObject(tcd.getName(), struct);
      
      tcd = findColumnDefinition(_listCdComplex,"CDISTINCT");
      @SuppressWarnings("unchecked")
      List<TestColumnDefinition> listDistinct = (List<TestColumnDefinition>)tcd.getValue();
      TestColumnDefinition tcdBase = listDistinct.get(0);
      getResultSet().updateObject(tcd.getName(),(String)tcdBase.getValue());
      
      tcd = findColumnDefinition(_listCdComplex,"CARRAY");
      Array array = createArray(tcd); 
      getResultSet().updateArray(tcd.getName(), array);
      
      getResultSet().insertRow();
      getResultSet().moveToCurrentRow();
      
      closeResultSet();
      
      openResultSet(_sSqlQueryComplex,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      tcd = findColumnDefinition(_listCdComplex,"CID");
      while ((getResultSet().getInt(tcd.getName()) != ((Integer)tcd.getValue()).intValue()) && 
        getResultSet().next()) {}

      tcd = findColumnDefinition(_listCdComplex,"CID");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Integer)tcd.getValue()).intValue(),
        getResultSet().getInt(tcd.getName()));
      
      tcd = findColumnDefinition(_listCdComplex,"COMPLETE");
      struct = (Struct)getResultSet().getObject(tcd.getName()); 
      assertTrue("Insert of "+tcd.getType()+" failed!",
        equalsStructValue(struct, _listCdSimple));
  
      tcd = findColumnDefinition(_listCdComplex,"CUDT");
      struct = (Struct)getResultSet().getObject(tcd.getName()); 
      assertTrue("Insert of "+tcd.getType()+" failed!",
        equalsStructValue(struct, _listAdComplex));
      
      tcd = findColumnDefinition(_listCdComplex,"CDISTINCT");
      Object o = getResultSet().getObject(tcd.getName());
      assertEquals("Insert of "+tcd.getType()+" failed!",
        tcdBase.getValue(),o);
  
      tcd = findColumnDefinition(_listCdComplex,"CARRAY");
      array = (Array)getResultSet().getObject(tcd.getName()); 
      assertTrue("Insert of "+tcd.getType()+" failed!",
        equalsArrayValue(array, _listCdArray));
 
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  } /* testInsertRowSqlComplex */
    
}
