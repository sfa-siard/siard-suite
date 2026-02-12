package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class BooleanPrimaryTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private BooleanPrimary _bp = null;

  @Before
  public void setUp()
  {
    _bp = _sf.newBooleanPrimary();
  }
  
  @Test
  public void testComparison()
  {
    _bp.parse("A1[4] <> 'abc'");
    // System.out.println(_bp.format());
    assertEquals("Comparison not recognized!","A1[4] <> 'abc'",_bp.format());
  }

  @Test
  public void testSimpleBetween()
  {
    _bp.parse("'aaa' BETWEEN A1[4] AND 'abc'");
    // System.out.println(_bp.format());
    assertEquals("BETWEEN not recognized!","'aaa' BETWEEN A1[4] AND 'abc'",_bp.format());
  }

  @Test
  public void testComplexBetween()
  {
    _bp.parse("'aaa' NOT BETWEEN ASYMMETRIC \"A\"[4] AND 'abc'");
    // System.out.println(_bp.format());
    assertEquals("BETWEEN with symmetric option not recognized!","'aaa' NOT BETWEEN ASYMMETRIC \"A\"[4] AND 'abc'",_bp.format());
  }

  @Test
  public void testIn()
  {
    _bp.parse("'aaa' IN B[4], 'abc', t.col");
    // System.out.println(_bp.format());
    assertEquals("IN not recognized!","'aaa' IN B[4] 'abc' T.COL",_bp.format());
  }

  @Test
  public void testInQuery()
  {
    _bp.parse("'aaa' NOT IN (SELECT A1 FROM t)");
    // System.out.println(_bp.format());
    assertEquals("IN not recognized!","'aaa' NOT IN (SELECT\r\n  A1\r\nFROM T)",_bp.format());
  }

  @Test
  public void testLike()
  {
    _bp.parse("'aaa' LIKE 'aa%' ESCAPE '\\'");
    // System.out.println(_bp.format());
    assertEquals("LIKE not recognized!","'aaa' LIKE 'aa%' ESCAPE '\\'",_bp.format());
  }

  @Test
  public void testSimilar()
  {
    _bp.parse("'aAa' NOT SIMILAR TO 'aa%' ESCAPE '\\'");
    // System.out.println(_bp.format());
    assertEquals("SIMILAR not recognized!","'aAa' NOT SIMILAR TO 'aa%' ESCAPE '\\'",_bp.format());
  }

  @Test
  public void testNull()
  {
    _bp.parse("t.col is null");
    // System.out.println(_bp.format());
    assertEquals("IS NULL not recognized!","T.COL IS NULL",_bp.format());
  }

  @Test
  public void testQuantified()
  {
    _bp.parse("t1.col <= some (select a1 from t2)");
    // System.out.println(_bp.format());
    assertEquals("Quantified comparison not recognized!","T1.COL <= SOME(SELECT\r\n  A1\r\nFROM T2)",_bp.format());
  }

  @Test
  public void testExists()
  {
    _bp.parse("exists(select a1 from t2)");
    // System.out.println(_bp.format());
    assertEquals("EXISTS not recognized!","EXISTS(SELECT\r\n  A1\r\nFROM T2)",_bp.format());
  }

  @Test
  public void testUnique()
  {
    _bp.parse("unique(select a1 from t2)");
    // System.out.println(_bp.format());
    assertEquals("UNIQUE not recognized!","UNIQUE(SELECT\r\n  A1\r\nFROM T2)",_bp.format());
  }

  @Test
  public void testNormalized()
  {
    _bp.parse("'aaa' is not normalized");
    // System.out.println(_bp.format());
    assertEquals("NORMALIZED not recognized!","'aaa' IS NOT NORMALIZED",_bp.format());
  }

  @Test
  public void testMatch()
  {
    _bp.parse("T.COL MATCH SIMPLE (select a1 from t2)");
    // System.out.println(_bp.format());
    assertEquals("MATCH not recognized!","T.COL MATCH SIMPLE(SELECT\r\n  A1\r\nFROM T2)",_bp.format());
  }

  @Test
  public void testOverlaps()
  {
    _bp.parse("T.COL OVERLAPS B");
    // System.out.println(_bp.format());
    assertEquals("OVERLAPS not recognized!","T.COL OVERLAPS B",_bp.format());
  }

  @Test
  public void testDistinct()
  {
    _bp.parse("T.COL is distinct from t1.col2");
    // System.out.println(_bp.format());
    assertEquals("DISTINCT not recognized!","T.COL IS DISTINCT FROM T1.COL2",_bp.format());
  }

  @Test
  public void testMember()
  {
    _bp.parse("T.COL member of t1.col2");
    // System.out.println(_bp.format());
    assertEquals("MEMBER not recognized!","T.COL MEMBER OF T1.COL2",_bp.format());
  }

  @Test
  public void testSubmultiset()
  {
    _bp.parse("T.COL submultiset of t1.col2");
    // System.out.println(_bp.format());
    assertEquals("SUBMULTISET not recognized!","T.COL SUBMULTISET OF T1.COL2",_bp.format());
  }

  @Test
  public void testSet()
  {
    _bp.parse("T.COL is a set");
    // System.out.println(_bp.format());
    assertEquals("SET not recognized!","T.COL IS A SET",_bp.format());
  }

  @Test
  public void testType()
  {
    _bp.parse("T.COL is of (udt1, only c1.s2.udt2, s1.udt3)");
    // System.out.println(_bp.format());
    assertEquals("TYPE not recognized!","T.COL IS OF(UDT1, ONLY C1.S2.UDT2, S1.UDT3)",_bp.format());
  }

  @Test
  public void testParenthesized()
  {
    _bp.parse("(t1.col <= some (select a1 from t2))");
    // System.out.println(_bp.format());
    assertEquals("Parenthesized expression not recognized!","(T1.COL <= SOME(SELECT\r\n  A1\r\nFROM T2))",_bp.format());
  }

}
