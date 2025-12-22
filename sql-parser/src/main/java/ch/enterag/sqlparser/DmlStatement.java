package ch.enterag.sqlparser;

import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.dml.*;
import ch.enterag.sqlparser.generated.*;

public class DmlStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DmlStatement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DsVisitor extends EnhancedSqlBaseVisitor<DmlStatement>
  {
    @Override
    public DmlStatement visitInsertStatement(SqlParser.InsertStatementContext ctx)
    {
      setInsertStatement(getSqlFactory().newInsertStatement());
      getInsertStatement().parse(ctx);
      return DmlStatement.this;
    }
    @Override
    public DmlStatement visitDeleteStatement(SqlParser.DeleteStatementContext ctx)
    {
      setDeleteStatement(getSqlFactory().newDeleteStatement());
      getDeleteStatement().parse(ctx);
      return DmlStatement.this;
    }
    @Override
    public DmlStatement visitUpdateStatement(SqlParser.UpdateStatementContext ctx)
    {
      setUpdateStatement(getSqlFactory().newUpdateStatement());
      getUpdateStatement().parse(ctx);
      return DmlStatement.this;
    }
  }
  /*==================================================================*/

  private DsVisitor _visitor = new DsVisitor();
  private DsVisitor getVisitor() { return _visitor; }

  private InsertStatement _is = null;
  public InsertStatement getInsertStatement() { return _is; }
  public void setInsertStatement(InsertStatement is) { _is = is; }
  
  private DeleteStatement _ds = null;
  public DeleteStatement getDeleteStatement() { return _ds; }
  public void setDeleteStatement(DeleteStatement ds) { _ds = ds; }
  
  private UpdateStatement _us = null;
  public UpdateStatement getUpdateStatement() { return _us; }
  public void setUpdateStatement(UpdateStatement us) { _us = us; }
  
  /*------------------------------------------------------------------*/
  /** format the DML statement.
   * @return the SQL string corresponding to the fields of the DML statement.
   */
  @Override
  public String format()
  {
    String sStatement = null;
    if (getInsertStatement() != null)
      sStatement = getInsertStatement().format();
    else if (getDeleteStatement() != null)
      sStatement = getDeleteStatement().format();
    else if (getUpdateStatement() != null)
      sStatement = getUpdateStatement().format();
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the DML statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DmlStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the DML statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().dmlStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a DML statement.
   * Only one parameter not null!
   * @param is insert statement.
   * @param ds delete statement.
   * @param us update statement.
   */
  public void initialize(
    InsertStatement is,
    DeleteStatement ds,
    UpdateStatement us
    )
  {
    _il.enter();
    setInsertStatement(is);
    setDeleteStatement(ds);
    setUpdateStatement(us);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DmlStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DmlStatement */
