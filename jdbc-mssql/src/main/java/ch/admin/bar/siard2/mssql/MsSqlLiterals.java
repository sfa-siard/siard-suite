package ch.admin.bar.siard2.mssql;

import ch.enterag.utils.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.enums.*;

public abstract class MsSqlLiterals extends SqlLiterals
{
  private  static final String sBYTE_LITERAL_PREFIX = "0x";
  
  /*------------------------------------------------------------------*/
  /** format byte buffer value.
   * @param bufValue byte buffer value to be formatted.
   * @return byte string literal.
   */
  public static String formatBytesLiteral(byte[] bufValue)
  {
    String sFormatted = sNULL;
    if (bufValue != null)
      sFormatted = sBYTE_LITERAL_PREFIX+BU.toHex(bufValue);
    return sFormatted;
  } /* formatBytesLiteral */
  
  /*------------------------------------------------------------------*/
  /** format a date value
   * MSSQL does not like date/time prefixes.
   * @param dateValue date value to be formatted.
   * @return date literal.
   */
  public static String formatDateLiteral(java.sql.Date dateValue)
  {
    String sFormatted = sNULL;
    if (dateValue != null)
      sFormatted = formatStringLiteral(sdfDATE.format(dateValue));
    return sFormatted;
  } /* formatDateLiteral */
  
  /*------------------------------------------------------------------*/
  /** format a time value
   * MSSQL does not like date/time prefixes.
   * @param timeValue time value to be formatted.
   * @return time literal.
   */
  public static String formatTimeLiteral(java.sql.Time timeValue)
  {
    String sFormatted = sNULL;
    if (timeValue != null)
    {
      sFormatted = sdfTIME.format(timeValue);
      long lMillis = timeValue.getTime() % 1000;
      if (lMillis != 0)
      {
        String sMillis = String.valueOf(lMillis);
        while (sMillis.length() < 3)
          sMillis = "0"+sMillis;
        while (sMillis.endsWith("0"))
          sMillis = sMillis.substring(0,sMillis.length()-1);
        sFormatted = sFormatted + sDOT + sMillis;
      }
      sFormatted = formatStringLiteral(sFormatted);
    }
    return sFormatted;
  } /* formatTimeLiteral */
  
  /*------------------------------------------------------------------*/
  /** format a timestamp value.
   * MSSQL does not like date/time prefixes.
   * @param tsValue timestamp value to be formatted.
   * @return timestamp literal.
   */
  public static String formatTimestampLiteral(java.sql.Timestamp tsValue)
  {
    String sFormatted = sNULL;
    if (tsValue != null)
    {
      sFormatted = sdfTIMESTAMP.format(tsValue);
      int iNanos = tsValue.getNanos();
      if (iNanos > 0)
      {
        String sNanos = String.valueOf(iNanos);
        while (sNanos.length() < 9)
          sNanos = "0" + sNanos;
        while (sNanos.endsWith("0"))
          sNanos = sNanos.substring(0,sNanos.length()-1);
        sFormatted = sFormatted + sDOT + sNanos;
      }
      sFormatted = formatStringLiteral(sFormatted);
    }
    return sFormatted;
  } /* formatTimestampLiteral */

  /*------------------------------------------------------------------*/
  /** format an interval value
   * In MSSQL an interval is serialized to an "image".
   * @param ivValue interval value to be formatted.
   * @return interval literal.
   */
  public static String formatIntervalLiteral(Interval ivValue)
  {
    return formatStringLiteral(SqlLiterals.formatIntervalLiteral(ivValue));
  } /* formatIntervalLiteral */
  
  /*------------------------------------------------------------------*/
  /** format a boolean literal value.
   * in MSSQL the BOOLEAN data type is realized as a "bit" data type. 
   * @param blValue boolean value to be formatted.
   * @return boolean (bit) literal.
   */
  public static String formatBooleanLiteral(BooleanLiteral blValue)
  {
    String sFormatted = null;
    switch (blValue)
    {
      case UNKNOWN:
        sFormatted = K.NULL.getKeyword();
        break;
      case FALSE:
        sFormatted = "0";
        break;
      case TRUE:
        sFormatted = "1";
        break;
    }
    return sFormatted;
  } /* formatBooleanLiteral */
  
} /* class MsSqlSqlLiteral */
