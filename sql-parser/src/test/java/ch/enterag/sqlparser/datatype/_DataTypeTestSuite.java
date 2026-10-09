package ch.enterag.sqlparser.datatype;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(
        {
                DataTypeTester.class,
                FieldDefinitionTester.class,
                PredefinedTypeTester.class
        })
public class _DataTypeTestSuite {
}
