package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class RowValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private RowValueExpression _rve = null;

  @Before
  public void setUp()
  {
    _rve = _sf.newRowValueExpression();
  }

  @Test
  public void testPrimary()
  {
    _rve.parse("C1.S1.T1.COL");
    // System.out.println(_rve.format());
    assertEquals("Value expression primary not recognized!","C1.S1.T1.COL",_rve.format());
  }

  @Test
  public void testList()
  {
    _rve.parse("(C1.S1.T1.COL,5,'abc')");
    // System.out.println(_rve.format());
    assertEquals("Value list not recognized!","(C1.S1.T1.COL, 5, 'abc')",_rve.format());
  }
  
  @Test
  public void testInterval()
  {
    _rve.parse("INTERVAL '123-2' YEAR(3) TO MONTH");
    // System.out.println(_rve.format());
    assertEquals("INTERVAL not recognized!","INTERVAL '123-2' YEAR(3) TO MONTH",_rve.format());
  }

  @Test
  public void testRow()
  {
    _rve.parse("ROW(C1.S1.T1.COL)");
    // System.out.println(_rve.format());
    assertEquals("ROW expression not recognized!","ROW(C1.S1.T1.COL)",_rve.format());
  }

  @Test
  public void testQuery()
  {
    _rve.parse("(SELECT COUNT(*) FROM C1.S1.T1)");
    System.out.println(_rve.format());
    assertEquals("Query expression not recognized!","(SELECT\r\n  COUNT(*)\r\nFROM C1.S1.T1)",_rve.format());
  }

}
