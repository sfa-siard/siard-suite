package ch.admin.bar.siard2.jdbcx;

import java.sql.*;
import javax.sql.*;
import static org.junit.Assert.*;
import org.junit.*;

import ch.enterag.utils.base.ConnectionProperties;

public class MsSqlDataSourceTester
{
  // see https://msdn.microsoft.com/en-us/library/ms378428(v=sql.110).aspx
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = "jdbc:sqlserver://"+_cp.getHost()+":"+_cp.getPort()+";databaseName="+_cp.getCatalog();
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();

  private MsSqlDataSource _dsMsSql = null;
  private Connection _conn = null;
  
  @Before
  public void setUp()
  {
    _dsMsSql = new MsSqlDataSource();
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
      Assert.assertSame("Invalid wrapper!", true, _dsMsSql.isWrapperFor(DataSource.class));
      DataSource dsWrapped = _dsMsSql.unwrap(DataSource.class);
      assertSame("Invalid wrapped class!", com.microsoft.sqlserver.jdbc.SQLServerDataSource.class, dsWrapped.getClass());
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* testWrapper */
  
  @Test
  public void testLoginTimeout()
  {
    try
    {
      int iLoginTimeout = _dsMsSql.getLoginTimeout();
      assertSame("Unexpected login timeout "+String.valueOf(iLoginTimeout)+"!", 15, iLoginTimeout);
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* testLoginTimeout */
  
  @Test
  public void testConnection()
  {
    _dsMsSql.setUrl(_sDB_URL);
    _dsMsSql.setUser(_sDB_USER);
    _dsMsSql.setPassword(_sDB_PASSWORD);
    try { _conn = _dsMsSql.getConnection(); }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* testConnection */
  
} /* MsSqlDataSource */
