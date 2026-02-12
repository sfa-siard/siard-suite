package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class BooleanValueExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(BooleanValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class BveVisitor extends EnhancedSqlBaseVisitor<BooleanValueExpression>
  {
    @Override
    public BooleanValueExpression visitBooleanValueExpression(SqlParser.BooleanValueExpressionContext ctx)
    {
      if (ctx.NOT() != null)
        setNot(true);
      if (ctx.booleanValueExpression().size() > 0)
      {
        setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
        getBooleanValueExpression().parse(ctx.booleanValueExpression(0));
      }
      if (ctx.booleanOperator() != null)
        setBooleanOperator(getBooleanOperator(ctx.booleanOperator()));
      if (ctx.booleanValueExpression().size() > 1)
      {
        setSecondBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
        getSecondBooleanValueExpression().parse(ctx.booleanValueExpression(1));
      }
      if (ctx.booleanPrimary() != null)
      {
        setBooleanPrimary(getSqlFactory().newBooleanPrimary());
        getBooleanPrimary().parse(ctx.booleanPrimary());
      }
      if (ctx.BOOLEAN_LITERAL() != null)
        setBooleanLiteral(getBooleanLiteral(ctx.BOOLEAN_LITERAL()));
      return BooleanValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private BveVisitor _visitor = new BveVisitor();
  private BveVisitor getVisitor() { return _visitor; }
  
  private boolean _bNot = false;
  public boolean isNot() { return _bNot; }
  public void setNot(boolean bNot) { _bNot = bNot; }

  private BooleanOperator _bo = null;
  public BooleanOperator getBooleanOperator() { return _bo; }
  public void setBooleanOperator(BooleanOperator bo) { _bo = bo; }
  
  private BooleanValueExpression _bve = null;
  public BooleanValueExpression getBooleanValueExpression() { return _bve; }
  public void setBooleanValueExpression(BooleanValueExpression bve) { _bve = bve; }
  
  private BooleanValueExpression _bve2 = null;
  public BooleanValueExpression getSecondBooleanValueExpression() { return _bve2; }
  public void setSecondBooleanValueExpression(BooleanValueExpression bve2) { _bve2 = bve2; }
  
  private BooleanPrimary _bp = null;
  public BooleanPrimary getBooleanPrimary() { return _bp; }
  public void setBooleanPrimary(BooleanPrimary bp) { _bp = bp; }
  
  private BooleanLiteral _bl = null;
  public BooleanLiteral getBooleanLiteral() { return _bl; }
  public void setBooleanLiteral(BooleanLiteral bl) { _bl = bl; }
  
  /*------------------------------------------------------------------*/
  /** format the boolean value expression.
   * @return the SQL string corresponding to the fields of the boolean 
   *   value expression.
   */
  @Override
  public String format()
  {
    String sExpression = "";
    if (isNot())
      sExpression = K.NOT.getKeyword() + sSP;
    if (getBooleanValueExpression() != null)
    {
      sExpression = sExpression + getBooleanValueExpression().format();
      if (getBooleanOperator() != null)
        sExpression = sExpression + sSP + getBooleanOperator().getKeywords();
      if (getSecondBooleanValueExpression() != null)
        sExpression = sExpression + sSP + getSecondBooleanValueExpression().format();
    }
    else if (getBooleanPrimary() != null)
    {
      sExpression = getBooleanPrimary().format();
      if  (getBooleanLiteral() != null)
      {
        sExpression = sExpression + sSP + K.IS.getKeyword();
        if (isNot())
          sExpression = sExpression + sSP + K.NOT.getKeyword();
        sExpression = sExpression + sSP + getBooleanLiteral().getKeywords();
      }
    }
    return sExpression;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** return the data type of a boolean value expression from the context of a query.
   * @param ss sql statement.
   * @return boolean data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = getSqlFactory().newDataType();
    PredefinedType pt = getSqlFactory().newPredefinedType();
    dt.initPredefinedDataType(pt);
    pt.initBooleanType();
    return dt;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  /** evaluate a boolean value expression from its components.
   * @return Boolean.TRUE, Boolean.FALSE oder null for the value.
   */
  private Boolean evaluate(
    Boolean bBooleanPrimary,
    Boolean bBooleanValue,
    Boolean bSecondBooleanValue)
  {
    Boolean bValue = null;
    if (getBooleanPrimary() != null)
    {
      bValue = bBooleanPrimary;
      BooleanLiteral bl = getBooleanLiteral();
      if (bl != null)
      {
        if (((bValue == null) && (bl == BooleanLiteral.UNKNOWN)) ||
            ((Boolean.FALSE.equals(bValue)) && (bl == BooleanLiteral.FALSE)) ||
            ((Boolean.TRUE.equals(bValue)) && (bl == BooleanLiteral.TRUE)))
          bValue = Boolean.TRUE;
        else
          bValue = Boolean.FALSE;
        if (isNot())
          bValue = Boolean.valueOf(!bValue.booleanValue());
      }
    }
    else if (getBooleanOperator() != null)
    {
      BooleanOperator bo = getBooleanOperator();
      bValue = bBooleanValue;
      Boolean bValue2 = bSecondBooleanValue;
      switch(bo) // 3-valued logic!
      {
        case AND:
          if (Boolean.TRUE.equals(bValue) && Boolean.TRUE.equals(bValue2))
            bValue = Boolean.TRUE;
          else if (Boolean.FALSE.equals(bValue) || Boolean.FALSE.equals(bValue2))
            bValue = Boolean.FALSE;
          else
            bValue = null;
          break;
        case OR:
          if (Boolean.FALSE.equals(bValue) && Boolean.FALSE.equals(bValue2))
            bValue = Boolean.FALSE;
          else if (Boolean.TRUE.equals(bValue) || Boolean.TRUE.equals(bValue2))
            bValue = Boolean.TRUE;
          else
            bValue = null;
          break;
      }
    }
    return bValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate the boolean value expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return Boolean.TRUE, Boolean.FALSE oder null for the value.
   */
  public Boolean evaluate(SqlStatement ss, boolean bAggregated)
  {
    Boolean bBooleanPrimary = null;
    if (getBooleanPrimary() != null)
      bBooleanPrimary = getBooleanPrimary().evaluate(ss, bAggregated);
    Boolean bBooleanValue = null;
    if (getBooleanValueExpression() != null)
      bBooleanValue = getBooleanValueExpression().evaluate(ss,bAggregated);
    Boolean bSecondBooleanValue = null;
    if (getSecondBooleanValueExpression() != null)
      bSecondBooleanValue = getSecondBooleanValueExpression().evaluate(ss, bAggregated);
    Boolean bValue = evaluate(bBooleanPrimary,bBooleanValue,bSecondBooleanValue);
    return bValue;
  } /* evaluate */

  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a boolean value expression to 
   * their initial value.
   * @param ss sql statement.
   * @return final value before reset.
   */
  public Boolean resetAggregates(SqlStatement ss)
  {
    Boolean bBooleanPrimary = null;
    if (getBooleanPrimary() != null)
      bBooleanPrimary = getBooleanPrimary().resetAggregates(ss);
    Boolean bBooleanValue = null;
    if (getBooleanValueExpression() != null)
      bBooleanValue = getBooleanValueExpression().resetAggregates(ss);
    Boolean bSecondBooleanValue = null;
    if (getSecondBooleanValueExpression() != null)
      bSecondBooleanValue = getSecondBooleanValueExpression().resetAggregates(ss);
    Boolean bValue = evaluate(bBooleanPrimary,bBooleanValue,bSecondBooleanValue);
    return bValue;
  } /* resetAggregates */

  /*------------------------------------------------------------------*/
  /** parse the boolean value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.BooleanValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the boolean value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().booleanValueExpression());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a boolean value expression.
   * @param bNot NOT.
   * @param bo boolean operator.
   * @param bve boolean value expression.
   * @param bve2 second boolean value expression.
   * @param bp boolean primary.
   * @param bl boolean literal.
   */
  public void initialize(
    boolean bNot,
    BooleanOperator bo,
    BooleanValueExpression bve,
    BooleanValueExpression bve2,
    BooleanPrimary bp,
    BooleanLiteral bl)
  {
    _il.enter(bNot, bo, bve, bve2, bp, bl);
    setNot(bNot);
    setBooleanOperator(bo);
    setBooleanValueExpression(bve);
    setSecondBooleanValueExpression(bve2);
    setBooleanPrimary(bp);
    setBooleanLiteral(bl);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public BooleanValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class BooleanValueExpression */
