package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DatetimeValueFunctionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DatetimeValueFunction _dvf = null;

  @Before
  public void setUp()
  {
    _dvf = _sf.newDatetimeValueFunction();
  }

  @Test
  public void testCurrentDate()
  {
    // ErrorListener.getInstance().suppressException();
    _dvf.parse("CURRENT_DATE");
    // System.out.println(_dvf.format());
    assertEquals("CURRENT_DATE not recognized!","CURRENT_DATE",_dvf.format());
  }

  @Test
  public void testCurrentTime()
  {
    // ErrorListener.getInstance().suppressException();
    _dvf.parse("CURRENT_TIME(3)");
    // System.out.println(_dvf.format());
    assertEquals("CURRENT_TIME not recognized!","CURRENT_TIME(3)",_dvf.format());
  }

  @Test
  public void testCurrentTimestamp()
  {
    // ErrorListener.getInstance().suppressException();
    _dvf.parse("CURRENT_TIMESTAMP");
    // System.out.println(_dvf.format());
    assertEquals("CURRENT_TIMESTAMP not recognized!","CURRENT_TIMESTAMP",_dvf.format());
  }

  @Test
  public void testLocalTime()
  {
    // ErrorListener.getInstance().suppressException();
    _dvf.parse("LOCALTIME");
    // System.out.println(_dvf.format());
    assertEquals("LOCALTIME not recognized!","LOCALTIME",_dvf.format());
  }

  @Test
  public void testLocalTimestamp()
  {
    // ErrorListener.getInstance().suppressException();
    _dvf.parse("LOCALTIMESTAMP(9)");
    // System.out.println(_dvf.format());
    assertEquals("LOCALTIMESTAMP not recognized!","LOCALTIMESTAMP(9)",_dvf.format());
  }


}
