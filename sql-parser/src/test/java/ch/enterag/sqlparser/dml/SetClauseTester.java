package ch.enterag.sqlparser.dml;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SetClauseTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private SetClause _sc = null;

    @Before
    public void setUp() {
        _sc = _sf.newSetClause();
    }

    @Test
    public void testNumericLiteral() {
        _sc.parse("id=45");
        // System.out.println(us.format());
        String sExpected = "ID = 45";
        assertEquals("SET CLAUSE not recognized!", sExpected, _sc.format());
    }

    @Test
    public void testStringLiteral() {
        _sc.parse("s = 'abc'");
        // System.out.println(us.format());
        String sExpected = "S = 'abc'";
        assertEquals("SET CLAUSE not recognized!", sExpected, _sc.format());
    }

    @Test
    public void testDateLiteral() {
        _sc.parse("dat=date'2016-05-09'");
        // System.out.println(us.format());
        String sExpected = "DAT = DATE'2016-05-09'";
        assertEquals("SET CLAUSE not recognized!", sExpected, _sc.format());
    }

}
