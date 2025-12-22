package ch.enterag.sqlparser.expression;

import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class QueryExpressionBody
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(QueryExpressionBody.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class QebVisitor extends EnhancedSqlBaseVisitor<QueryExpressionBody>
  {
    @Override
    public QueryExpressionBody visitQueryExpressionBody(SqlParser.QueryExpressionBodyContext ctx)
    {
      if ((ctx.queryOperator() != null) && (ctx.queryExpressionBody().size() == 2))
      {
        setQueryOperator(getQueryOperator(ctx.queryOperator()));
        setQueryExpressionBody(getSqlFactory().newQueryExpressionBody());
        getQueryExpressionBody().parse(ctx.queryExpressionBody(0));
        setSecondQueryExpressionBody(getSqlFactory().newQueryExpressionBody());
        getSecondQueryExpressionBody().parse(ctx.queryExpressionBody(1));
        if (ctx.setQuantifier() != null)
          setSetQuantifier(getSetQuantifier(ctx.setQuantifier()));
        if (ctx.correspondingSpecification() != null)
        {
          setCorresponding(true);
          for (int i = 0; i < ctx.correspondingSpecification().columnName().size(); i++)
          {
            Identifier idColumnName = new Identifier();
            setColumnName(ctx.correspondingSpecification().columnName(i),idColumnName);
            getCorrespondingColumnNames().add(idColumnName);
          }
        }
      }
      else if (ctx.tableReference() != null)
      {
        setTableReference(getSqlFactory().newTableReference());
        getTableReference().parse(ctx.tableReference());
      }
      else if (ctx.querySpecification() != null)
      {
        setQuerySpecification(getSqlFactory().newQuerySpecification());
        getQuerySpecification().parse(ctx.querySpecification());
      }
      else if (ctx.queryExpressionBody().size() == 1)
      {
        setQueryExpressionBody(getSqlFactory().newQueryExpressionBody());
        getQueryExpressionBody().parse(ctx.queryExpressionBody(0));
      }
      else if ((ctx.TABLE() != null) && (ctx.tableName() != null))
        setTableName(ctx.tableName(),getTableName());
      else if (ctx.VALUES() != null)
      {
        for (int i = 0; i < ctx.tableRowValueExpression().size(); i++)
        {
          TableRowValueExpression trve = getSqlFactory().newTableRowValueExpression();
          trve.parse(ctx.tableRowValueExpression(i));
          getTableRowValueExpressions().add(trve);
        }
      }
      return QueryExpressionBody.this;
    }
  }
  /*==================================================================*/
  
  private QebVisitor _visitor = new QebVisitor();
  private QebVisitor getVisitor() { return _visitor; }

  private QueryOperator _qo = null;
  public QueryOperator getQueryOperator() { return _qo; }
  public void setQueryOperator(QueryOperator qo) { _qo = qo; }
  
  private QueryExpressionBody _qeb = null;
  public QueryExpressionBody getQueryExpressionBody()  { return _qeb; }
  public void setQueryExpressionBody(QueryExpressionBody qeb) { _qeb = qeb; }
  
  private QueryExpressionBody _qeb2 = null;
  public QueryExpressionBody getSecondQueryExpressionBody()  { return _qeb2; }
  public void setSecondQueryExpressionBody(QueryExpressionBody qeb2) { _qeb2 = qeb2; }

  private SetQuantifier _sq = null;
  public SetQuantifier getSetQuantifier() { return _sq; }
  public void setSetQuantifier(SetQuantifier sq) { _sq = sq; }
  
  private boolean _bCorresponding = false;
  public boolean isCorresponding() { return _bCorresponding; }
  public void setCorresponding(boolean bCorresponding) { _bCorresponding = bCorresponding; }
  
  private List<Identifier> _listCorrespondingColumnNames = new ArrayList<Identifier>();
  public List<Identifier> getCorrespondingColumnNames() { return _listCorrespondingColumnNames; }
  private void setCorrespondingColumnNames(List<Identifier> listCorrespondingColumnNames) { _listCorrespondingColumnNames = listCorrespondingColumnNames; }
  
  private TableReference _tr = null;
  public TableReference getTableReference() { return _tr; }
  public void setTableReference(TableReference tr) { _tr = tr; }

  private QuerySpecification _qs = null;
  public QuerySpecification getQuerySpecification() { return _qs; }
  public void setQuerySpecification(QuerySpecification qs) { _qs = qs; }
  
  private QualifiedId _qiTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qiTableName; }
  private void setTableName(QualifiedId qiTableName) { _qiTableName = qiTableName; }
  
  private List<TableRowValueExpression> _listTrve = new ArrayList<TableRowValueExpression>();
  public List<TableRowValueExpression> getTableRowValueExpressions() { return _listTrve; }
  private void setTableRowValueExpressions(List<TableRowValueExpression> listTrve) { _listTrve = listTrve; }

  public String formatValues()
  {
    String s = K.VALUES.getKeyword() + sLEFT_PAREN;
    for (int i = 0; i < getTableRowValueExpressions().size(); i++)
    {
      if (i > 0)
        s = s + sCOMMA + sSP;
      s = s + getTableRowValueExpressions().get(i).format();
    }
    s = s + sRIGHT_PAREN;
    return s;
  }
  
  /*------------------------------------------------------------------*/
  /** format the query expression body.
   * @return the SQL string corresponding to the fields of the query expression body.
   */
  @Override
  public String format()
  {
    String s = null;
    if (getQueryOperator() != null)
    {
      s = getQueryExpressionBody().format() + sSP + getQueryOperator().getKeywords();
      if (getSetQuantifier() != null)
        s = s + sSP + getSetQuantifier().getKeywords();
      if (isCorresponding())
      {
        s = s + sSP + K.CORRESPONDING.getKeyword();
        if (getCorrespondingColumnNames().size() > 0)
        {
          s = s + sSP + K.BY.getKeyword() + sLEFT_PAREN;
          for (int i = 0; i < getCorrespondingColumnNames().size(); i++)
          {
            if (i > 0)
              s = s + sCOMMA + sSP;
            s = s + getCorrespondingColumnNames().get(i).format();
          }
          s = s + sRIGHT_PAREN;
        }
      }
      s = s + sSP + getSecondQueryExpressionBody().format();
    }
    else if (getTableReference() != null)
      s = getTableReference().format();
    else if (getQueryExpressionBody() != null)
      s = s + sLEFT_PAREN + getQueryExpressionBody().format() + sRIGHT_PAREN;
    else if (getQuerySpecification() != null)
      s = getQuerySpecification().format();
    else if (getTableName().isSet())
      s = K.TABLE.getKeyword() + sSP + getTableName().format();
    else if (getTableRowValueExpressions().size() > 0)
      s = formatValues();
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the query expression body from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.QueryExpressionBodyContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the query expression body from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().queryExpressionBody());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a query expression body.
   * @param qo query operator.
   * @param qeb query expression body operand.
   * @param qeb2 second query expression body operand.
   * @param sq set quantifier.
   * @param bCorresponding CORRESPONDING clause.
   * @param listCorrespondingColumnNames list of corresponding column names.
   * @param tr table reference.
   * @param qs query specification.
   * @param qiTableName name of table.
   * @param listTrve list of table row value expressions.
   */
  public void initialize(
      QueryOperator qo,
      QueryExpressionBody qeb,
      QueryExpressionBody qeb2,
      SetQuantifier sq,
      boolean bCorresponding,
      List<Identifier> listCorrespondingColumnNames,
      TableReference tr,
      QuerySpecification qs,
      QualifiedId qiTableName,
      List<TableRowValueExpression> listTrve
    )
  {
    _il.enter();
    setQueryOperator(qo);
    setQueryExpressionBody(qeb);
    setSecondQueryExpressionBody(qeb2);
    setSetQuantifier(sq);
    setCorresponding(bCorresponding);
    setCorrespondingColumnNames(listCorrespondingColumnNames);
    setTableReference(tr);
    setQuerySpecification(qs);
    setTableName(qiTableName);
    setTableRowValueExpressions(listTrve);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public QueryExpressionBody(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class QueryExpressionBody */
