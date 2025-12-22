package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class UnsignedLiteralTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private UnsignedLiteral _ul = null;

  @Before
  public void setUp()
  {
    _ul = _sf.newUnsignedLiteral();
  }

  @Test
  public void testApproximate()
  {
    _ul.parse("123.34E-6");
    System.out.println(_ul.format());
    assertEquals("Scientific notation not recognized!","1.2334E-4",_ul.format());
  }
  
  @Test
  public void testInteger()
  {
    _ul.parse("78437487");
    // System.out.println(_ul.format());
    assertEquals("Integer not recognized!","78437487",_ul.format());
  }
  
  @Test
  public void testDecimal()
  {
    _ul.parse("78437487.6590834063086739067");
    // System.out.println(_ul.format());
    assertEquals("Decimal not recognized!","78437487.6590834063086739067",_ul.format());
  }
  
  @Test
  public void testString()
  {
    _ul.parse("'a ''quoted'' string'");
    // System.out.println(_ul.format());
    assertEquals("Quoted string not recognized!","'a ''quoted'' string'",_ul.format());
  }
  
  @Test
  public void testNational()
  {
    _ul.parse("N'a ''quoted'' string'");
    // System.out.println(_ul.format());
    assertEquals("Quoted national string not recognized!","N'a ''quoted'' string'",_ul.format());
  }

  @Test
  public void testBitString()
  {
    _ul.parse("B'0111001011010100100000'");
    // System.out.println(_ul.format());
    assertEquals("Bit string not recognized!","B'0111001011010100100000'",_ul.format());
  }
  
  @Test
  public void testBytes()
  {
    _ul.parse("X'12af34bc47de00'");
    // System.out.println(_ul.format());
    assertEquals("Bytes not recognized!","X'12AF34BC47DE00'",_ul.format());
  }
  
  @Test
  public void testDate()
  {
    _ul.parse("DATE'1968-11-25'");
    // System.out.println(_ul.format());
    assertEquals("Date not recognized!","DATE'1968-11-25'",_ul.format());
  }
  
  @Test
  public void testTime()
  {
    _ul.parse("TIME'19:23:55'");
    // System.out.println(_ul.format());
    assertEquals("Time not recognized!","TIME'19:23:55'",_ul.format());
  }

  @Test
  public void testMillis()
  {
    _ul.parse("TIME'19:23:55.123'");
    // System.out.println(_ul.format());
    assertEquals("Time not recognized!","TIME'19:23:55.123'",_ul.format());
  }
  
  @Test
  public void testTimestamp()
  {
    _ul.parse("TIMESTAMP'2014-08-28 19:23:55'");
    // System.out.println(_ul.format());
    assertEquals("Timestamp not recognized!","TIMESTAMP'2014-08-28 19:23:55'",_ul.format());
  }

  @Test
  public void testNanos()
  {
    _ul.parse("TIMESTAMP'2014-08-28 19:23:55.857362538'");
    // System.out.println(_ul.format());
    assertEquals("Timestamp not recognized!","TIMESTAMP'2014-08-28 19:23:55.857362538'",_ul.format());
  }
  
  @Test
  public void TestYearInterval()
  {
    _ul.parse("INTERVAL -'2' YEAR");
    System.out.println(_ul.format());
    assertEquals("YEAR interval not recognized!","INTERVAL '- 2' YEAR",_ul.format());
  }
  
  @Test
  public void TestYearToMonthInterval()
  {
    _ul.parse("INTERVAL '8-4' YEAR(2) TO MONTH");
    // System.out.println(_ul.format());
    assertEquals("YEAR TO MONTH interval not recognized!","INTERVAL '8-4' YEAR TO MONTH",_ul.format());
  }
  
  @Test
  public void TestMonthInterval()
  {
    _ul.parse("INTERVAL '4' MONTH");
    // System.out.println(_ul.format());
    assertEquals("MONTH interval not recognized!","INTERVAL '4' MONTH",_ul.format());
  }
  
  @Test
  public void TestDayInterval()
  {
    // ErrorListener.getInstance().suppressException();
    _ul.parse("INTERVAL '- 500' DAY(3)");
    // System.out.println(_ul.format());
    assertEquals("DAY interval not recognized!","INTERVAL '- 500' DAY(3)",_ul.format());
  }
  
  @Test
  public void TestInterval()
  {
    Interval iv = new Interval(1,123,3);
    _ul.parse(SqlLiterals.formatIntervalLiteral(iv));
    // System.out.println(_ul.format());
    assertEquals("INTERVAL not recognized!","INTERVAL '123-3' YEAR(3) TO MONTH",_ul.format());
  }
  
  @Test
  public void TestDayToHourInterval()
  {
    // ErrorListener.getInstance().suppressException();
    _ul.parse("INTERVAL -'500 23' DAY TO HOUR");
    // System.out.println(_ul.format());
    assertEquals("DAY interval not recognized!","INTERVAL '- 500 23' DAY(3) TO HOUR",_ul.format());
  }
  
  @Test
  public void TestMinute()
  {
    // ErrorListener.getInstance().suppressException();
    _ul.parse("INTERVAL '30' MINUTE");
    // System.out.println(_ul.format());
    assertEquals("MINUTE interval not recognized!","INTERVAL '30' MINUTE",_ul.format());
  }
  
  @Test
  public void TestHourToSecondInterval()
  {
    // ErrorListener.getInstance().suppressException();
    _ul.parse("INTERVAL '23:12:45' HOUR TO SECOND");
    // System.out.println(_ul.format());
    assertEquals("HOUR TO SECOND interval not recognized!","INTERVAL '23:12:45' HOUR TO SECOND",_ul.format());
  }
  
  @Test
  public void TestSecondInterval()
  {
    // ErrorListener.getInstance().suppressException();
    _ul.parse("INTERVAL -'0.123456789' SECOND(2,9)");
    // System.out.println(_ul.format());
    assertEquals("SECOND interval not recognized!","INTERVAL '- 0.123456789' SECOND(2, 9)",_ul.format());
  }

  @Test
  public void TestDayToTimeInterval()
  {
    // ErrorListener.getInstance().suppressException();
    _ul.parse("INTERVAL -'500 14:30:25.123456789' DAY(3) TO SECOND(2,9)");
    // System.out.println(_ul.format());
    assertEquals("DAY TO SECOND interval not recognized!","INTERVAL '- 500 14:30:25.123456789' DAY(3) TO SECOND(9)",_ul.format());
  }
  
  @Test
  public void testBoolean()
  {
    _ul.parse("UNKNOWN");
    // System.out.println(_ul.format());
    assertEquals("Boolean not recognized!","UNKNOWN",_ul.format());
  }
}
