package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class ValueExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(ValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class VeVisitor extends EnhancedSqlBaseVisitor<ValueExpression>
  {
    @Override
    public ValueExpression visitCommonValueExpression(SqlParser.CommonValueExpressionContext ctx)
    {
      setCommonValueExpression(getSqlFactory().newCommonValueExpression());
      getCommonValueExpression().parse(ctx);
      return ValueExpression.this;
    }
    @Override
    public ValueExpression visitBooleanValueExpression(SqlParser.BooleanValueExpressionContext ctx)
    {
      setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
      getBooleanValueExpression().parse(ctx);
      return ValueExpression.this;
    }
    @Override
    public ValueExpression visitRowValueExpression(SqlParser.RowValueExpressionContext ctx)
    {
      setRowValueExpression(getSqlFactory().newRowValueExpression());
      getRowValueExpression().parse(ctx);
      return ValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private VeVisitor _visitor = new VeVisitor();
  private VeVisitor getVisitor() { return _visitor; }
  
  private CommonValueExpression _cve = null;
  public CommonValueExpression getCommonValueExpression() { return _cve; }
  public void setCommonValueExpression(CommonValueExpression cve) { _cve = cve; }
  
  private BooleanValueExpression _bve = null;
  public BooleanValueExpression getBooleanValueExpression() { return _bve; }
  public void setBooleanValueExpression(BooleanValueExpression bve) { _bve = bve; }
  
  private RowValueExpression _rve = null;
  public RowValueExpression getRowValueExpression() { return _rve; }
  public void setRowValueExpression(RowValueExpression rve) { _rve = rve; }
  
  /*------------------------------------------------------------------*/
  /** format the value expression.
   * @return the SQL string corresponding to the fields of the value expression.
   */
  @Override
  public String format()
  {
    String sExpression = null;
    if (getCommonValueExpression() != null)
      sExpression = getCommonValueExpression().format();
    else if (getBooleanValueExpression() != null)
      sExpression = getBooleanValueExpression().format();
    else if (getRowValueExpression() != null)
      sExpression = getRowValueExpression().format();
    return sExpression;
  } /* format */

  /*------------------------------------------------------------------*/
  /** get data type of a value expression from the context of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (getCommonValueExpression() != null)
      dt = getCommonValueExpression().getDataType(ss);
    else if (getBooleanValueExpression() != null)
      dt = getBooleanValueExpression().getDataType(ss);
    else if (getRowValueExpression() != null)
      dt = getRowValueExpression().getDataType(ss);
    return dt;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  /** evaluate a value expression from its components.
   * @return value.
   */
  private Object evaluate(Object oCommonValue, Boolean bBooleanValue, Object oRowValue)
  {
    Object oValue = null;
    if (getCommonValueExpression() != null)
      oValue = oCommonValue;
    else if (getBooleanValueExpression() != null)
      oValue = bBooleanValue;
    else if (getRowValueExpression() != null)
      oValue = oRowValue;
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate a value expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return value.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oCommonValue = null;
    if (getCommonValueExpression() != null)
      oCommonValue = getCommonValueExpression().evaluate(ss, bAggregated);
    Boolean bBooleanValue = null;
    if (getBooleanValueExpression() != null)
      bBooleanValue = getBooleanValueExpression().evaluate(ss, bAggregated);
    Object oRowValue = null;
    if (getRowValueExpression() != null)
      oRowValue = getRowValueExpression().evaluate(ss, bAggregated);
    Object oValue = evaluate(oCommonValue, bBooleanValue, oRowValue);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a value expression to their initial value.
   * @param ss sql statement.
   * @return final value before reset.
   */
  public Object resetAggregates(SqlStatement ss)
  {
    Object oCommonValue = null;
    if (getCommonValueExpression() != null)
      oCommonValue = getCommonValueExpression().resetAggregates(ss);
    Boolean bBooleanValue = null;
    if (getBooleanValueExpression() != null)
      bBooleanValue = getBooleanValueExpression().resetAggregates(ss);
    Object oRowValue = null;
    if (getRowValueExpression() != null)
      oRowValue = getRowValueExpression().resetAggregates(ss);
    Object oValue = evaluate(oCommonValue, bBooleanValue, oRowValue);
    return oValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.ValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().valueExpression());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a value expression.
   * @param cve common value expression.
   * @param bve boolean value expression.
   * @param rve row value expression.
   */
  public void initialize(
    CommonValueExpression cve,
    BooleanValueExpression bve,
    RowValueExpression rve)
  {
    _il.enter(cve, bve, rve);
    setCommonValueExpression(cve);
    setBooleanValueExpression(bve);
    setRowValueExpression(rve);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public ValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class ValueExpression */
