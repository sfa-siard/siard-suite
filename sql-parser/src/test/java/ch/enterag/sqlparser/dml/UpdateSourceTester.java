package ch.enterag.sqlparser.dml;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpdateSourceTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private UpdateSource _us = null;

    @Before
    public void setUp() {
        _us = _sf.newUpdateSource();
    }

    @Test
    public void testNumericLiteral() {
        _us.parse("45");
        // System.out.println(_us.format());
        String sExpected = "45";
        assertEquals("UPDATE source not recognized!", sExpected, _us.format());
    }

    @Test
    public void testStringLiteral() {
        _us.parse("'abc'");
        // System.out.println(_us.format());
        String sExpected = "'abc'";
        assertEquals("UPDATE source not recognized!", sExpected, _us.format());
    }

    @Test
    public void testDateLiteral() {
        _us.parse("date'2016-05-09'");
        // System.out.println(_us.format());
        String sExpected = "DATE'2016-05-09'";
        assertEquals("UPDATE source not recognized!", sExpected, _us.format());
    }

}
