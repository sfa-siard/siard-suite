package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

@Ignore("was not part of the TestSuite")
public class InsertRowTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = "jdbc:db2://"+_cp.getHost()+":"+_cp.getPort()+"/"+_cp.getCatalog();
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  // private static final String _sDBA_USER = _cp.getDbaUser();
  // private static final String _sDBA_PASSWORD = _cp.getDbaPassword();

  private Db2Connection _connDb2 = null;
  
  @Before
  public void setUp()
  {
    try
    {
      Db2DataSource dsDb2 = new Db2DataSource();
      dsDb2.setUrl(_sDB_URL);
      dsDb2.setUser(_sDB_USER);
      dsDb2.setPassword(_sDB_PASSWORD);
      _connDb2 = (Db2Connection)dsDb2.getConnection();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @After
  public void tearDown()
  {
    try
    {
      _connDb2.commit();
      _connDb2.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void test()
  {
    try
    {
      StringBuilder sbSql = new StringBuilder("DROP TABLE TDB2COMPLEX");
      Statement stmt = _connDb2.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, ResultSet.HOLD_CURSORS_OVER_COMMIT);
      int iResult = stmt.unwrap(Statement.class).executeUpdate(sbSql.toString());
      assertEquals("DROP TABLE failed!",0,iResult);
      sbSql = new StringBuilder("CREATE TABLE TDB2COMPLEX\r\n");
      sbSql.append("(\r\n");
      sbSql.append("  CID INTEGER,\r\n"); 
      sbSql.append("  \"CSTRUCT.AINTEGER\" INTEGER,\r\n");
      sbSql.append("  \"CSTRUCT.ACHAR_5\" CHAR(5 OCTETS)\r\n"); 
      sbSql.append(")");
      iResult = stmt.unwrap(Statement.class).executeUpdate(sbSql.toString());
      assertEquals("CREATE TABLE failed!",0,iResult);
      sbSql = new StringBuilder("SELECT\r\n");
      sbSql.append("  CID,\r\n"); 
      sbSql.append("  \"CSTRUCT.AINTEGER\",\r\n");
      sbSql.append("  \"CSTRUCT.ACHAR_5\"\r\n");
      sbSql.append("FROM TDB2COMPLEX");
      ResultSet rs = stmt.executeQuery(sbSql.toString());
      rs.moveToInsertRow();
      rs.updateInt("CID", 123);
      rs.updateInt("CSTRUCT.AINTEGER", 321);
      rs.updateString("CSTRUCT.ACHAR_5","ABCD");
      rs.insertRow();
      rs.moveToCurrentRow();
      rs.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

} /* InsertRowTester */
