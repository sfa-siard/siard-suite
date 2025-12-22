/*======================================================================
PostgresCreateTypeStatement overrides CreateTypeStatement of SQL parser.
Application : SIARD2
Description : PostgresCreateTypeStatement overrides CreateTypeStatement of 
              SQL parser because Postgres does not support DISTINCT types
              but uses DOMAINs instead. 
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 29.07.2017, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.postgres.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;

/*====================================================================*/
/** PostgresCreateTypeStatement overrides CreateTypeStatement of 
 * SQL parser because Postgres does not support DISTINCT types but uses 
 * DOMAINs instead.
 * @author Hartwig Thomas
 */
public class PostgresCreateTypeStatement
  extends CreateTypeStatement
{
  /*------------------------------------------------------------------*/
  /** format the create type statement.
   * @return the SQL string corresponding to the fields of the create 
   *   type statement.
   */
  @Override
  public String format()
  {
    String sStatement = null; 
    if (getDistinctBaseType() != null)
    {
      sStatement = K.CREATE.getKeyword() + sSP + K.DOMAIN.getKeyword() + sSP + 
      getTypeName().format();
      sStatement = sStatement + sSP + K.AS.getKeyword() + sSP + getDistinctBaseType().format();
    }
    else
    {
      setInstantiability(null);
      setFinality(null);
      sStatement = super.format();
    }
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public PostgresCreateTypeStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class PostgresCreateTypeStatement */
