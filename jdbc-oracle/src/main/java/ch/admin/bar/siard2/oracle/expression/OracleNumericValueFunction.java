package ch.admin.bar.siard2.oracle.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.expression.enums.*;

public class OracleNumericValueFunction
extends NumericValueFunction
{
  /*------------------------------------------------------------------*/
  /** format the numeric value function.
   * Oracle uses LENGTH or DBMS_LOB.GETLENGTH for OCTET_LENGTH.
   * Here we just use DBMS_LOB.GETLENGTH.
   * Determining, whether the StringValueExpression is of LOB type 
   * could be done but would require a lot of work.
   * @return the SQL string corresponding to the fields of the numeric 
   *   value function.
   */
  @Override
  public String format()
  {
    String sFunction = super.format();
    if (getNumericFunction() == NumericFunction.OCTET_LENGTH)
    {
      sFunction = "DBMS_LOB.GETLENGTH" + 
        sLEFT_PAREN + getStringValueExpression().format() + sRIGHT_PAREN;
    }
    return sFunction;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public OracleNumericValueFunction(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* OracleNumericValueFunction */
