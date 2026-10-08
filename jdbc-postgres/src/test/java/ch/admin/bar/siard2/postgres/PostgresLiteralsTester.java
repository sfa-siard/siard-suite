package ch.admin.bar.siard2.postgres;

import ch.enterag.sqlparser.Interval;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.utils.EU;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PostgresLiteralsTester {
    @Test
    public void test() {
        try {
            String s = "123 years 3 mons";
            Interval iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            assertEquals(new Interval(1, 123, 3), iv, "Invalid interval!");

            s = "-123 years 3 mons";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            assertEquals(new Interval(-1, 123 - 1, 12 - 3), iv, "Invalid interval!");

            s = "123 years -3 mons";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            assertEquals(new Interval(1, 123 - 1, 12 - 3), iv, "Invalid interval!");

            s = "-123 years -3 mons";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            assertEquals(new Interval(-1, 123, 3), iv, "Invalid interval!");

            s = "12 years";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            assertEquals(new Interval(1, 12, 0), iv, "Invalid interval!");

            s = "4 mons";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            assertEquals(new Interval(1, 0, 4), iv, "Invalid interval!");

            s = "3 days 04:05:06";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            LocalTime lt = LocalTime.parse("04:05:06");
            assertEquals(new Interval(1, 3, lt.getHour(), lt.getMinute(), lt.getSecond(), lt.getNano()), iv, "Invalid interval!");

            s = "-3 days 04:05:06";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            lt = LocalTime.parse("04:05:06");
            long lNanos = PostgresLiterals.lNANOS_PER_DAY - PostgresLiterals.getNanos(lt);
            lt = PostgresLiterals.getLocalTime(lNanos);
            assertEquals(new Interval(-1, 2, lt.getHour(), lt.getMinute(), lt.getSecond(), lt.getNano()), iv, "Invalid interval!");

            s = "3 days -04:05:06";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            lt = LocalTime.parse("04:05:06");
            lNanos = PostgresLiterals.lNANOS_PER_DAY - PostgresLiterals.getNanos(lt);
            lt = PostgresLiterals.getLocalTime(lNanos);
            assertEquals(new Interval(1, 2, lt.getHour(), lt.getMinute(), lt.getSecond(), lt.getNano()), iv, "Invalid interval!");

            s = "-3 days -04:05:06";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            lt = LocalTime.parse("04:05:06");
            assertEquals(new Interval(-1, 3, lt.getHour(), lt.getMinute(), lt.getSecond(), lt.getNano()), iv, "Invalid interval!");

            s = "3 days";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            assertEquals(new Interval(1, 3, 0, 0, 0, 0), iv, "Invalid interval!");

            s = "04:05:06";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            lt = LocalTime.parse("04:05:06");
            assertEquals(new Interval(1, 0, lt.getHour(), lt.getMinute(), lt.getSecond(), lt.getNano()), iv, "Invalid interval!");

            s = "-04:05:06";
            iv = PostgresLiterals.parseInterval(s);
            System.out.println(s + ": " + SqlLiterals.formatIntervalLiteral(iv));
            lt = LocalTime.parse("04:05:06");
            assertEquals(new Interval(-1, 0, lt.getHour(), lt.getMinute(), lt.getSecond(), lt.getNano()), iv, "Invalid interval!");
        } catch (ParseException pe) {
            fail(EU.getExceptionMessage(pe));
        }
    }
}
