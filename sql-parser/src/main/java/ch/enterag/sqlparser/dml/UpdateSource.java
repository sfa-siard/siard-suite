package ch.enterag.sqlparser.dml;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.dml.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class UpdateSource
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(UpdateSource.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class UsVisitor extends EnhancedSqlBaseVisitor<UpdateSource>
  {
    @Override
    public UpdateSource visitValueExpression(SqlParser.ValueExpressionContext ctx)
    {
      setValueExpression(getSqlFactory().newValueExpression());
      getValueExpression().parse(ctx);
      return UpdateSource.this;
    }
    @Override
    public UpdateSource visitSpecialValue(SqlParser.SpecialValueContext ctx)
    {
      setSpecialValue(getSpecialValue(ctx));
      return UpdateSource.this;
    }
  }
  /*==================================================================*/

  private UsVisitor _visitor = new UsVisitor();
  private UsVisitor getVisitor() { return _visitor; }

  private ValueExpression _ve = null;
  public ValueExpression getValueExpression() { return _ve; }
  public void setValueExpression(ValueExpression ve) { _ve = ve; }
  
  private SpecialValue _sv = null;
  public SpecialValue getSpecialValue() { return _sv; }
  public void setSpecialValue(SpecialValue sv) { _sv = sv; }
  
  /*------------------------------------------------------------------*/
  /** format the update source.
   * @return the SQL string corresponding to the fields of the update
   *   source.
   */
  @Override
  public String format()
  {
    String s = null;
    if (getValueExpression() != null)
      s = getValueExpression().format();
    else if (getSpecialValue() != null)
      s = getSpecialValue().getKeywords();
    return s;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the update source from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.UpdateSourceContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the update source from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().updateSource());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize an update source.
   * @param ve value expression.
   * @param sv special value.
   */
  public void initialize(
      ValueExpression ve,
      SpecialValue sv)
  {
    _il.enter();
    setValueExpression(ve);
    setSpecialValue(sv);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public UpdateSource(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class UpdateSource */
