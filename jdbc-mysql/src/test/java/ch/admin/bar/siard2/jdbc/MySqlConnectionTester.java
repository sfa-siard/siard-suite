package ch.admin.bar.siard2.jdbc;

import java.sql.*;

import static org.junit.Assert.*;

import org.junit.*;

import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.mysql.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;

public class MySqlConnectionTester extends BaseConnectionTester
{
	private static final ConnectionProperties _cp = new ConnectionProperties();
	private static final String _sDB_URL = MySqlDriver.getUrl(_cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog(),true);
	private static final String _sDB_USER = _cp.getUser();
	private static final String _sDB_PASSWORD = _cp.getPassword();
	private static final String _sDB_CATALOG = _cp.getCatalog();
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();

	private MySqlConnection _connMySql = null;
	  
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
      new TestMySqlDatabase(connMySql);
      TestMySqlDatabase.grantSchemaUser(connMySql, 
        TestMySqlDatabase._sTEST_SCHEMA, _sDB_USER);
      new TestSqlDatabase(connMySql);
      TestMySqlDatabase.grantSchemaUser(connMySql, 
        TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
  		connMySql.close();
    }
    catch(SQLException se) {
		  se.printStackTrace();
		  fail(EU.getExceptionMessage(se));
	  }
	} /* setUpClass */
	
	@Before
	public void setUp() 
	{
    try 
    {
  		MySqlDataSource dsMySql = new MySqlDataSource();
  		dsMySql.setUrl(_sDB_URL);
  		dsMySql.setUser(_sDB_USER);
  		dsMySql.setPassword(_sDB_PASSWORD);
			_connMySql = (MySqlConnection) dsMySql.getConnection();
			_connMySql.setAutoCommit(false);
			setConnection(_connMySql);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* setUp */
	
	@Test
	public void testClass() 
	{
	  assertEquals("Wrong connection class!", MySqlConnection.class, _connMySql.getClass());
	} /* testClass */
	
  @Test
  @Override
  public void testGetMetadata()
  {
    enter();
    try 
    {
      DatabaseMetaData dmd = _connMySql.getMetaData();
      assertEquals("Wrong metadata class!", MySqlDatabaseMetaData.class, dmd.getClass());
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetMetadata */

  @Test
  @Override
  public void testSetCatalog() 
  {
    try { _connMySql.setCatalog(_sDB_CATALOG); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetCatalog */
  
} /* class MySqlConnectionTester */
