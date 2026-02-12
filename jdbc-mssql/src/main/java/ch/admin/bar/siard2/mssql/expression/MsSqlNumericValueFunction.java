package ch.admin.bar.siard2.mssql.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.expression.enums.*;

public class MsSqlNumericValueFunction
  extends NumericValueFunction
{

  /*------------------------------------------------------------------*/
  /** format the numeric value function.
   * MS SQL Server uses DATALENGTH for OCTET_LENGTH.
   * @return the SQL string corresponding to the fields of the numeric 
   *   value function.
   */
  @Override
  public String format()
  {
    String sFunction = super.format();
    if (getNumericFunction() == NumericFunction.OCTET_LENGTH)
      sFunction = "DATALENGTH" + 
        sLEFT_PAREN + getStringValueExpression().format() + sRIGHT_PAREN;
    return sFunction;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MsSqlNumericValueFunction(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class MsSqlNumericValueFunction */
