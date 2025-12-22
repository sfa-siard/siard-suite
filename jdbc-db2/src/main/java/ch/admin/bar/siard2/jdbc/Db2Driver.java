/*======================================================================
Db2Driver implements a wrapped DB/2 Driver.
Version     : $Id: $
Application : SIARD2
Description : Db2Driver implements a wrapped DB/2 Driver.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 04.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.util.*;

import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.logging.*;

/*====================================================================*/
/** Db2Driver implements a wrapped DB/2 Driver.
 * @author Hartwig Thomas
 */
public class Db2Driver
  extends BaseDriver
  implements Driver
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(Db2Driver.class.getName());
  /** protocol sub scheme for DB/2 JDBC URL */
  public static final String sDB2_SCHEME = "db2";
  /** URL prefix for DB/2 JDBC URL */
  public static final String sDB2_URL_PREFIX = sJDBC_SCHEME+":"+sDB2_SCHEME+":";
  /** URL for database name.
   * @param sDatabaseName host:port/database, e.g. localhost:50000/testdb 
   * @return JDBC URL.
   */
  public static String getUrl(String sDatabaseName)
  {
    String sUrl = sDatabaseName;
    if (!sUrl.startsWith(sDB2_URL_PREFIX))
      sUrl = sDB2_URL_PREFIX+"//"+sDatabaseName;
    return sUrl;
  } /* getUrl */
  
  /** register this driver, replacing original DB/2 driver
   */
  public static void register()
  {
    try { BaseDriver.register(new Db2Driver(), "com.ibm.db2.jcc.DB2Driver", "jdbc:db2://localhost:50000//testdb"); }
    catch(Exception e) { throw new Error(e); }
  }

  /** replace DB/2 driver by this, when this class is loaded */
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
    boolean bAccepts = url.startsWith("jdbc:db2:") ||
                       url.startsWith("jdbc:db2j:net:") ||
                       url.startsWith("jdbc:ids:");
    _il.exit(bAccepts);
    return bAccepts;
  } /* acceptsUrl */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * returns the appropriately wrapped H2 Connection.
   */
  @Override
  public Connection connect(String url, Properties info)
      throws SQLException
  {
    _il.enter(url, info);
    Connection conn = super.connect(url, info);
    if (conn != null)
      conn = new Db2Connection(super.connect(url, info)); 
    _il.exit(conn);
    return conn;
  } /* connect */
  
} /* Db2Driver */
