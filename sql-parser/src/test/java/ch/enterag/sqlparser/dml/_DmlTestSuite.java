package ch.enterag.sqlparser.dml;

import org.junit.runners.*;
import org.junit.runner.*;

@RunWith(Suite.class)
@Suite.SuiteClasses(
  {
    DeleteStatementTester.class, 
    InsertStatementTester.class,
    SetClauseTester.class,
    UpdateSourceTester.class,
    UpdateStatementTester.class
  })
public class _DmlTestSuite
{
}
