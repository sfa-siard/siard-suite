package ch.enterag.sqlparser.dml;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DeleteStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DeleteStatement _ds = null;

  @Before
  public void setUp()
  {
    _ds = _sf.newDeleteStatement();
  }
  
  @Test
  public void testSimple()
  {
    _ds.parse("DELETE FROM sch.tab where a1 > 5");
    System.out.println(_ds.format());
    String sExpected = "DELETE FROM SCH.TAB WHERE A1 > 5"; 
    assertEquals("DELETE statement not recognized!",sExpected,_ds.format());
  }

}
