package ch.admin.bar.siard2.postgres;

import java.text.*;
import java.time.*;
import org.junit.*;
import static org.junit.Assert.*;
import ch.enterag.sqlparser.*;
import ch.enterag.utils.*;

public class PostgresLiteralsTester
{
  @Test
  public void test()
  {
    try
    {
      String s = "123 years 3 mons";
      Interval iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      assertEquals("Invalid interval!",new Interval(1,123,3),iv);
      
      s = "-123 years 3 mons";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      assertEquals("Invalid interval!",new Interval(-1,123-1,12-3),iv);
      
      s = "123 years -3 mons";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      assertEquals("Invalid interval!",new Interval(1,123-1,12-3),iv);
      
      s = "-123 years -3 mons";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      assertEquals("Invalid interval!",new Interval(-1,123,3),iv);
      
      s = "12 years";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      assertEquals("Invalid interval!",new Interval(1,12,0),iv);
      
      s = "4 mons";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      assertEquals("Invalid interval!",new Interval(1,0,4),iv);
      
      s = "3 days 04:05:06";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      LocalTime lt = LocalTime.parse("04:05:06");
      assertEquals("Invalid interval!",new Interval(1,3,lt.getHour(),lt.getMinute(),lt.getSecond(),lt.getNano()),iv);
      
      s = "-3 days 04:05:06";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      lt = LocalTime.parse("04:05:06");
      long lNanos = PostgresLiterals.lNANOS_PER_DAY-PostgresLiterals.getNanos(lt);
      lt = PostgresLiterals.getLocalTime(lNanos);
      assertEquals("Invalid interval!",new Interval(-1,2,lt.getHour(),lt.getMinute(),lt.getSecond(),lt.getNano()),iv);
      
      s = "3 days -04:05:06";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      lt = LocalTime.parse("04:05:06");
      lNanos = PostgresLiterals.lNANOS_PER_DAY-PostgresLiterals.getNanos(lt);
      lt = PostgresLiterals.getLocalTime(lNanos);
      assertEquals("Invalid interval!",new Interval(1,2,lt.getHour(),lt.getMinute(),lt.getSecond(),lt.getNano()),iv);
      
      s = "-3 days -04:05:06";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      lt = LocalTime.parse("04:05:06");
      assertEquals("Invalid interval!",new Interval(-1,3,lt.getHour(),lt.getMinute(),lt.getSecond(),lt.getNano()),iv);
      
      s = "3 days";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      assertEquals("Invalid interval!",new Interval(1,3,0,0,0,0),iv);
      
      s = "04:05:06";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      lt = LocalTime.parse("04:05:06");
      assertEquals("Invalid interval!",new Interval(1,0,lt.getHour(),lt.getMinute(),lt.getSecond(),lt.getNano()),iv);
          
      s = "-04:05:06";
      iv = PostgresLiterals.parseInterval(s);
      System.out.println(s+": "+SqlLiterals.formatIntervalLiteral(iv));
      lt = LocalTime.parse("04:05:06");
      assertEquals("Invalid interval!",new Interval(-1,0,lt.getHour(),lt.getMinute(),lt.getSecond(),lt.getNano()),iv);
    }
    catch (ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  }
}
