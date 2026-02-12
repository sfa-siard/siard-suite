/*======================================================================
OracleDataSource implements a wrapped Oracle DataSource.
Version     : $Id: $
Application : SIARD2
Description : OracleDataSource implements a wrapped Oracle DataSource.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rueti ZH, Switzerland
Created    : 15.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbcx;

import javax.sql.DataSource;
import java.sql.*;

import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.jdbcx.*;
import ch.enterag.utils.logging.*;

/** OracleDataSource implements a wrapped Oracle DataSource.
 * @author Simon Jutz
 */
public class OracleDataSource
	extends BaseDataSource
	implements DataSource
{
	/** logger */
	private static IndentLogger _il = IndentLogger.getIndentLogger(OracleDataSource.class.getName());
	static
	{
	  System.setProperty("oracle.jdbc.getObjectReturnsXMLType", "false");
	}
	
	/*------------------------------------------------------------------*/
	/** constructor 
	 * @throws SQLException */	  
	public OracleDataSource() throws SQLException 
	{
	  super(new oracle.jdbc.pool.OracleDataSource()); 
	} /* constructor OracleDataSource */
	
  /*------------------------------------------------------------------*/
  /** constructor
   * @param sUrl JDBC URL identifying the database instance to connect to.
   * @param sUser database user.
   * @param sPassword password of database user.
   * @throws SQLException if a database error occurred.
   */
	public OracleDataSource(String sUrl, String sUser, String sPassword)
	  throws SQLException
	{
    super(new oracle.jdbc.pool.OracleDataSource()); 
	  setUrl(sUrl);
	  setUser(sUser);
	  setPassword(sPassword);
	} /* constructor */
	
	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * returns the appropriately wrapped Oracle Connection.
	 */
	@Override
	public Connection getConnection() throws SQLException
	{
		return new OracleConnection(super.getConnection());
	} /* getConnection */

	/*------------------------------------------------------------------*/
	/** @return unwrapped Oracle DataSource
	 */
	private oracle.jdbc.pool.OracleDataSource getUnwrapped()
	{
		oracle.jdbc.pool.OracleDataSource ssds = null;
    try { ssds = (oracle.jdbc.pool.OracleDataSource)unwrap(DataSource.class); }
    catch(SQLException se) { _il.exception(se); }
    return ssds;
	} /* getUnwrapped */

	/*------------------------------------------------------------------*/
	/** set URL to be used for connection.
	 * @param url URL (format: "jjdbc:oracle:thin:@<databasehost>:<databasePort>:<databaseSID>")
	 */
	public void setUrl(String url)
	{
		getUnwrapped().setURL(url);
	} /* setUrl */
	  
	/*------------------------------------------------------------------*/
	/** @return URL used for connection.
	 *  @throws SQLException 
	 */
	public String getUrl() throws SQLException
	{
		return getUnwrapped().getURL();
	} /* getUrl */
	  
	/*------------------------------------------------------------------*/
	/** set database user to be used for connection.
	 * @param user database user to be used for connection.
	 */
	public void setUser(String user)
	{
		getUnwrapped().setUser(user);
	} /* setUser */
	  
	/*------------------------------------------------------------------*/
	/** @return database user used for connection.
	 */
	public String getUser()
	{
		return getUnwrapped().getUser();
	} /* getUser */
	  
	/*------------------------------------------------------------------*/
	/** set database password to be used for connection.
	 * @param user database password to be used for connection.
	 */
	public void setPassword(String password)
	{
		getUnwrapped().setPassword(password);
	} /* setPassword */
	  
	/*------------------------------------------------------------------*/
	/** set database description.
	 * @param description database description.
	 */
	public void setDescription(String description)
	{
		getUnwrapped().setDescription(description);
	} /* setDescription */
	  
	/*------------------------------------------------------------------*/
	/** @return database description.
	 */
	public String getDescription()
	{
		return getUnwrapped().getDescription();
	} /* getDescription */
	  
} /* class MsSqlDataSource */
