/*======================================================================
PostgresValueExpressionPrimary overrides ValueExpressionPrimary of SQL parser.
Application : SIARD2
Description : PostgresValueExpressionPrimary overrides ValueExpressionPrimary 
              of SQL parser in order to format UDT literals for Postgres. 
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 21.08.2017, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.postgres.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;

/*====================================================================*/
/** PostgresValueExpressionPrimary overrides ValueExpressionPrimary of 
 * SQL parser in order to format UDT literals for Postgres.
 * @author Hartwig Thomas
 */
public class PostgresValueExpressionPrimary
  extends ValueExpressionPrimary
{

  /*------------------------------------------------------------------*/
  /** format the value expression primary.
   * @return the SQL string corresponding to the fields of the value 
   *   expression primary.
   */
  @Override
  public String format()
  {
    String sExpression = "";
    /* newSpec */
    if (isNew() && getRoutineName().isSet())
      sExpression = formatSqlArguments();
    /* attributeOrMethodRef */
    else 
      sExpression = super.format();
    return sExpression;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public PostgresValueExpressionPrimary(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class PostgresValueExpressionPrimary */
