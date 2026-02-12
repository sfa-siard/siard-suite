/*======================================================================
OracleDriver implements a wrapped MSSQL Oracle.
Version     : $Id: $
Application : SIARD2
Description : OracleDriver implements a wrapped Oracle Driver.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rueti ZH, Switzerland
Created    : 16.06.2016, Simon Jutz
======================================================================*/

package ch.admin.bar.siard2.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import ch.enterag.utils.jdbc.BaseDriver;
import ch.enterag.utils.logging.IndentLogger;

/*====================================================================*/
/** OracleDriver implements a wrapped Oracle Driver.
 * @author Simon Jutz
 */
public class OracleDriver
	extends BaseDriver
	implements Driver
{
  static { System.setProperty("oracle.jdbc.getObjectReturnsXMLType", "false"); }
	/** logger */
	private static IndentLogger _il = IndentLogger.getIndentLogger(OracleDriver.class.getName());
  /** protocol sub scheme for Oracle JDBC URL */
  public static final String sORACLE_SCHEME = "oracle";
  /** URL prefix for Oracle JDBC URL */
  public static final String sORACLE_URL_PREFIX = sJDBC_SCHEME+":"+sORACLE_SCHEME+":";
  /** URL for database name.
   * @param sDatabaseName host:port:sid, e.g. localhost:1521:orcl
   *                      or host:port/service 
   * @return JDBC URL for thin driver.
   */
  public static String getUrl(String sDatabaseName)
  {
    String sUrl = sDatabaseName;
    if (!sUrl.startsWith(sORACLE_URL_PREFIX))
      sUrl = sORACLE_URL_PREFIX + "thin:@"+sDatabaseName;
    return sUrl;
  } /* getUrl */


  /** register this driver, replacing original H2 driver
   */
  public static void register()
  {
    try { BaseDriver.register(new OracleDriver(), "oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@localhost:1521:orcl"); }
    catch(Exception e) { throw new Error(e); }
  } /* register */

	/** replace OracleDriver driver by this */
	static
	{
		register();
	}

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}*/
  @Override
  public boolean acceptsURL(String url) throws SQLException
  {
    _il.enter(url);
    boolean bAccepts = url.startsWith("jdbc:oracle:");
    _il.exit(bAccepts);
    return bAccepts;
  } /* acceptsUrl */
  
	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * returns the appropriately wrapped Oracle Connection.
	 */
	@Override
	public Connection connect(String url, Properties info)
		throws SQLException
	{
		_il.enter(url, info);
    Connection conn = super.connect(url, info);
		if (conn != null)
		conn = new OracleConnection(conn); 
    _il.exit(conn);
    return conn;
	} /* connect */
	  
} /* class OracleDriver */
