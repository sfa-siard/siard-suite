package ch.admin.bar.siard2.jdbcx;

import java.sql.*;
import javax.sql.DataSource;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.base.*;
import ch.admin.bar.siard2.jdbc.*;

public class PostgresDataSourceTester
{
  // see https://jdbc.postgresql.org/documentation/head/connect.html
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = "jdbc:postgresql://" + _cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog();
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
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
