package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresConnectionTester extends BaseConnectionTester
{
  private static final PostgreSQLContainer<?> _pg = new PostgreSQLContainer<>("postgres:16-alpine")
    .withDatabaseName("postgres")
    .withUsername("postgres")
    .withPassword("postgres");

  private static String _sDB_URL;
  private static String _sDB_USER;
  private static String _sDB_PASSWORD;

  @BeforeClass
  public static void setUpClass()
  {
    _pg.start();
    _sDB_URL = PostgresDriver.getUrl(_pg.getHost()+":"+_pg.getFirstMappedPort()+"/"+_pg.getDatabaseName());
    _sDB_USER = _pg.getUsername();
    _sDB_PASSWORD = _pg.getPassword();
  }

  @AfterClass
  public static void tearDownClass()
  {
    _pg.stop();
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
      setConnection(connPostgres);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUp */
  
  @Test
  public void testClass()
  {
    assertEquals("Wrong connection class!", PostgresConnection.class, getConnection().getClass());
  } /* testClass */

  @Override
  @Test
  public void testCreateArrayOf()
  {
    enter();
    try 
    {
      /* For some reason VARCHAR is OK, but VARCHAR(255) is not. */
      Array array = getConnection().createArrayOf("VARCHAR", new String[] {"a", "bc", "abc"});
      array.free();
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* createArrayOf */
  
  @Override
  @Test
  public void testPrepareStatement_String_AInt()
  {
    enter();
    /* should really throw feature not supported */ 
    try { getConnection().prepareStatement(_sSQL, new int[] {1,2}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* testPrepareStatement_String_AInt */
  
} /* class PostgresConnectionTester */
