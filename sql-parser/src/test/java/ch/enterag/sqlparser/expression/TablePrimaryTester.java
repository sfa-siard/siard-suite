package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class TablePrimaryTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private TablePrimary _tp = null;

  @Before
  public void setUp()
  {
    _tp = _sf.newTablePrimary();
  }

  @Test
  public void testPlainTableName()
  {
    // ErrorListener.getInstance().suppressException();
    _tp.parse("T1");
    // System.out.println(_tp.format());
    assertEquals("Plain table name not recognized!","T1",_tp.format());
  }

  @Test
  public void testTableAlias()
  {
    // ErrorListener.getInstance().suppressException();
    _tp.parse("\"table\" T1");
    // System.out.println(_tp.format());
    assertEquals("Plain alias not recognized!","\"table\" AS T1",_tp.format());
  }

  @Test
  public void testQuery()
  {
    // ErrorListener.getInstance().suppressException();
    _tp.parse("(SELECT * FROM T)");
    // System.out.println(_tp.format());
    assertEquals("Query not recognized!","(SELECT *\r\nFROM T)",_tp.format());
  }

  @Test
  public void testQueryWithAlias()
  {
    // ErrorListener.getInstance().suppressException();
    _tp.parse("(SELECT * FROM T) T1(A1, B1, C1)");
    // System.out.println(_tp.format());
    assertEquals("Query not recognized!","(SELECT *\r\nFROM T) AS T1(A1, B1, C1)",_tp.format());
  }

  @Test
  public void testUnnest()
  {
    _tp.parse("UNNEST(array1) WITH ORDINALITY T1(A1, B1, C1)");
    // System.out.println(_tp.format());
    assertEquals("UNNEST not recognized!","UNNEST(ARRAY1) WITH ORDINALITY AS T1(A1, B1, C1)",_tp.format());
  }

  @Test
  public void testTable()
  {
    _tp.parse("TABLE(ms1) T1");
    System.out.println(_tp.format());
    assertEquals("TABLE not recognized!","TABLE(MS1) AS T1",_tp.format());
  }

  @Test
  public void testOnly()
  {
    _tp.parse("ONLY(ms1) T1");
    System.out.println(_tp.format());
    assertEquals("ONLY not recognized!","ONLY(MS1) AS T1",_tp.format());
  }

}
