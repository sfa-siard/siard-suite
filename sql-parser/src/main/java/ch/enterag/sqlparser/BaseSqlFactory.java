package ch.enterag.sqlparser;

import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.dml.*;
import ch.enterag.sqlparser.expression.*;

public class BaseSqlFactory
  implements SqlFactory
{
  private boolean _bAggregates = false;
  public boolean hasAggregates() { return _bAggregates; }
  public void setAggregates(boolean bAggregates) { _bAggregates = bAggregates; }
  
  private boolean _bCount = false;
  public boolean hasCount() { return _bCount; }
  public void setCount(boolean bCount) { _bCount = bCount; }
  
  public IntervalQualifier newIntervalQualifier()
  {
    return new IntervalQualifier(this);
  } /* newIntervalQualifier */

  public PredefinedType newPredefinedType()
  {
    return new PredefinedType(this);
  } /* newPredefinedType */
  
  public DataType newDataType()
  {
    return new DataType(this);
  } /* newDataType */
  
  public FieldDefinition newFieldDefinition()
  {
    return new FieldDefinition(this);
  } /* newFieldDefinition */
  
  public AggregateFunction newAggregateFunction()
  {
    return new AggregateFunction(this);
  } /* newAggregateFunction */
  
  public ArrayValueExpression newArrayValueExpression()
  {
    return new ArrayValueExpression(this);
  } /* newArrayValueExpression */
  
  public BooleanPrimary newBooleanPrimary()
  {
    return new BooleanPrimary(this);
  } /* newBooleanPrimary */
  
  public BooleanValueExpression newBooleanValueExpression()
  {
    return new BooleanValueExpression(this);
  } /* newBooleanValueExpression */
  
  public CaseExpression newCaseExpression()
  {
    return new CaseExpression(this);
  } /* newCaseExpression */
  
  public CastSpecification newCastSpecification()
  {
    return new CastSpecification(this);
  } /* newCastSpecification */
  
  public CommonValueExpression newCommonValueExpression()
  {
    return new CommonValueExpression(this);
  } /* newCommonValueExpression */
  
  public DatetimeValueExpression newDatetimeValueExpression()
  {
    return new DatetimeValueExpression(this);
  } /* newDatetimeValueExpression */
  
  public DatetimeValueFunction newDatetimeValueFunction()
  {
    return new DatetimeValueFunction(this);
  } /* newDatetimeValueFunction */
  
  public GeneralValueSpecification newGeneralValueSpecification()
  {
    return new GeneralValueSpecification(this);
  } /* newGeneralValueSpecification */
  
  public GroupingElement newGroupingElement()
  {
    return new GroupingElement(this);
  } /* newGroupingElement */
  
  public IntervalValueExpression newIntervalValueExpression()
  {
    return new IntervalValueExpression(this);
  } /* newIntervalValueExpression */
  
  public Literal newLiteral()
  {
    return new Literal(this);
  } /* newLiteral */
  
  public MultisetValueExpression newMultisetValueExpression()
  {
    return new MultisetValueExpression(this);
  } /* newMultisetValueExpression */
  
  public NumericValueExpression newNumericValueExpression()
  {
    return new NumericValueExpression(this);
  } /* newNumericValueExpression */
  
  public NumericValueFunction newNumericValueFunction()
  {
    return new NumericValueFunction(this);
  } /* newNumericValueFunction */
  
  public QueryExpression newQueryExpression()
  {
    return new QueryExpression(this);
  } /* newQueryExpression */
  
  public QueryExpressionBody newQueryExpressionBody()
  {
    return new QueryExpressionBody(this);
  } /* newQueryExpressionBody */
  
  public QuerySpecification newQuerySpecification()
  {
    return new QuerySpecification(this);
  } /* newQuerySpecification */
  
  public RowValueExpression newRowValueExpression()
  {
    return new RowValueExpression(this);
  } /* newRowValueExpression */
  
  public RowValuePredicand newRowValuePredicand()
  {
    return new RowValuePredicand(this);
  } /* newRowValuePredicand */
  
  public SelectSublist newSelectSublist()
  {
    return new SelectSublist(this);
  } /* newSelectSublist */
  
  public SimpleValueSpecification newSimpleValueSpecification()
  {
    return new SimpleValueSpecification(this);
  } /* newSimpleValueSpecification */
  
  public SortSpecification newSortSpecification()
  {
    return new SortSpecification(this);
  } /* newSortSpecification */
  
  public SqlArgument newSqlArgument()
  {
    return new SqlArgument(this);
  } /* newSqlArgument */
  
  public StringValueExpression newStringValueExpression()
  {
    return new StringValueExpression(this);
  } /* newStringValueExpression */
  
  public StringValueFunction newStringValueFunction()
  {
    return new StringValueFunction(this);
  } /* newStringValueFunction */
  
  public SubtypeTreatment newSubtypeTreatment()
  {
    return new SubtypeTreatment(this);
  } /* newSubtypeTreatment */
  
  public TablePrimary newTablePrimary()
  {
    return new TablePrimary(this);
  } /* newTablePrimary */
  
  public TableReference newTableReference()
  {
    return new TableReference(this);
  } /* newTableReference */
  
  public TableRowValueExpression newTableRowValueExpression()
  {
    return new TableRowValueExpression(this);
  } /* newTableRowValueExpression */
  
  public TargetSpecification newTargetSpecification()
  {
    return new TargetSpecification(this);
  } /* newTargetSpecification */
  
  public UnsignedLiteral newUnsignedLiteral()
  {
    return new UnsignedLiteral(this);
  } /* newUnsignedLiteral */
  
  public ValueExpression newValueExpression()
  {
    return new ValueExpression(this);
  } /* newValueExpression */
  
  public ValueExpressionPrimary newValueExpressionPrimary()
  {
    return new ValueExpressionPrimary(this);
  } /* newValueExpressionPrimary */
  
  public WhenOperand newWhenOperand()
  {
    return new WhenOperand(this);
  } /* newWhenOperand */
  
  public WindowFrameBound newWindowFrameBound()
  {
    return new WindowFrameBound(this);
  } /* newWindowFrameBound */
  
  public WindowFunction newWindowFunction()
  {
    return new WindowFunction(this);
  } /* newWindowFunction */
  
  public WindowSpecification newWindowSpecification()
  {
    return new WindowSpecification(this);
  } /* newWindowSpecification */
  
  public WithElement newWithElement()
  {
    return new WithElement(this);
  } /* newWithElement */
  
  public AlterColumnAction newAlterColumnAction()
  {
    return new AlterColumnAction(this);
  } /* newAlterColumnAction */
  
  public AlterTableStatement newAlterTableStatement()
  {
    return new AlterTableStatement(this);
  } /* newAlterTableStatement */
  
  public AlterTypeStatement newAlterTypeStatement()
  {
    return new AlterTypeStatement(this);
  } /* newAlterTypeStatement */
  
  public AttributeDefinition newAttributeDefinition()
  {
    return new AttributeDefinition(this);
  } /* newAttributeDefinition */
  
  public ColumnConstraintDefinition newColumnConstraintDefinition()
  {
    return new ColumnConstraintDefinition(this);
  } /* newColumnConstraintDefinition */
  
  public ColumnDefinition newColumnDefinition()
  {
    return new ColumnDefinition(this);
  } /* newColumnDefinition */
  
  public CreateFunctionStatement newCreateFunctionStatement()
  {
    return new CreateFunctionStatement(this);
  } /* newCreateFunctionStatement */
  
  public CreateMethodStatement newCreateMethodStatement()
  {
    return new CreateMethodStatement(this);
  } /* newCreateMethodStatement */
  
  public CreateProcedureStatement newCreateProcedureStatement()
  {
    return new CreateProcedureStatement(this);
  } /* newCreateProcedureStatement */
  
  public CreateSchemaStatement newCreateSchemaStatement()
  {
    return new CreateSchemaStatement(this);
  } /* newCreateSchemaStatement */
  
  public CreateTableStatement newCreateTableStatement()
  {
    return new CreateTableStatement(this);
  } /* newCreateTableStatement */
  
  public CreateTriggerStatement newCreateTriggerStatement()
  {
    return new CreateTriggerStatement(this);
  } /* newCreateTriggerStatement */
  
  public CreateTypeStatement newCreateTypeStatement()
  {
    return new CreateTypeStatement(this);
  } /* newCreateTypeStatement */
  
  public CreateViewStatement newCreateViewStatement()
  {
    return new CreateViewStatement(this);
  } /* newCreateViewStatement */
  
  public DropFunctionStatement newDropFunctionStatement()
  {
    return new DropFunctionStatement(this);
  } /* newDropFunctionStatement */
  
  public DropMethodStatement newDropMethodStatement()
  {
    return new DropMethodStatement(this);
  } /* newDropMethodStatement */
  
  public DropProcedureStatement newDropProcedureStatement()
  {
    return new DropProcedureStatement(this);
  } /* newDropProcedureStatement */
  
  public DropSchemaStatement newDropSchemaStatement()
  {
    return new DropSchemaStatement(this);
  } /* newDropSchemaStatement */
  
  public DropTableStatement newDropTableStatement()
  {
    return new DropTableStatement(this);
  } /* newDropTableStatement */
  
  public DropTriggerStatement newDropTriggerStatement()
  {
    return new DropTriggerStatement(this);
  } /* newDropTriggerStatement */
  
  public DropTypeStatement newDropTypeStatement()
  {
    return new DropTypeStatement(this);
  } /* newDropTypeStatement */
  
  public DropViewStatement newDropViewStatement()
  {
    return new DropViewStatement(this);
  } /* newDropViewStatement */
  
  public MethodDesignator newMethodDesignator()
  {
    return new MethodDesignator(this);
  } /* newMethodDesignator */
  
  public MethodSpecification newMethodSpecification()
  {
    return new MethodSpecification(this);
  } /* newMethodSpecification */
  
  public PartialMethodSpecification newPartialMethodSpecification()
  {
    return new PartialMethodSpecification(this);
  } /* newPartialMethodSpecification */
  
  public ReturnsClause newReturnsClause()
  {
    return new ReturnsClause(this);
  } /* newReturnsClause */
  
  public RoutineBody newRoutineBody()
  {
    return new RoutineBody(this);
  } /* newRoutineBody */
  
  public RoutineCharacteristics newRoutineCharacteristics()
  {
    return new RoutineCharacteristics(this);
  } /* newRoutineCharacteristics */
  
  public SqlParameterDeclaration newSqlParameterDeclaration()
  {
    return new SqlParameterDeclaration(this);
  } /* newSqlParameterDeclaration */
  
  public TableColumn newTableColumn()
  {
    return new TableColumn(this);
  } /* newTableColumn */
  
  public TableConstraintDefinition newTableConstraintDefinition()
  {
    return new TableConstraintDefinition(this);
  } /* newTableConstraintDefinition */
  
  public TableElement newTableElement()
  {
    return new TableElement(this);
  } /* newTableElement */
  
  public ViewElement newViewElement()
  {
    return new ViewElement(this);
  } /* newViewElement */
  
  public AssignedRow newAssignedRow()
  {
    return new AssignedRow(this);
  } /* newAssignedRow */
  
  public DeleteStatement newDeleteStatement()
  {
    return new DeleteStatement(this);
  } /* newDeleteStatement */
  
  public InsertStatement newInsertStatement()
  {
    return new InsertStatement(this);
  } /* newInsertStatement */
  
  public SetClause newSetClause()
  {
    return new SetClause(this);
  } /* newSetClause */
  
  public SetTarget newSetTarget()
  {
    return new SetTarget(this);
  } /* newSetTarget */
  
  public UpdateSource newUpdateSource()
  {
    return new UpdateSource(this);
  } /* newUpdateSource */
  
  public UpdateStatement newUpdateStatement()
  {
    return new UpdateStatement(this);
  } /* newUpdateStatement */
  
  public UpdateTarget newUpdateTarget()
  {
    return new UpdateTarget(this);
  } /* newUpdateTarget */
  
  public DdlStatement newDdlStatement()
  {
    return new DdlStatement(this);
  } /* newDdlStatement */
  
  public DmlStatement newDmlStatement()
  {
    return new DmlStatement(this);
  } /* newDmlStatement */
  
  public SqlStatement newSqlStatement()
  {
    return new SqlStatement(this);
  } /* SqlStatement */
  
} /* BaseSqlFactory */
