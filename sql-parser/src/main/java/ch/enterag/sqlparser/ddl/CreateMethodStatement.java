package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class CreateMethodStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CreateMethodStatement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CmsVisitor extends EnhancedSqlBaseVisitor<CreateMethodStatement>
  {
    @Override
    public CreateMethodStatement visitMethodType(SqlParser.MethodTypeContext ctx)
    {
      setMethodType(getMethodType(ctx));
      return CreateMethodStatement.this;
    }
    @Override
    public CreateMethodStatement visitRoutineName(SqlParser.RoutineNameContext ctx)
    {
      setRoutineName(ctx,getMethodName());
      return CreateMethodStatement.this;
    }
    @Override
    public CreateMethodStatement visitSqlParameterDeclaration(SqlParser.SqlParameterDeclarationContext ctx)
    {
      SqlParameterDeclaration spd = getSqlFactory().newSqlParameterDeclaration();
      spd.parse(ctx);
      getParameters().add(spd);
      return CreateMethodStatement.this;
    }
    @Override
    public CreateMethodStatement visitUdtName(SqlParser.UdtNameContext ctx)
    {
      setUdtName(ctx,getForTypeName());
      return CreateMethodStatement.this;
    }
    @Override
    public CreateMethodStatement visitReturnsClause(SqlParser.ReturnsClauseContext ctx)
    {
      setReturnsClause(getSqlFactory().newReturnsClause());
      getReturnsClause().parse(ctx);
      return CreateMethodStatement.this;
    }
    @Override
    public CreateMethodStatement visitRoutineCharacteristics(SqlParser.RoutineCharacteristicsContext ctx)
    {
      setRoutineCharacteristics(getSqlFactory().newRoutineCharacteristics());
      getRoutineCharacteristics().parse(ctx);
      return CreateMethodStatement.this;
    }
    @Override
    public CreateMethodStatement visitRoutineBody(SqlParser.RoutineBodyContext ctx)
    {
      setRoutineBody(getSqlFactory().newRoutineBody());
      getRoutineBody().parse(ctx);
      return CreateMethodStatement.this;
    }
  }
  /*==================================================================*/

  private CmsVisitor _visitor = new CmsVisitor();
  private CmsVisitor getVisitor() { return _visitor; }
  
  private MethodType _mt = null;
  public MethodType getMethodType() { return _mt; }
  public void setMethodType(MethodType mt) { _mt = mt; }
  
  private QualifiedId _qiMethodName = new QualifiedId();
  public QualifiedId getMethodName() { return _qiMethodName; }
  private void setMethodName(QualifiedId qiMethodName) { _qiMethodName = qiMethodName; }

  private List<SqlParameterDeclaration> _listParameters = new ArrayList<SqlParameterDeclaration>();
  public List<SqlParameterDeclaration> getParameters() { return _listParameters; }
  private void setParameters(List<SqlParameterDeclaration> listParameters) { _listParameters = listParameters; }

  private ReturnsClause _rc = null;
  public ReturnsClause getReturnsClause() { return _rc; }
  public void setReturnsClause(ReturnsClause rc) { _rc = rc; }
  
  private QualifiedId _qiForTypeName = new QualifiedId();
  public QualifiedId getForTypeName() { return _qiForTypeName; }
  private void setForTypeName(QualifiedId qiForTypeName) { _qiForTypeName = qiForTypeName; }

  private RoutineCharacteristics _rcs = null;
  public RoutineCharacteristics getRoutineCharacteristics() { return _rcs; }
  public void setRoutineCharacteristics(RoutineCharacteristics rcs) { _rcs = rcs; } 
  
  private RoutineBody _rb = null;
  public RoutineBody getRoutineBody() { return _rb; }
  public void setRoutineBody(RoutineBody rb) { _rb = rb; }
  
  private String formatParameters()
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
  /** format the create method statement.
   * @return the SQL string corresponding to the fields of the method specification.
   */
@Override
  public String format()
  {
    String sStatement = K.CREATE.getKeyword();
    if (getMethodType() != null)
      sStatement = sStatement + sSP + getMethodType().getKeywords();
    sStatement = sStatement + sSP + K.METHOD.getKeyword() + sSP + getMethodName().format() + formatParameters();
    if (getReturnsClause() != null)
      sStatement = sStatement + sNEW_LINE + getReturnsClause().format();
    sStatement = sStatement + sSP + K.FOR.getKeyword() + sSP + getForTypeName().format();
    if (getRoutineCharacteristics() != null)
      sStatement = sStatement + sSP + getRoutineCharacteristics().format();
    if (getRoutineBody() != null)
      sStatement = sStatement + sNEW_LINE + getRoutineBody().format();
    return sStatement;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the create method statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.CreateMethodStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the create method statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().createMethodStatement());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a create method statement.
   * @param mt method type.
   * @param qiMethodName name of method.
   * @param listParameters list of method parameters.
   * @param rc returns clause.
   * @param qiForTypeName name of type of method.
   * @param rcs routine characteristics.
   * @param rb routine body.
   */
  public void initialize(
      MethodType mt,
      QualifiedId qiMethodName,
      List<SqlParameterDeclaration> listParameters,
      ReturnsClause rc,
      QualifiedId qiForTypeName,
      RoutineCharacteristics rcs,
      RoutineBody rb
    )
  {
    _il.enter();
    setMethodType(mt);
    setMethodName(qiMethodName);
    setParameters(listParameters);
    setReturnsClause(rc);
    setForTypeName(qiForTypeName);
    setRoutineCharacteristics(rcs); 
    setRoutineBody(rb);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CreateMethodStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CreateMethodStatement */
