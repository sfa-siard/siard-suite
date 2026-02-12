package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class NumericValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private NumericValueExpression _nve = null;

  @Before
  public void setUp()
  {
    _nve = _sf.newNumericValueExpression();
  }

  @Test
  public void testUnsigned()
  {
    _nve.parse("123");
    // System.out.println(_nve.format());
    assertEquals("Unsigned numeric value expression not recognized !","123",_nve.format());
  }

  @Test
  public void testAddition()
  {
    _nve.parse("123+456");
    // System.out.println(_nve.format());
    assertEquals("Addition in numeric value expression not recognized !","123 + 456",_nve.format());
  }

  @Test
  public void testMultiplication()
  {
    _nve.parse("123*456");
    // System.out.println(_nve.format());
    assertEquals("Addition in numeric value expression not recognized !","123*456",_nve.format());
  }

  @Test
  public void testAdditionAndMultiplication()
  {
    _nve.parse("123*456+789");
    // System.out.println(_nve.format());
    assertEquals("Addition in numeric value expression not recognized !","123*456 + 789",_nve.format());
  }

  @Test
  public void testMultiplicationAndAddition()
  {
    _nve.parse("123*(456+789)");
    //System.out.println(_nve.format());
    assertEquals("Addition in numeric value expression not recognized !","123*(456 + 789)",_nve.format());
  }

  @Test
  public void testNumericFunctionMod()
  {
    _nve.parse("Mod(11,2)");
    // System.out.println(_nve.format());
    assertEquals("Numeric function MOD numeric value expression not recognized !","MOD(11, 2)",_nve.format());
  }
  
  @Test
  public void testValueExpressionPrimary()
  {
    _nve.parse("a1");
    // System.out.println(_nve.format());
    assertEquals("Value expression primary not recognized !","A1",_nve.format());
  }
  

}
