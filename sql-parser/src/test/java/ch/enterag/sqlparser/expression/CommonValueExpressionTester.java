package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private CommonValueExpression _cve = null;

    @BeforeEach
    public void setUp() {
        _cve = _sf.newCommonValueExpression();
    }

    @Test
    public void testMinimum() {
        _cve.parse("a1");
        // System.out.println(_cve.format());
        assertEquals("A1", _cve.format(), "Minimum common value expression not recognized!");
    }

    @Test
    public void testIdChain() {
        _cve.parse("emp.income");
        // System.out.println(_cve.format());
        assertEquals("EMP.INCOME", _cve.format(), "Id chain (value expression primary) not recognized!");
    }

    @Test
    public void testNumeric() {
        _cve.parse("123");
        // System.out.println(_cve.format());
        assertEquals("123", _cve.format(), "Numeric common value expression not recognized!");
    }

    @Test
    public void testString() {
        _cve.parse("'a ''character'' string with ''quotes'''");
        // System.out.println(_cve.format());
        assertEquals("'a ''character'' string with ''quotes'''", _cve.format(), "String common value expression not recognized!");
    }

}
