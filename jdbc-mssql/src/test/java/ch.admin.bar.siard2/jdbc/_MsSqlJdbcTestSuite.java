package ch.admin.bar.siard2.jdbc;

import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Suite.class)
@Suite.SuiteClasses(
  {
    MsSqlDriverTester.class,
    MsSqlConnectionTester.class,
    MsSqlDatabaseMetaDataTester.class,
    MsSqlResultSetMetaDataTester.class,
    MsSqlResultSetTester.class,
    MsSqlStatementTester.class
  })
public class _MsSqlJdbcTestSuite
{
}
