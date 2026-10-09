package ch.enterag.sqlparser.dml;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateSourceTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private UpdateSource _us = null;

    @BeforeEach
    public void setUp() {
        _us = _sf.newUpdateSource();
    }

    @Test
    public void testNumericLiteral() {
        _us.parse("45");
        // System.out.println(_us.format());
        String sExpected = "45";
        assertEquals(sExpected, _us.format(), "UPDATE source not recognized!");
    }

    @Test
    public void testStringLiteral() {
        _us.parse("'abc'");
        // System.out.println(_us.format());
        String sExpected = "'abc'";
        assertEquals(sExpected, _us.format(), "UPDATE source not recognized!");
    }

    @Test
    public void testDateLiteral() {
        _us.parse("date'2016-05-09'");
        // System.out.println(_us.format());
        String sExpected = "DATE'2016-05-09'";
        assertEquals(sExpected, _us.format(), "UPDATE source not recognized!");
    }

}
