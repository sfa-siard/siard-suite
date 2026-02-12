package ch.enterag.sqlparser.expression;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class RowValueExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(RowValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class RveVisitor extends EnhancedSqlBaseVisitor<RowValueExpression>
  {
    @Override
    public RowValueExpression visitRowValueExpression(SqlParser.RowValueExpressionContext ctx)
    {
      if (ctx.ROW() != null)
        setRow(true);
      return visitChildren(ctx);
    }
    @Override
    public RowValueExpression visitValueExpressionPrimary(SqlParser.ValueExpressionPrimaryContext ctx)
    {
      setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
      getValueExpressionPrimary().parse(ctx);
      return RowValueExpression.this;
    }
    @Override 
    public RowValueExpression visitValueExpression(SqlParser.ValueExpressionContext ctx)
    {
      ValueExpression ve = getSqlFactory().newValueExpression();
      ve.parse(ctx);
      getValueExpressions().add(ve);
      return RowValueExpression.this;
    }
    @Override
    public RowValueExpression visitQueryExpression(SqlParser.QueryExpressionContext ctx)
    {
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx);
      return RowValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private RveVisitor _visitor = new RveVisitor();
  private RveVisitor getVisitor() { return _visitor; }

  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }
  
  private List<ValueExpression> _listVe = new ArrayList<ValueExpression>();
  public List<ValueExpression> getValueExpressions() { return _listVe; }
  private void setValueExpressions(List<ValueExpression> listVe) { _listVe = listVe; }
  
  private boolean _bRow = false;
  public boolean isRow() { return _bRow; }
  public void setRow(boolean bRow){ _bRow = bRow; }
  
  private QueryExpression _qe = null;
  public QueryExpression getQueryExpression() { return _qe; }
  public void setQueryExpression(QueryExpression qe) { _qe = qe; }
  
  /*------------------------------------------------------------------*/
  /** format the row value expression.
   * @return the SQL string corresponding to the fields of the row value expression.
   */
  @Override
  public String format()
  {
    String sExpression = "";
    if (getValueExpressionPrimary() != null)
      sExpression = getValueExpressionPrimary().format();
    else if (getQueryExpression() != null)
      sExpression = getQueryExpression().format();
    else
    {
      if (isRow())
        sExpression = K.ROW.getKeyword();
      if (getValueExpressions().size() > 0)
      {
        sExpression = sExpression + sLEFT_PAREN;
        for (int i = 0; i < getValueExpressions().size(); i++)
        {
          if (i > 0)
            sExpression = sExpression + sCOMMA + sSP;
          sExpression = sExpression + getValueExpressions().get(i).format();
        }
        sExpression = sExpression + sRIGHT_PAREN;
      }
    }
    return sExpression;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** return data type of a row value expression from the context of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (getValueExpressionPrimary() != null)
      dt  = getValueExpressionPrimary().getDataType(ss);
    else if (getQueryExpression() != null)
      throw new IllegalArgumentException("Query expression not supported for evaluation!");
    else
    {
      if (isRow())
        throw new IllegalArgumentException("ROW expression not supported for evaluation!");
      if (getValueExpressions().size() > 0)
        throw new IllegalArgumentException("List of expressions not supported for evaluation!");
    }
    return dt;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  /** evaluate a row value expression from its components.
   * @return value.
   */
  private Object evaluate(Object oValuePrimary)
  {
    Object oValue = null;
    if (getValueExpressionPrimary() != null)
      oValue  = oValuePrimary;
    else if (getQueryExpression() != null)
      throw new IllegalArgumentException("Query expression not supported for evaluation!");
    else
    {
      if (isRow())
        throw new IllegalArgumentException("ROW expression not supported for evaluation!");
      if (getValueExpressions().size() > 0)
        throw new IllegalArgumentException("List of expressions not supported for evaluation!");
    }
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate a row value expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return value.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary  = getValueExpressionPrimary().evaluate(ss, bAggregated);
    Object oValue = evaluate(oValuePrimary);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a row value expression to their initial value.
   * @param ss sql statement.
   * @return final value before reset.
   */
  public Object resetAggregates(SqlStatement ss)
  {
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary  = getValueExpressionPrimary().resetAggregates(ss);
    Object oValue = evaluate(oValuePrimary);
    return oValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the row value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.RowValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the row value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().rowValueExpression());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a row value expression.
   * @param vep value expression primary.
   */
  public void initValue(ValueExpressionPrimary vep)
  {
    _il.enter(vep);
    setValueExpressionPrimary(vep);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** initialize a row value expression.
   * @param vep value expression primary.
   * @param listVe list of value expressions.
   * @param bRow true, if ROW constructor.
   * @param qe query expression.
   */
  public void initialize(
    ValueExpressionPrimary vep,
    List<ValueExpression> listVe,
    boolean bRow,
    QueryExpression qe)
  {
    _il.enter(vep, listVe, String.valueOf(bRow), qe);
    setValueExpressionPrimary(vep);
    setValueExpressions(listVe);
    setRow(bRow);
    setQueryExpression(qe);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public RowValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class RowValueExpression */
