package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class IntervalValueExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(IntervalValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class IveVisitor extends EnhancedSqlBaseVisitor<IntervalValueExpression>
  {
    @Override
    public IntervalValueExpression visitIntervalValueExpression(SqlParser.IntervalValueExpressionContext ctx)
    {
      IntervalValueExpression iveReturn = IntervalValueExpression.this;
      if (ctx.sign() != null)
        setSign(getSign(ctx.sign()));
      if (ctx.valueExpressionPrimary() != null)
      {
        setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
        getValueExpressionPrimary().parse(ctx.valueExpressionPrimary());
        if (ctx.intervalQualifier() != null)
        {
          setIntervalQualifier(getSqlFactory().newIntervalQualifier());
          getIntervalQualifier().parse(ctx.intervalQualifier());
        }
      }
      else if ((ctx.ABS() != null) && (ctx.intervalValueExpression().size() == 1))
      {
        setAbsArgument(getSqlFactory().newIntervalValueExpression());
        getAbsArgument().parse(ctx.intervalValueExpression(0));
      }
      else if ((ctx.ASTERISK() != null) && 
               (ctx.numericValueExpression() != null) && 
               (ctx.intervalValueExpression().size() == 1))
      {
        setFactor(getSqlFactory().newNumericValueExpression());
        getFactor().parse(ctx.numericValueExpression());
        setSecondOperand(getSqlFactory().newIntervalValueExpression());
        getSecondOperand().parse(ctx.intervalValueExpression(0));
      }
      else if ((ctx.numericValueExpression() != null) && (ctx.intervalValueExpression().size() == 1))
      {
        if (ctx.additiveOperator() != null)
          setAdditiveOperator(getAdditiveOperator(ctx.additiveOperator()));
        else if (ctx.multiplicativeOperator() != null)
          setMultiplicativeOperator(getMultiplicativeOperator(ctx.multiplicativeOperator()));
        setFirstOperand(getSqlFactory().newIntervalValueExpression());
        getFirstOperand().parse(ctx.intervalValueExpression(0));
        setFactor(getSqlFactory().newNumericValueExpression());
        getFactor().parse(ctx.numericValueExpression());
      }
      else if (ctx.intervalValueExpression().size() == 2)
      {
        if (ctx.additiveOperator() != null)
          setAdditiveOperator(getAdditiveOperator(ctx.additiveOperator()));
        setFirstOperand(getSqlFactory().newIntervalValueExpression());
        getFirstOperand().parse(ctx.intervalValueExpression(0));
        setSecondOperand(getSqlFactory().newIntervalValueExpression());
        getSecondOperand().parse(ctx.intervalValueExpression(1));
      }
      else if ((ctx.datetimeValueExpression().size() == 2) && (ctx.MINUS_SIGN() != null))
      {
        setMinuend(getSqlFactory().newDatetimeValueExpression());
        getMinuend().parse(ctx.datetimeValueExpression(0));
        setSubtrahend(getSqlFactory().newDatetimeValueExpression());
        getSubtrahend().parse(ctx.datetimeValueExpression(1));
        setIntervalQualifier(getSqlFactory().newIntervalQualifier());
        getIntervalQualifier().parse(ctx.intervalQualifier());
      }
      else
        iveReturn = visitChildren(ctx);
      return iveReturn;
    }
  }
  /*==================================================================*/
  
  private IveVisitor _visitor = new IveVisitor();
  private IveVisitor getVisitor() { return _visitor; }

  private Sign _sign = null;
  public Sign getSign() { return _sign; }
  public void setSign(Sign sign) { _sign = sign; }
  
  private AdditiveOperator _ao = null;
  public AdditiveOperator getAdditiveOperator() { return _ao; }
  public void setAdditiveOperator(AdditiveOperator ao) { _ao = ao; }
  
  private MultiplicativeOperator _mo = null;
  public MultiplicativeOperator getMultiplicativeOperator() { return _mo; }
  public void setMultiplicativeOperator(MultiplicativeOperator mo) { _mo = mo; }
  
  private IntervalValueExpression _ive1 = null;
  public IntervalValueExpression getFirstOperand() { return _ive1; }
  public void setFirstOperand(IntervalValueExpression nve1) { _ive1 = nve1; }
  
  private IntervalValueExpression _ive2 = null;
  public IntervalValueExpression getSecondOperand() { return _ive2; }
  public void setSecondOperand(IntervalValueExpression nve2) { _ive2 = nve2; }
  
  private DatetimeValueExpression _dve1 = null;
  public DatetimeValueExpression getMinuend() { return _dve1; }
  public void setMinuend(DatetimeValueExpression dve1) { _dve1 = dve1; }

  private DatetimeValueExpression _dve2 = null;
  public DatetimeValueExpression getSubtrahend() { return _dve2; }
  public void setSubtrahend(DatetimeValueExpression dve2) { _dve2 = dve2; }
  
  private NumericValueExpression _nveFactor = null;
  public  NumericValueExpression getFactor() { return _nveFactor; }
  public void setFactor(NumericValueExpression nveFactor) { _nveFactor = nveFactor; }
  
  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }

  private IntervalQualifier _iq = null;
  public IntervalQualifier getIntervalQualifier() { return _iq; }
  public void setIntervalQualifier(IntervalQualifier iq) { _iq = iq; }
  
  private IntervalValueExpression _iveAbsArgument = null;
  public IntervalValueExpression getAbsArgument() { return _iveAbsArgument; }
  public void setAbsArgument(IntervalValueExpression iveAbsArgument) { _iveAbsArgument = iveAbsArgument; }
  
  /*------------------------------------------------------------------*/
  /** format the interval value expression.
   * @return the SQL string corresponding to the fields of the interval 
   *   value expression.
   */
  @Override
  public String format()
  {
    String sExpression = "";
    if (getValueExpressionPrimary() != null)
    {
      if (getSign() != null)
        sExpression = getSign().getKeywords();
      sExpression = sExpression + getValueExpressionPrimary().format();
      if (getIntervalQualifier() != null)
        sExpression = sExpression + sSP + getIntervalQualifier().format();
    }
    else if (getAbsArgument() != null)
    {
      if (getSign() != null)
        sExpression = getSign().getKeywords();
      sExpression = sExpression + NumericFunction.ABS.getKeywords() +
          sLEFT_PAREN + getAbsArgument().format() + sRIGHT_PAREN;
    }
    else if ((getFactor() != null) && (getSecondOperand() != null))
      sExpression = getFactor().format() + sASTERISK + getSecondOperand().format();
    else if ((getFirstOperand() != null) && (getFactor() != null))
    {
      sExpression = sExpression + getFirstOperand().format();
      if (getAdditiveOperator() != null)
        sExpression = sExpression + sSP + getAdditiveOperator().getKeywords() + sSP;
      else if (getMultiplicativeOperator() != null)
        sExpression = sExpression + getMultiplicativeOperator().getKeywords();
      sExpression = sExpression + getFactor().format();
    }
    else if ((getAdditiveOperator() != null) && 
             (getFirstOperand() != null) && 
             (getSecondOperand() != null))
    {
      sExpression = getFirstOperand().format() + sSP + 
        getAdditiveOperator().getKeywords() + sSP + 
        getSecondOperand().format();
    }
    else if ((getMinuend() != null) && 
             (getSubtrahend() != null) && 
             (getIntervalQualifier() != null))
    {
      sExpression = getMinuend().format() + sSP + 
        AdditiveOperator.MINUS_SIGN.getKeywords() + sSP + 
        getSubtrahend().format() + sSP +
        getIntervalQualifier().format();
    }
    return sExpression;
  } /* format */

  /*------------------------------------------------------------------*/
  /** return data type of an interval value expression from the context 
   * of a query.
   * @param ss sql statement.
   * @return data type of interval value.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (getValueExpressionPrimary() != null)
    {
      dt = getValueExpressionPrimary().getDataType(ss);
      if ((dt != null) && (dt.getPredefinedType().getType() != PreType.INTERVAL))
        throw new IllegalArgumentException("Interval expression must be of INTERVAL type!");
    }
    else if (getAbsArgument() != null)
    {
      dt = getAbsArgument().getDataType(ss);
      if ((dt != null) && (dt.getPredefinedType().getType() != PreType.INTERVAL))
        throw new IllegalArgumentException("Interval expression must be of INTERVAL type!");
    }
    else if ((getFactor() != null) && (getSecondOperand() != null))
      throw new IllegalArgumentException("Multiplication of intervals not supported for evaluation!");
    else if ((getFirstOperand() != null) && (getFactor() != null))
      throw new IllegalArgumentException("Adding or multiplying an interval and a number not supported for evaluation!");
    else if ((getAdditiveOperator() != null) && 
             (getFirstOperand() != null) && 
             (getSecondOperand() != null))
      throw new IllegalArgumentException("Addition of intervals not supported for evaluation!"); 
    else if ((getMinuend() != null) && 
             (getSubtrahend() != null) && 
             (getIntervalQualifier() != null))
      throw new IllegalArgumentException("Subtraction of intervals not supported for evaluation!"); 
    return dt;
  } /* getDataType */
  
  /*------------------------------------------------------------------*/
  /** evaluate an interval value expression from its components.
   * @return interval value.
   */
  private Interval evaluate(
    Object oValuePrimary,
    Interval ivAbsArgument)
  {
    Interval ivValue = null;
    if (getValueExpressionPrimary() != null)
    {
      Object oValue = oValuePrimary;
      if (oValue != null)
      {
        if (oValue instanceof Interval)
        {
          ivValue = (Interval)oValue;
          if (Sign.MINUS_SIGN == getSign())
            ivValue.setSign(-ivValue.getSign());
        }
        else
          throw new IllegalArgumentException("Cannot cast to interval!");
      }
    }
    else if (getAbsArgument() != null)
    {
      ivValue = ivAbsArgument;
      if (ivValue != null)
      {
        ivValue.setSign(1);
        if (Sign.MINUS_SIGN == getSign())
          ivValue.setSign(-1);
      }
    }
    else if ((getFactor() != null) && (getSecondOperand() != null))
      throw new IllegalArgumentException("Multiplication of intervals not supported for evaluation!");
    else if ((getFirstOperand() != null) && (getFactor() != null))
      throw new IllegalArgumentException("Adding or multiplying an interval and a number not supported for evaluation!");
    else if ((getAdditiveOperator() != null) && 
             (getFirstOperand() != null) && 
             (getSecondOperand() != null))
      throw new IllegalArgumentException("Addition of intervals not supported for evaluation!"); 
    else if ((getMinuend() != null) && 
             (getSubtrahend() != null) && 
             (getIntervalQualifier() != null))
      throw new IllegalArgumentException("Subtraction of intervals not supported for evaluation!"); 
    return ivValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate an interval value expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return interval value.
   */
  public Interval evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().evaluate(ss, bAggregated);
    Interval ivAbsArgument = null;
    if (getAbsArgument() != null)
      ivAbsArgument = getAbsArgument().evaluate(ss, bAggregated);
    Interval ivValue = evaluate(oValuePrimary, ivAbsArgument);
    return ivValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate an interval value expression against the context of a query.
   * @param ss sql statement.
   * @return interval value.
   */
  public Interval resetAggregates(SqlStatement ss)
  {
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().resetAggregates(ss);
    Interval ivAbsArgument = null;
    if (getAbsArgument() != null)
      ivAbsArgument = getAbsArgument().resetAggregates(ss);
    Interval ivValue = evaluate(oValuePrimary, ivAbsArgument);
    return ivValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the interval value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.IntervalValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the interval value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    SqlParser.IntervalValueExpressionContext ctx = null; 
    try { ctx = getParser().intervalValueExpression(); }
    catch(Exception e)
    {
      setParser(newSqlParser2(sSql));
      ctx = getParser().intervalValueExpression();
    }
    parse(ctx);
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize an interval value expression.
   * @param ao additive operator (or null).
   * @param mo multiplicative operator (or null).
   * @param ive1 first operand (or null).
   * @param ive2 second operand (or null).
   * @param dve1 minuend (or null).
   * @param dve2 subtrahend (or null).
   * @param nveFactor factor (or null).
   * @param sign sign (or null).
   * @param vep value expression primary (or null).
   * @param iq interval qualifier (or null).
   * @param iveAbsArgument interval argument to ABS function.
   */
  public void initialize(
    AdditiveOperator ao,
    MultiplicativeOperator mo,
    IntervalValueExpression ive1,
    IntervalValueExpression ive2,
    DatetimeValueExpression dve1,
    DatetimeValueExpression dve2,
    NumericValueExpression nveFactor,
    Sign sign,
    ValueExpressionPrimary vep,
    IntervalQualifier iq,
    IntervalValueExpression iveAbsArgument)
  {
    _il.enter(ao, mo, ive1, ive2, dve1, dve2, nveFactor, vep, iq, iveAbsArgument);
    setAdditiveOperator(ao);
    setMultiplicativeOperator(mo);
    setFirstOperand(ive1);
    setSecondOperand(ive2);
    setMinuend(dve1);
    setSubtrahend(dve2);
    setFactor(nveFactor);
    setSign(sign);
    setValueExpressionPrimary(vep);
    setIntervalQualifier(iq);
    setAbsArgument(iveAbsArgument);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public IntervalValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class IntervalValueExpression */
