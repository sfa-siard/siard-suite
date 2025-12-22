package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.util.*;
import java.util.regex.*;
import static org.junit.Assert.*;

import org.junit.*;

import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.mysql.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.identifier.*;

public class MySqlDatabaseMetaDataTester extends BaseDatabaseMetaDataTester
{
	private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = MySqlDriver.getUrl(_cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog(),true);
	private static final String _sDB_USER = _cp.getUser();
	private static final String _sDB_PASSWORD = _cp.getPassword();
	private static final String _sDB_CATALOG = _cp.getCatalog();
	private static final String _sDBA_USER = _cp.getDbaUser();
	private static final String _sDBA_PASSWORD = _cp.getDbaPassword();
  private static Pattern _patTYPE = Pattern.compile("^(.*?)(\\(\\s*((\\d+)(\\s*,\\s*(\\d+))?)\\s*\\))?$");

	private MySqlDatabaseMetaData _dmdMySql = null;

	@BeforeClass
	public static void setUpClass()
	{
		try
		{
			MySqlDataSource dsMySql = new MySqlDataSource();
			dsMySql.setUrl(_sDB_URL);
			dsMySql.setUser(_sDBA_USER);
			dsMySql.setPassword(_sDBA_PASSWORD);
			MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
      connMySql.setAutoCommit(false);
      TestMySqlDatabase.grantSchemaUser(connMySql, 
        "mysql", _sDB_USER); // needed for some meta data!
      /* drop and create the test databases */
      new TestMySqlDatabase(connMySql);
      TestMySqlDatabase.grantSchemaUser(connMySql, 
        TestMySqlDatabase._sTEST_SCHEMA, _sDB_USER);
      new TestSqlDatabase(connMySql);
      TestMySqlDatabase.grantSchemaUser(connMySql, 
        TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
      connMySql.commit();
			connMySql.close();
		}
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* setUpClass */
	
	@AfterClass
	public static void tearDownClass()
	{
    try
    {
      MySqlDataSource dsMySql = new MySqlDataSource();
      dsMySql.setUrl(_sDB_URL);
      dsMySql.setUser(_sDBA_USER);
      dsMySql.setPassword(_sDBA_PASSWORD);
      MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
      connMySql.setAutoCommit(false);
      TestMySqlDatabase.revokeSchemaUser(connMySql, 
        "mysql", _sDB_USER);
      connMySql.commit();
      connMySql.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* tearDownClass */

	@Before
	public void setUp() throws Exception {
		try 
		{
			MySqlDataSource dsMySql = new MySqlDataSource();
			dsMySql.setUrl(_sDB_URL);
			dsMySql.setUser(_sDB_USER);
			dsMySql.setPassword(_sDB_PASSWORD);
			MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
			connMySql.setAutoCommit(false);
			_dmdMySql = (MySqlDatabaseMetaData) connMySql.getMetaData();
			setDatabaseMetaData(_dmdMySql);
		}
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* setUp */

	@Test
	public void testClass() {
		assertEquals("Wrong result set meta class!", MySqlDatabaseMetaData.class, _dmdMySql.getClass());
	} /* testClass */

	@Test
	@Override
	public void testGetTypeInfo() 
	{
		enter();
		try {	print(_dmdMySql.getTypeInfo());	}
		catch (SQLException se) {	fail(EU.getExceptionMessage(se));	}
	} /* getTypeInfo */

  private void testColumns(QualifiedId qiTable,List<TestColumnDefinition>listCd)
  {
    try
    {
      Map<String,TestColumnDefinition> mapCd = new HashMap<String,TestColumnDefinition>();
      for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
      {
        TestColumnDefinition tcd = listCd.get(iColumn);
        mapCd.put(tcd.getName(), tcd);
      }
      ResultSet rs = _dmdMySql.getColumns(
        qiTable.getCatalog(), 
        _dmdMySql.toPattern(qiTable.getSchema()),
        _dmdMySql.toPattern(qiTable.getName()),
        "%");
      if ((rs != null) && (!rs.isClosed()))
      {
        while (rs.next())
        {
          String sCatalogName = rs.getString("TABLE_CAT");
          String sSchemaName = rs.getString("TABLE_SCHEM");
          String sTableName = rs.getString("TABLE_NAME");
          if (sCatalogName != null)
          {
            if (!_sDB_CATALOG.equalsIgnoreCase(sCatalogName))
              fail("Unexpected catalog: "+sCatalogName);
          }
          if (!qiTable.getSchema().equals(sSchemaName.toUpperCase()))
            fail("Unexpected schema: "+sSchemaName);
          if (!qiTable.getName().equals(sTableName.toUpperCase()))
            fail("Unexpected table: "+sTableName);
          String sColumnName = rs.getString("COLUMN_NAME");
          int iDataType = rs.getInt("DATA_TYPE");
          String sTypeName = rs.getString("TYPE_NAME");
          long lColumnSize = rs.getLong("COLUMN_SIZE");
          // int iDecimalDigits = rs.getInt("DECIMAL_DIGITS");
          
          // Extract base type name
          String sBaseTypeName = sTypeName.toLowerCase();
          int iParenIndex = sBaseTypeName.indexOf('(');
          if (iParenIndex > 0) {
            sBaseTypeName = sBaseTypeName.substring(0, iParenIndex);
          }

          switch(sBaseTypeName)
          {
            case "char": assertEquals("Invalid char mapping!", Types.CHAR, iDataType); break;
            case "varchar": assertEquals("Invalid varchar mapping!", Types.VARCHAR, iDataType); break;
            case "text": assertEquals("Invalid text mapping!", Types.CLOB, iDataType); break;
            case "tinytext": assertEquals("Invalid tinytext mapping!", Types.VARCHAR, iDataType); break;
            case "mediumtext": assertEquals("Invalid mediumtext mapping!", Types.CLOB, iDataType); break;
            case "longtext": assertEquals("Invalid longtext mapping!", Types.CLOB, iDataType); break;
            case "binary": assertEquals("Invalid binary mapping!", Types.BINARY, iDataType); break;
            case "varbinary": assertEquals("Invalid varbinary mapping!", Types.VARBINARY, iDataType); break;
            case "blob": assertEquals("Invalid blob mapping!", Types.BLOB, iDataType); break;
            case "tinyblob": assertEquals("Invalid tinyblob mapping!", Types.VARBINARY, iDataType); break;
            case "mediumblob": assertEquals("Invalid mediumblob mapping!", Types.BLOB, iDataType); break;
            case "longblob": assertEquals("Invalid longblob mapping!", Types.BLOB, iDataType); break;
            case "int": assertEquals("Invalid int mapping!", Types.INTEGER, iDataType); break;
            case "int unsigned": assertEquals("Invalid int unsigned mapping!", Types.BIGINT, iDataType); break;
            case "tinyint": assertEquals("Invalid tinyint mapping!", Types.SMALLINT, iDataType); break;
            case "tinyint unsigned": assertEquals("Invalid tinyint unsigned mapping!", Types.SMALLINT, iDataType); break;
            case "smallint": assertEquals("Invalid smallint mapping!", Types.SMALLINT, iDataType); break;
            case "smallint unsigned": assertEquals("Invalid smallint unsigned mapping!", Types.INTEGER, iDataType); break;
            case "mediumint": assertEquals("Invalid mediumint mapping!", Types.INTEGER, iDataType); break;
            case "mediumint unsigned": assertEquals("Invalid mediumint unsigned mapping!", Types.BIGINT, iDataType); break;
            case "bigint": assertEquals("Invalid bigint mapping!", Types.BIGINT, iDataType); break;
            case "bigint unsigned": assertEquals("Invalid bigint unsigned mapping!", Types.BIGINT, iDataType); break;
            case "decimal": assertEquals("Invalid decimal mapping!", Types.DECIMAL, iDataType); break;
            case "numeric": assertEquals("Invalid numeric mapping!", Types.DECIMAL, iDataType); break;
            case "real": assertEquals("Invalid real mapping!", Types.REAL, iDataType); break;
            case "float": assertEquals("Invalid float mapping!", Types.FLOAT, iDataType); break;
            case "double": assertEquals("Invalid double mapping!", Types.DOUBLE, iDataType); break;
            case "bit":
              if (sTypeName.equalsIgnoreCase("bit(1)")) {
                assertEquals("Invalid bit mapping!", Types.BOOLEAN, iDataType);
              } else {
                assertEquals("Invalid multibit mapping!", Types.BINARY, iDataType);
              }
              break;
            case "bool": assertEquals("Invalid bool mapping!", Types.BOOLEAN, iDataType); break;
            case "date": assertEquals("Invalid date mapping!", Types.DATE, iDataType); break;
            case "time": assertEquals("Invalid time mapping!", Types.TIME, iDataType); break;
            case "timestamp": assertEquals("Invalid timestamp mapping!", Types.TIMESTAMP, iDataType); break;
            case "datetime": assertEquals("Invalid datetime mapping!", Types.TIMESTAMP, iDataType); break;
            case "year": assertEquals("Invalid year mapping!", Types.SMALLINT, iDataType); break;
            case "geometry": assertEquals("Invalid geometry mapping!", Types.CLOB, iDataType); break;
            case "point": assertEquals("Invalid point mapping!", Types.CLOB, iDataType); break;
            case "linestring": assertEquals("Invalid linestring mapping!", Types.CLOB, iDataType); break;
            case "polygon": assertEquals("Invalid polygon mapping!", Types.CLOB, iDataType); break;
            case "multipoint": assertEquals("Invalid multipoint mapping!", Types.CLOB, iDataType); break;
            case "multilinestring": assertEquals("Invalid multilinestring mapping!", Types.CLOB, iDataType); break;
            case "multipolygon": assertEquals("Invalid multipolygon mapping!", Types.CLOB, iDataType); break;
            case "geometrycollection": assertEquals("Invalid geometrycollection mapping!", Types.CLOB, iDataType); break;
            // new with mysql 8.0
            case "geomcollection": assertEquals("Invalid geometrycollection mapping!", Types.CLOB, iDataType); break;
            case "enum": assertEquals("Invalid enum mapping!", Types.VARCHAR, iDataType); break;
            case "set": assertEquals("Invalid set mapping!", Types.VARCHAR, iDataType); break;
            default:
              fail("Invalid type " + sTypeName + "!");
              break;
          }
          TestColumnDefinition tcd = mapCd.get(sColumnName);
          String sType = tcd.getType();
          if (!sType.startsWith("INTERVAL"))
          {
            // parse type
            Matcher matcher = _patTYPE.matcher(sBaseTypeName);
            if (matcher.matches())
            {
              /* compare column size with explicit precision */
              String sPrecision = matcher.group(4);
              if (sPrecision != null)
              {
                int iPrecision = Integer.parseInt(sPrecision);
                if ((iDataType == Types.DOUBLE) ||
                    (iDataType == Types.FLOAT) ||
                    (iDataType == Types.REAL) ||
                    (iDataType == Types.CLOB) ||
                    (iDataType == Types.BLOB) ||
                    (iDataType == Types.TIMESTAMP) ||
                    (iDataType == Types.TIME) ||
                    (sTypeName.startsWith("datetimeoffset")))
                {
                  assertTrue("Explicit precision too large!",(iPrecision <= lColumnSize));
                  lColumnSize = iPrecision;
                }
                assertEquals("Explicit precision does not match!",iPrecision,lColumnSize);
              }
            }
          }
        }
      }
      else 
        fail("Invalid column meta data result set!");
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testColumns */
  
  @Test
  public void testColumnsMySqlSimple()
  {
    enter();
    testColumns(TestMySqlDatabase.getQualifiedSimpleTable(),TestMySqlDatabase._listCdSimple);
  } /* testColumnsMySqlSimple */
  
  @Test
  public void testColumnsMySqlComplex()
  {
    enter();
    testColumns(TestMySqlDatabase.getQualifiedComplexTable(),TestMySqlDatabase._listCdComplex);
  } /* testColumnsMySqlComplex */
  
  @Test
  public void testColumnsSqlSimple()
  {
    enter();
    testColumns(TestSqlDatabase.getQualifiedSimpleTable(),TestSqlDatabase._listCdSimple);
  } /* testColumnsSqlSimple */
  
  @Test
  public void testColumnsSqlComplex()
  {
    enter();
    testColumns(TestSqlDatabase.getQualifiedComplexTable(),TestSqlDatabase._listCdComplex);
  } /* testColumnsSqlSimple */

  @Test
  @Override
  public void testGetTableTypes()
  {
    enter();
    try { print(_dmdMySql.getTableTypes()); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  @Override
  public void testGetProcedures()
  {
    enter();
    try { print(_dmdMySql.getProcedures(null,TestSqlDatabase._sTEST_SCHEMA,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  @Override
  public void testGetProcedureColumns()
  {
    enter();
    try { print(_dmdMySql.getProcedureColumns(null,TestSqlDatabase._sTEST_SCHEMA,"%","%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  @Test
  @Override
  public void testGetTables()
  {
    enter();
    try { print(_dmdMySql.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", new String[] {"TABLE"})); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  @Test
  public void testGetViews()
  {
    enter();
    try { print(_dmdMySql.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", new String[] {"VIEW"})); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  @Override
  public void testGetUDTs()
  {
    enter();
    try { print(_dmdMySql.getUDTs(null,"%","%",null)); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  @Override
  public void testGetAttributes()
  {
    enter();
    try { print(_dmdMySql.getAttributes(null, TestSqlDatabase._sTEST_SCHEMA, "%", "%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  @Override
  public void testGetPrimaryKeys()
  {
    enter();
    QualifiedId qiTable = TestMySqlDatabase.getQualifiedSimpleTable();
    try { print(_dmdMySql.getPrimaryKeys(qiTable.getCatalog(),qiTable.getSchema(),qiTable.getName())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  @Override
  public void testGetImportedKeys()
  {
    enter();
    QualifiedId qiTable = TestSqlDatabase.getQualifiedComplexTable();
    try { print(_dmdMySql.getImportedKeys(qiTable.getCatalog(),qiTable.getSchema(),qiTable.getName())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  @Override
  public void testGetExportedKeys()
  {
    enter();
    QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
    try { print(_dmdMySql.getExportedKeys(qiTable.getCatalog(),qiTable.getSchema(),qiTable.getName())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  @Override
  public void testGetCrossReference()
  {
    enter();
    try { print(_dmdMySql.getCrossReference(null,TestSqlDatabase._sTEST_SCHEMA,"%",null,TestSqlDatabase._sTEST_SCHEMA,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetColumnPrivileges()
  {
    enter();
    try { print(_dmdMySql.getColumnPrivileges(null,null,"%","%")); } 
    /* do not fail: MySql throws exception because of insufficient privileges of testuser */
    catch(SQLException se) { System.err.println(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetVersionColumns()
  {
    enter();
    QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
    try { print(_dmdMySql.getVersionColumns(
      qiTable.getCatalog(),
      qiTable.getSchema(),
      qiTable.getName())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetBestRowIdentifier()
  {
    enter();
    QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
    try { print(_dmdMySql.getBestRowIdentifier(
      qiTable.getCatalog(),
      qiTable.getSchema(),
      qiTable.getName(), 
      DatabaseMetaData.bestRowUnknown,true)); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  /***
  @Test
  public void testNullable()
  {
    /* this is from a bug report, so we need another database
     * This may be necessary but doesn't change things: 
     * grant all on mysql.* to 'asuser'@'localhost'
     
    try 
    {
      MySqlDataSource dsMySql = new MySqlDataSource();
      String sCatalog = "archivespace";
      dsMySql.setUrl(MySqlDriver.getUrl(_cp.getHost() + ":" + _cp.getPort()+"/"+sCatalog));
      dsMySql.setUser("asuser");
      dsMySql.setPassword("aspwd");
      MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
      connMySql.setAutoCommit(false);
      _dmdMySql = (MySqlDatabaseMetaData) connMySql.getMetaData();
      setDatabaseMetaData(_dmdMySql);
      /* now to test ... 
      ResultSet rs = getDatabaseMetaData().getColumns(null, sCatalog, "resource", "ead_id");
      if (rs.next())
      {
        String sNullable = rs.getString("IS_NULLABLE");
        assertEquals("Wrong ISO nullability!","YES",sNullable);
        int iNullable = rs.getInt("NULLABLE");
        assertEquals("Wrong nullability!",DatabaseMetaData.columnNullable,iNullable);
      }
      else
        fail("Column ead_id in table resource in catalog archivespace not found!");
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    
  }
  ***/
} /* class MySqlDatabaseMetaData */
