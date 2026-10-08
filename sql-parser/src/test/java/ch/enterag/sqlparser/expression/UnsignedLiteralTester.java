package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.Interval;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.SqlLiterals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnsignedLiteralTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private UnsignedLiteral _ul = null;

    @BeforeEach
    public void setUp() {
        _ul = _sf.newUnsignedLiteral();
    }

    @Test
    public void testApproximate() {
        _ul.parse("123.34E-6");
        System.out.println(_ul.format());
        assertEquals("1.2334E-4", _ul.format(), "Scientific notation not recognized!");
    }

    @Test
    public void testInteger() {
        _ul.parse("78437487");
        // System.out.println(_ul.format());
        assertEquals("78437487", _ul.format(), "Integer not recognized!");
    }

    @Test
    public void testDecimal() {
        _ul.parse("78437487.6590834063086739067");
        // System.out.println(_ul.format());
        assertEquals("78437487.6590834063086739067", _ul.format(), "Decimal not recognized!");
    }

    @Test
    public void testString() {
        _ul.parse("'a ''quoted'' string'");
        // System.out.println(_ul.format());
        assertEquals("'a ''quoted'' string'", _ul.format(), "Quoted string not recognized!");
    }

    @Test
    public void testNational() {
        _ul.parse("N'a ''quoted'' string'");
        // System.out.println(_ul.format());
        assertEquals("N'a ''quoted'' string'", _ul.format(), "Quoted national string not recognized!");
    }

    @Test
    public void testBitString() {
        _ul.parse("B'0111001011010100100000'");
        // System.out.println(_ul.format());
        assertEquals("B'0111001011010100100000'", _ul.format(), "Bit string not recognized!");
    }

    @Test
    public void testBytes() {
        _ul.parse("X'12af34bc47de00'");
        // System.out.println(_ul.format());
        assertEquals("X'12AF34BC47DE00'", _ul.format(), "Bytes not recognized!");
    }

    @Test
    public void testDate() {
        _ul.parse("DATE'1968-11-25'");
        // System.out.println(_ul.format());
        assertEquals("DATE'1968-11-25'", _ul.format(), "Date not recognized!");
    }

    @Test
    public void testTime() {
        _ul.parse("TIME'19:23:55'");
        // System.out.println(_ul.format());
        assertEquals("TIME'19:23:55'", _ul.format(), "Time not recognized!");
    }

    @Test
    public void testMillis() {
        _ul.parse("TIME'19:23:55.123'");
        // System.out.println(_ul.format());
        assertEquals("TIME'19:23:55.123'", _ul.format(), "Time not recognized!");
    }

    @Test
    public void testTimestamp() {
        _ul.parse("TIMESTAMP'2014-08-28 19:23:55'");
        // System.out.println(_ul.format());
        assertEquals("TIMESTAMP'2014-08-28 19:23:55'", _ul.format(), "Timestamp not recognized!");
    }

    @Test
    public void testNanos() {
        _ul.parse("TIMESTAMP'2014-08-28 19:23:55.857362538'");
        // System.out.println(_ul.format());
        assertEquals("TIMESTAMP'2014-08-28 19:23:55.857362538'", _ul.format(), "Timestamp not recognized!");
    }

    @Test
    public void TestYearInterval() {
        _ul.parse("INTERVAL -'2' YEAR");
        System.out.println(_ul.format());
        assertEquals("INTERVAL '- 2' YEAR", _ul.format(), "YEAR interval not recognized!");
    }

    @Test
    public void TestYearToMonthInterval() {
        _ul.parse("INTERVAL '8-4' YEAR(2) TO MONTH");
        // System.out.println(_ul.format());
        assertEquals("INTERVAL '8-4' YEAR TO MONTH", _ul.format(), "YEAR TO MONTH interval not recognized!");
    }

    @Test
    public void TestMonthInterval() {
        _ul.parse("INTERVAL '4' MONTH");
        // System.out.println(_ul.format());
        assertEquals("INTERVAL '4' MONTH", _ul.format(), "MONTH interval not recognized!");
    }

    @Test
    public void TestDayInterval() {
        // ErrorListener.getInstance().suppressException();
        _ul.parse("INTERVAL '- 500' DAY(3)");
        // System.out.println(_ul.format());
        assertEquals("INTERVAL '- 500' DAY(3)", _ul.format(), "DAY interval not recognized!");
    }

    @Test
    public void TestInterval() {
        Interval iv = new Interval(1, 123, 3);
        _ul.parse(SqlLiterals.formatIntervalLiteral(iv));
        // System.out.println(_ul.format());
        assertEquals("INTERVAL '123-3' YEAR(3) TO MONTH", _ul.format(), "INTERVAL not recognized!");
    }

    @Test
    public void TestDayToHourInterval() {
        // ErrorListener.getInstance().suppressException();
        _ul.parse("INTERVAL -'500 23' DAY TO HOUR");
        // System.out.println(_ul.format());
        assertEquals("INTERVAL '- 500 23' DAY(3) TO HOUR", _ul.format(), "DAY interval not recognized!");
    }

    @Test
    public void TestMinute() {
        // ErrorListener.getInstance().suppressException();
        _ul.parse("INTERVAL '30' MINUTE");
        // System.out.println(_ul.format());
        assertEquals("INTERVAL '30' MINUTE", _ul.format(), "MINUTE interval not recognized!");
    }

    @Test
    public void TestHourToSecondInterval() {
        // ErrorListener.getInstance().suppressException();
        _ul.parse("INTERVAL '23:12:45' HOUR TO SECOND");
        // System.out.println(_ul.format());
        assertEquals("INTERVAL '23:12:45' HOUR TO SECOND", _ul.format(), "HOUR TO SECOND interval not recognized!");
    }

    @Test
    public void TestSecondInterval() {
        // ErrorListener.getInstance().suppressException();
        _ul.parse("INTERVAL -'0.123456789' SECOND(2,9)");
        // System.out.println(_ul.format());
        assertEquals("INTERVAL '- 0.123456789' SECOND(2, 9)", _ul.format(), "SECOND interval not recognized!");
    }

    @Test
    public void TestDayToTimeInterval() {
        // ErrorListener.getInstance().suppressException();
        _ul.parse("INTERVAL -'500 14:30:25.123456789' DAY(3) TO SECOND(2,9)");
        // System.out.println(_ul.format());
        assertEquals("INTERVAL '- 500 14:30:25.123456789' DAY(3) TO SECOND(9)", _ul.format(), "DAY TO SECOND interval not recognized!");
    }

    @Test
    public void testBoolean() {
        _ul.parse("UNKNOWN");
        // System.out.println(_ul.format());
        assertEquals("UNKNOWN", _ul.format(), "Boolean not recognized!");
    }
}
