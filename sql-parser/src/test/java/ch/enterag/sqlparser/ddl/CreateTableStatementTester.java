package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class CreateTableStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CreateTableStatement _cts = null;

  @Before
  public void setUp()
  {
    _cts = _sf.newCreateTableStatement();
  }
  
  @Test
  public void testSimple()
  {
    _cts.parse("CREATE table test(id int, ch char(5))");
    // System.out.println(_cts.format());
    String sExpected = "CREATE TABLE TEST(\r\n" +
      "  ID INT,\r\n" +
      "  CH CHAR(5)\r\n" +
      ")";
    assertEquals("Simple create table statement not recognized!",sExpected,_cts.format());
  }

  @Test
  public void testNormal()
  {
    String sInput = "create table sch.test(\r\n" +
      "  id INT NOT NULL,\r\n" +
      "  \"Note\" varchar(255),\r\n" +
      "  mydate date,\r\n" +
      "  PRIMARY KEY(id, mydate),\r\n"+
      "  FOREIGN KEY (id) REFERENCES other(\"idOther\")\r\n"+
      ")";
    _cts.parse(sInput);
    // System.out.println(_cts.format());
    String sExpected = "CREATE TABLE SCH.TEST(\r\n" +
      "  ID INT NOT NULL,\r\n" +
      "  \"Note\" VARCHAR(255),\r\n" +
      "  MYDATE DATE,\r\n" +
      "  PRIMARY KEY(ID, MYDATE),\r\n" +
      "  FOREIGN KEY(ID) REFERENCES OTHER(\"idOther\")\r\n" +
      ")";
    assertEquals("Normal create table statement not recognized!",sExpected,_cts.format());
  }
  
  @Test
  public void testComplex()
  {
    String sInput = "create local temporary table \"cat\".sch.test of someType under supie (\r\n" +
        "  PRIMARY KEY(id, mydate),\r\n"+
        "  FOREIGN KEY (id) REFERENCES other(\"idOther\")\r\n"+
        ")";
    _cts.parse(sInput);
    // System.out.println(_cts.format());
    String sExpected = "CREATE LOCAL TEMPORARY TABLE \"cat\".SCH.TEST OF SOMETYPE UNDER SUPIE(\r\n"+
      "  PRIMARY KEY(ID, MYDATE),\r\n"+
      "  FOREIGN KEY(ID) REFERENCES OTHER(\"idOther\")\r\n"+
      ")";
    assertEquals("Complex create table statement not recognized!",sExpected,_cts.format());
  }
  
  @Test
  public void testQuery()
  {
    String sInput = "create table \"cat\".sch.testquery(a1, b1, c1) as (select * from testtable) WITH NO DATA";
    _cts.parse(sInput);
    System.out.println(_cts.format());
    String sExpected = "CREATE TABLE \"cat\".SCH.TESTQUERY(A1, B1, C1) AS(SELECT *\r\n"+
        "FROM TESTTABLE) WITH NO DATA";
    assertEquals("Complex table statement from query not recognized!",sExpected,_cts.format());
  }
}
