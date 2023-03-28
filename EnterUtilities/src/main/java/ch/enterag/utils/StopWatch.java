/*== StopWatch.java ====================================================
StopWatch implements a utility for performance testing.
Version     : $Id: StopWatch.java 1212 2010-09-02 12:00:26Z hartwig $
Application : Utilities
Description : StopWatch implements a utility for performance testing.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2010
Created    : 30.08.2010, Hartwig Thomas
======================================================================*/
package ch.enterag.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/*====================================================================*/

/**
 * StopWatch implements time measuring for performance assessment.
 *
 * @author Hartwig Thomas
 */
public class StopWatch {
    private static DecimalFormatSymbols dfsEUROPE = new DecimalFormatSymbols();
    private static final DecimalFormat dfLONG = new DecimalFormat("#,##0");
    private static final DecimalFormat dfDOUBLE = new DecimalFormat("#,##0.00");

    /*=====================================================================
    Members
    =====================================================================*/
    private long m_lMsStart = 0;
    private long m_lMsAccumulated = 0;

    public long getMsAccumulated() {
        return m_lMsAccumulated;
    }
  
  /*=====================================================================
  constructor and factory
  =====================================================================*/
    /*-------------------------------------------------------------------*/

    /**
     * private constructor prevents creation except through factory.
     */
    private StopWatch() {
        /* display numbers with . as decimal and ' as thousand's separator */
        dfsEUROPE.setGroupingSeparator('\'');
        dfsEUROPE.setDecimalSeparator('.');
        dfLONG.setDecimalFormatSymbols(dfsEUROPE);
        dfDOUBLE.setDecimalFormatSymbols(dfsEUROPE);
    } /* StopWatch */

    /*-------------------------------------------------------------------*/

    /**
     * private constructor prevents creation except through factory.
     *
     * @return StopWatch instance.
     */
    public static StopWatch getInstance() {
        return new StopWatch();
    } /* getInstance */
   
  /*=====================================================================
  methods
  =====================================================================*/
    /*-------------------------------------------------------------------*/

    /**
     * start notes the starting time in milliseconds.
     */
    public void start() {
        m_lMsStart = System.currentTimeMillis();
    } /* constructor */

    /*-------------------------------------------------------------------*/

    /**
     * stop returns the time since start after having added it to the
     * accumulated time.
     *
     * @return time since start in milliseconds.
     */
    public long stop() {
        long lMsInterval = System.currentTimeMillis() - m_lMsStart;
        m_lMsAccumulated += lMsInterval;
        return lMsInterval;
    } /* stop */

    /*-------------------------------------------------------------------*/

    /**
     * formatLong helper function to format a long.
     *
     * @param lLong long to be formatted.
     * @return string for long with a thousand's separator.
     */
    public static String formatLong(long lLong) {
        return dfLONG.format(lLong);
    } /* formatMs */

    /*-------------------------------------------------------------------*/

    /**
     * formatMs helper function to format accumulated milliseconds.
     *
     * @return string for long with a thousand's separator.
     */
    public String formatMs() {
        return formatLong(m_lMsAccumulated);
    } /* formatMs */

    /*-------------------------------------------------------------------*/

    /**
     * formatRate helper function to compute a rate (units/ms) and format it
     *
     * @param lUnits units for which a rate (presumably per millisecond) is to be computed.
     * @param lMs    duration (presumably per millisecond) for units.
     * @return string for rate with two decimals and a thousand's separator.
     */
    public String formatRate(long lUnits, long lMs) {
        double dRate = lUnits;
        if (lMs > 0)
            dRate = dRate / lMs;
        else
            dRate = 0.0;
        return dfDOUBLE.format(dRate);
    } /* formatRate */

    /*-------------------------------------------------------------------*/

    /**
     * formatRate helper function to compute a rate (units/ms) and format it
     *
     * @param lUnits units for which a rate (presumably per millisecond) is to be computed.
     * @return string for rate with two decimals and a thousand's separator.
     */
    public String formatRate(long lUnits) {
        return formatRate(lUnits, m_lMsAccumulated);
    } /* formatRate */

} /* class StopWatch */
