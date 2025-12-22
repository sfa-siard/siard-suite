package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

import ch.enterag.utils.base.ConnectionProperties;

public class MsSqlDriverTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = MsSqlDriver.getUrl(_cp.getHost()+":"+_cp.getPort()+";databaseName="+_cp.getCatalog());
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private static final String sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.MsSqlDriver";
  private static final String sTEST_MSSQL_URL = "jdbc:sqlserver://localhost";
  private static final String sINVALID_MSSQL_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";;
  
  private Driver _driver = null;
  private Connection _conn = null;
  
  @Before
  public void setUp()
  {
    try { Class.forName(sDRIVER_CLASS); }
    catch(ClassNotFoundException cnfe) { fail(cnfe.getClass().getName()+": "+cnfe.getMessage()); }
    try
    {
      _driver = DriverManager.getDriver(sTEST_MSSQL_URL);
      _conn = DriverManager.getConnection(_sDB_URL, _sDB_USER, _sDB_PASSWORD);
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* setUp */
  
  @After
  public void tearDown()
  {
    try
    {
      if ((_conn != null) && (!_conn.isClosed()))
        _conn.close();
      else
        fail("Connection cannot be closed!");
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* tearDown */

  @Test
  public void testWrapping()
  {
    assertSame("Registration of driver wrapper failed!", MsSqlDriver.class, _driver.getClass());
    assertSame("Choice of connection wrapper failed!", MsSqlConnection.class, _conn.getClass());
  } /* testWrapping */
  
  @Test
  public void testCompliant()
  {
    assertSame("MSSQL driver not JDBC compliant!", true, _driver.jdbcCompliant());
  } /* testCompliant */
  
  @Test
  public void testAcceptsURL()
  {
    try
    {
      assertSame("Valid MSSQL URL not accepted!", true, _driver.acceptsURL(_sDB_URL));
      assertSame("Invalid MSSQL URL accepted!", false, _driver.acceptsURL(sINVALID_MSSQL_URL));
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* testAcceptsURL */
  
  @Test
  public void testVersion()
  {
    int iMajorVersion = _driver.getMajorVersion();
    int iMinorVersion = _driver.getMinorVersion();
    String sVersion = String.valueOf(iMajorVersion)+"."+String.valueOf(iMinorVersion);
    assertEquals("Wrong MSSQL version "+sVersion+" found!", "4.2", sVersion);
  } /* testVersion */
  
  @Test
  public void testDriverProperties()
  {
    try
    {
      DriverPropertyInfo[] apropInfo = _driver.getPropertyInfo(_sDB_URL, new Properties());
      for (DriverPropertyInfo dpi: apropInfo)
        System.out.println(dpi.name+": "+dpi.value+" ("+String.valueOf(dpi.description)+")");
      assertSame("Unexpected driver properties!", 29, apropInfo.length);
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* testDriverProperties */

} /* MsSqlDriverTester */
