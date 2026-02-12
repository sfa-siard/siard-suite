package ch.enterag.sqlparser.dml;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class UpdateTarget
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(UpdateTarget.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class UtVisitor extends EnhancedSqlBaseVisitor<UpdateTarget>
  {
    @Override
    public UpdateTarget visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      setColumnName(ctx,getColumnName());
      return UpdateTarget.this;
    }
    @Override
    public UpdateTarget visitSimpleValueSpecification(SqlParser.SimpleValueSpecificationContext ctx)
    {
      setSimpleValueSpecification(getSqlFactory().newSimpleValueSpecification());
      getSimpleValueSpecification().parse(ctx);
      return UpdateTarget.this;
    }
  }
  /*==================================================================*/

  private UtVisitor _visitor = new UtVisitor();
  private UtVisitor getVisitor() { return _visitor; }

  private Identifier _idColumnName = new Identifier();
  public Identifier getColumnName() { return _idColumnName; }
  private void setColumnName(Identifier idColumnName) { _idColumnName = idColumnName; }
  
  private SimpleValueSpecification _svs = null;
  public SimpleValueSpecification getSimpleValueSpecification() { return _svs; }
  public void setSimpleValueSpecification(SimpleValueSpecification svs) { _svs = svs; }
  
  /*------------------------------------------------------------------*/
  /** format the update target.
   * @return the SQL string corresponding to the fields of the update
   *   target.
   */
  @Override
  public String format()
  {
    String s = getColumnName().format();
    if (getSimpleValueSpecification() != null)
      s = s + sLEFT_BRACKET + getSimpleValueSpecification().format() + sRIGHT_BRACKET;
    return s;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the update target from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.UpdateTargetContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the update target from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().updateTarget());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize an update target.
   * @param idColumnName name of column.
   * @param svs simple value specification.
   */
  public void initialize(
      Identifier idColumnName,
      SimpleValueSpecification svs)
  {
    _il.enter();
    setColumnName(idColumnName);
    setSimpleValueSpecification(svs);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public UpdateTarget(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class UpdateTarget */
