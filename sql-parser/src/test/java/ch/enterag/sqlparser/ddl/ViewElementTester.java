package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class ViewElementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private ViewElement _ve = null;

  @Before
  public void setUp()
  {
    _ve = _sf.newViewElement();
  }
  
  @Test
  public void testSelfRefColumn()
  {
    _ve.parse("REF IS obj_id SYSTEM GENERATED");
    // System.out.println(_ve.format());
    assertEquals("Self-referencing column specification not recognized!","REF IS OBJ_ID SYSTEM GENERATED",_ve.format());
  }
  
  @Test
  public void testScope()
  {
    _ve.parse("\"Column\" with options scope schema1.table1");
    // System.out.println(_ve.format());
    assertEquals("SCOPE column option not recognized!","\"Column\" WITH OPTIONS SCOPE SCHEMA1.TABLE1",_ve.format());
  }
  
}
