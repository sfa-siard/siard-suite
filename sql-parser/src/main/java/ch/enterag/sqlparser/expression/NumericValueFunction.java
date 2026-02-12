package ch.enterag.sqlparser.expression;

import java.math.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.utils.*;
import ch.enterag.utils.logging.*;

public class NumericValueFunction
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(NumericValueFunction.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class NvfVisitor extends EnhancedSqlBaseVisitor<NumericValueFunction>
  {
    @Override
    public NumericValueFunction visitPositionExpression(SqlParser.PositionExpressionContext ctx)
    {
      setNumericFunction(NumericFunction.POSITION);
      setFirstStringValueExpression(getSqlFactory().newStringValueExpression());
      getFirstStringValueExpression().parse(ctx.stringValueExpression(0));
      setSecondStringValueExpression(getSqlFactory().newStringValueExpression());
      getSecondStringValueExpression().parse(ctx.stringValueExpression(1));
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitExtractExpression(SqlParser.ExtractExpressionContext ctx)
    {
      setNumericFunction(NumericFunction.EXTRACT);
      return visitChildren(ctx);
    }
    @Override
    public NumericValueFunction visitLengthExpression(SqlParser.LengthExpressionContext ctx)
    {
      if ((ctx.CHAR_LENGTH() != null) || (ctx.CHARACTER_LENGTH() != null))
        setNumericFunction(NumericFunction.CHARACTER_LENGTH);
      else if (ctx.OCTET_LENGTH() != null)
        setNumericFunction(NumericFunction.OCTET_LENGTH);
      setStringValueExpression(getSqlFactory().newStringValueExpression());
      getStringValueExpression().parse(ctx.stringValueExpression());
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitCardinalityExpression(SqlParser.CardinalityExpressionContext ctx)
    {
      setNumericFunction(NumericFunction.CARDINALITY);
      if (ctx.arrayValueExpression() != null)
      {
        setArrayValueExpression(getSqlFactory().newArrayValueExpression());
        getArrayValueExpression().parse(ctx.arrayValueExpression());
      }
      else if (ctx.multisetValueExpression() != null)
      {
        setMultisetValueExpression(getSqlFactory().newMultisetValueExpression());
        getMultisetValueExpression().parse(ctx.multisetValueExpression());
      }
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitAbsoluteValueExpression(SqlParser.AbsoluteValueExpressionContext ctx)
    {
      setNumericFunction(NumericFunction.ABS);
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression());
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitModulusExpression(SqlParser.ModulusExpressionContext ctx)
    {
      setNumericFunction(NumericFunction.MOD);
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression(0));
      setSecondNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getSecondNumericValueExpression().parse(ctx.numericValueExpression(1));
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitNaturalLogarithm(SqlParser.NaturalLogarithmContext ctx)
    {
      setNumericFunction(NumericFunction.LN);
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression());
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitExponentialFunction(SqlParser.ExponentialFunctionContext ctx)
    {
      setNumericFunction(NumericFunction.EXP);
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression());
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitPowerFunction(SqlParser.PowerFunctionContext ctx)
    {
      setNumericFunction(NumericFunction.POWER);
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression(0));
      setSecondNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getSecondNumericValueExpression().parse(ctx.numericValueExpression(1));
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitSquareRoot(SqlParser.SquareRootContext ctx)
    {
      setNumericFunction(NumericFunction.SQRT);
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression());
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitFloorFunction(SqlParser.FloorFunctionContext ctx)
    {
      setNumericFunction(NumericFunction.FLOOR);
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression());
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitCeilingFunction(SqlParser.CeilingFunctionContext ctx)
    {
      setNumericFunction(NumericFunction.CEILING);
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx.numericValueExpression());
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitWidthBucketFunction(SqlParser.WidthBucketFunctionContext ctx)
    {
      setNumericFunction(NumericFunction.WIDTH_BUCKET);
      setWidthBucketOperand(getSqlFactory().newNumericValueExpression());
      getWidthBucketOperand().parse(ctx.widthBucketOperand().numericValueExpression());
      setWidthBucketBound1(getSqlFactory().newNumericValueExpression());
      getWidthBucketBound1().parse(ctx.widthBucketBound1().numericValueExpression());
      setWidthBucketBound2(getSqlFactory().newNumericValueExpression());
      getWidthBucketBound2().parse(ctx.widthBucketBound2().numericValueExpression());
      setWidthBucketCount(getSqlFactory().newNumericValueExpression());
      getWidthBucketCount().parse(ctx.widthBucketCount().numericValueExpression());
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitExtractField(SqlParser.ExtractFieldContext ctx)
    {
      if (ctx.primaryDatetimeField() != null)
        setDatetimeField(getDatetimeField(ctx.primaryDatetimeField()));
      else if (ctx.timeZoneField() != null)
        setTimeZoneField(getTimeZoneField(ctx.timeZoneField()));
      return NumericValueFunction.this;
    }
    @Override 
    public NumericValueFunction visitDatetimeValueExpression(SqlParser.DatetimeValueExpressionContext ctx)
    {
      setDatetimeValueExpression(getSqlFactory().newDatetimeValueExpression());
      getDatetimeValueExpression().parse(ctx);
      return NumericValueFunction.this;
    }
    @Override
    public NumericValueFunction visitIntervalValueExpression(SqlParser.IntervalValueExpressionContext ctx)
    {
      setIntervalValueExpression(getSqlFactory().newIntervalValueExpression());
      getIntervalValueExpression().parse(ctx);
      return NumericValueFunction.this;
    }
  }
  /*==================================================================*/
  
  private NvfVisitor _visitor = new NvfVisitor();
  private NvfVisitor getVisitor() { return _visitor; }

  private NumericFunction _nf = null;
  public NumericFunction getNumericFunction() { return _nf; }
  public void setNumericFunction(NumericFunction nf) { _nf = nf; }
  
  /* POS */
  private StringValueExpression _sve1 = null;
  public StringValueExpression getFirstStringValueExpression() { return _sve1; }
  public void setFirstStringValueExpression(StringValueExpression sve1) { _sve1 = sve1; }
  
  private StringValueExpression _sve2 = null;
  public StringValueExpression getSecondStringValueExpression() { return _sve2; }
  public void setSecondStringValueExpression(StringValueExpression sve2) { _sve2 = sve2; }
  
  /* EXTRACT */
  private DatetimeField _df = null;
  public DatetimeField getDatetimeField() { return _df; }
  public void setDatetimeField(DatetimeField df) { _df = df; }
  
  private TimeZoneField _tf = null;
  public TimeZoneField getTimeZoneField() { return _tf; }
  public void setTimeZoneField(TimeZoneField tf) { _tf = tf; }
  
  private DatetimeValueExpression _dve = null;
  public DatetimeValueExpression getDatetimeValueExpression() { return _dve; }
  public void setDatetimeValueExpression(DatetimeValueExpression dve) { _dve = dve; }
  
  private IntervalValueExpression _ive = null;
  public IntervalValueExpression getIntervalValueExpression() { return _ive; }
  public void setIntervalValueExpression(IntervalValueExpression ive) { _ive = ive; }
  
  /* CHARACTER_LENGTH, OCTET_LENGTH */
  private StringValueExpression _sve = null;
  public StringValueExpression getStringValueExpression() { return _sve; }
  public void setStringValueExpression(StringValueExpression sve) { _sve = sve; }
  
  /* CARDINALITY */
  private ArrayValueExpression _ave = null;
  public ArrayValueExpression getArrayValueExpression() { return _ave; }
  public void setArrayValueExpression(ArrayValueExpression ave) { _ave = ave; }
  
  private MultisetValueExpression _mve = null;
  public MultisetValueExpression getMultisetValueExpression() { return _mve; }
  public void setMultisetValueExpression(MultisetValueExpression mve) { _mve = mve; }
  
  /* ABS, MOD, LN, EXP, POWER, SQRT, FLOOR, CEIL */
  private NumericValueExpression _nve = null;
  public NumericValueExpression getNumericValueExpression() { return _nve; }
  public void setNumericValueExpression(NumericValueExpression nve) { _nve = nve; }
  
  /* MOD, POWER */
  private NumericValueExpression _nve2 = null;
  public NumericValueExpression getSecondNumericValueExpression() { return _nve2; }
  public void setSecondNumericValueExpression(NumericValueExpression nve2) { _nve2 = nve2; }
  
  /* WIDTH_BUCKET */
  private NumericValueExpression _nveWidthBucketOperand = null;
  public NumericValueExpression getWidthBucketOperand() { return _nveWidthBucketOperand; }
  public void setWidthBucketOperand(NumericValueExpression nveWidthBucketOperand) { _nveWidthBucketOperand = nveWidthBucketOperand; }
  
  private NumericValueExpression _nveWidthBucketBound1 = null;
  public NumericValueExpression getWidthBucketBound1() { return _nveWidthBucketBound1; }
  public void setWidthBucketBound1(NumericValueExpression nveWidthBucketBound1) { _nveWidthBucketBound1 = nveWidthBucketBound1; }

  private NumericValueExpression _nveWidthBucketBound2 = null;
  public NumericValueExpression getWidthBucketBound2() { return _nveWidthBucketBound2; }
  public void setWidthBucketBound2(NumericValueExpression nveWidthBucketBound2) { _nveWidthBucketBound2 = nveWidthBucketBound2; }

  private NumericValueExpression _nveWidthBucketCount = null;
  public NumericValueExpression getWidthBucketCount() { return _nveWidthBucketCount; }
  public void setWidthBucketCount(NumericValueExpression nveWidthBucketCount) { _nveWidthBucketCount = nveWidthBucketCount; }
  
  /*------------------------------------------------------------------*/
  /** format the numeric value function.
   * @return the SQL string corresponding to the fields of the numeric 
   *   value function.
   */
  @Override
  public String format()
  {
    String sFunction = getNumericFunction().getKeywords() + sLEFT_PAREN;
    switch(getNumericFunction())
    {
      case POSITION:
        sFunction = sFunction + getFirstStringValueExpression().format() + sSP +
        K.IN.getKeyword() + sSP + getSecondStringValueExpression().format();
        break;
      case EXTRACT:
        if (getDatetimeField() != null)
          sFunction = sFunction + getDatetimeField().getKeywords();
        else if (getTimeZoneField() != null)
          sFunction = sFunction + getTimeZoneField().getKeywords();
        sFunction = sFunction + sSP + K.FROM.getKeyword();
        if (getDatetimeValueExpression() != null)
          sFunction = sFunction + sSP + getDatetimeValueExpression().format();
        else if (getIntervalValueExpression() != null)
          sFunction = sFunction + sSP + getIntervalValueExpression().format();
        break;
      case CHARACTER_LENGTH:
      case OCTET_LENGTH:
        sFunction = sFunction + getStringValueExpression().format();
        break;
      case CARDINALITY:
        if (getArrayValueExpression() != null)
          sFunction = sFunction + getArrayValueExpression().format();
        else if (getMultisetValueExpression() != null)
            sFunction = sFunction + getMultisetValueExpression().format();
        break;
      case ABS:
      case LN:
      case EXP:
      case SQRT:
      case FLOOR:
      case CEILING:
        sFunction = sFunction + getNumericValueExpression().format();
        break;
      case MOD:
      case POWER:
        sFunction = sFunction + getNumericValueExpression().format() + sCOMMA + sSP +
          getSecondNumericValueExpression().format();
        break;
      case WIDTH_BUCKET:
        sFunction = sFunction + getWidthBucketOperand().format() + sCOMMA + sSP +
          getWidthBucketBound1().format() + sCOMMA + sSP +
          getWidthBucketBound2().format() + sCOMMA + sSP +
          getWidthBucketCount().format();
        break;
    }
    sFunction = sFunction + sRIGHT_PAREN;
    return sFunction;
  } /* format */

  /*------------------------------------------------------------------*/
  /** return data type of a numeric value function from the context of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    DataType dtArgument = null;
    if (getNumericValueExpression() != null)
      dtArgument = getNumericValueExpression().getDataType(ss);
    dt = getSqlFactory().newDataType();
    PredefinedType pt = getSqlFactory().newPredefinedType();
    dt.initPredefinedDataType(pt);
    switch(getNumericFunction())
    {
      case FLOOR:
      case CEILING:
      case CHARACTER_LENGTH:
      case OCTET_LENGTH:
      case POSITION: pt.initIntegerType(); break;
      case EXTRACT:
        throw new IllegalArgumentException("DateTime function EXTRACT is not yet supported for evaluation!");
      case CARDINALITY:
        throw new IllegalArgumentException("Array value function CARDINALITY not supported!");
      case MOD:
      case POWER:
      case ABS: dt = dtArgument; break;
      case SQRT:
      case EXP:
      case LN: pt.initDoubleType(); break;
      case WIDTH_BUCKET:
        throw new IllegalArgumentException("Numeric function WIDTH_BUCKET not supported for evaluation!");
    }
    return dt;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  /** evaluate a numeric value function from its components.
   * @return numeric value (Double or BigDecimal).
   */
  private Object evaluate( 
    Object oNumericValue,
    Object oSecondNumericValue,
    Object oStringValue,
    Object oSecondStringValue)
  {
    Object oValue = null;
    BigDecimal bd = null;
    Double d = null;
    String s = null;
    byte[] buf = null;
    if (getNumericValueExpression() != null)
    {
      if (oNumericValue != null)
      {
        if (oNumericValue instanceof BigDecimal)
          bd = (BigDecimal)oNumericValue;
        else if (oNumericValue instanceof Double)
          d = (Double)oNumericValue;
        else
          throw new IllegalArgumentException("Numeric values must be exact or approximate!");
      }
    }
    else if (getStringValueExpression() != null)
    {
      if (oStringValue != null)
      {
        if (oStringValue instanceof String)
          s = (String)oStringValue;
        else if (oStringValue instanceof byte[])
          buf = (byte[])oStringValue;
        else
          throw new IllegalArgumentException("String values must be character or binary strings!");
      }
    }
    switch(getNumericFunction())
    {
      case POSITION:
        int iIndex = -1;
        if (s != null)
        {
          String sContainer = (String)oSecondStringValue;
          iIndex = sContainer.indexOf(s);
        }
        else if (buf != null)
        {
          byte[] bufContainer = (byte[])oSecondStringValue;
          iIndex = BU.indexOf(bufContainer, buf);
        }
        oValue = BigDecimal.valueOf(iIndex+1);
        break;
      case EXTRACT:
        throw new IllegalArgumentException("DateTime function EXTRACT is not yet supported for evaluation!");
      case CHARACTER_LENGTH:
        if (s != null)
          oValue = BigDecimal.valueOf(s.length());
        break;
      case OCTET_LENGTH:
        if (s != null)
          oValue = BigDecimal.valueOf(SU.putUtf8String(s).length);
        else if (buf != null)
          oValue = BigDecimal.valueOf(buf.length);
        break;
      case CARDINALITY:
        throw new IllegalArgumentException("Array value function CARDINALITY not supported!");
      case ABS:
        if (bd != null)
          oValue = bd.abs();
        else if (d != null)
          oValue = Double.valueOf(Math.abs(d.doubleValue()));
        break;
      case LN:
        if (bd != null)
          oValue = BigDecimal.valueOf(Math.log(bd.doubleValue()));
        else if (d != null)
          oValue = Double.valueOf(Math.log(d.doubleValue()));
        break;
      case EXP:
        if (bd != null)
          oValue = BigDecimal.valueOf(Math.exp(bd.doubleValue()));
        else if (d != null)
          oValue = Double.valueOf(Math.exp(d.doubleValue()));
        break;
      case SQRT:
        if (bd != null)
          oValue = BigDecimal.valueOf(Math.sqrt(bd.doubleValue()));
        else if (d != null)
          oValue = Double.valueOf(Math.sqrt(d.doubleValue()));
        break;
      case FLOOR:
        if (bd != null)
          oValue = bd.setScale(0,RoundingMode.DOWN);
        else if (d != null)
          oValue = Double.valueOf(Math.floor(d));
        break;
      case CEILING:
        if (bd != null)
          oValue = bd.setScale(0,RoundingMode.UP);
        else if (d != null)
          oValue = Double.valueOf(Math.ceil(d));
        break;
      case MOD:
        BigDecimal bdDivisor = (BigDecimal)oSecondNumericValue;
        if ((bd != null) && (bdDivisor != null))
          oValue = bd.remainder(bdDivisor);
        break;
      case POWER:
        int iPower = getSecondNumericValueExpression().evaluateInteger(oSecondNumericValue);
        if (bd != null)
          oValue = bd.pow(iPower);
        else if (d != null)
        {
          double dValue = 1.0;
          for (int i = 0; i < iPower; i++)
            dValue = dValue*d.doubleValue();
          oValue = Double.valueOf(dValue);
        }
        else
          throw new IllegalArgumentException("Numeric values must be exact or approximate!");
        break;
      case WIDTH_BUCKET:
        throw new IllegalArgumentException("Numeric function WIDTH_BUCKET not supported for evaluation!");
    }
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate a numeric value function against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return numeric value (Double or BigDecimal).
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oNumericValue = null;
    if (getNumericValueExpression() != null)
      oNumericValue = getNumericValueExpression().evaluate(ss, bAggregated);
    Object oSecondNumericValue = null;
    if (getSecondNumericValueExpression() != null)
      oSecondNumericValue = getSecondNumericValueExpression().evaluate(ss, bAggregated);
    Object oStringValue = null;
    if (getStringValueExpression() != null)
      oStringValue = getStringValueExpression().evaluate(ss, bAggregated);
    Object oSecondStringValue = null;
    if (getSecondStringValueExpression() != null)
      oSecondStringValue = getSecondStringValueExpression().evaluate(ss, bAggregated);
    Object oValue = evaluate(oNumericValue, oSecondNumericValue, oStringValue, oSecondStringValue);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a numeric value function to 
   * their initial value.
   * @param ss sql statement.
   * @return final value before reset.
   */
  public Object resetAggregates(SqlStatement ss)
  {
    Object oNumericValue = null;
    if (getNumericValueExpression() != null)
      oNumericValue = getNumericValueExpression().resetAggregates(ss);
    Object oSecondNumericValue = null;
    if (getSecondNumericValueExpression() != null)
      oSecondNumericValue = getSecondNumericValueExpression().resetAggregates(ss);
    Object oStringValue = null;
    if (getStringValueExpression() != null)
      oStringValue = getStringValueExpression().resetAggregates(ss);
    Object oSecondStringValue = null;
    if (getSecondStringValueExpression() != null)
      oSecondStringValue = getSecondStringValueExpression().resetAggregates(ss);
    Object oValue = evaluate(oNumericValue, oSecondNumericValue, oStringValue, oSecondStringValue);
    return oValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the numeric value function from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.NumericValueFunctionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the numeric value function from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().numericValueFunction());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a numeric value function.
   * @param nf numeric function.
   * @param sve1 first argument to POSITION.
   * @param sve2 second argument to POSITION.
   * @param df argument to EXTRACT.
   * @param tzf argument to EXTRACT.
   * @param dve argument to EXTRACT.
   * @param ive argument to EXTRACT.
   * @param sve argument to CHARACTER_LENGTH, OCTET_LENGTH
   * @param ave argument to CARDINALITY.
   * @param mve argument to CARDINALITY.
   * @param nve (first) argument to ABS, MOD, LN, EXP, POWER, SQRT, FLOOR, CEILING.
   * @param nve2 second argument to MOD, POWER.
   * @param nveWidthBucketOperand WIDTH_BUCKET operand.
   * @param nveWidthBucketBound1 WIDTH_BUCKET first bound.
   * @param nveWidthBucketBound2 WIDTH_BUCKET second bound.
   * @param nveWidthBucketCount WIDTH_BUCKET count.
   */
  public void initialize(
    NumericFunction nf,
    StringValueExpression sve1,
    StringValueExpression sve2,
    DatetimeField df,
    TimeZoneField tzf,
    DatetimeValueExpression dve,
    IntervalValueExpression ive,
    StringValueExpression sve,
    ArrayValueExpression ave,
    MultisetValueExpression mve,
    NumericValueExpression nve,
    NumericValueExpression nve2,
    NumericValueExpression nveWidthBucketOperand,
    NumericValueExpression nveWidthBucketBound1,
    NumericValueExpression nveWidthBucketBound2,
    NumericValueExpression nveWidthBucketCount)
  {
    _il.enter(nf, sve1, sve2, df, tzf, dve, ive, sve, ave, mve, nve, nve2, 
        nveWidthBucketOperand, nveWidthBucketBound1, nveWidthBucketBound2, 
        nveWidthBucketCount);
    setNumericFunction(nf);
    setFirstStringValueExpression(sve1);
    setSecondStringValueExpression(sve2);
    setDatetimeField(df);
    setTimeZoneField(tzf);
    setDatetimeValueExpression(dve);
    setIntervalValueExpression(ive);
    setStringValueExpression(sve);
    setArrayValueExpression(ave);
    setMultisetValueExpression(mve);
    setNumericValueExpression(nve);
    setSecondNumericValueExpression(nve2);
    setWidthBucketOperand(nveWidthBucketOperand);
    setWidthBucketBound1(nveWidthBucketBound1);
    setWidthBucketBound2(nveWidthBucketBound2);
    setWidthBucketCount(nveWidthBucketCount);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public NumericValueFunction(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class NumericValueFunction */
