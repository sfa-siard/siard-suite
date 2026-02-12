package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;

public class CommonValueExpression
extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CommonValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CveVisitor extends EnhancedSqlBaseVisitor<CommonValueExpression>
  {
    @Override
    public CommonValueExpression visitNumericValueExpression(SqlParser.NumericValueExpressionContext ctx)
    {
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx);
      return CommonValueExpression.this;
    }
    @Override
    public CommonValueExpression visitStringValueExpression(SqlParser.StringValueExpressionContext ctx)
    {
      setStringValueExpression(getSqlFactory().newStringValueExpression());
      getStringValueExpression().parse(ctx);
      return CommonValueExpression.this;
    }
    @Override
    public CommonValueExpression visitDatetimeValueExpression(SqlParser.DatetimeValueExpressionContext ctx)
    {
      setDatetimeValueExpression(getSqlFactory().newDatetimeValueExpression());
      getDatetimeValueExpression().parse(ctx);
      return CommonValueExpression.this;
    }
    @Override
    public CommonValueExpression visitIntervalValueExpression(SqlParser.IntervalValueExpressionContext ctx)
    {
      setIntervalValueExpression(getSqlFactory().newIntervalValueExpression());
      getIntervalValueExpression().parse(ctx);
      return CommonValueExpression.this;
    }
    @Override
    public CommonValueExpression visitArrayValueExpression(SqlParser.ArrayValueExpressionContext ctx)
    {
      setArrayValueExpression(getSqlFactory().newArrayValueExpression());
      getArrayValueExpression().parse(ctx);
      return CommonValueExpression.this;
    }
    @Override
    public CommonValueExpression visitMultisetValueExpression(SqlParser.MultisetValueExpressionContext ctx)
    {
      setMultisetValueExpression(getSqlFactory().newMultisetValueExpression());
      getMultisetValueExpression().parse(ctx);
      return CommonValueExpression.this;
    }
    @Override
    public CommonValueExpression visitValueExpressionPrimary(SqlParser.ValueExpressionPrimaryContext ctx)
    {
      setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
      getValueExpressionPrimary().parse(ctx);
      return CommonValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private CveVisitor _visitor = new CveVisitor();
  private CveVisitor getVisitor() { return _visitor; }

  private NumericValueExpression _nve = null;
  public NumericValueExpression getNumericValueExpression() { return _nve; }
  public void setNumericValueExpression(NumericValueExpression nve) { _nve = nve; }
  
  private StringValueExpression _sve = null;
  public StringValueExpression getStringValueExpression() { return _sve; }
  public void setStringValueExpression(StringValueExpression sve) { _sve = sve; }
  
  private DatetimeValueExpression _dve = null;
  public DatetimeValueExpression getDatetimeValueExpression() { return _dve; }
  public void setDatetimeValueExpression(DatetimeValueExpression dve) { _dve = dve; }
  
  private IntervalValueExpression _ive = null;
  public IntervalValueExpression getIntervalValueExpression() { return _ive; }
  public void setIntervalValueExpression(IntervalValueExpression ive) { _ive = ive; }
  
  private ArrayValueExpression _ave = null;
  public ArrayValueExpression getArrayValueExpression() { return _ave; }
  public void setArrayValueExpression(ArrayValueExpression ave) { _ave = ave; }
  
  private MultisetValueExpression _mve = null;
  public MultisetValueExpression getMultisetValueExpression() { return _mve; }
  public void setMultisetValueExpression(MultisetValueExpression mve) { _mve = mve; }

  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }
  
  /*------------------------------------------------------------------*/
  /** format the common value expression.
   * @return the SQL string corresponding to the fields of the common 
   *   value expression.
   */
  @Override
  public String format()
  {
    String sExpression = null;
    if (getNumericValueExpression() != null)
      sExpression = getNumericValueExpression().format();
    else if (getStringValueExpression() != null)
      sExpression = getStringValueExpression().format();
    else if (getDatetimeValueExpression() != null)
      sExpression = getDatetimeValueExpression().format();
    else if (getIntervalValueExpression() != null)
      sExpression = getIntervalValueExpression().format();
    else if (getArrayValueExpression() != null)
      sExpression = getArrayValueExpression().format();
    else if (getMultisetValueExpression() != null)
      sExpression = getMultisetValueExpression().format();
    else if (getValueExpressionPrimary() != null)
      sExpression = getValueExpressionPrimary().format();
    return sExpression;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** return the data type of the common value expression from the context 
   * of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (getNumericValueExpression() != null)
      dt = getNumericValueExpression().getDataType(ss);
    else if (getStringValueExpression() != null)
      dt = getStringValueExpression().getDataType(ss);
    else if (getDatetimeValueExpression() != null)
      dt = getDatetimeValueExpression().getDataType(ss);
    else if (getIntervalValueExpression() != null)
      dt = getIntervalValueExpression().getDataType(ss);
    else if (getArrayValueExpression() != null)
      new IllegalArgumentException("Array value expressions are not supported for evaluation!");
    else if (getMultisetValueExpression() != null)
      new IllegalArgumentException("Multiset value expressions are not supported for evaluation!");
    else if (getValueExpressionPrimary() != null)
      dt = getValueExpressionPrimary().getDataType(ss);
    return dt;
  } /* getDataType */
  
  /*------------------------------------------------------------------*/
  /** evaluate a common value expression from its components.
   * @return value.
   */
  private Object evaluate(
    Object oNumericValue,
    Object oStringValue,
    Object oDatetimeValue,
    Interval ivIntervalValue,
    Object oValuePrimary)
  {
    Object oValue = null;
    if (getNumericValueExpression() != null)
      oValue = oNumericValue;
    else if (getStringValueExpression() != null)
      oValue = oStringValue;
    else if (getDatetimeValueExpression() != null)
      oValue = oDatetimeValue;
    else if (getIntervalValueExpression() != null)
      oValue = ivIntervalValue;
    else if (getArrayValueExpression() != null)
      throw new IllegalArgumentException("Array value expressions are not supported for evaluation!");
    else if (getMultisetValueExpression() != null)
      throw new IllegalArgumentException("Multiset value expressions are not supported for evaluation!");
    else if (getValueExpressionPrimary() != null)
      oValue = oValuePrimary;
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate the common value expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return value.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oNumericValue = null;
    if (getNumericValueExpression() != null)
      oNumericValue = getNumericValueExpression().evaluate(ss, bAggregated);
    Object oStringValue = null;
    if (getStringValueExpression() != null)
      oStringValue = getStringValueExpression().evaluate(ss, bAggregated);
    Object oDatetimeValue = null;
    if (getDatetimeValueExpression() != null)
      oDatetimeValue = getDatetimeValueExpression().evaluate(ss, bAggregated);
    Interval ivIntervalValue = null;
    if (getIntervalValueExpression() != null)
      ivIntervalValue = getIntervalValueExpression().evaluate(ss, bAggregated);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().evaluate(ss, bAggregated);
    Object oValue = evaluate(oNumericValue, oStringValue, oDatetimeValue, ivIntervalValue, oValuePrimary);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a common value expression to 
   * their initial value.
   * @param ss sql statement.
   * @return final value before reset.
   */
  public Object resetAggregates(SqlStatement ss)
  {
    Object oNumericValue = null;
    if (getNumericValueExpression() != null)
      oNumericValue = getNumericValueExpression().resetAggregates(ss);
    Object oStringValue = null;
    if (getStringValueExpression() != null)
      oStringValue = getStringValueExpression().resetAggregates(ss);
    Object oDatetimeValue = null;
    if (getDatetimeValueExpression() != null)
      oDatetimeValue = getDatetimeValueExpression().resetAggregates(ss);
    Interval ivIntervalValue = null;
    if (getIntervalValueExpression() != null)
      ivIntervalValue = getIntervalValueExpression().resetAggregates(ss);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().resetAggregates(ss);
    Object oValue = evaluate(oNumericValue, oStringValue, oDatetimeValue, ivIntervalValue, oValuePrimary);
    return oValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the common value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.CommonValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the common value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().commonValueExpression());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a common value expression.
   * Only one parameter not null!
   * @param nve numeric value expression.
   * @param sve string value expression.
   * @param dve date time value expression.
   * @param ive interval value expression.
   * @param ave array value expression.
   * @param mve multiset value expression.
   * @param vep value expression primary.
   */
  public void initialize(
    NumericValueExpression nve,
    StringValueExpression sve,
    DatetimeValueExpression dve,
    IntervalValueExpression ive,
    ArrayValueExpression ave,
    MultisetValueExpression mve,
    ValueExpressionPrimary vep)
  {
    _il.enter(nve, sve, dve, ive, ave, mve, vep);
    setNumericValueExpression(nve);
    setStringValueExpression(sve);
    setDatetimeValueExpression(dve);
    setIntervalValueExpression(ive);
    setArrayValueExpression(ave);
    setMultisetValueExpression(mve);
    setValueExpressionPrimary(vep);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CommonValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CommonValueExpression */
