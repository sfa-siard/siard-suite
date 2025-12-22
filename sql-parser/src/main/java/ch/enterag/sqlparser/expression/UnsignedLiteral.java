package ch.enterag.sqlparser.expression;

import java.math.*;
import java.sql.*;
import java.text.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class UnsignedLiteral
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(UnsignedLiteral.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class UlVisitor extends EnhancedSqlBaseVisitor<UnsignedLiteral>
  {
    @Override 
    public UnsignedLiteral visitGeneralLiteral(SqlParser.GeneralLiteralContext ctx)
    {
      try
      {
        if (ctx.CHARACTER_STRING_LITERAL() != null)
          setCharacterString(SqlLiterals.parseStringLiteral(ctx.CHARACTER_STRING_LITERAL().getText()));
        else if (ctx.NATIONAL_CHARACTER_STRING_LITERAL() != null)
          setNationalCharacterString(SqlLiterals.parseNationalStringLiteral(ctx.NATIONAL_CHARACTER_STRING_LITERAL().getText()));
        else if (ctx.BIT_STRING_LITERAL() != null)
          setBitString(SqlLiterals.parseBitStringLiteral(ctx.BIT_STRING_LITERAL().getText()));
        else if (ctx.BYTE_STRING_LITERAL() != null)
          setBytes(SqlLiterals.parseBytesLiteral(ctx.BYTE_STRING_LITERAL().getText()));
        else if (ctx.DATE_LITERAL() != null)
          setDate(SqlLiterals.parseDateLiteral(ctx.DATE_LITERAL().getText()));
        else if (ctx.TIME_LITERAL() != null)
          setTime(SqlLiterals.parseTimeLiteral(ctx.TIME_LITERAL().getText()));
        else if (ctx.TIMESTAMP_LITERAL() != null)
          setTimestamp(SqlLiterals.parseTimestampLiteral(ctx.TIMESTAMP_LITERAL().getText()));
        else if (ctx.intervalLiteral() != null)
          setInterval(SqlLiterals.parseInterval(ctx.intervalLiteral().getText()));
        else if (ctx.BOOLEAN_LITERAL() != null)
          setBooleanLiteral(SqlLiterals.parseBooleanLiteral(ctx.BOOLEAN_LITERAL().getText()));
      }
      catch (ParseException pe) { throw new IllegalArgumentException("Error visiting general literal!", pe); }
      return UnsignedLiteral.this;
    }
    @Override
    public UnsignedLiteral visitUnsignedNumericLiteral(SqlParser.UnsignedNumericLiteralContext ctx)
    {
      if (ctx.UNSIGNED_APPROXIMATE() != null)
      {
        try { setApproximate(SqlLiterals.parseApproximateLiteral(ctx.getText())); }
        catch (ParseException pe) { throw new IllegalArgumentException("Error visiting approximate numeric literal!", pe); }
      }
      return visitChildren(ctx);
    }
    @Override
    public UnsignedLiteral visitExactNumericLiteral(SqlParser.ExactNumericLiteralContext ctx)
    {
      try { setExact(SqlLiterals.parseExactLiteral(ctx.getText())); }
      catch (ParseException pe) { throw new IllegalArgumentException("Error visiting exact numeric literal!", pe); }
      return UnsignedLiteral.this;
    }
  }
  /*==================================================================*/

  private UlVisitor _visitor = new UlVisitor();
  private UlVisitor getVisitor() { return _visitor; }

  private Double _d = null;
  public Double getApproximate() { return _d; }
  public void setApproximate(Double d) { _d = d; }

  private BigDecimal _bd = null;
  public BigDecimal getExact() { return _bd; }
  public void setExact(BigDecimal bd) { _bd = bd; }
      
  private String _sCharacterString = null;
  public String getCharacterString() { return _sCharacterString; }
  public void setCharacterString(String sCharacterStringLiteral) { _sCharacterString = sCharacterStringLiteral; }

  private String _sNationalCharacterString = null;
  public String getNationalCharacterString() { return _sNationalCharacterString; }
  public void setNationalCharacterString(String sNationalCharacterString) { _sNationalCharacterString = sNationalCharacterString; }
  
  private String _sBitString = null;
  public String getBitString() { return _sBitString; }
  public void setBitString(String sBitString) { _sBitString = sBitString; }
      
  private byte[] _buf = null;
  public byte[] getBytes() { return _buf; }
  public void setBytes(byte[] buf) { _buf = buf; }
  
  private Date _date = null;
  public Date getDate() { return _date; }
  public void setDate(Date date) { _date = date; }
  
  private Time _time = null;
  public Time getTime() { return _time; }
  public void setTime(Time time) { _time = time; }
  
  private Timestamp _ts = null;
  public Timestamp getTimestamp() { return _ts; }
  public void setTimestamp(Timestamp ts) { _ts = ts; }
  
  private Interval _interval = null;
  public Interval getInterval() { return _interval; }
  public void setInterval(Interval interval) { _interval = interval; }
  
  private BooleanLiteral _bl = null;
  public BooleanLiteral getBoolean() { return _bl; }
  public void setBooleanLiteral(BooleanLiteral bl) { _bl = bl; }
  
  /*------------------------------------------------------------------*/
  /** format the unsigned literal
   * @return the SQL string corresponding to the fields of the unsigned literal.
   */
  @Override
  public String format()
  {
    String sFormatted = null;
    if (getApproximate() != null)
      sFormatted = SqlLiterals.formatApproximateLiteral(getApproximate());
    else if (getExact() != null)
      sFormatted = SqlLiterals.formatExactLiteral(getExact());
    else if (getCharacterString() != null)
      sFormatted = SqlLiterals.formatStringLiteral(getCharacterString());
    else if (getNationalCharacterString() != null)
      sFormatted = SqlLiterals.formatNationalStringLiteral(getNationalCharacterString());
    else if (getBitString() != null)
      sFormatted = SqlLiterals.formatBitStringLiteral(getBitString());
    else if (getBytes() != null)
      sFormatted = SqlLiterals.formatBytesLiteral(getBytes());
    else if (getDate() != null)
      sFormatted = SqlLiterals.formatDateLiteral(getDate());
    else if (getTime() != null)
      sFormatted = SqlLiterals.formatTimeLiteral(getTime());
    else if (getTimestamp() != null)
      sFormatted = SqlLiterals.formatTimestampLiteral(getTimestamp());
    else if (getInterval() != null)
      sFormatted = SqlLiterals.formatIntervalLiteral(getInterval());
    else if (getBoolean() != null)
      sFormatted = SqlLiterals.formatBooleanLiteral(getBoolean());
    return sFormatted;
  } /* format */

  /*------------------------------------------------------------------*/
  /** evaluate the literal value.
   * @return literal value.
   */
  public Object evaluate()
  {
    Object oValue = null;
    if (getApproximate() != null)
      oValue = getApproximate();
    else if (getExact() != null)
      oValue = getExact();
    else if (getCharacterString() != null)
      oValue = getCharacterString();
    else if (getNationalCharacterString() != null)
      oValue = getNationalCharacterString();
    else if (getBitString() != null)
    {
      int iLength = (getBitString().length()+7)/8;
      byte[] bufBinary = new byte[iLength];
      int iIndex = 0;
      for (int iByte = 0; iByte < iLength; iByte++)
      {
        int i = 0;
        for (int iBit = 0; (iIndex < getBitString().length()) && iBit < 8; iBit++)
        {
          i = i >>> 0;
          if (getBitString().charAt(iIndex) != '0')
            i = i & 0x00000080;
        }
        if (i >= 128)
          i = i - 256;
        bufBinary[iByte] = (byte)i;
      }
      oValue = bufBinary;
    }
    else if (getBytes() != null)
      oValue = getBytes();
    else if (getDate() != null)
      oValue = getDate();
    else if (getTime() != null)
      oValue = getTime();
    else if (getTimestamp() != null)
      oValue = getTimestamp();
    else if (getInterval() != null)
      oValue = getInterval();
    else if (getBoolean() != null)
      oValue = getBoolean();
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** return the data type of the literal value.
   * @return data type of literal value.
   */
  public DataType getDataType()
  {
    PredefinedType pt = getSqlFactory().newPredefinedType();
    int iSecondsDecimals = -1;
    long lMillis = -1l;
    if (getApproximate() != null)
      pt.initDoubleType();
    else if (getExact() != null)
    {
      BigDecimal bd = getExact();
      int iScale = bd.scale();
      int iPrecision = bd.toPlainString().length();
      if ((iScale == 0) && (bd.compareTo(BigDecimal.valueOf((long)Short.MAX_VALUE)) <= 0))
        pt.initSmallIntType();
      else if ((iScale == 0) && (bd.compareTo(BigDecimal.valueOf((long)Integer.MAX_VALUE)) <= 0))
        pt.initIntegerType();
      else if ((iScale == 0) && (bd.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) <= 0))
        pt.initBigIntType();
      else
        pt.initDecimalType(iPrecision, iScale);
    }
    else if (getCharacterString() != null)
      pt.initVarCharType(getCharacterString().length());
    else if (getNationalCharacterString() != null)
      pt.initNVarCharType(getNationalCharacterString().length());
    else if (getBitString() != null)
      pt.initBinaryType((getBitString().length()+7)/8);
    else if (getBytes() != null)
      pt.initBinaryType(getBytes().length);
    else if (getDate() != null)
      pt.initDateType();
    else if (getTime() != null)
    {
      lMillis = getTime().getTime();
      iSecondsDecimals = 3;
      for (int i = 10; i < 1000; i = 10*i)
        if ((lMillis % i) == 0)
          iSecondsDecimals--;
      pt.initTimeType(iSecondsDecimals, null);
    }
    else if (getTimestamp() != null)
    {
      int iNanos = getTimestamp().getNanos();
      if (iNanos != 0)
      {
        iSecondsDecimals = 9;
        for (int i = 10; i < 1000000000; i = 10*i)
        {
          if ((iNanos % i) == 0)
            iSecondsDecimals--;
        }
      }
      else 
      {
        lMillis = getTimestamp().getTime();
        iSecondsDecimals = 3;
        for (int i = 10; i < 1000; i = 10*i)
        {
          if ((lMillis % i) == 0)
            iSecondsDecimals--;
        }
      }
      pt.initTimestampType(iSecondsDecimals, null);
    }
    else if (getInterval() != null)
    {
      Interval iv = getInterval();
      IntervalField ifStart = null;
      IntervalField ifEnd = null;
      int iPrecision = 0;
      if (iv.getYears() != 0)
      {
        ifStart = IntervalField.YEAR;
        ifEnd = ifStart;
        iPrecision = String.valueOf(iv.getYears()).length();
        iSecondsDecimals = 0;
        if (iv.getMonths() != 0)
          ifEnd = IntervalField.MONTH;
      }
      else
      {
        if (iv.getDays() != 0)
        {
          ifStart = IntervalField.DAY;
          ifEnd = ifStart;
          if (iv.getHours() != 0)
            ifEnd = IntervalField.HOUR;
          if (iv.getMinutes() != 0)
            ifEnd = IntervalField.MINUTE;
          if ((iv.getSeconds() != 0) || (iv.getNanoSeconds() != 0))
            ifEnd = IntervalField.SECOND;
            
        }
        else if (iv.getHours() != 0)
        {
          ifStart = IntervalField.HOUR;
          ifEnd = ifStart;
          if (iv.getMinutes() != 0)
            ifEnd = IntervalField.MINUTE;
          if ((iv.getSeconds() != 0) || (iv.getNanoSeconds() != 0))
            ifEnd = IntervalField.SECOND;
        }
        else if (iv.getMinutes() != 0)
        {
          ifStart = IntervalField.MINUTE;
          ifEnd = ifStart;
          if ((iv.getSeconds() != 0) || (iv.getNanoSeconds() != 0))
            ifEnd = IntervalField.SECOND;
        }
        else
        {
          ifStart = IntervalField.SECOND;
          ifEnd = ifStart;
          long lNanos = iv.getNanoSeconds();
          iSecondsDecimals = 9;
          for (int i = 10; i < 1000000000; i = 10*i)
          {
            if ((lNanos % i) == 0)
              iSecondsDecimals--;
          }
        }
      }
      pt.initIntervalType(ifStart, ifEnd, iPrecision, iSecondsDecimals);
    }
    else if (getBoolean() != null)
      pt.initBooleanType();
    DataType dt = getSqlFactory().newDataType();
    dt.initPredefinedDataType(pt);
    return dt;
  } /* getDataType */
  
  /*------------------------------------------------------------------*/
  /** parse the unsigned literal from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.UnsignedLiteralContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the unsigned literal from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().unsignedLiteral());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from character string.
   * @param sCharacterString character string value.
   */
  public void initCharacterString(String sCharacterString)
  {
    _il.enter(sCharacterString);
    setCharacterString(sCharacterString);
    _il.exit();
  } /* initCharacterString */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from national character string.
   * @param sNationalCharacterString character string value.
   */
  public void initNationalCharacterString(String sNationalCharacterString)
  {
    _il.enter(sNationalCharacterString);
    setNationalCharacterString(sNationalCharacterString);
    _il.exit();
  } /* initNationalCharacterString */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from bit string.
   * @param sBitString bit string value.
   */
  public void initBitString( String sBitString )
  {
    _il.enter(sBitString);
    setBitString(sBitString);
    _il.exit();
  } /* initBitString */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from bytes.
   * @param bufBytes bytes value.
   */
  public void initBytes( byte[] bufBytes )
  {
    _il.enter(bufBytes);
    setBytes(bufBytes);
    _il.exit();
  } /* initBytes */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from big decimal.
   * @param bdExact exact numeric value.
   */
  public void initBigDecimal(BigDecimal bdExact)
  {
    _il.enter(bdExact);
    setExact(bdExact);
    _il.exit();
  } /* initBigDecimal */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from long
   * @param lExact exact long numeric value.
   */
  public void initLong(Long lExact)
  {
    _il.enter(String.valueOf(lExact));
    BigDecimal bd = BigDecimal.valueOf(lExact);
    setExact(bd);
    _il.exit();
  } /* initLong */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from int
   * @param iExact exact numeric int value.
   */
  public void initInteger(int iExact)
  {
    _il.enter(String.valueOf(iExact));
    BigDecimal bd = BigDecimal.valueOf(iExact);
    setExact(bd);
    _il.exit();
  } /* initInteger */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from double.
   * @param dApproximate approximate numeric value.
   */
  public void initDouble(Double dApproximate)
  {
    _il.enter(dApproximate);
    setApproximate(dApproximate);
    _il.exit();
  } /* initDouble */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from boolean literal.
   * @param bl boolean literal value.
   */
  public void initBoolean(BooleanLiteral bl)
  {
    _il.enter(bl);
    setBooleanLiteral(bl);
    _il.exit();
  } /* initBoolean */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from date.
   * @param date date value.
   */
  public void initDate(Date date)
  {
    _il.enter(date);
    setDate(date);
    _il.exit();
  } /* initDate */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from time.
   * @param time time value.
   */
  public void initTime(Time time)
  {
    _il.enter(time);
    setTime(time);
    _il.exit();
  } /* initTime */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from time stamp.
   * @param ts timestamp value.
   */
  public void initTimestamp(Timestamp ts)
  {
    _il.enter(ts);
    setTimestamp(ts);
    _il.exit();
  } /* initTimestamp */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal from interval.
   * @param iv interval value.
   */
  public void initInterval(Interval iv)
  {
    _il.enter(iv);
    setInterval(iv);
    _il.exit();
  } /* initInterval */

  /*------------------------------------------------------------------*/
  /** initialize an unsigned literal.
   * Only one parameter must be not null.
   * @param dApproximate approximate numeric value.
   * @param bdExact exact numeric value.
   * @param sCharacterString character string value.
   * @param sNationalCharacterString national character string value.
   * @param sBitString bit string value.
   * @param bufBytes bytes value.
   * @param date date value.
   * @param time time value.
   * @param ts timestamp value.
   * @param iv interval value.
   * @param bl boolean value.
   */
  public void initialize(
      Double dApproximate,
      BigDecimal bdExact,
      String sCharacterString,
      String sNationalCharacterString,
      String sBitString,
      byte[] bufBytes,
      Date date,
      Time time,
      Timestamp ts,
      Interval iv,
      BooleanLiteral bl
    )
  {
    _il.enter(dApproximate,bdExact,sCharacterString,sNationalCharacterString,
      sBitString, bufBytes, date, time, ts, bl);
    setApproximate(dApproximate);
    setExact(bdExact);
    setCharacterString(sCharacterString);
    setNationalCharacterString(sNationalCharacterString);
    setBitString(sBitString);
    setBytes(bufBytes);
    setDate(date);
    setTime(time);
    setTimestamp(ts);
    setInterval(iv);
    setBooleanLiteral(bl);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public UnsignedLiteral(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class UnsignedLiteral */
