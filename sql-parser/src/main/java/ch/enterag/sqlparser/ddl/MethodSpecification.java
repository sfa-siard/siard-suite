package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class MethodSpecification
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(MethodSpecification.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class MsVisitor extends EnhancedSqlBaseVisitor<MethodSpecification>
  {
    @Override
    public MethodSpecification visitOverridingMethodSpecification(SqlParser.OverridingMethodSpecificationContext ctx)
    {
      setOverriding(true);
      return visitChildren(ctx);
    }
    @Override
    public MethodSpecification visitOriginalMethodSpecification(SqlParser.OriginalMethodSpecificationContext ctx)
    {
      setOverriding(false);
      if (ctx.LOCATOR() != null)
        setSelfAsLocator(true);
      else
        setSelfAsLocator(false);
      if (ctx.RESULT() != null)
        setSelfAsResult(true);
      else
        setSelfAsResult(false);
      return visitChildren(ctx);
    }
    @Override
    public MethodSpecification visitPartialMethodSpecification(SqlParser.PartialMethodSpecificationContext ctx)
    {
      setPartialMethodSpecification(getSqlFactory().newPartialMethodSpecification());
      getPartialMethodSpecification().parse(ctx);
      return MethodSpecification.this;
    }
    @Override
    public MethodSpecification visitLanguageName(SqlParser.LanguageNameContext ctx)
    {
      setLanguageName(getLanguageName(ctx));
      return MethodSpecification.this;
    }
    @Override 
    public MethodSpecification visitParameterStyle(SqlParser.ParameterStyleContext ctx)
    {
      setParameterStyle(getParameterStyle(ctx));
      return MethodSpecification.this;
    }
    @Override
    public MethodSpecification visitDeterministic(SqlParser.DeterministicContext ctx)
    {
      setDeterministic(getDeterministic(ctx));
      return MethodSpecification.this;
    }
    @Override
    public MethodSpecification visitDataAccess(SqlParser.DataAccessContext ctx)
    {
      setDataAccess(getDataAccess(ctx));
      return MethodSpecification.this;
    }
    @Override
    public MethodSpecification visitNullCallClause(SqlParser.NullCallClauseContext ctx)
    {
      setNullCallClause(getNullCallClause(ctx));
      return MethodSpecification.this;
    }
  }
  /*==================================================================*/
  
  private MsVisitor _visitor = new MsVisitor();
  private MsVisitor getVisitor() { return _visitor; }
  
  private boolean _bOverriding = false;
  public boolean getOverriding() { return _bOverriding; }
  public void setOverriding(boolean bOverriding) { _bOverriding = bOverriding; }
  
  private PartialMethodSpecification _pms = null;
  public PartialMethodSpecification getPartialMethodSpecification() { return _pms; }
  public void setPartialMethodSpecification(PartialMethodSpecification pms) { _pms = pms; }
  
  private boolean _bSelfAsResult = true;
  public boolean getSelfAsResult() { return _bSelfAsResult; }
  public void setSelfAsResult(boolean bSelfAsResult) { _bSelfAsResult = bSelfAsResult; }

  private boolean _bSelfAsLocator = false;
  public boolean getSelfAsLocator() { return _bSelfAsLocator; }
  public void setSelfAsLocator(boolean bSelfAsLocator) { _bSelfAsLocator = bSelfAsLocator; }

  /* each of the method characteristics only appears at most once */
  private LanguageName _ln = null;
  public LanguageName getLanguageName() { return _ln; }
  public void setLanguageName(LanguageName ln) { _ln = ln; }
  
  private ParameterStyle _ps = null;
  public ParameterStyle getParameterStyle() { return _ps; }
  public void setParameterStyle(ParameterStyle ps) { _ps = ps; }
  
  private Deterministic _deterministic = null;
  public Deterministic getDeterministic() { return _deterministic; }
  public void setDeterministic(Deterministic deterministic) { _deterministic = deterministic; }
  
  private DataAccess _da = null;
  public DataAccess getDataAccess() { return _da; }
  public void setDataAccess(DataAccess da) { _da = da; }
  
  private NullCallClause _ncc = null;
  public NullCallClause getNullCallClause() { return _ncc; }
  public void setNullCallClause(NullCallClause ncc) { _ncc = ncc; }
  
  /*------------------------------------------------------------------*/
  /** format the method specification.
   * @return the SQL string corresponding to the fields of the method specification.
   */
  @Override
  public String format()
  {
    String sSpecification = "";
    if (getOverriding())
      sSpecification = K.OVERRIDING.getKeyword() + sSP;
    sSpecification = sSpecification + getPartialMethodSpecification().format();
    if (!getOverriding())
    {
      if (getSelfAsResult())
        sSpecification = sSpecification + sSP + K.SELF.getKeyword() + sSP + 
        K.AS.getKeyword() + sSP + K.RESULT.getKeyword();
      if (getSelfAsLocator())
        sSpecification = sSpecification + sSP + K.SELF.getKeyword() + sSP + 
          K.AS.getKeyword() + sSP + K.LOCATOR.getKeyword();
      if (getLanguageName() != null)
        sSpecification = sSpecification + sSP + K.LANGUAGE.getKeyword() + sSP + 
          getLanguageName().getKeywords();
      if (getParameterStyle() != null)
        sSpecification = sSpecification + sSP + K.PARAMETER.getKeyword() + sSP + K.STYLE.getKeyword() + sSP +
          getParameterStyle().getKeywords();
      if (getDeterministic() != null)
        sSpecification = sSpecification + sSP + getDeterministic().getKeywords();
      if (getDataAccess() != null)
        sSpecification = sSpecification + sSP + getDataAccess().getKeywords();
      if (getNullCallClause() != null)
        sSpecification = sSpecification + sSP + getNullCallClause().getKeywords();
    }
    return sSpecification;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the method specification from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.MethodSpecificationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the method specification from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().methodSpecification());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a method specification.
   * @param bOverriding true, if it is an OVERRIDING specification.
   * @param pms partial method specification (not null!).
   * @param bSelfAsResult SELF AS RESULT.
   * @param bSelfAsLocator SELF AS LOCATOR.
   * @param ln LANGUAGE name characteristic (or null).
   * @param ps PARAMETER STYLE characteristic (or null).
   * @param deterministic DETERMINISTIC characteristic (or null).
   * @param da access to SQL data characteristic (or null).
   * @param ncc NULL call reaction (or null).
   */
  public void initialize(
    boolean bOverriding,
    PartialMethodSpecification pms,
    boolean bSelfAsResult,
    boolean bSelfAsLocator,
    LanguageName ln,
    ParameterStyle ps,
    Deterministic deterministic,
    DataAccess da,
    NullCallClause ncc)
  {
    _il.enter(String.valueOf(bOverriding),pms,
      String.valueOf(bSelfAsResult),String.valueOf(bSelfAsLocator),
      ln, ps, deterministic, da, ncc);
    setOverriding(bOverriding);
    setPartialMethodSpecification(pms);
    setSelfAsResult(bSelfAsResult);
    setSelfAsLocator(bSelfAsLocator);
    setLanguageName(ln);
    setParameterStyle(ps);
    setDeterministic(deterministic);
    setDataAccess(da);
    setNullCallClause(ncc);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MethodSpecification(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class MethodSpecification */
