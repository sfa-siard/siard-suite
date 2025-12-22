/*======================================================================
Db2DataSource implements a wrapped DB/2 DataSource.
Version     : $Id: $
Application : SIARD2
Description : Db2DataSource implements a wrapped DB/2 DataSource.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbcx;

import java.sql.*;
import java.util.regex.*;
import javax.sql.*;
import com.ibm.db2.jcc.*;
import ch.enterag.utils.jdbcx.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.jdbc.*;

/*====================================================================*/
/** Db2DataSource implements a wrapped DB/2 DataSource.
 * @author Hartwig Thomas
 */
public class Db2DataSource
  extends BaseDataSource
  implements DataSource
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(Db2DataSource.class.getName());
  // see: http://www.ibm.com/support/knowledgecenter/SSEPGG_9.7.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_r0052342.html
  private static Pattern _patUrl = Pattern.compile("^jdbc:((db2:)|(db2j:net:)|(ids:))//(.*?)(:(\\d*))?/(.*)$");

  /*------------------------------------------------------------------*/
  /** constructor */
  public Db2DataSource()
    throws SQLException
  {
    super(new DB2SimpleDataSource());
    DB2SimpleDataSource dsd = (DB2SimpleDataSource)unwrap(DataSource.class);
    dsd.setDriverType(4);
    /***
    setEnableExtendedDescribe(DB2BaseDataSource.YES);
    ***/
  } /* constructor Db2DataSource */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param sUrl JDBC URL identifying the database instance to connect to.
   * @param sUser database user.
   * @param sPassword password of database user.
   * @throws SQLException if a database error occurred.
   */
  public Db2DataSource(String sUrl, String sUser, String sPassword)
    throws SQLException
  {
    super(new DB2SimpleDataSource());
    DB2SimpleDataSource dsd = (DB2SimpleDataSource)unwrap(DataSource.class);
    dsd.setDriverType(4);
    /***
    dsd.setEnableExtendedDescribe(DB2BaseDataSource.YES);
    ***/
    setUrl(sUrl);
    setUser(sUser);
    setPassword(sPassword);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * returns the appropriately wrapped H2 Connection.
   */
  @Override
  public Connection getConnection() throws SQLException
  {
    return new Db2Connection(super.getConnection());
  } /* getConnection */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * returns the appropriately wrapped H2 Connection.
   */
  @Override
  public Connection getConnection(String username, String password)
      throws SQLException
  {
    return new Db2Connection(super.getConnection(username, password));
  } /* getConnection */

  /*------------------------------------------------------------------*/
  /** @return unwrapped MSSQL DataSource
   */
  private DB2SimpleDataSource getUnwrapped()
  {
    DB2SimpleDataSource dsds = null;
    try { dsds = (DB2SimpleDataSource)unwrap(DataSource.class); }
    catch(SQLException se) { _il.exception(se); }
    return dsds;
  } /* getUnwrapped */

  /*------------------------------------------------------------------*/
  /** set URL to be used for connection.
   * @param url URL (format: "jdbc:h2:<database path>" results in file 
   *                 <database path>.h2.db)
   */
  public void setUrl(String url)
  {
    Matcher matcher = _patUrl.matcher(url);
    if (matcher.matches())
    {
      String sHost = matcher.group(5);
      String sPort = matcher.group(7);
      int iPort = 50000;
      if (sPort != null)
        iPort = Integer.parseInt(sPort);
      String sDatabase = matcher.group(8);
      DB2SimpleDataSource dsds = getUnwrapped();
      dsds.setServerName(sHost);
      dsds.setPortNumber(iPort);
      dsds.setDatabaseName(sDatabase);
    }
    else
      throw new IllegalArgumentException("Invalid jdbc:db2: URL!");
  } /* setUrl */
  
  /*------------------------------------------------------------------*/
  /** @return URL used for connection.
   */
  public String getUrl()
  {
    DB2SimpleDataSource dsds = getUnwrapped();
    String sHost = dsds.getServerName();
    int iPort = dsds.getPortNumber();
    String sDatabase = dsds.getDatabaseName();
    String sUrl = "jdbc:db2://"+sHost+":"+String.valueOf(iPort)+"/"+sDatabase;
    return sUrl;
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
    getUnwrapped().setDatabaseName(name);
  } /* setDatabaseName */
  
  /*------------------------------------------------------------------*/
  /** @return database name.
   */
  public String getDatabaseName()
  {
    return getUnwrapped().getDatabaseName();
  } /* getDatabaseName */
  
} /* class Db2DataSource */
