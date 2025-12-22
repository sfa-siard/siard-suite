/*== Conversions.java ==================================================
Implements "best effort" conversions between database value types. 
Version     : $Id: Conversions.java 4 2015-01-12 14:30:07Z hartwigthomas $
Application : SIARD2
Description : Implements "best effort" conversions between database value types. 
------------------------------------------------------------------------
Copyright  : 2014, Enter AG, Zurich, Switzerland
Created    : 17.12.2014, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.access;

import ch.admin.bar.siard2.jdbc.AccessBlob;
import ch.admin.bar.siard2.jdbc.AccessClob;
import ch.admin.bar.siard2.jdbc.AccessNClob;
import ch.admin.bar.siard2.jdbc.AccessSqlXml;
import ch.enterag.sqlparser.Interval;

import javax.xml.datatype.Duration;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;


/** Implements "best effort" conversions between database value types.
 * @author Hartwig
 */
public abstract class Conversions {
    /** milli seconds per day */
    public static final long lMILLI_SECONDS_PER_DAY = 24 * 60 * 60 * 1000;


    /** convert source object using the given conversion method if it exists.
     * @param oSource source object (not null!).
     * @param sMethod name of method
     * @param clsResult type of result.
     * @return converted object or null.
     */
    @SuppressWarnings("unchecked")
    private static <T> T get(Object oSource, String sMethod, Class<T> clsResult) {
        T oResult = null;
        try {
            /* find a declared method matching sMethod */
            Method method = null;
            for (Class<?> cls = oSource.getClass(); (method == null) && (cls != null); cls = cls.getSuperclass())
                method = cls.getDeclaredMethod(sMethod);
            if (method != null)
                oResult = (T) method.invoke(oSource);
            else
                throw new IllegalArgumentException("Method " + sMethod + " not found!");
        } catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException(nsme.getClass()
                                                   .getName() + ": " + nsme.getMessage());
        } catch (InvocationTargetException ite) {
            throw new IllegalArgumentException(ite.getClass()
                                                  .getName() + ": " + ite.getMessage());
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException(iae.getClass()
                                                  .getName() + ": " + iae.getMessage());
        }
        return oResult;
    }


    /** convert object to Boolean or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting Boolean.
     */
    public static Boolean getBoolean(Object oSource) {
        Boolean b = null;
        if (oSource != null) {
            if (oSource instanceof String)
                b = Boolean.valueOf(Boolean.parseBoolean((String) oSource));
            else
                b = get(oSource, "booleanValue", Boolean.class);
            if (b == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           Boolean.class.getName());
        }
        return b;
    }


    /** convert object to Byte or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting Byte.
     */
    public static Byte getByte(Object oSource) {
        Byte by = null;
        if (oSource != null) {
            if (oSource instanceof String)
                by = Byte.valueOf(Byte.parseByte((String) oSource));
            else
                by = get(oSource, "byteValue", Byte.class);
            if (by == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           Byte.class.getName());
        }
        return by;
    }


    /** convert object to Short or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting Short.
     */
    public static Short getShort(Object oSource) {
        Short w = null;
        if (oSource != null) {
            if (oSource instanceof String)
                w = Short.valueOf(Short.parseShort((String) oSource));
            else
                w = get(oSource, "shortValue", Short.class);
            if (w == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           Short.class.getName());
        }
        return w;
    }


    /** convert object to Integer or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting Integer.
     */
    public static Integer getInt(Object oSource) {
        Integer i = null;
        if (oSource != null) {
            if (oSource instanceof String)
                i = Integer.valueOf(Integer.parseInt((String) oSource));
            else if (oSource instanceof Integer)
                i = (Integer) oSource;
            else if (oSource instanceof Long)
                i = Integer.valueOf(((Long) oSource).intValue());
            else
                i = get(oSource, "intValue", Integer.class);
            if (i == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           Integer.class.getName());
        }
        return i;
    }


    /** convert object to Long or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting Long.
     */
    public static Long getLong(Object oSource) {
        Long l = null;
        if (oSource != null) {
            if (oSource instanceof String)
                l = Long.valueOf(Long.parseLong((String) oSource));
            else
                l = get(oSource, "longValue", Long.class);
            if (l == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           Long.class.getName());
        }
        return l;
    }


    /** convert object to Float or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting Float.
     */
    public static Float getFloat(Object oSource) {
        Float f = null;
        if (oSource != null) {
            if (oSource instanceof String)
                f = Float.valueOf(Float.parseFloat((String) oSource));
            else
                f = get(oSource, "floatValue", Float.class);
            if (f == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           Float.class.getName());
        }
        return f;
    }


    /** convert object to Double or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting Double.
     */
    public static Double getDouble(Object oSource) {
        Double d = null;
        if (oSource != null) {
            if (oSource instanceof String)
                d = Double.valueOf(Double.parseDouble((String) oSource));
            else
                d = get(oSource, "doubleValue", Double.class);
            if (d == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           Double.class.getName());
        }
        return d;
    }


    /** convert object to BigInteger or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting BigInteger.
     */
    public static BigInteger getBigInteger(Object oSource) {
        BigInteger bi = null;
        if (oSource != null) {
            if (oSource instanceof BigInteger)
                bi = (BigInteger) oSource;
            else {
                /* from BigDecimal */
                bi = get(oSource, "toBigInteger", BigInteger.class);
                /* from Long */
                if (bi == null) {
                    Long l = null;
                    try {
                        l = getLong(oSource);
                    } catch (IllegalArgumentException iae) {
                    }
                    if (l != null)
                        bi = BigInteger.valueOf(l.longValue());
                }
            }
            if (bi == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           BigInteger.class.getName());
        }
        return bi;
    }


    /** convert object to BigDecimal or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting BigDecimal.
     */
    public static BigDecimal getBigDecimal(Object oSource) {
        BigDecimal bd = null;
        if (oSource != null) {
            if (oSource instanceof BigDecimal)
                bd = (BigDecimal) oSource;
            else if (oSource instanceof BigInteger)
                bd = new BigDecimal((BigInteger) oSource);
            else {
                Double d = null;
                try {
                    d = getDouble(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (d != null)
                    bd = BigDecimal.valueOf(d.doubleValue());
            }
            if (bd == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           BigDecimal.class.getName());
        }
        return bd;
    }


    /** convert object to BigDecimal or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @param iScale scale.
     * @return resulting BigDecimal.
     */
    public static BigDecimal getBigDecimal(Object oSource, int iScale) {
        BigDecimal bd = null;
        if (oSource != null) {
            if (oSource instanceof BigDecimal)
                bd = (BigDecimal) oSource;
            else if (oSource instanceof BigInteger)
                bd = new BigDecimal((BigInteger) oSource, iScale);
            else {
                Long l = null;
                try {
                    l = getLong(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (l != null)
                    bd = BigDecimal.valueOf(l.longValue(), iScale);
                if (bd == null) {
                    Double d = null;
                    try {
                        d = getDouble(oSource);
                    } catch (IllegalArgumentException iae) {
                    }
                    if (d != null)
                        bd = BigDecimal.valueOf(d.doubleValue());
                }
            }
            if (bd == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           BigDecimal.class.getName());
        }
        return bd;
    }


    /** convert object to String or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting String.
     */
    public static String getString(Object oSource) {
        String s = null;
        if (oSource != null) {
            if (oSource instanceof String)
                s = String.valueOf(oSource);
            else if (oSource instanceof NClob) {
                NClob nclob = (NClob) oSource;
                try {
                    s = nclob.getSubString(1L, (int) nclob.length());
                } catch (SQLException se) {
                    throw new IllegalArgumentException("String of NClob could not be retrieved!");
                }
            } else if (oSource instanceof Clob) {
                Clob clob = (Clob) oSource;
                try {
                    s = clob.getSubString(1L, (int) clob.length());
                } catch (SQLException se) {
                    throw new IllegalArgumentException("String of Clob could not be retrieved!");
                }
            } else if (oSource instanceof SQLXML) {
                SQLXML sqlxml = (SQLXML) oSource;
                try {
                    s = sqlxml.getString();
                } catch (SQLException se) {
                    throw new IllegalArgumentException("String of SQLXML could not be retrieved!");
                }
            }
        }
        return s;
    }


    /** convert object to java.sql.Date or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting java.sql.Date.
     */
    @SuppressWarnings("deprecation")
    public static java.sql.Date getDate(Object oSource) {
        /* milliseconds in UTC with hours, minutes, seconds and milliseconds 0 */
        java.sql.Date date = null;
        if (oSource != null) {
            if (oSource instanceof java.sql.Date)
                date = (java.sql.Date) oSource;
            else if (oSource instanceof java.sql.Timestamp) {
                /* if it is a Timestamp, remove the time of day */
                java.sql.Timestamp ts = (java.sql.Timestamp) oSource;
                date = new java.sql.Date(ts.getTime());
            } else if (oSource instanceof String) {
                /* if it is a string, then try ANSI "yyyy-MM-dd" */
                String s = (String) oSource;
                try {
                    date = java.sql.Date.valueOf(s);
                } catch (IllegalArgumentException iae) {
                }
                try {
                    date = new java.sql.Date(java.util.Date.parse(s));
                } catch (IllegalArgumentException iae) {
                }
            } else if (oSource instanceof java.util.Date) {
                /* if it is a java.util.Date, just use the milliseconds */
                java.util.Date d = (java.util.Date) oSource;
                date = new java.sql.Date(d.getTime());
            } else if (oSource instanceof Calendar) {
                /* if it is a java.util.Calendar, just use the milliseconds */
                Calendar c = (Calendar) oSource;
                date = new java.sql.Date(c.getTimeInMillis());
            } else {
                /* if it is a long, use them as milliseconds */
                Long l = null;
                try {
                    l = getLong(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (l != null)
                    date = new java.sql.Date(l);
            }
            if (date == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           java.sql.Date.class.getName());
        }
        return date;
    }


    /** convert object to java.sql.Time or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting java.sql.Time.
     */
    @SuppressWarnings("deprecation")
    public static java.sql.Time getTime(Object oSource) {
        /* milliseconds in UTC with year, month, date from zero epoch value January 1st, 1970 */
        java.sql.Time time = null;
        if (oSource != null) {
            if (oSource instanceof java.sql.Time)
                time = (java.sql.Time) oSource;
            else if (oSource instanceof java.sql.Timestamp) {
                /* if it is a Timestamp, set date portion to zero */
                java.sql.Timestamp ts = (java.sql.Timestamp) oSource;
                time = new java.sql.Time(ts.getTime());
            } else if (oSource instanceof String) {
                /* if it is a string, then try ANSI "hh:mm:ss" */
                String s = (String) oSource;
                try {
                    time = java.sql.Time.valueOf(s);
                } catch (IllegalArgumentException iae) {
                }
                try {
                    time = new java.sql.Time(java.util.Date.parse(s));
                } catch (IllegalArgumentException iae) {
                }
            } else if (oSource instanceof java.util.Date) {
                /* if it is a java.util.Date, just use the milliseconds */
                java.util.Date d = (java.util.Date) oSource;
                time = new java.sql.Time(d.getTime());
            } else if (oSource instanceof Calendar) {
                /* if it is a java.util.Calendar, just use the milliseconds */
                Calendar c = (Calendar) oSource;
                time = new java.sql.Time(c.getTimeInMillis());
            } else {
                /* if it is a long, use them as milliseconds */
                Long l = null;
                try {
                    l = getLong(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (l != null)
                    time = new java.sql.Time(l);
            }
            if (time == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           java.sql.Time.class.getName());
        }
        return time;
    }


    /** convert object to java.sql.Timestamp or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting java.sql.Timestamp.
     */
    @SuppressWarnings("deprecation")
    public static java.sql.Timestamp getTimestamp(Object oSource) {
        /* like Date milliseconds in UTC with integer seconds and separate nanoseconds  */
        java.sql.Timestamp ts = null;
        if (oSource != null) {
            if (oSource instanceof java.sql.Timestamp)
                ts = (java.sql.Timestamp) oSource;
            else if (oSource instanceof java.sql.Time) {
                java.sql.Time t = (java.sql.Time) oSource;
                ts = new java.sql.Timestamp(t.getTime());
            } else if (oSource instanceof java.sql.Date) {
                java.sql.Date d = (java.sql.Date) oSource;
                ts = new java.sql.Timestamp(d.getTime());
            } else if (oSource instanceof String) {
                /* if it is a string, then try ANSI "yyyy-MM-dd hh:mm:ss.fffff" */
                String s = (String) oSource;
                try {
                    ts = java.sql.Timestamp.valueOf(s);
                } catch (IllegalArgumentException iae) {
                }
                try {
                    ts = new java.sql.Timestamp(java.util.Date.parse(s));
                } catch (IllegalArgumentException iae) {
                }
            } else if (oSource instanceof java.util.Date) {
                /* if it is a java.util.Date, just use the milliseconds */
                java.util.Date d = (java.util.Date) oSource;
                ts = new java.sql.Timestamp(d.getTime());
            } else if (oSource instanceof Calendar) {
                /* if it is a java.util.Calendar, just use the milliseconds */
                Calendar c = (Calendar) oSource;
                ts = new java.sql.Timestamp(c.getTime()
                                             .getTime());
            } else {
                /* if it is a long, use them as milliseconds */
                Long l = null;
                try {
                    l = getLong(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (l != null)
                    ts = new java.sql.Timestamp(l.longValue());
            }
            if (ts == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           java.sql.Timestamp.class.getName());
        }
        return ts;
    }


    /** convert object to Duration or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting Duration.
     */
    public static Duration getDuration(Object oSource) {
        Duration duration = null;
        if (oSource != null) {
            if (oSource instanceof BigDecimal) {
                Interval iv = null;
                int iSign = 1;
                BigDecimal bd = (BigDecimal) oSource;
                if (bd.compareTo(BigDecimal.ZERO) < 0) {
                    iSign = -1;
                    bd = bd.negate();
                }
                if (bd.scale() == 0) {
                    BigDecimal bdDivisor = BigDecimal.valueOf(12);
                    BigDecimal[] abd = bd.divideAndRemainder(bdDivisor);
                    int iYears = abd[0].intValueExact();
                    int iMonths = abd[1].intValueExact();
                    iv = new Interval(iSign, iYears, iMonths);
                } else if (bd.scale() == 9) {
                    BigDecimal bdDivisor = BigDecimal.ONE;
                    BigDecimal[] abd = bd.divideAndRemainder(bdDivisor);
                    long lNanoseconds = abd[1].unscaledValue()
                                              .longValue();
                    bdDivisor = BigDecimal.valueOf(60);
                    abd = abd[0].divideAndRemainder(bdDivisor);
                    int iSeconds = abd[1].intValueExact();
                    abd = abd[0].divideAndRemainder(bdDivisor);
                    int iMinutes = abd[1].intValueExact();
                    bdDivisor = BigDecimal.valueOf(24);
                    abd = abd[0].divideAndRemainder(bdDivisor);
                    int iHours = abd[1].intValueExact();
                    int iDays = abd[0].intValueExact();
                    iv = new Interval(iSign, iDays, iHours, iMinutes, iSeconds, lNanoseconds);
                } else
                    throw new IllegalArgumentException("Scale must be 0 or 9 for Intervals!");
                duration = iv.toDuration();
            } else
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           Duration.class.getName());
        }
        return duration;
    }


    /** convert object to byte[] or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting byte[].
     */
    public static byte[] getBytes(Object oSource) {
        byte[] buffer = null;
        if (oSource != null) {
            if (oSource instanceof byte[])
                buffer = (byte[]) oSource;
            else if (oSource instanceof Blob) {
                Blob blob = (Blob) oSource;
                try {
                    buffer = blob.getBytes(1L, (int) blob.length());
                } catch (SQLException se) {
                    throw new IllegalArgumentException("Bytes of Blob could not be retrieved!");
                }
            } else if (oSource instanceof Serializable) {
                Serializable s = (Serializable) oSource;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(s);
                    oos.close();
                    buffer = baos.toByteArray();
                } catch (IOException ie) {
                }
            }
            if (buffer == null)
                throw new IllegalArgumentException("Object of type " +
                                                           oSource.getClass()
                                                                  .getName() + " cannot be converted to " +
                                                           byte[].class.getName());
        }
        return buffer;
    }


    /** convert object to BLOB or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting java.sql.Blob.
     * @throws java.sql.SQLException if SerialBlob could not be instantiated.
     */
    public static java.sql.Blob getBlob(Object oSource)
            throws java.sql.SQLException {
        java.sql.Blob blob = null;
        if (oSource != null) {
            if (oSource instanceof Blob)
                blob = (Blob) oSource;
            else {
                byte[] buffer = null;
                try {
                    buffer = getBytes(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (buffer != null) {
                    blob = new AccessBlob();
                    blob.setBytes(1L, buffer);
                }
            }
        }
        return blob;
    }


    /** convert object to CLOB or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting java.sql.Clob.
     * @throws java.sql.SQLException if SerialClob could not be instantiated.
     */
    public static java.sql.Clob getClob(Object oSource)
            throws java.sql.SQLException {
        java.sql.Clob clob = null;
        if (oSource != null) {
            if (oSource instanceof java.sql.Clob)
                clob = (java.sql.Clob) oSource;
            else {
                String s = null;
                try {
                    s = getString(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (s != null) {
                    clob = new AccessClob();
                    clob.setString(1L, s);
                }
            }
        }
        return clob;
    }


    /** convert object to NCLOB or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting java.sql.NClob.
     * @throws java.sql.SQLException if SerialClob could not be instantiated.
     */
    public static java.sql.NClob getNClob(Object oSource)
            throws java.sql.SQLException {
        java.sql.NClob nclob = null;
        if (oSource != null) {
            if (oSource instanceof java.sql.NClob)
                nclob = (java.sql.NClob) oSource;
            else {
                String s = null;
                try {
                    s = getString(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (s != null) {
                    nclob = new AccessNClob();
                    nclob.setString(1L, s);
                }
            }
        }
        return nclob;
    }


    /** convert object to NCLOB or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return resulting java.sql.NClob.
     * @throws java.sql.SQLException if SerialClob could not be instantiated.
     */
    public static java.sql.SQLXML getSqlXml(Object oSource)
            throws java.sql.SQLException {
        java.sql.SQLXML sx = null;
        if (oSource != null) {
            if (oSource instanceof java.sql.SQLXML)
                sx = (java.sql.SQLXML) oSource;
            else {
                String s = null;
                try {
                    s = getString(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (s != null) {
                    sx = new AccessSqlXml();
                    sx.setString(s);
                }
            }
        }
        return sx;
    }


    /** convert object to URL or throw IllegalArgumentException, if
     * that is not possible.
     * @param oSource source object.
     * @return URL
     * @throws java.sql.SQLException if URL could not be instantiated.
     */
    public static URL getURL(Object oSource)
            throws java.sql.SQLException {
        URL url = null;
        if (oSource != null) {
            if (oSource instanceof URL)
                url = (URL) oSource;
            else {
                String s = null;
                try {
                    s = getString(oSource);
                } catch (IllegalArgumentException iae) {
                }
                if (s != null) {
                    try {
                        url = new URL(s);
                    } catch (MalformedURLException mfu) {
                        throw new java.sql.SQLException(mfu.getClass()
                                                           .getName() + ": " + mfu.getMessage());
                    }
                }
            }
        }
        return url;
    }

}
