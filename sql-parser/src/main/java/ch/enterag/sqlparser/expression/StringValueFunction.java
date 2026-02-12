package ch.enterag.sqlparser.expression;

import java.text.*;
import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.utils.logging.*;

public class StringValueFunction
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(StringValueFunction.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class SvfVisitor extends EnhancedSqlBaseVisitor<StringValueFunction>
  {
    @Override
    public StringValueFunction visitStringValueFunction(SqlParser.StringValueFunctionContext ctx)
    {
      StringValueFunction svfReturn = StringValueFunction.this;
      setStringFunction(getStringFunction(ctx));
      if (svfReturn.getStringFunction() != null)
      {
        setStringValueExpression(getSqlFactory().newStringValueExpression());
        getStringValueExpression().parse(ctx.stringValueExpression(0));
        if (svfReturn.getStringFunction() == StringFunction.SUBSTRING)
        {
          if (ctx.SIMILAR() != null)
          {
            setPattern(getSqlFactory().newStringValueExpression());
            getPattern().parse(ctx.stringValueExpression(1));
            if (ctx.CHARACTER_STRING_LITERAL() != null)
            {
              try { setEscapeLetter(SqlLiterals.parseStringLiteral(ctx.CHARACTER_STRING_LITERAL().getText())); }
              catch (ParseException pe) { throw new IllegalArgumentException("Error visiting escape letter!", pe); }
            }
          }
          else
          {
            setStartPosition(getSqlFactory().newNumericValueExpression());
            getStartPosition().parse(ctx.startPosition().numericValueExpression());
            setLength(getSqlFactory().newNumericValueExpression());
            getLength().parse(ctx.stringLength().numericValueExpression());
          }
        }
      }
      else
        svfReturn = visitChildren(ctx); 
      return svfReturn;
    }
    @Override
    public StringValueFunction visitValueExpressionPrimary(SqlParser.ValueExpressionPrimaryContext ctx)
    {
      setUdtValueSpecific(getSqlFactory().newValueExpressionPrimary());
      getUdtValueSpecific().parse(ctx);
      return StringValueFunction.this;
    }
  }
  /*==================================================================*/
  
  private SvfVisitor _visitor = new SvfVisitor();
  private SvfVisitor getVisitor() { return _visitor; }
  
  private StringFunction _sf = null;
  public StringFunction getStringFunction() { return _sf; }
  public void setStringFunction(StringFunction sf) { _sf = sf; }
  
  /* first argument for all string functions */
  private StringValueExpression _sve = null;
  public StringValueExpression getStringValueExpression() { return _sve; }
  public void setStringValueExpression(StringValueExpression sve) { _sve = sve; }
  
  /* SUBSTRING */
  private NumericValueExpression _nveStart = null;
  public NumericValueExpression getStartPosition() { return _nveStart; }
  public void setStartPosition(NumericValueExpression nveStart) { _nveStart = nveStart; }
  
  private NumericValueExpression _nveLength = null;
  public NumericValueExpression getLength() { return _nveLength; }
  public void setLength(NumericValueExpression nveLength) { _nveLength = nveLength; }
  
  /* SUBSTRING SIMILAR */
  private StringValueExpression _svePattern = null;
  public StringValueExpression getPattern() { return _svePattern; }
  public void setPattern(StringValueExpression svePattern) { _svePattern = svePattern; }
  
  private String _sEscapeLetter = null;
  public String getEscapeLetter() { return _sEscapeLetter; }
  public void setEscapeLetter(String sEscapeLetter) { _sEscapeLetter = sEscapeLetter; }
  
  /* specific method */
  private ValueExpressionPrimary _vepSpecific;
  public ValueExpressionPrimary getUdtValueSpecific() { return _vepSpecific; }
  public void setUdtValueSpecific(ValueExpressionPrimary vepSpecific) { _vepSpecific = vepSpecific; }
  
  /*------------------------------------------------------------------*/
  /** format the string value function.
   * @return the SQL string corresponding to the fields of the string 
   *   value function.
   */
  @Override
  public String format()
  {
    String sFunction = null;
    if ((getStringFunction() != null) && (getStringValueExpression() != null))
    {
      sFunction = getStringFunction().getKeywords() + sLEFT_PAREN + getStringValueExpression().format();
      if (getStartPosition() != null)
      {
        sFunction = sFunction + sSP + K.FROM.getKeyword() + sSP + getStartPosition().format();
        if (getLength() != null)
          sFunction = sFunction + sSP + K.FOR.getKeyword() + sSP + getLength().format();
      }
      else if (getPattern() != null)
      {
        sFunction = sFunction + sSP + K.SIMILAR.getKeyword() + sSP + getPattern().format();
        if (getEscapeLetter() != null)
          sFunction = sFunction + sSP + K.ESCAPE.getKeyword() + sSP + SqlLiterals.formatStringLiteral(getEscapeLetter());
      }
      sFunction = sFunction + sRIGHT_PAREN;
    }
    else
      sFunction = getUdtValueSpecific().format()+sPERIOD+K.SPECIFICTYPE.getKeyword();
    return sFunction;
  } /* format */

  /*------------------------------------------------------------------*/
  /** return data type of a string value function from the context of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if ((getStringFunction() != null) && (getStringValueExpression() != null))
    {
      DataType dtArgument = getStringValueExpression().getDataType(ss);
      switch (getStringFunction())
      {
        case LOWER:
        case UPPER:
        case TRIM:
        case SUBSTRING: dt = dtArgument; break;
        case NORMALIZE:
          throw new IllegalArgumentException("String function NORMALIZE is not supported for evaluation!");
      }
    }
    else
      throw new IllegalArgumentException("Evaluation of UDT methods not yet supported!");
    return dt;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  /** evaluate a string value function from its components.
   * @return string value.
   */
  private Object evaluate(
    Object oStringValue,
    Object oStartPosition,
    Object oLength)
  {
    Object oValue = null;
    if ((getStringFunction() != null) && (getStringValueExpression() != null))
    {
      String s = null;
      if (oStringValue instanceof String)
        s = (String)oStringValue;
      byte[] buf = null;
      if (oStringValue instanceof byte[])
        buf = (byte[])oStringValue;
      switch (getStringFunction())
      {
        case LOWER:
          if (s != null)
            oValue = s.toLowerCase();
          else
            throw new IllegalArgumentException("LOWER cannot be applid to binary string!");
          break;
        case UPPER:
          if (s != null)
            oValue = s.toUpperCase();
          else
            throw new IllegalArgumentException("UPPER cannot be applid to binary string!");
          break;
        case TRIM: // we do not support the more complex TRIM(LEADING/TRAILING c FROM s)
          if (s != null)
            oValue = s.trim();
          else
            throw new IllegalArgumentException("TRIM cannot be applid to binary string!");
          break;
        case SUBSTRING:
          if (getStartPosition() != null)
          {
            int iStart = getStartPosition().evaluateInteger(oStartPosition)-1;
            int iLength = getLength().evaluateInteger(oLength);
            if (buf != null)
              oValue = Arrays.copyOfRange(buf, iStart, iStart+iLength);
            if (s != null)
              oValue = s.substring(iStart,iStart+iLength);
          }
          else
            throw new IllegalArgumentException("String function SUBSTRING(s SIMILAR p ESCAPE e) is not supported for evaluation!");
          break;
        case NORMALIZE:
          throw new IllegalArgumentException("String function NORMALIZE is not supported for evaluation!");
      }
    }
    else
      throw new IllegalArgumentException("Evaluation of UDT methods not yet supported!");
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate a string value function against the context of a query.
   * @param ss sql statement.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return string value.
   */
  public Object evaluate(SqlStatement ss, boolean bAggregated)
  {
    Object oStringValue = null;
    if (getStringValueExpression() != null)
      oStringValue = getStringValueExpression().evaluate(ss, bAggregated);
    Object oStartPosition = null;
    if (getStartPosition() != null)
      oStartPosition = getStartPosition().evaluate(ss, bAggregated);
    Object oLength = null;
    if (getLength() != null)
      oLength = getLength().evaluate(ss, bAggregated);
    Object oValue = evaluate(oStringValue,oStartPosition,oLength);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a string value function to 
   * their initial value.
   * @param ss sql statement.
   * @return final value before reset.
   */
  public Object resetAggregates(SqlStatement ss)
  {
    Object oStringValue = null;
    if (getStringValueExpression() != null)
      oStringValue = getStringValueExpression().resetAggregates(ss);
    Object oStartPosition = null;
    if (getStartPosition() != null)
      oStartPosition = getStartPosition().resetAggregates(ss);
    Object oLength = null;
    if (getLength() != null)
      oLength = getLength().resetAggregates(ss);
    Object oValue = evaluate(oStringValue,oStartPosition,oLength);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** parse the string value function from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.StringValueFunctionContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the string value function from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().stringValueFunction());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a string value function.
   * @param sf string function (or null).
   * @param sve argument of function (or null).
   * @param nveStart start value of SUBSTRING (or null).
   * @param nveLength length value of SUBSTRING (or null).
   * @param svePattern pattern value of SUBSTRING (or null).
   * @param sEscapeLetter escape letter of pattern (or null).
   * @param vepSpecific SPECIFIC UDT value (or null).
   */
  public void initialize(
    StringFunction sf,
    StringValueExpression sve,
    NumericValueExpression nveStart,
    NumericValueExpression nveLength,
    StringValueExpression svePattern,
    String sEscapeLetter,
    ValueExpressionPrimary vepSpecific)
  {
    _il.enter(sf, sve, nveStart, nveLength, svePattern, sEscapeLetter, vepSpecific);
    setStringFunction(sf);
    setStringValueExpression(sve);
    setStartPosition(nveStart);
    setLength(nveLength);
    setPattern(svePattern);
    setEscapeLetter(sEscapeLetter);
    setUdtValueSpecific(vepSpecific);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public StringValueFunction(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class StringValueFunction */
