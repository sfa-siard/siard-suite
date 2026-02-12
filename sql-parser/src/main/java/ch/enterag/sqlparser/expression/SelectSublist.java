package ch.enterag.sqlparser.expression;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class SelectSublist
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SelectSublist.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class SsVisitor extends EnhancedSqlBaseVisitor<SelectSublist>
  {
    @Override
    public SelectSublist visitSelectSublist(SqlParser.SelectSublistContext ctx)
    {
      if (ctx.ASTERISK() != null)
        setAsterisk(true);
      for (int i = 0; i < ctx.columnName().size(); i++)
      {
        Identifier idColumnName = new Identifier();
        setColumnName(ctx.columnName(i),idColumnName);
        getColumnNames().add(idColumnName);
      }
      if (ctx.valueExpression() != null)
      {
        setValueExpression(getSqlFactory().newValueExpression());
        getValueExpression().parse(ctx.valueExpression());
      }
      if (ctx.identifierChain() != null)
        setIdChain(ctx.identifierChain(),getAsteriskQualifier());
      return SelectSublist.this;
    }
  }
  /*==================================================================*/
  
  private SsVisitor _visitor = new SsVisitor();
  private SsVisitor getVisitor() { return _visitor; }
  
  private ValueExpression _ve = null;
  public ValueExpression getValueExpression() { return _ve; }
  public void setValueExpression(ValueExpression ve) { _ve = ve; }

  private IdChain _icAsteriskQualifier = new IdChain();
  public  IdChain getAsteriskQualifier() { return _icAsteriskQualifier; }
  private void setAsteriskQualifier(IdChain icAsteriskQualifier) { _icAsteriskQualifier = icAsteriskQualifier; }
  
  private boolean _bAsterisk = false;
  public boolean isAsterisk() { return _bAsterisk; }
  public void setAsterisk(boolean bAsterisk) { _bAsterisk = bAsterisk; }
  
  private List<Identifier> _listColumnNames = new ArrayList<Identifier>();
  public List<Identifier> getColumnNames() { return _listColumnNames; }
  private void setColumnNames(List<Identifier> listColumnNames) { _listColumnNames = listColumnNames; }
  
  /*------------------------------------------------------------------*/
  /** format the select sublist.
   * @return the SQL string corresponding to the fields of the select sublist.
   */
  @Override
  public String format()
  {
    String s = "";
    if (isAsterisk())
    {
      if (getAsteriskQualifier().isSet())
        s = getAsteriskQualifier().format() + sPERIOD + sASTERISK;
      else if (getValueExpression() != null)
      {
        s = getValueExpression().format() + sPERIOD + sASTERISK;
        if (getColumnNames().size() > 0)
        {
          s = s + sSP + K.AS.getKeyword() + sSP + sLEFT_PAREN;
          for (int i = 0; i < getColumnNames().size(); i++)
          {
            if (i > 0)
              s = s + sCOMMA + sSP;
            s = s + getColumnNames().get(i).format();
          }
          s = s + sRIGHT_PAREN;
        }
      }
    }
    else
    {
      s = getValueExpression().format();
      if (getColumnNames().size() > 0)
        s = s + sSP + K.AS.getKeyword() + sSP + getColumnNames().get(0).format();
    }
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** return the data type of a select sublist from the context of a query.
   * @param ss sql statement.
   * @return data type.
   */
  public DataType getDataType(SqlStatement ss)
  {
    DataType dt = null;
    if (!isAsterisk())
      dt = getValueExpression().getDataType(ss);
    else
      throw new IllegalArgumentException("Qualified asterisk is not supported for evaluation!");
    return dt;
  } /* getDataType */

  /*------------------------------------------------------------------*/
  /** evaluate a select sublist from its components.
   * @return value.
   */
  private Object evaluate(Object oExpressionValue)
  {
    Object oValue = null;
    if (!isAsterisk())
      oValue = oExpressionValue;
    else
      throw new IllegalArgumentException("Qualified asterisk is not supported for evaluation!");
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** evaluate a select sublist against the context of a query.
   * @param ss sql statement.
   * @return value.
   */
  public Object evaluate(SqlStatement ss)
  {
    Object oExpressionValue = null;
    if (getValueExpression() != null)
      oExpressionValue = getValueExpression().evaluate(ss, false);
    Object oValue = evaluate(oExpressionValue);
    return oValue;
  } /* evaluate */
  
  /*------------------------------------------------------------------*/
  /** reset the aggregate values in a select sublist to their initial value.
   * @param ss sql statement.
   * @return final value before reset.
   */
  public Object resetAggregates(SqlStatement ss)
  {
    Object oExpressionValue = null;
    if (getValueExpression() != null)
      oExpressionValue = getValueExpression().resetAggregates(ss);
    Object oValue = evaluate(oExpressionValue);
    return oValue;
  } /* resetAggregates */
  
  /*------------------------------------------------------------------*/
  /** parse the select sublist from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.SelectSublistContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the select sublist from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().selectSublist());
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialize a select sublist.
   * @param ve value expression.
   * @param icAsteriskQualifier an asterisk qualifier (e.g. "T.*").
   * @param bAsterisk a single asterisk (e.g. "*").
   * @param listColumnNames list of column names in select sublist.
   */
  public void initialize(
    ValueExpression ve,
    IdChain icAsteriskQualifier,
    boolean bAsterisk,
    List<Identifier> listColumnNames)
  {
    _il.enter(ve, icAsteriskQualifier, 
      String.valueOf(bAsterisk), listColumnNames);
    setValueExpression(ve);
    setAsteriskQualifier(icAsteriskQualifier);
    setColumnNames(listColumnNames);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public SelectSublist(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class SelectSublist */
