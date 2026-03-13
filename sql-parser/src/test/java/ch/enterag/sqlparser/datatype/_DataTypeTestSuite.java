package ch.enterag.sqlparser.datatype;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                DataTypeTester.class,
                FieldDefinitionTester.class,
                PredefinedTypeTester.class
        })
public class _DataTypeTestSuite {
}
