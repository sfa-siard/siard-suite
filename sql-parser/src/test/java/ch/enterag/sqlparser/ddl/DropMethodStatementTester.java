package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DropMethodStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DropMethodStatement _dms = null;

  @Before
  public void setUp()
  {
    _dms = _sf.newDropMethodStatement();
  }
  
  @Test
  public void testSimple()
  {
    _dms.parse("DROP specific method cat1.sch1.delmeth for udt cascade");
    // System.out.println(_dms.format());
    assertEquals("DROP METHOD statement not recognized!","DROP SPECIFIC METHOD CAT1.SCH1.DELMETH FOR UDT CASCADE",_dms.format());
  }

  @Test
  public void testComplex()
  {
    _dms.parse("DROP Method cat1.sch1.delmeth(in idDel integer) for cat.sch.ty restrict");
    // System.out.println(_dms.format());
    assertEquals("DROP METHOD statement not recognized!","DROP METHOD CAT1.SCH1.DELMETH(IN IDDEL INT) FOR CAT.SCH.TY RESTRICT",_dms.format());
  }

}
