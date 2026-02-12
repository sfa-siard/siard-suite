package ch.enterag.sqlparser.expression;

import java.math.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class NumericValueExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(NumericValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class NveVisitor extends EnhancedSqlBaseVisitor<NumericValueExpression>
  {
    @Override
    public NumericValueExpression visitNumericValueExpression(SqlParser.NumericValueExpressionContext ctx)
    {
      NumericValueExpression nveReturn = NumericValueExpression.this;
      if (ctx.numericValueExpression().size() == 2)
      {
        if (ctx.additiveOperator() != null)
          setAdditiveOperator(getAdditiveOperator(ctx.additiveOperator()));
        else if (ctx.multiplicativeOperator() != null)
          setMultiplicativeOperator(getMultiplicativeOperator(ctx.multiplicativeOperator()));
        setFirstOperand(getSqlFactory().newNumericValueExpression());
        getFirstOperand().parse(ctx.numericValueExpression(0));
        setSecondOperand(getSqlFactory().newNumericValueExpression());
        getSecondOperand().parse(ctx.numericValueExpression(1));
      }
      else if ((ctx.LEFT_PAREN() != null) && (ctx.RIGHT_PAREN() != null))
      {
        setParenthesizedExpression(getSqlFactory().newNumericValueExpression());
        getParenthesizedExpression().parse(ctx.numericValueExpression(0));
      }
      else
      {
        if (ctx.sign() != null)
          setSign(getSign(ctx.sign()));
        nveReturn = visitChildren(ctx);
      }
      return nveReturn;
    }
    @Override
    public NumericValueExpression visitValueExpressionPrimary(SqlParser.ValueExpressionPrimaryContext ctx)
    {
      setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
      getValueExpressionPrimary().parse(ctx);
      return NumericValueExpression.this;
    }
    @Override 
    public NumericValueExpression visitNumericValueFunction(SqlParser.NumericValueFunctionContext ctx)
    {
      setNumericValueFunction(getSqlFactory().newNumericValueFunction());
      getNumericValueFunction().parse(ctx);
      return NumericValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private NveVisitor _visitor = new NveVisitor();
  private NveVisitor getVisitor() { return _visitor; }

  private AdditiveOperator _ao = null;
  public AdditiveOperator getAdditiveOperator() { return _ao; }
  public void setAdditiveOperator(AdditiveOperator ao) { _ao = ao; }
  
  private MultiplicativeOperator _mo = null;
  public MultiplicativeOperator getMultiplicativeOperator() { return _mo; }
  public void setMultiplicativeOperator(MultiplicativeOperator mo) { _mo = mo; }
  
  private NumericValueExpression _nve1 = null;
  public NumericValueExpression getFirstOperand() { return _nve1; }
  public void setFirstOperand(NumericValueExpression nve1) { _nve1 = nve1; }
  
  private NumericValueExpression _nve2 = null;
  public NumericValueExpression getSecondOperand() { return _nve2; }
  public void setSecondOperand(NumericValueExpression nve2) { _nve2 = nve2; }
  
  private NumericValueExpression _nve = null;
  public NumericValueExpression getParenthesizedExpression() { return _nve; }
  public void setParenthesizedExpression(NumericValueExpression nve) { _nve = nve; }
  
  private Sign _sign = null;
  public Sign getSign() { return _sign; }
  public void setSign(Sign sign) { _sign = sign; }
  
  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }
  
  private NumericValueFunction _nvf = null;
  public NumericValueFunction getNumericValueFunction() { return _nvf; }
  public void setNumericValueFunction(NumericValueFunction nvf) { _nvf = nvf; }
  
  /*------------------------------------------------------------------*/
  /** format the numeric value expression.
   * @return the SQL string corresponding to the fields of the numeric 
   *   value expression.
   */
  @Override
  public String format()
  {
    String sExpression = "";
    if ((getFirstOperand() != null) && (getSecondOperand() != null))
    {
      sExpression = getFirstOperand().format();
      if (getAdditiveOperator() != null)
        sExpression = sExpression + sSP + getAdditiveOperator().getKeywords() + sSP;
      else if (getMultiplicativeOperator() != null)
        sExpression = sExpression + getMultiplicativeOperator().getKeywords();
      sExpression = sExpression + getSecondOperand().format();
    }
    else if (getParenthesizedExpression() != null)
      sExpression = sLEFT_PAREN + getParenthesizedExpression().format() + sRIGHT_PAREN;
    else if (getValueExpressionPrimary() != null)
    {
      if (getSign() != null)
        sExpression = getSign().getKeywords();
      sExpression = sExpression + getValueExpressionPrimary().format();
    }
    else if (getNumericValueFunction() != null)
      sExpression = getNumericValueFunction().format();
    return sExpression;
  } /* format */

  /*------------------------------------------------------------------*/
  /** return the data type of the numeric value expression from the context 
   * of a query.
   * @param ss sql statement.
   * @return data type of numeric value (Double or BigDecimal).
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if ((getFirstOperand() != null) && (getSecondOperand() != null))
    {
      DataType dt1 = getFirstOperand().getDataType(ss);
      DataType dt2 = getSecondOperand().getDataType(ss);
      if ((dt1 != null) && (dt2 != null))
      {
        PredefinedType pt1 = dt1.getPredefinedType();
        PredefinedType pt2 = dt2.getPredefinedType();
        if ((pt1 != null) && (pt2 != null))
        {
          int ip1 = dt1.getPredefinedType().getPrecision();
          int is1 = dt1.getPredefinedType().getScale();
          int ip2 = dt2.getPredefinedType().getPrecision();
          int is2 = dt2.getPredefinedType().getScale();
          dt = getSqlFactory().newDataType();
          PredefinedType pt = getSqlFactory().newPredefinedType();
          dt.initPredefinedDataType(pt);
          switch(pt1.getType())
          {
            case DOUBLE:
            case FLOAT:
            case REAL:
              pt.initDoubleType();
              break;
            case DECIMAL:
            case NUMERIC:
              switch (pt2.getType())
              {
                case DOUBLE: 
                case FLOAT: 
                case REAL: 
                  pt.initDoubleType();
                  break;
                case DECIMAL:
                case NUMERIC:
                  pt.initDecimalType(ip2,is2);
                  break;
                case BIGINT:
                  ip2 = 19;
                  is2 = 0;
                  pt.initDecimalType(ip2, is2);
                  break;
                case INTEGER:
                  ip2 = 10;
                  is2 = 0;
                  pt.initDecimalType(ip2,is2);
                  break;
                case SMALLINT:
                  ip2 = 5;
                  is2 = 0;
                  pt.initDecimalType(ip2,is2);
                  break;
                default:
                  throw new IllegalArgumentException("Unexpected data type for numeric value expression: "+pt2.getType().getKeyword()+"!"); 
              }
              break;
            case BIGINT:
              switch(pt2.getType())
              {
                case DOUBLE: 
                case FLOAT: 
                case REAL: 
                  pt.initDoubleType();
                  break;
                case DECIMAL:
                case NUMERIC:
                  pt.initDecimalType(ip2,is2);
                  break;
                case BIGINT:
                case INTEGER:
                case SMALLINT:
                  pt.initBigIntType();
                  break;
                default:
                  throw new IllegalArgumentException("Unexpected data type for numeric value expression: "+pt2.getType().getKeyword()+"!"); 
              }
              break;
            case INTEGER:
              switch(pt2.getType())
              {
                case DOUBLE: 
                case FLOAT: 
                case REAL: 
                  pt.initDoubleType();
                  break;
                case DECIMAL:
                case NUMERIC:
                  pt.initDecimalType(ip2,is2);
                  break;
                case BIGINT:
                  pt.initBigIntType();
                  break;
                case INTEGER:
                case SMALLINT:
                  pt.initIntegerType();
                  break;
                default:
                  throw new IllegalArgumentException("Unexpected data type for numeric value expression: "+pt2.getType().getKeyword()+"!"); 
              }
              break;
            case SMALLINT:
              switch(pt2.getType())
              {
                case DOUBLE: 
                case FLOAT: 
                case REAL: 
                  pt.initDoubleType();
                  break;
                case DECIMAL:
                case NUMERIC:
                  pt.initDecimalType(ip2,is2);
                  break;
                case BIGINT:
                  pt.initBigIntType();
                  break;
                case INTEGER:
                  pt.initIntegerType();
                case SMALLINT:
                  pt.initSmallIntType();
                  break;
                default:
                  throw new IllegalArgumentException("Unexpected data type for numeric value expression: "+pt2.getType().getKeyword()+"!"); 
              }
              break;
            default:
              throw new IllegalArgumentException("Unexpected data type for numeric value expression: "+pt1.getType().getKeyword()+"!"); 
          }
          if (ip1 > ip2)
            dt.getPredefinedType().setPrecision(ip1);
          if (is1 > is2)
            dt.getPredefinedType().setScale(is1);
        }
      }
      else if (dt1.getPredefinedType() != null)
        dt = dt1;
      else if (dt2.getPredefinedType() != null)
        dt = dt2;
    }
    else if (getParenthesizedExpression() != null)
      dt = getParenthesizedExpression().getDataType(ss);
    else if (getValueExpressionPrimary() != null)
      dt = getValueExpressionPrimary().getDataType(ss);
    else if (getNumericValueFunction() != null)
      dt = getNumericValueFunction().getDataType(ss);
    return dt;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  /** evaluate the operator on the two numeric arguments.
   * @param ao additive operator.
   * @param mo multiplicative operator.
   * @param oValue1 first argument.
   * @param oValue2 second argument.
   * @return result
   */
  private Object evaluateOperator(AdditiveOperator ao, MultiplicativeOperator mo, 
    Object oValue1, Object oValue2)
  {
    Object oValue = null;
    BigDecimal bd1 = null;
    BigDecimal bd2 = null;
    Double d1 = null;
    Double d2 = null;
    if ((oValue1 != null) && (oValue2 != null))
    {
      if (oValue1 instanceof BigDecimal)
        bd1 = (BigDecimal)oValue1;
      else if (oValue1 instanceof Double)
        d1 = (Double)oValue1;
      else
        throw new IllegalArgumentException("Numeric value must be BigDecimal or Double!");
      if (oValue2 instanceof BigDecimal)
        bd2 = (BigDecimal)oValue2;
      else if (oValue2 instanceof Double)
        d2 = (Double)oValue2;
      else
        throw new IllegalArgumentException("Numeric value must be BigDecimal or Double!");
      if ((bd1 != null) || (bd2 != null))
      {
        if (bd1 == null)
          bd1 = BigDecimal.valueOf(d1.doubleValue());
        if (bd2 == null)
          bd2 = BigDecimal.valueOf(d2.doubleValue());
        if (ao != null)
        {
          if (ao == AdditiveOperator.PLUS_SIGN)
            oValue = bd1.add(bd2);
          else if (ao == AdditiveOperator.MINUS_SIGN)
            oValue = bd1.subtract(bd2);
        }
        else if (mo != null)
        {
          if (mo == MultiplicativeOperator.ASTERISK)
            oValue = bd1.multiply(bd2);
          else if (mo == MultiplicativeOperator.SOLIDUS)
            oValue = bd1.divide(bd2);
        }
      }
      else
      {
        if (ao != null)
        {
          if (ao == AdditiveOperator.PLUS_SIGN)
            oValue = Double.valueOf(d1.doubleValue() + d2.doubleValue());
          else if (ao == AdditiveOperator.MINUS_SIGN)
            oValue = Double.valueOf(d1.doubleValue() - d2.doubleValue());
        }
        else if (mo != null)
        {
          if (mo == MultiplicativeOperator.ASTERISK)
            oValue = Double.valueOf(d1.doubleValue()*d2.doubleValue());
          else if (mo == MultiplicativeOperator.SOLIDUS)
            oValue = Double.valueOf(d1.doubleValue()/d2.doubleValue());
        }
      }
    }    
    return oValue;
  } /* evaluateOperator */
  
  /*------------------------------------------------------------------*/
  /** evaluate the effects of the sign on a numeric value.
   * @param sign sign.
   * @param oValue value.
   * @return resulting value.
   */
  private Object evaluateSign(Sign sign, Object oValue)
  {
    if (Sign.MINUS_SIGN.equals(sign))
    {
      if (oValue instanceof BigDecimal)
      {
        BigDecimal bdValue = (BigDecimal)oValue;
        oValue = bdValue.negate();
      }
      else if (oValue instanceof Double)
      {
        Double dValue = (Double)oValue;
        oValue = Double.valueOf(-dValue.doubleValue());
      }
    }
    return oValue;
  } /* evaluateSign */
  
  /*------------------------------------------------------------------*/
  /** evaluate an integer numeric value.
   * @param oValue numeric value
   * @return numeric value cast to an integer.
   */
  public int evaluateInteger(Object oValue)
  {
    int iValue = -1;
    if (oValue instanceof BigDecimal)
      iValue = ((BigDecimal)oValue).intValueExact();
    else if (oValue instanceof Double)
      iValue = ((Double)oValue).intValue();
    else
      throw new IllegalArgumentException("Numeric value could not be cast to int!");
    return iValue;
  } /* evaluateInteger */

  /*------------------------------------------------------------------*/
  /** evaluate a numeric value expression from its components.
   * @return numeric value (Double or BigDecimal).
   */
  private Object evaluate(
    Object oFirstOperand,
    Object oSecondOperand,
    Object oParenthesized,
    Object oValuePrimary,
    Object oNumericValue)
  {
    Object oValue = null;
    if ((getFirstOperand() != null) && (getSecondOperand() != null))
      oValue = evaluateOperator(getAdditiveOperator(), getMultiplicativeOperator(),
        oFirstOperand, oSecondOperand);
    else if (getParenthesizedExpression() != null)
      oValue = oParenthesized;
    else if (getValueExpressionPrimary() != null)
      oValue = evaluateSign(getSign(),oValuePrimary);
    else if (getNumericValueFunction() != null)
      oValue = oNumericValue;
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate a numeric value expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return numeric value (Double or BigDecimal).
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oFirstOperand = null;
    if (getFirstOperand() != null)
      oFirstOperand = getFirstOperand().evaluate(ss, bAggregated);
    Object oSecondOperand = null;
    if (getSecondOperand() != null)
      oSecondOperand = getSecondOperand().evaluate(ss, bAggregated);
    Object oParenthesized = null;
    if (getParenthesizedExpression() != null)
      oParenthesized = getParenthesizedExpression().evaluate(ss, bAggregated);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().evaluate(ss, bAggregated);
    Object oNumericValue = null;
    if (getNumericValueFunction() != null)
      oNumericValue = getNumericValueFunction().evaluate(ss, bAggregated);
    Object oValue = evaluate(oFirstOperand, oSecondOperand, oParenthesized, oValuePrimary, oNumericValue);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a numeric value expression to 
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
    Object oParenthesized = null;
    if (getParenthesizedExpression() != null)
      oParenthesized = getParenthesizedExpression().resetAggregates(ss);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().resetAggregates(ss);
    Object oNumericValue = null;
    if (getNumericValueFunction() != null)
      oNumericValue = getNumericValueFunction().resetAggregates(ss);
    Object oValue = evaluate(oFirstOperand, oSecondOperand, oParenthesized, oValuePrimary, oNumericValue);
    return oValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the numeric value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.NumericValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the numeric value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().numericValueExpression());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a numeric value expression.
   * @param ao additive operator (or null).
   * @param mo multiplicative operator (or null).
   * @param nve1 first operand (or null).
   * @param nve2 second operand (or null).
   * @param nve numeric value expression.
   * @param sign sign of value expression primary.
   * @param vep value expression primary.
   * @param nvf numeric value function. 
   */
  public void initialize(
    AdditiveOperator ao,
    MultiplicativeOperator mo,
    NumericValueExpression nve1,
    NumericValueExpression nve2,
    NumericValueExpression nve,
    Sign sign,
    ValueExpressionPrimary vep,
    NumericValueFunction nvf)
  {
    _il.enter(ao, mo, nve1, nve2, sign, vep, nvf);
    setAdditiveOperator(ao);
    setMultiplicativeOperator(mo);
    setFirstOperand(nve1);
    setSecondOperand(nve2);
    setParenthesizedExpression(nve);
    setSign(sign);
    setValueExpressionPrimary(vep);
    setNumericValueFunction(nvf);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public NumericValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class NumericValueExpression */
