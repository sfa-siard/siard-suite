package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class MultisetValueExpression
extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(MultisetValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class MveVisitor extends EnhancedSqlBaseVisitor<MultisetValueExpression>
  {
    @Override
    public MultisetValueExpression visitMultisetValueExpression(SqlParser.MultisetValueExpressionContext ctx)
    {
      if (ctx.valueExpressionPrimary() != null)
      {
        setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
        getValueExpressionPrimary().parse(ctx.valueExpressionPrimary());
      }
      if ((ctx.multisetOperator() != null) && (ctx.multisetValueExpression().size() > 0))
      {
        setMultisetValueExpression(getSqlFactory().newMultisetValueExpression());
        getMultisetValueExpression().parse(ctx.multisetValueExpression(0));
        setMultisetOperator(getMultisetOperator(ctx.multisetOperator()));
        if (ctx.setQuantifier() != null)
          setSetQuantifier(getSetQuantifier(ctx.setQuantifier()));
        if ((ctx.SET() != null) && (ctx.multisetValueExpression().size() > 1))
        {
          setSetArgument(getSqlFactory().newMultisetValueExpression());
          getSetArgument().parse(ctx.multisetValueExpression(1));
        }
      }
      else if ((ctx.SET() != null) && (ctx.multisetValueExpression().size() > 0))
      {
        setSetArgument(getSqlFactory().newMultisetValueExpression());
        getSetArgument().parse(ctx.multisetValueExpression(0));
      }
      return MultisetValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private MveVisitor _visitor = new MveVisitor();
  private MveVisitor getVisitor() { return _visitor; }
  
  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }

  private MultisetOperator _mo = null;
  public MultisetOperator getMultisetOperator() { return _mo; }
  public void setMultisetOperator(MultisetOperator mo) { _mo = mo; }
  
  private SetQuantifier _sq = null;
  public SetQuantifier getSetQuantifier() { return _sq; }
  private void setSetQuantifier(SetQuantifier sq) { _sq = sq; }
  
  private MultisetValueExpression _mve = null;
  public MultisetValueExpression getMultisetValueExpression() { return _mve; }
  public void setMultisetValueExpression(MultisetValueExpression mve) { _mve = mve; }
  
  private MultisetValueExpression _mveSetArgument = null;
  public MultisetValueExpression getSetArgument() { return _mveSetArgument; }
  public void setSetArgument(MultisetValueExpression mveSetArgument) { _mveSetArgument = mveSetArgument; }
  
  /*------------------------------------------------------------------*/
  /** format the multiset value expression.
   * @return the SQL string corresponding to the fields of the multiset 
   *   value expression.
   */
  @Override
  public String format()
  {
    String sExpression = "";
    if ((getMultisetValueExpression() != null) && (getMultisetOperator() != null))
    {
      sExpression = getMultisetValueExpression().format() + sSP + 
        getMultisetOperator().getKeywords() + sSP;
      if (getSetQuantifier() != null)
        sExpression = sExpression + getSetQuantifier().getKeywords() + sSP;
    }
    if (getValueExpressionPrimary() != null)
      sExpression = sExpression + getValueExpressionPrimary().format();
    else if (getSetArgument() != null)
      sExpression = sExpression + K.SET.getKeyword() + 
        sLEFT_PAREN + getSetArgument().format() + sRIGHT_PAREN;
    return sExpression;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the multiset value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.MultisetValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the multiset value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().multisetValueExpression());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a multiset value expression.
   * @param mve first operand of multiset operator.
   * @param mo multiset operator.
   * @param sq set quantifier for second operator.
   * @param vep second operator or primary value.
   * @param mveSetArgument argument for SET function as second operator 
   *   or primary value. 
   */
  public void initialize(
    MultisetValueExpression mve,
    MultisetOperator mo,
    SetQuantifier sq,
    ValueExpressionPrimary vep,
    MultisetValueExpression mveSetArgument)
  {
    _il.enter(mve, mo, sq, vep, mveSetArgument);
    setMultisetValueExpression(mve);
    setMultisetOperator(mo);
    setSetQuantifier(sq);
    setValueExpressionPrimary(vep);
    setSetArgument(mveSetArgument);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MultisetValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class MultisetValueExpression */
