package ch.admin.bar.siard2.jdbcx;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.admin.bar.siard2.jdbcx.MySqlDataSource;
import ch.enterag.utils.base.ConnectionProperties;

public class MySqlDataSourceTester {
	private static final ConnectionProperties _cp = new ConnectionProperties();
	
	private static final String _sDB_URL = "jdbc:mysql://" + _cp.getHost() + ":" + _cp.getPort();
	private static final String _sDB_USER = _cp.getUser();
	private static final String _sDB_PASSWORD = _cp.getPassword();
	
	private MySqlDataSource _dsMySql = null;
	private Connection _conn = null;
	
	@Before
	public void setUp() throws Exception {
		_dsMySql = new MySqlDataSource();
	} /* setUp */

	@After
	public void tearDown() throws Exception {
		if((_conn != null) && (!_conn.isClosed())) {
			_conn.close();
		}
	} /* tearDown */
	
	@Test
	public void testWrapper() 
	{
		try {
			Assert.assertSame("Invalid wrapper!", true, _dsMySql.isWrapperFor(DataSource.class));
			DataSource dsWrapped = _dsMySql.unwrap(DataSource.class);
			assertSame("Invalid wrapper class!", com.mysql.cj.jdbc.MysqlDataSource.class, dsWrapped.getClass());
		} catch(SQLException se) {
			fail(se.getClass().getName() + ": " + se.getMessage());
		}
	} /* testWrapper */

	@Test
	public void testConnection() 
	{
		_dsMySql.setUrl(_sDB_URL);
		_dsMySql.setUser(_sDB_USER);
		_dsMySql.setPassword(_sDB_PASSWORD);
		
		try {
			_conn = _dsMySql.getConnection();
		} catch (SQLException se) {
			fail(se.getClass().getName() + ": " + se.getMessage());
		}
	} /* testConnection */
	
	@Test
	public void testLoginTimeout()
	{
		try {
			int iLoginTimeout = _dsMySql.getLoginTimeout();
			assertSame("Unexpected login timeout " + String.valueOf(iLoginTimeout) + "!", iLoginTimeout, 0);
		} catch (SQLException se) {
			fail(se.getClass().getName() + ": " + se.getMessage());
		}
	} /* testLoginTimeout */
	
} /* class MySqlDataSourceTester */
