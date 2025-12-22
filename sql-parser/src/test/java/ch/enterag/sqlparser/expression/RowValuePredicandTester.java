package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class RowValuePredicandTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private RowValuePredicand _rvp = null;

  @Before
  public void setUp()
  {
    _rvp = _sf.newRowValuePredicand();
  }

  @Test
  public void testReference()
  {
    // ErrorListener.getInstance().suppressException();
    _rvp.parse("C1.S1.TEST.COL");
    System.out.println(_rvp.format());
    assertEquals("Column reference not recognized!","C1.S1.TEST.COL",_rvp.format());
  }
  
  @Test
  public void testNegative()
  {
    // ErrorListener.getInstance().suppressException();
    _rvp.parse("-1");
    System.out.println(_rvp.format());
    assertEquals("Negative value not recognized!","-1",_rvp.format());
  }

  @Test
  public void testRow()
  {
    _rvp.parse("ROW(C1.S1.T1.COL)");
    // System.out.println(_rvp.format());
    assertEquals("ROW expression not recognized!","ROW(C1.S1.T1.COL)",_rvp.format());
  }
  
}
