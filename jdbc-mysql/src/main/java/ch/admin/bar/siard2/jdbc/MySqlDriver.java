/*======================================================================
MySqlDriver implements a wrapped MySql Driver.
Version     : $Id: $
Application : SIARD2
Description : MySqlDriver implements a wrapped MySql Driver.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 26.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import ch.enterag.utils.jdbc.BaseDriver;
import ch.enterag.utils.logging.IndentLogger;

/* =============================================================================== */
/**
 * MySqlDriver implements a wrapped MySql Driver
 * @author Simon Jutz
 */
public class MySqlDriver extends BaseDriver implements Driver 
{
	private static IndentLogger _il = IndentLogger.getIndentLogger(MySqlDriver.class.getName());
  /** protocol sub scheme for MsSQL JDBC URL */
  public static final String sMYSQL_SCHEME = "mysql";
  /** URL prefix for MySQL JDBC URL */
  public static final String sMYSQL_URL_PREFIX = sJDBC_SCHEME+":"+sMYSQL_SCHEME+":";
  /** URL for database name.
   * @param sDatabaseName host:port/database, e.g. localhost:3306/testdb 
   * @return JDBC URL.
   */
  public static String getUrl(String sDatabaseName,boolean bNoSsl)
  {
    String sUrl = sDatabaseName;
    if (!sUrl.startsWith(sMYSQL_URL_PREFIX))
      sUrl = sMYSQL_URL_PREFIX+"//"+sDatabaseName;
    if (bNoSsl)
      sUrl = sUrl+"?verifyServerCertificate=false&useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true";
    return sUrl;
  } /* getUrl */

	/** register this driver, replacing original MySql driver
	 */
	public static void register()
	{
		// com.mysql.fabric.jdbc.FabricMySQLDriver is excluded in acceptsURL
		try { BaseDriver.register(new MySqlDriver(), "com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/testschema"); }
		catch(Exception e) { throw new Error(e); }
	}

	/** replace MySQL driver by this, when this class is loaded */
	static
	{
		register();
	}

	/* ------------------------------------------------------------------------ */
	/**
	 * {@inheritDoc}
	 * @return the appropriately wrapped Connection
	 */
	public Connection connect(String url, Properties info) throws SQLException {
		_il.enter(url, info);
		/* add/change properties */
		if (info == null)
		  info = new Properties();
		/* avoid huge JAVA memory for large BLOBs */
		info.setProperty("useCursorFetch", "true");
		info.setProperty("defaultFetchSize", "1");
		/* avoid escape processing */
		info.setProperty("enableEscapeProcessing", "false");
		info.setProperty("processEscapeCodesForPrepStmts", "false");
		/* disable backslash escapes */
		info.setProperty("sessionVariables", "sql_mode='ANSI,NO_BACKSLASH_ESCAPES'");
		Connection conn = super.connect(url, info);
		if (conn != null)
			conn = new MySqlConnection(conn);
		_il.exit(conn);
		return conn;
	} /* connect */

	/* ------------------------------------------------------------------------ */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean acceptsURL(String url) {
		_il.enter(url);
		boolean bAccepts = url.startsWith("jdbc:mysql:");
		_il.exit(bAccepts);
		return bAccepts;
	} /* acceptsURL */

} /* class MySqlDriver */