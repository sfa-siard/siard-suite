package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class SortSpecification
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SortSpecification.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class SsVisitor extends EnhancedSqlBaseVisitor<SortSpecification>
  {
    @Override
    public SortSpecification visitValueExpression(SqlParser.ValueExpressionContext ctx)
    {
      setValueExpression(getSqlFactory().newValueExpression());
      getValueExpression().parse(ctx);
      return SortSpecification.this;
    }
    @Override
    public SortSpecification visitOrderingSpecification(SqlParser.OrderingSpecificationContext ctx)
    {
      setOrderingSpecification(getOrderingSpecification(ctx));
      return SortSpecification.this;
    }
    @Override
    public SortSpecification visitNullOrdering(SqlParser.NullOrderingContext ctx)
    {
      setNullOrdering(getNullOrdering(ctx));
      return SortSpecification.this;
    }
  }
  /*==================================================================*/
  
  private SsVisitor _visitor = new SsVisitor();
  private SsVisitor getVisitor() { return _visitor; }
  
  private ValueExpression _ve = null;
  public ValueExpression getValueExpression() { return _ve; }
  public void setValueExpression(ValueExpression ve) { _ve = ve; }
  
  private OrderingSpecification _os = null;
  public OrderingSpecification getOrderingSpecification() { return _os; }
  public void setOrderingSpecification(OrderingSpecification os) { _os = os; }
  
  private NullOrdering _no = null;
  public NullOrdering getNullOrdering() { return _no; }
  public void setNullOrdering(NullOrdering no) { _no = no; }
  
  /*------------------------------------------------------------------*/
  /** format the sort specification.
   * @return the SQL string corresponding to the fields of the sort specification.
   */
  @Override
  public String format()
  {
    String sSpecification = getValueExpression().format();
    if (getOrderingSpecification() != null)
      sSpecification = sSpecification + sSP + getOrderingSpecification().getKeywords();
    if (getNullOrdering() != null)
      sSpecification = sSpecification + sSP + getNullOrdering().getKeywords();
    return sSpecification;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the sort specification from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.SortSpecificationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the sort specification from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().sortSpecification());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a sort specification
   * @param ve value expression to be sorted by.
   * @param os ordering specification (ASC/DESC).
   * @param no NULL ordering.
   */
  public void initialize(
    ValueExpression ve,
    OrderingSpecification os,
    NullOrdering no
    )
  {
    _il.enter();
    setValueExpression(ve);
    setOrderingSpecification(os);
    setNullOrdering(no);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public SortSpecification(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class SortSpecification */
