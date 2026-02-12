package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class CastSpecification
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CastSpecification.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CeVisitor extends EnhancedSqlBaseVisitor<CastSpecification>
  {
    @Override
    public CastSpecification visitCastOperand(SqlParser.CastOperandContext ctx)
    {
      if (ctx.NULL() != null)
        setNullCast(true);
      else if (ctx.ARRAY() != null)
        setEmptyArrayCast(true);
      else if (ctx.MULTISET() != null)
        setEmptyMultisetCast(true);
      else if (ctx.valueExpression() != null)
      {
        setCastOperand(getSqlFactory().newValueExpression());
        getCastOperand().parse(ctx.valueExpression());
      }
      return CastSpecification.this;
    }
    @Override
    public CastSpecification visitDataType(SqlParser.DataTypeContext ctx)
    {
      setDataType(getSqlFactory().newDataType());
      getDataType().parse(ctx);
      return CastSpecification.this;
    }
  }
  /*==================================================================*/
  
  private CeVisitor _visitor = new CeVisitor();
  private CeVisitor getVisitor() { return _visitor; }

  private ValueExpression _veCastOperand = null;
  public ValueExpression getCastOperand() { return _veCastOperand; }
  public void setCastOperand(ValueExpression veCastOperand) { _veCastOperand = veCastOperand; }
  
  private boolean _bNullCast = false;
  public boolean isNullCast() { return _bNullCast; }
  public void setNullCast(boolean bNullCast) { _bNullCast = bNullCast; }
  
  private boolean _bEmptyArrayCast = false;
  public boolean isEmptyArrayCast() { return _bEmptyArrayCast; }
  public void setEmptyArrayCast(boolean bEmptyArrayCast) { _bEmptyArrayCast = bEmptyArrayCast; }

  private boolean _bEmptyMultisetCast = false;
  public boolean isEmptyMultisetCast() { return _bEmptyMultisetCast; }
  public void setEmptyMultisetCast(boolean bEmptyMultisetCast) { _bEmptyMultisetCast = bEmptyMultisetCast; }
  
  private DataType _dt = null;
  public DataType getDataType() { return _dt; }
  public void setDataType(DataType dt) { _dt = dt; }
  
  /*------------------------------------------------------------------*/
  /** format the cast specification.
   * @return the SQL string corresponding to the fields of the cast specification.
   */
  @Override
  public String format()
  {
    String sSpecification = K.CAST.getKeyword() + sLEFT_PAREN;
    if (isNullCast())
      sSpecification = sSpecification + K.NULL.getKeyword();
    else if (isEmptyArrayCast())
      sSpecification = sSpecification + K.ARRAY.getKeyword() + sLEFT_BRACKET + sRIGHT_BRACKET;
    else if (isEmptyMultisetCast())
      sSpecification = sSpecification + K.MULTISET.getKeyword() + sLEFT_BRACKET + sRIGHT_BRACKET;
    else
      sSpecification = sSpecification + getCastOperand().format();
    sSpecification = sSpecification + sSP + K.AS.getKeyword() + sSP + 
      getDataType().format() + sRIGHT_PAREN;
    return sSpecification;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the cast specification from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.CastSpecificationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the cast specification from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().castSpecification());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a cast specification.
   * @param veCastOperand cast operand.
   * @param bNullCast true, if NULL as cast operand.
   * @param bEmptyArrayCast true, if ARRAY[] as cast operand.
   * @param bEmptyMultisetCast ture, if MULTISET[] as cast operand.
   * @param dt data type for cast.
   */
  public void initialize(
    ValueExpression veCastOperand,
    boolean bNullCast,
    boolean bEmptyArrayCast,
    boolean bEmptyMultisetCast,
    DataType dt)
  {
    _il.enter();
    setCastOperand(veCastOperand);
    setNullCast(bNullCast);
    setEmptyArrayCast(bEmptyArrayCast);
    setEmptyMultisetCast(bEmptyMultisetCast);
    setDataType(dt);
    _il.exit();
  } /* initalize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CastSpecification(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CastSpecification */
