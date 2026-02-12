/*======================================================================
MsSqlValueExpressionPrimary implements the array value translation from ISO SQL to MSSQL.
Version     : $Id: $
Application : SIARD2
Description : MsSqlValueExpressionPrimary implements the array value translation 
              from ISO SQL:2008 to MSSQL. 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 14.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mssql.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;

/*====================================================================*/
/** MsSqlValueExpressionPrimary implements the array value translation from 
 * ISO SQL to MSSQL by turning into a CLOB.
 * @author Hartwig Thomas
 */
public class MsSqlValueExpressionPrimary
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
    /* arrayValueConstruct */
    if (isArrayValueConstructor())
      throw new IllegalArgumentException("ARRAY value constructor is not available in MSSQL!");
    /* routineInvoc */
    else if (getRoutineName().isSet())
    {
      if (getRoutineName().getName().equals("OCTET_LENGTH"))
        getRoutineName().setName("DATALENGTH");
      sExpression = getRoutineName().format() + formatSqlArguments();
    }
    /* nextValueExp */
    else
      sExpression = super.format();
    return sExpression;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MsSqlValueExpressionPrimary(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class MsSqlValueExpressionPrimary */
