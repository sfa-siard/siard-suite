package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class DropProcedureStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DropProcedureStatement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DpsVisitor extends EnhancedSqlBaseVisitor<DropProcedureStatement>
  {
    @Override
    public DropProcedureStatement visitDropProcedureStatement(SqlParser.DropProcedureStatementContext ctx)
    {
      if (ctx.SPECIFIC() != null)
        setSpecific(true);
      if ((ctx.LEFT_PAREN() != null) && (ctx.RIGHT_PAREN() != null))
        setParameterList(true);
      setRoutineName(ctx.routineName(),getProcedureName());
      return visitChildren(ctx);
    }
    @Override
    public DropProcedureStatement visitSqlParameterDeclaration(SqlParser.SqlParameterDeclarationContext ctx)
    {
      SqlParameterDeclaration spd = getSqlFactory().newSqlParameterDeclaration();
      spd.parse(ctx);
      getParameters().add(spd);
      return DropProcedureStatement.this;
    }
    @Override
    public DropProcedureStatement visitDropBehavior(SqlParser.DropBehaviorContext ctx)
    {
      setDropBehavior(getDropBehavior(ctx));
      return DropProcedureStatement.this;
    }
  }
  /*==================================================================*/

  private DpsVisitor _visitor = new DpsVisitor();
  private DpsVisitor getVisitor() { return _visitor; }

  private boolean _bSpecific = false;
  public boolean isSpecific() { return _bSpecific; }
  public void setSpecific(boolean bSpecific) { _bSpecific = bSpecific; }
  
  private QualifiedId _qiProcedureName = new QualifiedId();
  public QualifiedId getProcedureName() { return _qiProcedureName; }
  private void setProcedureName(QualifiedId qiProcedureName) { _qiProcedureName = qiProcedureName; }

  private boolean _bParameterList = false;
  public boolean hasParameterList() { return _bParameterList; }
  public void setParameterList(boolean bParameterList) { _bParameterList = bParameterList; } 
  
  private List<SqlParameterDeclaration> _listParameters = new ArrayList<SqlParameterDeclaration>();
  public List<SqlParameterDeclaration> getParameters() { return _listParameters; }
  private void setParameters(List<SqlParameterDeclaration> listParameters) { _listParameters = listParameters; }
  
  private DropBehavior _db = null;
  public DropBehavior getDropBehavior() { return _db; }
  public void setDropBehavior(DropBehavior db) { _db = db; }
  
  private String formatParameters()
  {
    String s = sLEFT_PAREN;
    for (int i = 0; i < getParameters().size(); i++)
    {
      if (i > 0)
        s = s + sCOMMA + sSP;
      s = s + getParameters().get(i).format();
    }
    s = s + sRIGHT_PAREN;
    return s;
  } /* formatParameters */
  
  /*------------------------------------------------------------------*/
  /** format the drop procedure statement.
   * @return the SQL string corresponding to the fields of the drop procedure statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DROP.getKeyword();
    if (isSpecific())
      sStatement = sStatement + sSP + K.SPECIFIC.getKeyword();
    sStatement = sStatement + sSP + K.PROCEDURE.getKeyword() + sSP + 
        getProcedureName().format();
    if (hasParameterList())
      sStatement = sStatement + formatParameters();
    sStatement = sStatement + sSP + getDropBehavior().getKeywords(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the drop procedure statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DropProcedureStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the drop procedure statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().dropProcedureStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a drop procedure statement.
   * @param bSpecific true for SPECIFIC.
   * @param qiProcedureName name of procedure to be dropped (not null).
   * @param bParameterList true, if parameter declarations identify procedure.
   * @param listParameters parameter declarations (not null!)
   * @param db drop behavior (not null!).
   */
  public void initialize(
    boolean bSpecific,
    QualifiedId qiProcedureName,
    boolean bParameterList,
    List<SqlParameterDeclaration> listParameters,
    DropBehavior db
      )
  {
    _il.enter();
    setSpecific(bSpecific);
    setProcedureName(qiProcedureName);
    setParameterList(bParameterList); 
    setParameters(listParameters);
    setDropBehavior(db);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DropProcedureStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DropProcedureStatement */
