package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class TableColumn
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(TableColumn.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class TcVisitor extends EnhancedSqlBaseVisitor<TableColumn>
  {
    @Override
    public TableColumn visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      setColumnName(ctx,getColumnName());
      return TableColumn.this;
    }
    @Override
    public TableColumn visitDataType(SqlParser.DataTypeContext ctx)
    {
      setDataType(getSqlFactory().newDataType());
      getDataType().parse(ctx);
      return TableColumn.this;
    }
  }
  /*==================================================================*/

  private TcVisitor _visitor = new TcVisitor();
  private TcVisitor getVisitor() { return _visitor; }
  
  private Identifier _idColumnName = new Identifier();
  public Identifier getColumnName() { return _idColumnName; }
  private void setColumnName(Identifier idColumnName) { _idColumnName = idColumnName; }
  
  private DataType _dt = null;
  public DataType getDataType() { return _dt; }
  public void setDataType(DataType dt) { _dt = dt; }
  
  /*------------------------------------------------------------------*/
  /** format the table column.
   * @return the SQL string corresponding to the fields of the table column.
   */
  @Override
  public String format()
  {
    String sTableColumn = getColumnName().format() + sSP + getDataType().format();
    return sTableColumn;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the table column from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.TableColumnContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the table column from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().tableColumn());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a table column.
   * @param idColumnName column name (not null!).
   * @param dt data type (not null!).
   */
  public void initialize(
    Identifier idColumnName,
    DataType dt)
  {
    _il.enter(idColumnName,dt);
    setColumnName(idColumnName);
    setDataType(dt);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public TableColumn(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class TableColumn */
