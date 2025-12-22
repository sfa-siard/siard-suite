package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class WindowFunction
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(WindowFunction.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class WfVisitor extends EnhancedSqlBaseVisitor<WindowFunction>
  {
    @Override
    public WindowFunction visitWindowFunctionType(SqlParser.WindowFunctionTypeContext ctx)
    {
      if (ctx.ROW_NUMBER() != null)
        setRowNumber(true);
      return visitChildren(ctx);
    }
    @Override
    public WindowFunction visitRankFunction(SqlParser.RankFunctionContext ctx)
    {
      setRankFunction(getRankFunction(ctx));
      return WindowFunction.this;
    }
    @Override
    public WindowFunction visitAggregateFunction(SqlParser.AggregateFunctionContext ctx)
    {
      setAggregateFunction(getSqlFactory().newAggregateFunction());
      getAggregateFunction().parse(ctx);
      return WindowFunction.this;
    }
    @Override
    public WindowFunction visitWindowName(SqlParser.WindowNameContext ctx)
    {
      setIdentifier(ctx.IDENTIFIER(),getWindowName());
      return WindowFunction.this;
    }
    @Override
    public WindowFunction visitWindowSpecification(SqlParser.WindowSpecificationContext ctx)
    {
      setWindowSpecification(getSqlFactory().newWindowSpecification());
      getWindowSpecification().parse(ctx);
      return WindowFunction.this;
    }
  }
  /*==================================================================*/

  private WfVisitor _visitor = new WfVisitor();
  private WfVisitor getVisitor() { return _visitor; }
  
  private RankFunction _rf = null;
  public RankFunction getRankFunction() { return _rf; }
  public void setRankFunction(RankFunction rf) { _rf = rf; }
  
  private boolean _bRowNumber = false;
  public boolean isRowNumber() { return _bRowNumber; }
  public void setRowNumber(boolean bRowNumber) { _bRowNumber = bRowNumber; }
  
  private AggregateFunction _af = null;
  public AggregateFunction getAggregateFunction() { return _af; }
  public void setAggregateFunction(AggregateFunction af) { _af = af; }
  
  private Identifier _idWindowName = new Identifier();
  public Identifier getWindowName() { return _idWindowName; }
  private void setWindowName(Identifier idWindowName) { _idWindowName = idWindowName; }
  
  private WindowSpecification _ws = null;
  public WindowSpecification getWindowSpecification() { return _ws; }
  public void setWindowSpecification(WindowSpecification ws) { _ws = ws; }
  
  /*------------------------------------------------------------------*/
  /** format the window function.
   * @return the SQL string corresponding to the fields of the window function.
   */
  @Override
  public String format()
  {
    String sFunction = null;
    if (getRankFunction() != null)
      sFunction = getRankFunction().getKeywords()+sLEFT_PAREN+sRIGHT_PAREN;
    else if (isRowNumber())
      sFunction = K.ROW_NUMBER.getKeyword()+sLEFT_PAREN+sRIGHT_PAREN;
    else if (getAggregateFunction() != null)
      sFunction = getAggregateFunction().format();
    sFunction = sFunction + sSP + K.OVER.getKeyword();
    if (getWindowName().isSet())
      sFunction = sFunction + sSP + getWindowName().format();
    else if (getWindowSpecification() != null)
      sFunction = sFunction + sSP + sLEFT_PAREN + getWindowSpecification().format() + sRIGHT_PAREN;
    return sFunction;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the window function from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.WindowFunctionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the window function from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().windowFunction());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a window function
   * @param rf rank function.
   * @param bRowNumber true, for ROW_NUMBER.
   * @param af aggregate function.
   * @param idWindowName window name.
   * @param ws window specification.
   */
  public void initialize(
    RankFunction rf,
    boolean bRowNumber,
    AggregateFunction af,
    Identifier idWindowName,
    WindowSpecification ws)
  {
    _il.enter(rf, String.valueOf(bRowNumber),af, idWindowName, ws);
    setRankFunction(rf);
    setRowNumber(bRowNumber);
    setAggregateFunction(af);
    setWindowName(idWindowName);
    setWindowSpecification(ws);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public WindowFunction(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class WindowFunction */
