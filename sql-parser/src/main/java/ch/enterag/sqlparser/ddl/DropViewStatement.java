package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class DropViewStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DropViewStatement.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DvsVisitor extends EnhancedSqlBaseVisitor<DropViewStatement>
  {
    @Override
    public DropViewStatement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getViewName());
      return DropViewStatement.this;
    }
    @Override
    public DropViewStatement visitDropBehavior(SqlParser.DropBehaviorContext ctx)
    {
      setDropBehavior(getDropBehavior(ctx));
      return DropViewStatement.this;
    }
  } /* class DtsVisitor */
  /*==================================================================*/
  
  private DvsVisitor _visitor = new DvsVisitor();
  private DvsVisitor getVisitor() { return _visitor; }
  
  private QualifiedId _qViewName = new QualifiedId();
  public QualifiedId getViewName() { return _qViewName; }
  private void setViewName(QualifiedId qViewName) { _qViewName = qViewName; }
  
  private DropBehavior _db = null;
  public DropBehavior getDropBehavior() { return _db; }
  public void setDropBehavior(DropBehavior db) { _db = db; }

  /*------------------------------------------------------------------*/
  /** format the drop view statement.
   * @return the SQL string corresponding to the fields of the drop view statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DROP.getKeyword() + sSP + K.VIEW.getKeyword() + sSP + 
      getViewName().format() + sSP + getDropBehavior().getKeywords(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the drop view statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DropViewStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the drop view statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().dropViewStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a drop view statement.
   * @param qViewName name of view to be dropped.
   * @param db drop behavior (not null!).
   */
  public void initialize(QualifiedId qViewName, DropBehavior db)
  {
    _il.enter(qViewName, db);
    setViewName(qViewName);
    setDropBehavior(db);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DropViewStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DropViewStatement */
