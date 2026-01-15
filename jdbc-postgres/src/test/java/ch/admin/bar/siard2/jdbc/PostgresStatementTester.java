package ch.admin.bar.siard2.jdbc;

import java.io.IOException;
import java.sql.*;

import org.junit.*;
import static org.junit.Assert.*;

import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;
import ch.admin.bar.siard2.postgres.*;
import ch.admin.bar.siard2.jdbcx.*;
import org.testcontainers.containers.PostgreSQLContainer;

@Ignore
public class PostgresStatementTester extends BaseStatementTester
{
  private static final PostgreSQLContainer<?> _pg = new PostgreSQLContainer<>("postgres:16-alpine")
    .withDatabaseName("postgres")
    .withUsername("postgres")
    .withPassword("postgres");

  private static String _sDB_URL;
  private static String _sDB_USER;
  private static String _sDB_PASSWORD;
  private static String _sDBA_USER;
  private static String _sDBA_PASSWORD;

  @AfterClass
  public static void stopContainer()
  {
    _pg.stop();
  }

  @BeforeClass
  public static void setUpClass()
  {
    _pg.start();
    _sDB_URL = PostgresDriver.getUrl(_pg.getHost()+":"+_pg.getFirstMappedPort()+"/"+_pg.getDatabaseName());
    _sDB_USER = _pg.getUsername();
    _sDB_PASSWORD = _pg.getPassword();
    _sDBA_USER = _pg.getUsername();
    _sDBA_PASSWORD = _pg.getPassword();
    try 
    { 
      PostgresDataSource dsPostgres = new PostgresDataSource();
      dsPostgres.setUrl(_sDB_URL);
      dsPostgres.setUser(_sDBA_USER);
      dsPostgres.setPassword(_sDBA_PASSWORD);
      PostgresConnection connPostgres = (PostgresConnection)dsPostgres.getConnection();
      /* drop and create the test databases */
      new TestSqlDatabase(connPostgres,_sDB_USER);
      TestPostgresDatabase.grantSchemaUser(connPostgres, TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
      new TestPostgresDatabase(connPostgres,_sDB_USER);
      TestPostgresDatabase.grantSchemaUser(connPostgres, TestPostgresDatabase._sTEST_SCHEMA, _sDB_USER);
      connPostgres.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* setUpClass */
  
  @Before
  public void setUp()
  {
    try 
    { 
      PostgresDataSource dsPostgres = new PostgresDataSource();
      dsPostgres.setUrl(_sDB_URL);
      dsPostgres.setUser(_sDB_USER);
      dsPostgres.setPassword(_sDB_PASSWORD);
      PostgresConnection connPostgres = (PostgresConnection)dsPostgres.getConnection();
      connPostgres.setAutoCommit(false);
      PostgresStatement stmtPostgres = (PostgresStatement)connPostgres.createStatement();
      setStatement(stmtPostgres);
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* setUp */
  
  @Test
  public void testClass()
  {
    assertEquals("Wrong statement class!", PostgresStatement.class, getStatement().getClass());
  } /* testClass */

} /* class PostgresStatementTester */
