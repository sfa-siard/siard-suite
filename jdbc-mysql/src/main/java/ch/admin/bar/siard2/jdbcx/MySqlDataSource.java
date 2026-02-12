/*======================================================================
MySqlDataSource implements a wrapped MySql DataSource
Version     : $Id: $
Application : SIARD2
Description : MySqlDataSource implements a wrapped MySql DataSource.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 26.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbcx;

import java.sql.*;
import javax.sql.*;
import com.mysql.cj.jdbc.*;
import ch.enterag.utils.logging.*;
import ch.enterag.utils.jdbcx.*;
import ch.admin.bar.siard2.jdbc.*;

/* ===============================================================================- */
/**
 * MySqlDataSource implements a wrapped MySql DataSource
 * @author Simon Jutz
 */
public class MySqlDataSource extends BaseDataSource implements DataSource 
{
	/** logger */
	private static IndentLogger _il = IndentLogger.getIndentLogger(MySqlDataSource.class.getName());

	/**
	 * @param dsWrapped DataSource to be wrapped
	 */
	public MySqlDataSource() 
	  throws SQLException
	{
		super(new MysqlDataSource());
    com.mysql.cj.jdbc.MysqlDataSource ds = (com.mysql.cj.jdbc.MysqlDataSource)super.unwrap(DataSource.class);
    ds.setUseCursorFetch(true);
    ds.setDefaultFetchSize(1);
    ds.setEnableEscapeProcessing(false);
    ds.setProcessEscapeCodesForPrepStmts(false);
    ds.setSessionVariables("sql_mode='ANSI,NO_BACKSLASH_ESCAPES'");
	} /* constructor */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param sUrl JDBC URL identifying the database instance to connect to.
   * @param sUser database user.
   * @param sPassword password of database user.
   * @throws SQLException if a database error occurred.
   */
  public MySqlDataSource(String sUrl, String sUser, String sPassword)
    throws SQLException
  {
    super(new MysqlDataSource());
    setUrl(sUrl);
    setUser(sUser);
    setPassword(sPassword);
    com.mysql.cj.jdbc.MysqlDataSource ds = (com.mysql.cj.jdbc.MysqlDataSource)super.unwrap(DataSource.class);
    ds.setUseCursorFetch(true);
    ds.setDefaultFetchSize(1);
    ds.setEnableEscapeProcessing(false);
    ds.setProcessEscapeCodesForPrepStmts(false);
    ds.setSessionVariables("sqlmode='ANSI,NO_BACKSLASH_ESCAPES'");
  } /* constructor */
	
	/* ------------------------------------------------------------------------ */
	/**
	 * Returns a appropriately wrapped MySql Connection
	 */
	public Connection getConnection() throws SQLException {
		return new MySqlConnection(super.getConnection());
	} /* getConnection */
	
	/* ------------------------------------------------------------------------ */
	/**
	 * Returns the unwrapped MySql DataSource
	 * @return the unwrapped MySql DataSource
	 */
	private MysqlDataSource getUnwrapped() {
		MysqlDataSource msds = null;
	    try { msds = (MysqlDataSource)unwrap(DataSource.class); }
	    catch(SQLException se) { _il.exception(se); }
	    return msds;
	} /* getUnwrapped */
	
	/* ------------------------------------------------------------------------ */
	/** 
	 * Get the database name to be used in the connection
	 * @return database name to be used in the connection
	 */
	public String getDatabaseName() {
		return getUnwrapped().getDatabaseName();
	} /* getDatabaseName */
	
	/* ------------------------------------------------------------------------ */
	/**
	 * Get the URL to be used in the connection
	 * @return URL to be used in the connection
	 */
	public String getUrl() {
		return getUnwrapped().getUser();
	} /* getUrl */
	
	/* ------------------------------------------------------------------------ */
	/**
	 * Get the user to be used in the connection
	 * @return the user to be used in the connection
	 */
	public String getUser() {
		return getUnwrapped().getUser();
	} /* getUser */
	
	/* ------------------------------------------------------------------------ */
	/**
	 * Set the database name to be used in the connection
	 * @param dbName database name to be used in the connection
	 */
	public void setDatabaseName(String dbName) {
		getUnwrapped().setDatabaseName(dbName);
	} /* setDatabaseName */
	
	/* ------------------------------------------------------------------------ */
	/**
	 * Set the password to be used in the connection
	 * @param password the password to be used in the connection
	 */
	public void setPassword(String password) {
		getUnwrapped().setPassword(password);
	} /* setPassword */
	
	/* ------------------------------------------------------------------------ */
	/**
	 * Set the URL to be used in the connection
	 * @param url the URL to be used in the connection (e.g. "jdbc://mysql:localhost:3306/testdb")
	 */
	public void setUrl(String url) {
		getUnwrapped().setUrl(url);
	} /* setUrl */
	
	/* ------------------------------------------------------------------------ */
	/**
	 * Set the user to use in connection
	 * @param user the user to use in connection
	 */
	public void setUser(String user) {
		getUnwrapped().setUser(user);
	} /* setUser */
} /* class MySqlDataSource */
