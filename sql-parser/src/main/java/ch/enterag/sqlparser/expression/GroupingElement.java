package ch.enterag.sqlparser.expression;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class GroupingElement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(GroupingElement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class GeVisitor extends EnhancedSqlBaseVisitor<GroupingElement>
  {
    @Override
    public GroupingElement visitGroupingElement(SqlParser.GroupingElementContext ctx)
    {
      if (ctx.ordinaryGroupingSet() != null)
      {
        for (int i = 0; i < ctx.ordinaryGroupingSet().identifierChain().size(); i++)
        {
          IdChain icGroupingSet = new IdChain();
          setIdChain(ctx.ordinaryGroupingSet().identifierChain(i),icGroupingSet);
          getOrdinaryGroupingSets().add(icGroupingSet);
        }
      }
      if (ctx.ROLLUP() != null)
        setRollup(true);
      else if (ctx.CUBE() != null)
        setCube(true);
      else if ((ctx.GROUPING() != null) && (ctx.SETS() != null))
      {
        setGroupingSets(true);
        for (int i = 0; i < ctx.groupingElement().size(); i++)
        {
          GroupingElement ge = getSqlFactory().newGroupingElement();
          ge.parse(ctx.groupingElement(i));
          getGroupingElements().add(ge);
        }
      }
      return GroupingElement.this;
    }
  }
  /*==================================================================*/
  
  private GeVisitor _visitor = new GeVisitor();
  private GeVisitor getVisitor() { return _visitor; }
  
  private List<IdChain> _listOrdinaryGroupingSets = new ArrayList<IdChain>();
  public List<IdChain> getOrdinaryGroupingSets() { return _listOrdinaryGroupingSets; }
  private void setOrdinaryGroupingSets(List<IdChain> listOrdinaryGroupingSets) { _listOrdinaryGroupingSets = listOrdinaryGroupingSets; }
  
  private boolean _bRollup = false;
  public boolean isRollup() { return _bRollup; }
  public void setRollup(boolean bRollup) { _bRollup = bRollup; }
  
  private boolean _bCube = false;
  public boolean isCube() { return _bCube; }
  public void setCube(boolean bCube) { _bCube = bCube; }

  private boolean _bGroupingSets = false;
  public boolean isGroupingSets() { return _bGroupingSets; }
  public void setGroupingSets(boolean bGroupingSets) { _bGroupingSets = bGroupingSets; }
  
  private List<GroupingElement> _listGroupingElements = new ArrayList<GroupingElement>();
  public List<GroupingElement> getGroupingElements() { return _listGroupingElements; }
  private void setGroupingElements(List<GroupingElement> listGroupingElements) { _listGroupingElements = listGroupingElements; }
  
  /*------------------------------------------------------------------*/
  /** format the grouping element.
   * @return the SQL string corresponding to the fields of the grouping element.
   */
  @Override
  public String format()
  {
    String s = null;
    if (isRollup() || isCube())
    {
      if (isRollup())
        s = K.ROLLUP.getKeyword();
      else if (isCube())
        s = K.CUBE.getKeyword();
      s = s + sLEFT_PAREN;
      for (int i = 0; i < getOrdinaryGroupingSets().size(); i++)
      {
        if (i > 0)
          s = s + sCOMMA + sSP;
        s = s + getOrdinaryGroupingSets().get(i).format();
      }
      s = s + sRIGHT_PAREN;
    }
    else if (isGroupingSets())
    {
      s = K.GROUPING.getKeyword() + sSP + K.SETS.getKeyword() + sLEFT_PAREN;
      for (int i = 0; i < getGroupingElements().size(); i++)
      {
        if (i > 0)
          s = s + sCOMMA + sSP;
        s = s + getGroupingElements().get(i).format();
      }
      s = s + sRIGHT_PAREN;
    }
    else
    {
      if (getOrdinaryGroupingSets().size() == 1)
        s = getOrdinaryGroupingSets().get(0).format();
      else
      {
        s = sLEFT_PAREN;
        for (int i = 0; i < getOrdinaryGroupingSets().size(); i++)
        {
          if (i > 0)
            s = s + sCOMMA + sSP;
          s = s + getOrdinaryGroupingSets().get(i).format();
        }
        s = s + sRIGHT_PAREN;
      }
    }
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the grouping element from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.GroupingElementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the grouping element from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().groupingElement());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a grouping element.
   * @param listOrdinaryGroupingSets list of ordinary grouping sets.
   * @param bRollup ROLLUP expression.
   * @param bCube CUBE expression.
   * @param bGroupingSets GROUPING expression.
   * @param listGroupingElements list of grouping elements.
   */
  public void initialize(
    List<IdChain> listOrdinaryGroupingSets,
    boolean bRollup,
    boolean bCube,
    boolean bGroupingSets,
    List<GroupingElement> listGroupingElements)
  {
    _il.enter();
    setOrdinaryGroupingSets(listOrdinaryGroupingSets);
    setRollup(bRollup);
    setCube(bCube);
    setGroupingElements(listGroupingElements);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public GroupingElement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class GroupingElement */
