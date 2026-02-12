package ch.admin.bar.siard2.jdbc;

import static org.junit.Assert.*;

import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.database.SqlTypes;

@Ignore("was not part of the TestSuite")
public class TransFormTester
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
      Statement stmt = _connDb2.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE).unwrap(Statement.class);
      /* drop table */
      String sSql = "DROP TABLE TA";
      try { stmt.executeUpdate(sSql); }
      catch(SQLException se) { stmt.clearWarnings(); _connDb2.clearWarnings(); }
      /* drop transform */
      sSql = "DROP TRANSFORMS ALL FOR TY";
      try { stmt.executeUpdate(sSql); }
      catch(SQLException se) { stmt.clearWarnings(); _connDb2.clearWarnings(); }
      /* drop function */
      sSql = "DROP FUNCTION TY_TO_STRING";
      try { stmt.executeUpdate(sSql); }
      catch(SQLException se) { stmt.clearWarnings(); _connDb2.clearWarnings(); }
      /* drop type */
      sSql = "DROP TYPE TY";
      try { stmt.executeUpdate(sSql); }
      catch(SQLException se) { stmt.clearWarnings(); _connDb2.clearWarnings(); }
      /* create type */
      sSql = "CREATE TYPE TY AS (AI INTEGER, AC CHAR) MODE DB2SQL";
      stmt.executeUpdate(sSql);
      _connDb2.commit();
      /* create function
      sSql = "CREATE FUNCTION TY_TO_STRING(IN T TY) RETURNS VARCHAR(5) LANGUAGE SQL RETURN CAST(T..AI AS VARCHAR(3)) || T..AC";
      stmt.executeUpdate(sSql);
      _connDb2.commit();
      /* create transform 
      sSql = "CREATE TRANSFORM FOR TY DB2_PROGRAM(FROM SQL WITH FUNCTION TY_TO_STRING)";
      stmt.executeUpdate(sSql);
      _connDb2.commit();
      /* create table */
      sSql = "CREATE TABLE TA(ID INTEGER NOT NULL, V TY, PRIMARY KEY(ID))";
      stmt.executeUpdate(sSql);
      _connDb2.commit();
      /* insert table */
      sSql = "INSERT INTO TA(ID, V) VALUES(4,TY() ..AI(5) ..AC('A'))";
      stmt.executeUpdate(sSql);
      _connDb2.commit();
      /* select table */
      sSql = "SELECT ID AS I, V..AI AS \"V.AI\", V..AC AS \"V.AC\" FROM TA";
      ResultSet rs = stmt.executeQuery(sSql);
      if (rs.getConcurrency() == ResultSet.CONCUR_UPDATABLE)
        System.out.println("Updatable");
      else
        System.out.println("Read Only");
      ResultSetMetaData rsmd = rs.getMetaData();
      for (int iColumn = 1; iColumn <= rsmd.getColumnCount(); iColumn++)
      {
        System.out.print(String.valueOf(iColumn)+": ");
        System.out.println("Name: "+rsmd.getColumnName(iColumn));
        System.out.println("Label: "+rsmd.getColumnLabel(iColumn));
        System.out.println("Class: "+rsmd.getColumnClassName(iColumn));
        System.out.println("Type: "+SqlTypes.getTypeName(rsmd.getColumnType(iColumn)));
        System.out.println("TypeName: "+rsmd.getColumnTypeName(iColumn));
        System.out.println();
      }
      while(rs.next())
      {
        int id = rs.getInt(1);
        int iAi = rs.getInt(2);
        String sAc = rs.getString(3);
        System.out.println(String.valueOf(id)+": "+String.valueOf(iAi)+", "+sAc);
        rs.updateInt(1,7);
        rs.updateInt(2,6); // fails!
        rs.updateString(3,"B"); // fails!
      }
      rs.close();
      rs = stmt.executeQuery(sSql);
      while(rs.next())
      {
        int id = rs.getInt(1);
        int iAi = rs.getInt(2);
        String sAc = rs.getString(2);
        System.out.println(String.valueOf(id)+": "+String.valueOf(iAi)+", "+sAc);
      }
      rs.close();
      stmt.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

}
