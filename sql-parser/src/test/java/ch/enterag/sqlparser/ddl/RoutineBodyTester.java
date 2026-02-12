package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class RoutineBodyTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private RoutineBody _rb = null;

  @Before
  public void setUp()
  {
    _rb = _sf.newRoutineBody();
  }
  
  @Test
  public void testSimple()
  {
    _rb.parse("delete from tab where id = idDel");
    System.out.println(_rb.format());
    assertEquals("Routine body not recognized!","DELETE FROM TAB WHERE ID = IDDEL",_rb.format());
  }

  @Test
  public void testComplex()
  {
    _rb.parse("delete from \"tab\" where id = idDel and v = 'abc'");
    // System.out.println(_rb.format());
    assertEquals("Routine body not recognized!","DELETE FROM \"tab\" WHERE ID = IDDEL AND V = 'abc'",_rb.format());
  }

}
