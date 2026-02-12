package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class CreateTriggerStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CreateTriggerStatement _cts = null;

  @Before
  public void setUp()
  {
    _cts = _sf.newCreateTriggerStatement();
  }
  
  @Test
  public void testSimple()
  {
    _cts.parse("CREATE TRIGGER cat.sch.tg before delete on cat.sch.tab insert current_timestamp into archive");
    // System.out.println(_cts.format());
    assertEquals("CREATE TRIGGER statement not recognized!","CREATE TRIGGER CAT.SCH.TG BEFORE DELETE ON CAT.SCH.TAB INSERT CURRENT_TIMESTAMP INTO ARCHIVE",_cts.format());
  }

}
