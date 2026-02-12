package ch.enterag.sqlparser.dml;

import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class SetTarget
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SetTarget.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class StVisitor extends EnhancedSqlBaseVisitor<SetTarget>
  {
    @Override
    public SetTarget visitSetTarget(SqlParser.SetTargetContext ctx)
    {
      SetTarget stResult = SetTarget.this;
      if ((ctx.methodName() == null) && (ctx.updateTarget().size() == 1))
      {
        setUpdateTarget(getSqlFactory().newUpdateTarget());
        getUpdateTarget().parse(ctx.updateTarget(0));
      }
      else
      {
        setMethodName(ctx.methodName(),getMethodName());
        stResult = visitChildren(ctx);
      }
      return stResult;
    }
    @Override
    public SetTarget visitUpdateTarget(SqlParser.UpdateTargetContext ctx)
    {
      UpdateTarget ut = getSqlFactory().newUpdateTarget();
      ut.parse(ctx);
      getUpdateTargets().add(ut);
      return SetTarget.this;
    }
  }
  /*==================================================================*/

  private StVisitor _visitor = new StVisitor();
  private StVisitor getVisitor() { return _visitor; }

  private UpdateTarget _ut = null;
  public UpdateTarget getUpdateTarget() { return _ut; }
  private void setUpdateTarget(UpdateTarget ut) { _ut = ut; }
  
  private List<UpdateTarget> _listUpdateTargets = new ArrayList<UpdateTarget>();
  public List<UpdateTarget> getUpdateTargets() { return _listUpdateTargets; }
  private void setUpdateTargets(List<UpdateTarget> listUpdateTargets) { _listUpdateTargets = listUpdateTargets; }

  private Identifier _idMethodName = new Identifier();
  public Identifier getMethodName() { return _idMethodName; }
  public void setMethodName(Identifier idMethodName) { _idMethodName = idMethodName; }
  
  /*------------------------------------------------------------------*/
  /** format the set target.
   * @return the SQL string corresponding to the fields of the set target.
   */
  @Override
  public String format()
  {
    String s = "";
    if (getUpdateTarget() != null)
      s = getUpdateTarget().format();
    else
    {
      
      for (int i = 0; i < getUpdateTargets().size(); i++)
        s = s + getUpdateTargets().get(i).format() + sPERIOD;
      s = s + getMethodName();
    }
    return s;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the set target from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.SetTargetContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the set target from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().setTarget());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a set target.
   * @param ut update target.
   * @param listUpdateTargets list of update targets.
   * @param idMethodName method name.
   */
  public void initialize(
      UpdateTarget ut,
      List<UpdateTarget> listUpdateTargets,
      Identifier idMethodName)
  {
    _il.enter();
    setUpdateTarget(ut);
    setUpdateTargets(listUpdateTargets);
    setMethodName(idMethodName);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public SetTarget(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class SetTarget */
