package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class ColumnDefinition
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(ColumnDefinition.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CdVisitor extends EnhancedSqlBaseVisitor<ColumnDefinition>
  {
    @Override 
    public ColumnDefinition visitReferenceScopeCheck(SqlParser.ReferenceScopeCheckContext ctx)
    {
      setReferenceScopeCheck(getReferenceScopeCheck(ctx));
      return ColumnDefinition.this;
    }
    @Override 
    public ColumnDefinition visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      setColumnName(ctx,getColumnName());
      return ColumnDefinition.this;
    }
    @Override
    public ColumnDefinition visitDataType(SqlParser.DataTypeContext ctx)
    {
      setDataType(getSqlFactory().newDataType());
      getDataType().parse(ctx);
      return ColumnDefinition.this;
    }
    @Override
    public ColumnDefinition visitDeleteAction(SqlParser.DeleteActionContext ctx)
    {
      setDeleteAction(getReferentialAction(ctx.referentialAction()));
      return ColumnDefinition.this;
    }
    @Override
    public ColumnDefinition visitDefaultOption(SqlParser.DefaultOptionContext ctx)
    {
      String sDefaultValue = ctx.getText();
      if (!sDefaultValue.startsWith("'"))
        sDefaultValue = sDefaultValue.toUpperCase();
      setDefaultOption(sDefaultValue);
      return ColumnDefinition.this;
    }
    @Override
    public ColumnDefinition visitValueExpression(SqlParser.ValueExpressionContext ctx)
    {
      setValueExpression(getSqlFactory().newValueExpression());
      getValueExpression().parse(ctx);
      return ColumnDefinition.this;
    }
    @Override
    public ColumnDefinition visitColumnConstraintDefinition(SqlParser.ColumnConstraintDefinitionContext ctx)
    {
      setColumnConstraintDefinition(getSqlFactory().newColumnConstraintDefinition());
      getColumnConstraintDefinition().parse(ctx);
      return ColumnDefinition.this;
    }
  }
  /*==================================================================*/
  public static final int iUNDEFINED = -1;

  private CdVisitor _visitor = new CdVisitor();
  private CdVisitor getVisitor() { return _visitor; }
  
  private Identifier _idColumnName = new Identifier();
  public Identifier getColumnName() { return _idColumnName; }
  private void setColumnName(Identifier idColumnName) { _idColumnName = idColumnName; }
  
  private DataType _dt = null;
  public DataType getDataType() { return _dt; }
  public void setDataType(DataType dt) { _dt = dt; }
  
  private ReferenceScopeCheck _rsc = null;
  public ReferenceScopeCheck getReferenceScopeCheck() { return _rsc; }
  public void setReferenceScopeCheck(ReferenceScopeCheck rc) { _rsc = rc; }

  private ReferentialAction _raDelete = null;
  public ReferentialAction getDeleteAction() { return _raDelete; }
  public void setDeleteAction(ReferentialAction raDelete) { _raDelete = raDelete; }

  private String _sDefaultOption = null;
  public String getDefaultOption() { return _sDefaultOption; }
  public void setDefaultOption(String sDefaultOption) { _sDefaultOption = sDefaultOption; }
  
  private ValueExpression _ve = null;
  public ValueExpression getValueExpression() { return _ve; }
  public void setValueExpression(ValueExpression ve) { _ve = ve; }
  
  private ColumnConstraintDefinition _ccd = null;
  public ColumnConstraintDefinition getColumnConstraintDefinition() { return _ccd; }
  public void setColumnConstraintDefinition(ColumnConstraintDefinition ccd) { _ccd = ccd; }
  
  /*------------------------------------------------------------------*/
  /** format the column definition.
   * @return the SQL string corresponding to the fields of the column definition.
   */
  @Override
  public String format()
  {
    String sDefinition = getColumnName().format() + sSP + getDataType().format();
    if (getReferenceScopeCheck() != null)
    {
      sDefinition = sDefinition + sSP + getReferenceScopeCheck().getKeywords();
      if (getDeleteAction() != null)
        sDefinition = sDefinition + sSP + getDeleteAction().getKeywords();
    }
    if (getDefaultOption() != null)
      sDefinition = sDefinition + sSP + K.DEFAULT.getKeyword() + sSP + getDefaultOption();
    if (getValueExpression() != null)
      sDefinition = sDefinition + sSP + K.GENERATED.getKeyword() + sSP + K.ALWAYS.getKeyword() + sSP +
        K.AS.getKeyword() + sLEFT_PAREN + getValueExpression().format() + sRIGHT_PAREN;
    if (getColumnConstraintDefinition() != null)
      sDefinition = sDefinition + sSP + getColumnConstraintDefinition().format();
    return sDefinition;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the column definition from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.ColumnDefinitionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the column definition from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().columnDefinition());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a column definition.
   * @param idColumnName column name (not null!).
   * @param dt data type (not null!).
   * @param rsc REFERENCES ARE (NOT)? CHECKED (or null).
   * @param raDelete referential action of DELETE.
   * @param sDefaultOption default value (or null).
   * @param ccd column constraint definition (or null). 
   */
  public void initialize(
    Identifier idColumnName, 
    DataType dt, 
    ReferenceScopeCheck rsc,
    ReferentialAction raDelete, 
    String sDefaultOption,
    ColumnConstraintDefinition ccd)
  {
    _il.enter(idColumnName, dt, rsc, sDefaultOption, ccd);
    setColumnName(idColumnName);
    setDataType(dt);
    setReferenceScopeCheck(rsc);
    setDeleteAction(raDelete);
    setDefaultOption(sDefaultOption);
    setColumnConstraintDefinition(ccd);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** initialize a column definition.
   * @param idColumnName column name (not null!).
   * @param dt data type (not null!).
   */
  public void initNameType(
    Identifier idColumnName, 
    DataType dt) 
  {
    _il.enter(idColumnName, dt);
    setColumnName(idColumnName);
    setDataType(dt);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public ColumnDefinition(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class ColumnDefinition */
