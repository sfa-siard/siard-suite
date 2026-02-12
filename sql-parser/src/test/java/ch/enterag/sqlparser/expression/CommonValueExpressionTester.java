package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class CommonValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CommonValueExpression _cve = null;

  @Before
  public void setUp()
  {
    _cve = _sf.newCommonValueExpression();
  }

  @Test
  public void testMinimum()
  {
    _cve.parse("a1");
    // System.out.println(_cve.format());
    assertEquals("Minimum common value expression not recognized!","A1",_cve.format());
  }

  @Test
  public void testIdChain()
  {
    _cve.parse("emp.income");
    // System.out.println(_cve.format());
    assertEquals("Id chain (value expression primary) not recognized!","EMP.INCOME",_cve.format());
  }
  
  @Test
  public void testNumeric()
  {
    _cve.parse("123");
    // System.out.println(_cve.format());
    assertEquals("Numeric common value expression not recognized!","123",_cve.format());
  }

  @Test
  public void testString()
  {
    _cve.parse("'a ''character'' string with ''quotes'''");
    // System.out.println(_cve.format());
    assertEquals("String common value expression not recognized!","'a ''character'' string with ''quotes'''",_cve.format());
  }

}
