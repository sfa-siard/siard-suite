/*======================================================================
Utility for handling dates in all kinds of formats. 
Version     : $Id: DU.java 538 2016-02-17 14:11:41Z hartwig $
Application : Utilities
Description : Converts all kind of dates to and from string representations.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2015, Enter AG, Rüti ZH, Switzerland
Created    : 22.10.2015, Hartwig Thomas
======================================================================*/
package ch.enterag.utils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/*====================================================================*/

/**
 * Utility for handling date strings.
 *
 * @author Hartwig
 */
public class DU {
    private static final String _sZ = "Z";
    private static final SimpleDateFormat _sdfXS_DATE = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat _sdfXS_TIME = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat _sdfXS_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final DecimalFormat _dfMILLIS = new DecimalFormat("000");
    private static final DecimalFormat _dfNANOS = new DecimalFormat("000000000");
    public static java.sql.Date dateMINIMUM_SQL = null;
    public static java.sql.Date dateMAXIMUM_SQL = null;
    public static java.sql.Timestamp tsMINIMUM_SQL = null;
    public static java.sql.Timestamp tsMAXIMUM_SQL = null;

    static {
        _sdfXS_TIME.setTimeZone(TZ.getUtcTimeZone());
        _sdfXS_DATE_TIME.setTimeZone(TZ.getUtcTimeZone());
        try {
            SimpleDateFormat sdfInternal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
            Date dateMinimum = sdfInternal.parse("0001-01-01 00:00:00.000 UTC");
            Date dateMaximum = sdfInternal.parse("9999-12-31 00:00:00.000 UTC");
            dateMINIMUM_SQL = new java.sql.Date(dateMinimum.getTime());
            dateMAXIMUM_SQL = new java.sql.Date(dateMaximum.getTime());
            dateMaximum = sdfInternal.parse("9999-12-31 23:59:59.999 UTC");
            tsMINIMUM_SQL = new java.sql.Timestamp(dateMinimum.getTime());
            tsMAXIMUM_SQL = new java.sql.Timestamp(dateMaximum.getTime());
            tsMAXIMUM_SQL.setNanos(999999999);
        } catch (ParseException pe) {
            System.err.println(pe.getClass().getName() + ": " + pe.getMessage());
        }
    }

    /**
     * one instance per language
     */
    private static Map<String, DU> _map = new HashMap<String, DU>();
    /**
     * the simple date format of this instance
     */
    private SimpleDateFormat _sdf = null;

    public String getDateFormat() {
        return _sdf.toPattern();
    }

    private SimpleDateFormat _sdfDate = null;

    public String getDateOnlyFormat() {
        return (_sdfDate == null) ? null : _sdfDate.toPattern();
    }

    private SimpleDateFormat _sdfTime = null;

    public String getTimeOnlyFormat() {
        return (_sdfTime == null) ? null : _sdfTime.toPattern();
    }

    private SimpleDateFormat _sdfTimestamp = null;

    public String getDateAndTimeFormat() {
        return (_sdfTimestamp == null) ? null : _sdfTimestamp.toPattern();
    }

    /*------------------------------------------------------------------*/

    /**
     * constructor creates an instance of this language with this date format.
     *
     * @param sLanguage   language (2 characters)
     * @param sDateFormat date format (SimpleDateFormat pattern)
     */
    private DU(String sLanguage, String sDateFormat) {
        // try to split it into a date and a time portion
        int iFirstDateSymbol = -1;
        int iLastDateSymbol = -1;
        int iFirstTimeSymbol = -1;
        int iLastTimeSymbol = -1;
        for (int i = 0; i < sDateFormat.length(); i++) {
            char c = sDateFormat.charAt(i);
            if ("GyYMwWDdFEu".indexOf(c) >= 0) {
                if (iFirstDateSymbol < 0)
                    iFirstDateSymbol = i;
                iLastDateSymbol = i;
            } else if ("aHkKhmsSzZX".indexOf(c) >= 0) {
                if (iFirstTimeSymbol < 0)
                    iFirstTimeSymbol = i;
                iLastTimeSymbol = i;
            }
        }
        if (iFirstDateSymbol >= 0)
            _sdfDate = new SimpleDateFormat(sDateFormat.substring(iFirstDateSymbol, iLastDateSymbol + 1));
        if (iFirstTimeSymbol >= 0)
            _sdfTime = new SimpleDateFormat(sDateFormat.substring(iFirstTimeSymbol, iLastTimeSymbol + 1));
        _sdf = new SimpleDateFormat(sDateFormat);
        _sdfTimestamp = new SimpleDateFormat(sDateFormat);
        _map.put(sLanguage, this);
    } /* constructor DU */

    /*------------------------------------------------------------------*/

    /**
     * factory gets the instance corresponding to the language and the
     * date format.
     *
     * @param sLanguage   language (2 characters)
     * @param sDateFormat date format (SimpleDateFormat pattern)
     * @return DU instance corresponding to the language.
     */
    public static DU getInstance(String sLanguage, String sDateFormat) {
        DU du = _map.get(sLanguage);
        if ((du == null) || (!du.getDateFormat().equals(sDateFormat)))
            du = new DU(sLanguage, sDateFormat);
        return du;
    } /* factory getInstance */

    /*------------------------------------------------------------------*/

    /**
     * convert a date to a string
     *
     * @param date date to be converted.
     * @return string representation of date.
     */
    public String fromDate(Date date) {
        String s = "";
        if (date != null)
            s = _sdf.format(date);
        return s;
    } /* fromDate */

    /*------------------------------------------------------------------*/

    /**
     * convert a string to a date.
     *
     * @param s string to be converted.
     * @return date represented by string.
     * @throws ParseException if string cannot be parsed as a date
     *                        using the simple date format used at creation time.
     */
    public Date toDate(String s)
            throws ParseException {
        Date date = null;
        if (s.length() > 0)
            date = _sdf.parse(s);
        return date;
    } /* toDate */

    /*------------------------------------------------------------------*/

    /**
     * convert an SQL date (date only) to a string
     *
     * @param date SQL date to be converted.
     * @return string representation of SQL date.
     */
    public String fromSqlDate(java.sql.Date date) {
        String s = "";
        if ((date != null) && (_sdfDate != null))
            s = _sdfDate.format(date);
        return s;
    } /* fromSqlDate */

    /*------------------------------------------------------------------*/

    /**
     * convert an SQL time (time only) to a string
     *
     * @param time SQL time to be converted.
     * @return string representation of SQL time.
     */
    public String fromSqlTime(java.sql.Time time) {
        String s = "";
        if ((time != null) && (_sdfTime != null))
            s = _sdfTime.format(time);
        return s;
    } /* fromSqlTime */

    /*------------------------------------------------------------------*/

    /**
     * convert an SQL timestamp to a string.
     * N.B.: loses precision, because not more than milliseconds can be
     * specified.
     *
     * @param ts SQL timestamp to be converted.
     * @return string representation of SQL timestamp.
     */
    public String fromSqlTimestamp(java.sql.Timestamp ts) {
        String s = "";
        if ((ts != null) && (_sdfTimestamp != null))
            s = _sdfTimestamp.format(ts);
        return s;
    } /* fromSqlTimestamp */

    /*------------------------------------------------------------------*/

    /**
     * convert a gregorian calendar date to a date.
     *
     * @param gc gregorian calendar date to be converted.
     * @return date represented by gregorian calendar date.
     */
    public Date toDate(GregorianCalendar gc) {
        Date date = null;
        if (gc != null)
            date = gc.getTime();
        return date;
    } /* toDate */

    /*------------------------------------------------------------------*/

    /**
     * convert a gregorian calendar date in XML format to a date.
     *
     * @param xgc gregorian calendar date in XML format to be converted.
     * @return date represented by gregorian calendar date in XML format.
     */
    public Date toDate(XMLGregorianCalendar xgc) {
        Date date = null;
        if (xgc != null)
            date = toDate(xgc.toGregorianCalendar());
        return date;
    } /* toDate */

    /*------------------------------------------------------------------*/

    /**
     * convert a Gregorian calendar date to a string
     *
     * @param gc Gregorian calendar date to be converted.
     * @return string representation of date.
     */
    public String fromGregorianCalendar(GregorianCalendar gc) {
        String s = "";
        if (gc != null)
            s = _sdf.format(gc.getTime());
        return s;
    } /* fromGregorianCalendar */

    /*------------------------------------------------------------------*/

    /**
     * convert a string to a gregorian calendar date.
     *
     * @param s string to be converted.
     * @return gregorian calendar date represented by string.
     * @throws ParseException if string cannot be parsed as a gregorian
     *                        calendar date using the simple date format used at creation
     *                        time.
     */
    public GregorianCalendar toGregorianCalendar(String s)
            throws ParseException {
        GregorianCalendar gc = null;
        Date date = toDate(s);
        if (date != null) {
            gc = new GregorianCalendar();
            gc.setTime(date);
        }
        return gc;
    } /* toGregorianCalendar */

    /*------------------------------------------------------------------*/

    /**
     * convert a date to a gregorian calendar date.
     *
     * @param date to be converted.
     * @return gregorian calendar date represented by date (using current Locale).
     */
    public GregorianCalendar toGregorianCalendar(Date date) {
        GregorianCalendar gc = null;
        if (date != null) {
            gc = new GregorianCalendar();
            gc.setTime(date);
        }
        return gc;
    } /* toGregorianCalendar */

    /*------------------------------------------------------------------*/

    /**
     * convert a gregorian calendar date in XML format to a gregorian
     * calendar date.
     *
     * @param xgc gregorian calendar date in XML format to be converted.
     * @return gregorian calendar date represented by gregorian calendar
     * date in XML format (using current Locale).
     */
    public GregorianCalendar toGregorianCalendar(XMLGregorianCalendar xgc) {
        GregorianCalendar gc = null;
        if (xgc != null)
            gc = xgc.toGregorianCalendar();
        return gc;
    } /* toGregorianCalendar */

    /*------------------------------------------------------------------*/

    /**
     * convert a Gregorian calendar date in XML format to a string
     *
     * @param xgc Gregorian calendar date in XML format to be converted.
     * @return string representation of date.
     */
    public String fromXmlGregorianCalendar(XMLGregorianCalendar xgc) {
        String s = "";
        if (xgc != null)
            s = _sdf.format(xgc.toGregorianCalendar().getTime());
        return s;
    } /* fromXmlGregorianCalendar */

    /*------------------------------------------------------------------*/

    /**
     * convert a string to a gregorian calendar date in XML format.
     *
     * @param s string to be converted.
     * @return gregorian calendar date in XML format represented by string.
     * @throws ParseException if string cannot be parsed as a gregorian
     *                        calendar date in XML format using the simple date format
     *                        used at creation time..
     */
    public XMLGregorianCalendar toXmlGregorianCalendar(String s)
            throws ParseException {
        XMLGregorianCalendar xgc = null;
        GregorianCalendar gc = toGregorianCalendar(s);
        if (gc != null) {
            try {
                xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (DatatypeConfigurationException dtce) {
                System.err.println(dtce.getClass().getName() + ": " + dtce.getMessage());
            }
        }
        return xgc;
    } /* toXmlGregorianCalendar */

    /*------------------------------------------------------------------*/

    /**
     * convert a date to a gregorian calendar date in XML format.
     *
     * @param date to be converted.
     * @return gregorian calendar date in XML format represented by date.
     */
    public XMLGregorianCalendar toXmlGregorianCalendar(Date date) {
        XMLGregorianCalendar xgc = null;
        if (date != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(date);
            try {
                xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (DatatypeConfigurationException dtce) {
                System.err.println(dtce.getClass().getName() + ": " + dtce.getMessage());
            }
        }
        return xgc;
    } /* toXmlGregorianCalendar */

    /*------------------------------------------------------------------*/

    /**
     * convert a gregorian calendar date to a gregorian calendar date
     * in XML format.
     *
     * @param gc gregorian calendar date to be converted.
     * @return gregorian calendar date in XML format represented by
     * gregorian calendar date.
     */
    public XMLGregorianCalendar toXmlGregorianCalendar(GregorianCalendar gc) {
        XMLGregorianCalendar xgc = null;
        if (gc != null) {
            try {
                xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (DatatypeConfigurationException dtce) {
                System.err.println(dtce.getClass().getName() + ": " + dtce.getMessage());
            }
        }
        return xgc;
    } /* toXmlGregorianCalendar */

    /*------------------------------------------------------------------*/

    /**
     * convert a java.sql.Date to xs:date format.
     *
     * @param date date value.
     * @return xs:date representation.
     */
    public String toXsDate(java.sql.Date date) {
        if (date.after(dateMAXIMUM_SQL)) {
            System.err.println("Converted illegal date " + _sdf.format(date) + " to " + _sdf.format(dateMAXIMUM_SQL));
            date = dateMAXIMUM_SQL;
        }
        if (date.before(dateMINIMUM_SQL)) {
            System.err.println("Converted illegal date " + _sdf.format(date) + " to " + _sdf.format(dateMINIMUM_SQL));
            date = dateMINIMUM_SQL;
        }
        String s = _sdfXS_DATE.format(date);
        return s;
    } /* toXsDate */

    /*------------------------------------------------------------------*/

    /**
     * convert XML representation of xs:date to java.sql.Date.
     * NB.: Resulting date will be restricted to 01.01.01 to 31.12.9999.
     *
     * @param s date as xs:date.
     * @return date value.
     * @throws ParseException if string cannot be parsed as a xs:date.
     */
    public java.sql.Date fromXsDate(String s)
            throws ParseException {
        java.sql.Date d = null;
        Date date = _sdfXS_DATE.parse(s);
        d = new java.sql.Date(date.getTime());
        if (d.after(dateMAXIMUM_SQL)) {
            System.err.println("Converted illegal date " + _sdf.format(d) + " to " + _sdf.format(dateMAXIMUM_SQL));
            d = dateMAXIMUM_SQL;
        }
        if (d.before(dateMINIMUM_SQL)) {
            System.err.println("Converted illegal date " + _sdf.format(d) + " to " + _sdf.format(dateMINIMUM_SQL));
            d = dateMINIMUM_SQL;
        }
        return d;
    } /* fromXsDate */

    /*------------------------------------------------------------------*/

    /**
     * convert a java.sql.Time to xs:time format.
     *
     * @param time time value.
     * @return xs:time representation.
     */
    public String toXsTime(java.sql.Time time) {
        StringBuilder sbTime = new StringBuilder();
        int iMillis = (int) (time.getTime() % 1000);
        sbTime.append(_sdfXS_TIME.format(time));
        if (iMillis > 0) {
            sbTime.append(".");
            /* format left padding with "0"s */
            String sDecimals = _dfMILLIS.format(iMillis);
            /* remove trailing zeros */
            sDecimals = sDecimals.replaceAll("0*$", "");
            sbTime.append(sDecimals);
        }
        sbTime.append(_sZ);
        return sbTime.toString();
    } /* toXsTime */

    /*------------------------------------------------------------------*/

    /**
     * convert an xs:time string to java.sql.Time.
     * N.B.: will truncate input to milliseconds!
     *
     * @param s as xs:time.
     * @return time value.
     * @throws ParseException if string cannot be parsed as a xs:date.
     */
    public java.sql.Time fromXsTime(String s)
            throws ParseException {
        java.sql.Time time = null;
        if (s.endsWith(_sZ))
            s = s.substring(0, s.length() - 1);
        int iMillis = 0;
        int iDecimalPoint = s.lastIndexOf('.');
        if (iDecimalPoint >= 0) {
            String sDecimals = s.substring(iDecimalPoint + 1);
            s = s.substring(0, iDecimalPoint);
            /* truncate to 3 places */
            if (sDecimals.length() > 3)
                sDecimals = sDecimals.substring(0, 3);
            /* right padding with "0"s */
            if (sDecimals.length() < 3)
                sDecimals = String.format("%1$-3s", sDecimals).replace(" ", "0");
            iMillis = Integer.parseInt(sDecimals);
        }
        time = new java.sql.Time(_sdfXS_TIME.parse(s).getTime() + iMillis);
        return time;
    } /* fromXsTime */

    /*------------------------------------------------------------------*/

    /**
     * convert a java.sql.Timestamp to xs:dateTime format.
     *
     * @param ts timestamp value.
     * @return xs:dateTime representation.
     */
    public String toXsDateTime(java.sql.Timestamp ts) {
        if (ts.after(tsMAXIMUM_SQL)) {
            System.err.println("Converted illegal date/time " + _sdf.format(ts) + " to " + _sdf.format(tsMAXIMUM_SQL));
            ts = tsMAXIMUM_SQL;
        }
        if (ts.before(tsMINIMUM_SQL)) {
            System.err.println("Converted illegal date/time " + _sdf.format(ts) + " to " + _sdf.format(tsMINIMUM_SQL));
            ts = tsMINIMUM_SQL;
        }
        StringBuilder sbDateTime = new StringBuilder();
        sbDateTime.append(_sdfXS_DATE_TIME.format(ts));
        if (ts.getNanos() > 0) {
            sbDateTime.append(".");
            /* format left padding with "0"s */
            String sDecimals = _dfNANOS.format(ts.getNanos());
            /* remove trailing zeros */
            sDecimals = sDecimals.replaceAll("0*$", "");
            sbDateTime.append(_dfNANOS.format(ts.getNanos()));
        }
        sbDateTime.append(_sZ);
        return sbDateTime.toString();
    } /* toXsDateTime */

    /*------------------------------------------------------------------*/

    /**
     * convert an xs:dateTime string to java.sql.Timestamp.
     * N.B.: will truncate input to nanoseconds!
     *
     * @param s as xs:dateTime.
     * @return timestamp value.
     * @throws ParseException if string cannot be parsed as a xs:dateTime.
     */
    public java.sql.Timestamp fromXsDateTime(String s)
            throws ParseException {
        java.sql.Timestamp ts = null;
        if (s.endsWith(_sZ))
            s = s.substring(0, s.length() - 1);
        int iNanos = 0;
        int iDecimalPoint = s.lastIndexOf('.');
        if (iDecimalPoint >= 0) {
            String sDecimals = s.substring(iDecimalPoint + 1);
            s = s.substring(0, iDecimalPoint);
            /* truncate to 9 places */
            if (sDecimals.length() > 9)
                sDecimals = sDecimals.substring(0, 9);
            /* right padding with "0"s */
            sDecimals = String.format("%1$-9s", sDecimals).replace(" ", "0");
            ;
            iNanos = Integer.parseInt(sDecimals);
        }
        Date date = _sdfXS_DATE_TIME.parse(s);
        ts = new java.sql.Timestamp(date.getTime());
        ts.setNanos(iNanos);
        if (ts.after(tsMAXIMUM_SQL)) {
            System.err.println("Converted illegal date/time " + _sdf.format(ts) + " to " + _sdf.format(tsMAXIMUM_SQL));
            ts = tsMAXIMUM_SQL;
        }
        if (ts.before(tsMINIMUM_SQL)) {
            System.err.println("Converted illegal date/time " + _sdf.format(ts) + " to " + _sdf.format(tsMINIMUM_SQL));
            ts = tsMINIMUM_SQL;
        }
        return ts;
    } /* fromXsDateTime */

    /*------------------------------------------------------------------*/

    /**
     * convert a duration value to a xs:duration string.
     *
     * @param duration duration value.
     * @return xs:duration representation
     */
    public String toXsDuration(Duration duration) {
        String s = duration.toString();
        return s;
    } /* toXsDuration */

    /*------------------------------------------------------------------*/

    /**
     * convert an xs:duration string to Duration.
     *
     * @param s duration as xs:duration.
     * @return duration value.
     * @throws ParseException if string cannot be parsed as xs:duration.
     */
    public Duration fromXsDuration(String s)
            throws ParseException {
        Duration duration = null;
        try {
            duration = DatatypeFactory.newInstance().newDuration(s);
        } catch (DatatypeConfigurationException dtce) {
            System.err.println(dtce.getClass().getName() + ": " + dtce.getMessage());
        }
        return duration;
    } /* fromXsDate */

} /* class DU */
