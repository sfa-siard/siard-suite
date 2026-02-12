package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class ViewElement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(ViewElement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class VeVisitor extends EnhancedSqlBaseVisitor<ViewElement>
  {
    @Override 
    public ViewElement visitViewElement(SqlParser.ViewElementContext ctx)
    {
      if (ctx.selfrefColumnSpecification() != null)
        setType(ViewElementType.SELFREF_COLUMN_SPECIFICATION);
      else
        setType(ViewElementType.VIEW_COLUMN_OPTION);
      return visitChildren(ctx);
    }
    @Override
    public ViewElement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return ViewElement.this;
    }
    @Override
    public ViewElement visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      setColumnName(ctx,getColumnName());
      return ViewElement.this;
    }
    @Override
    public ViewElement visitReferenceGeneration(SqlParser.ReferenceGenerationContext ctx)
    {
      setReferenceGeneration(getReferenceGeneration(ctx));
      return ViewElement.this;
    }
  }
  /*==================================================================*/

  private VeVisitor _visitor = new VeVisitor();
  private VeVisitor getVisitor() { return _visitor; }
  
  private ViewElementType _type = null;
  public ViewElementType getType() { return _type; }
  public void setType(ViewElementType type) { _type = type; }
  
  /* getType() == SELFREF_COLUMN_SPECIFICATION */
  private Identifier _idColumnName = new Identifier();
  public Identifier getColumnName() { return _idColumnName; }
  private void setColumnName(Identifier idColumnName) { _idColumnName = idColumnName; }
  
  private ReferenceGeneration _rg = null;
  public ReferenceGeneration getReferenceGeneration() { return _rg; }
  public void setReferenceGeneration(ReferenceGeneration rg) { _rg = rg; }
  
  /* getType() == COLUMN_OPTION */
  /* columnName already in SELFREF */
  private QualifiedId _qTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qTableName; }
  private void setTableName(QualifiedId qTableName) { _qTableName = qTableName; }
  
  /*------------------------------------------------------------------*/
  /** format the view element.
   * @return the SQL string corresponding to the fields of the view element.
   */
  @Override
  public String format()
  {
    String sDefinition = null;
    switch (getType())
    {
      case SELFREF_COLUMN_SPECIFICATION:
        sDefinition = K.REF.getKeyword() + sSP + K.IS.getKeyword() + sSP +
          getColumnName().format() + sSP + getReferenceGeneration().getKeywords(); 
        break; 
      case VIEW_COLUMN_OPTION:
        sDefinition = getColumnName().format() + sSP + 
          K.WITH.getKeyword() + sSP + K.OPTIONS.getKeyword() + sSP +
          K.SCOPE.getKeyword() + sSP + getTableName().format();
        break;
    }
    return sDefinition;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the view element from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.ViewElementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the view element from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().viewElement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a view element.
   * @param type type of view element (not null!).
   * @param qTableName table name (type = SELFREF) (not null!).
   * @param idColumnName column name (type = SELFREF or COLUMN_OPTIONS) (not null!).
   * @param rg reference generation (type = SELFREF) or null.
   */
  public void initialize(
      ViewElementType type,
      QualifiedId qTableName,
      Identifier idColumnName,
      ReferenceGeneration rg
    )
  {
    _il.enter(type,qTableName,idColumnName);
    setType(type);
    setTableName(qTableName);
    setColumnName(idColumnName);
    setReferenceGeneration(rg);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public ViewElement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class ViewElement */
