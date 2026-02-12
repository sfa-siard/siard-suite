package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class CreateFunctionStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CreateFunctionStatement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CfsVisitor extends EnhancedSqlBaseVisitor<CreateFunctionStatement>
  {
    @Override
    public CreateFunctionStatement visitCreateFunctionStatement(SqlParser.CreateFunctionStatementContext ctx)
    {
      if ((ctx.STATIC() != null) && (ctx.DISPATCH() != null))
        setStaticDispatch(true);
      return visitChildren(ctx);
    }
    @Override
    public CreateFunctionStatement visitRoutineName(SqlParser.RoutineNameContext ctx)
    {
      setRoutineName(ctx,getFunctionName());
      return CreateFunctionStatement.this;
    }
    @Override
    public CreateFunctionStatement visitSqlParameterDeclaration(SqlParser.SqlParameterDeclarationContext ctx)
    {
      SqlParameterDeclaration spd = getSqlFactory().newSqlParameterDeclaration();
      spd.parse(ctx);
      getParameters().add(spd);
      return CreateFunctionStatement.this;
    }
    @Override
    public CreateFunctionStatement visitReturnsClause(SqlParser.ReturnsClauseContext ctx)
    {
      setReturnsClause(getSqlFactory().newReturnsClause());
      getReturnsClause().parse(ctx);
      return CreateFunctionStatement.this;
    }
    @Override
    public CreateFunctionStatement visitRoutineCharacteristics(SqlParser.RoutineCharacteristicsContext ctx)
    {
      setRoutineCharacteristics(getSqlFactory().newRoutineCharacteristics());
      getRoutineCharacteristics().parse(ctx);
      return CreateFunctionStatement.this;
    }
    @Override
    public CreateFunctionStatement visitRoutineBody(SqlParser.RoutineBodyContext ctx)
    {
      setRoutineBody(getSqlFactory().newRoutineBody());
      getRoutineBody().parse(ctx);
      return CreateFunctionStatement.this;
    }
  }
  /*==================================================================*/

  private CfsVisitor _visitor = new CfsVisitor();
  private CfsVisitor getVisitor() { return _visitor; }
  
  private QualifiedId _qiFunctionName = new QualifiedId();
  public QualifiedId getFunctionName() { return _qiFunctionName; }
  private void setFunctionName(QualifiedId qiFunctionName) { _qiFunctionName = qiFunctionName; }
  
  private List<SqlParameterDeclaration> _listParameters = new ArrayList<SqlParameterDeclaration>();
  public List<SqlParameterDeclaration> getParameters() { return _listParameters; }
  private void setParameters(List<SqlParameterDeclaration> listParameters) { _listParameters = listParameters; }

  private ReturnsClause _rc = null;
  public ReturnsClause getReturnsClause() { return _rc; }
  public void setReturnsClause(ReturnsClause rc) { _rc = rc; }
  
  private RoutineCharacteristics _rcs = null;
  public RoutineCharacteristics getRoutineCharacteristics() { return _rcs; }
  public void setRoutineCharacteristics(RoutineCharacteristics rcs) { _rcs = rcs; } 
  
  private boolean _bStaticDispatch = false;
  public boolean isStaticDispath() { return _bStaticDispatch; }
  public void setStaticDispatch(boolean bStaticDispatch) { _bStaticDispatch = bStaticDispatch; }
  
  private RoutineBody _rb = null;
  public RoutineBody getRoutineBody() { return _rb; }
  public void setRoutineBody(RoutineBody rb) { _rb = rb; }
  
  protected String formatParameters()
  {
    String s = sLEFT_PAREN;
    for (int i = 0; i < getParameters().size(); i++)
    {
      if (i > 0)
        s = s + sCOMMA;
      s = s + sNEW_LINE + sINDENT + getParameters().get(i).format();
    }
    s = s + sNEW_LINE + sRIGHT_PAREN;
    return s;
  } /* formatParameters */

  /*------------------------------------------------------------------*/
  /** format the create function statement.
   * @return the SQL string corresponding to the fields of the create 
   *   function statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.CREATE.getKeyword() + sSP + K.FUNCTION.getKeyword() + sSP +
      getFunctionName().format() + formatParameters() + sNEW_LINE + getReturnsClause().format();
    if (getRoutineCharacteristics() != null)
      sStatement = sStatement + getRoutineCharacteristics().format();
    if (getRoutineBody() != null)
      sStatement = sStatement + sNEW_LINE + getRoutineBody().format();
    return sStatement;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the create function statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.CreateFunctionStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the create function statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().createFunctionStatement());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a create function statement.
   * @param qiFunctionName name of function.
   * @param listParameters list of parameters.
   * @param rc returns clause.
   * @param rcs routine characteristics.
   * @param rb routine body.
   */
  public void initialize(
    QualifiedId qiFunctionName,
    List<SqlParameterDeclaration> listParameters,
    ReturnsClause rc,
    RoutineCharacteristics rcs,
    RoutineBody rb)
  {
    _il.enter();
    setFunctionName(qiFunctionName);
    setParameters(listParameters);
    setReturnsClause(rc);
    setRoutineCharacteristics(rcs); 
    setRoutineBody(rb);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CreateFunctionStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CreateFunctionStatement */
