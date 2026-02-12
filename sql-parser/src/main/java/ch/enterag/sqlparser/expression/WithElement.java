package ch.enterag.sqlparser.expression;

import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class WithElement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(WithElement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class WeVisitor extends EnhancedSqlBaseVisitor<WithElement>
  {
    @Override
    public WithElement visitQueryName(SqlParser.QueryNameContext ctx)
    {
      setIdentifier(ctx.IDENTIFIER(),getQueryName());
      return WithElement.this;
    }
    @Override
    public WithElement visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      Identifier idColumnName = new Identifier(); 
      setColumnName(ctx, idColumnName);
      getColumnNames().add(idColumnName);
      return WithElement.this;
    }
    @Override
    public WithElement visitQueryExpression(SqlParser.QueryExpressionContext ctx)
    {
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx);
      return WithElement.this;
    }
    @Override
    public WithElement visitSearchClause(SqlParser.SearchClauseContext ctx)
    {
      if (ctx.DEPTH() != null)
        setSearchFunction(SearchFunction.SEARCH_DEPTH_FIRST_BY);
      else if (ctx.BREADTH() != null)
        setSearchFunction(SearchFunction.SEARCH_BREADTH_FIRST_BY);
      for (int i = 0; i < ctx.sortSpecificationList().sortSpecification().size(); i++)
      {
        SortSpecification ss = getSqlFactory().newSortSpecification();
        ss.parse(ctx.sortSpecificationList().sortSpecification(i));
        getSortSpecifications().add(ss);
      }
      setColumnName(ctx.columnName(),getSearchColumnName());
      return WithElement.this;
    }
    @Override
    public WithElement visitCycleClause(SqlParser.CycleClauseContext ctx)
    {
      if (ctx.CYCLE() != null)
      {
        setCycleFunction(CycleFunction.CYCLE);
        for (int i = 0; i < ctx.columnName().size(); i++)
        {
          Identifier idColumnName = new Identifier();
          setColumnName(ctx.columnName(i), idColumnName);
          getCycleColumnNames().add(idColumnName);
        }
      }
      else if (ctx.SET() != null)
      {
        setCycleFunction(CycleFunction.SET);
        setColumnName(ctx.columnName(0),getCycleColumnName());
        setValueExpression(getSqlFactory().newValueExpression());
        getValueExpression().parse(ctx.valueExpression());
      }
      else if (ctx.DEFAULT() != null)
      {
        setCycleFunction(CycleFunction.DEFAULT);
        setValueExpression(getSqlFactory().newValueExpression());
        getValueExpression().parse(ctx.valueExpression());
      }
      else if (ctx.USING() != null)
      {
        setCycleFunction(CycleFunction.USING);
        setColumnName(ctx.columnName(0),getCycleColumnName());
      }
      return WithElement.this;
    }
  }
  /*==================================================================*/
  
  private WeVisitor _visitor = new WeVisitor();
  private WeVisitor getVisitor() { return _visitor; }
  
  private Identifier _idQueryName = new Identifier();
  public Identifier getQueryName() { return _idQueryName; }
  private void setQueryName(Identifier idQueryName) { _idQueryName = idQueryName; }
  
  private List<Identifier> _listColumnNames = new ArrayList<Identifier>();
  public List<Identifier> getColumnNames() { return _listColumnNames; }
  private void setColumnNames(List<Identifier> listColumnNames) { _listColumnNames = listColumnNames; }
  
  private QueryExpression _qe = null;
  public QueryExpression getQueryExpression() { return _qe; }
  public void setQueryExpression(QueryExpression qe) { _qe = qe; }
  
  private SearchFunction _sf = null;
  public SearchFunction getSearchFunction() { return _sf; }
  public void setSearchFunction(SearchFunction sf) { _sf = sf; }
  
  private List<SortSpecification> _listSortSpecifications = new ArrayList<SortSpecification>();
  public List<SortSpecification> getSortSpecifications() { return _listSortSpecifications; }
  private void setSortSpecifications(List<SortSpecification> listSortSpecifications) { _listSortSpecifications = listSortSpecifications; }
  
  private Identifier _idSearchColumnName = new Identifier();
  public Identifier getSearchColumnName() { return _idSearchColumnName; }
  public void setSearchColumnName(Identifier idSearchColumnName) { _idSearchColumnName = idSearchColumnName; }

  private CycleFunction _cf = null;
  public CycleFunction getCycleFunction() { return _cf; }
  public void setCycleFunction(CycleFunction cf) { _cf = cf; }
  
  private List<Identifier> _listCycleColumnNames = new ArrayList<Identifier>();
  public List<Identifier> getCycleColumnNames() { return _listCycleColumnNames; }
  private void setCycleColumnNames(List<Identifier> listCycleColumnNames) { _listCycleColumnNames = listCycleColumnNames; }
  
  private ValueExpression _ve = null;
  public ValueExpression getValueExpression() { return _ve; }
  public void setValueExpression(ValueExpression ve) { _ve = ve; }
  
  private Identifier _idCycleColumnName = new Identifier();
  public Identifier getCycleColumnName() { return _idCycleColumnName; }
  public void setCycleColumnName(Identifier idCycleColumnName) { _idCycleColumnName = idCycleColumnName; }

  /*------------------------------------------------------------------*/
  /** format the with element.
   * @return the SQL string corresponding to the fields of the with element.
   */
  @Override
  public String format()
  {
    String s = getQueryName().format();
    if (getColumnNames().size() > 0)
    {
      s = s + sLEFT_PAREN;
      for (int i = 0; i < getColumnNames().size(); i++)
      {
        if (i > 0)
          s = s + sCOMMA + sSP;
        s = s + getColumnNames().get(i).format();
      }
      s = s + sRIGHT_PAREN;
    }
    if (getQueryExpression() != null)
    {
      s = s + sSP + K.AS.getKeyword() + sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN;
      if (getSearchFunction() != null)
      {
        s = s + sSP + getSearchFunction().getKeywords();
        for (int i = 0; i < getSortSpecifications().size(); i++)
        {
          if (i > 0)
            s = s + sCOMMA + sSP;
          s = s + getSortSpecifications().get(i).format();
        }
        s = s + sSP + K.SET.getKeyword() + sSP + getSearchColumnName().format(); 
      }
      if (getCycleFunction() != null)
      {
        s = s + sSP + getCycleFunction().getKeywords()+sSP;
        switch (getCycleFunction())
        {
          case CYCLE:
            for (int i = 0; i < getCycleColumnNames().size(); i++)
            {
              if (i > 0)
                s = s + sCOMMA + sSP;
              s = s + getCycleColumnNames().get(i).format();
            }
            break;
          case SET:
            s = s + getCycleColumnName().format() + sSP + K.TO.getKeyword() + sSP + 
              getValueExpression().format(); 
            break;
          case DEFAULT:
            s = s + getValueExpression().format();
            break;
          case USING:
            s = s + getCycleColumnName().format();
            break;
        }
      }
    }
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the with element from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.WithElementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the with element from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().withElement());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /* initialize a with element.
   */
  public void initialize(
      Identifier idQueryName,
      List<Identifier> listColumnNames,
      QueryExpression qe,
      SearchFunction sf,
      List<SortSpecification> listSortSpecifications,
      Identifier idSearchColumnName,
      CycleFunction cf,
      List<Identifier> listCycleColumnNames,
      ValueExpression ve,
      Identifier idCycleColumnName)
  {
    _il.enter();
    setQueryName(idQueryName);
    setColumnNames(listColumnNames);
    setQueryExpression(qe);
    setSearchFunction(sf);
    setSortSpecifications(listSortSpecifications);
    setSearchColumnName(idSearchColumnName);
    setCycleFunction(cf);
    setCycleColumnNames(listCycleColumnNames);
    setValueExpression(ve);
    setCycleColumnName(idCycleColumnName);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public WithElement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class WithElement */
