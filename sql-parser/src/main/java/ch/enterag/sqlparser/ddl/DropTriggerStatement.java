package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class DropTriggerStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DropTriggerStatement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DtsVisitor extends EnhancedSqlBaseVisitor<DropTriggerStatement>
  {
    @Override
    public DropTriggerStatement visitTriggerName(SqlParser.TriggerNameContext ctx)
    {
      setTriggerName(ctx,getTriggerName());
      return visitChildren(ctx);
    }
  }
  /*==================================================================*/

  private DtsVisitor _visitor = new DtsVisitor();
  private DtsVisitor getVisitor() { return _visitor; }

  private QualifiedId _qiTriggerName = new QualifiedId();
  public QualifiedId getTriggerName() { return _qiTriggerName; }
  private void setTriggerName(QualifiedId qiTriggerName) { _qiTriggerName = qiTriggerName; }

  /*------------------------------------------------------------------*/
  /** format the drop trigger statement.
   * @return the SQL string corresponding to the fields of the drop procedure statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DROP.getKeyword() + sSP + K.TRIGGER.getKeyword() + sSP + 
        getTriggerName().format();
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the drop trigger statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DropTriggerStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the drop trigger statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().dropTriggerStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a drop trigger statement.
   * @param qiTriggerName name of trigger to be dropped (not null).
   */
  public void initialize(QualifiedId qiTriggerName)
  {
    _il.enter();
    setTriggerName(qiTriggerName);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DropTriggerStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DropTriggerStatement */
