package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class CaseExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CaseExpression _ce = null;

  @Before
  public void setUp()
  {
    _ce = _sf.newCaseExpression();
  }
  
  @Test
  public void testNullIf()
  {
    _ce.parse("NullIf(A1,5)");
    // System.out.println(_ce.format());
    assertEquals("NULLIF expression not recognized!","NULLIF(A1, 5)",_ce.format());
  }

  @Test
  public void testCoalesce()
  {
    _ce.parse("Coalesce(A1,5,'BBB',S1.Test)");
    // System.out.println(_ce.format());
    assertEquals("COALESCE expression not recognized!","COALESCE(A1, 5, 'BBB', S1.TEST)",_ce.format());
  }

  @Test
  public void testSimpleWhen()
  {
    _ce.parse("CASE C1.S1.TEST.COL WHEN IS NULL THEN -1 WHEN -1 THEN 0 ELSE C1.S1.TEST.COL END");
    // System.out.println(_ce.format());
    assertEquals("Simple WHEN clause not recognized!","CASE C1.S1.TEST.COL\r\n  WHEN IS NULL THEN -1\r\n  WHEN -1 THEN 0\r\n  ELSE C1.S1.TEST.COL\r\nEND",_ce.format());
  }

  @Test
  public void testSearchedWhen()
  {
    _ce.parse("CASE WHEN A1 IS NULL THEN -1 WHEN B1 = -1 THEN 0 ELSE C1.S1.TEST.COL END");
    // System.out.println(_ce.format());
    assertEquals("Searched WHEN clause not recognized!","CASE\r\n  WHEN A1 IS NULL THEN -1\r\n  WHEN B1 = -1 THEN 0\r\n  ELSE C1.S1.TEST.COL\r\nEND",_ce.format());
  }

}
