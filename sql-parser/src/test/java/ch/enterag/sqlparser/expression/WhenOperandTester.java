package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenOperandTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private WhenOperand _wo = null;

    @BeforeEach
    public void setUp() {
        _wo = _sf.newWhenOperand();
    }

    @Test
    public void testMinusOne() {
        // ErrorListener.getInstance().suppressException();
        _wo.parse("-1");
        // System.out.println(_wo.format());
        assertEquals("-1", _wo.format(), "-1 not recognized!");
    }

    @Test
    public void testComparison() {
        // ErrorListener.getInstance().suppressException();
        _wo.parse(">= t.a1");
        // System.out.println(_wo.format());
        assertEquals(">= T.A1", _wo.format(), "Comparison not recognized!");
    }

    @Test
    public void testBetween() {
        _wo.parse("BETWEEN A1 and B");
        // System.out.println(_wo.format());
        assertEquals("BETWEEN A1 AND B", _wo.format(), "BETWEEN not recognized!");
    }

    @Test
    public void testIn() {
        _wo.parse("NOT IN (SELECT A1 FROM t)");
        // System.out.println(_wo.format());
        assertEquals("NOT IN (SELECT\r\n  A1\r\nFROM T)", _wo.format(), "IN not recognized!");
    }

    @Test
    public void testLike() {
        _wo.parse("LIKE 'aa%' ESCAPE '\\'");
        // System.out.println(_wo.format());
        assertEquals("LIKE 'aa%' ESCAPE '\\'", _wo.format(), "LIKE not recognized!");
    }

    @Test
    public void testSimilar() {
        _wo.parse("SIMILAR TO 'aa%' ESCAPE '\\'");
        // System.out.println(_wo.format());
        assertEquals("SIMILAR TO 'aa%' ESCAPE '\\'", _wo.format(), "SIMILAR not recognized!");
    }

    @Test
    public void testIsNull() {
        _wo.parse("IS NULL");
        // System.out.println(_wo.format());
        assertEquals("IS NULL", _wo.format(), "IS NULL not recognized!");
    }

    @Test
    public void testQuantified() {
        _wo.parse("<= some (select a1 from t2)");
        // System.out.println(_wo.format());
        assertEquals("<= SOME(SELECT\r\n  A1\r\nFROM T2)", _wo.format(), "Quantified comparison not recognized!");
    }

    @Test
    public void testMatch() {
        _wo.parse("MATCH FULL (select a1 from t2)");
        // System.out.println(_wo.format());
        assertEquals("MATCH FULL(SELECT\r\n  A1\r\nFROM T2)", _wo.format(), "MATCH not recognized!");
    }

    @Test
    public void testOverlaps() {
        _wo.parse("OVERLAPS B");
        // System.out.println(_wo.format());
        assertEquals("OVERLAPS B", _wo.format(), "OVERLAPS not recognized!");
    }

    @Test
    public void testDistinct() {
        _wo.parse("is distinct from t1.col2");
        // System.out.println(_wo.format());
        assertEquals("IS DISTINCT FROM T1.COL2", _wo.format(), "DISTINCT not recognized!");
    }

    @Test
    public void testMember() {
        _wo.parse("member of t1.col2");
        // System.out.println(_wo.format());
        assertEquals("MEMBER OF T1.COL2", _wo.format(), "MEMBER not recognized!");
    }

    @Test
    public void testSubmultiset() {
        _wo.parse("submultiset of t1.col2");
        // System.out.println(_wo.format());
        assertEquals("SUBMULTISET OF T1.COL2", _wo.format(), "SUBMULTISET not recognized!");
    }

    @Test
    public void testSet() {
        _wo.parse("is a set");
        // System.out.println(_wo.format());
        assertEquals("IS A SET", _wo.format(), "SET not recognized!");
    }

    @Test
    public void testType() {
        _wo.parse("is of (udt1, only c1.s2.udt2, s1.udt3)");
        // System.out.println(_wo.format());
        assertEquals("IS OF( UDT1, ONLY C1.S2.UDT2, S1.UDT3)", _wo.format(), "TYPE test not recognized!");
    }

}
