/*== Interval.java =====================================================
Interval implements a date-time interval.
Version     : $Id: BU.java 394 2014-10-28 14:21:07Z hartwig $
Application : Utilities
Description : Interval implements a date-time interval.
              It is similar to a combination of Duration and Period
              in Java 8.
              It caters for the wish to add integer amounts of years,
              months, days to dates, although the number of seconds
              varies for these units.
              It also extends the resolution to milliseconds.
              It is useful for the INTERVAL data type of ISO SQL.
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2016
Created    : 28.04.2016, Hartwig Thomas
======================================================================*/
package ch.enterag.sqlparser;

import java.io.*;
import java.util.*;
import javax.xml.datatype.*;

/*====================================================================*/
/** Implements a date-time interval in integer years, months, days and 
 * with a resolution of nanoseconds.
 @author Hartwig Thomas
 */
public class Interval
  implements Serializable
{
  private static final long serialVersionUID = -1705264794060459365L;
  private int _iSign = 1;
  /** get the sign of the interval.
   * @return sign of the interval.
   */
  public int getSign() { return _iSign; }
  /** set the sign of the interval.
   * The sign must be 1 or -1.
   * @param iSign sign of the interval.
   */
  public void setSign(int iSign)
  {
    if ((iSign == 1) || (iSign == -1)) 
      _iSign = iSign;
    else
      throw new IllegalArgumentException("Sign must be 1 or -1!");
  } /* setSign */
  
  private int _iYears = 0;
  /*------------------------------------------------------------------*/
  /** get the number of years in the interval.
   * @return years in the interval.
   */
  public int getYears() { return _iYears; }
  /*------------------------------------------------------------------*/
  /** set the number of years in the interval.
   * The number of years must be non-negative. (Use setSign() to change the
   * sign of the interval.)
   * Setting the years value will set the days ... nanoseconds to zero
   * @param iYears years in the interval.
   */
  public void setYears(int iYears)
  { 
    if (iYears >= 0)
    {
    _iYears = iYears;
    _iDays = 0;
    _iMinutes = 0;
    _iSeconds = 0;
    _lNanoSeconds = 0;
    }
    else
      throw new IllegalArgumentException("Years value must be non-negative. Use setSign() to change the sign of the interval.");
  } /* setYears */
  
  private int _iMonths = 0;
  /*------------------------------------------------------------------*/
  /** get the number of months in the interval.
   * @return months in the interval.
   */
  public int getMonths() { return _iMonths; }
  /*------------------------------------------------------------------*/
  /** set the number of months in the interval.
   * The number of months must be non-negative. (Use setSign() to change the
   * sign of the interval.) The number of months must be less than 12.
   * Setting the months value will set the days ... nanoseconds to zero.
   * @param iMonths months in the interval.
   */
  public void setMonths(int iMonths)
  {
    if ((iMonths >= 0) && (iMonths < 12))
    {
      _iMonths = iMonths;
      _iDays = 0;
      _iMinutes = 0;
      _iSeconds = 0;
      _lNanoSeconds = 0;
    }
    else
      throw new IllegalArgumentException("Months value must be non-negative and less than 12. Use setSign() to change the sign of the interval.");
  } /* setMonths */

  private int _iDays = 0;
  /*------------------------------------------------------------------*/
  /** get the number of days in the interval.
   * @return days in the interval.
   */
  public int getDays() { return _iDays; }
  /*------------------------------------------------------------------*/
  /** set the number of days in the interval.
   * The number of days must be non-negative. (Use setSign() to change the sign
   * of the interval.)
   * Setting the days value will set the years and months values to zero.
   * @param iDays days in the interval.
   */
  public void setDays(int iDays)
  {
    if (iDays >= 0)
    {
      _iDays = iDays;
      _iYears = 0;
      _iMonths = 0;
    }
    else
      throw new IllegalArgumentException("Days value must be non-negative. Use setSign() to change the sign of the interval.");
  } /* setDays */
  
  private int _iHours = 0;
  /*------------------------------------------------------------------*/
  /** get the number of hours in the interval.
   * @return hours in the interval.
   */
  public int getHours() { return _iHours; }
  /*------------------------------------------------------------------*/
  /** set the number of hours in the interval.
   * The number of hours must be non-negative. (Use setSign() to change the
   * sign of the interval.) The number of hours must be less than 24.
   * Setting the hours value will set the years and months values to zero.
   * @param iHours hours in the interval.
   */
  public void setHours(int iHours) 
  {
    if ((iHours >= 0) && (iHours < 24))
    {
      _iHours = iHours; 
      _iYears = 0;
      _iMonths = 0;
    }
    else
      throw new IllegalArgumentException("Hours value must be non-negative and less than 24. Use setSign() to change the sign of the interval.");
  } /* setHours */
  
  private int _iMinutes = 0;
  /*------------------------------------------------------------------*/
  /** get the number of minutes in the interval.
   * @return minutes in the interval.
   */
  public int getMinutes() { return _iMinutes; }
  /*------------------------------------------------------------------*/
  /** set the number of minutes in the interval.
   * The number of minutes must be non-negative. (Use setSign() to change the
   * sign of the interval.) The number of minutes must be less than 60.
   * Setting the minutes value will set the years and months values to zero.
   * @param iMinutes minutes in the interval.
   */
  public void setMinutes(int iMinutes)
  {
    if ((iMinutes >= 0) && (iMinutes < 60))
    {
      _iMinutes = iMinutes;
      _iYears = 0;
      _iMonths = 0;
    }
    else
      throw new IllegalArgumentException("Minutes value must be non-negative and less than 60. Use setSign() to change the sign of the interval.");
  } /* setMinutes */
  
  private int _iSeconds = 0;
  /*------------------------------------------------------------------*/
  /** get the number of seconds in the interval.
   * @return seconds in the interval.
   */
  public int getSeconds() { return _iSeconds; }
  /*------------------------------------------------------------------*/
  /** set the number of seconds in the interval.
   * The number of seconds must be non-negative. (Use setSign() to change the
   * sign of the interval.) The number of seconds must be less than 61 (because of leap seconds).
   * Setting the seconds value will set the years and months values to zero.
   * @param iSeconds seconds in the interval.
   */
  public void setSeconds(int iSeconds)
  {
    if ((iSeconds >= 0) && (iSeconds < 61))
    {
      _iSeconds = iSeconds;
      _iYears = 0;
      _iMonths = 0;
    }
    else
      throw new IllegalArgumentException("Seconds value must be non-negative and less than 61. Use setSign() to change the sign of the interval.");
  } /* setSeconds */

  private long _lNanoSeconds = 0;
  /*------------------------------------------------------------------*/
  /** get the number of nano seconds in the interval.
   * @return nano seconds in the interval.
   */
  public long getNanoSeconds() { return _lNanoSeconds; }
  /*------------------------------------------------------------------*/
  /** set the number of nano seconds in the interval.
   * The number of nano seconds must be non-negative. (Use setSign() to change the
   * sign of the interval.) The number of nano seconds must be less than 1'000'000'000.
   * Setting the nano seconds value will set the years and months values to zero.
   * @param lNanoSeconds nano seconds in the interval.
   */
  public void setNanoSeconds(long lNanoSeconds)
  {
    if ((lNanoSeconds >= 0) && (lNanoSeconds < 1000000000))
      _lNanoSeconds = lNanoSeconds;
    else
      throw new IllegalArgumentException("Nano seconds must be non-negative and between 0 and 1'000'000'000!");
  } /* setNanoSeconds */
  /*------------------------------------------------------------------*/
  /** get the number of milliseconds (rounded down from nanoseconds) in the interval.
   * @return milliseconds in the interval.
   */
  public int getMilliSeconds() { return (int)(getNanoSeconds()/1000000l); }
  /*------------------------------------------------------------------*/
  /** set the number of nanoseconds in the interval given by milliseconds.
   * @param iMilliseconds milliseconds.
   */
  public void setMilliseconds(int iMilliseconds)
  {
    long lNanoSeconds = 1000000l*iMilliseconds;
    setNanoSeconds(lNanoSeconds);
  } 

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean equals(Object o)
  {
    boolean bEquals = false;
    if (o instanceof Interval)
    {
      Interval iv = (Interval)o;
      bEquals = true;
      if (bEquals && (getSign() != iv.getSign()))
        bEquals = false;
      if (bEquals &&  (getYears() != iv.getYears()))
        bEquals = false;
      if (bEquals && (getMonths() != iv.getMonths()))
        bEquals = false;
      if (bEquals && (getDays() != iv.getDays()))
        bEquals = false;
      if (bEquals && (getHours() != iv.getHours()))
        bEquals = false;
      if (bEquals && (getMinutes() != iv.getMinutes()))
        bEquals = false;
      if (bEquals && (getSeconds() != iv.getSeconds()))
        bEquals = false;
      if (bEquals && (getNanoSeconds() != iv.getNanoSeconds()))
        bEquals = false;
    }
    return bEquals;
  } /* equals */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return SqlLiterals.formatIntervalLiteral(this);
  } /* toString */
  
  /*------------------------------------------------------------------*/
  /** construct a YEAR TO MONTH interval.
   * @param iSign sign (-1 or 1)
   * @param iYears interval years.
   * @param iMonths interval months.
   */
  public Interval(int iSign, int iYears, int iMonths)
  {
    setSign(iSign);
    setYears(iYears);
    setMonths(iMonths);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** construct a DAY time interval.
   * @param iSign sign (-1 or 1)
   * @param iDays days in the interval.
   * @param iHours hours in the interval.
   * @param iMinutes minutes in the interval.
   * @param iSeconds seconds in the interval.
   * @param lNanoSeconds nanoseconds in the interval.
   */
  public Interval(int iSign, int iDays, int iHours, int iMinutes, int iSeconds, long lNanoSeconds)
  {
    setSign(iSign);
    setDays(iDays);
    setHours(iHours);
    setMinutes(iMinutes);
    setSeconds(iSeconds);
    setNanoSeconds(lNanoSeconds);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** create an interval between two calendar dates.
   * @param cal1 first calendar date.
   * @param cal2 second calendar date.
   * @return interval.
   */
  public static Interval between(Calendar cal1, Calendar cal2)
  {
    Interval interval = new Interval(1,0,0,0,0,0l); // NULL interval
    int iSign = 1;
    if (cal1.after(cal2))
    {
      iSign = -1;
      Calendar cal = cal2;
      cal2 = cal1;
      cal1 = cal;
    }
    int iYears = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
    int iMonths = cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
    int iDays = cal2.get(Calendar.DATE) - cal1.get(Calendar.DATE);
    int iHours = cal2.get(Calendar.HOUR_OF_DAY) - cal1.get(Calendar.HOUR_OF_DAY);
    int iMinutes = cal2.get(Calendar.MINUTE) - cal1.get(Calendar.MINUTE);
    int iSeconds = cal2.get(Calendar.SECOND) - cal1.get(Calendar.SECOND);
    int iMillis = cal2.get(Calendar.MILLISECOND) - cal1.get(Calendar.MILLISECOND);
    boolean bDayToTime = (iDays != 0) || (iHours != 0) || (iMinutes != 0) || (iSeconds != 0) || (iMillis != 0);
    boolean bYearToMonth = (iYears != 0) || (iMonths != 0);
    if (bDayToTime && bYearToMonth)
    {
      /* get the difference in days (ignore leap seconds!) */
      long l = cal2.getTimeInMillis() - cal1.getTimeInMillis();
      iMillis = (int)(l % 1000);
      l = l / 1000;
      iSeconds = (int)(l % 60);
      l = l / 60;
      iMinutes = (int)(l % 60);
      l = l / 60;
      iHours = (int)(l % 24);
      l = l / 24;
      iDays = (int)l;
      bYearToMonth = false;
    }
    if (bYearToMonth)
    {
      if (iMonths < 0)
      {
        iYears--;
        iMonths = iMonths + 12;
      }
      interval = new Interval(iSign,iYears,iMonths);
    }
    else if (bDayToTime)
    {
      if (iMillis < 0)
      {
        iSeconds--;
        iMillis = iMillis + 1000;
      }
      if (iSeconds < 0)
      {
        iMinutes--;
        iSeconds = iSeconds + 60;
      }
      if (iMinutes < 0)
      {
        iHours --;
        iMinutes = iMinutes + 60;
      }
      if (iHours < 0)
      {
        iDays--;
        iHours = iHours + 24;
      }
      interval = new Interval(iSign,iDays,iHours,iMinutes,iSeconds,0l);
      interval.setMilliseconds(iMillis);
    }
    return interval;
  } /* between */
  
  /*------------------------------------------------------------------*/
  /** add this interval to the given calendar date.
   * @param cal calendar date.
   * @return calendar date increased by interval.
   */
  public Calendar addTo(Calendar cal)
  {
    Calendar calResult = new GregorianCalendar();
    calResult.setTime(cal.getTime());
    calResult.add(Calendar.YEAR, getSign()*getYears());
    calResult.add(Calendar.MONTH, getSign()*getMonths());
    calResult.add(Calendar.DATE, getSign()*getDays());
    calResult.add(Calendar.HOUR_OF_DAY, getSign()*getHours());
    calResult.add(Calendar.MINUTE, getSign()*getMinutes());
    calResult.add(Calendar.SECOND, getSign()*getSeconds());
    calResult.add(Calendar.MILLISECOND, getSign()*getMilliSeconds());
    return calResult;
  } /* addTo */
  
  /*------------------------------------------------------------------*/
  /** convert this interval to a Duration instance.
   * @return duration corresponding to this interval.
   */
  public Duration toDuration()
  {
    Duration duration = null;
    try
    {
      DatatypeFactory df = DatatypeFactory.newInstance();
      if ((getDays() == 0) && 
          (getMinutes() == 0) && 
          (getSeconds() == 0) && 
          (getMilliSeconds() == 0))
        duration = df.newDurationYearMonth(getSign() > 0? true: false, getYears(), getMonths());
      else
      {
        long lMilliSeconds = getDays();
        lMilliSeconds = 24*lMilliSeconds+getHours();
        lMilliSeconds = 60*lMilliSeconds+getMinutes();
        lMilliSeconds = 60*lMilliSeconds+getSeconds();
        lMilliSeconds = 1000*lMilliSeconds+getMilliSeconds();
        lMilliSeconds = getSign()*lMilliSeconds;
        duration = df.newDurationDayTime(lMilliSeconds);
      }
    }
    catch(DatatypeConfigurationException dcfe){}
    return duration;
  } /* toDuration */
  
  /*------------------------------------------------------------------*/
  /** create a new Interval instance from a Duration instance.
   * @param duration duration
   * @return new interval instance.
   */
  public static Interval fromDuration(Duration duration)
  {
    Interval iv = null;
    int iSign = duration.getSign();
    iSign = iSign == 0? 1: iSign;
    if ((duration.getDays() == 0) &&
        (duration.getMinutes() == 0) &&
        (duration.getSeconds() == 0))
      iv = new Interval(iSign,duration.getYears(),duration.getMonths());
    else
    {
      long lMilliSeconds = duration.getTimeInMillis(new Date(0l));
      if (lMilliSeconds < 0)
        lMilliSeconds = -lMilliSeconds;
      long lMillis = 1000*60*60*24;
      int iDays = (int)(lMilliSeconds / lMillis);
      lMilliSeconds = lMilliSeconds % lMillis;
      lMillis = 1000*60*60;
      int iHours = (int)(lMilliSeconds/lMillis);
      lMilliSeconds = lMilliSeconds % lMillis;
      lMillis = 1000*60;
      int iMinutes = (int)(lMilliSeconds / lMillis);
      lMilliSeconds = lMilliSeconds % lMillis;
      lMillis = 1000;
      int iSeconds = (int)(lMilliSeconds / lMillis);
      lMilliSeconds = lMilliSeconds % lMillis;
      long lNanoSeconds = 1000000l*lMilliSeconds;
      iv = new Interval(iSign,iDays,iHours,iMinutes,iSeconds,lNanoSeconds);
    }
    return iv;
  } /* fromDuration */

} /* Interval */
