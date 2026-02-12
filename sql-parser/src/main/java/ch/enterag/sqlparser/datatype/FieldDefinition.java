package ch.enterag.sqlparser.datatype;

import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;

public class FieldDefinition
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(FieldDefinition.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  protected class FdVisitor extends EnhancedSqlBaseVisitor<FieldDefinition>
  {
    @Override
    public FieldDefinition visitFieldName(SqlParser.FieldNameContext ctx)
    {
      setIdentifier(ctx.IDENTIFIER(), getFieldName());
      return FieldDefinition.this;
    }
    @Override
    public FieldDefinition visitDataType(SqlParser.DataTypeContext ctx)
    {
      setDataType(getSqlFactory().newDataType());
      getDataType().parse(ctx);
      return FieldDefinition.this;
    }
    @Override
    public FieldDefinition visitReferenceScopeCheck(SqlParser.ReferenceScopeCheckContext ctx)
    {
      setReferenceScopeCheck(getReferenceScopeCheck(ctx));
      return FieldDefinition.this;
    }
    @Override
    public FieldDefinition visitReferentialAction(SqlParser.ReferentialActionContext ctx)
    {
      setDeleteAction(getReferentialAction(ctx));
      return FieldDefinition.this;
    }
  } /* class FdVisitor */
  /*==================================================================*/

  protected FdVisitor _visitor = new FdVisitor();
  protected FdVisitor getVisitor() { return _visitor; }

  private Identifier _idFieldName = new Identifier();
  public Identifier getFieldName() { return _idFieldName; }
  private void setFieldName(Identifier idFieldName) { _idFieldName = idFieldName; }
  
  private DataType _dt = null;
  public DataType getDataType() { return _dt; }
  public void setDataType(DataType dt) { _dt = dt; }
  
  private ReferenceScopeCheck _rc = null;
  public ReferenceScopeCheck getReferenceScopeCheck() { return _rc; }
  public void setReferenceScopeCheck(ReferenceScopeCheck rc) { _rc = rc; }
  
  private ReferentialAction _raDelete = null;
  public ReferentialAction getDeleteAction() { return _raDelete; }
  public void setDeleteAction(ReferentialAction raDelete) { _raDelete = raDelete; }

  /*------------------------------------------------------------------*/
  /** format the field definition.
   * @return the SQL string corresponding to the fields of the field definition.
   */
  @Override
  public String format()
  {
    String sDefinition = getFieldName().format() + sSP + getDataType().format();
    if (getReferenceScopeCheck() != null)
      sDefinition = sDefinition + sSP + getReferenceScopeCheck().getKeywords();
    if (getDeleteAction() != null)
      sDefinition = sDefinition + sSP + K.ON + sSP + K.DELETE + sSP + getDeleteAction().getKeywords();
    return sDefinition;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the field definition from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.FieldDefinitionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the field definition from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().fieldDefinition());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** constructor for building a field definition.
   * @param idFieldName field name.
   * @param dt data type.
   * @param rc REFERENCES ARE (NOT)? CHECKED (or null).
   * @param raDelete ON DELETE action (or null).
   */
  public void initialize(Identifier idFieldName, DataType dt, ReferenceScopeCheck rc, ReferentialAction raDelete)
  {
    _il.enter(idFieldName, dt, rc, raDelete);
    setFieldName(idFieldName);
    setDataType(dt);
    setReferenceScopeCheck(rc);
    setDeleteAction(raDelete);
    _il.exit();
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public FieldDefinition(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class FieldDefinition */
