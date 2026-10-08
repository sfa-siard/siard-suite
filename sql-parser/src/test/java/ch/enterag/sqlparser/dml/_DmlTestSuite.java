package ch.enterag.sqlparser.dml;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(
        {
                DeleteStatementTester.class,
                InsertStatementTester.class,
                SetClauseTester.class,
                UpdateSourceTester.class,
                UpdateStatementTester.class
        })
public class _DmlTestSuite {
}
