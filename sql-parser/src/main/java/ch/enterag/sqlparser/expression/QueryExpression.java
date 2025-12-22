package ch.enterag.sqlparser.expression;

import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class QueryExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(QueryExpression.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class QeVisitor extends EnhancedSqlBaseVisitor<QueryExpression>
  {
    @Override
    public QueryExpression visitWithClause(SqlParser.WithClauseContext ctx)
    {
      if (ctx.RECURSIVE() != null)
        setRecursive(true);
      for (int i = 0; i < ctx.withElement().size(); i++)
      {
        WithElement we = getSqlFactory().newWithElement();
        we.parse(ctx.withElement(i));
        getWithElements().add(we);
      }
      return QueryExpression.this;
    }
    @Override
    public QueryExpression visitQueryExpressionBody(SqlParser.QueryExpressionBodyContext ctx)
    {
      setQueryExpressionBody(getSqlFactory().newQueryExpressionBody());
      getQueryExpressionBody().parse(ctx);
      return QueryExpression.this;
    }
  }
  /*==================================================================*/
  
  private QeVisitor _visitor = new QeVisitor();
  private QeVisitor getVisitor() { return _visitor; }
  
  /* withClause */
  private boolean _bRecursive = false;
  public boolean isRecursive() { return _bRecursive; }
  public void setRecursive(boolean bRecursive) { _bRecursive = bRecursive; }
  
  private List<WithElement> _listWithElements = new ArrayList<WithElement>();
  public List<WithElement> getWithElements() { return _listWithElements; }
  private void setWithElements(List<WithElement> listWithElements) { _listWithElements = listWithElements; }
  
  /* queryExpressionBody */
  private QueryExpressionBody _qeb = null;
  public QueryExpressionBody getQueryExpressionBody() { return _qeb; }
  public void setQueryExpressionBody(QueryExpressionBody qeb) { _qeb = qeb; }
      
  /*------------------------------------------------------------------*/
  /** format the query expression.
   * @return the SQL string corresponding to the fields of the query expression.
   */
  @Override
  public String format()
  {
    String sExpression = "";
    if (getWithElements().size() > 0)
    {
      sExpression = K.WITH.getKeyword() + sSP;
      if (isRecursive())
        sExpression = sExpression + K.RECURSIVE.getKeyword() + sSP;
      for (int i = 0; i < getWithElements().size(); i++)
      {
        if (i > 0)
          sExpression = sExpression + sCOMMA + sSP;
        sExpression = sExpression + getWithElements().get(i).format();
      }
      sExpression = sExpression + sSP;
    }
    sExpression = sExpression + getQueryExpressionBody().format();
    return sExpression;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the query expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.QueryExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the query expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().queryExpression());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a query expression.
   * @param bRecursive true for RECURSIVE.
   * @param listWithElements elements of WITH clause.
   * @param qeb query expression body.
   */
  public void initialize(
    boolean bRecursive,
    List<WithElement> listWithElements,
    QueryExpressionBody qeb)
  {
    _il.enter(String.valueOf(bRecursive), listWithElements, qeb);
    setRecursive(bRecursive);
    setWithElements(listWithElements);
    setQueryExpressionBody(qeb);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public QueryExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class QueryExpression */
