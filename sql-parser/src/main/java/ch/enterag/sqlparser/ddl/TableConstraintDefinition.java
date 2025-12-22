package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class TableConstraintDefinition
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(TableConstraintDefinition.class.getName());
  /*==================================================================*/
  private class TcdVisitor extends EnhancedSqlBaseVisitor<TableConstraintDefinition>
  {
    @Override 
    public TableConstraintDefinition visitConstraintName(SqlParser.ConstraintNameContext ctx)
    {
      setConstraintName(ctx, getConstraint());
      return TableConstraintDefinition.this;
    }
    @Override
    public TableConstraintDefinition visitTableConstraint(SqlParser.TableConstraintContext ctx)
    {
      setType(getTableConstraint(ctx));
      return visitChildren(ctx);
    }
    @Override
    public TableConstraintDefinition visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getReferencedTable());
      return TableConstraintDefinition.this;
    }
    @Override
    public TableConstraintDefinition visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      if (ctx.getParent() instanceof SqlParser.ReferencesSpecificationContext)
        addColumnName(ctx,getReferencedColumns());
      else
        addColumnName(ctx,getColumns());
      return TableConstraintDefinition.this;
    }
    @Override
    public TableConstraintDefinition visitUpdateAction(SqlParser.UpdateActionContext ctx)
    {
      setUpdateAction(getReferentialAction(ctx.referentialAction()));
      return TableConstraintDefinition.this;
    }
    @Override
    public TableConstraintDefinition visitDeleteAction(SqlParser.DeleteActionContext ctx)
    {
      setDeleteAction(getReferentialAction(ctx.referentialAction()));
      return TableConstraintDefinition.this;
    }
    @Override
    public TableConstraintDefinition visitMatch(SqlParser.MatchContext ctx)
    {
      setMatch(getMatch(ctx));
      return TableConstraintDefinition.this;
    }
    @Override
    public TableConstraintDefinition visitBooleanValueExpression(SqlParser.BooleanValueExpressionContext ctx)
    {
      setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
      getBooleanValueExpression().parse(ctx);
      return TableConstraintDefinition.this;
    }
    @Override
    public TableConstraintDefinition visitDeferrability(SqlParser.DeferrabilityContext ctx)
    {
      setDeferrability(getDeferrability(ctx));
      return TableConstraintDefinition.this;
    }
    @Override
    public TableConstraintDefinition visitConstraintCheckTime(SqlParser.ConstraintCheckTimeContext ctx)
    {
      setConstraintCheckTime(getConstraintCheckTime(ctx));
      return TableConstraintDefinition.this;
    }
  }
  /*==================================================================*/

  private TcdVisitor _visitor = new TcdVisitor();
  private TcdVisitor getVisitor() { return _visitor; }

  private QualifiedId _qConstraint = new QualifiedId();
  public QualifiedId getConstraint() { return _qConstraint; }
  private void setConstraint(QualifiedId qConstraint) { _qConstraint = qConstraint; }
  
  private TableConstraintType _type = null;
  public TableConstraintType getType() { return _type; }
  public void setType(TableConstraintType type) { _type = type; }
  
  private IdList _idlColumns = new IdList();
  public IdList getColumns() { return _idlColumns; }
  private void setColumns(IdList idlColumns) { _idlColumns = idlColumns; }
  
  private QualifiedId _qReferencedTable = new QualifiedId();
  public QualifiedId getReferencedTable() { return _qReferencedTable; }
  private void setReferencedTable(QualifiedId qReferencedTable) { _qReferencedTable = qReferencedTable; }

  private IdList _idlReferencedColumns = new IdList();
  public IdList getReferencedColumns() { return _idlReferencedColumns; }
  private void setReferencedColumns(IdList idlReferencedColumns) { _idlReferencedColumns = idlReferencedColumns; }

  private Match _match = null;
  public Match getMatch() { return _match; }
  public void setMatch(Match match) { _match = match; }
  
  private ReferentialAction _raDelete = null;
  public ReferentialAction getDeleteAction() { return _raDelete; }
  public void setDeleteAction(ReferentialAction raDelete) { _raDelete = raDelete; }

  private ReferentialAction _raUpdate = null;
  public ReferentialAction getUpdateAction() { return _raUpdate; }
  public void setUpdateAction(ReferentialAction raUpdate) { _raUpdate = raUpdate; }
  
  private BooleanValueExpression _bve = null;
  public BooleanValueExpression getBooleanValueExpression(){ return _bve; }
  public void setBooleanValueExpression(BooleanValueExpression bve) { _bve = bve; }
  
  private Deferrability _def = null;
  public Deferrability getDeferrability() { return _def; }
  public void setDeferrability(Deferrability def) { _def = def; }
  
  private ConstraintCheckTime _cct = null;
  public ConstraintCheckTime getConstraintCheckTime() { return _cct; }
  public void setConstraintCheckTime(ConstraintCheckTime cct) { _cct = cct; }
    
  /*------------------------------------------------------------------*/
  /** format the table constraint definition.
   * @return the SQL string corresponding to the fields of the table
   *   constraint definition.
   */
  @Override
  public String format()
  {
    String sDefinition = "";
    if (getConstraint().isSet())
      sDefinition = K.CONSTRAINT.getKeyword() + sSP + getConstraint().format() + sSP;
    sDefinition = sDefinition + getType().getKeywords();
    if (getType() == TableConstraintType.CHECK)
      sDefinition = sDefinition + sLEFT_PAREN + getBooleanValueExpression().format() + sRIGHT_PAREN;
    else 
    { 
      sDefinition = sDefinition + sLEFT_PAREN + getColumns().format() + sRIGHT_PAREN;
      if (getType() == TableConstraintType.FOREIGN_KEY)
      {
        sDefinition = sDefinition + sSP + K.REFERENCES.getKeyword() + sSP +
          getReferencedTable().format() +
          sLEFT_PAREN + getReferencedColumns().format() + sRIGHT_PAREN;
        if (getMatch() != null)
          sDefinition = sDefinition + sSP + getMatch().getKeywords();
        if (getDeleteAction() != null)
          sDefinition = sDefinition + sSP + 
            K.ON.getKeyword() + sSP + K.DELETE.getKeyword() + sSP + 
            getDeleteAction().getKeywords();
        if (getUpdateAction() != null)
          sDefinition = sDefinition + sSP +
            K.ON.getKeyword() + sSP + K.UPDATE.getKeyword() + sSP +
            getUpdateAction().getKeywords();
      }
    }
    if (getDeferrability() != null)
      sDefinition = sDefinition + sSP + getDeferrability().getKeywords();
    if (getConstraintCheckTime() != null)
      sDefinition = sDefinition + sSP + getConstraintCheckTime().getKeywords();
    return sDefinition;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the table constraint definition from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.TableConstraintDefinitionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the table constraint definition from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().tableConstraintDefinition());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a table constraint definition.
   * @param qConstraint constraint name (not null!).
   * @param type constraint type (not null!).
   * @param idlColumns constraint column names (not null!).
   * @param qReferencedTable referenced table (or null).
   * @param idlReferencedColumns referenced column names (or null).
   * @param match MATCH option for REFERENCES constraint (or null).
   * @param raDelete ON DELETE action (or null).
   * @param raUpdate ON UPDATE action (or null).
   * @param bve CHECK condition (or null).
   * @param def deferrability (or null).
   * @param cct constraint check time (or null).
   */
  public void initialize(
      QualifiedId qConstraint,
      TableConstraintType type,
      IdList idlColumns,
      QualifiedId qReferencedTable,
      IdList idlReferencedColumns,
      Match match, ReferentialAction raDelete, ReferentialAction raUpdate,
      BooleanValueExpression bve,
      Deferrability def, ConstraintCheckTime cct
    )
  {
    _il.enter(qConstraint,type,idlColumns,qReferencedTable,idlReferencedColumns,
      match,raDelete,raUpdate,bve,def,cct);
    setConstraint(qConstraint);
    setType(type);
    setColumns(idlColumns);
    setReferencedTable(qReferencedTable);
    setReferencedColumns(idlReferencedColumns);
    setMatch(match);
    setDeleteAction(raDelete);
    setUpdateAction(raUpdate);
    setBooleanValueExpression(bve);
    setDeferrability(def);
    setConstraintCheckTime(cct);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** initialize a PRIMARY KEY table constraint definition.
   * @param qConstraint constraint name (not null!).
   * @param idlColumns constraint column names (not null!).
   */
  public void initPrimaryKey(
      QualifiedId qConstraint,
      IdList idlColumns
    )
  {
    _il.enter(qConstraint,idlColumns);
    setConstraint(qConstraint);
    setType(TableConstraintType.PRIMARY_KEY);
    setColumns(idlColumns);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public TableConstraintDefinition(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class TableConstraintDefinition */
