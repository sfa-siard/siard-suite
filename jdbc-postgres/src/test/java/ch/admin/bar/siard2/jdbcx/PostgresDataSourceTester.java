package ch.admin.bar.siard2.jdbcx;

import java.sql.*;
import javax.sql.DataSource;

import static org.junit.Assert.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresDataSourceTester
{
  // see https://jdbc.postgresql.org/documentation/head/connect.html
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
    _sDB_URL = "jdbc:postgresql://" + _pg.getHost() + ":" + _pg.getFirstMappedPort() + "/" + _pg.getDatabaseName();
    _sDB_USER = _pg.getUsername();
    _sDB_PASSWORD = _pg.getPassword();
  }

  @AfterClass
  public static void tearDownClass()
  {
    _pg.stop();
  }
  private PostgresDataSource _dsPostgres = null;
  private Connection _conn = null;

  @Before
  public void setUp()
  {
    _dsPostgres = new PostgresDataSource();
  } /* setUp */
  
  @After
  public void tearDown()
  {
    try
    {
      if ((_conn != null) && (!_conn.isClosed()))
        _conn.close();
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* tearDown */
  
  @Test
  public void testWrapper()
  {
    try
    {
      Assert.assertSame("Invalid wrapper!", true, _dsPostgres.isWrapperFor(DataSource.class));
      DataSource dsWrapped = _dsPostgres.unwrap(DataSource.class);
      assertSame("Invalid wrapped class!", org.postgresql.ds.PGSimpleDataSource.class, dsWrapped.getClass());
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* testWrapper */
  
  @Test
  public void testGetConnection()
  {
    _dsPostgres.setUrl(_sDB_URL);
    _dsPostgres.setUser(_sDB_USER);
    _dsPostgres.setPassword(_sDB_PASSWORD);
    try 
    {
      _conn = _dsPostgres.getConnection();
      if (_conn.unwrap(Connection.class) instanceof PostgresConnection)
        fail("Double wrap!");
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* testConnection */
  
}
