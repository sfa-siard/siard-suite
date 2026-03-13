package ch.admin.bar.siard2.db2;

import ch.enterag.sqlparser.Interval;
import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.expression.enums.BooleanLiteral;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Db2Literals extends SqlLiterals {
    private static final String sBYTE_LITERAL_PREFIX = "BINARY";

    /** format byte buffer value.
     * @param bufValue byte buffer value to be formatted.
     * @return byte string literal.
     */
    public static String formatBytesLiteral(byte[] bufValue) {
        String sFormatted = sNULL;
        if (bufValue != null)
            sFormatted = sBYTE_LITERAL_PREFIX +
                    sLEFT_PAREN + SqlLiterals.formatBytesLiteral(bufValue) + sRIGHT_PAREN;
        return sFormatted;
    }


    /** format a boolean literal value.
     * in DB/2 the BOOLEAN data type is realized as a SMALLINT data type.
     * @param blValue boolean value to be formatted.
     * @return boolean (SMALLINT) literal.
     */
    public static String formatBooleanLiteral(BooleanLiteral blValue) {
        String sFormatted = null;
        switch (blValue) {
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
    }


    /** format a date value
     * MSSQL does not like date/time prefixes.
     * @param dateValue date value to be formatted.
     * @return date literal.
     */
    public static String formatDateLiteral(Date dateValue) {
        String sFormatted = sNULL;
        if (dateValue != null)
            sFormatted = SqlLiterals.formatDateLiteral(dateValue)
                                    .substring(sDATE_LITERAL_PREFIX.length());
        return sFormatted;
    }


    /** format a time value
     * MSSQL does not like date/time prefixes.
     * @param timeValue time value to be formatted.
     * @return time literal.
     */
    public static String formatTimeLiteral(Time timeValue) {
        String sFormatted = sNULL;
        if (timeValue != null) {
            /* DB/2 cannot handle seconds decimals */
            timeValue.setTime(1000l * (timeValue.getTime() / 1000l));
            sFormatted = SqlLiterals.formatTimeLiteral(timeValue)
                                    .substring(sTIME_LITERAL_PREFIX.length());
        }
        return sFormatted;
    }


    /** format a timestamp value.
     * MSSQL does not like date/time prefixes.
     * @param tsValue timestamp value to be formatted.
     * @return timestamp literal.
     */
    public static String formatTimestampLiteral(Timestamp tsValue) {
        String sFormatted = sNULL;
        if (tsValue != null)
            sFormatted = SqlLiterals.formatTimestampLiteral(tsValue)
                                    .substring(sTIMESTAMP_LITERAL_PREFIX.length());
        return sFormatted;
    }


    /** format an interval value
     * In DB/2 a year-month interval is represented by a BIGINT and
     * day-second interval is represented as a DECIMAL.
     * @param ivValue interval value to be formatted.
     * @return interval literal.
     */
    public static String formatIntervalLiteral(Interval ivValue) {
        String sFormatted = null;
        if ((ivValue.getYears() != 0) || (ivValue.getMonths() != 0)) {
            Long l = ((long) ivValue.getYears()) * 12 + ivValue.getMonths();
            sFormatted = String.valueOf(l);
        } else {
            long l = ivValue.getNanoSeconds() +
                    1000000000l * (ivValue.getSeconds() +
                            60 * (ivValue.getMinutes() +
                                    60 * (ivValue.getHours() +
                                            24 * ivValue.getDays())));
            BigDecimal bd = new BigDecimal(BigInteger.valueOf(l), 9);
            sFormatted = bd.toPlainString();
        }
        return sFormatted;
    }

}
