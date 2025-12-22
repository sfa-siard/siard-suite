package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class ArrayValueExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(ArrayValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class AveVisitor extends EnhancedSqlBaseVisitor<ArrayValueExpression>
  {
    @Override
    public ArrayValueExpression visitArrayValueExpression(SqlParser.ArrayValueExpressionContext ctx)
    {
      if ((ctx.CONCATENATION_OPERATOR() != null) && (ctx.arrayValueExpression().size() == 2))
      {
        setConcatenation(true);
        setFirstOperand(getSqlFactory().newArrayValueExpression());
        getFirstOperand().parse(ctx.arrayValueExpression(0));
        setSecondOperand(getSqlFactory().newArrayValueExpression());
        getSecondOperand().parse(ctx.arrayValueExpression(1));
      }
      else if (ctx.valueExpressionPrimary() != null)
      {
        setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
        getValueExpressionPrimary().parse(ctx.valueExpressionPrimary());
      }
      return ArrayValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private AveVisitor _visitor = new AveVisitor();
  private AveVisitor getVisitor() { return _visitor; }

  private boolean _bConcatenation = false;
  public boolean isConcatenation() { return _bConcatenation; }
  public void setConcatenation(boolean bConcatenation) { _bConcatenation = bConcatenation; }
  
  private ArrayValueExpression _ave1 = null;
  public ArrayValueExpression getFirstOperand() { return _ave1; }
  public void setFirstOperand(ArrayValueExpression ave1) { _ave1 = ave1; }

  private ArrayValueExpression _ave2 = null;
  public ArrayValueExpression getSecondOperand() { return _ave2; }
  public void setSecondOperand(ArrayValueExpression ave2) { _ave2 = ave2; }
  
  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }
  
  /*------------------------------------------------------------------*/
  /** format the array value expression.
   * @return the SQL string corresponding to the fields of the array 
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
    return sExpression;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the array value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.ArrayValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the array value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().arrayValueExpression());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize an array value expression.
   * @param bConcatenation true, if concatentation expression.
   * @param ave1 first operand (or null).
   * @param ave2 second operand (or null).
   * @param vep value expression primary.
   */
  public void initialize(
    boolean bConcatenation,
    ArrayValueExpression ave1,
    ArrayValueExpression ave2,
    ValueExpressionPrimary vep)
  {
    _il.enter(String.valueOf(bConcatenation), ave1, ave2, vep);
    setConcatenation(bConcatenation);
    setFirstOperand(ave1);
    setSecondOperand(ave2);
    setValueExpressionPrimary(vep);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public ArrayValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class ArrayValueExpression */
