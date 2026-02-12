package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class AlterColumnAction
extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(AlterColumnAction.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class AcaVisitor extends EnhancedSqlBaseVisitor<AlterColumnAction>
  {
    @Override
    public AlterColumnAction visitSetColumnDefaultClause(SqlParser.SetColumnDefaultClauseContext ctx)
    {
      String sDefaultValue = ctx.defaultOption().getText();
      if (!sDefaultValue.startsWith("'"))
        sDefaultValue = sDefaultValue.toUpperCase();
      setDefaultOption(sDefaultValue);
      return AlterColumnAction.this;
    }
    @Override 
    public AlterColumnAction visitDropColumnDefaultClause(SqlParser.DropColumnDefaultClauseContext ctx)
    {
      setDropDefault(true);
      return AlterColumnAction.this;
    }
    @Override
    public AlterColumnAction visitAddColumnScopeClause(SqlParser.AddColumnScopeClauseContext ctx)
    {
      setTableName(ctx.tableName(),getScopeTable());
      return AlterColumnAction.this;
    }
    @Override
    public AlterColumnAction visitDropColumnScopeClause(SqlParser.DropColumnScopeClauseContext ctx)
    {
      setScopeDropBehavior(getDropBehavior(ctx.dropBehavior()));
      return AlterColumnAction.this;
    }
  }
  /*==================================================================*/
  
  private AcaVisitor _visitor = new AcaVisitor();
  private AcaVisitor getVisitor() { return _visitor; }

  /* SET DEFAULT option */
  private String _sDefaultOption = null;
  public String getDefaultOption() { return _sDefaultOption; }
  public void setDefaultOption(String sDefaultOption) { _sDefaultOption = sDefaultOption; }
  
  /* DROP DEFAULT */
  private boolean _bDropDefault = false;
  public boolean isDropDefault() { return _bDropDefault; }
  public void setDropDefault(boolean bDropDefault) { _bDropDefault = bDropDefault; }
  
  /* ADD SCOPE */
  private QualifiedId _qScopeTable = new QualifiedId();
  public QualifiedId getScopeTable() { return _qScopeTable; }
  private void setScopeTable(QualifiedId qScopeTable) { _qScopeTable = qScopeTable; }
  
  /* DROP SCOPE */
  private DropBehavior _dbScope = null;
  public DropBehavior getScopeDropBehavior() { return _dbScope; }
  public void setScopeDropBehavior(DropBehavior dbScope) { _dbScope = dbScope; }
  
  /*------------------------------------------------------------------*/
  /** format the alter column action.
   * @return the SQL string corresponding to the fields of the column definition.
   */
  @Override
  public String format()
  {
    String sAction = null;
    if (getDefaultOption() != null)
      sAction = K.SET.getKeyword() + sSP + K.DEFAULT.getKeyword() + sSP + getDefaultOption();
    else if (isDropDefault())
      sAction = K.DROP.getKeyword() + sSP + K.DEFAULT.getKeyword();
    else if (getScopeTable().isSet())
      sAction = K.ADD.getKeyword() + sSP + K.SCOPE.getKeyword() + sSP + getScopeTable().format();
    else if (getScopeDropBehavior() != null)
      sAction = K.DROP.getKeyword() + sSP + K.SCOPE.getKeyword() + sSP + getScopeDropBehavior().getKeywords();
    return sAction;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the alter column action from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.AlterColumnActionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the alter column action from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().alterColumnAction());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize an alter column action.
   * @param sDefaultOption value for SET DEFAULT (or null).
   * @param bDropDefault true for DROP DEFAULT.
   * @param qScopeTable for ADD SCOPE (not null!).
   * @param dbScope drop behavior for DROP SCOPE (or null).
   */
  public void initialize(
    String sDefaultOption, 
    boolean bDropDefault, 
    QualifiedId qScopeTable,
    DropBehavior dbScope)
  {
    _il.enter(sDefaultOption, String.valueOf(bDropDefault), qScopeTable, dbScope);
    setDefaultOption(sDefaultOption);
    setDropDefault(bDropDefault);
    setScopeTable(qScopeTable);
    setScopeDropBehavior(dbScope);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public AlterColumnAction(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class AlterColumnAction */
