package ch.enterag.sqlparser.expression;

import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class WindowSpecification
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(WindowSpecification.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class WsVisitor extends EnhancedSqlBaseVisitor<WindowSpecification>
  {
    @Override
    public WindowSpecification visitWindowPartitionClause(SqlParser.WindowPartitionClauseContext ctx)
    {
      for (int i = 0; i < ctx.columnReference().size(); i++)
      {
        IdChain idcColumnReference = new IdChain();
        setIdChain(ctx.columnReference(i).identifierChain(),idcColumnReference);
        getPartitionColumns().add(idcColumnReference);
      }
      return WindowSpecification.this;
    }
    @Override
    public WindowSpecification visitSortSpecificationList(SqlParser.SortSpecificationListContext ctx)
    {
      for (int i = 0; i < ctx.sortSpecification().size(); i++)
      {
        SortSpecification ss = getSqlFactory().newSortSpecification();
        ss.parse(ctx.sortSpecification(i));
        getSortSpecifications().add(ss);
      }
      return WindowSpecification.this;
    }
    @Override
    public WindowSpecification visitWindowName(SqlParser.WindowNameContext ctx)
    {
      setIdentifier(ctx.IDENTIFIER(),getWindowName());
      return WindowSpecification.this;
    }
    @Override
    public WindowSpecification visitWindowFrameUnits(SqlParser.WindowFrameUnitsContext ctx)
    {
      setWindowFrameUnits(getWindowFrameUnits(ctx));
      return WindowSpecification.this;
    }
    @Override
    public WindowSpecification visitWindowFrameStart(SqlParser.WindowFrameStartContext ctx)
    {
      if ((ctx.CURRENT() != null) && (ctx.ROW() != null))
        setCurrentRow(true);
      else
      {
        if (ctx.UNBOUNDED() != null)
          setUnbounded(true);
      }
      return visitChildren(ctx);
    }
    @Override
    public WindowSpecification visitUnsignedValueSpecification(SqlParser.UnsignedValueSpecificationContext ctx)
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
      return WindowSpecification.this;
    }
    @Override
    public WindowSpecification visitWindowFrameBetween(SqlParser.WindowFrameBetweenContext ctx)
    {
      setBound1(getSqlFactory().newWindowFrameBound());
      getBound1().parse(ctx.windowFrameBound1().windowFrameBound());
      setBound2(getSqlFactory().newWindowFrameBound());
      getBound2().parse(ctx.windowFrameBound2().windowFrameBound());
      return WindowSpecification.this;
    }
    @Override
    public WindowSpecification visitWindowFrameExclusion(SqlParser.WindowFrameExclusionContext ctx)
    {
      setWindowFrameExclusion(getWindowFrameExclusion(ctx));
      return WindowSpecification.this;
    }
  }
  /*==================================================================*/
  
  private WsVisitor _visitor = new WsVisitor();
  private WsVisitor getVisitor() { return _visitor; }

  private Identifier _idWindowName = new Identifier();
  public Identifier getWindowName() { return _idWindowName; }
  private void setWindowName(Identifier idWindowName) { _idWindowName = idWindowName; }
  
  private List<IdChain> _listPartitionColumns = new ArrayList<IdChain>();
  public List<IdChain> getPartitionColumns() { return _listPartitionColumns; }
  private void setPartitionColumns(List<IdChain> listPartition) { _listPartitionColumns = listPartition; }
  
  private List<SortSpecification> _listSortSpecifications = new ArrayList<SortSpecification>();
  public List<SortSpecification> getSortSpecifications() { return _listSortSpecifications; }
  private void setSortSpecifications(List<SortSpecification> listSortSpecifications) { _listSortSpecifications = listSortSpecifications; }
  
  private WindowFrameUnits _wfu = null;
  public WindowFrameUnits getWindowFrameUnits() { return _wfu; }
  public void setWindowFrameUnits(WindowFrameUnits wfu) { _wfu = wfu; }
  
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
  
  /* BETWEEN */
  private WindowFrameBound _wfb1 = null;
  public WindowFrameBound getBound1() { return _wfb1; }
  public void setBound1(WindowFrameBound wfb1) { _wfb1 = wfb1; }
  
  private WindowFrameBound _wfb2 = null;
  public WindowFrameBound getBound2() { return _wfb2; }
  public void setBound2(WindowFrameBound wfb2) { _wfb2 = wfb2; }

  private WindowFrameExclusion _wfe = null;
  public WindowFrameExclusion getWindowFrameExclusion() { return _wfe; }
  public void setWindowFrameExclusion(WindowFrameExclusion wfe) { _wfe = wfe; }
  
  /*------------------------------------------------------------------*/
  /** format the window specification.
   * @return the SQL string corresponding to the fields of the window specification.
   */
  @Override
  public String format()
  {
    String sSpecification = "";
    if (getWindowName() != null)
      sSpecification = sSpecification + getWindowName().format();
    if (getPartitionColumns().size() > 0)
    {
      sSpecification = sSpecification + sSP + K.PARTITION.getKeyword() + sSP + K.BY.getKeyword();
      for (int i = 0; i < getPartitionColumns().size(); i++)
      {
        if (i > 0)
          sSpecification = sSpecification + sCOMMA;
        sSpecification = sSpecification + sSP + getPartitionColumns().get(i).format();
      }
    }
    if (getSortSpecifications().size() > 0)
    {
      sSpecification = sSpecification + sSP + K.ORDER.getKeyword() + sSP + K.BY.getKeyword();
      for (int i = 0; i < getSortSpecifications().size(); i++)
      {
        if (i > 0)
          sSpecification = sSpecification + sCOMMA;
        sSpecification = sSpecification + sSP + getSortSpecifications().get(i).format();
      }
    }
    if (getWindowFrameUnits() != null)
    {
      sSpecification = sSpecification + sSP + getWindowFrameUnits().getKeywords();
      if (isUnbounded() || (getUnsignedLiteral() != null) || (getGeneralValueSpecification() != null))
      {
        if (isUnbounded())
          sSpecification = sSpecification + sSP + K.UNBOUNDED.getKeyword();
        else if (getUnsignedLiteral() != null)
          sSpecification = sSpecification + sSP + getUnsignedLiteral().format();
        else if (getGeneralValueSpecification() != null)
          sSpecification = sSpecification + sSP + getGeneralValueSpecification().format();
        sSpecification = sSpecification + sSP + WindowFrameOrder.PRECEDING.getKeywords();
      }
      else if ((getBound1() != null) && (getBound2() != null))
      {
        sSpecification = sSpecification + sSP + K.BETWEEN.getKeyword() + sSP +
            getBound1().format() + sSP + K.AND.getKeyword() + sSP + getBound2().format();
      }
      if (getWindowFrameExclusion() != null)
        sSpecification = sSpecification + sSP + getWindowFrameExclusion().getKeywords(); 
    }
    return sSpecification;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the window specification from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.WindowSpecificationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the window specification from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().windowSpecification());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a window specification
   * @param idWindowName window name (not null!).
   * @param listPartitionColumns PARTITION BY column references (not null!)
   * @param listSortSpecifications ORDER BY sort specifications (not null!)
   * @param wfu window frame units.
   * @param bCurrentRow true, for CURRENT ROW.
   * @param bUnbounded true, for UNBOUNDED.
   * @param ul unsigned value PRECEDING.
   * @param gvs general value specification PRECEDING.
   * @param wfb1 first bound for BETWEEN.
   * @param wfb2 second bound for BETWEEN.
   * @param wfe window frame exclusion.
   */
  public void initialize(
    Identifier idWindowName,
    List<IdChain> listPartitionColumns,
    List<SortSpecification> listSortSpecifications,
    WindowFrameUnits wfu,
    boolean bCurrentRow,
    boolean bUnbounded,
    UnsignedLiteral ul,
    GeneralValueSpecification gvs,
    WindowFrameBound wfb1,
    WindowFrameBound wfb2,
    WindowFrameExclusion wfe)
  {
    _il.enter(idWindowName, listPartitionColumns, listSortSpecifications,
      wfu, String.valueOf(bCurrentRow), String.valueOf(bUnbounded),
      ul, gvs, wfb1, wfb2, wfe);
    setWindowName(idWindowName);
    setPartitionColumns(listPartitionColumns);
    setSortSpecifications(listSortSpecifications);
    setWindowFrameUnits(wfu);
    setCurrentRow(bCurrentRow);
    setUnsignedLiteral(ul);
    setGeneralValueSpecification(gvs);
    setBound1(wfb1);
    setBound2(wfb2);
    setWindowFrameExclusion(wfe);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public WindowSpecification(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class WindowSpecification */
