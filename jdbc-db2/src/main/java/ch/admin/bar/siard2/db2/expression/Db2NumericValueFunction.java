package ch.admin.bar.siard2.db2.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.expression.enums.*;

public class Db2NumericValueFunction
extends NumericValueFunction
{
  /*------------------------------------------------------------------*/
  /** format the numeric value function.
   * DB/2 uses LENGTH for OCTET_LENGTH.
   * @return the SQL string corresponding to the fields of the numeric 
   *   value function.
   */
  @Override
  public String format()
  {
    String sFunction = super.format();
    if (getNumericFunction() == NumericFunction.OCTET_LENGTH)
      sFunction = "LENGTH" + 
        sLEFT_PAREN + getStringValueExpression().format() + sRIGHT_PAREN;
    return sFunction;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public Db2NumericValueFunction(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* OracleNumericValueFunction */
