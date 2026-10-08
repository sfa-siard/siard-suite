package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntervalValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private IntervalValueExpression _ive = null;

    @BeforeEach
    public void setUp() {
        _ive = _sf.newIntervalValueExpression();
    }

    @Test
    public void testYearLiteral() {
        _ive.parse("INTERVAL'1' YEAR");
        // System.out.println(_ive.format());
        assertEquals("INTERVAL '1' YEAR", _ive.format(), "Year literal not recognized!");
    }

    @Test
    public void testAbs() {
        _ive.parse("-ABS(INTERVAL'1 20:23' DAY TO MINUTE)");
        // System.out.println(_ive.format());
        assertEquals("-ABS(INTERVAL '1 20:23' DAY TO MINUTE)", _ive.format(), "ABS not recognized!");
    }

    @Test
    public void testMultiple() {
        _ive.parse("T.COL * ABS(INTERVAL -'46 23:34:12' DAY TO SECOND)");
        // System.out.println(_ive.format());
        assertEquals("T.COL*ABS(INTERVAL '- 46 23:34:12' DAY TO SECOND)", _ive.format(), "Multiple not recognized!");
    }

    @Test
    public void testDivision() {
        _ive.parse("INTERVAL -'46 23:34:12' DAY TO SECOND / 20");
        // System.out.println(_ive.format());
        assertEquals("INTERVAL '- 46 23:34:12' DAY TO SECOND/20", _ive.format(), "Division not recognized!");
    }

    @Test
    public void testAddition() {
        _ive.parse("T.COL * INTERVAL -'46 23:34' DAY TO MINUTE + INTERVAL'3' MINUTE");
        // System.out.println(_ive.format());
        assertEquals("T.COL*INTERVAL '- 46 23:34' DAY TO MINUTE + INTERVAL '3' MINUTE", _ive.format(), "Addition not recognized!");
    }

    @Test
    public void testMultiplication() {
        _ive.parse("INTERVAL -'46 23:34' DAY TO MINUTE * INTERVAL'3' MINUTE");
        System.out.println(_ive.format());
        assertEquals("INTERVAL '- 46 23:34' DAY TO MINUTE*INTERVAL '3' MINUTE", _ive.format(), "Addition not recognized!");
    }

    @Test
    public void testDateSubtraction() {
        _ive.parse("DATE'2016-04-28' - DATE'2015-04-28'");
        // System.out.println(_ive.format());
        assertEquals("DATE'2016-04-28' - DATE'2015-04-28'", _ive.format(), "Date subtraction not recognized!");
    }

}
