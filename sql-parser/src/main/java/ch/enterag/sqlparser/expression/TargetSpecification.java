package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class TargetSpecification
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(TargetSpecification.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class TsVisitor extends EnhancedSqlBaseVisitor<TargetSpecification>
  {
    @Override
    public TargetSpecification visitTargetSpecification(SqlParser.TargetSpecificationContext ctx)
    {
      TargetSpecification tsReturn = TargetSpecification.this; 
      if (ctx.QUESTION_MARK() != null)
        setQuestionMark(true);
      else
        tsReturn = visitChildren(ctx);
      return tsReturn;
    }
    @Override
    public TargetSpecification visitVariableName(SqlParser.VariableNameContext ctx)
    {
      setVariableName(ctx,getVariableName());
      return TargetSpecification.this;
    }
    @Override
    public TargetSpecification visitIndicatorVariable(SqlParser.IndicatorVariableContext ctx)
    {
      setVariableName(ctx.variableName(),getIndicatorVariable());
      return TargetSpecification.this;
    }
    @Override
    public TargetSpecification visitIdentifierChain(SqlParser.IdentifierChainContext ctx)
    {
      setIdChain(ctx,getSqlParameterOrColumnReference());
      return TargetSpecification.this;
    }
    @Override
    public TargetSpecification visitTargetArrayReference(SqlParser.TargetArrayReferenceContext ctx)
    {
      setIdChain(ctx.identifierChain(),getTargetArrayReference());
      return TargetSpecification.this;
    }
    @Override
    public TargetSpecification visitSimpleValueSpecification(SqlParser.SimpleValueSpecificationContext ctx)
    {
      setSimpleValueSpecification(getSqlFactory().newSimpleValueSpecification());
      getSimpleValueSpecification().parse(ctx);
      return TargetSpecification.this;
    }
  }
  /*==================================================================*/
  
  private TsVisitor _visitor = new TsVisitor();
  private TsVisitor getVisitor() { return _visitor; }
  
  private ColonId _ciVariableName = new ColonId();
  public ColonId getVariableName() { return _ciVariableName; }
  private void setVariableName(ColonId ciVariableName) { _ciVariableName = ciVariableName; }
  
  private ColonId _ciIndicatorVariable = new ColonId();
  public ColonId getIndicatorVariable() { return _ciIndicatorVariable; }
  private void setIndicatorVariable(ColonId ciIndicatorVariable) { _ciIndicatorVariable = ciIndicatorVariable; }
  
  private IdChain _icSqlOrColumnRef = new IdChain();
  public IdChain getSqlParameterOrColumnReference() { return _icSqlOrColumnRef; }
  private void setSqlParameterOrColumnReference(IdChain icSqlOrColumnRef) { _icSqlOrColumnRef = icSqlOrColumnRef; }
  
  private IdChain _icTargetArrayRef = new IdChain();
  public IdChain getTargetArrayReference() { return _icTargetArrayRef; }
  private void setTargetArrayReference(IdChain icTargetArrayRef) { _icTargetArrayRef = icTargetArrayRef; }
  
  private SimpleValueSpecification _svs = null;
  public SimpleValueSpecification getSimpleValueSpecification() { return _svs; }
  public void setSimpleValueSpecification(SimpleValueSpecification svs) { _svs = svs; }
  
  private boolean _bQuestionMark = false;
  public boolean isQuestionMark() { return _bQuestionMark; }
  public void setQuestionMark(boolean bQuestionMark) { _bQuestionMark = bQuestionMark; }
  
  /*------------------------------------------------------------------*/
  /** format the target specification.
   * @return the SQL string corresponding to the fields of the target specification.
   */
  @Override
  public String format()
  {
    String sSpecification = "";
    if (getVariableName().isSet())
    {
      sSpecification = getVariableName().format();
      if (getIndicatorVariable().isSet())
      {
        sSpecification = sSpecification + sSP + 
        K.INDICATOR.getKeyword() + sSP + getIndicatorVariable().format();
      }
    }
    else if (getSqlParameterOrColumnReference().isSet())
      sSpecification = getSqlParameterOrColumnReference().format();
    else if (getTargetArrayReference().isSet() && 
             (getSimpleValueSpecification() != null))
    {
      sSpecification = getTargetArrayReference().format() + sLEFT_BRACKET +
          getSimpleValueSpecification().format() + sRIGHT_BRACKET;
    }
    else if (isQuestionMark())
      sSpecification = sQUESTION_MARK;
    return sSpecification;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the target specification from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.TargetSpecificationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the target specification from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().targetSpecification());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a target specification.
   * @param ciVariableName variable name (not null).
   * @param ciIndicatorVariable INDICATOR variable (not null).
   * @param icSqlParameterOrColumnReference SQL parameter or column reference (not null).
   * @param icTargetArrayRef target array reference (not null).
   * @param svs index of target array.
   * @param bQuestionMark dynamic parameter specification.
   */
  public void initialize(
    ColonId ciVariableName,
    ColonId ciIndicatorVariable,
    IdChain icSqlParameterOrColumnReference,
    IdChain icTargetArrayRef,
    SimpleValueSpecification svs,
    boolean bQuestionMark)
  {
    _il.enter(ciVariableName, ciIndicatorVariable, 
      icSqlParameterOrColumnReference, icTargetArrayRef, svs, 
      String.valueOf(bQuestionMark));
    setVariableName(ciVariableName);
    setIndicatorVariable(ciIndicatorVariable);
    setSqlParameterOrColumnReference(icSqlParameterOrColumnReference);
    setTargetArrayReference(icTargetArrayRef);
    setSimpleValueSpecification(svs);
    setQuestionMark(bQuestionMark);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public TargetSpecification(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class TargetSpecification */
