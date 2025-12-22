package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class WhenOperandTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private WhenOperand _wo = null;

  @Before
  public void setUp()
  {
    _wo = _sf.newWhenOperand();
  }

  @Test
  public void testMinusOne()
  {
    // ErrorListener.getInstance().suppressException();
    _wo.parse("-1");
    // System.out.println(_wo.format());
    assertEquals("-1 not recognized!","-1",_wo.format());
  }
  
  @Test
  public void testComparison()
  {
    // ErrorListener.getInstance().suppressException();
    _wo.parse(">= t.a1");
    // System.out.println(_wo.format());
    assertEquals("Comparison not recognized!",">= T.A1",_wo.format());
  }

  @Test
  public void testBetween()
  {
    _wo.parse("BETWEEN A1 and B");
    // System.out.println(_wo.format());
    assertEquals("BETWEEN not recognized!","BETWEEN A1 AND B",_wo.format());
  }

  @Test
  public void testIn()
  {
    _wo.parse("NOT IN (SELECT A1 FROM t)");
    // System.out.println(_wo.format());
    assertEquals("IN not recognized!","NOT IN (SELECT\r\n  A1\r\nFROM T)",_wo.format());
  }

  @Test
  public void testLike()
  {
    _wo.parse("LIKE 'aa%' ESCAPE '\\'");
    // System.out.println(_wo.format());
    assertEquals("LIKE not recognized!","LIKE 'aa%' ESCAPE '\\'",_wo.format());
  }

  @Test
  public void testSimilar()
  {
    _wo.parse("SIMILAR TO 'aa%' ESCAPE '\\'");
    // System.out.println(_wo.format());
    assertEquals("SIMILAR not recognized!","SIMILAR TO 'aa%' ESCAPE '\\'",_wo.format());
  }

  @Test
  public void testIsNull()
  {
    _wo.parse("IS NULL");
    // System.out.println(_wo.format());
    assertEquals("IS NULL not recognized!","IS NULL",_wo.format());
  }

  @Test
  public void testQuantified()
  {
    _wo.parse("<= some (select a1 from t2)");
    // System.out.println(_wo.format());
    assertEquals("Quantified comparison not recognized!","<= SOME(SELECT\r\n  A1\r\nFROM T2)",_wo.format());
  }

  @Test
  public void testMatch()
  {
    _wo.parse("MATCH FULL (select a1 from t2)");
    // System.out.println(_wo.format());
    assertEquals("MATCH not recognized!","MATCH FULL(SELECT\r\n  A1\r\nFROM T2)",_wo.format());
  }

  @Test
  public void testOverlaps()
  {
    _wo.parse("OVERLAPS B");
    // System.out.println(_wo.format());
    assertEquals("OVERLAPS not recognized!","OVERLAPS B",_wo.format());
  }

  @Test
  public void testDistinct()
  {
    _wo.parse("is distinct from t1.col2");
    // System.out.println(_wo.format());
    assertEquals("DISTINCT not recognized!","IS DISTINCT FROM T1.COL2",_wo.format());
  }

  @Test
  public void testMember()
  {
    _wo.parse("member of t1.col2");
    // System.out.println(_wo.format());
    assertEquals("MEMBER not recognized!","MEMBER OF T1.COL2",_wo.format());
  }

  @Test
  public void testSubmultiset()
  {
    _wo.parse("submultiset of t1.col2");
    // System.out.println(_wo.format());
    assertEquals("SUBMULTISET not recognized!","SUBMULTISET OF T1.COL2",_wo.format());
  }

  @Test
  public void testSet()
  {
    _wo.parse("is a set");
    // System.out.println(_wo.format());
    assertEquals("SET not recognized!","IS A SET",_wo.format());
  }

  @Test
  public void testType()
  {
    _wo.parse("is of (udt1, only c1.s2.udt2, s1.udt3)");
    // System.out.println(_wo.format());
    assertEquals("TYPE test not recognized!","IS OF( UDT1, ONLY C1.S2.UDT2, S1.UDT3)",_wo.format());
  }

}
