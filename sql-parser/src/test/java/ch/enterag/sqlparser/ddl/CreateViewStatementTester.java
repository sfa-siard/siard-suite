package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class CreateViewStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CreateViewStatement _cvs = null;

  @Before
  public void setUp()
  {
    _cvs = _sf.newCreateViewStatement();
  }
  
  @Test
  public void testSimple()
  {
    _cvs.parse("CREATE VIEW cat.\"schema\".vw(id, s, ts) as select id, s, ts from tab where col < 45");
    // System.out.println(_cvs.format());
    String sExpected = "CREATE VIEW CAT.\"schema\".VW(ID, S, TS) AS SELECT\r\n"+
                       "  ID,\r\n"+
                       "  S,\r\n"+
                       "  TS\r\n"+
                       "FROM TAB\r\n"+
                       "WHERE COL < 45"; 
    assertEquals("CREATE VIEW statement not recognized!",sExpected,_cvs.format());
  }

  @Test
  public void testComplex()
  {
    _cvs.parse("CREATE recursive view \"schema\".vw of vtype under ttab (ref is id derived, ref is ts user generated, acol with options scope sch.tab1) as select id, ts, s, n from sch.tab where col < 45 with local check option");
    // System.out.println(_cvs.format());
    String sExpected = "CREATE RECURSIVE VIEW \"schema\".VW OF VTYPE UNDER TTAB( REF IS ID DERIVED, REF IS TS USER GENERATED, ACOL WITH OPTIONS SCOPE SCH.TAB1) AS SELECT\r\n  ID,\r\n  TS,\r\n  S,\r\n  N\r\nFROM SCH.TAB\r\nWHERE COL < 45 WITH LOCAL CHECK OPTION"; 
    assertEquals("CREATE VIEW statement not recognized!",sExpected,_cvs.format());
  }

}
