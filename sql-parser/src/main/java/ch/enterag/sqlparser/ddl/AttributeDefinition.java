package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class AttributeDefinition
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(AttributeDefinition.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class AdVisitor extends EnhancedSqlBaseVisitor<AttributeDefinition>
  {
    @Override 
    public AttributeDefinition visitReferenceScopeCheck(SqlParser.ReferenceScopeCheckContext ctx)
    {
      setReferenceScopeCheck(getReferenceScopeCheck(ctx));
      return AttributeDefinition.this;
    }
    @Override 
    public AttributeDefinition visitAttributeName(SqlParser.AttributeNameContext ctx)
    {
      setAttributeName(ctx,getAttributeName());
      return AttributeDefinition.this;
    }
    @Override
    public AttributeDefinition visitDataType(SqlParser.DataTypeContext ctx)
    {
      setDataType(getSqlFactory().newDataType());
      getDataType().parse(ctx);
      return AttributeDefinition.this;
    }
    @Override
    public AttributeDefinition visitDeleteAction(SqlParser.DeleteActionContext ctx)
    {
      setDeleteAction(getReferentialAction(ctx.referentialAction()));
      return AttributeDefinition.this;
    }
    @Override
    public AttributeDefinition visitDefaultOption(SqlParser.DefaultOptionContext ctx)
    {
      String sDefaultValue = ctx.getText();
      if (!sDefaultValue.startsWith("'"))
        sDefaultValue = sDefaultValue.toUpperCase();
      setDefaultOption(sDefaultValue);
      return AttributeDefinition.this;
    }
  }
  /*==================================================================*/

  private AdVisitor _visitor = new AdVisitor();
  private AdVisitor getVisitor() { return _visitor; }
  
  private Identifier _idAttributeName = new Identifier();
  public Identifier getAttributeName() { return _idAttributeName; }
  private void setAttributeName(Identifier idAttributeName) { _idAttributeName = idAttributeName; }

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
  
  /*------------------------------------------------------------------*/
  /** format the attribute definition.
   * @return the SQL string corresponding to the fields of the attribute definition.
   */
  @Override
  public String format()
  {
    String sDefinition = getAttributeName().format() + sSP + getDataType().format();
    if (getReferenceScopeCheck() != null)
    {
      sDefinition = sDefinition + sSP + getReferenceScopeCheck().getKeywords();
      if (getDeleteAction() != null)
        sDefinition = sDefinition + sSP + getDeleteAction().getKeywords();
    }
    if (getDefaultOption() != null)
      sDefinition = sDefinition + sSP + K.DEFAULT.getKeyword() + sSP + getDefaultOption();
    return sDefinition;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the attribute definition from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.AttributeDefinitionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the attribute definition from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().attributeDefinition());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize an attribute definition.
   * @param idAttributeName attribute name (not null!).
   * @param dt data type (not null!).
   * @param rsc REFERENCES ARE (NOT)? CHECKED (or null).
   * @param raDelete referential action on DELETE.
   * @param sDefaultOption default value (or null).
   */
  public void initialize(
    Identifier idAttributeName, 
    DataType dt, 
    ReferenceScopeCheck rsc,
    ReferentialAction raDelete, 
    String sDefaultOption)
  {
    _il.enter(idAttributeName, dt, rsc, sDefaultOption);
    setAttributeName(idAttributeName);
    setDataType(dt);
    setReferenceScopeCheck(rsc);
    setDeleteAction(raDelete);
    setDefaultOption(sDefaultOption);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** initialize an attribute definition.
   * @param idAttributeName attribute name (not null!).
   * @param dt data type (not null!).
   */
  public void initNameType(
    Identifier idAttributeName, 
    DataType dt)
  {
    _il.enter(idAttributeName, dt);
    setAttributeName(idAttributeName);
    setDataType(dt);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public AttributeDefinition(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class AttributeDefinition */
