/**
 * 
 */
package ch.admin.bar.siard2.jdbc.legacy;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import ch.admin.bar.siard2.jdbc.OracleConnection;
import ch.admin.bar.siard2.jdbc.OracleDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.testcontainers.containers.OracleContainer;

/**
 * @author jutzs
 *
 */
public class OracleDriverTester {

 	private static final String sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.OracleDriver";
  private static final String sTEST_ORACLE_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";;
 	private static final String sINVALID_ORACLE_URL = "jdbc:sqlserver://localhost";
 	
 	private Driver _driver = null;
 	private Connection _conn = null;


	@ClassRule
	public final static OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");
 	
	@Before
	public void setUp() {
		try 
		{
			Class.forName(sDRIVER_CLASS); 
			_driver = DriverManager.getDriver(sTEST_ORACLE_URL);
			_conn = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword());
		} catch(ClassNotFoundException cnfe) { fail(cnfe.getClass().getName()+": " + cnfe.getMessage());
		} catch(SQLException se) { fail(se.getClass().getName()+": " + se.getMessage()); }
	} /* setUp */

	@After
	public void tearDown() {
		try
		{
			if ((_conn != null) && (!_conn.isClosed())) {
				_conn.close();
			} else {
				fail("Connection cannot be closed!");
			}
		} catch(SQLException se) { fail(se.getClass().getName()+": " + se.getMessage()); }
	} /* tearDown */

	  @Test
	public void testWrapping()
	{
		  assertSame("Registration of driver wrapper failed!", OracleDriver.class, _driver.getClass());
		  assertSame("Choice of connection wrapper failed!", OracleConnection.class, _conn.getClass());
	} /* testWrapping */
	  
	@Test
	public void testCompliant()
	{
		assertSame("Oracle driver not JDBC compliant!", true, _driver.jdbcCompliant());
	} /* testCompliant */

	@Test
	public void testAcceptsURL()
	{
		try
	    {
			assertSame("Valid Oracle URL not accepted!", true, _driver.acceptsURL(db.getJdbcUrl()));
			assertSame("Invalid Oracle URL accepted!", false, _driver.acceptsURL(sINVALID_ORACLE_URL));
	    }
		catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
	} /* testAcceptsURL */

	@Test
	public void testVersion()
	{
		int iMajorVersion = _driver.getMajorVersion();
		int iMinorVersion = _driver.getMinorVersion();
		String sVersion = String.valueOf(iMajorVersion)+"."+String.valueOf(iMinorVersion);
		assertEquals("Wrong Oracle version "+sVersion+" found!", "12.1", sVersion);
	} /* testVersion */
	  
	@Test
	public void testDriverProperties()
	{
		try
		{
			DriverPropertyInfo[] apropInfo = _driver.getPropertyInfo(db.getJdbcUrl(), new Properties());
			assertSame("Unexpected driver properties!", 99, apropInfo.length);
		}
		catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
	} /* testDriverProperties */
}
