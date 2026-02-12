package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DropSchemaStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DropSchemaStatement _dss = null;

  @Before
  public void setUp()
  {
    _dss = _sf.newDropSchemaStatement();
  }
  
  @Test
  public void test()
  {
    _dss.parse("DROP SCHEMA cat.\"sch\" cascade");
    // System.out.println(dts.format());
    assertEquals("DROP SCHEMA statement not recognized!","DROP SCHEMA CAT.\"sch\" CASCADE",_dss.format());
  }
}
