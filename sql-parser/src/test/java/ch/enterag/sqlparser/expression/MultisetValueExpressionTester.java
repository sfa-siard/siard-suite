package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultisetValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private MultisetValueExpression _mve = null;

    @BeforeEach
    public void setUp() {
        _mve = _sf.newMultisetValueExpression();
    }

    @Test
    public void testSimpleValue() {
        // ErrorListener.getInstance().suppressException();
        _mve.parse("\"A\"");
        // System.out.println(_mve.format());
        assertEquals("\"A\"", _mve.format(), "Simple multiset value not recognized!");
    }

    @Test
    public void testSetExpression() {
        // ErrorListener.getInstance().suppressException();
        _mve.parse("SET(B)");
        // System.out.println(_mve.format());
        assertEquals("SET(B)", _mve.format(), "SET expression not recognized!");
    }

    @Test
    public void testMultisetOperator() {
        // ErrorListener.getInstance().suppressException();
        _mve.parse("D MULTISET EXCEPT SET(B)");
        // System.out.println(_mve.format());
        assertEquals("D MULTISET EXCEPT SET(B)", _mve.format(), "Multiset operaator EXCEPT not recognized!");
    }

    @Test
    public void testMultisetOperatorWithQualifier() {
        // ErrorListener.getInstance().suppressException();
        _mve.parse("C MULTISET INTERSECT DISTINCT B");
        // System.out.println(_mve.format());
        assertEquals("C MULTISET INTERSECT DISTINCT B", _mve.format(), "Multiset operator with DISTINCT not recognized!");
    }

}
