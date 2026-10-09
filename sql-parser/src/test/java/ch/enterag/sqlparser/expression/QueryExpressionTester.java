package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private QueryExpression _qe = null;

    @BeforeEach
    public void setUp() {
        _qe = _sf.newQueryExpression();
    }

    @Test
    public void testCountQuery() {
        _qe.parse("SELECT COUNT(*) FROM C1.S1.T1");
        // System.out.println(_qe.format());
        assertEquals("SELECT\r\n  COUNT(*)\r\nFROM C1.S1.T1", _qe.format(), "Count query not recognized!");
    }

}
