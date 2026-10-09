package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private ValueExpression _ve = null;

    @BeforeEach
    public void setUp() {
        _ve = _sf.newValueExpression();
    }

    @Test
    public void testInteger() {
        _ve.parse("-123456789");
        System.out.println(_ve.format());
        assertEquals("-123456789", _ve.format(), "Integer literal not recognized!");
    }

    @Test
    public void testMinimum() {
        _ve.parse("a1");
        // System.out.println(_ve.format());
        assertEquals("A1", _ve.format(), "Minimum value expression not recognized!");
    }

    @Test
    public void testIdChain() {
        _ve.parse("emp.income");
        // System.out.println(_ve.format());
        assertEquals("EMP.INCOME", _ve.format(), "IdChain (value expression primary) not recognized!");
    }

    @Test
    public void testColumnReference() {
        _ve.parse("C1.S1.TEST.COL");
        // System.out.println(_ve.format());
        assertEquals("C1.S1.TEST.COL", _ve.format(), "Column reference not recognized!");
    }

}
