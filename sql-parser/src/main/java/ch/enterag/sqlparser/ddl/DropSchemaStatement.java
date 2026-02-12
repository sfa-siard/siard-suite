package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class DropSchemaStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DropSchemaStatement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DssVisitor extends EnhancedSqlBaseVisitor<DropSchemaStatement>
  {
    @Override
    public DropSchemaStatement visitSchemaName(SqlParser.SchemaNameContext ctx)
    {
      setSchemaName(ctx,getSchemaName());
      return DropSchemaStatement.this;
    }
    @Override
    public DropSchemaStatement visitDropBehavior(SqlParser.DropBehaviorContext ctx)
    {
      setDropBehavior(getDropBehavior(ctx));
      return DropSchemaStatement.this;
    }
  }
  /*==================================================================*/

  private DssVisitor _visitor = new DssVisitor();
  private DssVisitor getVisitor() { return _visitor; }

  private SchemaId _sidSchemaName = new SchemaId();
  public SchemaId getSchemaName() { return _sidSchemaName; }
  private void setSchemaName(SchemaId sidSchemaName) { _sidSchemaName = sidSchemaName; }
  
  private DropBehavior _db = null;
  public DropBehavior getDropBehavior() { return _db; }
  public void setDropBehavior(DropBehavior db) { _db = db; }
  
  /*------------------------------------------------------------------*/
  /** format the drop schema statement.
   * @return the SQL string corresponding to the fields of the drop schema statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.DROP.getKeyword() + sSP + K.SCHEMA.getKeyword() + sSP + 
      getSchemaName().format() + sSP + getDropBehavior().getKeywords(); 
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the drop schema statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DropSchemaStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the drop schema statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().dropSchemaStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a drop schema statement.
   * @param sidSchemaName name of schema to be dropped.
   * @param db drop behavor (not null!).
   */
  public void initialize(SchemaId sidSchemaName, DropBehavior db)
  {
    _il.enter(sidSchemaName, db);
    setSchemaName(sidSchemaName);
    setDropBehavior(db);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DropSchemaStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DropSchemaStatement */
