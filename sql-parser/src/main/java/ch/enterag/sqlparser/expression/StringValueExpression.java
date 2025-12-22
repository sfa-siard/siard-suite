package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class StringValueExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(StringValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class SveVisitor extends EnhancedSqlBaseVisitor<StringValueExpression>
  {
    @Override
    public StringValueExpression visitStringValueExpression(SqlParser.StringValueExpressionContext ctx)
    {
      if ((ctx.CONCATENATION_OPERATOR() != null) && (ctx.stringValueExpression().size() == 2))
      {
        setConcatenation(true);
        setFirstOperand(getSqlFactory().newStringValueExpression());
        getFirstOperand().parse(ctx.stringValueExpression(0));
        setSecondOperand(getSqlFactory().newStringValueExpression());
        getSecondOperand().parse(ctx.stringValueExpression(1));
      }
      else if (ctx.valueExpressionPrimary() != null)
      {
        setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
        getValueExpressionPrimary().parse(ctx.valueExpressionPrimary());
      }
      else if (ctx.stringValueFunction() != null)
      {
        setStringValueFunction(getSqlFactory().newStringValueFunction());
        getStringValueFunction().parse(ctx.stringValueFunction());
      }
      return StringValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private SveVisitor _visitor = new SveVisitor();
  private SveVisitor getVisitor() { return _visitor; }
  
  private boolean _bConcatenation = false;
  public boolean isConcatenation() { return _bConcatenation; }
  public void setConcatenation(boolean bConcatenation) { _bConcatenation = bConcatenation; }
  
  private StringValueExpression _sve1 = null;
  public StringValueExpression getFirstOperand() { return _sve1; }
  public void setFirstOperand(StringValueExpression sve1) { _sve1 = sve1; }

  private StringValueExpression _sve2 = null;
  public StringValueExpression getSecondOperand() { return _sve2; }
  public void setSecondOperand(StringValueExpression sve2) { _sve2 = sve2; }
  
  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }

  private StringValueFunction _svf = null;
  public StringValueFunction getStringValueFunction() { return _svf; }
  public void setStringValueFunction(StringValueFunction svf) { _svf = svf; }
  
  /*------------------------------------------------------------------*/
  /** format the string value expression.
   * @return the SQL string corresponding to the fields of the string 
   *   value expression.
   */
  @Override
  public String format()
  {
    String sExpression = null;
    if (isConcatenation())
      sExpression = getFirstOperand().format() + sSP + sCONCATENATION_OPERATOR + sSP + getSecondOperand().format();
    else if (getValueExpressionPrimary() != null)
      sExpression = getValueExpressionPrimary().format();
    else if (getStringValueFunction() != null)
      sExpression = getStringValueFunction().format();
    return sExpression;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** get data type of the string value expression from the context of 
   * a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (isConcatenation())
    {
      DataType dt1 = getFirstOperand().getDataType(ss);
      DataType dt2 = getSecondOperand().getDataType(ss);
      if ((dt1 != null) && (dt2 != null))
      {
        PredefinedType pt1 = dt1.getPredefinedType();
        PredefinedType pt2 = dt2.getPredefinedType();
        if ((pt1 != null) && (pt2 != null))
        {
          long lLength1 = pt1.getLength();
          if (pt1.getMultiplier() != null)
            lLength1 = lLength1 * pt1.getMultiplier().getValue();
          long lLength2 = pt2.getLength();
          if (pt2.getMultiplier() != null)
            lLength2 = lLength2 * pt2.getMultiplier().getValue();
          long lLength = lLength1 + lLength2;
          Multiplier m = null;
          if (lLength > Integer.MAX_VALUE)
          {
            if (lLength < Multiplier.K.getValue()*((long)Integer.MAX_VALUE))
            {
              m = Multiplier.K;
              lLength = (lLength + Multiplier.K.getValue()-1)/Multiplier.K.getValue();
            }
            else if (lLength < Multiplier.M.getValue()*((long)Integer.MAX_VALUE))
            {
              m = Multiplier.M;
              lLength = (lLength + Multiplier.M.getValue()-1)/Multiplier.M.getValue();
            }
            else if (lLength < Multiplier.G.getValue()*((long)Integer.MAX_VALUE))
            {
              m = Multiplier.G;
              lLength = (lLength + Multiplier.G.getValue()-1)/Multiplier.G.getValue();
            }
          }
          dt = getSqlFactory().newDataType();
          PredefinedType pt = getSqlFactory().newPredefinedType();
          dt.initPredefinedDataType(pt);
          switch(pt1.getType())
          {
            case CHAR: 
            case NCHAR:
              switch(pt2.getType())
              {
                case CHAR:
                case NCHAR:
                  if (m == null)
                    pt.initCharType((int)lLength);
                  else
                    throw new IllegalArgumentException("CHAR length overflow!");
                  break;
                case VARCHAR:
                case NVARCHAR:
                  if (m == null)
                    pt.initVarCharType((int)lLength);
                  else
                    throw new IllegalArgumentException("VARCHAR length overflow!");
                  break;
                case CLOB:
                case NCLOB:
                case XML:
                  pt.initClobType((int)lLength, m);
                  break;
                default:
                  throw new IllegalArgumentException("Unexpected data type for string value expression: "+pt2.getType().getKeyword()+"!"); 
              }
              break;
            case VARCHAR:
            case NVARCHAR:
              switch(pt2.getType())
              {
                case CHAR:
                case NCHAR:
                case VARCHAR:
                case NVARCHAR:
                  if (m == null)
                    pt.initVarCharType((int)lLength);
                  else
                    throw new IllegalArgumentException("VARCHAR length overflow!");
                  break;
                case CLOB:
                case NCLOB:
                case XML:
                  pt.initClobType((int)lLength, m);
                  break;
                default:
                  throw new IllegalArgumentException("Unexpected data type for string value expression: "+pt2.getType().getKeyword()+"!"); 
              }
              break;
            case CLOB:
            case NCLOB:
              switch(pt2.getType())
              {
                case CHAR:
                case NCHAR:
                case VARCHAR:
                case NVARCHAR:
                case CLOB:
                case NCLOB:
                case XML:
                  pt.initClobType((int)lLength, m);
                  break;
                default:
                  throw new IllegalArgumentException("Unexpected data type for string value expression: "+pt2.getType().getKeyword()+"!"); 
              }
              break;
            case XML:
              switch(pt2.getType())
              {
                case CHAR:
                case NCHAR:
                case VARCHAR:
                case NVARCHAR:
                case CLOB:
                case NCLOB:
                  pt.initClobType((int)lLength, m);
                  break;
                case XML:
                  pt.initXmlType();
                  pt.setLength((int)lLength);
                  pt.setMultiplier(m);
                  break;
                default:
                  throw new IllegalArgumentException("Unexpected data type for string value expression: "+pt2.getType().getKeyword()+"!"); 
              }
              break;
            default:
              throw new IllegalArgumentException("Unexpected data type for string value expresseion: "+pt1.getType().getKeyword()+"!"); 
          }
        }
      }
    }
    else if (getValueExpressionPrimary() != null)
      dt = getValueExpressionPrimary().getDataType(ss);
    else if (getStringValueFunction() != null)
      dt = getStringValueFunction().getDataType(ss);
    return dt;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  /** evaluate a string value expression from its components.
   * @return string value.
   */
  private Object evaluate(
    Object oFirstOperand,
    Object oSecondOperand,
    Object oValuePrimary,
    Object oStringValue)
  {
    Object oValue = null;
    if (isConcatenation())
    {
      if ((oFirstOperand instanceof String) && 
          (oSecondOperand instanceof String))
      {
        String s1 = (String)oFirstOperand; 
        String s2 = (String)oSecondOperand; 
        oValue = s1 + s2;
      }
      else if (oFirstOperand instanceof byte[] && (oSecondOperand instanceof byte[]))
      {
        byte[] buf1 = (byte[])oFirstOperand;
        byte[] buf2 = (byte[])oSecondOperand;
        byte[] buf = new byte[buf1.length+buf2.length];
        System.arraycopy(buf1, 0, buf, 0, buf1.length);
        System.arraycopy(buf2, 0, buf, buf1.length, buf2.length);
        oValue = buf;
      }
    }
    else if (getValueExpressionPrimary() != null)
      oValue = oValuePrimary;
    else if (getStringValueFunction() != null)
      oValue = oStringValue;
    return oValue;
  }
  /*------------------------------------------------------------------*/
  /** evaluate the string value expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return value.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oFirstOperand = null;
    if (getFirstOperand() != null)
      oFirstOperand = getFirstOperand().evaluate(ss, bAggregated);
    Object oSecondOperand = null;
    if (getSecondOperand() != null)
      oSecondOperand = getSecondOperand().evaluate(ss, bAggregated);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().evaluate(ss, bAggregated);
    Object oStringValue = null;
    if (getStringValueFunction() != null)
      oStringValue = getStringValueFunction().evaluate(ss, bAggregated);
    Object oValue = evaluate(oFirstOperand, oSecondOperand, oValuePrimary, oStringValue);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a string value expression to 
   * their initial value.
   * @param ss sql statement.
   * @return final value before reset.
   */
  public Object resetAggregates(SqlStatement ss)
  {
    Object oFirstOperand = null;
    if (getFirstOperand() != null)
      oFirstOperand = getFirstOperand().resetAggregates(ss);
    Object oSecondOperand = null;
    if (getSecondOperand() != null)
      oSecondOperand = getSecondOperand().resetAggregates(ss);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().resetAggregates(ss);
    Object oStringValue = null;
    if (getStringValueFunction() != null)
      oStringValue = getStringValueFunction().resetAggregates(ss);
    Object oValue = evaluate(oFirstOperand, oSecondOperand, oValuePrimary, oStringValue);
    return oValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the string value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.StringValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the string value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().stringValueExpression());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a string value expression.
   * @param bConcatenation true, if concatentation expression.
   * @param sve1 first operand (or null).
   * @param sve2 second operand (or null).
   * @param vep value expression primary.
   * @param svf string value function. 
   */
  public void initialize(
    boolean bConcatenation,
    StringValueExpression sve1,
    StringValueExpression sve2,
    ValueExpressionPrimary vep,
    StringValueFunction svf)
  {
    _il.enter(String.valueOf(bConcatenation), sve1, sve2, vep, svf);
    setConcatenation(bConcatenation);
    setFirstOperand(sve1);
    setSecondOperand(sve2);
    setValueExpressionPrimary(vep);
    setStringValueFunction(svf);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public StringValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class StringValueExpression */
