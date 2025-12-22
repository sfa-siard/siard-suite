package ch.admin.bar.siard2.jdbc;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.junit.*;

import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.mysql.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class MySqlBlobTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = MySqlDriver.getUrl(_cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog(),true);
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();
  
  private static List<String> listPngs = new ArrayList<String>();
  private static List<String> listFlacs = new ArrayList<String>();
  
  private MySqlConnection _connMySql = null;
  
  @BeforeClass 
  public static void setUpClass()
  {
    for (int iRecord = 0; _cp.getBlobPng(iRecord) != null; iRecord++)
      listPngs.add(_cp.getBlobPng(iRecord));
    for (int iRecord = 0; _cp.getBlobFlac(iRecord) != null; iRecord++)
      listFlacs.add(_cp.getBlobFlac(iRecord));
    try
    {
      MySqlDataSource dsMySql = new MySqlDataSource();
      dsMySql.setUrl(_sDB_URL);
      dsMySql.setUser(_sDBA_USER);
      dsMySql.setPassword(_sDBA_PASSWORD);
      MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
      new TestBlobDatabase(connMySql,listPngs,listFlacs);
      TestMySqlDatabase.grantSchemaUser(connMySql, 
        TestBlobDatabase._sTEST_SCHEMA, _sDB_USER);
      connMySql.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* setUpClass */

  
  @Before
  public void setUp() 
  {
    try 
    {
      MySqlDataSource dsMySql = new MySqlDataSource();
      dsMySql.setUrl(_sDB_URL);
      dsMySql.setUser(_sDB_USER);
      dsMySql.setPassword(_sDB_PASSWORD);
      _connMySql = (MySqlConnection) dsMySql.getConnection();
      _connMySql.setAutoCommit(false);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUp */

  @After
  public void tearDown()
  {
    try
    {
      if ((_connMySql != null) && (!_connMySql.isClosed()))
      {
        _connMySql.commit();
        _connMySql.close();
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* tearDown */
  
  @Test
  public void testSizes() 
  {
    System.out.println();
    System.out.println("testSizes");
    try
    {
      /* query rows and LOB sizes */
      String sQuery = "SELECT COUNT(*) AS RECORDS";
      sQuery = sQuery + ",\r\n SUM(OCTET_LENGTH(CPNG)) AS CPNG_SIZE";
      sQuery = sQuery + ",\r\n SUM(OCTET_LENGTH(CFLAC)) AS CFLAC_SIZE";
      QualifiedId qiTable = TestBlobDatabase.getQualifiedSimpleTable();
      sQuery = sQuery + "\r\nFROM "+qiTable.format();
      Statement stmtSizes = _connMySql.createStatement();
      stmtSizes.setQueryTimeout(30);
      ResultSet rsSizes = stmtSizes.executeQuery(sQuery);
      ResultSetMetaData rsmd = rsSizes.getMetaData();
      if (rsSizes.next())
      {
        long lRows = rsSizes.getLong("RECORDS");
        System.out.println("  Rows: "+String.valueOf(lRows));
        for (int iLob = 0; iLob < 2; iLob++)
        {
          String sLobName = rsmd.getColumnLabel(iLob+2);
          long lLobSize = rsSizes.getLong(sLobName);
          System.out.println("  "+sLobName+": "+String.valueOf(lLobSize));
        }
      }
      else
        throw new IOException("Sizes of table "+qiTable.format()+" could not be determined!");
      rsSizes.close();
      stmtSizes.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

  @Test
  public void testDownload() 
  {
    System.out.println();
    System.out.println("testDownload");
    try
    {
      /* download table */
      String sQuery = "SELECT CINT";
      sQuery = sQuery + ",\r\n CPNG";
      sQuery = sQuery + ",\r\n CFLAC";
      QualifiedId qiTable = TestBlobDatabase.getQualifiedSimpleTable();
      sQuery = sQuery + "\r\nFROM "+qiTable.format();
      Statement stmt = _connMySql.createStatement();
      stmt.setQueryTimeout(300);
      ResultSet rs = stmt.executeQuery(sQuery);
      while (rs.next())
      {
        int iRecord = rs.getInt("CINT");
        
        byte[] buf = new byte[8192];
        
        InputStream is = rs.getBinaryStream("CPNG");
        long lPngSize = 0;
        for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
          lPngSize += iRead;
        is.close();
        
        is = rs.getBinaryStream("CFLAC");
        long lFlacSize = 0;
        for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
          lFlacSize += iRead;
        is.close();
        System.out.println("  Record: "+String.valueOf(iRecord)+": PNG "+String.valueOf(lPngSize)+", FLAC "+String.valueOf(lFlacSize));
        
      }
      rs.close();
      stmt.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

}
