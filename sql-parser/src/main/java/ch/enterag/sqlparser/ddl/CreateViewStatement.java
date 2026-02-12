package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class CreateViewStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CreateViewStatement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CvsVisitor extends EnhancedSqlBaseVisitor<CreateViewStatement>
  {
    @Override
    public CreateViewStatement visitCreateViewStatement(SqlParser.CreateViewStatementContext ctx)
    {
      if (ctx.RECURSIVE() != null)
        setRecursive(true);
      if (ctx.CHECK() != null)
        setCheckOption(true);
      return visitChildren(ctx);
    }
    @Override
    public CreateViewStatement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getViewName());
      return CreateViewStatement.this;
    }
    @Override
    public CreateViewStatement visitQueryExpression(SqlParser.QueryExpressionContext ctx)
    {
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx);
      return CreateViewStatement.this;
    }
    @Override
    public CreateViewStatement visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      addColumnName(ctx, getColumnNames());
      return CreateViewStatement.this;
    }
    @Override 
    public CreateViewStatement visitUdtName(SqlParser.UdtNameContext ctx)
    {
      setUdtName(ctx, getUdtName());
      return CreateViewStatement.this;
    }
    @Override
    public CreateViewStatement visitSubviewClause(SqlParser.SubviewClauseContext ctx)
    {
      setTableName(ctx.tableName(),getSuperTable());
      return CreateViewStatement.this;
    }
    @Override
    public CreateViewStatement visitViewElement(SqlParser.ViewElementContext ctx)
    {
      ViewElement ve = getSqlFactory().newViewElement();
      ve.parse(ctx);
      getViewElements().add(ve);
      return CreateViewStatement.this;
    }
    @Override
    public CreateViewStatement visitLevels(SqlParser.LevelsContext ctx)
    {
      setLevels(getLevels(ctx));
      return CreateViewStatement.this;
    }
  }
  /*==================================================================*/

  private CvsVisitor _visitor = new CvsVisitor();
  private CvsVisitor getVisitor() { return _visitor; }
  
  private boolean _bRecursive = false;
  public boolean getRecursive() { return _bRecursive; }
  public void setRecursive(boolean bRecursive) { _bRecursive = bRecursive; }
  
  private QualifiedId _qViewName = new QualifiedId();
  public QualifiedId getViewName() { return _qViewName; }
  private void setViewName(QualifiedId qViewName) { _qViewName = qViewName; }

  private IdList _ilColumnNames = new IdList();
  public IdList getColumnNames() { return _ilColumnNames; }
  private void setColumnNames(IdList ilColumnNames) { _ilColumnNames = ilColumnNames; }

  private QualifiedId _qUdtName = new QualifiedId();
  public QualifiedId getUdtName() { return _qUdtName; }
  private void setUdtName(QualifiedId qUdtName) { _qUdtName = qUdtName; }
  
  private List<ViewElement> _listVe = new ArrayList<ViewElement>();
  public List<ViewElement> getViewElements() { return _listVe; }
  private void setViewElements(List<ViewElement> listVe) { _listVe = listVe; }
  
  private QualifiedId _qSuperTable = new QualifiedId();
  public QualifiedId getSuperTable() { return _qSuperTable; }
  private void setSuperTable(QualifiedId qSuperTable) { _qSuperTable = qSuperTable; }
  
  private QueryExpression _qe = null;
  public QueryExpression getQueryExpression() { return _qe; }
  public void setQueryExpression(QueryExpression qe) { _qe = qe; }

  private boolean _bCheckOption = false;
  public boolean getCheckOption() { return _bCheckOption; }
  public void setCheckOption(boolean bCheckOption) { _bCheckOption = bCheckOption; }
  
  private Levels _levels = null;
  public Levels getLevels() { return _levels; }
  public void setLevels(Levels levels) { _levels = levels; }
  
  /*------------------------------------------------------------------*/
  /** format the create view statement.
   * @return the SQL string corresponding to the fields of the create 
   *   view statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.CREATE.getKeyword();
    if (getRecursive())
      sStatement = sStatement + sSP + K.RECURSIVE.getKeyword();
    sStatement = sStatement + sSP + K.VIEW.getKeyword() + sSP + getViewName().format();
    if (getColumnNames().isSet())
      sStatement = sStatement + sLEFT_PAREN + getColumnNames().format() + sRIGHT_PAREN;
    else
    {
      sStatement = sStatement + sSP + K.OF.getKeyword() + sSP + getUdtName().format();
      if (getSuperTable() != null)
        sStatement = sStatement + sSP + K.UNDER.getKeyword() + sSP + getSuperTable().format();
      sStatement = sStatement + sLEFT_PAREN;
      for (int i = 0; i < getViewElements().size(); i++)
      {
        if (i > 0)
          sStatement = sStatement + sCOMMA;
        sStatement = sStatement + sSP + getViewElements().get(i).format();
      }
      sStatement = sStatement + sRIGHT_PAREN;
    }
    sStatement = sStatement + sSP + K.AS.getKeyword() + sSP + getQueryExpression().format();
    if (getCheckOption())
    {
      sStatement = sStatement + sSP + K.WITH.getKeyword();
      if (getLevels() != null)
        sStatement = sStatement + sSP + getLevels().getKeywords();
      sStatement = sStatement + sSP + K.CHECK.getKeyword() + sSP + K.OPTION.getKeyword();
    }
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the create view statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.CreateViewStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the create view statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().createViewStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a create view statement.
   * @param bRecursive true, of recursive view definition.
   * @param qiViewName name of view.
   * @param ilColumnNames name of columns.
   * @param qiUdtName name of UDT type.
   * @param listVe list of view elements.
   * @param qiSuperTable name of super table.
   * @param qe query expression.
   * @param bCheckOption WITH CHECK OPTION.
   * @param levels levels of check option. 
   */
  public void initialize(
    boolean bRecursive,
    QualifiedId qiViewName,
    IdList ilColumnNames,
    QualifiedId qiUdtName,
    List<ViewElement> listVe,
    QualifiedId qiSuperTable,
    QueryExpression qe,
    boolean bCheckOption,
    Levels levels)
  {
    _il.enter();
    setRecursive(bRecursive);
    setViewName(qiViewName);
    setColumnNames(ilColumnNames);
    setUdtName(qiUdtName);
    setViewElements(listVe);
    setSuperTable(qiSuperTable);
    setQueryExpression(qe);
    setCheckOption(bCheckOption);
    setLevels(levels);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CreateViewStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CreateViewStatement */
