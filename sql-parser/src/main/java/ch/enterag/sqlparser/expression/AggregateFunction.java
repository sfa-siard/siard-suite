package ch.enterag.sqlparser.expression;

import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class AggregateFunction
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(AggregateFunction.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class AfVisitor extends EnhancedSqlBaseVisitor<AggregateFunction>
  {
    @Override
    public AggregateFunction visitAggregateFunction(SqlParser.AggregateFunctionContext ctx)
    {
      if (ctx.COUNT() != null)
        setCountFunction(true);
      return visitChildren(ctx);
    }
    @Override
    public AggregateFunction visitSetFunction(SqlParser.SetFunctionContext ctx)
    {
      setSetFunction(getSetFunction(ctx));
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitSetQuantifier(SqlParser.SetQuantifierContext ctx)
    {
      setSetQuantifier(getSetQuantifier(ctx));
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitValueExpression(SqlParser.ValueExpressionContext ctx)
    {
      setValueExpression(getSqlFactory().newValueExpression());
      getValueExpression().parse(ctx);
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitBinarySetFunction(SqlParser.BinarySetFunctionContext ctx)
    {
      setBinarySetFunction(getBinarySetFunction(ctx));
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitDependentVariableExpression(SqlParser.DependentVariableExpressionContext ctx)
    {
      setDependent(getSqlFactory().newNumericValueExpression());
      getDependent().parse(ctx.numericValueExpression());
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitIndependentVariableExpression(SqlParser.IndependentVariableExpressionContext ctx)
    {
      setIndependent(getSqlFactory().newNumericValueExpression());
      getIndependent().parse(ctx.numericValueExpression());
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitRankFunction(SqlParser.RankFunctionContext ctx)
    {
      setRankFunction(getRankFunction(ctx));
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitRankFunctionArgumentList(SqlParser.RankFunctionArgumentListContext ctx)
    {
      for (int i = 0; i < ctx.valueExpression().size(); i++)
      {
        ValueExpression ve = getSqlFactory().newValueExpression();
        ve.parse(ctx.valueExpression(i));
        getRankArguments().add(ve);
      }
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitInverseDistributionFunction(SqlParser.InverseDistributionFunctionContext ctx)
    {
      setInverseDistributionFunction(getInverseDistributionFunction(ctx));
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitNumericValueExpression(SqlParser.NumericValueExpressionContext ctx)
    {
      setNumericValueExpression(getSqlFactory().newNumericValueExpression());
      getNumericValueExpression().parse(ctx);
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitSortSpecificationList(SqlParser.SortSpecificationListContext ctx)
    {
      for (int i = 0; i < ctx.sortSpecification().size(); i++)
      {
        SortSpecification ss = getSqlFactory().newSortSpecification();
        ss.parse(ctx.sortSpecification(i));
        getWithinGroupSortSpecifications().add(ss);
      }
      return AggregateFunction.this;
    }
    @Override
    public AggregateFunction visitFilterClause(SqlParser.FilterClauseContext ctx)
    {
      setFilter(getSqlFactory().newBooleanValueExpression());
      getFilter().parse(ctx.booleanValueExpression());
      return AggregateFunction.this;
    }
  }
  /*==================================================================*/

  private AfVisitor _visitor = new AfVisitor();
  private AfVisitor getVisitor() { return _visitor; }
  
  /* count function */
  private boolean _bCountFunction = false;
  public boolean isCountFunction() { return _bCountFunction; }
  public void setCountFunction(boolean bCountFunction) 
  {
    _bCountFunction = bCountFunction;
    getSqlFactory().setCount(true);
  }
  
  /* set function */
  private SetFunction _sf = null;
  public SetFunction getSetFunction() { return _sf; }
  public void setSetFunction(SetFunction sf) { _sf = sf; }

  private SetQuantifier _sq = null;
  public SetQuantifier getSetQuantifier() { return _sq; }
  public void setSetQuantifier(SetQuantifier sq) { _sq = sq; }
  
  private ValueExpression _ve = null;
  public ValueExpression getValueExpression() { return _ve; }
  public void setValueExpression(ValueExpression ve) { _ve = ve; }
  
  /* binary set function */
  private BinarySetFunction _bsf = null;
  public BinarySetFunction getBinarySetFunction() { return _bsf; }
  public void setBinarySetFunction(BinarySetFunction bsf) { _bsf = bsf; }

  private NumericValueExpression _nveDependent = null;
  public NumericValueExpression getDependent() { return _nveDependent; }
  public void setDependent(NumericValueExpression nveDependent) { _nveDependent = nveDependent; }
  
  private NumericValueExpression _nveIndependent = null;
  public NumericValueExpression getIndependent() { return _nveIndependent; }
  public void setIndependent(NumericValueExpression nveIndependent) { _nveIndependent = nveIndependent; } 

  private RankFunction _rf = null;
  public RankFunction getRankFunction() { return _rf; }
  public void setRankFunction(RankFunction rf) { _rf = rf; }
  
  private List<ValueExpression> _listRankArguments = new ArrayList<ValueExpression>();
  public List<ValueExpression> getRankArguments() { return _listRankArguments; }
  private void setRankArguments(List<ValueExpression> listRankArguments) { _listRankArguments = listRankArguments; }
  
  private InverseDistributionFunction _idf = null;
  public InverseDistributionFunction getInverseDistributionFunction() { return _idf; }
  public void setInverseDistributionFunction(InverseDistributionFunction idf) { _idf = idf; }

  private NumericValueExpression _nve = null;
  public NumericValueExpression getNumericValueExpression() { return _nve; }
  public void setNumericValueExpression(NumericValueExpression nve) { _nve = nve; }
  
  private List<SortSpecification> _listWithinGroupSortSpecifications = new ArrayList<SortSpecification>();
  public List<SortSpecification> getWithinGroupSortSpecifications() { return _listWithinGroupSortSpecifications; }
  private void setWithinGroupSortSpecifications(List<SortSpecification> listWithinGroupSortSpecifications) { _listWithinGroupSortSpecifications = listWithinGroupSortSpecifications; } 

  private BooleanValueExpression _bveFilter = null;
  public BooleanValueExpression getFilter() { return _bveFilter; }
  public void setFilter(BooleanValueExpression bveFilter) { _bveFilter = bveFilter; }
  
  private Object _oValue = null;
  private Object getValue() { return _oValue; }
  private void setValue(Object oValue) { _oValue = oValue; }
  
  private int _iCount = 0;
  private int getCount() { return _iCount; }
  private void setCount(int iCount) { _iCount = iCount; }
  
  /*------------------------------------------------------------------*/
  /** format the aggregate function.
   * @return the SQL string corresponding to the fields of the aggregate function.
   */
  @Override
  public String format()
  {
    String sExpression = "";
    if (isCountFunction())
      sExpression = K.COUNT.getKeyword() + sLEFT_PAREN + sASTERISK + sRIGHT_PAREN;
    else if ((getSetFunction() != null) && (getValueExpression() != null))
    {
      sExpression = getSetFunction().getKeywords() + sLEFT_PAREN;
      if (getSetQuantifier() != null)
        sExpression = sExpression + getSetQuantifier().getKeywords() + sSP;
      sExpression = sExpression + getValueExpression().format() + sRIGHT_PAREN;
    }
    else if ((getBinarySetFunction() != null) && 
             (getDependent() != null) &&
             (getIndependent() != null))
    {
      sExpression = getBinarySetFunction().getKeywords() + sLEFT_PAREN +
        getDependent().format() + sCOMMA + sSP + getIndependent().format() + sRIGHT_PAREN;
    }
    else
    {
      if (getRankFunction() != null)
      {
        sExpression = getRankFunction().getKeywords() + sLEFT_PAREN;
        for (int i = 0; i < getRankArguments().size(); i++)
        {
          if (i > 0)
            sExpression = sExpression + sCOMMA + sSP;
          sExpression = sExpression + getRankArguments().get(i).format();
        }
        sExpression = sExpression + sRIGHT_PAREN;
      }
      else if (getInverseDistributionFunction() != null)
      {
        sExpression = sExpression + getInverseDistributionFunction().getKeywords() + sLEFT_PAREN +
          getNumericValueExpression().format() + sRIGHT_PAREN;
      }
      if (getWithinGroupSortSpecifications().size() > 0)
      {
        sExpression = sExpression + sSP + K.WITHIN.getKeyword() + sSP + 
          K.GROUP.getKeyword() + sLEFT_PAREN + K.ORDER.getKeyword() + sSP + K.BY.getKeyword() + sSP;
        for (int i = 0; i < getWithinGroupSortSpecifications().size(); i++)
        {
          if (i > 0)
            sExpression = sExpression + sCOMMA + sSP;
          sExpression = sExpression + getWithinGroupSortSpecifications().get(i).format();
        }
        sExpression = sExpression + sRIGHT_PAREN;
      }
    }
    if (getFilter() != null)
      sExpression = sExpression + sSP + K.FILTER.getKeyword() + 
        sLEFT_PAREN + K.WHERE.getKeyword() + sSP + getFilter().format() + sRIGHT_PAREN;
    return sExpression;
  } /* format */

  /*------------------------------------------------------------------*/
  /** get data type of this value expression primary from the context 
   * of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (isCountFunction())
    {
      dt = getSqlFactory().newDataType();
      PredefinedType pt = getSqlFactory().newPredefinedType();
      dt.initPredefinedDataType(pt);
      pt.initBigIntType();
    }
    else
    {
      dt = getValueExpression().getDataType(ss);
      PredefinedType pt = dt.getPredefinedType();
      switch(getSetFunction())
      {
        case AVG:
          if (pt != null)
          {
            if ((pt.getType() == PreType.SMALLINT) ||
                (pt.getType() == PreType.INTEGER) ||
                (pt.getType() == PreType.BIGINT))
              pt.initDecimalType(PredefinedType.iUNDEFINED,PredefinedType.iUNDEFINED);
          }
        case SUM:
          if (pt != null)
          {
            if ((pt.getType() == PreType.SMALLINT) ||
                (pt.getType() == PreType.INTEGER) ||
                (pt.getType() == PreType.BIGINT))
              pt.initBigIntType();
          }
          break;
        case MAX:
        case MIN:
          break;
        default:
          throw new IllegalArgumentException("Cannot evaluate set function "+format()+"!");
      }
    }
    return dt;
  } /* getDataType */
  
  private int compare(Object o1, Object o2)
  {
    int iCompare = 0;
    if ((o1 instanceof BigDecimal) && (o2 instanceof Double))
    {
      Double d = (Double)o2;
      o2 = BigDecimal.valueOf(d.doubleValue());
    }
    if ((o1 instanceof Double) && (o2 instanceof BigDecimal))
    {
      Double d = (Double)o1;
      o1 = BigDecimal.valueOf(d.doubleValue());
    }
    if ((o1 instanceof Boolean) && (o2 instanceof Boolean))
    {
      Boolean b1 = (Boolean)o1;
      Boolean b2 = (Boolean)o2;
      iCompare = b1.compareTo(b2);
    }
    else if ((o1 instanceof Double) && (o2 instanceof Double))
    {
      Double d1 = (Double)o1;
      Double d2 = (Double)o2;
      iCompare = d1.compareTo(d2);
    }
    else if ((o1 instanceof BigDecimal) && (o2 instanceof BigDecimal))
    {
      BigDecimal bd1 = (BigDecimal)o1;
      BigDecimal bd2 = (BigDecimal)o2;
      iCompare = bd1.compareTo(bd2);
    }
    else if ((o1 instanceof String) && (o2 instanceof String))
    {
      String s1 = (String)o1;
      String s2 = (String)o2;
      iCompare = s1.compareTo(s2);
    }
    else if ((o1 instanceof Date) && (o2 instanceof Date))
    {
      Date date1 = (Date)o1;
      Date date2 = (Date)o2;
      iCompare = date1.compareTo(date2);
    }
    else if ((o1 instanceof Time) && (o2 instanceof Time))
    {
      Time time1 = (Time)o1;
      Time time2 = (Time)o2;
      iCompare = time1.compareTo(time2);
    }
    else if ((o1 instanceof Timestamp) && (o2 instanceof Timestamp))
    {
      Timestamp ts1 = (Timestamp)o1;
      Timestamp ts2 = (Timestamp)o2;
      iCompare = ts1.compareTo(ts2);
    }
    else if ((o1 instanceof byte[]) && (o2 instanceof byte[]))
    {
      byte[] buf1 = (byte[])o1;
      byte[] buf2 = (byte[])o2;
      for (int i = 0; (iCompare == 0) && (i < buf1.length) && (i < buf2.length); i++)
        iCompare = Byte.compare(buf1[i], buf2[i]);
      if (iCompare == 0)
        iCompare = Integer.compare(buf1.length, buf2.length);
    }
    else
      throw new IllegalArgumentException("Values cannot be compared!");
    return iCompare;
  } /* compare */
  
  private Object evaluateSum(Object o1, Object o2)
  {
    Object oValue = null;
    Double d1 = null;
    Double d2 = null;
    BigDecimal bd1 = null;
    BigDecimal bd2 = null;
    if (o1 instanceof Double)
      d1 = (Double) o1;
    else
      bd1 = (BigDecimal) o1;
    if (o2 instanceof Double)
      d2 = (Double) o2;
    else
      bd2 = (BigDecimal) o2;
    if ((bd1 != null) || (bd2 != null))
    {
      if (bd1 == null)
        bd1 = BigDecimal.valueOf(d1.doubleValue());
      else if (bd2 == null)
        bd2 = BigDecimal.valueOf(d2.doubleValue());
      oValue = bd1.add(bd2);
    }
    else
      oValue = Double.valueOf(d1.doubleValue() + d2.doubleValue());
    return oValue;
  } /* evaluateSum */

  private Object evaluateAverage(Object o1, Object o2, int iCount)
  {
    Object oValue = null;
    Double d1 = null;
    Double d2 = null;
    BigDecimal bd1 = null;
    BigDecimal bd2 = null;
    if (o1 instanceof Double)
      d1 = (Double) o1;
    else
      bd1 = (BigDecimal) o1;
    if (o2 instanceof Double)
      d2 = (Double) o2;
    else
      bd2 = (BigDecimal) o2;
    if ((bd1 != null) || (bd2 != null))
    {
      if (bd1 == null)
        bd1 = BigDecimal.valueOf(d1.doubleValue());
      else if (bd2 == null)
        bd2 = BigDecimal.valueOf(d2.doubleValue());
      BigDecimal bd = bd1.multiply(BigDecimal.valueOf(iCount-1));
      bd = bd.add(bd2);
      oValue = bd.divide(BigDecimal.valueOf(iCount));
    }
    else
      oValue = Double.valueOf((d1.doubleValue()*(iCount-1) + d2.doubleValue())/iCount);
    return oValue;
  } /* evaluateAverage */

  /*------------------------------------------------------------------*/
  /** evaluate this aggregate value.
   * @param ss sql statement.
   * @return value.
   */
  public Object evaluate(SqlStatement ss)
  {
    Object oValue = null;
    setCount(getCount()+1);
    if (isCountFunction())
      oValue = BigDecimal.valueOf(getCount());
    else if ((getSetFunction() != null) && (getValueExpression() != null))
    {
      oValue = getValueExpression().evaluate(ss,true);
      if ((getValue() != null) && (oValue != null))
      {
        switch(getSetFunction())
        {
          case AVG:
            oValue = evaluateAverage(getValue(),oValue,getCount());
            break;
          case SUM:
            oValue = evaluateSum(getValue(),oValue);
            break;
          case MAX:
            if (compare(getValue(),oValue) > 0)
              oValue = getValue();
            break;
          case MIN:
            if (compare(getValue(),oValue) < 0)
              oValue = getValue();
            break;
          default:
            throw new IllegalArgumentException("Cannot evaluate set function "+format()+"!");
        }
      }
    }
    else
      throw new IllegalArgumentException("Cannot evaluate complex aggregate function "+format()+"!");
    setValue(oValue);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset this aggregate value to its initial value.
   * @return previous (final) value.
   */
  public Object reset()
  {
    Object oValue = getValue();
    setValue(null);
    setCount(0);
    return oValue;
  } /* reset */
  
  /*------------------------------------------------------------------*/
  /** parse the aggregate function from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.AggregateFunctionContext ctx)
  {
    getSqlFactory().setAggregates(true);
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the aggregate function from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().aggregateFunction());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize an aggregate function
   * @param bCountFunction true for COUNT(*).
   * @param sf set function.
   * @param sq set quantifier for set function.
   * @param bsf binary set function.
   * @param nveDependent dependent variable of binary set function.
   * @param nveIndependent independent variable of binary set function.
   * @param rf rank function.
   * @param listRankArguments list of arguments of rank function.
   * @param idf inverse distribution function.
   * @param nve numeric argument of inverse distribution function.
   * @param listWithinGroupSortSpecifications sort specifications for WITH GROUP.
   */
  public void initialize(
      boolean bCountFunction,
      SetFunction sf,
      SetQuantifier sq,
      BinarySetFunction bsf,
      NumericValueExpression nveDependent,
      NumericValueExpression nveIndependent,
      RankFunction rf,
      List<ValueExpression> listRankArguments,
      InverseDistributionFunction idf,
      NumericValueExpression nve,
      List<SortSpecification> listWithinGroupSortSpecifications
    )
  {
    _il.enter(String.valueOf(bCountFunction), sf, sq, bsf, 
      nveDependent, nveIndependent, rf, listRankArguments, idf, nve,
      listWithinGroupSortSpecifications);
    setCountFunction(bCountFunction);
    setSetFunction(sf);
    setSetQuantifier(sq);
    setBinarySetFunction(bsf);
    setDependent(nveDependent);
    setIndependent(nveIndependent);
    setRankFunction(rf);
    setRankArguments(listRankArguments);
    setInverseDistributionFunction(idf);
    setNumericValueExpression(nve);
    setWithinGroupSortSpecifications(listWithinGroupSortSpecifications);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public AggregateFunction(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class AggregateFunction */
