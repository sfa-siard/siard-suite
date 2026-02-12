package ch.enterag.sqlparser.expression;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class TablePrimary
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(TablePrimary.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class TpVisitor extends EnhancedSqlBaseVisitor<TablePrimary>
  {
    @Override
    public TablePrimary visitTablePrimary(SqlParser.TablePrimaryContext ctx)
    {
      if (ctx.UNNEST() != null)
      {
        setUnnest(true);
        if (ctx.ORDINALITY() != null)
          setWithOrdinality(true);
      }
      else if (ctx.TABLE() != null)
        setTable(true);
      else if (ctx.ONLY() != null)
        setOnly(true);
      else if (ctx.LATERAL() != null)
        setLateral(true);
      return visitChildren(ctx);
    }
    @Override
    public TablePrimary visitArrayValueExpression(SqlParser.ArrayValueExpressionContext ctx)
    {
      setArrayValueExpression(getSqlFactory().newArrayValueExpression());
      getArrayValueExpression().parse(ctx);
      return TablePrimary.this;
    }
    @Override
    public TablePrimary visitMultisetValueExpression(SqlParser.MultisetValueExpressionContext ctx)
    {
      setMultisetValueExpression(getSqlFactory().newMultisetValueExpression());
      getMultisetValueExpression().parse(ctx);
      return TablePrimary.this;
    }
    @Override
    public TablePrimary visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return TablePrimary.this;
    }
    @Override
    public TablePrimary visitTableAlias(SqlParser.TableAliasContext ctx)
    {
      setIdentifier(ctx.correlationName().IDENTIFIER(),getCorrelationName());
      for (int i = 0; i < ctx.columnName().size(); i++)
      {
        Identifier idColumnName = new Identifier();
        setColumnName(ctx.columnName(i),idColumnName);
        getAliasColumnNames().add(idColumnName);
      }
      return TablePrimary.this;
    }
    @Override
    public TablePrimary visitTableReference(SqlParser.TableReferenceContext ctx)
    {
      setTableReference(getSqlFactory().newTableReference());
      getTableReference().parse(ctx);
      return TablePrimary.this;
    }
    @Override
    public TablePrimary visitQueryExpression(SqlParser.QueryExpressionContext ctx)
    {
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx);
      return TablePrimary.this;
    }
    
  }
  /*==================================================================*/
  
  private TpVisitor _visitor = new TpVisitor();
  private TpVisitor getVisitor() { return _visitor; }

  private QualifiedId _qiTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qiTableName; }
  private void setTableName(QualifiedId qiTableName) { _qiTableName = qiTableName; }
  
  private Identifier _idCorrelationName = new Identifier();
  public Identifier getCorrelationName() { return _idCorrelationName; }
  private void setCorrelationName(Identifier idCorrelationName) { _idCorrelationName = idCorrelationName; }
  
  private List<Identifier> _listAliasColumnNames = new ArrayList<Identifier>();
  public List<Identifier> getAliasColumnNames() { return _listAliasColumnNames; }
  private void setAliasColumnNames(List<Identifier> listAliasColumnNames) { _listAliasColumnNames = listAliasColumnNames; }

  private TableReference _tr = null;
  public TableReference getTableReference() { return _tr; }
  public void setTableReference(TableReference tr) { _tr = tr; }
  
  private boolean _bLateral = false;
  public boolean isLateral() { return _bLateral; }
  public void setLateral(boolean bLateral) { _bLateral = bLateral; }
  
  private QueryExpression _qe = null;
  public QueryExpression getQueryExpression() { return _qe; }
  public void setQueryExpression(QueryExpression qe) { _qe = qe; }

  private boolean _bUnnest = false;
  public boolean isUnnest() { return _bUnnest; }
  public void setUnnest(boolean bUnnest) { _bUnnest = bUnnest; }
  
  private boolean _bWithOrdinality = false;
  public boolean isWithOrdinality() { return _bWithOrdinality; }
  public void setWithOrdinality(boolean bWithOrdinality) { _bWithOrdinality = bWithOrdinality; }
  
  private ArrayValueExpression _ave = null;
  public ArrayValueExpression getArrayValueExpression() { return _ave; }
  public void setArrayValueExpression(ArrayValueExpression ave) { _ave = ave; }
  
  private MultisetValueExpression _mve = null;
  public MultisetValueExpression getMultisetValueExpression() { return _mve; }
  public void setMultisetValueExpression(MultisetValueExpression mve) { _mve = mve; }
  
  private boolean _bTable = false;
  public boolean isTable() { return _bTable; }
  public void setTable(boolean bTable) { _bTable = bTable; }
  
  private boolean _bOnly = false;
  public boolean isOnly() { return _bOnly; }
  public void setOnly(boolean bOnly) { _bOnly = bOnly; }

  /* for evaluation */
  /*------------------------------------------------------------------*/
  /** check, if the table has a column with the given name.
   * @param sColumn column name.
   * @return true, if the table has a column with the given name.
   */
  public boolean hasColumn(String sColumn)
  {
    return getColumnTypes().keySet().contains(sColumn);
  } /* hasColumn */
  
  private List<String> _listColumnNames = new ArrayList<String>();
  public List<String> getColumnNames() { return _listColumnNames; }
  
  /*------------------------------------------------------------------*/
  /** set the column names of the table and initialize their values to null.
   * @param listColumnNames list of column names of table.
   */
  public void setColumnNames(List<String> listColumnNames)
  {
    _listColumnNames = listColumnNames;
    getColumnValues().clear();
    for (int iColumn = 0; iColumn < listColumnNames.size(); iColumn++)
      getColumnValues().put(listColumnNames.get(iColumn), null);
  } /* setColumnNames */

  private Map<String, DataType> _mapColumnTypes = new HashMap<String, DataType>();
  protected Map<String, DataType> getColumnTypes() { return _mapColumnTypes; }
  private Map<String, Object> _mapColumnValues = new HashMap<String,Object>();
  protected Map<String, Object> getColumnValues() { return _mapColumnValues; }
  /*------------------------------------------------------------------*/
  /** set data type of a table column.
   * @param sColumnName name of column.
   * @param dtColumn data type of column.
   */
  public void setColumnType(String sColumnName, DataType dtColumn)
  {
    getColumnTypes().put(sColumnName, dtColumn);
  } /* setColumnType */
  
  /*------------------------------------------------------------------*/
  /** set data type of a table column.
   * @param iPosition (1-based) position of column.
   * @param dtColumn data type of column.
   */
  public void setColumnType(int iPosition, DataType dtColumn)
  {
    setColumnType(getColumnNames().get(iPosition-1),dtColumn);
  } /* setColumnType */
  
  /*------------------------------------------------------------------*/
  /** get type of table column.
   * N.B.: We ignore alias names of column for the moment!
   * @param sColumnName name of column.
   * @return value of column.
   */
  public DataType getColumnType(String sColumnName)
  {
    return getColumnTypes().get(sColumnName);
  } /* getColumnType */
  
  /*------------------------------------------------------------------*/
  /** set value of a table column.
   * @param sColumnName name of column.
   * @param oColumnValue value of column.
   */
  public void setColumnValue(String sColumnName, Object oColumnValue)
  {
    getColumnValues().put(sColumnName,oColumnValue);
  } /* setColumnValue */
  
  /*------------------------------------------------------------------*/
  /** set value of a table column.
   * @param iPosition (1-based) position of column.
   * @param oColumnValue value of column.
   */
  public void setColumnValue(int iPosition, Object oColumnValue)
  {
    setColumnValue(getColumnNames().get(iPosition-1),oColumnValue);
  } /* setColumnValue */
  
  /*------------------------------------------------------------------*/
  /** get value of table column.
   * N.B.: We ignore alias names of column for the moment!
   * @param sColumnName name of column.
   * @return value of column.
   */
  public Object getColumnValue(String sColumnName)
  {
    return getColumnValues().get(sColumnName);
  } /* setColumnValue */
  
  /*------------------------------------------------------------------*/
  /** format the table primary.
   * @return the SQL string corresponding to the fields of the table primary.
   */
  @Override
  public String format()
  {
    String s = "";
    if (isOnly() && (getTableName() != null))
      s = K.ONLY.getKeyword() + sLEFT_PAREN + getTableName().format() + sRIGHT_PAREN;
    else if (isTable())
    {
      s = K.TABLE.getKeyword() + sLEFT_PAREN;
      if (getArrayValueExpression() != null)
        s = s + getArrayValueExpression().format();
      else if (getMultisetValueExpression() != null)
        s = s + getMultisetValueExpression().format();
      s = s + sRIGHT_PAREN;
    }
    else if (isUnnest())
    {
      s = K.UNNEST.getKeyword() + sLEFT_PAREN;
      if (getArrayValueExpression() != null)
        s = s + getArrayValueExpression().format();
      else if (getMultisetValueExpression() != null)
        s = s + getMultisetValueExpression().format();
      s = s + sRIGHT_PAREN;
      if (isWithOrdinality())
        s = s + sSP + K.WITH.getKeyword() + sSP + K.ORDINALITY.getKeyword();
    }
    else if (getQueryExpression() != null)
    {
      if (isLateral())
        s = K.LATERAL.getKeyword();
      s = s + sLEFT_PAREN + getQueryExpression().format() + sRIGHT_PAREN;
    }
    else if (getTableReference() != null)
      s = s + getTableReference().format();
    else
      s = s + getTableName().format();
    if (getCorrelationName().isSet())
    {
      s = s + sSP + K.AS.getKeyword() + sSP + getCorrelationName().format();
      if (getAliasColumnNames().size() > 0)
      {
        s = s + sLEFT_PAREN;
        for (int i = 0; i < getAliasColumnNames().size(); i++)
        {
          if (i > 0)
            s = s + sCOMMA + sSP;
          s = s + getAliasColumnNames().get(i).format();
        }
        s = s + sRIGHT_PAREN;
      }
    }
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the table primary from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.TablePrimaryContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the table primary from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().tablePrimary());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a table primary.
   * @param qiTableName table name.
   * @param idCorrelationName table alias.
   * @param listAliasColumnNames list of AS column names.
   * @param tr table reference.
   * @param bLateral LATERAL table expression.
   * @param qe query expression for LATERAL.
   * @param bUnnest true, if array/multiset value expression is to be unnested.
   * @param bWithOrdinality true, if unnesting is executed WITH ORDINALITY.
   * @param ave array value expression.
   * @param mve multiset value expression.
   * @param bTable true, for TABLE expression for arrays/multisets.
   * @param bOnly true, of ONLY(table) expression. 
   */
  public void initialize(
    QualifiedId qiTableName,
    Identifier idCorrelationName,
    List<Identifier> listAliasColumnNames,
    TableReference tr,
    boolean bLateral,
    QueryExpression qe,
    boolean bUnnest,
    boolean bWithOrdinality,
    ArrayValueExpression ave,
    MultisetValueExpression mve,
    boolean bTable,
    boolean bOnly)
  {
    _il.enter();
    setTableName(qiTableName);
    setCorrelationName(idCorrelationName);
    setAliasColumnNames(listAliasColumnNames);
    setTableReference(tr);
    setLateral(bLateral);
    setQueryExpression(qe);
    setUnnest(bUnnest);
    setWithOrdinality(bWithOrdinality);
    setArrayValueExpression(ave);
    setMultisetValueExpression(mve);
    setTable(bTable);
    setOnly(bOnly);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** initialize a table primary.
   * @param qiTableName table name.
   * @param idCorrelationName correlation name (table alias).
   */
  public void initialize(
    QualifiedId qiTableName,
    Identifier idCorrelationName)
  {
    _il.enter();
    setTableName(qiTableName);
    setCorrelationName(idCorrelationName);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** initialize a table primary.
   * @param qiTableName table name.
   */
  public void initialize(QualifiedId qiTableName)
  {
    _il.enter();
    setTableName(qiTableName);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public TablePrimary(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class TablePrimary */
