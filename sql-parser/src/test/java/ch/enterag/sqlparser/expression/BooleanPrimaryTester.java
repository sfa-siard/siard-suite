package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanPrimaryTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private BooleanPrimary _bp = null;

    @BeforeEach
    public void setUp() {
        _bp = _sf.newBooleanPrimary();
    }

    @Test
    public void testComparison() {
        _bp.parse("A1[4] <> 'abc'");
        // System.out.println(_bp.format());
        assertEquals("A1[4] <> 'abc'", _bp.format(), "Comparison not recognized!");
    }

    @Test
    public void testSimpleBetween() {
        _bp.parse("'aaa' BETWEEN A1[4] AND 'abc'");
        // System.out.println(_bp.format());
        assertEquals("'aaa' BETWEEN A1[4] AND 'abc'", _bp.format(), "BETWEEN not recognized!");
    }

    @Test
    public void testComplexBetween() {
        _bp.parse("'aaa' NOT BETWEEN ASYMMETRIC \"A\"[4] AND 'abc'");
        // System.out.println(_bp.format());
        assertEquals("'aaa' NOT BETWEEN ASYMMETRIC \"A\"[4] AND 'abc'", _bp.format(), "BETWEEN with symmetric option not recognized!");
    }

    @Test
    public void testIn() {
        _bp.parse("'aaa' IN B[4], 'abc', t.col");
        // System.out.println(_bp.format());
        assertEquals("'aaa' IN B[4] 'abc' T.COL", _bp.format(), "IN not recognized!");
    }

    @Test
    public void testInQuery() {
        _bp.parse("'aaa' NOT IN (SELECT A1 FROM t)");
        // System.out.println(_bp.format());
        assertEquals("'aaa' NOT IN (SELECT\r\n  A1\r\nFROM T)", _bp.format(), "IN not recognized!");
    }

    @Test
    public void testLike() {
        _bp.parse("'aaa' LIKE 'aa%' ESCAPE '\\'");
        // System.out.println(_bp.format());
        assertEquals("'aaa' LIKE 'aa%' ESCAPE '\\'", _bp.format(), "LIKE not recognized!");
    }

    @Test
    public void testSimilar() {
        _bp.parse("'aAa' NOT SIMILAR TO 'aa%' ESCAPE '\\'");
        // System.out.println(_bp.format());
        assertEquals("'aAa' NOT SIMILAR TO 'aa%' ESCAPE '\\'", _bp.format(), "SIMILAR not recognized!");
    }

    @Test
    public void testNull() {
        _bp.parse("t.col is null");
        // System.out.println(_bp.format());
        assertEquals("T.COL IS NULL", _bp.format(), "IS NULL not recognized!");
    }

    @Test
    public void testQuantified() {
        _bp.parse("t1.col <= some (select a1 from t2)");
        // System.out.println(_bp.format());
        assertEquals("T1.COL <= SOME(SELECT\r\n  A1\r\nFROM T2)", _bp.format(), "Quantified comparison not recognized!");
    }

    @Test
    public void testExists() {
        _bp.parse("exists(select a1 from t2)");
        // System.out.println(_bp.format());
        assertEquals("EXISTS(SELECT\r\n  A1\r\nFROM T2)", _bp.format(), "EXISTS not recognized!");
    }

    @Test
    public void testUnique() {
        _bp.parse("unique(select a1 from t2)");
        // System.out.println(_bp.format());
        assertEquals("UNIQUE(SELECT\r\n  A1\r\nFROM T2)", _bp.format(), "UNIQUE not recognized!");
    }

    @Test
    public void testNormalized() {
        _bp.parse("'aaa' is not normalized");
        // System.out.println(_bp.format());
        assertEquals("'aaa' IS NOT NORMALIZED", _bp.format(), "NORMALIZED not recognized!");
    }

    @Test
    public void testMatch() {
        _bp.parse("T.COL MATCH SIMPLE (select a1 from t2)");
        // System.out.println(_bp.format());
        assertEquals("T.COL MATCH SIMPLE(SELECT\r\n  A1\r\nFROM T2)", _bp.format(), "MATCH not recognized!");
    }

    @Test
    public void testOverlaps() {
        _bp.parse("T.COL OVERLAPS B");
        // System.out.println(_bp.format());
        assertEquals("T.COL OVERLAPS B", _bp.format(), "OVERLAPS not recognized!");
    }

    @Test
    public void testDistinct() {
        _bp.parse("T.COL is distinct from t1.col2");
        // System.out.println(_bp.format());
        assertEquals("T.COL IS DISTINCT FROM T1.COL2", _bp.format(), "DISTINCT not recognized!");
    }

    @Test
    public void testMember() {
        _bp.parse("T.COL member of t1.col2");
        // System.out.println(_bp.format());
        assertEquals("T.COL MEMBER OF T1.COL2", _bp.format(), "MEMBER not recognized!");
    }

    @Test
    public void testSubmultiset() {
        _bp.parse("T.COL submultiset of t1.col2");
        // System.out.println(_bp.format());
        assertEquals("T.COL SUBMULTISET OF T1.COL2", _bp.format(), "SUBMULTISET not recognized!");
    }

    @Test
    public void testSet() {
        _bp.parse("T.COL is a set");
        // System.out.println(_bp.format());
        assertEquals("T.COL IS A SET", _bp.format(), "SET not recognized!");
    }

    @Test
    public void testType() {
        _bp.parse("T.COL is of (udt1, only c1.s2.udt2, s1.udt3)");
        // System.out.println(_bp.format());
        assertEquals("T.COL IS OF(UDT1, ONLY C1.S2.UDT2, S1.UDT3)", _bp.format(), "TYPE not recognized!");
    }

    @Test
    public void testParenthesized() {
        _bp.parse("(t1.col <= some (select a1 from t2))");
        // System.out.println(_bp.format());
        assertEquals("(T1.COL <= SOME(SELECT\r\n  A1\r\nFROM T2))", _bp.format(), "Parenthesized expression not recognized!");
    }

}
