package ch.admin.bar.siard2.jdbc.legacy;

import static org.junit.Assert.*;

import java.sql.*;

import ch.admin.bar.siard2.jdbc.OracleConnection;
import ch.admin.bar.siard2.jdbc.OracleDriver;
import org.junit.*;

import ch.admin.bar.siard2.jdbcx.*;
import ch.enterag.utils.*;

@Ignore("seems to depend on setup @enterag")
public class OeMetaDataTester
{
  private static final String _sDB_URL = OracleDriver.getUrl("vmw10.enterag.ch:1521:orcl");
  private static final String _sDB_USER = "OE";
  private static final String _sDB_PASSWORD = "OEPWD";

  private DatabaseMetaData _dmd = null;
  
  @Before
  public void setUp() 
  {
    try {
      OracleDataSource dsOracle = new OracleDataSource();
      dsOracle.setUrl(_sDB_URL);
      dsOracle.setUser(_sDB_USER);
      dsOracle.setPassword(_sDB_PASSWORD);
      OracleConnection connOracle = (OracleConnection) dsOracle.getConnection();
      connOracle.setAutoCommit(false);
      _dmd = connOracle.getMetaData();
    }
    catch(SQLException se) { fail(se.getClass().getName()+": " + se.getMessage()); }
  }

  @After
  public void tearDown()
  {
    try
    {
      Connection conn = _dmd.getConnection();
      if ((conn != null) && (!conn.isClosed()))
      {
        conn.commit();
        conn.close();
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* tearDown */
  
  @Test
  public void test()
  {
    try
    {
      String sProcedureName = "GET_PHONE_NUMBER_F";
      ResultSet rsParm = _dmd.getProcedureColumns(null, _sDB_USER, sProcedureName, "%");
      while (rsParm.next())
      {
        String sParameterName =rsParm.getString("COLUMN_NAME");
        System.out.println(sParameterName);
      }
      rsParm.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

}
