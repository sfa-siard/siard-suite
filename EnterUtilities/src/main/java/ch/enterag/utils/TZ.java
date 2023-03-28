/*== TZ.java ===========================================================
TZ implements trivial timezone stuff.
Version     : $Id: TZ.java 1460 2012-02-27 16:09:17Z hartwig $
Application : JAVA Utilities
Description : TZ implements trivial timezone stuff.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2012
Created    : 28.02.2012, Hartwig Thomas, Enter AG, Zurich
======================================================================*/
package ch.enterag.utils;

import java.util.TimeZone;

public abstract class TZ {
    /**
     * local default time zone at the time of loading this class
     */
    private static final TimeZone m_tzLocal = TimeZone.getDefault();

    public static void setLocalTimeZone() {
        TimeZone.setDefault(m_tzLocal);
    }

    public static void setUtcTimeZone() {
        TimeZone.setDefault(getUtcTimeZone());
    }

    /*------------------------------------------------------------------*/

    /**
     * returns UTC TimeZone.
     *
     * @return UTC TimeZone.
     */
    public static TimeZone getUtcTimeZone() {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        tz.setRawOffset(0);
        return tz;
    } /* getUtcTimeZone */

} /* TZ */
