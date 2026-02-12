package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class WindowFrameBound
extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(WindowFrameBound.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class WfbVisitor extends EnhancedSqlBaseVisitor<WindowFrameBound>
  {
    @Override
    public WindowFrameBound visitWindowFrameStart(SqlParser.WindowFrameStartContext ctx)
    {
      if ((ctx.CURRENT() != null) && (ctx.ROW() != null))
        setCurrentRow(true);
      else
      {
        if (ctx.UNBOUNDED() != null)
          setUnbounded(true);
        if (ctx.PRECEDING() != null)
          setWindowFrameOrder(WindowFrameOrder.PRECEDING);
      }
      return visitChildren(ctx);
    }
    @Override 
    public WindowFrameBound visitWindowFrameBound(SqlParser.WindowFrameBoundContext ctx)
    {
      if (ctx.UNBOUNDED() != null)
        setUnbounded(true);
      if (ctx.FOLLOWING() != null)
        setWindowFrameOrder(WindowFrameOrder.FOLLOWING);
      return visitChildren(ctx);
    }
    @Override
    public WindowFrameBound visitUnsignedValueSpecification(SqlParser.UnsignedValueSpecificationContext ctx)
    {
      if (ctx.unsignedLiteral() != null)
      {
        setUnsignedLiteral(getSqlFactory().newUnsignedLiteral());
        getUnsignedLiteral().parse(ctx.unsignedLiteral());
      }
      else if (ctx.generalValueSpecification() != null)
      {
        setGeneralValueSpecification(getSqlFactory().newGeneralValueSpecification());
        getGeneralValueSpecification().parse(ctx.generalValueSpecification());
      }
      return WindowFrameBound.this;
    }
  }
  /*==================================================================*/

  private WfbVisitor _visitor = new WfbVisitor();
  private WfbVisitor getVisitor() { return _visitor; }

  private boolean _bCurrentRow = false;
  public boolean isCurrentRow() { return _bCurrentRow; }
  public void setCurrentRow(boolean bCurrentRow) { _bCurrentRow = bCurrentRow; }
  
  private boolean _bUnbounded = false;
  public boolean isUnbounded() { return _bUnbounded; }
  public void setUnbounded(boolean bUnbounded) { _bUnbounded = bUnbounded; }
  
  /* unsigned value is either unsigned literal or general value */
  private UnsignedLiteral _ul = null;
  public UnsignedLiteral getUnsignedLiteral() { return _ul; }
  public void setUnsignedLiteral(UnsignedLiteral ul) { _ul = ul; }
  
  private GeneralValueSpecification _gvs = null;
  public GeneralValueSpecification getGeneralValueSpecification() { return _gvs; }
  public void setGeneralValueSpecification(GeneralValueSpecification gvs) { _gvs = gvs; }
  
  private WindowFrameOrder _wfo = null;
  public WindowFrameOrder getWindowFrameOrder() { return _wfo; }
  public void setWindowFrameOrder(WindowFrameOrder wfo) { _wfo = wfo; }
  
  /*------------------------------------------------------------------*/
  /** format the window frame bound.
   * @return the SQL string corresponding to the fields of the window
   *   frame bound.
   */
  @Override
  public String format()
  {
    String sBound = null;
    if (isCurrentRow())
      sBound = K.CURRENT.getKeyword() + sSP + K.ROW.getKeyword();
    else
    {
      if (isUnbounded())
        sBound = K.UNBOUNDED.getKeyword();
      else if (getUnsignedLiteral() != null)
        sBound = getUnsignedLiteral().format();
      else if (getGeneralValueSpecification() != null)
        sBound = getGeneralValueSpecification().format();
      sBound = sBound + sSP + getWindowFrameOrder().getKeywords();
    }
    return sBound;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the window frame bound from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.WindowFrameBoundContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the window frame bound from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().windowFrameBound());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /* initialize a window frame bound.
   */
  public void initialize(
    boolean bCurrentRow,
    boolean bUnbounded,
    UnsignedLiteral ul,
    GeneralValueSpecification gv,
    WindowFrameOrder wfo)
  {
    _il.enter(String.valueOf(bCurrentRow), String.valueOf(bUnbounded),
      ul, gv, wfo);
    setCurrentRow(bCurrentRow);
    setUnbounded(bUnbounded);
    setUnsignedLiteral(ul);
    setGeneralValueSpecification(gv);
    setWindowFrameOrder(wfo);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public WindowFrameBound(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class WindowFrameBound */
