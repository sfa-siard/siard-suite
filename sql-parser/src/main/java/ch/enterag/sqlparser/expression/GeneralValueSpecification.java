package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class GeneralValueSpecification
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(GeneralValueSpecification.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class GvsVisitor extends EnhancedSqlBaseVisitor<GeneralValueSpecification>
  {
    @Override
    public GeneralValueSpecification visitGeneralValueSpecification(SqlParser.GeneralValueSpecificationContext ctx)
    {
      GeneralValueSpecification vepResult = GeneralValueSpecification.this; 
      setGeneralValue(getGeneralValue(ctx));
      if (vepResult.getGeneralValue() == null)
        vepResult = visitChildren(ctx);
      return vepResult;
    }
    @Override
    public GeneralValueSpecification visitVariableName(SqlParser.VariableNameContext ctx)
    {
      setVariableName(ctx, getVariable());
      return GeneralValueSpecification.this;
    }
    @Override 
    public GeneralValueSpecification visitIndicatorVariable(SqlParser.IndicatorVariableContext ctx)
    {
      setVariableName(ctx.variableName(), getIndicator());
      return GeneralValueSpecification.this;
    }
    @Override
    public GeneralValueSpecification visitIdentifierChain(SqlParser.IdentifierChainContext ctx)
    {
      setIdChain(ctx, getColumnOrParameter());
      return GeneralValueSpecification.this;
    }
  }
  /*==================================================================*/

  private GvsVisitor _visitor = new GvsVisitor();
  private GvsVisitor getVisitor() { return _visitor; }
  
  /* "general value" is either an embedded variable, an sql parameter spec, a dynamic ? or a constant */
  private ColonId _cid = new ColonId();
  public ColonId getVariable() { return _cid; }
  private void setVariable(ColonId cid) { _cid = cid; }
  
  private ColonId _cidIndicator = new ColonId();
  public ColonId getIndicator() { return _cidIndicator; }
  private void setIndicator(ColonId cidIndicator) { _cidIndicator = cidIndicator; }
  
  private IdChain _idcColumnOrParameter = new IdChain();
  public IdChain getColumnOrParameter() { return _idcColumnOrParameter; }
  private void setColumnOrParameter(IdChain idcParameter) { _idcColumnOrParameter = idcParameter; }
  
  private GeneralValue _gv = null;
  public GeneralValue getGeneralValue() { return _gv; }
  public void setGeneralValue(GeneralValue generalValue) { _gv = generalValue; }
  
  /*------------------------------------------------------------------*/
  /** format the general value specification.
   * @return the SQL string corresponding to the fields of the general value specification.
   */
  @Override
  public String format()
  {
    String sSpecification = null;
    if (getVariable().isSet())
    {
      sSpecification = getVariable().format();
      if (getIndicator().isSet())
        sSpecification = sSpecification + sSP + K.INDICATOR.getKeyword() + sSP + getIndicator().format();
    }
    else if (getColumnOrParameter().isSet())
      sSpecification = getColumnOrParameter().format();
    else if (getGeneralValue() != null)
      sSpecification = getGeneralValue().getKeywords();
    return sSpecification;
  } /* format */

  /*------------------------------------------------------------------*/
  /** get the type of the general value from the context of a query.
   * @param ss sql statement.
   * @return data type of the column
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (getColumnOrParameter().isSet())
      dt = ss.getColumnType(getColumnOrParameter());
    else if (getGeneralValue() != null)
      dt = ss.getGeneralType(this);
    else
      throw new IllegalArgumentException("Evaluating variables is not supported!");
    return dt;
  } /* getDataType */
  
  /*------------------------------------------------------------------*/
  /** evaluate the general value against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return value of the column.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oValue = null;
    if (getColumnOrParameter().isSet())
      oValue = ss.getColumnValue(getColumnOrParameter(),bAggregated);
    else if (getGeneralValue() != null)
      oValue = ss.getGeneralValue(this);
    else
      throw new IllegalArgumentException("Evaluating variables is not supported!");
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** parse the general value specification from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.GeneralValueSpecificationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the general value specification from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().generalValueSpecification());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a general value specification.
   * @param cidVariable variable name.
   * @param cidIndicator indicator name.
   * @param idcColumnOrParameter column or parameter name.
   * @param gv general value.
   */
  public void initialize(
      ColonId cidVariable,
      ColonId cidIndicator,
      IdChain idcColumnOrParameter,
      GeneralValue gv
    )
  {
    _il.enter(cidVariable,cidIndicator,idcColumnOrParameter,gv);
    setVariable(cidVariable);
    setIndicator(cidIndicator);
    setColumnOrParameter(idcColumnOrParameter);
    setGeneralValue(gv);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** initialize a general value specification.
   * @param idcColumnOrParameter column or parameter name.
   */
  public void initialize(IdChain idcColumnOrParameter)
  {
    setColumnOrParameter(idcColumnOrParameter);
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public GeneralValueSpecification(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class GeneralValueSpecification */
