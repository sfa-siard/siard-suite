package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class TableElement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(TableElement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class TeVisitor extends EnhancedSqlBaseVisitor<TableElement>
  {
    @Override 
    public TableElement visitTableElement(SqlParser.TableElementContext ctx)
    {
      if (ctx.columnDefinition() != null)
        setType(TableElementType.COLUMN_DEFINITION);
      else if (ctx.tableConstraintDefinition() != null)
        setType(TableElementType.TABLE_CONSTRAINT_DEFINITION);
      else if (ctx.likeClause() != null)
        setType(TableElementType.LIKE_CLAUSE);
      else if (ctx.selfrefColumnSpecification() != null)
        setType(TableElementType.SELFREF_COLUMN_SPECIFICATION);
      else if (ctx.columnOptions() != null)
        setType(TableElementType.COLUMN_OPTIONS);
      return visitChildren(ctx);
    }
    @Override 
    public TableElement visitColumnDefinition(SqlParser.ColumnDefinitionContext ctx)
    {
      setColumnDefinition(getSqlFactory().newColumnDefinition());
      getColumnDefinition().parse(ctx);
      return visitChildren(ctx);
    }
    @Override
    public TableElement visitTableConstraintDefinition(SqlParser.TableConstraintDefinitionContext ctx)
    {
      setTableConstraintDefinition(getSqlFactory().newTableConstraintDefinition());
      getTableConstraintDefinition().parse(ctx);
      return TableElement.this;
    }
    @Override
    public TableElement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return TableElement.this;
    }
    @Override 
    public TableElement visitIdentityOption(SqlParser.IdentityOptionContext ctx)
    {
      setIdentityOption(getIdentityOption(ctx));
      return TableElement.this;
    }
    @Override
    public TableElement visitDefaultsOption(SqlParser.DefaultsOptionContext ctx)
    {
      setDefaultsOption(getDefaultsOption(ctx));
      return TableElement.this;
    }
    @Override
    public TableElement visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      setColumnName(ctx,getColumnName());
      return TableElement.this;
    }
    @Override
    public TableElement visitDefaultOption(SqlParser.DefaultOptionContext ctx)
    {
      String sDefaultValue = ctx.getText();
      if (!sDefaultValue.startsWith("'"))
        sDefaultValue = sDefaultValue.toUpperCase();
      setDefaultOption(sDefaultValue);
      return TableElement.this;
    }
    @Override
    public TableElement visitColumnConstraintDefinition(SqlParser.ColumnConstraintDefinitionContext ctx)
    {
      setColumnConstraintDefinition(getSqlFactory().newColumnConstraintDefinition());
      getColumnConstraintDefinition().parse(ctx);
      return TableElement.this;
    }
    @Override
    public TableElement visitReferenceGeneration(SqlParser.ReferenceGenerationContext ctx)
    {
      setReferenceGeneration(getReferenceGeneration(ctx));
      return TableElement.this;
    }
  }
  /*==================================================================*/

  private TeVisitor _visitor = new TeVisitor();
  private TeVisitor getVisitor() { return _visitor; }
  
  private TableElementType _type = null;
  public TableElementType getType() { return _type; }
  public void setType(TableElementType type) { _type = type; }
  
  /* getType() == COLUMN_DEFINITION */
  private ColumnDefinition _cd = null;
  public ColumnDefinition getColumnDefinition() { return _cd; }
  public void setColumnDefinition(ColumnDefinition cd) { _cd = cd; }
  
  /* getType() == TABLE_CONSTRAINT_DEFINITION */
  private TableConstraintDefinition _tcd = null;
  public TableConstraintDefinition getTableConstraintDefinition() { return _tcd; }
  public void setTableConstraintDefinition(TableConstraintDefinition tcd) { _tcd = tcd; }

  /* getType() == LIKE_CLAUSE */
  private QualifiedId _qTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qTableName; }
  private void setTableName(QualifiedId qTableName) { _qTableName = qTableName; }
  
  private IdentityOption _io = null;
  public IdentityOption getIdentityOption() { return _io; }
  public void setIdentityOption(IdentityOption io) { _io = io; }

  private DefaultsOption _do = null;
  public DefaultsOption getDefaultsOption() { return _do; }
  public void setDefaultsOption(DefaultsOption defOption) { _do = defOption; }

  /* getType() == SELFREF_COLUMN_SPECIFICATION */
  private Identifier _idColumnName = new Identifier();
  public Identifier getColumnName() { return _idColumnName; }
  private void setColumnName(Identifier idColumnName) { _idColumnName = idColumnName; }
  
  private ReferenceGeneration _rg = null;
  public ReferenceGeneration getReferenceGeneration() { return _rg; }
  public void setReferenceGeneration(ReferenceGeneration rg) { _rg = rg; }
  
  /* getType() == COLUMN_OPTIONS */
  /* columnName already in SELFREF, tableName already in LIKE */
  private String _sDefaultOption = null;
  public String getDefaultOption() { return _sDefaultOption; }
  public void setDefaultOption(String sDefaultOption) { _sDefaultOption = sDefaultOption; }
  
  private ColumnConstraintDefinition _ccd = null;
  public ColumnConstraintDefinition getColumnConstraintDefinition() { return _ccd; }
  public void setColumnConstraintDefinition(ColumnConstraintDefinition ccd) { _ccd = ccd; }
  
  /*------------------------------------------------------------------*/
  /** format the table element.
   * @return the SQL string corresponding to the fields of the table element.
   */
  @Override
  public String format()
  {
    String sDefinition = null;
    switch (getType())
    {
      case COLUMN_DEFINITION:
        sDefinition = getColumnDefinition().format();
        break;
      case TABLE_CONSTRAINT_DEFINITION:
        sDefinition = getTableConstraintDefinition().format();
        break;
      case LIKE_CLAUSE:
        sDefinition = K.LIKE.getKeyword() + sSP + getTableName().format();
        if (getIdentityOption() != null)
          sDefinition = sDefinition + sSP + getIdentityOption().getKeywords();
        if (getDefaultsOption() != null)
          sDefinition = sDefinition + sSP + getDefaultsOption().getKeywords();
        break;
      case SELFREF_COLUMN_SPECIFICATION:
        sDefinition = K.REF.getKeyword() + sSP + K.IS.getKeyword() + sSP +
          getColumnName().format() + sSP + getReferenceGeneration().getKeywords(); 
        break; 
      case COLUMN_OPTIONS:
        sDefinition = getColumnName().format() + sSP + 
          K.WITH.getKeyword() + sSP + K.OPTIONS.getKeyword() + sSP;
        if (getTableName().isSet())
          sDefinition = sDefinition + K.SCOPE.getKeyword() + sSP + getTableName().format();
        else if (getDefaultOption() != null)
          sDefinition = sDefinition + K.DEFAULT.getKeyword() + sSP + getDefaultOption();
        else
          sDefinition = sDefinition + getColumnConstraintDefinition().format();
        break;
    }
    return sDefinition;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the table element from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.TableElementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the table element from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().tableElement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a table element.
   * @param type type of table element (not null!).
   * @param cd column definition (type = COLUMN_DEFINITION) or null.
   * @param tcd table constraint definition (type = TABLE_CONSTRAINT_DEFINITION) or null.
   * @param qTableName table name (type = LIKE or SELFREF) (not null!).
   * @param io identity option (type = LIKE) or null.
   * @param dop defaults option (type = LIKE) or null.
   * @param idColumnName column name (type = SELFREF or COLUMN_OPTIONS) (not null!).
   * @param rg reference generation (type = SELFREF) or null.
   * @param sDefaultOption (type = COLUMN_OPTIONS) or null.
   * @param ccd column constraint definition (type = COLUMN_OPTIONS) or null.
   */
  public void initialize(
      TableElementType type,
      ColumnDefinition cd,
      TableConstraintDefinition tcd,
      QualifiedId qTableName,
      IdentityOption io, DefaultsOption dop,
      Identifier idColumnName,
      ReferenceGeneration rg,
      String sDefaultOption,
      ColumnConstraintDefinition ccd
    )
  {
    _il.enter(type,cd,tcd,qTableName,io,dop,idColumnName,sDefaultOption,ccd);
    setType(type);
    setColumnDefinition(cd);
    setTableConstraintDefinition(tcd);
    setTableName(qTableName);
    setIdentityOption(io);
    setDefaultsOption(dop);
    setColumnName(idColumnName);
    setReferenceGeneration(rg);
    setDefaultOption(sDefaultOption);
    setColumnConstraintDefinition(ccd);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** initialize a column definition table element.
   * @param cd column definition (type = COLUMN_DEFINITION) or null.
   */
  public void initColumnDefinition(ColumnDefinition cd)
  {
    _il.enter();
    setType(TableElementType.COLUMN_DEFINITION);
    setColumnDefinition(cd);
    _il.exit();
  } /* initColumnDefinition */

  /*------------------------------------------------------------------*/
  /** initialize a column definition table element.
   * @param tcd table constraint definition (type = TABLE_CONSTRAINT_DEFINITION) or null.
   */
  public void initTableConstraintDefinition(TableConstraintDefinition tcd)
  {
    _il.enter();
    setType(TableElementType.TABLE_CONSTRAINT_DEFINITION);
    setTableConstraintDefinition(tcd);
    _il.exit();
  } /* initTableConstraintDefinition */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public TableElement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class TableElement */
