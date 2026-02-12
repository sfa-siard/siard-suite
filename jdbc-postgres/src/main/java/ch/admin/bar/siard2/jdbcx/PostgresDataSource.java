/*======================================================================
PostgresDataSource implements a wrapped PostgreSQL DataSource.
Application : SIARD2
Description : PostgresDataSource implements a wrapped PostgreSQL DataSource.
Platform    : Java 17
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 19.07.2019, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.jdbcx;

import java.sql.*;
import javax.sql.*;
import org.postgresql.ds.*;
import ch.enterag.utils.jdbcx.BaseDataSource;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.jdbc.*;

/*====================================================================*/
/** PostgresDataSource implements a wrapped simple PostgreSQL DataSource.
 * @author Hartwig Thomas
 */
public class PostgresDataSource
  extends BaseDataSource
  implements DataSource
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(PostgresDataSource.class.getName());

  /*------------------------------------------------------------------*/
  /** constructor */
  public PostgresDataSource()
  {
    super(new PGSimpleDataSource());
  } /* constructor MsSqlDataSource */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param sUrl JDBC URL identifying the database instance to connect to.
   * @param sUser database user.
   * @param sPassword password of database user.
   * @throws SQLException if a database error occurred.
   */
  public PostgresDataSource(String sUrl, String sUser, String sPassword)
    throws SQLException
  {
    super(new PGSimpleDataSource()); 
    setUrl(sUrl);
    setUser(sUser);
    setPassword(sPassword);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * returns the appropriately wrapped Postgres Connection.
   */
  @Override
  public Connection getConnection() throws SQLException
  {
    Connection conn = super.getConnection();
    if (conn != null)
    {
      /* wrapped native DataSource gets connection from DriverManager and therefore from PostgresDriver */
      if (!(conn instanceof PostgresConnection))
        conn = new PostgresConnection(conn);
    }
    return conn;
  } /* getConnection */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * returns the appropriately wrapped Postgres Connection.
   */
  @Override
  public Connection getConnection(String username, String password)
      throws SQLException
  {
    Connection conn = super.getConnection(username, password);
    if (conn != null)
    {
      /* wrapped native DataSource gets connection from DriverManager and therefore from PostgresDriver */
      if (!(conn instanceof PostgresConnection))
        conn = new PostgresConnection(conn);
    }
    return conn;
  } /* getConnection */

  /*------------------------------------------------------------------*/
  /** @return unwrapped Postgres DataSource
   */
  private PGSimpleDataSource getUnwrapped()
  {
    PGSimpleDataSource psds = null;
    try { psds = (PGSimpleDataSource)unwrap(DataSource.class); }
    catch(SQLException se) { _il.exception(se); }
    return psds;
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
    ((PGSimpleDataSource)getUnwrapped()).setDatabaseName(name);
  } /* setDatabaseName */
  
  /*------------------------------------------------------------------*/
  /** @return database name.
   */
  public String getDatabaseName()
  {
    return ((PGSimpleDataSource)getUnwrapped()).getDatabaseName();
  } /* getDatabaseName */
  
} /* class PostgresDataSource */
