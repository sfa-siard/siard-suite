package ch.enterag.sqlparser.expression;

import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class TableReference
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(TableReference.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class TrVisitor extends EnhancedSqlBaseVisitor<TableReference>
  {
    @Override
    public TableReference visitTableReference(SqlParser.TableReferenceContext ctx)
    {
      if (ctx.CROSS() != null)
      {
        setJoinOperator(JoinOperator.CROSS_JOIN);
        setTableReference(getSqlFactory().newTableReference());
        getTableReference().parse(ctx.tableReference(0));
        setTablePrimary(getSqlFactory().newTablePrimary());
        getTablePrimary().parse(ctx.tablePrimary());
      }
      else if (ctx.NATURAL() != null)
      {
        setJoinOperator(JoinOperator.NATURAL);
        setJoinType(getJoinType(ctx.joinType()));
        setTableReference(getSqlFactory().newTableReference());
        getTableReference().parse(ctx.tableReference(0));
        setTablePrimary(getSqlFactory().newTablePrimary());
        getTablePrimary().parse(ctx.tablePrimary());
      }
      else if (ctx.UNION() != null)
      {
        setJoinOperator(JoinOperator.UNION);
        setTableReference(getSqlFactory().newTableReference());
        getTableReference().parse(ctx.tableReference(0));
        setTablePrimary(getSqlFactory().newTablePrimary());
        getTablePrimary().parse(ctx.tablePrimary());
      }
      else if (ctx.JOIN() != null)
      {
        setJoinOperator(JoinOperator.JOIN);
        setJoinType(getJoinType(ctx.joinType()));
        setTableReference(getSqlFactory().newTableReference());
        getTableReference().parse(ctx.tableReference(0));
        setSecondTableReference(getSqlFactory().newTableReference());
        getSecondTableReference().parse(ctx.tableReference(1));
        if (ctx.joinSpecification().booleanValueExpression() != null)
        {
          setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
          getBooleanValueExpression().parse(ctx.joinSpecification().booleanValueExpression());
        }
        else
        {
          for (int i = 0; i < ctx.joinSpecification().columnName().size(); i++)
          {
            Identifier idColumnName = new Identifier();
            setColumnName(ctx.joinSpecification().columnName(i),idColumnName);
            getJoinColumnNames().add(idColumnName);
          }
        }
      }
      else if ((ctx.TABLESAMPLE() != null) && (ctx.numericValueExpression().size() > 0))
      {
        setSampleMethod(getSampleMethod(ctx.sampleMethod()));
        setNumericValueExpression(getSqlFactory().newNumericValueExpression());
        getNumericValueExpression().parse(ctx.numericValueExpression(0));
        if (ctx.numericValueExpression().size() > 1)
        {
          setRepeatableNumericValueExpression(getSqlFactory().newNumericValueExpression());
          getRepeatableNumericValueExpression().parse(ctx.numericValueExpression(1));
        }
      }
      else if (ctx.tablePrimary() != null)
      {
        setTablePrimary(getSqlFactory().newTablePrimary());
        getTablePrimary().parse(ctx.tablePrimary());
      }
      return TableReference.this;
    }
  }
  /*==================================================================*/
  
  private TrVisitor _visitor = new TrVisitor();
  private TrVisitor getVisitor() { return _visitor; }
  
  private TablePrimary _tp = null;
  public TablePrimary getTablePrimary() { return _tp; }
  public void setTablePrimary(TablePrimary tp) { _tp = tp; }
  
  private JoinOperator _jo = null;
  public JoinOperator getJoinOperator() { return _jo; }
  public void setJoinOperator(JoinOperator jf) { _jo = jf; }
  
  private TableReference _tr = null;
  public TableReference getTableReference() { return _tr; }
  public void setTableReference(TableReference tr) { _tr = tr; }
  
  private TableReference _tr2 = null;
  public TableReference getSecondTableReference() { return _tr2; }
  public void setSecondTableReference(TableReference tr2) { _tr2 = tr2; }
  
  private JoinType _jt = null;
  public JoinType getJoinType() { return _jt; }
  public void setJoinType(JoinType jt) { _jt = jt; }

  private BooleanValueExpression _bve = null;
  public BooleanValueExpression getBooleanValueExpression() { return _bve; }
  public void setBooleanValueExpression(BooleanValueExpression bve) { _bve = bve; }
  
  private List<Identifier> _listJoinColumnNames = null;
  public List<Identifier> getJoinColumnNames() { return _listJoinColumnNames; }
  private void setJoinColumnNames(List<Identifier> listJoinColumnNames) { _listJoinColumnNames = listJoinColumnNames; }

  private SampleMethod _sm = null;
  public SampleMethod getSampleMethod()  { return _sm; }
  public void setSampleMethod(SampleMethod sm) { _sm = sm; }
  
  private NumericValueExpression _nve = null;
  public NumericValueExpression getNumericValueExpression() { return _nve; }
  public void setNumericValueExpression(NumericValueExpression nve) { _nve = nve; }
  
  private NumericValueExpression _nveRep = null;
  public NumericValueExpression getRepeatableNumericValueExpression() { return _nveRep; }
  public void setRepeatableNumericValueExpression(NumericValueExpression nveRep) { _nveRep = nveRep; }
  
  /*------------------------------------------------------------------*/
  /** format the table reference.
   * @return the SQL string corresponding to the fields of the table reference.
   */
  @Override
  public String format()
  {
    String s = null;
    if (getJoinOperator() != null)
    {
      switch (getJoinOperator())
      {
        case CROSS_JOIN:
        case UNION:
          s = getTableReference().format() + sSP + getJoinOperator().getKeywords() + sSP + 
            getTablePrimary().format();
          break;
        case NATURAL:
          s = getTableReference().format() + sSP + getJoinOperator().getKeywords() + sSP +
            getJoinType().getKeywords() + sSP + K.JOIN.getKeyword() + sSP +
            getTablePrimary().format();
          break;
        case JOIN:
          s = getTableReference().format() + sSP + getJoinType().getKeywords() + sSP + 
            getJoinOperator() + sSP + getSecondTableReference().format();
          if (getBooleanValueExpression() != null)
            s = s + sSP + K.ON.getKeyword() + sSP + getBooleanValueExpression().format();
          else
          {
            s = s + sSP + K.USING.getKeyword() + sLEFT_PAREN;
            for (int i = 0; i < getJoinColumnNames().size(); i++)
            {
              if (i > 0)
                s = s + sCOMMA + sSP;
              s = s + getJoinColumnNames().get(i).format();
            }
            s = s + sRIGHT_PAREN;
          }
          break;
      }
    }
    else if ((getSampleMethod() != null) && ((getNumericValueExpression() != null)))
    {
      s = K.TABLESAMPLE.getKeyword() + sSP + getSampleMethod().getKeywords() +
        sLEFT_PAREN + getNumericValueExpression().format() + sRIGHT_PAREN;
      if (getRepeatableNumericValueExpression() != null)
      {
        s = s + sSP + K.REPEATABLE.getKeyword() + 
          sLEFT_PAREN + getRepeatableNumericValueExpression().format() +  sRIGHT_PAREN;
      }
          
    }
    else
      s = getTablePrimary().format();
    return s;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the table reference from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.TableReferenceContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the table reference from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().tableReference());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** initialize a table reference.
   * @param tp table primary.
   * @param jo JOIN operator.
   * @param tr table reference operand.
   * @param tr2 second table reference operand.
   * @param jt JOIN type.
   * @param bve boolean value expression for join.
   * @param listJoinColumnNames list of column names to join on.
   * @param sm sample method.
   * @param nve numeric value expression.
   */
  public void initialize(
    TablePrimary tp,
    JoinOperator jo,
    TableReference tr,
    TableReference tr2,
    JoinType jt,
    BooleanValueExpression bve,
    List<Identifier> listJoinColumnNames,
    SampleMethod sm,
    NumericValueExpression nve)
  {
    _il.enter();
    setTablePrimary(tp);
    setJoinOperator(jo);
    setTableReference(tr);
    setSecondTableReference(tr2);
    setJoinType(jt);
    setBooleanValueExpression(bve);
    setJoinColumnNames(listJoinColumnNames);
    setSampleMethod(sm);
    setNumericValueExpression(nve);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public TableReference(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class TableReference */
