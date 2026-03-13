package ch.enterag.sqlparser.dml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                DeleteStatementTester.class,
                InsertStatementTester.class,
                SetClauseTester.class,
                UpdateSourceTester.class,
                UpdateStatementTester.class
        })
public class _DmlTestSuite {
}
