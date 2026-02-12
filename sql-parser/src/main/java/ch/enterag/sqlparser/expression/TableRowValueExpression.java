package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class TableRowValueExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(TableRowValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class TrveVisitor extends EnhancedSqlBaseVisitor<TableRowValueExpression>
  {
    @Override
    public TableRowValueExpression visitCommonValueExpression(SqlParser.CommonValueExpressionContext ctx)
    {
      setCommonValueExpression(getSqlFactory().newCommonValueExpression());
      getCommonValueExpression().parse(ctx);
      return TableRowValueExpression.this;
    }
    @Override
    public TableRowValueExpression visitBooleanValueExpression(SqlParser.BooleanValueExpressionContext ctx)
    {
      setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
      getBooleanValueExpression().parse(ctx);
      return TableRowValueExpression.this;
    }
    @Override
    public TableRowValueExpression visitRowValueExpression(SqlParser.RowValueExpressionContext ctx)
    {
      setRowValueExpression(getSqlFactory().newRowValueExpression());
      getRowValueExpression().parse(ctx);
      return TableRowValueExpression.this;
    }
    @Override
    public TableRowValueExpression visitValueExpressionPrimary(SqlParser.ValueExpressionPrimaryContext ctx)
    {
      setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
      getValueExpressionPrimary().parse(ctx);
      return TableRowValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private TrveVisitor _visitor = new TrveVisitor();
  private TrveVisitor getVisitor() { return _visitor; }
  
  private CommonValueExpression _cve = null;
  public CommonValueExpression getCommonValueExpression() { return _cve; }
  public void setCommonValueExpression(CommonValueExpression cve) { _cve = cve; }
  
  private BooleanValueExpression _bve = null;
  public BooleanValueExpression getBooleanValueExpression() { return _bve; }
  public void setBooleanValueExpression(BooleanValueExpression bve) { _bve = bve; }
  
  private RowValueExpression _rve = null;
  public RowValueExpression getRowValueExpression() { return _rve; }
  public void setRowValueExpression(RowValueExpression rve) { _rve = rve; }
  
  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }
  
  /*------------------------------------------------------------------*/
  /** format the table row value expression.
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
    else if (getValueExpressionPrimary() != null)
      sExpression = getValueExpressionPrimary().format();
    return sExpression;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the table row value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.TableRowValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the table row value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().tableRowValueExpression());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a table row value expression.
   * @param cve common value expression.
   * @param bve boolean value expression.
   * @param rve row value expression.
   * @param vep value expression primary.
   */
  public void initialize(
    CommonValueExpression cve,
    BooleanValueExpression bve,
    RowValueExpression rve,
    ValueExpressionPrimary vep)
  {
    _il.enter(cve, bve, rve);
    setCommonValueExpression(cve);
    setBooleanValueExpression(bve);
    setRowValueExpression(rve);
    setValueExpressionPrimary(vep);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public TableRowValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class TableRowValueExpression */
