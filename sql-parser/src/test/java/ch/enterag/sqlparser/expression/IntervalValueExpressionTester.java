package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class IntervalValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private IntervalValueExpression _ive = null;

  @Before
  public void setUp()
  {
    _ive = _sf.newIntervalValueExpression();
  }

  @Test
  public void testYearLiteral()
  {
    _ive.parse("INTERVAL'1' YEAR");
    // System.out.println(_ive.format());
    assertEquals("Year literal not recognized!","INTERVAL '1' YEAR",_ive.format());
  }

  @Test
  public void testAbs()
  {
    _ive.parse("-ABS(INTERVAL'1 20:23' DAY TO MINUTE)");
    // System.out.println(_ive.format());
    assertEquals("ABS not recognized!","-ABS(INTERVAL '1 20:23' DAY TO MINUTE)",_ive.format());
  }

  @Test
  public void testMultiple()
  {
    _ive.parse("T.COL * ABS(INTERVAL -'46 23:34:12' DAY TO SECOND)");
    // System.out.println(_ive.format());
    assertEquals("Multiple not recognized!","T.COL*ABS(INTERVAL '- 46 23:34:12' DAY TO SECOND)",_ive.format());
  }

  @Test
  public void testDivision()
  {
    _ive.parse("INTERVAL -'46 23:34:12' DAY TO SECOND / 20");
    // System.out.println(_ive.format());
    assertEquals("Division not recognized!","INTERVAL '- 46 23:34:12' DAY TO SECOND/20",_ive.format());
  }

  @Test
  public void testAddition()
  {
    _ive.parse("T.COL * INTERVAL -'46 23:34' DAY TO MINUTE + INTERVAL'3' MINUTE");
    // System.out.println(_ive.format());
    assertEquals("Addition not recognized!","T.COL*INTERVAL '- 46 23:34' DAY TO MINUTE + INTERVAL '3' MINUTE",_ive.format());
  }

  @Test
  public void testMultiplication()
  {
    _ive.parse("INTERVAL -'46 23:34' DAY TO MINUTE * INTERVAL'3' MINUTE");
    System.out.println(_ive.format());
    assertEquals("Addition not recognized!","INTERVAL '- 46 23:34' DAY TO MINUTE*INTERVAL '3' MINUTE",_ive.format());
  }

  @Test
  public void testDateSubtraction()
  {
    _ive.parse("DATE'2016-04-28' - DATE'2015-04-28'");
    // System.out.println(_ive.format());
    assertEquals("Date subtraction not recognized!","DATE'2016-04-28' - DATE'2015-04-28'",_ive.format());
  }

}
