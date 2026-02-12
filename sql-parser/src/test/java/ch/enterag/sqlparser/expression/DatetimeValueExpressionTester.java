package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DatetimeValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DatetimeValueExpression _dve = null;

  @Before
  public void setUp()
  {
    _dve = _sf.newDatetimeValueExpression();
  }

  @Test
  public void testLiteralDate()
  {
    _dve.parse("DATE'2016-4-28'");
    // System.out.println(_dve.format());
    assertEquals("Date literal not recognized!","DATE'2016-04-28'",_dve.format());
  }

  @Test
  public void testLiteralTime()
  {
    _dve.parse("TIME'10:54:45.123' AT LOCAL");
    // System.out.println(_dve.format());
    assertEquals("Time literal not recognized!","TIME'10:54:45.123' AT LOCAL",_dve.format());
  }

  @Test
  public void testLiteralTimestamp()
  {
    _dve.parse("TIMESTAMP'2016-4-28 10:54:5.123' AT TIME ZONE INTERVAL '2:30' HOUR TO MINUTE");
    System.out.println(_dve.format());
    assertEquals("Timestamp literal not recognized!","TIMESTAMP'2016-04-28 10:54:05.123' AT TIME ZONE INTERVAL '2:30' HOUR TO MINUTE",_dve.format());
  }
  
  @Test
  public void testSubtractInterval()
  {
    // ErrorListener.getInstance().suppressException();
    _dve.parse("TIMESTAMP'2016-4-28 10:54:5.123' - INTERVAL'1' DAY");
    // System.out.println(_dve.format());
    assertEquals("Subtract interval not recognized!","TIMESTAMP'2016-04-28 10:54:05.123' - INTERVAL '1' DAY",_dve.format());
  }

  @Test
  public void testAddInterval()
  {
    _dve.parse("TIMESTAMP'2016-4-28 10:54:5.123' + INTERVAL'1' DAY");
    // System.out.println(_dve.format());
    assertEquals("Add interval not recognized!","TIMESTAMP'2016-04-28 10:54:05.123' + INTERVAL '1' DAY",_dve.format());
  }

  @Test
  public void testFunction()
  {
    _dve.parse("CURRENT_DATE");
    System.out.println(_dve.format());
    assertEquals("CURRENT_DATE not recognized!","CURRENT_DATE",_dve.format());
  }

}
