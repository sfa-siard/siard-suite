package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class WindowSpecificationTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private WindowSpecification _ws = null;

  @Before
  public void setUp()
  {
    _ws = _sf.newWindowSpecification();
  }

  @Test
  public void testBetween()
  {
    _ws.parse("WN ROWS BETWEEN UNBOUNDED PRECEDING AND 5 FOLLOWING");
    System.out.println(_ws.format());
    assertEquals("Window BETWEEN specification not recognized!","WN ROWS BETWEEN UNBOUNDED PRECEDING AND 5 FOLLOWING",_ws.format());
  }

}
