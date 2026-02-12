package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class WindowFrameBoundTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private WindowFrameBound _wfb = null;

  @Before
  public void setUp()
  {
    _wfb = _sf.newWindowFrameBound();
  }

  @Test
  public void testUnboundedPreceding()
  {
    // ErrorListener.getInstance().suppressException();
    _wfb.parse("UNBOUNDED PRECEDING");
    System.out.println(_wfb.format());
    assertEquals("Window frame bound UNBOUNDED PRECEDING not recognized!","UNBOUNDED PRECEDING",_wfb.format());
  }

  @Test
  public void testNumericFollowing()
  {
    // ErrorListener.getInstance().suppressException();
    _wfb.parse("5 FOLLOWING");
    System.out.println(_wfb.format());
    assertEquals("Window frame bound 5 FOLLOWING not recognized!","5 FOLLOWING",_wfb.format());
  }


}
