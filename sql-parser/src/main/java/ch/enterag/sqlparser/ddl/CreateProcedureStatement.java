package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class CreateProcedureStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CreateProcedureStatement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CpsVisitor extends EnhancedSqlBaseVisitor<CreateProcedureStatement>
  {
    @Override
    public CreateProcedureStatement visitRoutineName(SqlParser.RoutineNameContext ctx)
    {
      setRoutineName(ctx,getProcedureName());
      return CreateProcedureStatement.this;
    }
    @Override
    public CreateProcedureStatement visitSqlParameterDeclaration(SqlParser.SqlParameterDeclarationContext ctx)
    {
      SqlParameterDeclaration spd = getSqlFactory().newSqlParameterDeclaration();
      spd.parse(ctx);
      getParameters().add(spd);
      return CreateProcedureStatement.this;
    }
    @Override
    public CreateProcedureStatement visitRoutineCharacteristics(SqlParser.RoutineCharacteristicsContext ctx)
    {
      setRoutineCharacteristics(getSqlFactory().newRoutineCharacteristics());
      getRoutineCharacteristics().parse(ctx);
      return CreateProcedureStatement.this;
    }
    @Override
    public CreateProcedureStatement visitRoutineBody(SqlParser.RoutineBodyContext ctx)
    {
      setRoutineBody(getSqlFactory().newRoutineBody());
      getRoutineBody().parse(ctx);
      return CreateProcedureStatement.this;
    }
  }
  /*==================================================================*/

  private CpsVisitor _visitor = new CpsVisitor();
  private CpsVisitor getVisitor() { return _visitor; }
  
  private QualifiedId _qiProcedureName = new QualifiedId();
  public QualifiedId getProcedureName() { return _qiProcedureName; }
  private void setProcedureName(QualifiedId qiProcedureName) { _qiProcedureName = qiProcedureName; }
  
  private List<SqlParameterDeclaration> _listParameters = new ArrayList<SqlParameterDeclaration>();
  public List<SqlParameterDeclaration> getParameters() { return _listParameters; }
  private void setParameters(List<SqlParameterDeclaration> listParameters) { _listParameters = listParameters; }
  
  private RoutineCharacteristics _rc = null;
  public RoutineCharacteristics getRoutineCharacteristics() { return _rc; }
  public void setRoutineCharacteristics(RoutineCharacteristics rc) { _rc = rc; } 
  
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
  /** format the create procedure statement.
   * @return the SQL string corresponding to the fields of the create 
   *   procedure statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.CREATE.getKeyword() + sSP + K.PROCEDURE.getKeyword() + sSP +
      getProcedureName().format() + formatParameters();
    if (getRoutineCharacteristics() != null)
      sStatement = sStatement + getRoutineCharacteristics().format();
    if (getRoutineBody() != null)
      sStatement = sStatement + sNEW_LINE + getRoutineBody().format();
    return sStatement;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the create procedure statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.CreateProcedureStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the create procedure statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().createProcedureStatement());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a create procedure statement.
   * @param qiProcedureName name of procedure.
   * @param listParameters list of parameters.
   * @param rc routine characteristics.
   * @param rb routine body.
   */
  public void initialize(
    QualifiedId qiProcedureName,
    List<SqlParameterDeclaration> listParameters,
    RoutineCharacteristics rc,
    RoutineBody rb)
  {
    _il.enter();
    setProcedureName(qiProcedureName);
    setParameters(listParameters);
    setRoutineCharacteristics(rc); 
    setRoutineBody(rb);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CreateProcedureStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CreateProcedureStatement */
