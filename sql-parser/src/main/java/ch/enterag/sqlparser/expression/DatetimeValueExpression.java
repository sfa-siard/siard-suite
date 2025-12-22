package ch.enterag.sqlparser.expression;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.Interval;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class DatetimeValueExpression
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DatetimeValueExpression.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DveVisitor extends EnhancedSqlBaseVisitor<DatetimeValueExpression>
  {
    @Override
    public DatetimeValueExpression visitDatetimeValueExpression(SqlParser.DatetimeValueExpressionContext ctx)
    {
      if ((ctx.datetimeValueExpression() != null) &&
          (ctx.intervalValueExpression() != null))
      {
        if (ctx.MINUS_SIGN() != null)
        {
          setSubtraction(true);
          setDatetimeOperand(getSqlFactory().newDatetimeValueExpression());
          getDatetimeOperand().parse(ctx.datetimeValueExpression());
          setIntervalOperand(getSqlFactory().newIntervalValueExpression());
          getIntervalOperand().parse(ctx.intervalValueExpression());
        }
        else if (ctx.PLUS_SIGN() != null)
        {
          setAddition(true);
          /* it is not really worth the effort to distinguish the order of the operands */
          setIntervalOperand(getSqlFactory().newIntervalValueExpression());
          getIntervalOperand().parse(ctx.intervalValueExpression());
          setDatetimeOperand(getSqlFactory().newDatetimeValueExpression());
          getDatetimeOperand().parse(ctx.datetimeValueExpression());
        }
      }
      else if (ctx.valueExpressionPrimary() != null)
      {
        setValueExpressionPrimary(getSqlFactory().newValueExpressionPrimary());
        getValueExpressionPrimary().parse(ctx.valueExpressionPrimary());
      }
      else if (ctx.datetimeValueFunction() != null)
      {
        setDatetimeValueFunction(getSqlFactory().newDatetimeValueFunction());
        getDatetimeValueFunction().parse(ctx.datetimeValueFunction());
      }
      return visitChildren(ctx);
    }
    @Override
    public DatetimeValueExpression visitTimeZone(SqlParser.TimeZoneContext ctx)
    {
      if (ctx.LOCAL() != null)
        setLocalTimeZone(true);
      return visitChildren(ctx);
    }
    @Override
    public DatetimeValueExpression visitIntervalPrimary(SqlParser.IntervalPrimaryContext ctx)
    {
      if (ctx.valueExpressionPrimary() != null)
      {
        setInterval(getSqlFactory().newValueExpressionPrimary());
        getInterval().parse(ctx.valueExpressionPrimary());
        if (ctx.intervalQualifier() != null)
        {
          setIntervalQualifier(getSqlFactory().newIntervalQualifier());
          getIntervalQualifier().parse(ctx.intervalQualifier());
        }
      }
      else if ((ctx.ABS() != null) && (ctx.intervalValueExpression() != null))
      {
        setAbsArgument(getSqlFactory().newIntervalValueExpression());
        getAbsArgument().parse(ctx.intervalValueExpression());
      }
      return DatetimeValueExpression.this;
    }
  }
  /*==================================================================*/
  
  private DveVisitor _visitor = new DveVisitor();
  private DveVisitor getVisitor() { return _visitor; }

  private boolean _bAddition = false;
  public boolean isAddition() { return _bAddition; }
  public void setAddition(boolean bAddition) { _bAddition = bAddition; }
  
  private boolean _bSubtraction = false;
  public boolean isSubtraction() { return _bSubtraction; }
  public void setSubtraction(boolean bSubtraction) { _bSubtraction = bSubtraction; }
  
  private DatetimeValueExpression _dve = null;
  public DatetimeValueExpression getDatetimeOperand() { return _dve; }
  public void setDatetimeOperand(DatetimeValueExpression dve) { _dve = dve; }
  
  private IntervalValueExpression _ive = null;
  public IntervalValueExpression getIntervalOperand() { return _ive; }
  public void setIntervalOperand(IntervalValueExpression ive) { _ive = ive; }

  /* datetimePrimary */
  private ValueExpressionPrimary _vep = null;
  public ValueExpressionPrimary getValueExpressionPrimary() { return _vep; }
  public void setValueExpressionPrimary(ValueExpressionPrimary vep) { _vep = vep; }
  
  public DatetimeValueFunction _dvf = null;
  public DatetimeValueFunction getDatetimeValueFunction() { return _dvf; }
  public void setDatetimeValueFunction(DatetimeValueFunction dvf) { _dvf = dvf; }

  /* time zone */
  private boolean _bLocalTimeZone = false;
  public boolean isLocalTimeZone() { return _bLocalTimeZone; }
  public void setLocalTimeZone(boolean bLocalTimeZone) { _bLocalTimeZone = bLocalTimeZone; }

  /* interval primary */
  private ValueExpressionPrimary _vepInterval = null;
  public ValueExpressionPrimary getInterval() { return _vepInterval; }
  public void setInterval(ValueExpressionPrimary vepInterval) { _vepInterval = vepInterval; }

  private IntervalQualifier _iq = null;
  public IntervalQualifier getIntervalQualifier() { return _iq; }
  public void setIntervalQualifier(IntervalQualifier iq) { _iq = iq; }

  private IntervalValueExpression _iveAbsArgument = null;
  public IntervalValueExpression getAbsArgument() { return _iveAbsArgument; }
  public void setAbsArgument(IntervalValueExpression iveAbsArgument) { _iveAbsArgument = iveAbsArgument; }

  /*------------------------------------------------------------------*/
  /** format the datetime value expression.
   * @return the SQL string corresponding to the fields of the datetime 
   *   value expression.
   */
  @Override
  public String format()
  {
    String sExpression = null;
    if (isAddition())
    {
      sExpression = getDatetimeOperand().format() + sSP + 
        AdditiveOperator.PLUS_SIGN.getKeywords() + sSP +
        getIntervalOperand().format();
    }
    else if (isSubtraction())
    {
      sExpression = getDatetimeOperand().format() + sSP + 
        AdditiveOperator.MINUS_SIGN.getKeywords() + sSP +
        getIntervalOperand().format();
    }
    else
    {
      if (getValueExpressionPrimary() != null)
        sExpression = getValueExpressionPrimary().format();
      else if (getDatetimeValueFunction() != null)
        sExpression = getDatetimeValueFunction().format();
      if (isLocalTimeZone())
        sExpression = sExpression + sSP + K.AT.getKeyword() + sSP + K.LOCAL.getKeyword();
      if ((getInterval() != null) || (getAbsArgument() != null))
      {
        sExpression = sExpression + sSP + K.AT.getKeyword() + sSP + 
          K.TIME.getKeyword() + sSP + K.ZONE.getKeyword() + sSP;
        if (getInterval() != null)
        {
          sExpression = sExpression + getInterval().format();
          if (getIntervalQualifier() != null)
            sExpression = sExpression + sSP + getIntervalQualifier().format();
        }
        else if (getAbsArgument() != null)
        {
          sExpression = sExpression + K.ABS.getKeyword() + 
            sLEFT_PAREN + getAbsArgument().format() + sRIGHT_PAREN;
        }
      }
    }
    return sExpression;
  } /* format */

  /*------------------------------------------------------------------*/
  /** return the data type of a date/time value expression fromt the 
   * context of a query.
   * @param ss sql statement.
   * @return date/time data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (isAddition() || isSubtraction())
      dt = getDatetimeOperand().getDataType(ss);
    else
    {
      if (getValueExpressionPrimary() != null)
        dt = getValueExpressionPrimary().getDataType(ss);
      else if (getDatetimeValueFunction() != null)
        dt = getDatetimeValueFunction().getDataType(ss);
      // we don't handle the TIME ZONE nonsense
    }
    return dt;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  /** evaluate a date/time value expression from its components.
   * @return date/time value.
   */
  private Object evaluate(
    Object oDatetimeOperand,
    Interval ivIntervalOperand,
    Object oDatetimeValue,
    Object oValuePrimary)
  {
    Object oValue = null;
    if (isAddition() || isSubtraction())
    {
      Object o = oDatetimeOperand;
      Interval iv = ivIntervalOperand;
      if ((o != null) && (iv != null))
      {
        Calendar cal = new GregorianCalendar(); 
        if (o instanceof Date)
          cal.setTime((Date)o);
        else if (o instanceof Time)
          cal.setTime((Time)o);
        else if (o instanceof Timestamp)
          cal.setTime((Timestamp)o);
        else
          throw new IllegalArgumentException("Invalid type of datetime operand!");
        if (isSubtraction())
          iv.setSign(-iv.getSign());
        cal = iv.addTo(cal);
        if (o instanceof Date)
          oValue = new Date(cal.getTimeInMillis());
        else if (o instanceof Time)
          oValue = new Time(cal.getTimeInMillis());
        else if (o instanceof Timestamp)
        {
          Timestamp ts = new Timestamp(cal.getTimeInMillis());
          ts.setNanos(((Timestamp)o).getNanos());
          oValue = ts;
        }
      }
    }
    else
    {
      if (getDatetimeValueFunction() != null)
        oValue = oDatetimeValue;
      else if (getValueExpressionPrimary() != null)
        oValue = oValuePrimary;
      // we don't handle the TIME ZONE nonsense
    }
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate a date/time value expression against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return date/time value.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oDatetimeOperand = null;
    if (getDatetimeOperand() != null)
      oDatetimeOperand = getDatetimeOperand().evaluate(ss, bAggregated);
    Interval ivIntervalOperand = null;
    if (getIntervalOperand() != null)
      ivIntervalOperand = getIntervalOperand().evaluate(ss, bAggregated);
    Object oDatetimeValue = null;
    if (getDatetimeValueFunction() != null)
      oDatetimeValue = getDatetimeValueFunction().evaluate(ss);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().evaluate(ss, bAggregated);
    Object oValue = evaluate(oDatetimeOperand,ivIntervalOperand,oDatetimeValue,oValuePrimary);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a date/time value expression to 
   * their initial value.
   * @param ss sql statement.
   * @return final date/time value before reset.
   */
  public Object resetAggregates(SqlStatement ss)
  {
    Object oDatetimeOperand = null;
    if (getDatetimeOperand() != null)
      oDatetimeOperand = getDatetimeOperand().resetAggregates(ss);
    Interval ivIntervalOperand = null;
    if (getIntervalOperand() != null)
      ivIntervalOperand = getIntervalOperand().resetAggregates(ss);
    Object oDatetimeValue = null;
    if (getDatetimeValueFunction() != null)
      oDatetimeValue = getDatetimeValueFunction().evaluate(ss);
    Object oValuePrimary = null;
    if (getValueExpressionPrimary() != null)
      oValuePrimary = getValueExpressionPrimary().resetAggregates(ss);
    Object oValue = evaluate(oDatetimeOperand,ivIntervalOperand,oDatetimeValue,oValuePrimary);
    return oValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the datetime value expression from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DatetimeValueExpressionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the datetime value expression from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().datetimeValueExpression());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a datetime value expression.
   * @param bAddition true, if the expression is an addition.
   * @param bSubtraction true, if the expression is a subtraction.
   * @param dve datetime value expression operand.
   * @param ive interval value expression operand.
   * @param vep value expression primary.
   * @param dvf datetime value function.
   * @param bLocalTimeZone local time zone.
   * @param vepInterval value expression primary for interval.
   * @param iq interval qualifier.
   * @param iveAbsArgument interval argument for ABS function.
   */
  public void initialize(
    boolean bAddition,
    boolean bSubtraction,
    DatetimeValueExpression dve,
    IntervalValueExpression ive,
    ValueExpressionPrimary vep,
    DatetimeValueFunction dvf,
    boolean bLocalTimeZone,
    ValueExpressionPrimary vepInterval,
    IntervalQualifier iq,
    IntervalValueExpression iveAbsArgument)
  {
    _il.enter(String.valueOf(bAddition),String.valueOf(bSubtraction),
      dve,ive,vep,dvf, String.valueOf(bLocalTimeZone),
      vepInterval,iq,iveAbsArgument);
    setAddition(bAddition);
    setSubtraction(bSubtraction);
    setDatetimeOperand(dve);
    setIntervalOperand(ive);
    setDatetimeValueFunction(dvf);
    setLocalTimeZone(bLocalTimeZone);
    setInterval(vepInterval);
    setIntervalQualifier(iq);
    setAbsArgument(iveAbsArgument);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DatetimeValueExpression(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DatetimeValueExpression */
