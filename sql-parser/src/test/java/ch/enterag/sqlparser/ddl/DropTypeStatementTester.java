package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DropTypeStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DropTypeStatement _dts = null;

  @Before
  public void setUp()
  {
    _dts = _sf.newDropTypeStatement();
  }
  
  @Test
  public void test()
  {
    _dts.parse("DROP TYPE cat.sch.\"typ\" cascade");
    // System.out.println(dts.format());
    assertEquals("DROP TYPE statement not recognized!","DROP TYPE CAT.SCH.\"typ\" CASCADE",_dts.format());
  }

}
