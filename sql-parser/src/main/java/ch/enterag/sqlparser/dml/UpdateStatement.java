package ch.enterag.sqlparser.dml;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class UpdateStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(UpdateStatement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class UsVisitor extends EnhancedSqlBaseVisitor<UpdateStatement>
  {
    @Override
    public UpdateStatement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return UpdateStatement.this;
    }
    @Override
    public UpdateStatement visitSetClause(SqlParser.SetClauseContext ctx)
    {
      SetClause sc = getSqlFactory().newSetClause();
      sc.parse(ctx);
      getSetClauses().add(sc);
      return UpdateStatement.this;
    }
    @Override
    public UpdateStatement visitBooleanValueExpression(SqlParser.BooleanValueExpressionContext ctx)
    {
      setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
      getBooleanValueExpression().parse(ctx);
      return UpdateStatement.this;
    }
  }
  /*==================================================================*/

  private UsVisitor _visitor = new UsVisitor();
  private UsVisitor getVisitor() { return _visitor; }

  private QualifiedId _qiTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qiTableName; }
  private void setTableName(QualifiedId qiTableName) { _qiTableName = qiTableName; }
  
  private List<SetClause> _listSetClauses = new ArrayList<SetClause>();
  public List<SetClause> getSetClauses() { return _listSetClauses; }
  private void setSetClauses(List<SetClause> listSetClauses) { _listSetClauses = listSetClauses; }
  
  private BooleanValueExpression _bve = null;
  public BooleanValueExpression getBooleanValueExpression() { return _bve; }
  public void setBooleanValueExpression(BooleanValueExpression bve) { _bve = bve; }
  
  /* for evaluation */
  /*------------------------------------------------------------------*/
  /** check, if the table has a column with the given name.
   * @param sColumn column name.
   * @return true, if the table has a column with the given name.
   */
  public boolean hasColumn(String sColumn)
  {
    return getColumnValues().keySet().contains(sColumn);
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
  private Map<String, DataType> getColumnTypes() { return _mapColumnTypes; }
  private Map<String, Object> _mapColumnValues = new HashMap<String,Object>();
  private Map<String, Object> getColumnValues() { return _mapColumnValues; }
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
  /** format the update statement.
   * @return the SQL string corresponding to the fields of the update
   *   statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.UPDATE.getKeyword() + sSP + getTableName().format() + 
      sSP + K.SET.getKeyword();
    for (int i = 0; i < getSetClauses().size(); i++)
    {
      if (i > 0)
        sStatement = sStatement + sCOMMA;
      sStatement = sStatement  + sNEW_LINE + sINDENT + getSetClauses().get(i).format();
    }
    if (getBooleanValueExpression() != null)
      sStatement = sStatement + sNEW_LINE + K.WHERE + sSP + getBooleanValueExpression().format();
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the update statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.UpdateStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the update statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().updateStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize an update statement.
   * @param qiTableName name of table to be updated.
   * @param listSetClauses list of SET clauses.
   * @param bve WHEN condition for records to be updated.
   */
  public void initialize(
      QualifiedId qiTableName,
      List<SetClause> listSetClauses,
      BooleanValueExpression bve)
  {
    _il.enter();
    setTableName(qiTableName);
    setSetClauses(listSetClauses);
    setBooleanValueExpression(bve);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public UpdateStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class UpdateStatement */
