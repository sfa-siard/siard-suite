package ch.admin.bar.siard2.oracle.expression;

import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.*;

public class OracleQueryExpressionBody
  extends QueryExpressionBody
{
  private static String[] _asArrayTypes = null;
  public static void setArrayTypes(String[] asArrayTypes)
  { 
    _asArrayTypes = asArrayTypes;
  }

  public String formatValues()
  {
    String s = K.VALUES.getKeyword() + sLEFT_PAREN;
    for (int i = 0; i < getTableRowValueExpressions().size(); i++)
    {
      OracleValueExpressionPrimary.setArrayType(_asArrayTypes[i]);
      if (i > 0)
        s = s + sCOMMA + sSP;
      s = s + getTableRowValueExpressions().get(i).format();
    }
    s = s + sRIGHT_PAREN;
    return s;
  }

  public OracleQueryExpressionBody(SqlFactory sf) {
    super(sf);
  } /* constructor */

}
