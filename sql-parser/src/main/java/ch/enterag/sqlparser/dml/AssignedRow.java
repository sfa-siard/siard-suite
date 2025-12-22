package ch.enterag.sqlparser.dml;

import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class AssignedRow
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(AssignedRow.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class ArVisitor extends EnhancedSqlBaseVisitor<AssignedRow>
  {
    @Override
    public AssignedRow visitAssignedRow(SqlParser.AssignedRowContext ctx)
    {
      AssignedRow arReturn = AssignedRow.this;
      if ((ctx.LEFT_PAREN() == null) && 
          (ctx.RIGHT_PAREN() == null) && 
          (ctx.updateSource().size() == 1))
      {
        setUpdateSource(getSqlFactory().newUpdateSource());
        getUpdateSource().parse(ctx.updateSource(0));
      }
      else
      {
        if (ctx.ROW() != null)
          setRowExpression(true);
        arReturn = visitChildren(ctx);
      }
      return arReturn;
    }
    @Override
    public AssignedRow visitUpdateSource(SqlParser.UpdateSourceContext ctx)
    {
      UpdateSource us = getSqlFactory().newUpdateSource();
      us.parse(ctx);
      getUpdateSources().add(us);
      return AssignedRow.this;
    }
  }
  /*==================================================================*/

  private ArVisitor _visitor = new ArVisitor();
  private ArVisitor getVisitor() { return _visitor; }

  private List<UpdateSource> _listUpdateSources = new ArrayList<UpdateSource>();
  public List<UpdateSource> getUpdateSources() { return _listUpdateSources; }
  private void setUpdateSources(List<UpdateSource> listUpdateSources) { _listUpdateSources = listUpdateSources; }
  
  private boolean _bRowExpression = false;
  public boolean isRowExpression() { return _bRowExpression; }
  public void setRowExpression(boolean bRowExpression) { _bRowExpression = bRowExpression; }
  
  private UpdateSource _us = null;
  public UpdateSource getUpdateSource() { return _us; }
  public void setUpdateSource(UpdateSource us) { _us = us; }
  
  /*------------------------------------------------------------------*/
  /** format the assigned row.
   * @return the SQL string corresponding to the fields of the assigned
   *   row.
   */
  @Override
  public String format()
  {
    String s = "";
    if (getUpdateSource() != null)
      s = getUpdateSource().format();
    else
    {
      if (isRowExpression())
        s = K.ROW.getKeyword() + sSP;
      s = s + sLEFT_PAREN; 
      for (int i = 0; i < getUpdateSources().size(); i++)
      {
        if (i > 0)
          s = s + sCOMMA + sSP;
        s = s + getUpdateSources().get(i).format();
      }
      s = s + sRIGHT_PAREN;
    }
    return s;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the assigned row from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.AssignedRowContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the assigned row from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().assignedRow());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize an assigned row from update source.
   * @param us update source.
   */
  public void initUpdateSource(
      UpdateSource us)
  {
    _il.enter();
    setUpdateSource(us);
    _il.exit();
  } /* initUpdateSource */
  
  /*------------------------------------------------------------------*/
  /** initialize an assigned row.
   * @param listUpdateSources list of update sources.
   * @param bRowExpression true if assigned row is a row expression.
   * @param us update source. 
   */
  public void initialize(
      List<UpdateSource> listUpdateSources,
      boolean bRowExpression,
      UpdateSource us)
  {
    _il.enter();
    setUpdateSources(listUpdateSources);
    setRowExpression(bRowExpression);
    setUpdateSource(us);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public AssignedRow(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class AssignedRow */
