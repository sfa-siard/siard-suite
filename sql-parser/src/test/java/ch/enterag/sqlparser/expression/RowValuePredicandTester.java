package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RowValuePredicandTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private RowValuePredicand _rvp = null;

    @BeforeEach
    public void setUp() {
        _rvp = _sf.newRowValuePredicand();
    }

    @Test
    public void testReference() {
        // ErrorListener.getInstance().suppressException();
        _rvp.parse("C1.S1.TEST.COL");
        System.out.println(_rvp.format());
        assertEquals("C1.S1.TEST.COL", _rvp.format(), "Column reference not recognized!");
    }

    @Test
    public void testNegative() {
        // ErrorListener.getInstance().suppressException();
        _rvp.parse("-1");
        System.out.println(_rvp.format());
        assertEquals("-1", _rvp.format(), "Negative value not recognized!");
    }

    @Test
    public void testRow() {
        _rvp.parse("ROW(C1.S1.T1.COL)");
        // System.out.println(_rvp.format());
        assertEquals("ROW(C1.S1.T1.COL)", _rvp.format(), "ROW expression not recognized!");
    }

}
