package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class QueryExpressionBodyTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private QueryExpressionBody _qeb = null;

  @Before
  public void setUp()
  {
    _qeb = _sf.newQueryExpressionBody();
  }

  @Test
  public void testTableReference()
  {
    _qeb.parse("T1 AS A1 CROSS JOIN T2 A2");
    // System.out.println(_qeb.format());
    assertEquals("Table reference not recognized!","T1 AS A1 CROSS JOIN T2 AS A2",_qeb.format());
  }

  @Test
  public void testIntersect()
  {
    _qeb.parse("(T1 AS A1 CROSS JOIN T2 A2) INTERSECT (SELECT COUNT(*) FROM C1.S1.T1)");
    // System.out.println(_qeb.format());
    assertEquals("Table reference not recognized!","T1 AS A1 CROSS JOIN T2 AS A2 INTERSECT (SELECT\r\n  COUNT(*)\r\nFROM C1.S1.T1)",_qeb.format());
  }

  @Test
  public void testUnion()
  {
    _qeb.parse("T1 AS A1 UNION ALL (SELECT COUNT(*) FROM C1.S1.T1)");
    // System.out.println(_qeb.format());
    assertEquals("Table reference not recognized!","T1 AS A1 UNION ALL (SELECT\r\n  COUNT(*)\r\nFROM C1.S1.T1)",_qeb.format());
  }

  @Test
  public void testExcept()
  {
    _qeb.parse("T1 EXCEPT ALL CORRESPONDING BY (ID, TDATE, SNAME) T2");
    // System.out.println(_qeb.format());
    assertEquals("Table reference not recognized!","T1 EXCEPT ALL CORRESPONDING BY(ID, TDATE, SNAME) T2",_qeb.format());
  }

  @Test
  public void testQueryExpression()
  {
    _qeb.parse("SELECT COUNT(*) FROM C1.S1.T1");
    // System.out.println(_qeb.format());
    assertEquals("Count query not recognized!","SELECT\r\n  COUNT(*)\r\nFROM C1.S1.T1",_qeb.format());
  }

  @Test
  public void testParenthesized()
  {
    _qeb.parse("((T1 AS A1 CROSS JOIN T2 A2) INTERSECT (SELECT COUNT(*) FROM C1.S1.T1))");
    // System.out.println(_qeb.format());
    assertEquals("Parenthesized table reference not recognized!","(T1 AS A1 CROSS JOIN T2 AS A2 INTERSECT (SELECT\r\n  COUNT(*)\r\nFROM C1.S1.T1))",_qeb.format());
  }

  @Test
  public void testTable()
  {
    _qeb.parse("TABLE C1.S1.T1");
    // System.out.println(_qeb.format());
    assertEquals("TABLE not recognized!","TABLE C1.S1.T1",_qeb.format());
  }

  @Test
  public void testValues()
  {
    _qeb.parse("VALUES(C1.S1.T1,5,DATE'2016-04-29','aaa')");
    // System.out.println(_qeb.format());
    assertEquals("VALUES not recognized!","VALUES(C1.S1.T1, 5, DATE'2016-04-29', 'aaa')",_qeb.format());
  }
}
