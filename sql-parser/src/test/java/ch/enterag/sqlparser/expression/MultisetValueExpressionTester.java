package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class MultisetValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private MultisetValueExpression _mve = null;

  @Before
  public void setUp()
  {
    _mve = _sf.newMultisetValueExpression();
  }

  @Test
  public void testSimpleValue()
  {
    // ErrorListener.getInstance().suppressException();
    _mve.parse("\"A\"");
    // System.out.println(_mve.format());
    assertEquals("Simple multiset value not recognized!","\"A\"",_mve.format());
  }

  @Test
  public void testSetExpression()
  {
    // ErrorListener.getInstance().suppressException();
    _mve.parse("SET(B)");
    // System.out.println(_mve.format());
    assertEquals("SET expression not recognized!","SET(B)",_mve.format());
  }

  @Test
  public void testMultisetOperator()
  {
    // ErrorListener.getInstance().suppressException();
    _mve.parse("D MULTISET EXCEPT SET(B)");
    // System.out.println(_mve.format());
    assertEquals("Multiset operaator EXCEPT not recognized!","D MULTISET EXCEPT SET(B)",_mve.format());
  }

  @Test
  public void testMultisetOperatorWithQualifier()
  {
    // ErrorListener.getInstance().suppressException();
    _mve.parse("C MULTISET INTERSECT DISTINCT B");
    // System.out.println(_mve.format());
    assertEquals("Multiset operator with DISTINCT not recognized!","C MULTISET INTERSECT DISTINCT B",_mve.format());
  }

}
