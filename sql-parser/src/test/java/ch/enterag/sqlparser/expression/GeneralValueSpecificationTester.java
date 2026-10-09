package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneralValueSpecificationTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private GeneralValueSpecification _gvs = null;

    @BeforeEach
    public void setUp() {
        _gvs = _sf.newGeneralValueSpecification();
    }

    @Test
    public void testVariable() {
        _gvs.parse(":var");
        // System.out.println(_gvs.format());
        assertEquals(":VAR", _gvs.format(), "Variable not recognized!");
    }

    @Test
    public void testIndicator() {
        _gvs.parse(":var indicator :\"ind\"");
        // System.out.println(_gvs.format());
        assertEquals(":VAR INDICATOR :\"ind\"", _gvs.format(), "Indicator not recognized!");
    }

    @Test
    public void testReference() {
        _gvs.parse("aa.\"am\"");
        // System.out.println(_gvs.format());
        assertEquals("AA.\"am\"", _gvs.format(), "Parameter not recognized!");
    }

    @Test
    public void testColumnOrParameter() {
        _gvs.parse("aa.bb.ddd.\"am\".ccd");
        // System.out.println(_gvs.format());
        assertEquals("AA.BB.DDD.\"am\".CCD", _gvs.format(), "Parameter not recognized!");
    }

    @Test
    public void testDynamicValue() {
        _gvs.parse("?");
        // System.out.println(_gvs.format());
        assertEquals("?", _gvs.format(), "Dynamic value not recognized!");
    }

}
