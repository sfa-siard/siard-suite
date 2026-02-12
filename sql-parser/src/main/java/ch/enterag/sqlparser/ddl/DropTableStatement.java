package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class DropTableStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DropTableStatement.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DtsVisitor extends EnhancedSqlBaseVisitor<DropTableStatement>
  {
    @Override
    public DropTableStatement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return DropTableStatement.this;
    }
    @Override
    public DropTableStatement visitDropBehavior(SqlParser.DropBehaviorContext ctx)
    {
      setDropBehavior(getDropBehavior(ctx));
      return DropTableStatement.this;
    }
  } /* class DtsVisitor */
  /*==================================================================*/
  
  private DtsVisitor _visitor = new DtsVisitor();
  private DtsVisitor getVisitor() { return _visitor; }
  
  private QualifiedId _qTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qTableName; }
  private void setTableName(QualifiedId qTableName) { _qTableName = qTableName; }
  
  private DropBehavior _db = null;
  public DropBehavior getDropBehavior() { return _db; }
  public void setDropBehavior(DropBehavior db) { _db = db; }

  /*------------------------------------------------------------------*/
  /** format the drop table statement.
   * @return the SQL string corresponding to the fields of the drop table statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DROP.getKeyword() + sSP + K.TABLE.getKeyword() + sSP + 
      getTableName().format() + sSP + getDropBehavior().getKeywords(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the drop table statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DropTableStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the drop table statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().dropTableStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a drop table statement.
   * @param qTableName name of table to be dropped.
   * @param db drop behavior (not null!).
   */
  public void initialize(QualifiedId qTableName, DropBehavior db)
  {
    _il.enter(qTableName, db);
    setTableName(qTableName);
    setDropBehavior(db);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DropTableStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DropTableStatement */
