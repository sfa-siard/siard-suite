package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class AlterTableStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(AlterTableStatement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class AtsVisitor extends EnhancedSqlBaseVisitor<AlterTableStatement>
  {
    @Override
    public AlterTableStatement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return AlterTableStatement.this;
    }
    @Override
    public AlterTableStatement visitAddColumnDefinition(SqlParser.AddColumnDefinitionContext ctx)
    {
      setColumnDefinition(getSqlFactory().newColumnDefinition());
      getColumnDefinition().parse(ctx.columnDefinition());
      return AlterTableStatement.this;
    }
    @Override
    public AlterTableStatement visitDropColumnDefinition(SqlParser.DropColumnDefinitionContext ctx)
    {
      setColumnName(ctx.columnName(),getColumnName());
      return AlterTableStatement.this;
    }
    @Override
    public AlterTableStatement visitAlterColumnDefinition(SqlParser.AlterColumnDefinitionContext ctx)
    {
      setColumnName(ctx.columnName(),getColumnName());
      setAlterColumnAction(getSqlFactory().newAlterColumnAction());
      getAlterColumnAction().parse(ctx.alterColumnAction());
      return AlterTableStatement.this;
    }
    @Override
    public AlterTableStatement visitAddTableConstraintDefinition(SqlParser.AddTableConstraintDefinitionContext ctx)
    {
      setTableConstraintDefinition(getSqlFactory().newTableConstraintDefinition());
      getTableConstraintDefinition().parse(ctx.tableConstraintDefinition());
      return AlterTableStatement.this;
    }
    @Override
    public AlterTableStatement visitDropTableConstraintDefinition(SqlParser.DropTableConstraintDefinitionContext ctx)
    {
      setConstraintName(ctx.constraintName(),getConstraintName());
      setDropBehavior(getDropBehavior(ctx.dropBehavior()));
      return AlterTableStatement.this;
    }
  }
  /*==================================================================*/
  
  private AtsVisitor _visitor = new AtsVisitor();
  private AtsVisitor getVisitor() { return _visitor; }

  /* type name */
  private QualifiedId _qTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qTableName; }
  private void setTableName(QualifiedId qTableName) { _qTableName = qTableName; }
  
  /* add column definition */
  private ColumnDefinition _cd = null;
  public ColumnDefinition getColumnDefinition() { return _cd; }
  public void setColumnDefinition(ColumnDefinition cd) { _cd = cd; }
  
  /* drop or alter column definition */
  private Identifier _idColumnName = new Identifier();
  public Identifier getColumnName() { return _idColumnName; }
  private void setColumnName(Identifier idColumnName) { _idColumnName = idColumnName; }
  
  /* alter column definition */
  private AlterColumnAction _aca = null;
  public AlterColumnAction getAlterColumnAction() { return _aca; }
  public void setAlterColumnAction(AlterColumnAction aca) { _aca = aca; }
  
  /* add table constraint definition */
  private TableConstraintDefinition _tcd = null;
  public TableConstraintDefinition getTableConstraintDefinition() { return _tcd; }
  public void setTableConstraintDefinition(TableConstraintDefinition tcd) { _tcd = tcd; }
  
  /* drop table constraint definition */
  private QualifiedId _qConstraintName = new QualifiedId();
  public QualifiedId getConstraintName() { return _qConstraintName; }
  private void setConstraintName(QualifiedId qConstraintName) { _qConstraintName = qConstraintName; }
  
  private DropBehavior _db = null;
  public DropBehavior getDropBehavior() { return _db; }
  public void setDropBehavior(DropBehavior db) { _db = db; }
  
  /*------------------------------------------------------------------*/
  /** format the alter table statement.
   * @return the SQL string corresponding to the fields of the alter table 
   *   statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.ALTER.getKeyword() + sSP + K.TABLE.getKeyword() + sSP + 
      getTableName().format();
    if (getColumnDefinition() != null)
      sStatement = sStatement + sSP + K.ADD.getKeyword() + sSP + K.COLUMN.getKeyword() + sSP + 
        getColumnDefinition().format();
    else if (getAlterColumnAction() != null)
      sStatement = sStatement + sSP + K.ALTER.getKeyword() + sSP + K.COLUMN.getKeyword() + sSP +
        getColumnName().format() + sSP + getAlterColumnAction().format();
    else if (getColumnName().isSet())
      sStatement = sStatement + sSP + K.DROP.getKeyword() + sSP + K.COLUMN.getKeyword() + sSP +
        getColumnName().format();
    else if (getTableConstraintDefinition() != null)
      sStatement = sStatement + sSP + K.ADD.getKeyword() + sSP + 
        getTableConstraintDefinition().format();
    else if (getConstraintName().isSet())
      sStatement = sStatement + sSP + K.DROP.getKeyword() + sSP + K.CONSTRAINT.getKeyword() + sSP +
        getConstraintName().format() + sSP + getDropBehavior().getKeywords();
    return sStatement;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the alter table statement  from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.AlterTableStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the alter table statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().alterTableStatement());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize an alter table statement.
   * @param qTableName name of type to be altered (not null!).
   * @param cd definition of column to be added (or null).
   * @param idColumnName name of column to be dropped (not null!).
   * @param aca action of column to be altered (or null).
   * @param tcd definition of table constraint to be added (or null).
   * @param qConstraintName name of table constraint to be dropped (not null!).
   * @param db drop behavior for table constraint to be dropped (or null). 
   */
  public void initialize(
    QualifiedId qTableName,
    ColumnDefinition cd,
    Identifier idColumnName,
    AlterColumnAction aca,
    TableConstraintDefinition tcd,
    QualifiedId qConstraintName,
    DropBehavior db)
  {
    _il.enter(qTableName,cd,idColumnName,aca,tcd,db);
    setTableName(qTableName);
    setColumnDefinition(cd);
    setColumnName(idColumnName);
    setAlterColumnAction(aca);
    setTableConstraintDefinition(tcd);
    setConstraintName(qConstraintName);
    setDropBehavior(db);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public AlterTableStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class AlterTableStatement */
