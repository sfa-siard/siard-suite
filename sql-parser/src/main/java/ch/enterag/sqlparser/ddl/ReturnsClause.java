package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class ReturnsClause
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(ReturnsClause.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class RcVisitor extends EnhancedSqlBaseVisitor<ReturnsClause>
  {
    @Override
    public ReturnsClause visitReturnsDataType(SqlParser.ReturnsDataTypeContext ctx)
    {
      if (ctx.LOCATOR() != null)
        setAsLocator(true);
      else
      {
        setDataType(getSqlFactory().newDataType());
        getDataType().parse(ctx.dataType());
      }
      return ReturnsClause.this;
    }
    @Override
    public ReturnsClause visitResultCast(SqlParser.ResultCastContext ctx)
    {
      if (ctx.LOCATOR() != null)
        setCastAsLocator(true);
      else
      {
        setCastDataType(getSqlFactory().newDataType());
        getCastDataType().parse(ctx.dataType());
      }
      return ReturnsClause.this;
    }
    @Override
    public ReturnsClause visitTableColumn(SqlParser.TableColumnContext ctx)
    {
      TableColumn tc = getSqlFactory().newTableColumn();
      tc.parse(ctx);
      getTableColumns().add(tc);
      return ReturnsClause.this;
    }
  }
  /*==================================================================*/

  private RcVisitor _visitor = new RcVisitor();
  private RcVisitor getVisitor() { return _visitor; }
  
  private DataType _dt = null;
  public DataType getDataType() { return _dt; }
  public void setDataType(DataType dt) { _dt = dt;  }
  
  private boolean _bAsLocator = false;
  public boolean isAsLocator() { return _bAsLocator; }
  public void setAsLocator(boolean bAsLocator) { _bAsLocator = bAsLocator; }
  
  private DataType _dtCast = null;
  public DataType getCastDataType() { return _dtCast; }
  public void setCastDataType(DataType dtCast) { _dtCast = dtCast;  }
  
  private boolean _bCastAsLocator = false;
  public boolean isCastAsLocator() { return _bCastAsLocator; }
  public void setCastAsLocator(boolean bCastAsLocator) { _bCastAsLocator = bCastAsLocator; }
  
  private List<TableColumn> _listTableColumns = new ArrayList<TableColumn>();
  public List<TableColumn> getTableColumns() { return _listTableColumns; }
  private void setTableColumns(List<TableColumn> listTableColumns) { _listTableColumns = listTableColumns; }
  
  private String formatTableColumns()
  {
    String s = sLEFT_PAREN;
    for (int i = 0; i < getTableColumns().size(); i++)
    {
      if (i > 0)
        s = s + sCOMMA + sSP;
      s = s + getTableColumns().get(i).format();
    }
    s = s + sRIGHT_PAREN;
    return s;
  } /* formatTableColumns */
  
  /*------------------------------------------------------------------*/
  /** format the returns clause.
   * @return the SQL string corresponding to the fields of the returns 
   *   clause.
   */
  @Override
  public String format()
  {
    String s = K.RETURNS.getKeyword();
    if ((getDataType() != null) || (isAsLocator()))
    {
      if (getDataType() != null)
        s = s + sSP + getDataType().format();
      else if (isAsLocator())
        s = s + sSP + K.AS.getKeyword() + sSP + K.LOCATOR.getKeyword();
      if ((getCastDataType() != null) || isCastAsLocator())
      {
        s = s + sSP + K.CAST.getKeyword() + sSP + K.FROM.getKeyword();
        if (getCastDataType() != null)
          s = s + sSP + getCastDataType().format();
        else if (isCastAsLocator())
          s = s + sSP + K.AS.getKeyword() + sSP + K.LOCATOR.getKeyword();
      }
    }
    else if (getTableColumns().size() > 0)
      s = s + K.TABLE.getKeyword() + formatTableColumns();
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the returns clause from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.ReturnsClauseContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the returns clause from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().returnsClause());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a returns clause.
   * @param dt data type of returned value.
   * @param bAsLocator true, if value is to be returned AS LOCATOR.
   * @param dtCast data type for CAST expression.
   * @param bCastAsLocator true, if expression is to be cast AS LOCATOR.
   * @param listTableColumns list of table columns.
   */
  public void initialize(
      DataType dt,
      boolean bAsLocator,
      DataType dtCast,
      boolean bCastAsLocator,
      List<TableColumn> listTableColumns)
  {
    _il.enter();
    setDataType(dt);
    setAsLocator(bAsLocator);
    setCastDataType(dtCast);
    setCastAsLocator(bCastAsLocator);
    setTableColumns(listTableColumns);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public ReturnsClause(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class ReturnsClause */
