package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private BooleanValueExpression _bve = null;

    @BeforeEach
    public void setUp() {
        _bve = _sf.newBooleanValueExpression();
    }

    @Test
    public void testEqual() {
        _bve.parse("A1 = 'abc'");
        // System.out.println(_bve.format());
        assertEquals("A1 = 'abc'", _bve.format(), "Equal not recognized!");
    }


    @Test
    public void testNotDistinct() {
        _bve.parse("NOT T.COL is distinct from t1.col2");
        // System.out.println(_bve.format());
        assertEquals("NOT T.COL IS DISTINCT FROM T1.COL2", _bve.format(), "DISTINCT not recognized!");
    }

    @Test
    public void testOr() {
        _bve.parse("COL1 IS NULL OR EXISTS (SELECT CUR FROM DUAL)");
        // System.out.println(_bve.format());
        assertEquals("COL1 IS NULL OR EXISTS(SELECT\r\n  CUR\r\nFROM DUAL)", _bve.format(), "OR not recognized!");
    }

    @Test
    public void testAnd() {
        _bve.parse("(A1 = 5) and (NOT T.COL is distinct from t1.col2)");
        // System.out.println(_bve.format());
        assertEquals("(A1 = 5) AND (NOT T.COL IS DISTINCT FROM T1.COL2)", _bve.format(), "AND not recognized!");
    }

    @Test
    public void testIsTrue() {
        _bve.parse("(A1 = 5) IS not true");
        // System.out.println(_bve.format());
        assertEquals("(A1 = 5) IS NOT TRUE", _bve.format(), "IS NOT TRUE not recognized!");
    }


}
