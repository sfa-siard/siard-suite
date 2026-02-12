package ch.enterag.sqlparser.expression;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class QuerySpecification
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(QuerySpecification.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class QsVisitor extends EnhancedSqlBaseVisitor<QuerySpecification>
  {
    @Override
    public QuerySpecification visitQuerySpecification(SqlParser.QuerySpecificationContext ctx)
    {
      if (ctx.setQuantifier() != null)
        setSetQuantifier(getSetQuantifier(ctx.setQuantifier()));
      return visitChildren(ctx);
    }
    @Override
    public QuerySpecification visitSelectList(SqlParser.SelectListContext ctx)
    {
      if (ctx.ASTERISK() != null)
        setAsterisk(true);
      else
      {
        getSqlFactory().setCount(false);
        getSqlFactory().setAggregates(false);
        for (int i = 0; i < ctx.selectSublist().size(); i++)
        {
          SelectSublist ss = getSqlFactory().newSelectSublist();
          ss.parse(ctx.selectSublist(i));
          getSelectSublists().add(ss);
        }
        if (getSqlFactory().hasCount())
          setCount(true);
        if (getSqlFactory().hasAggregates())
          setAggregates(true);
      }
      return QuerySpecification.this;
    }
    @Override
    public QuerySpecification visitFromClause(SqlParser.FromClauseContext ctx)
    {
      for (int i = 0; i < ctx.tableReference().size(); i++)
      {
        TableReference tr = getSqlFactory().newTableReference();
        tr.parse(ctx.tableReference(i));
        getTableReferences().add(tr);
      }
      return QuerySpecification.this;
    }
    @Override
    public QuerySpecification visitWhereClause(SqlParser.WhereClauseContext ctx)
    {
      getSqlFactory().setAggregates(false);
      setWhereCondition(getSqlFactory().newBooleanValueExpression());
      getWhereCondition().parse(ctx.booleanValueExpression());
      /**
      if (getSqlFactory().hasAggregates())
        throwParseCancellationException("Aggregate function in WHERE clause!");
      **/
      return QuerySpecification.this;
    }
    @Override
    public QuerySpecification visitGroupByClause(SqlParser.GroupByClauseContext ctx)
    {
      if (ctx.setQuantifier() != null)
        setSetQuantifierGroupBy(getSetQuantifier(ctx.setQuantifier()));
      for (int i = 0; i < ctx.groupingElement().size(); i++)
      {
        GroupingElement ge = getSqlFactory().newGroupingElement();
        ge.parse(ctx.groupingElement(i));
        getGroupingElements().add(ge);
      }
      return QuerySpecification.this;
    }
    @Override
    public QuerySpecification visitHavingClause(SqlParser.HavingClauseContext ctx)
    {
      getSqlFactory().setAggregates(false);
      setHavingCondition(getSqlFactory().newBooleanValueExpression());
      getHavingCondition().parse(ctx.booleanValueExpression());
      /**
      if (getSqlFactory().hasAggregates())
        throwParseCancellationException("Aggregate function in HAVING clause!");
      **/
      return QuerySpecification.this;
    }
    @Override
    public QuerySpecification visitWindowClause(SqlParser.WindowClauseContext ctx)
    {
      for (int i = 0; i < ctx.windowDefinition().size(); i++)
      {
        Identifier idWindowName = new Identifier();
        setIdentifier(ctx.windowDefinition(i).windowName().IDENTIFIER(),idWindowName);
        WindowSpecification ws = getSqlFactory().newWindowSpecification();
        ws.parse(ctx.windowDefinition(i).windowSpecification());
        getWindowSpecifications().add(ws);
      }
      return QuerySpecification.this;
    }
  }
  /*==================================================================*/
  
  private QsVisitor _visitor = new QsVisitor();
  private QsVisitor getVisitor() { return _visitor; }
  
  private SetQuantifier _sq = null;
  public SetQuantifier getSetQuantifier() { return _sq; }
  public void setSetQuantifier(SetQuantifier sq) { _sq = sq; }
  
  private boolean _bAsterisk = false;
  public boolean isAsterisk() { return _bAsterisk; }
  public void setAsterisk(boolean bAsterisk) { _bAsterisk = bAsterisk; }
  
  private List<SelectSublist> _listSelectSublists = new ArrayList<SelectSublist>();
  public  List<SelectSublist> getSelectSublists() { return _listSelectSublists; }
  private void setSelectSublists(List<SelectSublist> listSelectSublists) { _listSelectSublists = listSelectSublists; }
  public void addSelectSublist(SelectSublist ss) { _listSelectSublists.add(ss); }

  private List<TableReference> _listTableReferences = new ArrayList<TableReference>();
  public  List<TableReference> getTableReferences() { return _listTableReferences; }
  private void setTableReferences( List<TableReference> listTableReferences) { _listTableReferences = listTableReferences; }
  
  private BooleanValueExpression _bveWhere = null;
  public BooleanValueExpression getWhereCondition() { return _bveWhere; }
  public void setWhereCondition(BooleanValueExpression bveWhere) { _bveWhere = bveWhere; }

  private SetQuantifier _sqGroupBy = null;
  public SetQuantifier getSetQuantifierGroupBy() { return _sqGroupBy; }
  public void setSetQuantifierGroupBy(SetQuantifier sqGroupBy) { _sqGroupBy = sqGroupBy; }
  
  private List<GroupingElement> _listGroupingElements = new ArrayList<GroupingElement>();
  public  List<GroupingElement> getGroupingElements() { return _listGroupingElements; }
  private void setGroupingElements(List<GroupingElement> listGroupingElements) { _listGroupingElements = listGroupingElements; } 
  
  private BooleanValueExpression _bveHaving = null;
  public BooleanValueExpression getHavingCondition() { return _bveHaving; }
  public void setHavingCondition(BooleanValueExpression bveHaving) { _bveHaving = bveHaving; }
  
  private List<Identifier> _listWindowNames = new ArrayList<Identifier>();
  public  List<Identifier> getWindowNames() { return _listWindowNames; }
  private void setWindowNames(List<Identifier> listWindowNames) { _listWindowNames = listWindowNames; }
  
  private List<WindowSpecification> _listWindowSpecifications = new ArrayList<WindowSpecification>();
  public List<WindowSpecification> getWindowSpecifications() { return _listWindowSpecifications; }
  private void setWindowSpecifications(List<WindowSpecification> listWindowSpecifications) { _listWindowSpecifications = listWindowSpecifications; } 

  private boolean _bCount = false;
  /** hasCount returns true, if a COUNT(*) aggregate function was detected on parsing a SelectSublist.
   * @return true, if a select sublist contains an COUNT(*) aggregate function.
   */
  public boolean hasCount() { return _bCount; }
  public void setCount(boolean bCount) { _bCount = bCount; }
  
  private boolean _bAggregates = false;
  /** hasAggregates returns true, if an aggregate function was detected on parsing a SelectSublist.
   * @return true, if a select sublist contains an aggregate function.
   */
  public boolean hasAggregates() { return _bAggregates; }
  public void setAggregates(boolean bAggregates) { _bAggregates = bAggregates; }
  
  public boolean isCount()
  {
    boolean bCount = hasCount() && (getSelectSublists().size() == 1) && (getWhereCondition() == null);
    return bCount;
  }
  /*------------------------------------------------------------------*/
  /** determine if query is grouped.
   * @return true, if query is grouped explicitly or implicitly.
   */
  public boolean isGrouped()
  {
    boolean bGrouped = hasAggregates() || (getGroupingElements().size() > 0);
    return bGrouped;
  } /* isGrouped */
  
  /*------------------------------------------------------------------*/
  /** true, if either the query has no grouping or the column
   * is in the grouping set. 
   * @param idcColumn column to be tested.
   * @return true, if column may be evaluated outside an aggregate function's
   * argument.
   */
  public boolean isGroupedOk(IdChain idcColumn)
  {
    boolean bGroupedOk = true;
    if ((getGroupingElements().size() > 0) || hasAggregates())
    {
      for (int i = 0; bGroupedOk && (i < getGroupingElements().size()); i++)
      {
        GroupingElement ge = getGroupingElements().get(i);
        if (!ge.getOrdinaryGroupingSets().contains(idcColumn))
          bGroupedOk = false;
      }
    }
    return bGroupedOk;
  } /* isGroupedOk */

  /*------------------------------------------------------------------*/
  protected boolean equalTables(QualifiedId qiTable1, QualifiedId qiTable2)
  {
    return qiTable1.equals(qiTable2);
  } /* equalTables */
  
  /*------------------------------------------------------------------*/
  private TablePrimary getTablePrimary(SqlStatement sqlstmt, QualifiedId qiTable, TablePrimary tp, String sColumnName)
  {
    TablePrimary tpFound = null;
    if ((tp.getTableName().getName() != null))
    {
      QualifiedId qiName = new QualifiedId(
        tp.getTableName().getCatalog() != null? tp.getTableName().getCatalog(): sqlstmt.getDefaultCatalog(),
        tp.getTableName().getSchema() != null? tp.getTableName().getSchema(): sqlstmt.getDefaultSchema(),
        tp.getTableName().getName());
      QualifiedId qiCorrelation = new QualifiedId(
        tp.getTableName().getCatalog() != null? tp.getTableName().getCatalog(): sqlstmt.getDefaultCatalog(),
        tp.getTableName().getSchema() != null? tp.getTableName().getSchema(): sqlstmt.getDefaultSchema(),
        tp.getCorrelationName().get());
      if ((qiTable.getName() == null) || equalTables(qiTable,qiName) || equalTables(qiTable,qiCorrelation))
      {
        if (tp.hasColumn(sColumnName))
          tpFound = tp;
      }
    }
    if ((tpFound == null) && (tp.getTableReference() != null))
      tpFound = getTablePrimary(sqlstmt, qiTable, tp.getTableReference(),sColumnName);
    return tpFound;
  } /* getTablePrimary */
  
  /*------------------------------------------------------------------*/
  private TablePrimary getTablePrimary(SqlStatement sqlstmt, QualifiedId qiTable, TableReference tr,String sColumnName)
  {
    TablePrimary tp = null;
    if (tr.getTablePrimary() != null)
      tp = getTablePrimary(sqlstmt, qiTable,tr.getTablePrimary(),sColumnName);
    if ((tp == null) && (tr.getTableReference() != null))
      tp = getTablePrimary(sqlstmt, qiTable, tr.getTableReference(),sColumnName);
    if ((tp == null) && (tr.getSecondTableReference() != null))
      tp = getTablePrimary(sqlstmt, qiTable,tr.getSecondTableReference(),sColumnName);
    return tp;
  } /* getTablePrimary */
  
  /*------------------------------------------------------------------*/
  /** look up the registered data type of a query column.
   * @param sqlstmt SQL statement.
   * @param idcColumn column.
   * @return data type of the query column.
   */
  public TablePrimary getTablePrimary(SqlStatement sqlstmt, IdChain idcColumn)
  {
    TablePrimary tpFound = null;
    List<String> list = idcColumn.get();
    int iSize = list.size();
    if ((iSize > 0) && (iSize <= 4))
    {
      String sColumnName = list.get(iSize-1);
      QualifiedId qiTable = new QualifiedId();
      if (iSize > 1)
        qiTable.setName(list.get(iSize-2));
      if (iSize > 2)
        qiTable.setSchema(list.get(iSize-3));
      else
        qiTable.setSchema(sqlstmt.getDefaultSchema());
      if (iSize > 3)
        qiTable.setCatalog(list.get(iSize-4));
      else
        qiTable.setCatalog(sqlstmt.getDefaultCatalog());
      for (int iTable = 0; (tpFound == null) && (iTable < getTableReferences().size()); iTable++)
      {
        TablePrimary tp = getTablePrimary(sqlstmt, qiTable, getTableReferences().get(iTable),sColumnName);
        tpFound = tp;
      }
    }
    else
      throw new IllegalArgumentException("Identifier chain is invalid for column!");
    return tpFound;
  } /* getTablePrimary */
  
  /*------------------------------------------------------------------*/
  /** format the query specification.
   * @return the SQL string corresponding to the fields of the query specification.
   */
  @Override
  public String format()
  {
    /* SELECT */
    String sSpecification = K.SELECT.getKeyword();
    if (getSetQuantifier() != null)
      sSpecification = sSpecification + sSP + getSetQuantifier().getKeywords();
    if (isAsterisk())
      sSpecification = sSpecification + sSP + sASTERISK;
    else
    {
      for (int i = 0; i < getSelectSublists().size(); i++)
      {
        if (i > 0)
          sSpecification = sSpecification + sCOMMA;
        sSpecification = sSpecification +  sNEW_LINE + sINDENT + getSelectSublists().get(i).format();
      }
    }
    /* FROM */
    sSpecification = sSpecification + sNEW_LINE + K.FROM.getKeyword();
    for (int i = 0; i < getTableReferences().size(); i++)
    {
      if (i > 0)
        sSpecification = sSpecification + sCOMMA;
      sSpecification = sSpecification + sSP + getTableReferences().get(i).format();
    }
    /* WHERE */
    if (getWhereCondition() != null)
      sSpecification = sSpecification + sNEW_LINE + K.WHERE.getKeyword() + sSP + getWhereCondition().format();
    /* GROUP BY */
    if (getGroupingElements().size() > 0)
    {
      sSpecification = sSpecification + sNEW_LINE + K.GROUP.getKeyword() + sSP + K.BY.getKeyword();
      if (getSetQuantifierGroupBy() != null)
        sSpecification = sSpecification + sSP + getSetQuantifierGroupBy().getKeywords();
      for (int i = 0; i < getGroupingElements().size(); i++)
      {
        if (i > 0)
          sSpecification = sSpecification + sCOMMA;
        sSpecification = sSpecification + sSP + getGroupingElements().get(i).format();
      }
    }
    /* HAVING */
    if (getHavingCondition() != null)
      sSpecification = sSpecification + sNEW_LINE + K.HAVING.getKeyword() + sSP + getHavingCondition().format();
    /* WINDOW */
    if (getWindowNames().size() > 0)
    {
      sSpecification = sSpecification + sNEW_LINE + K.WINDOW.getKeyword();
      for (int i = 0; i < getWindowNames().size(); i++)
      {
        if (i > 0)
          sSpecification = sSpecification + sCOMMA;
        sSpecification = sSpecification + sSP + getWindowNames().get(i).format() + sSP + 
          K.AS.getKeyword() + sSP + getWindowSpecifications().get(i).format();
      }
    }
    return sSpecification;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the query specification from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.QuerySpecificationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** evaluate the WHERE condition.
   * @param ss SqlStatement to which this query specification belongs.
   * @return true, if WHERE condition is true for current values
   *   in primary tables.
   */
  public boolean evaluateWhere(SqlStatement ss)
  {
    boolean bWhere = true;
    if (getWhereCondition() != null)
      bWhere = getWhereCondition().evaluate(ss, true).booleanValue();
    return bWhere;
  } /* evaluateWhere */
  
  /*------------------------------------------------------------------*/
  /** evaluate the HAVING condition.
   * @param ss SqlStatement to which this query specification belongs.
   * @return true, if HAVING condition is true for current values in
   *   primary tables.
   */
  public boolean evaluateHaving(SqlStatement ss)
  {
    boolean bHaving = true;
    if (getHavingCondition() != null)
      bHaving = getHavingCondition().evaluate(ss, false).booleanValue();
    return bHaving;
  } /* evaluateHaving */
  
  /*------------------------------------------------------------------*/
  /** parse the query specification from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    SqlParser.QuerySpecificationContext ctx = null; 
    try { ctx = getParser().querySpecification(); }
    catch(Exception e)
    {
      setParser(newSqlParser2(sSql));
      ctx = getParser().querySpecification();
    }
    parse(ctx);
  } /* parse */

  /*------------------------------------------------------------------*/
  /** constructor for building a query specification.
   * @param sq set quantifier.
   * @param bAsterisk true, if * as select.
   * @param listSelectSublists list of SELECT expressions.
   * @param listTableReferences list of FROM table references.
   * @param bveWhere boolean value expression for WHERE.
   * @param bAggregates true, if aggregate expressions are contained.
   * @param sqGroupBy set quantifier for GROUP BY.
   * @param listGroupingElements elements of GROUP BY expression.
   * @param bveHaving boolean value expression for HAVING.
   * @param listWindowNames list of names of WINDOW specifications.
   * @param listWindowSpecifications list of WINDOW specifications.
   */
  public void initialize(
      SetQuantifier sq,
      boolean bAsterisk,
      List<SelectSublist> listSelectSublists,
      List<TableReference> listTableReferences,
      BooleanValueExpression bveWhere,
      boolean bAggregates,
      SetQuantifier sqGroupBy,
      List<GroupingElement> listGroupingElements,
      BooleanValueExpression bveHaving,
      List<Identifier> listWindowNames,
      List<WindowSpecification> listWindowSpecifications
    )
  {
    _il.enter();
    setSetQuantifier(sq);
    setAsterisk(bAsterisk);
    setSelectSublists(listSelectSublists);
    setTableReferences(listTableReferences);
    setWhereCondition(bveWhere);
    setAggregates(bAggregates);
    setSetQuantifierGroupBy(sqGroupBy);
    setGroupingElements(listGroupingElements); 
    setHavingCondition(bveHaving);
    setWindowNames(listWindowNames);
    setWindowSpecifications(listWindowSpecifications); 
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor for building a simple query specification.
   * @param bAsterisk true, for "*" instead of SELECT sublists.
   * @param listSelectSublists SELECT sublists.
   * @param listTableReferences list of table references.
   * @param bveWhere boolean value expression for WHERE.
   */
  public void initialize(
      boolean bAsterisk,
      List<SelectSublist> listSelectSublists,
      List<TableReference> listTableReferences,
      BooleanValueExpression bveWhere
    )
  {
    _il.enter();
    setAsterisk(bAsterisk);
    setSelectSublists(listSelectSublists);
    setTableReferences(listTableReferences);
    setWhereCondition(bveWhere);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public QuerySpecification(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class QuerySpecification */
