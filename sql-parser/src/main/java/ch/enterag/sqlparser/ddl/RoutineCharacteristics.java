package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class RoutineCharacteristics
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(RoutineCharacteristics.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class RcVisitor extends EnhancedSqlBaseVisitor<RoutineCharacteristics>
  {
    @Override
    public RoutineCharacteristics visitLanguageName(SqlParser.LanguageNameContext ctx)
    {
      setLanguageName(getLanguageName(ctx));
      return RoutineCharacteristics.this;
    }
    @Override 
    public RoutineCharacteristics visitParameterStyle(SqlParser.ParameterStyleContext ctx)
    {
      setParameterStyle(getParameterStyle(ctx));
      return RoutineCharacteristics.this;
    }
    @Override
    public RoutineCharacteristics visitDeterministic(SqlParser.DeterministicContext ctx)
    {
      setDeterministic(getDeterministic(ctx));
      return RoutineCharacteristics.this;
    }
    @Override
    public RoutineCharacteristics visitDataAccess(SqlParser.DataAccessContext ctx)
    {
      setDataAccess(getDataAccess(ctx));
      return RoutineCharacteristics.this;
    }
    @Override
    public RoutineCharacteristics visitNullCallClause(SqlParser.NullCallClauseContext ctx)
    {
      setNullCallClause(getNullCallClause(ctx));
      return RoutineCharacteristics.this;
    }
  }
  /*==================================================================*/
  
  private RcVisitor _visitor = new RcVisitor();
  private RcVisitor getVisitor() { return _visitor; }
  
  /* each of the routine characteristics only appears at most once */
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
  /** format the routine characteristics.
   * @return the SQL string corresponding to the fields of the routine characteristics.
   */
  @Override
  public String format()
  {
    String s = "";
    if (getLanguageName() != null)
      s = s + sNEW_LINE + sINDENT + K.LANGUAGE.getKeyword() + sSP + 
        getLanguageName().getKeywords();
    if (getParameterStyle() != null)
      s = s + sNEW_LINE + sINDENT + K.PARAMETER.getKeyword() + sSP + K.STYLE.getKeyword() + sSP +
        getParameterStyle().getKeywords();
    if (getDeterministic() != null)
      s = s + sNEW_LINE + sINDENT + getDeterministic().getKeywords();
    if (getDataAccess() != null)
      s = s + sNEW_LINE + sINDENT + getDataAccess().getKeywords();
    if (getNullCallClause() != null)
      s = s + sNEW_LINE + sINDENT + getNullCallClause().getKeywords();
    return s;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the routine characteristics from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.RoutineCharacteristicsContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the routine characteristics from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().routineCharacteristics());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize routine characteristics.
   * @param ln LANGUAGE name characteristic (or null).
   * @param ps PARAMETER STYLE characteristic (or null).
   * @param deterministic DETERMINISTIC characteristic (or null).
   * @param da access to SQL data characteristic (or null).
   * @param ncc NULL call reaction (or null).
   */
  public void initialize(
    LanguageName ln,
    ParameterStyle ps,
    Deterministic deterministic,
    DataAccess da,
    NullCallClause ncc)
  {
    _il.enter(ln, ps, deterministic, da, ncc);
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
  public RoutineCharacteristics(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class RoutineCharacteristics */
