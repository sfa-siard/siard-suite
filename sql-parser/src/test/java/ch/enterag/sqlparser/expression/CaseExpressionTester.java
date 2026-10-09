package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CaseExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private CaseExpression _ce = null;

    @BeforeEach
    public void setUp() {
        _ce = _sf.newCaseExpression();
    }

    @Test
    public void testNullIf() {
        _ce.parse("NullIf(A1,5)");
        // System.out.println(_ce.format());
        assertEquals("NULLIF(A1, 5)", _ce.format(), "NULLIF expression not recognized!");
    }

    @Test
    public void testCoalesce() {
        _ce.parse("Coalesce(A1,5,'BBB',S1.Test)");
        // System.out.println(_ce.format());
        assertEquals("COALESCE(A1, 5, 'BBB', S1.TEST)", _ce.format(), "COALESCE expression not recognized!");
    }

    @Test
    public void testSimpleWhen() {
        _ce.parse("CASE C1.S1.TEST.COL WHEN IS NULL THEN -1 WHEN -1 THEN 0 ELSE C1.S1.TEST.COL END");
        // System.out.println(_ce.format());
        assertEquals("CASE C1.S1.TEST.COL\r\n  WHEN IS NULL THEN -1\r\n  WHEN -1 THEN 0\r\n  ELSE C1.S1.TEST.COL\r\nEND", _ce.format(), "Simple WHEN clause not recognized!");
    }

    @Test
    public void testSearchedWhen() {
        _ce.parse("CASE WHEN A1 IS NULL THEN -1 WHEN B1 = -1 THEN 0 ELSE C1.S1.TEST.COL END");
        // System.out.println(_ce.format());
        assertEquals("CASE\r\n  WHEN A1 IS NULL THEN -1\r\n  WHEN B1 = -1 THEN 0\r\n  ELSE C1.S1.TEST.COL\r\nEND", _ce.format(), "Searched WHEN clause not recognized!");
    }

}
