/*
Copyright  : 2010, Enter AG, Zurich, Switzerland
             2024, Puzzle ITC GmbH, Switzerland
Created    : 30.08.2010, Hartwig Thomas
*/
package ch.enterag.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * StopWatch implements time measuring for performance assessment.
 *
 * @author Hartwig Thomas
 */
public class StopWatch {
    private static final DecimalFormat DF_LONG = new DecimalFormat("#,##0");
    private static final DecimalFormat DF_DOUBLE = new DecimalFormat("#,##0.00");
    private static final DecimalFormatSymbols DFS_EUROPE = new DecimalFormatSymbols();

    private long msStart = 0;
    private long msAccumulated = 0;

    public StopWatch() {
        DFS_EUROPE.setGroupingSeparator('\'');
        DFS_EUROPE.setDecimalSeparator('.');
        DF_LONG.setDecimalFormatSymbols(DFS_EUROPE);
        DF_DOUBLE.setDecimalFormatSymbols(DFS_EUROPE);
    }

    /**
     * @return StopWatch instance.
     * @deprecated Use {@link #StopWatch()} to create instances.
     */
    @Deprecated
    public static StopWatch getInstance() {
        return new StopWatch();
    }

    /**
     * Helper function to format a long.
     *
     * @param longValue long to be formatted.
     * @return String for long with a thousand's separator.
     */
    public static String formatLong(long longValue) {
        return DF_LONG.format(longValue);
    }

    /**
     * Notes the starting time in milliseconds.
     */
    public void start() {
        msStart = System.currentTimeMillis();
    }

    /**
     * Returns the time since start after having added it to the
     * accumulated time.
     *
     * @return Time since start in milliseconds.
     */
    public long stop() {
        long interval = System.currentTimeMillis() - msStart;
        msAccumulated += interval;
        return interval;
    }

    /**
     * Helper function to format accumulated milliseconds.
     *
     * @return String for long with a thousand's separator.
     */
    public String formatMs() {
        return formatLong(msAccumulated);
    }

    /**
     * Helper function to compute and format a rate (units/ms).
     *
     * @param units Units for which a rate (presumably per millisecond) is to be computed.
     * @param duration Duration (presumably per millisecond) for units.
     * @return String for rate with two decimals and a thousand's separator.
     */
    public String formatRate(long units, long duration) {
        double rate = units;
        if (duration > 0) rate = rate / duration;
        else rate = 0.0;
        return DF_DOUBLE.format(rate);
    }

    /**
     * Helper function to compute and format a rate (units/ms).
     *
     * @param units Units for which a rate (presumably per millisecond) is to be computed.
     * @return String for rate with two decimals and a thousand's separator.
     */
    public String formatRate(long units) {
        return formatRate(units, msAccumulated);
    }

}
