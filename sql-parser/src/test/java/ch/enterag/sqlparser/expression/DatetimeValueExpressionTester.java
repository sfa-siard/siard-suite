package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatetimeValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DatetimeValueExpression _dve = null;

    @BeforeEach
    public void setUp() {
        _dve = _sf.newDatetimeValueExpression();
    }

    @Test
    public void testLiteralDate() {
        _dve.parse("DATE'2016-4-28'");
        // System.out.println(_dve.format());
        assertEquals("DATE'2016-04-28'", _dve.format(), "Date literal not recognized!");
    }

    @Test
    public void testLiteralTime() {
        _dve.parse("TIME'10:54:45.123' AT LOCAL");
        // System.out.println(_dve.format());
        assertEquals("TIME'10:54:45.123' AT LOCAL", _dve.format(), "Time literal not recognized!");
    }

    @Test
    public void testLiteralTimestamp() {
        _dve.parse("TIMESTAMP'2016-4-28 10:54:5.123' AT TIME ZONE INTERVAL '2:30' HOUR TO MINUTE");
        System.out.println(_dve.format());
        assertEquals("TIMESTAMP'2016-04-28 10:54:05.123' AT TIME ZONE INTERVAL '2:30' HOUR TO MINUTE", _dve.format(), "Timestamp literal not recognized!");
    }

    @Test
    public void testSubtractInterval() {
        // ErrorListener.getInstance().suppressException();
        _dve.parse("TIMESTAMP'2016-4-28 10:54:5.123' - INTERVAL'1' DAY");
        // System.out.println(_dve.format());
        assertEquals("TIMESTAMP'2016-04-28 10:54:05.123' - INTERVAL '1' DAY", _dve.format(), "Subtract interval not recognized!");
    }

    @Test
    public void testAddInterval() {
        _dve.parse("TIMESTAMP'2016-4-28 10:54:5.123' + INTERVAL'1' DAY");
        // System.out.println(_dve.format());
        assertEquals("TIMESTAMP'2016-04-28 10:54:05.123' + INTERVAL '1' DAY", _dve.format(), "Add interval not recognized!");
    }

    @Test
    public void testFunction() {
        _dve.parse("CURRENT_DATE");
        System.out.println(_dve.format());
        assertEquals("CURRENT_DATE", _dve.format(), "CURRENT_DATE not recognized!");
    }

}
