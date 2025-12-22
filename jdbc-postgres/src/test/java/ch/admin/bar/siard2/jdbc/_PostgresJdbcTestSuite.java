package ch.admin.bar.siard2.jdbc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	PostgresDriverTester.class,
	PostgresConnectionTester.class,
	PostgresDatabaseMetaDataTester.class,
	PostgresResultSetMetaDataTester.class,
	PostgresResultSetTester.class,
  	PostgresStatementTester.class
})
public class _PostgresJdbcTestSuite {

}
