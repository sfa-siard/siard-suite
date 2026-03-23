package ch.enterag.sqlparser;

import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.FieldDefinition;
import ch.enterag.sqlparser.datatype.IntervalQualifier;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.dml.*;
import ch.enterag.sqlparser.expression.*;

public class BaseSqlFactory
        implements SqlFactory {
    private boolean _bAggregates = false;

    public boolean hasAggregates() {
        return _bAggregates;
    }

    public void setAggregates(boolean bAggregates) {
        _bAggregates = bAggregates;
    }

    private boolean _bCount = false;

    public boolean hasCount() {
        return _bCount;
    }

    public void setCount(boolean bCount) {
        _bCount = bCount;
    }

    public IntervalQualifier newIntervalQualifier() {
        return new IntervalQualifier(this);
    }

    public PredefinedType newPredefinedType() {
        return new PredefinedType(this);
    }

    public DataType newDataType() {
        return new DataType(this);
    }

    public FieldDefinition newFieldDefinition() {
        return new FieldDefinition(this);
    }

    public AggregateFunction newAggregateFunction() {
        return new AggregateFunction(this);
    }

    public ArrayValueExpression newArrayValueExpression() {
        return new ArrayValueExpression(this);
    }

    public BooleanPrimary newBooleanPrimary() {
        return new BooleanPrimary(this);
    }

    public BooleanValueExpression newBooleanValueExpression() {
        return new BooleanValueExpression(this);
    }

    public CaseExpression newCaseExpression() {
        return new CaseExpression(this);
    }

    public CastSpecification newCastSpecification() {
        return new CastSpecification(this);
    }

    public CommonValueExpression newCommonValueExpression() {
        return new CommonValueExpression(this);
    }

    public DatetimeValueExpression newDatetimeValueExpression() {
        return new DatetimeValueExpression(this);
    }

    public DatetimeValueFunction newDatetimeValueFunction() {
        return new DatetimeValueFunction(this);
    }

    public GeneralValueSpecification newGeneralValueSpecification() {
        return new GeneralValueSpecification(this);
    }

    public GroupingElement newGroupingElement() {
        return new GroupingElement(this);
    }

    public IntervalValueExpression newIntervalValueExpression() {
        return new IntervalValueExpression(this);
    }

    public Literal newLiteral() {
        return new Literal(this);
    }

    public MultisetValueExpression newMultisetValueExpression() {
        return new MultisetValueExpression(this);
    }

    public NumericValueExpression newNumericValueExpression() {
        return new NumericValueExpression(this);
    }

    public NumericValueFunction newNumericValueFunction() {
        return new NumericValueFunction(this);
    }

    public QueryExpression newQueryExpression() {
        return new QueryExpression(this);
    }

    public QueryExpressionBody newQueryExpressionBody() {
        return new QueryExpressionBody(this);
    }

    public QuerySpecification newQuerySpecification() {
        return new QuerySpecification(this);
    }

    public RowValueExpression newRowValueExpression() {
        return new RowValueExpression(this);
    }

    public RowValuePredicand newRowValuePredicand() {
        return new RowValuePredicand(this);
    }

    public SelectSublist newSelectSublist() {
        return new SelectSublist(this);
    }

    public SimpleValueSpecification newSimpleValueSpecification() {
        return new SimpleValueSpecification(this);
    }

    public SortSpecification newSortSpecification() {
        return new SortSpecification(this);
    }

    public SqlArgument newSqlArgument() {
        return new SqlArgument(this);
    }

    public StringValueExpression newStringValueExpression() {
        return new StringValueExpression(this);
    }

    public StringValueFunction newStringValueFunction() {
        return new StringValueFunction(this);
    }

    public SubtypeTreatment newSubtypeTreatment() {
        return new SubtypeTreatment(this);
    }

    public TablePrimary newTablePrimary() {
        return new TablePrimary(this);
    }

    public TableReference newTableReference() {
        return new TableReference(this);
    }

    public TableRowValueExpression newTableRowValueExpression() {
        return new TableRowValueExpression(this);
    }

    public TargetSpecification newTargetSpecification() {
        return new TargetSpecification(this);
    }

    public UnsignedLiteral newUnsignedLiteral() {
        return new UnsignedLiteral(this);
    }

    public ValueExpression newValueExpression() {
        return new ValueExpression(this);
    }

    public ValueExpressionPrimary newValueExpressionPrimary() {
        return new ValueExpressionPrimary(this);
    }

    public WhenOperand newWhenOperand() {
        return new WhenOperand(this);
    }

    public WindowFrameBound newWindowFrameBound() {
        return new WindowFrameBound(this);
    }

    public WindowFunction newWindowFunction() {
        return new WindowFunction(this);
    }

    public WindowSpecification newWindowSpecification() {
        return new WindowSpecification(this);
    }

    public WithElement newWithElement() {
        return new WithElement(this);
    }

    public AlterColumnAction newAlterColumnAction() {
        return new AlterColumnAction(this);
    }

    public AlterTableStatement newAlterTableStatement() {
        return new AlterTableStatement(this);
    }

    public AlterTypeStatement newAlterTypeStatement() {
        return new AlterTypeStatement(this);
    }

    public AttributeDefinition newAttributeDefinition() {
        return new AttributeDefinition(this);
    }

    public ColumnConstraintDefinition newColumnConstraintDefinition() {
        return new ColumnConstraintDefinition(this);
    }

    public ColumnDefinition newColumnDefinition() {
        return new ColumnDefinition(this);
    }

    public CreateFunctionStatement newCreateFunctionStatement() {
        return new CreateFunctionStatement(this);
    }

    public CreateMethodStatement newCreateMethodStatement() {
        return new CreateMethodStatement(this);
    }

    public CreateProcedureStatement newCreateProcedureStatement() {
        return new CreateProcedureStatement(this);
    }

    public CreateSchemaStatement newCreateSchemaStatement() {
        return new CreateSchemaStatement(this);
    }

    public CreateTableStatement newCreateTableStatement() {
        return new CreateTableStatement(this);
    }

    public CreateTriggerStatement newCreateTriggerStatement() {
        return new CreateTriggerStatement(this);
    }

    public CreateTypeStatement newCreateTypeStatement() {
        return new CreateTypeStatement(this);
    }

    public CreateViewStatement newCreateViewStatement() {
        return new CreateViewStatement(this);
    }

    public DropFunctionStatement newDropFunctionStatement() {
        return new DropFunctionStatement(this);
    }

    public DropMethodStatement newDropMethodStatement() {
        return new DropMethodStatement(this);
    }

    public DropProcedureStatement newDropProcedureStatement() {
        return new DropProcedureStatement(this);
    }

    public DropSchemaStatement newDropSchemaStatement() {
        return new DropSchemaStatement(this);
    }

    public DropTableStatement newDropTableStatement() {
        return new DropTableStatement(this);
    }

    public DropTriggerStatement newDropTriggerStatement() {
        return new DropTriggerStatement(this);
    }

    public DropTypeStatement newDropTypeStatement() {
        return new DropTypeStatement(this);
    }

    public DropViewStatement newDropViewStatement() {
        return new DropViewStatement(this);
    }

    public MethodDesignator newMethodDesignator() {
        return new MethodDesignator(this);
    }

    public MethodSpecification newMethodSpecification() {
        return new MethodSpecification(this);
    }

    public PartialMethodSpecification newPartialMethodSpecification() {
        return new PartialMethodSpecification(this);
    }

    public ReturnsClause newReturnsClause() {
        return new ReturnsClause(this);
    }

    public RoutineBody newRoutineBody() {
        return new RoutineBody(this);
    }

    public RoutineCharacteristics newRoutineCharacteristics() {
        return new RoutineCharacteristics(this);
    }

    public SqlParameterDeclaration newSqlParameterDeclaration() {
        return new SqlParameterDeclaration(this);
    }

    public TableColumn newTableColumn() {
        return new TableColumn(this);
    }

    public TableConstraintDefinition newTableConstraintDefinition() {
        return new TableConstraintDefinition(this);
    }

    public TableElement newTableElement() {
        return new TableElement(this);
    }

    public ViewElement newViewElement() {
        return new ViewElement(this);
    }

    public AssignedRow newAssignedRow() {
        return new AssignedRow(this);
    }

    public DeleteStatement newDeleteStatement() {
        return new DeleteStatement(this);
    }

    public InsertStatement newInsertStatement() {
        return new InsertStatement(this);
    }

    public SetClause newSetClause() {
        return new SetClause(this);
    }

    public SetTarget newSetTarget() {
        return new SetTarget(this);
    }

    public UpdateSource newUpdateSource() {
        return new UpdateSource(this);
    }

    public UpdateStatement newUpdateStatement() {
        return new UpdateStatement(this);
    }

    public UpdateTarget newUpdateTarget() {
        return new UpdateTarget(this);
    }

    public DdlStatement newDdlStatement() {
        return new DdlStatement(this);
    }

    public DmlStatement newDmlStatement() {
        return new DmlStatement(this);
    }

    public SqlStatement newSqlStatement() {
        return new SqlStatement(this);
    }

}
