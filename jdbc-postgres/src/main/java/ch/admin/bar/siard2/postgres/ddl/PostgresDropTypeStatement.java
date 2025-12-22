/*======================================================================
PostgresDropTypeStatement overrides DropTypeStatement of SQL parser.
Application : SIARD2
Description : PostgresDropTypeStatement overrides DropTypeStatement of 
              SQL parser because Postgres does not support DISTINCT types
              but uses DOMAINs instead. 
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 20.08.2017, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.postgres.ddl;

import java.sql.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;
import ch.admin.bar.siard2.postgres.*;

/*====================================================================*/
/** PostgresDropTypeStatement overrides DropTypeStatement of 
 * SQL parser because Postgres does not support DISTINCT types but uses 
 * DOMAINs instead.
 * @author Hartwig Thomas
 */
public class PostgresDropTypeStatement
  extends DropTypeStatement
{
  /*------------------------------------------------------------------*/
  /** format the drop type statement for Postgres dropping the domain,
   * if the type name designates a domain.
   * @return the SQL string corresponding to the fields of the drop type statement.
   */
  @Override
  public String format()
  {
    String sStatement = null;
    PostgresSqlFactory psf = (PostgresSqlFactory)getSqlFactory();
    try
    {
      Connection connNative = psf.getConnection().unwrap(Connection.class);
      String sSchema = PostgresLiterals.formatId(getUdtName().getSchema());
      if (sSchema == null)
        sSchema = "public";
      String sType = PostgresLiterals.formatId(getUdtName().getName());
      String sSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.DOMAINS WHERE DOMAIN_SCHEMA = " + 
        PostgresLiterals.formatStringLiteral(sSchema)+" AND DOMAIN_NAME = " +
        PostgresLiterals.formatStringLiteral(sType);
      ResultSet rs = connNative.createStatement().executeQuery(sSql);
      int iDomains = 0;
      if (rs.next())
        iDomains = rs.getInt(1);
      rs.close();
      if (iDomains > 0)
        sStatement = K.DROP.getKeyword() + sSP + K.DOMAIN.getKeyword() + sSP +
        getUdtName().format();
    }
    catch (SQLException se) {}
    if (sStatement == null)
      sStatement = K.DROP.getKeyword() + sSP + K.TYPE.getKeyword() + sSP +
      getUdtName().format(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public PostgresDropTypeStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class PostgresDropTypeStatement */
