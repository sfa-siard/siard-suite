package ch.enterag.sqlparser;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;

import javax.xml.datatype.*;

public class IntervalTester
{

  @Test
  public void testDuration()
  {
    try
    {
      DatatypeFactory df = DatatypeFactory.newInstance();
      long lMilliSeconds = (((((17*60)+54)*60)+23)*1000)+123;
      Duration duration1 = df.newDurationDayTime(lMilliSeconds);
      System.out.println(duration1.toString());
      Interval iv = Interval.fromDuration(duration1);
      System.out.println(iv.toString());
      Duration duration2 = iv.toDuration();
      assertEquals("Invalid duration!",duration1,duration2);
    }
    catch(DatatypeConfigurationException dcfe){ fail(EU.getExceptionMessage(dcfe)); }
  }

}
