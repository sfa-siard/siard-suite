package ch.enterag.sqlparser.dml;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.dml.enums.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class InsertStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(InsertStatement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class IsVisitor extends EnhancedSqlBaseVisitor<InsertStatement>
  {
    @Override
    public InsertStatement visitAssignedRow(SqlParser.AssignedRowContext ctx)
    {
      AssignedRow ar = getSqlFactory().newAssignedRow();
      ar.parse(ctx);
      getValues().add(ar);
      return visitChildren(ctx);
    }
    @Override
    public InsertStatement visitFromDefault(SqlParser.FromDefaultContext ctx)
    {
      setFromDefault(true);
      return InsertStatement.this;
    }
    @Override
    public InsertStatement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return InsertStatement.this;
    }
    @Override
    public InsertStatement visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      addColumnName(ctx,getColumnNames());
      return InsertStatement.this;
    }
    @Override
    public InsertStatement visitOverrideClause(SqlParser.OverrideClauseContext ctx)
    {
      setOverrideClause(getOverrideClause(ctx));
      return InsertStatement.this;
    }
    @Override
    public InsertStatement visitQueryExpression(SqlParser.QueryExpressionContext ctx)
    {
      setQueryExpression(getSqlFactory().newQueryExpression());
      getQueryExpression().parse(ctx);
      return InsertStatement.this;
    }
  }
  /*==================================================================*/

  private IsVisitor _visitor = new IsVisitor();
  private IsVisitor getVisitor() { return _visitor; }

  private QualifiedId _qiTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qiTableName; }
  private void setTableName(QualifiedId qiTableName) { _qiTableName = qiTableName; }
  
  private IdList _ilColumnNames = new IdList();
  public IdList getColumnNames() { return _ilColumnNames; }
  private void setColumnNames(IdList ilColumnNames) { _ilColumnNames = ilColumnNames; }
  
  private List<AssignedRow> _listValues = new ArrayList<AssignedRow>();
  public List<AssignedRow> getValues() { return _listValues; }
  private void setValues(List<AssignedRow> listValues) { _listValues = listValues; }
  
  private QueryExpression _qe = null;
  public QueryExpression getQueryExpression() { return _qe; }
  public void setQueryExpression(QueryExpression qe) { _qe = qe; }
  
  private OverrideClause _oc = null;
  public OverrideClause getOverrideClause() { return _oc; }
  public void setOverrideClause(OverrideClause oc) { _oc = oc; }
  
  private boolean _bFromDefault = false;
  public boolean isFromDefault() { return _bFromDefault; }
  public void setFromDefault(boolean bFromDefault) { _bFromDefault = bFromDefault; }

  /*------------------------------------------------------------------*/
  /** format the values
   * @return SQL string representing the values.
   */
  public String formatValues()
  {
    String sValues = K.VALUES.getKeyword();
    for (int i = 0; i < getValues().size(); i++)
    {
      if (i > 0)
        sValues = sValues + sCOMMA + sSP;
      sValues = sValues + sNEW_LINE + sINDENT + getValues().get(i).format();
    }
    return sValues;
  } /* formatValues */
  
  /*------------------------------------------------------------------*/
  /** format the insert statement.
   * @return the SQL string corresponding to the fields of the insert
   *   statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.INSERT.getKeyword() + sSP + K.INTO.getKeyword() + sSP +
      getTableName().format();
    if (!isFromDefault())
    {
      sStatement = sStatement + sLEFT_PAREN + getColumnNames().format() + sRIGHT_PAREN;
      if (getOverrideClause() != null)
        sStatement = sStatement + sSP + getOverrideClause().getKeywords();
      if (getValues().size() > 0)
        sStatement = sStatement + sSP + formatValues(); 
      else if (getQueryExpression() != null)
        sStatement = sStatement + sSP + getQueryExpression().format();
    }
    else
      sStatement = sStatement + sSP + K.DEFAULT.getKeyword() + sSP + K.VALUES.getKeyword();
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the insert statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.InsertStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the insert statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().insertStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize an insert statement.
   * @param qiTableName name of table for INSERT.
   * @param ilColumnNames list of column names for values.
   * @param listValues list of values to be inserted.
   * @param qe query expression for INSERT.
   * @param oc override clause.
   * @param bFromDefault FROM DEFAULT.
   */
  public void initialize(
      QualifiedId qiTableName,
      IdList ilColumnNames,
      List<AssignedRow> listValues,
      QueryExpression qe,
      OverrideClause oc,
      boolean bFromDefault)
  {
    _il.enter();
    setTableName(qiTableName);
    setColumnNames(ilColumnNames);
    setValues(listValues);
    setQueryExpression(qe);
    setOverrideClause(oc);
    setFromDefault(bFromDefault);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public InsertStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class InsertStatement */
