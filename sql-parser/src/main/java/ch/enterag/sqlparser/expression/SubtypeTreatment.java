package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class SubtypeTreatment
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SubtypeTreatment.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class StVisitor extends EnhancedSqlBaseVisitor<SubtypeTreatment>
  {
    @Override
    public SubtypeTreatment visitSubtypeTreatment(SqlParser.SubtypeTreatmentContext ctx)
    {
      setValueExpression(getSqlFactory().newValueExpression());
      getValueExpression().parse(ctx.valueExpression());
      if (ctx.udtName() != null)
        setUdtName(ctx.udtName(),getUdtName());
      else if (ctx.referenceType() != null)
      {
        setUdtName(ctx.referenceType().udtName(), getUdtName());
        setTableName(ctx.referenceType().scopeDefinition().tableName(), getScopeTable());
      }
      return SubtypeTreatment.this;
    }
  }
  /*==================================================================*/
  
  private StVisitor _visitor = new StVisitor();
  private StVisitor getVisitor() { return _visitor; }
  
  private ValueExpression _ve = null;
  public ValueExpression getValueExpression() { return _ve; }
  public void setValueExpression(ValueExpression ve) { _ve = ve; }
  
  private QualifiedId _qiUdtName = new QualifiedId();
  public QualifiedId getUdtName() { return _qiUdtName; }
  private void setUdtName(QualifiedId qiUdtName) { _qiUdtName = qiUdtName; }
  
  private QualifiedId _qiScopeTable = new QualifiedId();
  public QualifiedId getScopeTable() { return _qiScopeTable; }
  private void setScopeTable(QualifiedId qiScopeTable) { _qiScopeTable = qiScopeTable; }
  
  /*------------------------------------------------------------------*/
  /** format the subtype treatment.
   * @return the SQL string corresponding to the fields of the subtype 
   *   treatment.
   */
  @Override
  public String format()
  {
    String s = K.TREAT.getKeyword() + sLEFT_PAREN + 
      getValueExpression().format() + sSP + K.AS.getKeyword();
    if (getScopeTable().isSet())
    {
      s = s + sLEFT_PAREN + getUdtName().format() + sRIGHT_PAREN + sSP + 
        K.SCOPE.getKeyword() + sSP + getScopeTable().format();
    }
    else
      s = s + sSP + getUdtName().format();
    s = s + sRIGHT_PAREN;
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the subtype treatment from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.SubtypeTreatmentContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the subtype treatment from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().subtypeTreatment());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a subtype treatment.
   * @param ve value expression (not null!).
   * @param qiUdtName UDT (not null!).
   * @param qiScopeTable SCOPE table (not null!).
   */
  public void initialize(
    ValueExpression ve,
    QualifiedId qiUdtName,
    QualifiedId qiScopeTable)
  {
    _il.enter(ve, qiUdtName, qiScopeTable);
    setValueExpression(ve);
    setUdtName(qiUdtName);
    setScopeTable(qiScopeTable);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public SubtypeTreatment(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class SubtypeTreatment */
