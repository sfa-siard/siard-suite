package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class NumericValueFunctionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private NumericValueFunction _nvf = null;

  @Before
  public void setUp()
  {
    _nvf = _sf.newNumericValueFunction();
  }

  @Test
  public void testNumericFunctionAbs()
  {
    _nvf.parse("Abs(-11)");
    System.out.println(_nvf.format());
    assertEquals("Numeric value function ABS not recognized !","ABS(-11)",_nvf.format());
  }

  @Test
  public void testNumericFunctionMod()
  {
    _nvf.parse("Mod(11,2)");
    // System.out.println(_nvf.format());
    assertEquals("Numeric value function MOD not recognized !","MOD(11, 2)",_nvf.format());
  }

  @Test
  public void testNumericFunctionLn()
  {
    _nvf.parse("Ln(11)");
    // System.out.println(_nvf.format());
    assertEquals("Numeric value function LN not recognized !","LN(11)",_nvf.format());
  }

  @Test
  public void testNumericFunctionExp()
  {
    _nvf.parse("Exp(11)");
    // System.out.println(_nvf.format());
    assertEquals("Numeric value function EXP not recognized !","EXP(11)",_nvf.format());
  }

  @Test
  public void testNumericFunctionPower()
  {
    _nvf.parse("Power(11,3)");
    // System.out.println(_nvf.format());
    assertEquals("Numeric value function POWER not recognized !","POWER(11, 3)",_nvf.format());
  }

  @Test
  public void testNumericFunctionFloor()
  {
    _nvf.parse("Floor(11.45)");
    // System.out.println(_nvf.format());
    assertEquals("Numeric value function FLOOR not recognized !","FLOOR(11.45)",_nvf.format());
  }

  @Test
  public void testNumericFunctionCeiling()
  {
    _nvf.parse("Ceil(11.45)");
    // System.out.println(_nvf.format());
    assertEquals("Numeric value function CEILING not recognized !","CEILING(11.45)",_nvf.format());
  }

  @Test
  public void testNumericFunctionWidthBucket()
  {
    _nvf.parse("Width_Bucket(11.45,23,2,56)");
    System.out.println(_nvf.format());
    assertEquals("Numeric value function WIDTH_BUCKET not recognized !","WIDTH_BUCKET(11.45, 23, 2, 56)",_nvf.format());
  }

  @Test
  public void testNumericFunctionOctetLength()
  {
    _nvf.parse("OCTET_LENGTH(COLA)");
    System.out.println(_nvf.format());
    
  }
}
