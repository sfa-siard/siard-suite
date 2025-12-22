package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class BooleanValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private BooleanValueExpression _bve = null;
  
  @Before
  public void setUp()
  {
    _bve = _sf.newBooleanValueExpression();
  }
  
  @Test
  public void testEqual()
  {
    _bve.parse("A1 = 'abc'");
    // System.out.println(_bve.format());
    assertEquals("Equal not recognized!","A1 = 'abc'",_bve.format());
  }


  @Test
  public void testNotDistinct()
  {
    _bve.parse("NOT T.COL is distinct from t1.col2");
    // System.out.println(_bve.format());
    assertEquals("DISTINCT not recognized!","NOT T.COL IS DISTINCT FROM T1.COL2",_bve.format());
  }

  @Test
  public void testOr()
  {
    _bve.parse("COL1 IS NULL OR EXISTS (SELECT CUR FROM DUAL)");
    // System.out.println(_bve.format());
    assertEquals("OR not recognized!","COL1 IS NULL OR EXISTS(SELECT\r\n  CUR\r\nFROM DUAL)",_bve.format());
  }

  @Test
  public void testAnd()
  {
    _bve.parse("(A1 = 5) and (NOT T.COL is distinct from t1.col2)");
    // System.out.println(_bve.format());
    assertEquals("AND not recognized!","(A1 = 5) AND (NOT T.COL IS DISTINCT FROM T1.COL2)",_bve.format());
  }

  @Test
  public void testIsTrue()
  {
    _bve.parse("(A1 = 5) IS not true");
    // System.out.println(_bve.format());
    assertEquals("IS NOT TRUE not recognized!","(A1 = 5) IS NOT TRUE",_bve.format());
  }


}
