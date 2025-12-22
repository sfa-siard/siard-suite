package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class DropFunctionStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DropFunctionStatement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DfsVisitor extends EnhancedSqlBaseVisitor<DropFunctionStatement>
  {
    @Override
    public DropFunctionStatement visitDropFunctionStatement(SqlParser.DropFunctionStatementContext ctx)
    {
      if (ctx.SPECIFIC() != null)
        setSpecific(true);
      if ((ctx.LEFT_PAREN() != null) && (ctx.RIGHT_PAREN() != null))
        setParameterList(true);
      setRoutineName(ctx.routineName(),getFunctionName());
      return visitChildren(ctx);
    }
    @Override
    public DropFunctionStatement visitSqlParameterDeclaration(SqlParser.SqlParameterDeclarationContext ctx)
    {
      SqlParameterDeclaration spd = getSqlFactory().newSqlParameterDeclaration();
      spd.parse(ctx);
      getParameters().add(spd);
      return DropFunctionStatement.this;
    }
    @Override
    public DropFunctionStatement visitDropBehavior(SqlParser.DropBehaviorContext ctx)
    {
      setDropBehavior(getDropBehavior(ctx));
      return DropFunctionStatement.this;
    }
  }
  /*==================================================================*/

  private DfsVisitor _visitor = new DfsVisitor();
  private DfsVisitor getVisitor() { return _visitor; }

  private boolean _bSpecific = false;
  public boolean isSpecific() { return _bSpecific; }
  public void setSpecific(boolean bSpecific) { _bSpecific = bSpecific; }
  
  private QualifiedId _qiFunctionName = new QualifiedId();
  public QualifiedId getFunctionName() { return _qiFunctionName; }
  private void setFunctionName(QualifiedId qiFunctionName) { _qiFunctionName = qiFunctionName; }

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
  /** format the drop function statement.
   * @return the SQL string corresponding to the fields of the drop function statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DROP.getKeyword();
    if (isSpecific())
      sStatement = sStatement + sSP + K.SPECIFIC.getKeyword();
    sStatement = sStatement + sSP + K.FUNCTION.getKeyword() + sSP + 
        getFunctionName().format();
    if (hasParameterList())
      sStatement = sStatement + formatParameters();
    sStatement = sStatement + sSP + getDropBehavior().getKeywords(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the drop function statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DropFunctionStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the drop function statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().dropFunctionStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a drop function statement.
   * @param bSpecific true for SPECIFIC.
   * @param qiFunctionName name of function to be dropped (not null).
   * @param bParameterList true, if parameter declarations identify function.
   * @param listParameters parameter declarations (not null!)
   * @param db drop behavior (not null!).
   */
  public void initialize(
    boolean bSpecific,
    QualifiedId qiFunctionName,
    boolean bParameterList,
    List<SqlParameterDeclaration> listParameters,
    DropBehavior db
      )
  {
    _il.enter();
    setSpecific(bSpecific);
    setFunctionName(qiFunctionName);
    setParameterList(bParameterList); 
    setParameters(listParameters);
    setDropBehavior(db);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DropFunctionStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DropFunctionStatement */
