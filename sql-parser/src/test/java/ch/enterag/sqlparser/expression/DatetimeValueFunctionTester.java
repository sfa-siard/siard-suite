package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatetimeValueFunctionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DatetimeValueFunction _dvf = null;

    @BeforeEach
    public void setUp() {
        _dvf = _sf.newDatetimeValueFunction();
    }

    @Test
    public void testCurrentDate() {
        // ErrorListener.getInstance().suppressException();
        _dvf.parse("CURRENT_DATE");
        // System.out.println(_dvf.format());
        assertEquals("CURRENT_DATE", _dvf.format(), "CURRENT_DATE not recognized!");
    }

    @Test
    public void testCurrentTime() {
        // ErrorListener.getInstance().suppressException();
        _dvf.parse("CURRENT_TIME(3)");
        // System.out.println(_dvf.format());
        assertEquals("CURRENT_TIME(3)", _dvf.format(), "CURRENT_TIME not recognized!");
    }

    @Test
    public void testCurrentTimestamp() {
        // ErrorListener.getInstance().suppressException();
        _dvf.parse("CURRENT_TIMESTAMP");
        // System.out.println(_dvf.format());
        assertEquals("CURRENT_TIMESTAMP", _dvf.format(), "CURRENT_TIMESTAMP not recognized!");
    }

    @Test
    public void testLocalTime() {
        // ErrorListener.getInstance().suppressException();
        _dvf.parse("LOCALTIME");
        // System.out.println(_dvf.format());
        assertEquals("LOCALTIME", _dvf.format(), "LOCALTIME not recognized!");
    }

    @Test
    public void testLocalTimestamp() {
        // ErrorListener.getInstance().suppressException();
        _dvf.parse("LOCALTIMESTAMP(9)");
        // System.out.println(_dvf.format());
        assertEquals("LOCALTIMESTAMP(9)", _dvf.format(), "LOCALTIMESTAMP not recognized!");
    }


}
