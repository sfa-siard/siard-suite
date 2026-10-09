package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private ArrayValueExpression _ave = null;

    @BeforeEach
    public void setUp() {
        _ave = _sf.newArrayValueExpression();
    }

    @Test
    public void testLiteral() {
        _ave.parse("t.COL");
        // System.out.println(_ave.format());
        assertEquals("T.COL", _ave.format(), "Array value literal not recognized!");
    }

    @Test
    public void testConcatenation() {
        _ave.parse("t.COL || \"a\"");
        // System.out.println(_ave.format());
        assertEquals("T.COL || \"a\"", _ave.format(), "Array value concatenation not recognized!");
    }

}
