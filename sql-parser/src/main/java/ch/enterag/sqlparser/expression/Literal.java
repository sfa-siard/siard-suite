package ch.enterag.sqlparser.expression;

import java.math.*;
import java.sql.*;
import java.text.*;

import ch.enterag.sqlparser.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;

public class Literal
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(Literal.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class LitVisitor extends EnhancedSqlBaseVisitor<Literal>
  {
    @Override
    public Literal visitLiteral(SqlParser.LiteralContext ctx)
    {
      if (ctx.sign() != null)
        setSign(getSign(ctx.sign()));
      return Literal.this;
    }
    @Override 
    public Literal visitGeneralLiteral(SqlParser.GeneralLiteralContext ctx)
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
        else if (ctx.BOOLEAN_LITERAL() != null)
          setBooleanLiteral(SqlLiterals.parseBooleanLiteral(ctx.BOOLEAN_LITERAL().getText()));
      }
      catch (ParseException pe) { throw new IllegalArgumentException("Error visiting general literal!", pe); }
      return Literal.this;
    }
    @Override
    public Literal visitUnsignedNumericLiteral(SqlParser.UnsignedNumericLiteralContext ctx)
    {
      if (ctx.UNSIGNED_APPROXIMATE() != null)
      {
        try { setApproximate(SqlLiterals.parseApproximateLiteral(ctx.getText())); }
        catch (ParseException pe) { throw new IllegalArgumentException("Error visiting approximate numeric literal!", pe); }
      }
      return visitChildren(ctx);
    }
    @Override
    public Literal visitExactNumericLiteral(SqlParser.ExactNumericLiteralContext ctx)
    {
      try { setExact(SqlLiterals.parseExactLiteral(ctx.getText())); }
      catch (ParseException pe) { throw new IllegalArgumentException("Error visiting exact numeric literal!", pe); }
      return Literal.this;
    }
  }
  /*==================================================================*/

  private LitVisitor _visitor = new LitVisitor();
  private LitVisitor getVisitor() { return _visitor; }

  private Sign _sign = null;
  public Sign getSign() { return _sign; }
  public void setSign(Sign sign) { _sign = sign; }
  
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
  
  private BooleanLiteral _bl = null;
  public BooleanLiteral getBoolean() { return _bl; }
  public void setBooleanLiteral(BooleanLiteral bl) { _bl = bl; }
  
  /*------------------------------------------------------------------*/
  /** format the literal
   * @return the SQL string corresponding to the fields of the literal.
   */
  @Override
  public String format()
  {
    String sFormatted = "";
    if (getApproximate() != null)
    {
      if (getSign() != null)
        sFormatted = getSign().getKeywords();
      sFormatted = sFormatted + SqlLiterals.formatApproximateLiteral(getApproximate());
    }
    else if (getExact() != null)
    {
      if (getSign() != null)
        sFormatted = getSign().getKeywords();
      sFormatted = sFormatted + SqlLiterals.formatExactLiteral(getExact());
    }
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
    else if (getBoolean() != null)
      sFormatted = SqlLiterals.formatBooleanLiteral(getBoolean());
    return sFormatted;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the literal from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.LiteralContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the literal from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().literal());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a literal.
   * Only one parameter must be not null.
   * @param sign sign for numeric values.
   * @param dApproximate approximate numeric value.
   * @param bdExact exact numeric value.
   * @param sCharacterString character string value.
   * @param sNationalCharacterString national character string value.
   * @param sBitString bit string value.
   * @param bufBytes bytes value.
   * @param date date value.
   * @param time time value.
   * @param ts timestamp value.
   * @param bl boolean literal value.
   */
  public void initialize(
      Sign sign,
      Double dApproximate,
      BigDecimal bdExact,
      String sCharacterString,
      String sNationalCharacterString,
      String sBitString,
      byte[] bufBytes,
      Date date,
      Time time,
      Timestamp ts,
      BooleanLiteral bl
    )
  {
    _il.enter(sign,dApproximate,bdExact,sCharacterString,sNationalCharacterString,
      sBitString, bufBytes, date, time, ts, bl);
    setSign(sign);
    setApproximate(dApproximate);
    setExact(bdExact);
    setCharacterString(sCharacterString);
    setNationalCharacterString(sNationalCharacterString);
    setBitString(sBitString);
    setBytes(bufBytes);
    setDate(date);
    setTime(time);
    setTimestamp(ts);
    setBooleanLiteral(bl);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public Literal(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class Literal */
