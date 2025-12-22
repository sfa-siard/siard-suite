package ch.admin.bar.siard2.oracle.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.identifier.*;

public class OracleValueExpressionPrimary
  extends ValueExpressionPrimary
{
  private static String _sArrayType = null;
  public static void setArrayType(String sArrayType)
  {
    _sArrayType = sArrayType;
  } /* setArrayType */
  
  @Override
  protected String formatCollectionValueComponents()
  {
    String s = "";
    if (getCollectionValueComponents().size() > 0)
    {
      s = sLEFT_PAREN;
      for (int i = 0; i < getCollectionValueComponents().size(); i++)
      {
        if (i > 0)
          s = s + sCOMMA + sSP;
        s = s + getCollectionValueComponents().get(i).format();
      }
      s = s + sRIGHT_PAREN;
    }
    return s;
  } /* formatCollectionValueComponents */
  
  @Override
  public String format()
  {
    String sExpression = null;
    /* arrayValueConstruct */
    if (isArrayValueConstructor())
    {
      sExpression = _sArrayType;
      if (getQueryExpression() != null)
      {
        sExpression = sExpression + sLEFT_PAREN + getQueryExpression().format();
        if (getSortSpecifications().size() > 0)
        {
          sExpression = sExpression + sNEW_LINE + K.ORDER.getKeyword() + sSP + K.BY.getKeyword() + sSP;
          for (int i = 0; i < getSortSpecifications().size(); i++)
          {
            if (i > 0)
              sExpression = sExpression + sCOMMA + sSP;
            sExpression = sExpression + getSortSpecifications().get(i).format();
          }
        }
        sExpression = sExpression + sRIGHT_PAREN;
      }
      else
        sExpression = sExpression + formatCollectionValueComponents();
    }
    else if (getRoutineName().isSet())
    {
      if (getRoutineName().getName().equals("OCTET_LENGTH"))
        setRoutineName(new QualifiedId(null,"DBMS_LOB","GETLENGTH"));
      sExpression = getRoutineName().format() + formatSqlArguments();
    }
    else
      sExpression = super.format();
    return sExpression;
  } /* format */
  
  public OracleValueExpressionPrimary(SqlFactory sf) {
    super(sf);
  } /* constructor */
}
