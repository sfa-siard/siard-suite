package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WindowSpecificationTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private WindowSpecification _ws = null;

    @BeforeEach
    public void setUp() {
        _ws = _sf.newWindowSpecification();
    }

    @Test
    public void testBetween() {
        _ws.parse("WN ROWS BETWEEN UNBOUNDED PRECEDING AND 5 FOLLOWING");
        System.out.println(_ws.format());
        assertEquals("WN ROWS BETWEEN UNBOUNDED PRECEDING AND 5 FOLLOWING", _ws.format(), "Window BETWEEN specification not recognized!");
    }

}
