package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class CreateTriggerStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(CreateTriggerStatement.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class CtsVisitor extends EnhancedSqlBaseVisitor<CreateTriggerStatement>
  {
    @Override
    public CreateTriggerStatement visitTriggerName(SqlParser.TriggerNameContext ctx)
    {
      setTriggerName(ctx,getTriggerName());
      return CreateTriggerStatement.this;
    }
    @Override
    public CreateTriggerStatement visitTriggerActionTime(SqlParser.TriggerActionTimeContext ctx)
    {
      setTriggerActionTime(getTriggerActionTime(ctx));
      return CreateTriggerStatement.this;
    }
    @Override
    public CreateTriggerStatement visitTriggerEvent(SqlParser.TriggerEventContext ctx)
    {
      setTriggerEvent(getTriggerEvent(ctx));
      return visitChildren(ctx);
    }
    @Override
    public CreateTriggerStatement visitColumnName(SqlParser.ColumnNameContext ctx)
    {
      addColumnName(ctx,getColumnNames());
      return visitChildren(ctx);
    }
    @Override
    public CreateTriggerStatement visitTableName(SqlParser.TableNameContext ctx)
    {
      setTableName(ctx,getTableName());
      return visitChildren(ctx);
    }
    @Override
    public CreateTriggerStatement visitOldOrNewValue(SqlParser.OldOrNewValueContext ctx)
    {
      getOldOrNewRefs().add(getOldOrNew(ctx));
      Identifier idCorrelationName = new Identifier();
      setIdentifier(ctx.correlationName().IDENTIFIER(),idCorrelationName);
      getCorrelationNames().add(idCorrelationName);
      return CreateTriggerStatement.this;
    }
    @Override
    public CreateTriggerStatement visitTriggeredAction(SqlParser.TriggeredActionContext ctx)
    {
      if ((ctx.FOR() != null) && (ctx.EACH() != null))
      {
        if (ctx.ROW() != null)
          setForEach(ForEach.FOR_EACH_ROW);
        else if (ctx.STATEMENT() != null)
          setForEach(ForEach.FOR_EACH_STATEMENT);
      }
      if (ctx.booleanValueExpression() != null)
      {
        setBooleanValueExpression(getSqlFactory().newBooleanValueExpression());
        getBooleanValueExpression().parse(ctx.booleanValueExpression());
      }
      if (ctx.ATOMIC() != null)
        setAtomicBody(true);
      setRoutineBody(getSqlFactory().newRoutineBody());
      getRoutineBody().parse(ctx.routineBody());
      return CreateTriggerStatement.this;
    }
  }
  /*==================================================================*/

  private CtsVisitor _visitor = new CtsVisitor();
  private CtsVisitor getVisitor() { return _visitor; }
  
  private QualifiedId _qiTriggerName = new QualifiedId();
  public QualifiedId getTriggerName() { return _qiTriggerName; }
  private void setTriggerName(QualifiedId qiTriggerName) { _qiTriggerName = qiTriggerName; }
  
  private TriggerActionTime _tat = null;
  public TriggerActionTime getTriggerActionTime() { return _tat; }
  public void setTriggerActionTime(TriggerActionTime tat) { _tat = tat; }
  
  private TriggerEvent _te = null;
  public TriggerEvent getTriggerEvent() { return _te; }
  public void setTriggerEvent(TriggerEvent te) { _te = te; }
  
  private IdList _ilColumnNames = new IdList();
  public IdList getColumnNames() { return _ilColumnNames; }
  private void setColumnNames(IdList ilColumnNames) { _ilColumnNames = ilColumnNames; }
  
  private QualifiedId _qiTableName = new QualifiedId();
  public QualifiedId getTableName() { return _qiTableName; }
  public void setTableName(QualifiedId qiTableName) { _qiTableName = qiTableName; }
  
  private List<OldOrNew> _listOldOrNewRefs = new ArrayList<OldOrNew>();
  public List<OldOrNew> getOldOrNewRefs() { return _listOldOrNewRefs; }
  private void setOldOrNewRefs(List<OldOrNew> listOldOrNewRefs) { _listOldOrNewRefs = listOldOrNewRefs; }

  private List<Identifier> _listCorreleationNames = new ArrayList<Identifier>();
  public List<Identifier> getCorrelationNames() { return _listCorreleationNames; }
  private void setCorrelationNames(List<Identifier> listCorreleationNames) { _listCorreleationNames = listCorreleationNames; }

  private ForEach _fe = null;
  public ForEach getForEach() { return _fe; }
  public void setForEach(ForEach fe) { _fe = fe; }
  
  private BooleanValueExpression _bve = null;
  public BooleanValueExpression getBooleanValueExpression() { return _bve; }
  public void setBooleanValueExpression(BooleanValueExpression bve) { _bve = bve; }
  
  private boolean _bAtomicBody = false;
  public boolean hasAtomicBody() { return _bAtomicBody; }
  public void setAtomicBody(boolean bAtomicBody) { _bAtomicBody = bAtomicBody; }
  
  private RoutineBody _rb = null;
  public RoutineBody getRoutineBody() { return _rb; }
  public void setRoutineBody(RoutineBody rb) { _rb = rb; }
  
  /*------------------------------------------------------------------*/
  /** format the create trigger statement.
   * @return the SQL string corresponding to the fields of the create 
   *   trigger statement.
   */
  @Override
  public String format()
  {
    String sStatement = K.CREATE.getKeyword() + sSP + K.TRIGGER.getKeyword() + sSP +
      getTriggerName().format() + sSP + getTriggerActionTime().getKeywords() + sSP + 
      getTriggerEvent().getKeywords();
    if (getColumnNames().isSet())
      sStatement = sStatement + sSP + K.OF.getKeyword() + sSP + getColumnNames().format();
    sStatement = sStatement + sSP + K.ON.getKeyword() + sSP + getTableName().format();
    if (getOldOrNewRefs().size() > 0)
    {
      sStatement = sStatement + sSP + K.REFERENCING.getKeyword();
      for (int i = 0; i < getOldOrNewRefs().size(); i++)
      {
        sStatement = sStatement + sSP + getOldOrNewRefs().get(i).getKeywords() + sSP + 
          getCorrelationNames().get(i).format();
      }
    }
    if (getForEach() != null)
      sStatement = sStatement + sSP + getForEach().getKeywords();
    if (getBooleanValueExpression() != null)
      sStatement = sStatement + sSP + K.WHEN.getKeyword() + 
        sLEFT_PAREN + getBooleanValueExpression().format() + sRIGHT_PAREN;
    if (hasAtomicBody())
      sStatement = sStatement + sSP + K.BEGIN.getKeyword() + sSP + K.ATOMIC.getKeyword() + sSP + 
        getRoutineBody() + K.END.getKeyword();
    else
      sStatement = sStatement + sSP + getRoutineBody().format();
    return sStatement;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the create trigger statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.CreateTriggerStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the create trigger statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().createTriggerStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a create trigger statement.
   * @param qiTriggerName name of trigger.
   * @param tat trigger action time.
   * @param te trigger event.
   * @param ilColumnNames list if column names for trigger event.
   * @param qiTableName name of table for trigger event.
   * @param listOldOrNewRefs OLD/NEW TABLE/ROW AS
   * @param listCorrelationNames list of correlation names.
   * @param fe FOR EACH ROW/STATEMENT.
   * @param bve boolean value expression for trigger.
   * @param bAtomicBody true, if trigger has an atomic body.
   * @param rb routine body of trigger.
   *   
   */
  public void initialize(
    QualifiedId qiTriggerName,
    TriggerActionTime tat ,
    TriggerEvent te,
    IdList ilColumnNames,
    QualifiedId qiTableName,
    List<OldOrNew> listOldOrNewRefs,
    List<Identifier> listCorrelationNames,
    ForEach fe,
    BooleanValueExpression bve,
    boolean bAtomicBody,
    RoutineBody rb)
  {
    _il.enter();
    setTriggerName(qiTriggerName);
    setTriggerActionTime(tat);
    setTriggerEvent(te);
    setColumnNames(ilColumnNames);
    setTableName(qiTableName);
    setOldOrNewRefs(listOldOrNewRefs);
    setCorrelationNames(listCorrelationNames);
    setForEach(fe);
    setBooleanValueExpression(bve);
    setAtomicBody(bAtomicBody);
    setRoutineBody(rb);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public CreateTriggerStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class CreateTriggerStatement */
