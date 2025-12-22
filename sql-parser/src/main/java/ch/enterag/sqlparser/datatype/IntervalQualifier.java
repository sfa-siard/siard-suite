package ch.enterag.sqlparser.datatype;

import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.generated.*;

public class IntervalQualifier
extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(IntervalQualifier.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class IqVisitor extends EnhancedSqlBaseVisitor<IntervalQualifier>
  {
    @Override
    public IntervalQualifier visitIntervalQualifier(SqlParser.IntervalQualifierContext ctx)
    {
      if (ctx.YEAR() != null)
        setField(IntervalField.YEAR);
      if (ctx.MONTH() != null)
        setField(IntervalField.MONTH);
      if (ctx.DAY() != null)
        setField(IntervalField.DAY);
      if (ctx.HOUR() != null)
        setField(IntervalField.HOUR);
      if (ctx.MINUTE() != null)
        setField(IntervalField.MINUTE);
      if (ctx.SECOND() != null)
        setField(IntervalField.SECOND);
      return visitChildren(ctx);
    }
    @Override
    public IntervalQualifier visitSecondsDecimals(SqlParser.SecondsDecimalsContext ctx)
    {
      setSecondsDecimals(Integer.parseInt(ctx.getText()));
      return IntervalQualifier.this;
    }
    @Override
    public IntervalQualifier visitPrecision(SqlParser.PrecisionContext ctx)
    {
      setPrecision(Integer.parseInt(ctx.getText()));
      return IntervalQualifier.this;
    }
  }
  /*==================================================================*/
  public static final int iUNDEFINED = -1;
  public static final int iTIME_PRECISION_DEFAULT = 0;
  public static final int iTIMESTAMP_PRECISION_DEFAULT = 6;
  
  private IqVisitor _visitor = new IqVisitor();
  private IqVisitor getVisitor() { return _visitor; }

  private IntervalField _startField = null;
  public IntervalField getStartField() { return _startField; }
  public void setStartField(IntervalField startField) { _startField = startField; }
  
  private IntervalField _endField = null;
  public IntervalField getEndField() { return _endField; }
  public void setEndField(IntervalField endField) { _endField = endField; }
  private void setField(IntervalField field)
  {
    if (getStartField() == null)
      setStartField(field);
    else
      setEndField(field);
  }

  private int _iPrecision = iUNDEFINED;
  public int getPrecision() { return _iPrecision; }
  public void setPrecision(int iPrecision) { _iPrecision = iPrecision; }
  
  private int _iSecondsDecimals = iUNDEFINED;
  public int getSecondsDecimals() { return _iSecondsDecimals; }
  public void setSecondsDecimals(int iSecondsDecimals) { _iSecondsDecimals = iSecondsDecimals; }
  
  /*------------------------------------------------------------------*/
  /** format the interval qualifier.
   * @return the SQL string corresponding to the fields of the interval 
   *   qualifier.
   */
  @Override
  public String format()
  {
    String sQualifier = getStartField().getKeywords();
    if (getPrecision() != iUNDEFINED)
    {
      sQualifier = sQualifier + sLEFT_PAREN + String.valueOf(getPrecision());
      if ((getStartField() == IntervalField.SECOND) && 
          (getEndField() == null) && 
          (getSecondsDecimals() != iUNDEFINED))
        sQualifier = sQualifier + sCOMMA + sSP + String.valueOf(getSecondsDecimals());
      sQualifier = sQualifier + sRIGHT_PAREN;
    }
    if (getEndField() != null)
    {
      sQualifier = sQualifier + sSP + K.TO + sSP + getEndField().getKeywords();
      if ((getEndField() == IntervalField.SECOND) && 
          (getSecondsDecimals() != iUNDEFINED))
        sQualifier = sQualifier + sLEFT_PAREN + String.valueOf(getSecondsDecimals()) + sRIGHT_PAREN;
    }
    return sQualifier;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the interval qualifier from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.IntervalQualifierContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the interval qualifier from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().intervalQualifier());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize an interval qualifier.
   * @param startField start field of interval.
   * @param endField end field of interval.
   * @param iPrecision maximum digits of start field.
   * @param iSecondsDecimals maximum decimals of seconds field.
   */
  public void initialize(
    IntervalField startField,
    IntervalField endField,
    int iPrecision,
    int iSecondsDecimals)
  {
    _il.enter(startField, endField, String.valueOf(iPrecision), String.valueOf(iSecondsDecimals));
    setStartField(startField);
    setEndField(endField);
    setPrecision(iPrecision);
    setSecondsDecimals(iSecondsDecimals);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public IntervalQualifier(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class IntervalQualifier */
