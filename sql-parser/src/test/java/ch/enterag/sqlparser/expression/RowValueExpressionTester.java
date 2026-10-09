package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RowValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private RowValueExpression _rve = null;

    @BeforeEach
    public void setUp() {
        _rve = _sf.newRowValueExpression();
    }

    @Test
    public void testPrimary() {
        _rve.parse("C1.S1.T1.COL");
        // System.out.println(_rve.format());
        assertEquals("C1.S1.T1.COL", _rve.format(), "Value expression primary not recognized!");
    }

    @Test
    public void testList() {
        _rve.parse("(C1.S1.T1.COL,5,'abc')");
        // System.out.println(_rve.format());
        assertEquals("(C1.S1.T1.COL, 5, 'abc')", _rve.format(), "Value list not recognized!");
    }

    @Test
    public void testInterval() {
        _rve.parse("INTERVAL '123-2' YEAR(3) TO MONTH");
        // System.out.println(_rve.format());
        assertEquals("INTERVAL '123-2' YEAR(3) TO MONTH", _rve.format(), "INTERVAL not recognized!");
    }

    @Test
    public void testRow() {
        _rve.parse("ROW(C1.S1.T1.COL)");
        // System.out.println(_rve.format());
        assertEquals("ROW(C1.S1.T1.COL)", _rve.format(), "ROW expression not recognized!");
    }

    @Test
    public void testQuery() {
        _rve.parse("(SELECT COUNT(*) FROM C1.S1.T1)");
        System.out.println(_rve.format());
        assertEquals("(SELECT\r\n  COUNT(*)\r\nFROM C1.S1.T1)", _rve.format(), "Query expression not recognized!");
    }

}
