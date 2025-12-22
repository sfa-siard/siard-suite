package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DropProcedureStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DropProcedureStatement _dps = null;

  @Before
  public void setUp()
  {
    _dps = _sf.newDropProcedureStatement();
  }
  
  @Test
  public void testSimple()
  {
    _dps.parse("DROP specific Procedure cat1.sch1.delproc cascade");
    // System.out.println(_dps.format());
    assertEquals("DROP PROCEDURE statement not recognized!","DROP SPECIFIC PROCEDURE CAT1.SCH1.DELPROC CASCADE",_dps.format());
  }

  @Test
  public void testComplex()
  {
    _dps.parse("DROP Procedure cat1.sch1.delproc(in idDel integer) restrict");
    // System.out.println(_dps.format());
    assertEquals("DROP PROCEDURE statement not recognized!","DROP PROCEDURE CAT1.SCH1.DELPROC(IN IDDEL INT) RESTRICT",_dps.format());
  }

}
