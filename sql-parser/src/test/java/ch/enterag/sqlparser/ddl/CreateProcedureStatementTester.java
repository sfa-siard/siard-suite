package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class CreateProcedureStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CreateProcedureStatement _cps = null;

  @Before
  public void setUp()
  {
    _cps = _sf.newCreateProcedureStatement();
  }

  @Test
  public void testSimple()
  {
    _cps.parse("create procedure cat1.sch1.delproc(in idDel integer) delete from tab where id = idDel");
    System.out.println(_cps.format());
    assertEquals("CREATE PROCEDURE statement not recognized!","CREATE PROCEDURE CAT1.SCH1.DELPROC(\r\n  IN IDDEL INT\r\n)\r\nDELETE FROM TAB WHERE ID = IDDEL",_cps.format());
  }

  @Test
  public void testComplex()
  {
    _cps.parse("create procedure cat1.sch1.delproc(in idDel integer, in vc varchar(255)) MODIFIES SQL DATA  RETURNS NULL ON NULL INPUT language sql parameter style sql deterministic delete from tab where id = idDel and vc = v and v = 'abc' and id = 46");
    System.out.println(_cps.format());
    assertEquals("CREATE PROCEDURE statement not recognized!","CREATE PROCEDURE CAT1.SCH1.DELPROC(\r\n  IN IDDEL INT,\r\n  IN VC VARCHAR(255)\r\n)\r\n  LANGUAGE SQL\r\n  PARAMETER STYLE SQL\r\n  DETERMINISTIC\r\n  MODIFIES SQL DATA\r\n  RETURNS NULL ON NULL INPUT\r\nDELETE FROM TAB WHERE ID = IDDEL AND VC = V AND V = 'abc' AND ID = 46",_cps.format());
  }

}
