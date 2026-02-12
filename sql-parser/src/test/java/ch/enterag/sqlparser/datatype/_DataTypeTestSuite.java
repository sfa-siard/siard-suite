package ch.enterag.sqlparser.datatype;

import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Suite.class)
@Suite.SuiteClasses(
  {
    DataTypeTester.class, 
    FieldDefinitionTester.class,
    PredefinedTypeTester.class
  })
public class _DataTypeTestSuite
{
}
