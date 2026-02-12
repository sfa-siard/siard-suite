package ch.enterag.sqlparser.dml;

import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class DeleteStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DeleteStatement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DsVisitor extends EnhancedSqlBaseVisitor<DeleteStatement>
  {
    @Override
    public DeleteStatement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return DeleteStatement.this;
    }
    @Override
    public DeleteStatement visitBooleanValueExpression(SqlParser.BooleanValueExpressionContext ctx)
    {
      setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
      getBooleanValueExpression().parse(ctx);
      return DeleteStatement.this;
    }
  }
  /*==================================================================*/

  private DsVisitor _visitor = new DsVisitor();
  private DsVisitor getVisitor() { return _visitor; }

  private QualifiedId _qiTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qiTableName; }
  private void setTableName(QualifiedId qiTableName) { _qiTableName = qiTableName; }
  
  private BooleanValueExpression _bve = null;
  public BooleanValueExpression getBooleanValueExpression() { return _bve; }
  public void setBooleanValueExpression(BooleanValueExpression bve) { _bve = bve; }
  
  /* for evaluation */
  /*------------------------------------------------------------------*/
  /** check, if the table has a column with the given name.
   * @param sColumn column name.
   * @return true, if the table ahs a column with the given name.
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
  /** format the delete statement.
   * @return the SQL string corresponding to the fields of the delete
   *   statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DELETE.getKeyword() + sSP + K.FROM.getKeyword() + sSP +
      getTableName().format();
    if (getBooleanValueExpression() != null)
      sStatement = sStatement + sSP + K.WHERE + sSP + getBooleanValueExpression().format();
    return sStatement;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the delete statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DeleteStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the delete statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().deleteStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a delete statement.
   * @param qiTableName name of table.
   * @param bve WHEN condition for records to delete.
   */
  public void initialize(
      QualifiedId qiTableName,
      BooleanValueExpression bve)
  {
    _il.enter();
    setTableName(qiTableName);
    setBooleanValueExpression(bve);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DeleteStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DeleteStatement */
