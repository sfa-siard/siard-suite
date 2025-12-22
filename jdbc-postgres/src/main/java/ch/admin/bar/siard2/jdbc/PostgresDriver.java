/*======================================================================
PostgresDriver implements a wrapped Postgres Driver.
Application : SIARD2
Description : PostgresDriver implements a wrapped Postgres Driver.
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 25.07.2019, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.util.Properties;

import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.logging.*;

/*====================================================================*/
/** PostgresDriver implements a wrapped Postgres Driver.
 * @author Hartwig Thomas
 */
public class PostgresDriver
  extends BaseDriver
  implements Driver
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(PostgresDriver.class.getName());
  /** protocol sub scheme for Postgres JDBC URL */
  public static final String sPOSTGRES_SCHEME = "postgresql";
  /** URL prefix for Postgres JDBC URL */
  public static final String sPOSTGRES_URL_PREFIX = sJDBC_SCHEME+":"+sPOSTGRES_SCHEME+":";
  /** URL for database name.
   * @param sDatabaseName host:port/database, e.g. localhost:5432/testdb 
   * @return JDBC URL.
   */
  public static String getUrl(String sDatabaseName)
  {
    String sUrl = sDatabaseName;
    if (!sUrl.startsWith(sPOSTGRES_URL_PREFIX))
      sUrl = sPOSTGRES_URL_PREFIX+"//"+sDatabaseName;
    return sUrl;
  } /* getUrl */

  /** register this driver, replacing original Postgres driver
   */
  public static void register()
  {
    try { BaseDriver.register(new PostgresDriver(), "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/testdb"); }
    catch(Exception e) { throw new Error(e); }
  }

  /** replace Postgres driver by this, when this class is loaded */
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
    boolean bAccepts = url.startsWith(sPOSTGRES_URL_PREFIX);
    _il.exit(bAccepts);
    return bAccepts;
  } /* acceptsUrl */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * returns the appropriately wrapped Postgres Connection.
   */
  @Override
  public Connection connect(String url, Properties info)
      throws SQLException
  {
    _il.enter(url, info);
    Connection conn = super.connect(url, info);
    if (conn != null)
    {
      if (conn instanceof PostgresConnection)
        throw new SQLException("PostgresDriver.connect() returned a wrapped connection!");
      conn = new PostgresConnection(conn);
    }
    _il.exit(conn);
    return conn;
  } /* connect */
  
} /* class PostgresDriver */
