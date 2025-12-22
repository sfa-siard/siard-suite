/*======================================================================
MsSqlDataSource implements a wrapped MSSQL DataSource.
Version     : $Id: $
Application : SIARD2
Description : MsSqlDataSource implements a wrapped MSSQL DataSource.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbcx;

import java.sql.*;
import javax.sql.*;
import com.microsoft.sqlserver.jdbc.*;
import ch.enterag.utils.jdbcx.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.jdbc.*;

/*====================================================================*/
/** MsSqlDataSource implements a wrapped MSSQL DataSource.
 * @author Hartwig Thomas
 */
public class MsSqlDataSource
  extends BaseDataSource
  implements DataSource
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(MsSqlDataSource.class.getName());

  /*------------------------------------------------------------------*/
  /** constructor */
  public MsSqlDataSource()
  {
    super(new SQLServerDataSource());
    /**
    SQLServerDataSource ssds = (SQLServerDataSource)getUnwrapped();
    ssds.setSelectMethod("cursor");
    **/
  } /* constructor MsSqlDataSource */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param sUrl JDBC URL identifying the database instance to connect to.
   * @param sUser database user.
   * @param sPassword password of database user.
   * @throws SQLException if a database error occurred.
   */
  public MsSqlDataSource(String sUrl, String sUser, String sPassword)
    throws SQLException
  {
    super(new SQLServerDataSource()); 
    setUrl(sUrl);
    setUser(sUser);
    setPassword(sPassword);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * returns the appropriately wrapped MSSQL Connection.
   */
  @Override
  public Connection getConnection() throws SQLException
  {
    return new MsSqlConnection(super.getConnection());
  } /* getConnection */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * returns the appropriately wrapped MSSQL Connection.
   */
  @Override
  public Connection getConnection(String username, String password)
      throws SQLException
  {
    return new MsSqlConnection(super.getConnection(username, password));
  } /* getConnection */

  /*------------------------------------------------------------------*/
  /** @return unwrapped MSSQL DataSource
   */
  private SQLServerDataSource getUnwrapped()
  {
    SQLServerDataSource ssds = null;
    try { ssds = (SQLServerDataSource)unwrap(DataSource.class); }
    catch(SQLException se) { _il.exception(se); }
    return ssds;
  } /* getUnwrapped */

  /*------------------------------------------------------------------*/
  /** set URL to be used for connection.
   * @param url URL (format: "jdbc:h2:<database path>" results in file 
   *                 <database path>.h2.db)
   */
  public void setUrl(String url)
  {
    getUnwrapped().setURL(url);
  } /* setUrl */
  
  /*------------------------------------------------------------------*/
  /** @return URL used for connection.
   */
  public String getUrl()
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
  /** The password of the connection is private but not very well hidden. 
   * This method will be deleted again!
   * @return database password for connection.
  public String getPassword()
  {
    return (String)ch.enterag.utils.reflect.Glue.invokePrivate(getUnwrapped(), "getPassword", new Class<?>[]{}, new Object[]{});
  } /* getPassword */
  
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
  
  /*------------------------------------------------------------------*/
  /** set database name.
   * @param name database name.
   */
  public void setDatabaseName(String name)
  {
    ((SQLServerDataSource)getUnwrapped()).setDatabaseName(name);
  } /* setDatabaseName */
  
  /*------------------------------------------------------------------*/
  /** @return database name.
   */
  public String getDatabaseName()
  {
    return ((SQLServerDataSource)getUnwrapped()).getDatabaseName();
  } /* getDatabaseName */
  
} /* class MsSqlDataSource */
