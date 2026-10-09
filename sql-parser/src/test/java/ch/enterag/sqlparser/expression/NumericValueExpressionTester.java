package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumericValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private NumericValueExpression _nve = null;

    @BeforeEach
    public void setUp() {
        _nve = _sf.newNumericValueExpression();
    }

    @Test
    public void testUnsigned() {
        _nve.parse("123");
        // System.out.println(_nve.format());
        assertEquals("123", _nve.format(), "Unsigned numeric value expression not recognized !");
    }

    @Test
    public void testAddition() {
        _nve.parse("123+456");
        // System.out.println(_nve.format());
        assertEquals("123 + 456", _nve.format(), "Addition in numeric value expression not recognized !");
    }

    @Test
    public void testMultiplication() {
        _nve.parse("123*456");
        // System.out.println(_nve.format());
        assertEquals("123*456", _nve.format(), "Addition in numeric value expression not recognized !");
    }

    @Test
    public void testAdditionAndMultiplication() {
        _nve.parse("123*456+789");
        // System.out.println(_nve.format());
        assertEquals("123*456 + 789", _nve.format(), "Addition in numeric value expression not recognized !");
    }

    @Test
    public void testMultiplicationAndAddition() {
        _nve.parse("123*(456+789)");
        //System.out.println(_nve.format());
        assertEquals("123*(456 + 789)", _nve.format(), "Addition in numeric value expression not recognized !");
    }

    @Test
    public void testNumericFunctionMod() {
        _nve.parse("Mod(11,2)");
        // System.out.println(_nve.format());
        assertEquals("MOD(11, 2)", _nve.format(), "Numeric function MOD numeric value expression not recognized !");
    }

    @Test
    public void testValueExpressionPrimary() {
        _nve.parse("a1");
        // System.out.println(_nve.format());
        assertEquals("A1", _nve.format(), "Value expression primary not recognized !");
    }


}
