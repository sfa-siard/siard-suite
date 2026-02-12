package ch.enterag.sqlparser.expression;

import java.util.*;
import java.sql.*;
import java.sql.Date;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class DatetimeValueFunction
extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DatetimeValueFunction.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DvfVisitor extends EnhancedSqlBaseVisitor<DatetimeValueFunction>
  {
    @Override
    public DatetimeValueFunction visitDatetimeValueFunction(SqlParser.DatetimeValueFunctionContext ctx)
    {
      setDatetimeFunction(getDatetimeFunction(ctx));
      return visitChildren(ctx);
    }
    @Override
    public DatetimeValueFunction visitSecondsDecimals(SqlParser.SecondsDecimalsContext ctx)
    {
      setSecondsDecimals(Integer.parseInt(ctx.getText()));
      return DatetimeValueFunction.this;
    }
  }
  /*==================================================================*/
  public static final int iUNDEFINED = -1;
  public static final int iTIME_PRECISION_DEFAULT = 0;
  public static final int iTIMESTAMP_PRECISION_DEFAULT = 6;
  
  private DvfVisitor _visitor = new DvfVisitor();
  private DvfVisitor getVisitor() { return _visitor; }
  
  private DatetimeFunction _df = null;
  public DatetimeFunction getDatetimeFunction() { return _df; }
  public void setDatetimeFunction(DatetimeFunction df) { _df = df; }
  
  private int _iSecondsDecimals = iUNDEFINED;
  public int getSecondsDecimals() { return _iSecondsDecimals; }
  public void setSecondsDecimals(int iSecondsDecimals) { _iSecondsDecimals = iSecondsDecimals; }
  
  /*------------------------------------------------------------------*/
  /** decimals of seconds in parentheses.
   * @param iDefaultDecimals default decimals.
   * @return seconds decimals in parentheses.
   */
  private String formatSecondsDecimals(int iDefaultDecimals)
  {
    String sSecondsDecimals = "";
    if (getSecondsDecimals() != iUNDEFINED)
    {
      if (getSecondsDecimals() != iDefaultDecimals)
        sSecondsDecimals = sSecondsDecimals + sLEFT_PAREN + String.valueOf(getSecondsDecimals()) + sRIGHT_PAREN;
    }
    return sSecondsDecimals;
  } /* formatSecondsPrecision */
  
  /*------------------------------------------------------------------*/
  /** format the datetime value function.
   * @return the SQL string corresponding to the fields of the datetime 
   *   value function.
   */
  @Override
  public String format()
  {
    String sFunction = getDatetimeFunction().getKeywords();
    if (getSecondsDecimals() != iUNDEFINED)
    {
      if ((getDatetimeFunction() == DatetimeFunction.CURRENT_TIME) ||
        (getDatetimeFunction() == DatetimeFunction.LOCALTIME))
        sFunction = sFunction + formatSecondsDecimals(iTIME_PRECISION_DEFAULT);
      else if ((getDatetimeFunction() == DatetimeFunction.CURRENT_TIMESTAMP) ||
        (getDatetimeFunction() == DatetimeFunction.LOCALTIMESTAMP))
        sFunction = sFunction + formatSecondsDecimals(iTIMESTAMP_PRECISION_DEFAULT);
    }
    return sFunction;
  } /* format */

  /*------------------------------------------------------------------*/
  /** return data type of a date/time value function fromt the context 
   * of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = getSqlFactory().newDataType();
    PredefinedType pt = getSqlFactory().newPredefinedType();
    dt.initPredefinedDataType(pt);
    switch(getDatetimeFunction())
    {
      case CURRENT_DATE:
        pt.initDateType();
        break;
      case CURRENT_TIME:
      case LOCALTIME:
        pt.initTimeType(iUNDEFINED, null);
        break;
      case CURRENT_TIMESTAMP:
      case LOCALTIMESTAMP:
        pt.initTimestampType(iUNDEFINED, null);
        break;
    }
    return dt;
  } /* getDataType */
  
  /*------------------------------------------------------------------*/
  /** evaluate a date/time value expression against the context of a query.
   * @param ss sql statement.
   * @return date/time value.
   */
  public Object evaluate(SqlStatement ss)
  {
    Object oValue = null;
    long lNow = (new GregorianCalendar()).getTime().getTime();
    switch(getDatetimeFunction())
    {
      case CURRENT_DATE:
        oValue = new Date(lNow);
        break;
      case CURRENT_TIME:
      case LOCALTIME:
        oValue = new Time(lNow % 1000l*60l*60l*24l);
        break;
      case CURRENT_TIMESTAMP:
      case LOCALTIMESTAMP:
        oValue = new Timestamp(lNow);
        break;
    }
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** parse the datetime value function from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DatetimeValueFunctionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the datetime value function from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().datetimeValueFunction());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a datetime value function.
   * @param df datetime function.
   * @param iSecondsDecimals number od decimals for seconds (or -1 for default).
   */
  public void initialize(
    DatetimeFunction df,
    int iSecondsDecimals)
  {
    _il.enter(df, String.valueOf(iSecondsDecimals));
    setDatetimeFunction(df);
    setSecondsDecimals(iSecondsDecimals);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DatetimeValueFunction(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DatetimeValueFunction */
