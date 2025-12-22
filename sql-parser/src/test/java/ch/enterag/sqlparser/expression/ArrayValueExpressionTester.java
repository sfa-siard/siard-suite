package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class ArrayValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private ArrayValueExpression _ave = null;

  @Before
  public void setUp()
  {
    _ave = _sf.newArrayValueExpression();
  }
  
  @Test
  public void testLiteral()
  {
    _ave.parse("t.COL");
    // System.out.println(_ave.format());
    assertEquals("Array value literal not recognized!","T.COL",_ave.format());
  }

  @Test
  public void testConcatenation()
  {
    _ave.parse("t.COL || \"a\"");
    // System.out.println(_ave.format());
    assertEquals("Array value concatenation not recognized!","T.COL || \"a\"",_ave.format());
  }

}
