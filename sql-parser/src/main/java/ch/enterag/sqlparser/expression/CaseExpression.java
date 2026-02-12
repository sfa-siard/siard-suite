package ch.enterag.sqlparser.expression;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class CaseExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CaseExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CeVisitor extends EnhancedSqlBaseVisitor<CaseExpression>
  {
    @Override
    public CaseExpression visitCaseExpression(SqlParser.CaseExpressionContext ctx)
    {
      if (ctx.NULLIF() != null)
      {
        setNullIf(true);
        setFirstValueExpression(getSqlFactory().newValueExpression());
        getFirstValueExpression().parse(ctx.valueExpression(0));
        setSecondValueExpression(getSqlFactory().newValueExpression());
        getSecondValueExpression().parse(ctx.valueExpression(1));
      }
      else if (ctx.COALESCE() != null)
      {
        setCoalesce(true);
        for (int i = 0; i < ctx.valueExpression().size(); i++)
        {
          ValueExpression ve = getSqlFactory().newValueExpression();
          ve.parse(ctx.valueExpression(i));
          getValueExpressions().add(ve);
        }
      }
      else if (ctx.simpleWhenClause().size() > 0)
      {
        if (ctx.OVERLAPS() != null)
          setOverlaps(true);
        setRowValuePredicand(getSqlFactory().newRowValuePredicand());
        getRowValuePredicand().parse(ctx.rowValuePredicand());
        for (int i = 0; i < ctx.simpleWhenClause().size(); i++)
        {
          WhenOperand wo = getSqlFactory().newWhenOperand();
          wo.parse(ctx.simpleWhenClause(i).whenOperand());
          getWhenOperands().add(wo);
          ValueExpression veResult = getSqlFactory().newValueExpression();
          veResult.parse(ctx.simpleWhenClause(i).result().valueExpression());
          getWhenResults().add(veResult);
        }
        if (ctx.ELSE() != null)
        {
          setElseResult(getSqlFactory().newValueExpression());
          getElseResult().parse(ctx.result().valueExpression());
        }
      }
      else if (ctx.searchedWhenClause().size() > 0)
      {
        for (int i = 0; i < ctx.searchedWhenClause().size(); i++)
        {
          BooleanValueExpression bve = getSqlFactory().newBooleanValueExpression();
          bve.parse(ctx.searchedWhenClause(i).booleanValueExpression());
          getBooleanValueExpressions().add(bve);
          ValueExpression veResult = getSqlFactory().newValueExpression();
          veResult.parse(ctx.searchedWhenClause(i).result().valueExpression());
          getWhenResults().add(veResult);
        }
        if (ctx.ELSE() != null)
        {
          setElseResult(getSqlFactory().newValueExpression());
          getElseResult().parse(ctx.result().valueExpression());
        }
      }
      return CaseExpression.this;
    }
  }
  /*==================================================================*/
  
  private CeVisitor _visitor = new CeVisitor();
  private CeVisitor getVisitor() { return _visitor; }
  
  private boolean _bNullIf = false;
  public boolean isNullIf() { return _bNullIf; }
  public void setNullIf(boolean bNullIf) { _bNullIf = bNullIf; }
  
  private ValueExpression _ve1 = null;
  public ValueExpression getFirstValueExpression() { return _ve1; }
  public void setFirstValueExpression(ValueExpression ve1) { _ve1 = ve1; }

  private ValueExpression _ve2 = null;
  public ValueExpression getSecondValueExpression() { return _ve2; }
  public void setSecondValueExpression(ValueExpression ve2) { _ve2 = ve2; }
  
  private boolean _bCoalesce = false;
  public boolean isCoalesce() { return _bCoalesce; }
  public void setCoalesce(boolean bCoalesce) { _bCoalesce = bCoalesce; }
  
  private List<ValueExpression> _listValueExpressions = new ArrayList<ValueExpression>();
  public List<ValueExpression> getValueExpressions() { return _listValueExpressions; }
  private void setValueExpressions(List<ValueExpression> listValueExpressions) { _listValueExpressions = listValueExpressions; }

  private boolean _bOverlaps = false;
  public boolean isOverlaps() { return _bOverlaps; }
  public void setOverlaps(boolean bOverlaps) { _bOverlaps = bOverlaps; }
  
  private RowValuePredicand _rvp = null;
  public RowValuePredicand getRowValuePredicand() { return _rvp; }
  public void setRowValuePredicand(RowValuePredicand rvp) { _rvp = rvp; }
  
  private List<WhenOperand> _listWhenOperands = new ArrayList<WhenOperand>();
  public List<WhenOperand> getWhenOperands() { return _listWhenOperands; }
  private void setWhenOperands(List<WhenOperand> listWhenOperands) { _listWhenOperands = listWhenOperands; }
  
  private List<BooleanValueExpression> _listBooleanValueExpressions = new ArrayList<BooleanValueExpression>();
  public List<BooleanValueExpression> getBooleanValueExpressions() { return _listBooleanValueExpressions; }
  private void setBooleanValueExpressions(List<BooleanValueExpression> listBooleanValueExpressions) { _listBooleanValueExpressions = listBooleanValueExpressions; }

  private List<ValueExpression> _listWhenResults = new ArrayList<ValueExpression>();
  public List<ValueExpression> getWhenResults() { return _listWhenResults; }
  private void setWhenResults(List<ValueExpression> listWhenResults) { _listWhenResults = listWhenResults; }

  private ValueExpression _veElseResult = null;
  public ValueExpression getElseResult() { return _veElseResult; }
  public void setElseResult(ValueExpression veElseResult) { _veElseResult = veElseResult; }
      
  /*------------------------------------------------------------------*/
  /** format the case expression.
   * @return the SQL string corresponding to the fields of the case expression.
   */
  @Override
  public String format()
  {
    String sExpression = "";
    if (isNullIf())
    {
      sExpression = K.NULLIF.getKeyword() + sLEFT_PAREN +
        getFirstValueExpression().format() + sCOMMA + sSP + 
        getSecondValueExpression().format() + sRIGHT_PAREN;
    }
    else if (isCoalesce())
    {
      sExpression = K.COALESCE.getKeyword() + sLEFT_PAREN;
      for (int i = 0; i < getValueExpressions().size(); i++)
      {
        if (i > 0)
          sExpression = sExpression + sCOMMA + sSP;
        sExpression = sExpression + getValueExpressions().get(i).format();
      }
      sExpression = sExpression + sRIGHT_PAREN;
    }
    else
    {
      sExpression = K.CASE.getKeyword();
      if (getWhenOperands().size() > 0)
      {
        if (isOverlaps())
          sExpression = sExpression + sSP + K.OVERLAPS;
        sExpression = sExpression + sSP + getRowValuePredicand().format();
        for (int i = 0; i < getWhenOperands().size(); i++)
        {
          sExpression = sExpression + sNEW_LINE + sINDENT + K.WHEN.getKeyword() + sSP +
              getWhenOperands().get(i).format() + sSP + K.THEN.getKeyword() + sSP +
              getWhenResults().get(i).format();
        }
      }
      else if (getBooleanValueExpressions().size() > 0)
      {
        for (int i = 0; i < getBooleanValueExpressions().size(); i++)
        {
          sExpression = sExpression + sNEW_LINE + sINDENT + K.WHEN.getKeyword() + sSP +
              getBooleanValueExpressions().get(i).format() + sSP + K.THEN.getKeyword() + sSP +
              getWhenResults().get(i).format();
        }
      }
      if (getElseResult() != null)
        sExpression = sExpression + sNEW_LINE + sINDENT + K.ELSE.getKeyword() + sSP + getElseResult().format();
      sExpression = sExpression + sNEW_LINE + K.END.getKeyword();
    }
    return sExpression;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** get data type of this case expression from the context 
   * of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    List<DataType> listTypes = new ArrayList<DataType>();
    if (isNullIf())
    {
      listTypes.add(getFirstValueExpression().getDataType(ss));
      listTypes.add(getSecondValueExpression().getDataType(ss));
    }
    else if (isCoalesce())
    {
      for (int iValueExpression = 0; iValueExpression < getValueExpressions().size(); iValueExpression++)
        listTypes.add(getValueExpressions().get(iValueExpression).getDataType(ss));
    }
    else
    {
      for (int iWhenResult = 0; iWhenResult < getWhenResults().size(); iWhenResult++)
        listTypes.add(getWhenResults().get(iWhenResult).getDataType(ss));
      if (getElseResult() != null)
        listTypes.add(getElseResult().getDataType(ss));
    }
    dt = listTypes.get(0); // we should probably return the "widest" data type
    return dt;
  } /* getDataType */
  
  /*------------------------------------------------------------------*/
  /** evaluate this case expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return value.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oValue = null;
    if (isNullIf())
    {
      Object o1 = getFirstValueExpression().evaluate(ss,bAggregated);
      Object o2 = getFirstValueExpression().evaluate(ss,bAggregated);
      if (!o1.equals(o2))
        oValue = o1;
    }
    else if (isCoalesce())
    {
      for (int iValueExpression = 0; (oValue == null) && (iValueExpression < getValueExpressions().size()); iValueExpression++)
      {
        Object o = getValueExpressions().get(iValueExpression).evaluate(ss,bAggregated);
        if (o != null)
          oValue = o;
      }
    }
    else
    {
      if (getWhenOperands().size() > 0)
      {
        Object oTestValue = getRowValuePredicand().evaluate(ss, bAggregated);
        for (int iWhenOperand = 0; iWhenOperand < getWhenOperands().size(); iWhenOperand++)
        {
          Object oTestCondition = getWhenOperands().get(iWhenOperand).evaluate(ss,bAggregated);
          if (oTestValue.equals(oTestCondition))
            oValue = getWhenResults().get(iWhenOperand).evaluate(ss, bAggregated);
        }
      }
      else if (getBooleanValueExpressions().size() > 0)
      {
        for (int iBooleanValue = 0; (oValue == null) && (iBooleanValue < getBooleanValueExpressions().size()); iBooleanValue++)
        {
          Object oTest = getBooleanValueExpressions().get(iBooleanValue).evaluate(ss, bAggregated);
          if (Boolean.TRUE.equals((Boolean)oTest))
            oValue = getWhenResults().get(iBooleanValue).evaluate(ss, bAggregated);
        }
      }
      if (oValue == null)
        oValue = getElseResult().evaluate(ss,bAggregated);
    }
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** parse the case expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.CaseExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the case expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().caseExpression());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a case expression.
   * @param bNullIf true, if NULLIF expression.
   * @param ve1 first argument to NULLIF.
   * @param ve2 second argument to NULLIF.
   * @param bCoalesce true, if COALESCE expression.
   * @param listValueExpressions arguments to COALESCE.
   * @param bOverlaps true for OVERLAPS option of simple when clause.
   * @param rvp "switch" value for simple when clause.
   * @param listWhenOperands when operands of simple when clause.
   * @param listBooleanValueExpressions boolean value expressions of searched when clause.
   * @param listWhenResults results of simple or searched when clauses.
   * @param veElseResult ELSE result of simple or searched when clauses.
   */
  public void initialize(
      boolean bNullIf,
      ValueExpression ve1,
      ValueExpression ve2,
      boolean bCoalesce,
      List<ValueExpression> listValueExpressions,
      boolean bOverlaps,
      RowValuePredicand rvp,
      List<WhenOperand> listWhenOperands,
      List<BooleanValueExpression> listBooleanValueExpressions,
      List<ValueExpression> listWhenResults,
      ValueExpression veElseResult)
  {
    _il.enter(String.valueOf(bNullIf), ve1, ve2,
        String.valueOf(bCoalesce), listValueExpressions,
        String.valueOf(bOverlaps), rvp, listWhenOperands,
        listBooleanValueExpressions, listWhenResults, veElseResult);
    setNullIf(bNullIf);
    setFirstValueExpression(ve1);
    setSecondValueExpression(ve2);
    setCoalesce(bCoalesce);
    setValueExpressions(listValueExpressions);
    setOverlaps(bOverlaps);
    setRowValuePredicand(rvp);
    setWhenOperands(listWhenOperands);
    setBooleanValueExpressions(listBooleanValueExpressions);
    setWhenResults(listWhenResults);
    setElseResult(veElseResult);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CaseExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CaseExpression */
