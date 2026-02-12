package ch.enterag.sqlparser.expression;

import org.junit.runners.*;
import org.junit.runner.*;

@RunWith(Suite.class)
@Suite.SuiteClasses(
  {
    AggregateFunctionTester.class, 
    ArrayValueExpressionTester.class,
    BooleanPrimaryTester.class,
    BooleanValueExpressionTester.class,
    CaseExpressionTester.class,
    CastSpecificationTester.class,
    CommonValueExpressionTester.class,
    DatetimeValueExpressionTester.class,
    GeneralValueSpecificationTester.class,
    IntervalValueExpressionTester.class,
    MultisetValueExpressionTester.class,
    NumericValueExpressionTester.class,
    QueryExpressionBodyTester.class,
    QueryExpressionTester.class,
    QuerySpecificationTester.class,
    RowValueExpressionTester.class,
    RowValuePredicandTester.class,
    SelectSublistTester.class,
    StringValueExpressionTester.class,
    StringValueFunctionTester.class,
    TablePrimaryTester.class,
    TableReferenceTester.class,
    UnsignedLiteralTester.class,
    ValueExpressionPrimaryTester.class,
    ValueExpressionTester.class,
    WhenOperandTester.class,
    WindowFrameBoundTester.class,
    WindowSpecificationTester.class
  })
public class _ExpressionTestSuite
{
}
