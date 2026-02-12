package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class CreateTableStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CreateTableStatement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CtsVisitor extends EnhancedSqlBaseVisitor<CreateTableStatement>
  {
    @Override
    public CreateTableStatement visitTableScope(SqlParser.TableScopeContext ctx)
    {
      setTableScope(getTableScope(ctx));
      return CreateTableStatement.this;
    }
    @Override
    public CreateTableStatement visitSubtableClause(SqlParser.SubtableClauseContext ctx)
    {
      setTableName(ctx.tableName(),getSuperTable());
      return CreateTableStatement.this;
    }
    @Override
    public CreateTableStatement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return CreateTableStatement.this;
    }
    @Override 
    public CreateTableStatement visitUdtName(SqlParser.UdtNameContext ctx)
    {
      setUdtName(ctx, getUdtName());
      return CreateTableStatement.this;
    }
    @Override
    public CreateTableStatement visitTableElement(SqlParser.TableElementContext ctx)
    {
      TableElement te = getSqlFactory().newTableElement();
      te.parse(ctx);
      getTableElements().add(te);
      return CreateTableStatement.this;
    }
    @Override
    public CreateTableStatement visitCommitAction(SqlParser.CommitActionContext ctx)
    {
      setCommitAction(getCommitAction(ctx));
      return CreateTableStatement.this;
    }
    @Override
    public CreateTableStatement visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      addColumnName(ctx, getColumnNames());
      return CreateTableStatement.this;
    }
    @Override
    public CreateTableStatement visitQueryExpression(SqlParser.QueryExpressionContext ctx)
    {
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx);
      return CreateTableStatement.this;
    }
    @Override
    public CreateTableStatement visitWithOrWithoutData(SqlParser.WithOrWithoutDataContext ctx)
    {
      setWithOrWithoutData(getWithOrWithoutData(ctx));
      return CreateTableStatement.this;
    }
  }
  /*==================================================================*/

  private CtsVisitor _visitor = new CtsVisitor();
  private CtsVisitor getVisitor() { return _visitor; }
  
  private TableScope _ts = null;
  public TableScope getTableScope() { return _ts; }
  public void setTableScope(TableScope ts) { _ts = ts; }
  
  private QualifiedId _qTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qTableName; }
  private void setTableName(QualifiedId qTableName) { _qTableName = qTableName; }
  
  private List<TableElement> _listTe = new ArrayList<TableElement>();
  public List<TableElement> getTableElements() { return _listTe; }
  private void setTableElements(List<TableElement> listTe) { _listTe = listTe; }
  
  private QualifiedId _qUdtName = new QualifiedId();
  public QualifiedId getUdtName() { return _qUdtName; }
  private void setUdtName(QualifiedId qUdtName) { _qUdtName = qUdtName; }
  
  private QualifiedId _qSuperTable = new QualifiedId();
  public QualifiedId getSuperTable() { return _qSuperTable; }
  private void setSuperTable(QualifiedId qSuperTable) { _qSuperTable = qSuperTable; }
  
  private CommitAction _ca = null;
  public CommitAction getCommitAction() { return _ca; }
  public void setCommitAction(CommitAction ca) { _ca = ca; }
  
  private IdList _ilColumnNames = new IdList();
  public IdList getColumnNames() { return _ilColumnNames; }
  private void setColumnNames(IdList ilColumnNames) { _ilColumnNames = ilColumnNames; }
  
  private QueryExpression _qe = null;
  public QueryExpression getQueryExpression() { return _qe; }
  public void setQueryExpression(QueryExpression qe) { _qe = qe; }
  
  private WithOrWithoutData _wowd = null;
  public WithOrWithoutData getWithOrWithoutData() { return _wowd; }
  public void setWithOrWithoutData(WithOrWithoutData wowd) { _wowd = wowd; }
  
  /*------------------------------------------------------------------*/
  /** format the table elements list.
   * @return formatted list.
   */
  private String formatTableElements()
  {
    String sElements = sLEFT_PAREN;
    for (int iElement = 0; iElement < getTableElements().size(); iElement++)
    {
      if (iElement > 0)
        sElements = sElements + sCOMMA;
      sElements = sElements + sNEW_LINE + sINDENT + getTableElements().get(iElement).format();
    }
    sElements = sElements + sNEW_LINE + sRIGHT_PAREN;
    return sElements;
  } /* formatTableElements */
  
  /*------------------------------------------------------------------*/
  /** format the create table statement.
   * @return the SQL string corresponding to the fields of the create 
   *   table statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.CREATE.getKeyword();
    if (getTableScope() != null)
      sStatement = sStatement + sSP + getTableScope().getKeywords();
    sStatement = sStatement  + sSP + K.TABLE.getKeyword() + sSP + 
      getTableName().format();
    if (getUdtName().isSet())
    {
      sStatement = sStatement + sSP + K.OF.getKeyword() + sSP +
        getUdtName().format();
      if (getSuperTable().isSet())
        sStatement = sStatement + sSP + K.UNDER.getKeyword() + sSP +
          getSuperTable().format();
    }
    if (getQueryExpression() == null)
      sStatement = sStatement + formatTableElements();
    else
    {
      if (getColumnNames().isSet())
        sStatement = sStatement + sLEFT_PAREN + getColumnNames().format() + sRIGHT_PAREN;
      sStatement = sStatement + sSP + K.AS.getKeyword() + sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN;
      if (getWithOrWithoutData() != null)
        sStatement = sStatement + sSP + getWithOrWithoutData().getKeywords();
    }
    if (getCommitAction() != null)
      sStatement = sStatement + sSP + getCommitAction().getKeywords();
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the create table statement from the parsing tree context.
  * @param ctx parsing context (tree).
  * @throws NullPointerException if no parsing tree is available. 
  */
 public void parse(SqlParser.CreateTableStatementContext ctx)
 {
   setContext(ctx);
   getVisitor().visit(getContext());
 } /* parse */
 
 /*------------------------------------------------------------------*/
 /** parse the create table statement from SQL.
  * @param sSql SQL.
  */
 @Override
 public void parse(String sSql)
 {
   setParser(newSqlParser(sSql));
   parse(getParser().createTableStatement());
 } /* parse */
 
  /*------------------------------------------------------------------*/
  /** constructor for building a create table statement.
   * @param ts table scope (or null).
   * @param qTableName name of table to be created (not null!).
   * @param listElements table element list (not null!).
   * @param qUdtName name of UDT (not null!).
   * @param qSuperTable name of super table (not null!).
   * @param ca commit action (or null).
   * @param ilColumnNames list of column names for query (not null!)
   * @param qe query expression.
   * @param wowd WITH DATA or WITH NO DATA.
   */
  public void initialize(
    TableScope ts,
    QualifiedId qTableName, 
    List<TableElement> listElements,
    QualifiedId qUdtName,
    QualifiedId qSuperTable,
    CommitAction ca,
    IdList ilColumnNames,
    QueryExpression qe,
    WithOrWithoutData wowd)
  {
    _il.enter(ts, qTableName, listElements, qUdtName, qSuperTable, ca,
      ilColumnNames, qe, wowd);
    setTableScope(ts);
    setTableName(qTableName);
    setTableElements(listElements);
    setUdtName(qUdtName);
    setSuperTable(qSuperTable);
    setCommitAction(ca);
    setColumnNames(ilColumnNames);
    setQueryExpression(qe);
    setWithOrWithoutData(wowd);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** initialize a create table statement from table elements.
   * @param ts table scope (or null).
   * @param qTableName name of table to be created (not null!).
   * @param listElements table element list (not null!).
   * @param ca commit action (or null).
   */
  public void initTableElements(
    TableScope ts,
    QualifiedId qTableName, 
    List<TableElement> listElements,
    CommitAction ca)
  {
    _il.enter(ts, qTableName, listElements, ca);
    setTableScope(ts);
    setTableName(qTableName);
    setTableElements(listElements);
    setCommitAction(ca);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CreateTableStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CreateTableStatement */
