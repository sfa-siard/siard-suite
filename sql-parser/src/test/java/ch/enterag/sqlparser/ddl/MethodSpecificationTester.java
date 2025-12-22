package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class MethodSpecificationTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private MethodSpecification _ms = null;

  @Before
  public void setUp()
  {
    _ms = _sf.newMethodSpecification();
  }
  
  @Test
  public void test()
  {
    _ms.parse("METHOD length_interval () RETURNS INTERVAL HOUR(2) TO MINUTE");
    // System.out.println(_ms.format());
    assertEquals("Method specification recognized!","METHOD LENGTH_INTERVAL() RETURNS INTERVAL HOUR(2) TO MINUTE",_ms.format());
  }

}
