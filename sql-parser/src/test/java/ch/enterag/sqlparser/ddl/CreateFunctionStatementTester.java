package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;

import ch.enterag.sqlparser.*;

public class CreateFunctionStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CreateFunctionStatement _cfs = null;

  @Before
  public void setUp()
  {
    _cfs = _sf.newCreateFunctionStatement();
  }

  @Test
  public void testSimple()
  {
    _cfs.parse("create function countfunc(in idLimit integer) returns int return select count(id) from tab where id < idLimit; end");
    // System.out.println(_cfs.format());
    assertEquals("CREATE FUNCTION statement not recognized!","CREATE FUNCTION COUNTFUNC(\r\n  IN IDLIMIT INT\r\n)\r\nRETURNS INT\r\nRETURN SELECT COUNT ( ID ) FROM TAB WHERE ID < IDLIMIT ; END",_cfs.format());
  }

  @Test
  public void testComplex()
  {
    _cfs.parse("create function countfunc2(in idCount integer, in vc varchar(255)) returns int RETURNS NULL ON NULL INPUT language sql parameter style sql deterministic return select count(id) from tab where id = idCount and v < vc and v = 'abc' and id = 46");
    // System.out.println(_cfs.format());
    assertEquals("CREATE FUNCTION statement not recognized!","CREATE FUNCTION COUNTFUNC2(\r\n  IN IDCOUNT INT,\r\n  IN VC VARCHAR(255)\r\n)\r\nRETURNS INT\r\n  LANGUAGE SQL\r\n  PARAMETER STYLE SQL\r\n  DETERMINISTIC\r\n  RETURNS NULL ON NULL INPUT\r\nRETURN SELECT COUNT ( ID ) FROM TAB WHERE ID = IDCOUNT AND V < VC AND V = 'abc' AND ID = 46",_cfs.format());
  }

}
