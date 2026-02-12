package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class SqlArgument
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SqlArgument.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class SaVisitor extends EnhancedSqlBaseVisitor<SqlArgument>
  {
    @Override
    public SqlArgument visitSqlArgument(SqlParser.SqlArgumentContext ctx)
    {
      if (ctx.DEFAULT() != null)
        setDefaultArgument(true);
      else if (ctx.NULL() != null)
        setNullArgument(true);
      else if (ctx.ARRAY() != null)
        setEmptyArrayArgument(true);
      else if (ctx.MULTISET() != null)
        setEmptyMultisetArgument(true);
      else
        visitChildren(ctx);
      return SqlArgument.this;
    }
    @Override
    public SqlArgument visitValueExpression(SqlParser.ValueExpressionContext ctx)
    {
      setValueExpression(getSqlFactory().newValueExpression());
      getValueExpression().parse(ctx);
      return SqlArgument.this;
    }
    @Override
    public SqlArgument visitGeneralizedExpression(SqlParser.GeneralizedExpressionContext ctx)
    {
      setValueExpression(getSqlFactory().newValueExpression());
      getValueExpression().parse(ctx.valueExpression());
      setUdtName(ctx.udtName(),getUdtName());
      return SqlArgument.this;
    }
    @Override
    public SqlArgument visitTargetSpecification(SqlParser.TargetSpecificationContext ctx)
    {
      setTargetSpecification(getSqlFactory().newTargetSpecification());
      getTargetSpecification().parse(ctx);
      return SqlArgument.this;
    }
  }
  /*==================================================================*/
  
  private SaVisitor _visitor = new SaVisitor();
  private SaVisitor getVisitor() { return _visitor; }
  
  private ValueExpression _ve = null;
  public ValueExpression getValueExpression() { return _ve; }
  public void setValueExpression(ValueExpression ve) { _ve = ve; }
  
  private QualifiedId _qiUdtName = new QualifiedId();
  public QualifiedId getUdtName() { return _qiUdtName; }
  public void setUdtName(QualifiedId qiUdtName) { _qiUdtName = qiUdtName; }
  
  private TargetSpecification _ts = null;
  public TargetSpecification getTargetSpecification() { return _ts; }
  public void setTargetSpecification(TargetSpecification ts) { _ts = ts; }
  
  private boolean _bDefaultArgument = false;
  public boolean isDefaultArgument() { return _bDefaultArgument; }
  public void setDefaultArgument(boolean bDefaultArgument) { _bDefaultArgument = bDefaultArgument; }
  
  private boolean _bNullArgument = false;
  public boolean isNullArgument() { return _bNullArgument; }
  public void setNullArgument(boolean bNullArgument) { _bNullArgument = bNullArgument; }
  
  private boolean _bEmptyArrayArgument = false;
  public boolean isEmptyArrayArgument() { return _bEmptyArrayArgument; }
  public void setEmptyArrayArgument(boolean bEmptyArrayArgument) { _bEmptyArrayArgument = bEmptyArrayArgument; }

  private boolean _bEmptyMultisetArgument = false;
  public boolean isEmptyMultisetArgument() { return _bEmptyMultisetArgument; }
  public void setEmptyMultisetArgument(boolean bEmptyMultisetArgument) { _bEmptyMultisetArgument = bEmptyMultisetArgument; }
  
  /*------------------------------------------------------------------*/
  /** format the SQL argument.
   * @return the SQL string corresponding to the fields of the SQL argument.
   */
  @Override
  public String format()
  {
    String s = null;
    if (isDefaultArgument())
      s = K.DEFAULT.getKeyword();
    else if (isNullArgument())
      s = K.NULL.getKeyword();
    else if (isEmptyArrayArgument())
      s = K.ARRAY.getKeyword() + sLEFT_BRACKET + sRIGHT_BRACKET;
    else if (isEmptyMultisetArgument())
      s = K.MULTISET.getKeyword() + sLEFT_BRACKET + sRIGHT_BRACKET;
    else if ((getUdtName().isSet()) && (getValueExpression() != null))
    {
      s = getValueExpression().format() + sSP + 
        K.AS.getKeyword() + sSP + getUdtName().format();
    }
    else if (getTargetSpecification() != null)
      s = getTargetSpecification().format();
    else if (getValueExpression() != null)
      s = getValueExpression().format();
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the SQL argument from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.SqlArgumentContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the SQL argument from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().sqlArgument());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize an SQL argument.
   * @param ve value expression.
   * @param qiUdtName UDT name in generalized expression.
   * @param ts target specification.
   */
  public void initialize(
    ValueExpression ve,
    QualifiedId qiUdtName,
    TargetSpecification ts)
  {
    _il.enter(ve, qiUdtName, ts);
    setValueExpression(ve);
    setUdtName(qiUdtName);
    setTargetSpecification(ts);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public SqlArgument(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class SqlArgument */
