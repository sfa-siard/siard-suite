package ch.enterag.sqlparser.dml;

import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class SetClause
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SetClause.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class ScVisitor extends EnhancedSqlBaseVisitor<SetClause>
  {
    @Override
    public SetClause visitSetClause(SqlParser.SetClauseContext ctx)
    {
      SetClause scResult = SetClause.this;
      if ((ctx.updateSource() != null) && (ctx.setTarget().size() == 1))
      {
        setUpdateSource(getSqlFactory().newUpdateSource());
        getUpdateSource().parse(ctx.updateSource());
        setSetTarget(getSqlFactory().newSetTarget());
        getSetTarget().parse(ctx.setTarget(0));
      }
      else if (ctx.assignedRow() != null)
      {
        setAssignedRow(getSqlFactory().newAssignedRow());
        getAssignedRow().parse(ctx.assignedRow());
        scResult = visitChildren(ctx);
      }
      return scResult;
    }
    @Override
    public SetClause visitSetTarget(SqlParser.SetTargetContext ctx)
    {
      SetTarget st = getSqlFactory().newSetTarget();
      st.parse(ctx);
      getSetTargets().add(st);
      return SetClause.this;
    }
  }
  /*==================================================================*/

  private ScVisitor _visitor = new ScVisitor();
  private ScVisitor getVisitor() { return _visitor; }

  private SetTarget _st = null;
  public SetTarget getSetTarget() { return _st; }
  public void setSetTarget(SetTarget st) { _st = st; }
  
  private UpdateSource _us = null;
  public UpdateSource getUpdateSource() { return _us; }
  public void setUpdateSource(UpdateSource us) { _us = us; }
  
  private List<SetTarget> _listSetTargets = new ArrayList<SetTarget>();
  public List<SetTarget> getSetTargets() { return _listSetTargets; }
  private void setSetTargets(List<SetTarget> listSetTargets) { _listSetTargets = listSetTargets; }

  private AssignedRow _as = null;
  public AssignedRow getAssignedRow() { return _as; }
  public void setAssignedRow(AssignedRow as) { _as = as; }

  private String formatSetTargets()
  {
    String s = "";
    for (int i = 0; i < getSetTargets().size(); i++)
    {
      if (i > 0)
        s = s + sCOMMA + sSP;
      s = s + getSetTargets().get(i).format();
    }
    return s;
  }
  /*------------------------------------------------------------------*/
  /** format the set clause
   * @return the SQL string corresponding to the fields of the set clause.
   *
   */
  @Override
  public String format()
  {
    String s = "";
    if ((getSetTarget() != null) && (getUpdateSource() != null))
      s = getSetTarget().format() + sSP + sEQUALS + sSP + getUpdateSource().format();
    else
      s = sLEFT_PAREN + formatSetTargets() + sRIGHT_PAREN + sSP + sEQUALS + sSP + getAssignedRow().format();
    return s;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the set clause from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.SetClauseContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the set clause from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().setClause());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a set clause.
   * @param st set target.
   * @param us update source.
   * @param listSetTargets list of set targets.
   * @param as assigned row.
   */
  public void initialize(
      SetTarget st,
      UpdateSource us,
      List<SetTarget> listSetTargets,
      AssignedRow as
      )
  {
    _il.enter();
    setSetTarget(st);
    setUpdateSource(us);
    setSetTargets(listSetTargets);
    setAssignedRow(as);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public SetClause(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class SetClause */
