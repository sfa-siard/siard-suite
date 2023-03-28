package ch.enterag.utils;

import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DuTester {
    private static final DU _du = DU.getInstance("en", "dd.MM.yyyy HH:mm:ss");

    @Test
    public void testDate() {
        try {
            @SuppressWarnings("deprecation")
            java.sql.Date d1 = new java.sql.Date(2018 - 1900, 11 - 1, 30);
            System.out.println("Before " + String.valueOf(d1) + " (" + String.valueOf(d1.getTime()) + ")");
            String sXmlDate = _du.toXsDate(d1);
            System.out.println("XML: " + sXmlDate);
            java.sql.Date d2 = _du.fromXsDate(sXmlDate);
            System.out.println("After " + String.valueOf(d1) + " (" + String.valueOf(d1.getTime()) + ")");
            assertEquals("Invalid conversion!", d1, d2);
        } catch (ParseException pe) {
            fail(EU.getExceptionMessage(pe));
        }
    }

    @Test
    public void testTime() {
        try {
            @SuppressWarnings("deprecation")
            java.sql.Time t1 = new java.sql.Time(8, 59, 31);
            t1 = new java.sql.Time(t1.getTime() + 123);
            System.out.println("Before " + String.valueOf(t1) + " (" + String.valueOf(t1.getTime()) + ")");
            String sXmlTime = _du.toXsTime(t1);
            System.out.println("XML: " + sXmlTime);
            java.sql.Time t2 = _du.fromXsTime(sXmlTime);
            System.out.println("After " + String.valueOf(t2) + " (" + String.valueOf(t2.getTime()) + ")");
            assertEquals("Invalid conversion!", t1, t2);
        } catch (ParseException pe) {
            fail(EU.getExceptionMessage(pe));
        }
    }

    @Test
    public void testDateTime() {
        try {
            @SuppressWarnings("deprecation")
            java.sql.Timestamp ts1 = new java.sql.Timestamp(2018 - 1900, 12 - 1, 3, 8, 59, 31, 123456789);
            System.out.println("Before " + String.valueOf(ts1) + " (" + String.valueOf(ts1.getTime()) + "," + String.valueOf(
                    ts1.getNanos()) + ")");
            String sXmlDateTime = _du.toXsDateTime(ts1);
            System.out.println("XML: " + sXmlDateTime);
            java.sql.Timestamp ts2 = _du.fromXsDateTime(sXmlDateTime);
            System.out.println("After " + String.valueOf(ts2) + " (" + String.valueOf(ts2.getTime()) + "," + String.valueOf(
                    ts2.getNanos()) + ")");
            assertEquals("Invalid conversion!", ts1, ts2);
        } catch (ParseException pe) {
            fail(EU.getExceptionMessage(pe));
        }
    }

}
