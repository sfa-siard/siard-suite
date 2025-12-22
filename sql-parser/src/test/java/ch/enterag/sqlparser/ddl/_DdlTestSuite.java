package ch.enterag.sqlparser.ddl;

import org.junit.runners.*;
import org.junit.runner.*;

@RunWith(Suite.class)
@Suite.SuiteClasses(
  {
    AttributeDefinitionTester.class, 
    ColumnConstraintDefinitionTester.class,
    ColumnDefinitionTester.class,
    CreateFunctionStatementTester.class,
    CreateMethodStatementTester.class,
    CreateProcedureStatementTester.class,
    CreateSchemaStatementTester.class,
    CreateTableStatementTester.class,
    CreateTriggerStatementTester.class,
    CreateTypeStatementTester.class,
    CreateViewStatementTester.class,
    DropFunctionStatementTester.class,
    DropMethodStatementTester.class,
    DropProcedureStatementTester.class,
    DropSchemaStatementTester.class,
    DropTableStatementTester.class,
    DropTriggerStatementTester.class,
    DropTypeStatementTester.class,
    DropViewStatementTester.class,
    MethodSpecificationTester.class,
    TableConstraintDefinitionTester.class,
    TableElementTester.class
  })
public class _DdlTestSuite
{
}
