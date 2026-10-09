package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WindowFrameBoundTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private WindowFrameBound _wfb = null;

    @BeforeEach
    public void setUp() {
        _wfb = _sf.newWindowFrameBound();
    }

    @Test
    public void testUnboundedPreceding() {
        // ErrorListener.getInstance().suppressException();
        _wfb.parse("UNBOUNDED PRECEDING");
        System.out.println(_wfb.format());
        assertEquals("UNBOUNDED PRECEDING", _wfb.format(), "Window frame bound UNBOUNDED PRECEDING not recognized!");
    }

    @Test
    public void testNumericFollowing() {
        // ErrorListener.getInstance().suppressException();
        _wfb.parse("5 FOLLOWING");
        System.out.println(_wfb.format());
        assertEquals("5 FOLLOWING", _wfb.format(), "Window frame bound 5 FOLLOWING not recognized!");
    }


}
