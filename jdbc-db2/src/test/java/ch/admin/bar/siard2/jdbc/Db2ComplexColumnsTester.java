package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.admin.bar.siard2.db2.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;

@Ignore("was not part of the original TestSuite")
public class Db2ComplexColumnsTester extends BaseDatabaseMetaDataTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = "jdbc:db2://"+_cp.getHost()+":"+_cp.getPort()+"/"+_cp.getCatalog();
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();

  private Db2DatabaseMetaData _dmdDb2 = null;
  
  @BeforeClass
  public static void setUpClass()
  {
    try 
    { 
      Db2DataSource dsDb2 = new Db2DataSource();
      dsDb2.setUrl(_sDB_URL);
      dsDb2.setUser(_sDBA_USER);
      dsDb2.setPassword(_sDBA_PASSWORD);
      Db2Connection connDb2 = (Db2Connection)dsDb2.getConnection();
      /* drop and create the test database granting access to _sDB_USER */
      new TestSqlDatabase(connDb2,_sDB_USER);
      new TestDb2Database(connDb2,_sDB_USER);
      connDb2.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUpClass */
  
  @Before
  public void setUp()
  {
    try 
    { 
      Db2DataSource dsDb2 = new Db2DataSource();
      dsDb2.setUrl(_sDB_URL);
      dsDb2.setUser(_sDB_USER);
      dsDb2.setPassword(_sDB_PASSWORD);
      Connection conn = dsDb2.getConnection();
      conn.setAutoCommit(false);
      _dmdDb2 = (Db2DatabaseMetaData)conn.getMetaData();
      setDatabaseMetaData(_dmdDb2);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUp */
  
  @Test
  public void testGetProcedures()
  {
    enter();
    try { print(_dmdDb2.getProcedures(null,"%","%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

}
