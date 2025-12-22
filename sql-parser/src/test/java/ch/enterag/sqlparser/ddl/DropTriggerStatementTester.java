package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DropTriggerStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DropTriggerStatement _dts = null;

  @Before
  public void setUp()
  {
    _dts = _sf.newDropTriggerStatement();
  }
  
  @Test
  public void test()
  {
    _dts.parse("DROP TRIGGER cat.sch.tg");
    // System.out.println(_dts.format());
    assertEquals("DROP TRIGGER statement not recognized!","DROP TRIGGER CAT.SCH.TG",_dts.format());
  }

}
