package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class SqlParameterDeclaration
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SqlParameterDeclaration.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class SpdVisitor extends EnhancedSqlBaseVisitor<SqlParameterDeclaration>
  {
    @Override
    public SqlParameterDeclaration visitParameterMode(SqlParser.ParameterModeContext ctx)
    {
      setParameterMode(getParameterMode(ctx));
      return SqlParameterDeclaration.this;
    }
    @Override
    public SqlParameterDeclaration visitParameterName(SqlParser.ParameterNameContext ctx)
    {
      setParameterName(ctx,getParameterName());
      return SqlParameterDeclaration.this;
    }
    @Override
    public SqlParameterDeclaration visitParameterType(SqlParser.ParameterTypeContext ctx)
    {
      if (ctx.LOCATOR() != null)
        setDataType(null);
      else
      {
        setDataType(getSqlFactory().newDataType());
        getDataType().parse(ctx.dataType());
      }
      return SqlParameterDeclaration.this;
    }
    @Override
    public SqlParameterDeclaration visitSqlParameterDeclaration(SqlParser.SqlParameterDeclarationContext ctx)
    {
      if (ctx.RESULT() != null)
        setResult(true);
      else
        setResult(false);
      return visitChildren(ctx);
    }
  }
  /*==================================================================*/

  private SpdVisitor _visitor = new SpdVisitor();
  private SpdVisitor getVisitor() { return _visitor; }
  
  private ParameterMode _pm = null;
  public ParameterMode getParameterMode() { return _pm; }
  public void setParameterMode(ParameterMode pm) { _pm = pm; }
  
  private Identifier _idParameterName = new Identifier();
  public Identifier getParameterName() { return _idParameterName; }
  private void setParameterName(Identifier idParameterName) { _idParameterName = idParameterName; }
  
  private DataType _dt = null;
  public DataType getDataType() { return _dt; }
  public void setDataType(DataType dt) { _dt = dt; }
  
  private boolean _bResult = false;
  public boolean getResult() { return _bResult; }
  public void setResult(boolean bResult) { _bResult = bResult; }
  
  /*------------------------------------------------------------------*/
  /** format the SQL parameter declaration.
   * @return the SQL string corresponding to the fields of the SQL parameter declaration.
   */
  @Override
  public String format()
  {
    String sDeclaration = "";
    if (getParameterMode() != null)
      sDeclaration = getParameterMode().getKeywords() + sSP;
    if (getParameterName().isSet())
      sDeclaration = sDeclaration + getParameterName().format();
    if (getDataType() != null)
      sDeclaration = sDeclaration + sSP + getDataType().format();
    else
      sDeclaration = sDeclaration + sSP + K.AS.getKeyword() + sSP + K.LOCATOR.getKeyword();
    if (getResult())
      sDeclaration = sDeclaration + sSP + K.RESULT.getKeyword();
    return sDeclaration;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the SQL parameter declaration from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.SqlParameterDeclarationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the SQL parameter declaration from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().sqlParameterDeclaration());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize an SQL parameter declaration.
   * @param pm paramter mode (or null).
   * @param idParameterName (not null!).
   */
  public void initialize(
    ParameterMode pm,
    Identifier idParameterName)
  {
    _il.enter(pm, idParameterName);
    setParameterMode(pm);
    setParameterName(idParameterName);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public SqlParameterDeclaration(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class SqlParameterDeclaration */
