package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class ColumnConstraintDefinition
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(ColumnConstraintDefinition.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CcdVisitor extends EnhancedSqlBaseVisitor<ColumnConstraintDefinition>
  {
    @Override 
    public ColumnConstraintDefinition visitColumnConstraint(SqlParser.ColumnConstraintContext ctx)
    {
      setType(getColumnConstraint(ctx));
      return visitChildren(ctx);
    }
    @Override 
    public ColumnConstraintDefinition visitConstraintName(SqlParser.ConstraintNameContext ctx)
    {
      setConstraintName(ctx, getConstraint());
      return ColumnConstraintDefinition.this;
    }
    @Override
    public ColumnConstraintDefinition visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx, getReferencedTable());
      return ColumnConstraintDefinition.this;
    }
    @Override
    public ColumnConstraintDefinition visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      setColumnName(ctx,getReferencedColumn());
      return ColumnConstraintDefinition.this;
    }
    @Override
    public ColumnConstraintDefinition visitUpdateAction(SqlParser.UpdateActionContext ctx)
    {
      setUpdateAction(getReferentialAction(ctx.referentialAction()));
      return ColumnConstraintDefinition.this;
    }
    @Override
    public ColumnConstraintDefinition visitDeleteAction(SqlParser.DeleteActionContext ctx)
    {
      setDeleteAction(getReferentialAction(ctx.referentialAction()));
      return ColumnConstraintDefinition.this;
    }
    @Override
    public ColumnConstraintDefinition visitMatch(SqlParser.MatchContext ctx)
    {
      setMatch(getMatch(ctx));
      return ColumnConstraintDefinition.this;
    }
    @Override
    public ColumnConstraintDefinition visitBooleanValueExpression(SqlParser.BooleanValueExpressionContext ctx)
    {
      setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
      getBooleanValueExpression().parse(ctx);
      return ColumnConstraintDefinition.this;
    }
    @Override
    public ColumnConstraintDefinition visitDeferrability(SqlParser.DeferrabilityContext ctx)
    {
      setDeferrability(getDeferrability(ctx));
      return ColumnConstraintDefinition.this;
    }
    @Override
    public ColumnConstraintDefinition visitConstraintCheckTime(SqlParser.ConstraintCheckTimeContext ctx)
    {
      setConstraintCheckTime(getConstraintCheckTime(ctx));
      return ColumnConstraintDefinition.this;
    }
  }
  /*==================================================================*/

  private CcdVisitor _visitor = new CcdVisitor();
  private CcdVisitor getVisitor() { return _visitor; }
  
  private QualifiedId _qConstraint = new QualifiedId();
  public QualifiedId getConstraint() { return _qConstraint; }
  private void setConstraint(QualifiedId qConstraint) { _qConstraint = qConstraint; }
  
  private ColumnConstraintType _type = null;
  public ColumnConstraintType getType() { return _type; }
  public void setType(ColumnConstraintType type) { _type = type; }

  private QualifiedId _qReferencedTable = new QualifiedId();
  public QualifiedId getReferencedTable() { return _qReferencedTable; }
  private void setReferencedTable(QualifiedId qReferencedTable) { _qReferencedTable = qReferencedTable; }
  
  private Identifier _idReferencedColumn = new Identifier();
  public Identifier getReferencedColumn() { return _idReferencedColumn; }
  private void setReferencedColumn(Identifier idReferencedColumn) { _idReferencedColumn = idReferencedColumn; }

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
  /** format the column constraint definition.
   * @return the SQL string corresponding to the fields of the column
   *   constraint definition.
   */
  @Override
  public String format()
  {
    String sDefinition = "";
    if (getConstraint().isSet())
      sDefinition = K.CONSTRAINT.getKeyword() + sSP + getConstraint().format() + sSP;
    sDefinition = sDefinition + getType().getKeywords();
    if (getType() == ColumnConstraintType.REFERENCES)
    {
      sDefinition = sDefinition + sSP + getReferencedTable().format();
      if (getReferencedColumn().isSet())
        sDefinition = sDefinition + sLEFT_PAREN + getReferencedColumn().format() + sRIGHT_PAREN;
      if (getMatch() != null)
        sDefinition = sDefinition + sSP + getMatch().getKeywords();
      if (getDeleteAction() != null)
        sDefinition = sDefinition + sSP + K.ON.getKeyword() + sSP + K.DELETE.getKeyword() + sSP +
          getDeleteAction().getKeywords();
      if (getUpdateAction() != null)
        sDefinition = sDefinition + sSP + K.ON.getKeyword() + sSP + K.UPDATE.getKeyword() + sSP + 
          getUpdateAction().getKeywords();
    }
    else if (getType() == ColumnConstraintType.CHECK)
      sDefinition = sDefinition + sLEFT_PAREN + getBooleanValueExpression().format() + sRIGHT_PAREN;
    if (getDeferrability() != null)
      sDefinition = sDefinition + sSP + getDeferrability().getKeywords();
    if (getConstraintCheckTime() != null)
      sDefinition = sDefinition + sSP + getConstraintCheckTime().getKeywords();
    return sDefinition;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the column constraint definition from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.ColumnConstraintDefinitionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the column constraint definition from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().columnConstraintDefinition());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a column constraint definition.
   * @param qConstraint constraint name (not null!).
   * @param type column constraint type (not null!).
   * @param qReferencedTable referenced table (not null!).
   * @param idReferencedColumn referenced column name (not null!).
   * @param match MATCH option for REFERENCES constraint (or null).
   * @param raDelete ON DELETE action (or null).
   * @param raUpdate ON UPDATE action (or null).
   * @param bve CHECK condition (or null).
   * @param def deferrability (or null).
   * @param cct constraint check time (or null).
   */
  public void initialize(
      QualifiedId qConstraint,
      ColumnConstraintType type,
      QualifiedId qReferencedTable,
      Identifier idReferencedColumn,
      Match match, ReferentialAction raDelete, ReferentialAction raUpdate,
      BooleanValueExpression bve,
      Deferrability def, ConstraintCheckTime cct
    )
  {
    _il.enter(qConstraint, type, qReferencedTable, idReferencedColumn, 
      match, raDelete, raUpdate, bve, def, cct);
    setConstraint(qConstraint);
    setType(type);
    setReferencedTable(qReferencedTable);
    setReferencedColumn(idReferencedColumn);
    setMatch(match);
    setDeleteAction(raDelete);
    setUpdateAction(raUpdate);
    setBooleanValueExpression(bve);
    setDeferrability(def);
    setConstraintCheckTime(cct);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public ColumnConstraintDefinition(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class ColumnConstraintDefinition */
