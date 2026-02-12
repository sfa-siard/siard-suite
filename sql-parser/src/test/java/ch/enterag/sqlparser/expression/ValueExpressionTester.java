package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class ValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private ValueExpression _ve = null;

  @Before
  public void setUp()
  {
    _ve = _sf.newValueExpression();
  }

  @Test
  public void testInteger()
  {
    _ve.parse("-123456789");
    System.out.println(_ve.format());
    assertEquals("Integer literal not recognized!","-123456789",_ve.format());
  }
  
  @Test
  public void testMinimum()
  {
    _ve.parse("a1");
    // System.out.println(_ve.format());
    assertEquals("Minimum value expression not recognized!","A1",_ve.format());
  }

  @Test
  public void testIdChain()
  {
    _ve.parse("emp.income");
    // System.out.println(_ve.format());
    assertEquals("IdChain (value expression primary) not recognized!","EMP.INCOME",_ve.format());
  }

  @Test
  public void testColumnReference()
  {
    _ve.parse("C1.S1.TEST.COL");
    // System.out.println(_ve.format());
    assertEquals("Column reference not recognized!","C1.S1.TEST.COL",_ve.format());
  }

}
