package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class GeneralValueSpecificationTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private GeneralValueSpecification _gvs = null;

  @Before
  public void setUp()
  {
    _gvs = _sf.newGeneralValueSpecification();
  }

  @Test
  public void testVariable()
  {
    _gvs.parse(":var");
    // System.out.println(_gvs.format());
    assertEquals("Variable not recognized!",":VAR",_gvs.format());
  }
  
  @Test
  public void testIndicator()
  {
    _gvs.parse(":var indicator :\"ind\"");
    // System.out.println(_gvs.format());
    assertEquals("Indicator not recognized!",":VAR INDICATOR :\"ind\"",_gvs.format());
  }
  
  @Test
  public void testReference()
  {
    _gvs.parse("aa.\"am\"");
    // System.out.println(_gvs.format());
    assertEquals("Parameter not recognized!","AA.\"am\"",_gvs.format());
  }
  
  @Test
  public void testColumnOrParameter()
  {
    _gvs.parse("aa.bb.ddd.\"am\".ccd");
    // System.out.println(_gvs.format());
    assertEquals("Parameter not recognized!","AA.BB.DDD.\"am\".CCD",_gvs.format());
  }
  
  @Test
  public void testDynamicValue()
  {
    _gvs.parse("?");
    // System.out.println(_gvs.format());
    assertEquals("Dynamic value not recognized!","?",_gvs.format());
  }

}
