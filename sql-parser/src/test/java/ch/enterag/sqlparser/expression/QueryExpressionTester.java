package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class QueryExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private QueryExpression _qe = null;

  @Before
  public void setUp()
  {
    _qe = _sf.newQueryExpression();
  }

  @Test
  public void testCountQuery()
  {
    _qe.parse("SELECT COUNT(*) FROM C1.S1.T1");
    // System.out.println(_qe.format());
    assertEquals("Count query not recognized!","SELECT\r\n  COUNT(*)\r\nFROM C1.S1.T1",_qe.format());
  }
  
}
