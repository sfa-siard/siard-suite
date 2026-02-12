package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DropViewStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DropViewStatement _dvs = null;

  @Before
  public void setUp()
  {
    _dvs = _sf.newDropViewStatement();
  }
  
  @Test
  public void test()
  {
    _dvs.parse("DROP VIEW cat.sch.vw restrict");
    // System.out.println(_dvs.format());
    assertEquals("DROP VIEW statement not recognized!","DROP VIEW CAT.SCH.VW RESTRICT",_dvs.format());
  }

}
