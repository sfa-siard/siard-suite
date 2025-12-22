package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class SimpleValueSpecification
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SimpleValueSpecification.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class SvsVisitor extends EnhancedSqlBaseVisitor<SimpleValueSpecification>
  {
    @Override
    public SimpleValueSpecification visitLiteral(SqlParser.LiteralContext ctx)
    {
      setLiteral(getSqlFactory().newLiteral());
      getLiteral().parse(ctx);
      return SimpleValueSpecification.this;
    }
    @Override
    public SimpleValueSpecification visitVariableName(SqlParser.VariableNameContext ctx)
    {
      setVariableName(ctx,getVariableName());
      return SimpleValueSpecification.this;
    }
    @Override
    public SimpleValueSpecification visitIndicatorVariable(SqlParser.IndicatorVariableContext ctx)
    {
      setVariableName(ctx.variableName(),getIndicatorVariable());
      return SimpleValueSpecification.this;
    }
    @Override
    public SimpleValueSpecification visitIdentifierChain(SqlParser.IdentifierChainContext ctx)
    {
      setIdChain(ctx,getSqlParameterSpecification());
      return SimpleValueSpecification.this;
    }
  }
  /*==================================================================*/
  
  private SvsVisitor _visitor = new SvsVisitor();
  private SvsVisitor getVisitor() { return _visitor; }

  private Literal _literal = null;
  public Literal getLiteral() { return _literal; }
  public void setLiteral(Literal literal) { _literal = literal; }
  
  private ColonId _ciVariableName = new ColonId();
  public ColonId getVariableName() { return _ciVariableName; }
  private void setVariableName(ColonId ciVariableName) { _ciVariableName = ciVariableName; }
  
  private ColonId _ciIndicatorVariable = new ColonId();
  public ColonId getIndicatorVariable() { return _ciIndicatorVariable; }
  private void setIndicatorVariable(ColonId ciIndicatorVariable) { _ciIndicatorVariable = ciIndicatorVariable; }
  
  private IdChain _icSqlParameter = null;
  public IdChain getSqlParameterSpecification() { return _icSqlParameter; }
  private void setSqlParameterSpecification(IdChain icSqlParameter) { _icSqlParameter = icSqlParameter; }
  
  /*------------------------------------------------------------------*/
  /** format the simple value specification.
   * @return the SQL string corresponding to the fields of the simple 
   *   value specification.
   */
  @Override
  public String format()
  {
    String sSpecification = null;
    if (getLiteral() != null)
      sSpecification = getLiteral().format();
    else if (getVariableName().isSet())
    {
      sSpecification = getVariableName().format();
      if (getIndicatorVariable().isSet())
      {
        sSpecification = sSpecification + sSP + 
        K.INDICATOR.getKeyword() + sSP + getIndicatorVariable().format();
      }
    }
    else if (getSqlParameterSpecification().isSet())
      sSpecification = getSqlParameterSpecification().format();
    return sSpecification;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the simple value specification from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.SimpleValueSpecificationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the simple value specification from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().simpleValueSpecification());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a simple value specification.
   * @param literal literal value.
   * @param ciVariableName variable name.
   * @param ciIndicatorVariable variable name for INDICATOR option.
   * @param icSqlParameter SQL parameter specification.
   */
  public void initialize(
    Literal literal,
    ColonId ciVariableName,
    ColonId ciIndicatorVariable,
    IdChain icSqlParameter)
  {
    _il.enter(literal, ciVariableName, ciIndicatorVariable, icSqlParameter);
    setLiteral(literal);
    setVariableName(ciVariableName);
    setIndicatorVariable(ciIndicatorVariable);
    setSqlParameterSpecification(icSqlParameter);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public SimpleValueSpecification(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class SimpleValueSpecification */
