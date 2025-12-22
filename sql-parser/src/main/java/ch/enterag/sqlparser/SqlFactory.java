package ch.enterag.sqlparser;

import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.dml.*;
import ch.enterag.sqlparser.expression.*;

public interface SqlFactory
{
  /*------------------------------------------------------------------*/
  /** set indicator that COUNT(*) aggregate function has been encountered.
   * @param bCount true, if COUNT(*) aggregate function has been encountered. */
  public void setCount(boolean bCount);
  /*------------------------------------------------------------------*/
  /** check indicator, whether COUNT(*) aggregate functions have been encountered
   * since they were reset last. 
   * @return true, if COUNT(*) aggregate functions were encountered. */
  public boolean hasCount();
  
  /*------------------------------------------------------------------*/
  /** set indicator that aggregate function has been encountered.
   * @param bAggregates true, if aggregate function has been encountered. */
  public void setAggregates(boolean bAggregates);
  /*------------------------------------------------------------------*/
  /** check indicator, whether aggregate functions have been encountered
   * since they were reset last. 
   * @return true, if aggregate functions were encountered. */
  public boolean hasAggregates();
  
  /* IntervalQualifier */
  public IntervalQualifier newIntervalQualifier();
  /* PredefinedType */
  public PredefinedType newPredefinedType();
  /* DataType */
  public DataType newDataType();
  /* FieldDefinition */
  public FieldDefinition newFieldDefinition();
  /* AggregateFunction */
  public AggregateFunction newAggregateFunction();
  /* ArrayValueExpression */
  public ArrayValueExpression newArrayValueExpression();
  /* BooleanPrimary */
  public BooleanPrimary newBooleanPrimary();
  /* BooleanValueExpression */
  public BooleanValueExpression newBooleanValueExpression();
  /* CaseExpression */
  public CaseExpression newCaseExpression();
  /* CastSpecification */
  public CastSpecification newCastSpecification();
  /* CommonValueExpression */
  public CommonValueExpression newCommonValueExpression();
  /* DatetimeValueExpression */
  public DatetimeValueExpression newDatetimeValueExpression();
  /* DatetimeValueFunction */
  public DatetimeValueFunction newDatetimeValueFunction();
  /* GeneralValueSpecification */
  public GeneralValueSpecification newGeneralValueSpecification();
  /* GroupingElement */
  public GroupingElement newGroupingElement();
  /* IntervalValueExpression */
  public IntervalValueExpression newIntervalValueExpression();
  /* Literal */
  public Literal newLiteral();
  /* MultisetValueExpression */
  public MultisetValueExpression newMultisetValueExpression();
  /* NumericValueExpression */
  public NumericValueExpression newNumericValueExpression();
  /* NumericValueFunction */
  public NumericValueFunction newNumericValueFunction();
  /* QueryExpression */
  public QueryExpression newQueryExpression();
  /* QueryExpressionBody */
  public QueryExpressionBody newQueryExpressionBody();
  /* QuerySpecification */
  public QuerySpecification newQuerySpecification();
  /* RowValueExpression */
  public RowValueExpression newRowValueExpression();
  /* RowValuePredicand */
  public RowValuePredicand newRowValuePredicand();
  /* SelectSublist */
  public SelectSublist newSelectSublist();
  /* SimpleValueSpecification */
  public SimpleValueSpecification newSimpleValueSpecification();
  /* SortSpecification */
  public SortSpecification newSortSpecification();
  /* SqlArgument */
  public SqlArgument newSqlArgument();
  /* StringValueExpression */
  public StringValueExpression newStringValueExpression();
  /* StringValueFunction */
  public StringValueFunction newStringValueFunction();
  /* SubtypeTreatment */
  public SubtypeTreatment newSubtypeTreatment();
  /* TablePrimary */
  public TablePrimary newTablePrimary();
  /* TableReference */
  public TableReference newTableReference();
  /* TableRowValueExpression */
  public TableRowValueExpression newTableRowValueExpression();
  /* TargetSpecification */
  public TargetSpecification newTargetSpecification();
  /* UnsignedLiteral */
  public UnsignedLiteral newUnsignedLiteral();
  /* ValueExpression */
  public ValueExpression newValueExpression();
  /* ValueExpressionPrimary */
  public ValueExpressionPrimary newValueExpressionPrimary();
  /* WhenOperand */
  public WhenOperand newWhenOperand();
  /* WindowFrameBound */
  public WindowFrameBound newWindowFrameBound();
  /* WindowFunction */
  public WindowFunction newWindowFunction();
  /* WindowSpecification */
  public WindowSpecification newWindowSpecification();
  /* WithElement */
  public WithElement newWithElement();
  /* AlterColumnAction */
  public AlterColumnAction newAlterColumnAction();
  /* AlterTableStatement */
  public AlterTableStatement newAlterTableStatement();
  /* AlterTypeStatement */
  public AlterTypeStatement newAlterTypeStatement();
  /* AttributeDefinition */
  public AttributeDefinition newAttributeDefinition();
  /* ColumnConstraintDefinition */
  public ColumnConstraintDefinition newColumnConstraintDefinition();
  /* ColumnDefinition */
  public ColumnDefinition newColumnDefinition();
  /* CreateFunctionStatement */
  public CreateFunctionStatement newCreateFunctionStatement();
  /* CreateMethodStatement */
  public CreateMethodStatement newCreateMethodStatement();
  /* CreateProcedureStatement */
  public CreateProcedureStatement newCreateProcedureStatement();
  /* CreateSchemaStatement */
  public CreateSchemaStatement newCreateSchemaStatement();
  /* CreateTableStatement */
  public CreateTableStatement newCreateTableStatement();
  /* CreateTriggerStatement */
  public CreateTriggerStatement newCreateTriggerStatement();
  /* CreateTypeStatement */
  public CreateTypeStatement newCreateTypeStatement();
  /* CreateViewStatement */
  public CreateViewStatement newCreateViewStatement();
  /* DropFunctionStatement */
  public DropFunctionStatement newDropFunctionStatement();
  /* DropMethodStatement */
  public DropMethodStatement newDropMethodStatement();
  /* DropProcedureStatement */
  public DropProcedureStatement newDropProcedureStatement();
  /* DropSchemaStatement */
  public DropSchemaStatement newDropSchemaStatement();
  /* DropTableStatement */
  public DropTableStatement newDropTableStatement();
  /* DropTriggerStatement */
  public DropTriggerStatement newDropTriggerStatement();
  /* DropTypeStatement */
  public DropTypeStatement newDropTypeStatement();
  /* DropViewStatement */
  public DropViewStatement newDropViewStatement();
  /* MethodDesignator */
  public MethodDesignator newMethodDesignator();
  /* MethodSpecification */
  public MethodSpecification newMethodSpecification();
  /* PartialMethodSpecification */
  public PartialMethodSpecification newPartialMethodSpecification();
  /* ReturnsClause */
  public ReturnsClause newReturnsClause();
  /* RoutineBody */
  public RoutineBody newRoutineBody();
  /* RoutineCharacteristics */
  public RoutineCharacteristics newRoutineCharacteristics();
  /* SqlParameterDeclaration */
  public SqlParameterDeclaration newSqlParameterDeclaration();
  /* TableColumn */
  public TableColumn newTableColumn();
  /* TableConstraintDefinition */
  public TableConstraintDefinition newTableConstraintDefinition();
  /* TableElement */
  public TableElement newTableElement();
  /* ViewElement */
  public ViewElement newViewElement();
  /* AssignedRow */
  public AssignedRow newAssignedRow();
  /* DeleteStatement */
  public DeleteStatement newDeleteStatement();
  /* InsertStatement */
  public InsertStatement newInsertStatement();
  /* SetClause */
  public SetClause newSetClause();
  /* SetTarget */
  public SetTarget newSetTarget();
  /* UpdateSource */
  public UpdateSource newUpdateSource();
  /* UpdateStatement */
  public UpdateStatement newUpdateStatement();
  /* UpdateTarget */
  public UpdateTarget newUpdateTarget();
  /* DdlStatement */
  public DdlStatement newDdlStatement();
  /* DmlStatement */
  public DmlStatement newDmlStatement();
  /* SqlStatement */
  public SqlStatement newSqlStatement();
}
