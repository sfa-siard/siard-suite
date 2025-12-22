package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import static org.junit.Assert.*;
import org.junit.*;

import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.database.SqlTypes;
import ch.enterag.utils.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.postgres.*;
import ch.admin.bar.siard2.postgres.identifier.PostgresQualifiedId;

@Ignore
public class PostgresDatabaseMetaDataTester extends BaseDatabaseMetaDataTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = PostgresDriver.getUrl(_cp.getHost()+":"+_cp.getPort()+"/"+_cp.getCatalog());
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();
  private static PostgresQualifiedId _pqiNativeSimpleTable = null;
  private static PostgresQualifiedId _pqiNativeComplexTable = null;
  private static PostgresQualifiedId _pqiSqlSimpleTable = null;
  private static PostgresQualifiedId _pqiSqlComplexTable = null;
  private static PostgresQualifiedId _pqiSqlSimpleView = null;
  private static Set<PostgresQualifiedId> _setTestTables = null;
  private static Set<PostgresQualifiedId> _setTestViews = null;
  {
    try
    {
      _pqiNativeSimpleTable = new PostgresQualifiedId(TestPostgresDatabase.getQualifiedSimpleTable().format());
      _pqiNativeComplexTable = new PostgresQualifiedId(TestPostgresDatabase.getQualifiedComplexTable().format());
      _pqiSqlSimpleTable = new PostgresQualifiedId(TestSqlDatabase.getQualifiedSimpleTable().format());
      _pqiSqlComplexTable = new PostgresQualifiedId(TestSqlDatabase.getQualifiedComplexTable().format());
      _pqiSqlSimpleView = new PostgresQualifiedId(TestSqlDatabase.getQualifiedSimpleView().format());
      _setTestTables = new HashSet<PostgresQualifiedId>(
        Arrays.asList(new PostgresQualifiedId[]{
          _pqiNativeSimpleTable,
          _pqiNativeComplexTable,
          _pqiSqlSimpleTable,
          _pqiSqlComplexTable}));
      _setTestViews = new HashSet<PostgresQualifiedId>(
        Arrays.asList(new PostgresQualifiedId[] {_pqiSqlSimpleView}));
    }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  }

  public static void print(ResultSet rs)
    throws SQLException
  {
    if ((rs != null) && (!rs.isClosed()))
    {
      ResultSetMetaData rsmd = rs.getMetaData();
      if (rsmd != null)
      {
        int iColumns = rsmd.getColumnCount();
        List<String> listColumns = new ArrayList<String>();
        StringBuilder sbLine = new StringBuilder();
        for (int iColumn = 0; iColumn < iColumns; iColumn++)
        {
          if (iColumn > 0)
            sbLine.append("\t");
          String sColumnName = rsmd.getColumnLabel(iColumn+1);
          sbLine.append(sColumnName);
          listColumns.add(sColumnName);
        }
        System.out.println(sbLine.toString());
        sbLine.setLength(0);
        while (rs.next())
        {
          for (int iColumn = 0; iColumn < iColumns; iColumn++)
          {
            if (iColumn > 0)
              sbLine.append("\t");
            String sColumnName = listColumns.get(iColumn);
            String sValue = String.valueOf(rs.getObject(iColumn+1));
            if (!rs.wasNull())
            {
              if (sColumnName.equalsIgnoreCase("DATA_TYPE"))
                sValue = sValue + " ("+SqlTypes.getTypeName(Integer.parseInt(sValue))+")";
            }
            else
              sValue = "(null)";
            sbLine.append(sValue);
          }
          System.out.println(sbLine.toString());
          sbLine.setLength(0);
        }
        rs.close();
      }
    }
    else if (rs.isClosed()) 
      throw new SQLException("Empty meta data result set!");
    else
      fail("Invalid meta data result set");
  } /* print */
  
  @BeforeClass
  public static void setUpClass() throws SQLException, IOException {
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
  
  @Before
  public void setUp()
  {
    try 
    { 
      PostgresDataSource dsPostgres = new PostgresDataSource();
      dsPostgres.setUrl(_sDB_URL);
      dsPostgres.setUser(_sDB_USER);
      dsPostgres.setPassword(_sDB_PASSWORD);
      PostgresConnection connPostgres = (PostgresConnection)dsPostgres.getConnection();
      connPostgres.setAutoCommit(false);
      PostgresDatabaseMetaData dmdPostgres = (PostgresDatabaseMetaData)connPostgres.getMetaData();
      setDatabaseMetaData(dmdPostgres);
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* setUp */
  
  @Test
  public void testClass()
  {
    assertEquals("Wrong database meta data class!", PostgresDatabaseMetaData.class, getDatabaseMetaData().getClass());
  } /* testClass */
  
  @Override
  @Test
  public void testGetClientInfoProperties()
  {
    enter();
    try { print(getDatabaseMetaData().getClientInfoProperties()); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  /** getTypeInfo is not overridden with a version conforming to SQL:2008
   * because that would not result in useful information (just the
   * standard predefined types would be listed).  
   */
  @Override
  @Test
  public void testGetTypeInfo()
  {
    enter();
    try { print(getDatabaseMetaData().getTypeInfo()); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Override
  @Test
  public void testGetTableTypes()
  {
    enter();
    try { print(getDatabaseMetaData().getTableTypes()); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetTablesTable()
  {
    enter();
    try 
    {
      print(getDatabaseMetaData().getTables(null,null,"%",new String[] {"TABLE"}));
      Set<PostgresQualifiedId>setTestTables = new HashSet<PostgresQualifiedId>(_setTestTables);
      ResultSet rs = getDatabaseMetaData().getTables(null,null,"%",new String[] {"TABLE"});
      while (rs.next())
      {
        PostgresQualifiedId pqiTable = new PostgresQualifiedId(); 
        pqiTable.setCatalog(rs.getString("TABLE_CAT"));
        pqiTable.setSchema(rs.getString("TABLE_SCHEM"));
        pqiTable.setName(rs.getString("TABLE_NAME"));
        setTestTables.remove(pqiTable);
      }
      rs.close();
      assertTrue("Some test tables not found!",setTestTables.isEmpty());
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetTablesView()
  {
    enter();
    try 
    { 
      print(getDatabaseMetaData().getTables(null,null,"%",new String[] {"VIEW"})); 
      Set<PostgresQualifiedId>setTestViews = new HashSet<PostgresQualifiedId>(_setTestViews);
      ResultSet rs = getDatabaseMetaData().getTables(null,null,"%",new String[] {"VIEW"});
      while (rs.next())
      {
        PostgresQualifiedId pqiView = new PostgresQualifiedId(); 
        pqiView.setCatalog(rs.getString("TABLE_CAT"));
        pqiView.setSchema(rs.getString("TABLE_SCHEM"));
        pqiView.setName(rs.getString("TABLE_NAME"));
        setTestViews.remove(pqiView);
      }
      rs.close();
      assertTrue("Some test views not found!",setTestViews.isEmpty());
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  /** determine set of user tables or views for tests.
   * @return user tables.
   * @throws SQLException
   */
  private Set<PostgresQualifiedId> getUserTablesViews()
    throws SQLException
  {
    Set<PostgresQualifiedId> setTablesViews = new HashSet<PostgresQualifiedId>();
    setTablesViews.addAll(_setTestTables);
    setTablesViews.addAll(_setTestViews);
    return setTablesViews;
  } /* getTablesUserViews */
  
  /** compute size in characters of the int type with the given maximum.
   * N.B.: Postgres uses this value. The correct value would be
   * length(2*kMaxUnsigned+1)+1 for unsigned types with sign.
   * @param lMaxSigned maximum of signed int type.
   * @return size in characters
   */
  private int sizeFromMax(long lMaxSigned)
  {
    int iSize = String.valueOf(lMaxSigned).length();
    return iSize;
  }
  
  /** split type name from rest
   * @param sType type with qualifications.
   * @return array with type name as first element and possibly 
   *   qualifications as second element. 
   */
  
  private PostgresType parsePostgresType(String sType)
  {
    String[] asType = sType.split("\\(",2);
    PostgresType pgt = PostgresType.getByKeyword(asType[0]);
    if (pgt == null)
    {
      asType = sType.split("\\s",2);
      pgt = PostgresType.getByKeyword(asType[0]);
    } 
    return pgt;
  }
  
  /** split type name from rest and drop "N" (NATIONAL) from character types.
   * @param sType type with qualifications.
   * @return array with type name as first element and possibly 
   *   qualifications as second element. 
   */
  
  private PreType parsePreType(String sType)
  {
    String[] asType = sType.split("\\(",2);
    PreType pt = PreType.getByKeyword(asType[0]);
    if (pt == null)
    {
      asType = sType.split("\\s",2);
      pt = PreType.getByKeyword(asType[0]);
    }
    if (pt == PreType.NCHAR)
      pt = PreType.CHAR;
    else if (pt == PreType.NVARCHAR)
      pt = PreType.VARCHAR;
    else if (pt == PreType.BINARY)
      pt = PreType.VARBINARY;
    else if (pt == PreType.NCLOB)
      pt = PreType.CLOB;
    else if (pt == PreType.DECIMAL)
      pt = PreType.NUMERIC;
    else if (pt == PreType.FLOAT)
      pt = PreType.DOUBLE;
    return pt;
  }
  
  /** extract precision from type in column definition.
   * @param sType type in column definition.
   * @return precision.
   */
  private int parsePrecision(String sType)
  {
    int iPrecision = 0;
    PostgresType pgt = parsePostgresType(sType);
    PreType pt = null;
    if (pgt != null)
      pt = pgt.getPreType();
    else
      pt = parsePreType(sType);
    
    if (pgt != null)
    {
      if (pgt == PostgresType.MONEY)
        iPrecision = 0; // PostgresMetaColumns.iMAX_NUMERIC_PRECISION;
      else if (pgt == PostgresType.OID)
        iPrecision = sizeFromMax(Long.MAX_VALUE);
      else if (pgt == PostgresType.TIMETZ)
        iPrecision = 21;
      else if (pgt == PostgresType.TIMESTAMPTZ)
        iPrecision = 29;
      else if (pgt == PostgresType.UUID)
        iPrecision = 16;
      else if (pgt == PostgresType.MACADDR)
        iPrecision = 6;
      else if (pgt == PostgresType.MACADDR8)
        iPrecision = 8;
      else if ((pgt == PostgresType.TEXT) ||
        (pgt == PostgresType.POINT) ||
        (pgt == PostgresType.LINE) ||
        (pgt == PostgresType.LSEG) ||
        (pgt == PostgresType.BOX) ||
        (pgt == PostgresType.PATH) ||
        (pgt == PostgresType.POLYGON) ||
        (pgt == PostgresType.CIRCLE))
        iPrecision = PostgresMetaColumns.iMAX_VAR_LENGTH;
      else if (pgt == PostgresType.BYTEA)
        iPrecision = PostgresMetaColumns.iMAX_TEXT_LENGTH;;
    }
    if (iPrecision == 0)
    {
      if (pt == PreType.INTEGER)
        iPrecision = sizeFromMax(Integer.MAX_VALUE);
      else if (pt == PreType.SMALLINT)
        iPrecision = sizeFromMax(Short.MAX_VALUE);
      else if (pt == PreType.BIGINT)
        iPrecision = sizeFromMax(Long.MAX_VALUE);
      else if (pt == PreType.DOUBLE)
        iPrecision = 17;
      else if (pt == PreType.FLOAT)
        iPrecision = 17;
      else if (pt == PreType.REAL)
        iPrecision = 8;
      else if (pt == PreType.BOOLEAN)
        iPrecision = 1;
      else if (pt == PreType.CLOB)
        iPrecision = Integer.MAX_VALUE;
      else if (pt == PreType.NCLOB)
        iPrecision = Integer.MAX_VALUE;
      else if (pt == PreType.XML)
        iPrecision = PostgresMetaColumns.iMAX_VAR_LENGTH;
      else if (pt == PreType.BLOB)
        iPrecision = Integer.MAX_VALUE;
      else if (pt == PreType.DATE)
        iPrecision = 13;
      else if (pt == PreType.TIME)
        iPrecision = 15;
      else if (pt == PreType.TIMESTAMP)
        iPrecision = 29;
      else if (pt == PreType.INTERVAL)
        iPrecision = 49;
    }
    if (iPrecision == 0)
    {
      int iParen = sType.indexOf('(');
      if (iParen >= 0)
      {
        String sPrecision = sType.substring(iParen+1,sType.length()-1);
        int iComma = sPrecision.indexOf(',');
        if (iComma >= 0)
          sPrecision = sPrecision.substring(0,iComma).toUpperCase();
        int iMult = 1;
        if (sPrecision.endsWith("K") || sPrecision.endsWith("M") || sPrecision.endsWith("G"))
        {
          iMult = 1024;
          if (!sPrecision.endsWith("K"))
          {
            iMult = 1024*iMult;
            if (!sPrecision.endsWith("M"))
              iMult = 1024*iMult;
          }
          sPrecision = sPrecision.substring(0,sPrecision.length()-1);
        }
        iPrecision = iMult*Integer.valueOf(sPrecision);
        if ((pgt == PostgresType.BIT) || (pgt == PostgresType.VARBIT))
          iPrecision = (iPrecision + 7)/8;
        else if ((pt == PreType.BINARY) || (pt == PreType.VARBINARY))
          iPrecision = PostgresMetaColumns.iMAX_TEXT_LENGTH;
      }
    }
    if (iPrecision == 0)
    {
      if (pt == PreType.VARCHAR || pt == PreType.DATALINK)
        iPrecision = PostgresMetaColumns.iMAX_VAR_LENGTH;
      else if (pt == PreType.VARBINARY)
        iPrecision = PostgresMetaColumns.iMAX_TEXT_LENGTH;
    }
    
    return iPrecision;
  } /* parsePrecision */
  
  private int parseScale(String sType)
  {
    int iScale = 0;
    PostgresType pgt = parsePostgresType(sType);
    PreType pt = null;
    if (pgt != null)
      pt = pgt.getPreType();
    else
      pt = parsePreType(sType);
    
    if (pt == PreType.DOUBLE)
      iScale = 17;
    else if (pt == PreType.REAL)
      iScale = 8;
    else if (pt == PreType.TIME)
      iScale = 6;
    else if (pt == PreType.TIMESTAMP)
      iScale = 6;

    if (iScale == 0)
    {
      int iRparen = sType.lastIndexOf(")");
      int iLparen = sType.lastIndexOf("(");
      if ((iLparen >= 0) && (iRparen == (sType.length()-1)))
      {
        String sPrecision = sType.substring(iLparen+1,iRparen);
        int iComma = sPrecision.indexOf(',');
        if (iComma >= 0)
        {
          String sScale = sPrecision.substring(iComma+1);
          iScale = Integer.valueOf(sScale);
        }
        else if (pt == PreType.INTERVAL)
          iScale = Integer.valueOf(sPrecision);
      }
    }
    return iScale;
  } /* parseScale */
  
  /** find column definition in list with matching column name.
   * @param sColumnName column name.
   * @param listCd list
   * @return column definition.
   */
  private TestColumnDefinition findTestColumnDefinition(String sColumnName, List<TestColumnDefinition> listCd)
  {
    TestColumnDefinition cdFound = null;
    for (Iterator<TestColumnDefinition> iterCd = listCd.iterator(); (cdFound == null) && iterCd.hasNext(); )
    {
      TestColumnDefinition cd = iterCd.next();
      if (cd.getName().equalsIgnoreCase(sColumnName))
        cdFound = cd;
    }
    return cdFound;
  } /* findTestColumnDefinition */
  
  @Test
  @Override
  public void testGetColumns()
  {
    try
    {    
      PostgresQualifiedId pqiTable = new PostgresQualifiedId(TestPostgresDatabase.getQualifiedComplexTable().format());
      print(getDatabaseMetaData().getColumns(pqiTable.getCatalog(), pqiTable.getSchema(), pqiTable.getName(), "%"));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  } /* testGetColumns */
  
  /** list columns of user tables
   */
  @Test
  public void testGetColumnsUser()
  {
    enter();
    try
    {
      Set<PostgresQualifiedId> setUserTablesViews = getUserTablesViews();
      for (Iterator<PostgresQualifiedId> iterTableView = setUserTablesViews.iterator(); iterTableView.hasNext(); )
      {
        PostgresQualifiedId pqiTableView = iterTableView.next();
        System.out.println("\nTable/View: "+pqiTableView.format());
        ResultSet rsColumns = getDatabaseMetaData().getColumns(
          pqiTableView.getCatalog(), pqiTableView.getSchema(), pqiTableView.getName(), "%");
        int iPosition = 0;
        while (rsColumns.next())
        {
          String sColumnName = rsColumns.getString("COLUMN_NAME");
          int iDataType = rsColumns.getInt("DATA_TYPE");
          String sTypeName = rsColumns.getString("TYPE_NAME");
          int iColumnSize = rsColumns.getInt("COLUMN_SIZE");
          int iDecimalDigits = rsColumns.getInt("DECIMAL_DIGITS");
          int iNumPrecRadix = rsColumns.getInt("NUM_PREC_RADIX");
          int iNullable = rsColumns.getInt("NULLABLE");
          int iCharOctetLength = rsColumns.getInt("CHAR_OCTET_LENGTH");
          int iOrdinalPosition = rsColumns.getInt("ORDINAL_POSITION");
          String sIsNullable = rsColumns.getString("IS_NULLABLE");
          String sIsAutoIncrement = rsColumns.getString("IS_AUTOINCREMENT");
          
          String sAutoIncrement = "NO";
          int iNulls = DatabaseMetaData.columnNullable;
          String sNullable = "YES";
          int iPrecision = Integer.MAX_VALUE;
          int iScale = 0;
          int iRadix = 10;
          int iType = Types.NULL;
          String sType = sTypeName;
          // native 
          if (pqiTableView.equals(_pqiNativeSimpleTable) ||
              pqiTableView.equals(_pqiNativeComplexTable))
          {
            TestColumnDefinition cd = null;
            if (pqiTableView.equals(_pqiNativeSimpleTable))
            {
              cd = findTestColumnDefinition(sColumnName,TestPostgresDatabase._listCdSimple);
              if (iPosition == TestPostgresDatabase._iPrimarySimple)
                iNulls = DatabaseMetaData.columnNoNulls;
            }
            else if (pqiTableView.equals(_pqiNativeComplexTable))
            {
              cd = findTestColumnDefinition(sColumnName,TestPostgresDatabase._listCdComplex);
              if (iPosition == TestPostgresDatabase._iPrimaryComplex)
                iNulls = DatabaseMetaData.columnNoNulls;
            }
            if (sColumnName.equalsIgnoreCase("CINT_DOMAIN") || sColumnName.equalsIgnoreCase("CYEAR"))
            {
              iType = Types.DISTINCT;
              iPrecision = sizeFromMax(Integer.MAX_VALUE);              
              System.out.println("  DISTINCT "+sTypeName);
            }
            else if (sColumnName.equalsIgnoreCase("CENUM_SUIT"))
            {
              iType = Types.DISTINCT;
              iPrecision = PostgresMetaColumns.iMAX_VAR_LENGTH;
              System.out.println("  ENUM "+sTypeName);
            }
            else if (sColumnName.equalsIgnoreCase("CCOMPOSITE"))
            {
              iType = Types.STRUCT;
              iPrecision = 0;              
              System.out.println("  STRUCT "+sTypeName);
            }
            else if (sColumnName.equalsIgnoreCase("CINT_BUILTIN"))
            {
              iType = Types.STRUCT;
              iPrecision = 0;              
              System.out.println("  BUILTIN "+sTypeName);
            }
            else if (sColumnName.equalsIgnoreCase("CSTRING_RANGE"))
            {
              iType = Types.STRUCT;
              System.out.println("  RANGE "+sTypeName);
            }
            else if (sColumnName.equalsIgnoreCase("CSTRING_ARRAY"))
            {
              iType = Types.ARRAY;
              iPrecision = 0;              
              System.out.println("  ARRAY "+sTypeName);
            }
            else if (sColumnName.equalsIgnoreCase("CDOUBLE_MATRIX"))
            {
              iType = Types.ARRAY;
              iPrecision = 0;              
              System.out.println("  MATRIX "+sTypeName);
            }
            else
            {
              // we really do not really need to check the TYPE_NAME as it remains unchanged
              if (!sTypeName.startsWith(PostgresType.INTERVAL.getKeyword()))
              {
                PostgresType pgtFound = parsePostgresType(sTypeName);
                sTypeName = pgtFound.getPreType().getKeyword();
              }
              iPrecision = parsePrecision(cd.getType());
              iScale = parseScale(cd.getType());
              if (!cd.getType().startsWith(PostgresType.INTERVAL.getKeyword()))
              {
                PostgresType pgtExpected = parsePostgresType(cd.getType());
                iType = pgtExpected.getPreType().getSqlType();
                sType = pgtExpected.getPreType().getKeyword();
                if ((pgtExpected == PostgresType.BIT) || (pgtExpected == PostgresType.VARBIT))
                  iRadix = 2;
              }
              else
              {
                iType = Types.OTHER;
                sType = cd.getType();
              }
              if (cd.getType().indexOf("serial") >= 0)
                sAutoIncrement = "YES";
              if (sAutoIncrement.equals("YES"))
                iNulls = DatabaseMetaData.columnNoNulls;
              if (iNulls == DatabaseMetaData.columnNoNulls)
                sNullable = "NO";
              else if (iNulls == DatabaseMetaData.columnNullableUnknown)
                sNullable = "";
            }
          }
          // SQL
          else if (pqiTableView.equals(_pqiSqlSimpleTable) ||
            pqiTableView.equals(_pqiSqlComplexTable) ||
            pqiTableView.equals(_pqiSqlSimpleView))
          {
            TestColumnDefinition tcd = null;
            if (pqiTableView.equals(_pqiSqlSimpleTable))
            {
              tcd = findTestColumnDefinition(sColumnName,TestSqlDatabase._listCdSimple);
              if ((iPosition == TestSqlDatabase._iPrimarySimple) ||
                  (tcd.getName().equals("CCHAR_5")))
                iNulls = DatabaseMetaData.columnNoNulls;
            }
            else if (pqiTableView.equals(_pqiSqlComplexTable))
            {
              tcd = findTestColumnDefinition(sColumnName,TestSqlDatabase._listCdComplex);
              if (iPosition == TestSqlDatabase._iPrimaryComplex)
                iNulls = DatabaseMetaData.columnNoNulls;
            }
            else if (pqiTableView.equals(_pqiSqlSimpleView))
              tcd = findTestColumnDefinition(sColumnName,TestSqlDatabase._listCdSimple);
            if (sColumnName.equalsIgnoreCase("CDISTINCT"))
            {
              iType = Types.DISTINCT;
              iPrecision = PostgresMetaColumns.iMAX_VAR_LENGTH;              
              System.out.println("  DISTINCT "+sTypeName);
            }
            else if (sColumnName.equalsIgnoreCase("CUDT") || sColumnName.equalsIgnoreCase("COMPLETE"))
            {
              iType = Types.STRUCT;
              iPrecision = 0;              
              System.out.println("  STRUCT "+sTypeName);
            }
            else if (sColumnName.equalsIgnoreCase("CARRAY"))
            {
              iType = Types.ARRAY;
              iPrecision = 0;              
              System.out.println("  ARRAY "+sTypeName);
            }
            else
            {
              // we really do not really need to check the TYPE_NAME as it remains unchanged
              PostgresType pgtFound = parsePostgresType(sTypeName);
              sTypeName = pgtFound.getPreType().getKeyword();
              iPrecision = parsePrecision(tcd.getType());
              iScale = parseScale(tcd.getType());
              PreType pt = parsePreType(tcd.getType());
              iType = pt.getSqlType();
              sType = pt.getKeyword();
              if (iNulls == DatabaseMetaData.columnNoNulls)
                sNullable = "NO";
              else if (iNulls == DatabaseMetaData.columnNullableUnknown)
                sNullable = "";
              if (pt == PreType.BINARY)
                iRadix = 2;
            }
          }
          assertEquals("Unexpected data type for "+sColumnName,iType,iDataType);
          assertEquals("Unexpected type name for "+sColumnName,sType,sTypeName);
          assertEquals("Unexpected column size for "+sColumnName,iPrecision,iColumnSize);
          assertEquals("Unexpected decimal digits for "+sColumnName,iScale,iDecimalDigits);
          assertEquals("Unexpected radix for "+sColumnName,iRadix,iNumPrecRadix);
          assertEquals("Unexpected nullable for "+sColumnName,iNulls,iNullable);
          assertEquals("Unexpected length for "+sColumnName,iPrecision,iCharOctetLength);        
          iPosition = iPosition + 1;
          assertEquals("Unexpected ordinal_position for "+sColumnName,iPosition,iOrdinalPosition);
          assertEquals("Unexpected is_nullable for "+sColumnName,sNullable,sIsNullable);
          assertEquals("Unexpected is_autoincrement for "+sColumnName,sAutoIncrement,sIsAutoIncrement);
        }
        rsColumns.close();
        print(getDatabaseMetaData().getColumns(pqiTableView.getCatalog(), pqiTableView.getSchema(), pqiTableView.getName(), "%"));
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetUserColumns */
    
  /** determine set of schemas holding user tables.
   * @return schemas, holding user tables.
   * @throws SQLException
   */
  private Set<String> getUserSchemas()
    throws SQLException
  {
    Set<String>setSchemas = new HashSet<String>();
    setSchemas.add(TestPostgresDatabase._sTEST_SCHEMA.toLowerCase());
    setSchemas.add(TestSqlDatabase._sTEST_SCHEMA.toLowerCase());
    setSchemas.add("public");
    return setSchemas;
  } /* getUserSchemas */
  
  @Test
  public void testGetUDTsUserDistinct()
  {
    enter();
    try 
    { 
      Set<String>setSchemas = getUserSchemas();
      for (Iterator<String>iterSchema = setSchemas.iterator(); iterSchema.hasNext(); )
      {
        String sSchema = iterSchema.next();
        System.out.println("Schema "+sSchema);
        print(getDatabaseMetaData().getUDTs(null,sSchema,"%", new int[] {Types.DISTINCT} ));
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetUDTsUserStruct()
  {
    enter();
    try 
    { 
      Set<String>setSchemas = getUserSchemas();
      for (Iterator<String>iterSchema = setSchemas.iterator(); iterSchema.hasNext(); )
      {
        String sSchema = iterSchema.next();
        System.out.println("Schema "+sSchema);
        print(getDatabaseMetaData().getUDTs(null,sSchema,"%", new int[] {Types.STRUCT} ));
      }
      // and the built-in range
      System.out.println("Schema pg_catalog");
      print(getDatabaseMetaData().getUDTs(null,null,"int4range", new int[] {Types.STRUCT} ));
      
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetUDTsUserNative()
  {
    enter();
    try 
    { 
      String sSchema = "testpgschema";
      print(getDatabaseMetaData().getUDTs(null,sSchema,"%", null ));
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetAttributesUser()
  {
    enter();
    try 
    { 
      Set<String>setSchemas = getUserSchemas();
      for (Iterator<String>iterSchema = setSchemas.iterator(); iterSchema.hasNext(); )
      {
        String sSchema = iterSchema.next();
        System.out.println("Schema "+sSchema);
        print(getDatabaseMetaData().getAttributes(null,sSchema,"%", null));
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetAttributesAll()
  {
    enter();
    try 
    { 
      print(getDatabaseMetaData().getAttributes(null,"testsqlschema","typsqlall", null));
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetAttributesComposite()
  {
    enter();
    try 
    { 
      QualifiedId qiType = TestPostgresDatabase.getQualifiedCompositeType();
      print(getDatabaseMetaData().getAttributes(null,qiType.getSchema().toLowerCase(),qiType.getName().toLowerCase(), null));
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetAttributesRange()
  {
    enter();
    try 
    { 
      QualifiedId qiType = TestPostgresDatabase.getQualifiedRangeType();
      print(getDatabaseMetaData().getAttributes(null,qiType.getSchema().toLowerCase(),qiType.getName().toLowerCase(), null));
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetAttributesRangeBuiltin()
  {
    enter();
    try 
    { 
      print(getDatabaseMetaData().getAttributes(null,"pg_catalog","int4range", null));
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  /** get procedures of schemas holding user tables
   */
  @Test
  public void testGetProceduresUser()
  {
    enter();
    try 
    { 
      Set<String>setSchemas = getUserSchemas();
      for (Iterator<String>iterSchema = setSchemas.iterator(); iterSchema.hasNext(); )
      {
        String sSchema = iterSchema.next();
        System.out.println("Schema "+sSchema);
        print(getDatabaseMetaData().getProcedures(null,sSchema,"%"));
      }
      print(getDatabaseMetaData().getProcedures(null,null,"int4range"));
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetUserProcedures */
  
  /** get parameters of procedures of schemas holding user tables
   */
  @Test
  public void testGetProcedureColumnsUser()
  {
    enter();
    try 
    { 
      Set<String>setSchemas = getUserSchemas();
      for (Iterator<String>iterSchema = setSchemas.iterator(); iterSchema.hasNext(); )
      {
        String sSchema = iterSchema.next();
        System.out.println("Schema "+sSchema);
        print(getDatabaseMetaData().getProcedureColumns(null,sSchema,"%","%"));
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetUserProcedureColumns */
  
  /** get functions of schemas holding user tables
   */
  @Test
  public void testGetFunctionsUser()
  {
    enter();
    try 
    {
      Set<String>setSchemas = getUserSchemas();
      for (Iterator<String>iterSchema = setSchemas.iterator(); iterSchema.hasNext(); )
      {
        String sSchema = iterSchema.next();
        System.out.println("Schema "+sSchema);
        print(getDatabaseMetaData().getFunctions(null,sSchema,"%"));
      }
    } 
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetFunctionColumnsUser()
  {
    enter();
    try 
    { 
      Set<String>setSchemas = getUserSchemas();
      for (Iterator<String>iterSchema = setSchemas.iterator(); iterSchema.hasNext(); )
      {
        String sSchema = iterSchema.next();
        System.out.println("Schema "+sSchema);
        print(getDatabaseMetaData().getFunctionColumns(null,sSchema,"%","%"));
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  @Override
  public void testGetProcedureColumns()
  {
  }
  
  @Test
  @Override
  public void testGetFunctionColumns()
  {
  }
  
}
