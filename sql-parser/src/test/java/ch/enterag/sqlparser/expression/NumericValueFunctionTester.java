package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumericValueFunctionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private NumericValueFunction _nvf = null;

    @BeforeEach
    public void setUp() {
        _nvf = _sf.newNumericValueFunction();
    }

    @Test
    public void testNumericFunctionAbs() {
        _nvf.parse("Abs(-11)");
        System.out.println(_nvf.format());
        assertEquals("ABS(-11)", _nvf.format(), "Numeric value function ABS not recognized !");
    }

    @Test
    public void testNumericFunctionMod() {
        _nvf.parse("Mod(11,2)");
        // System.out.println(_nvf.format());
        assertEquals("MOD(11, 2)", _nvf.format(), "Numeric value function MOD not recognized !");
    }

    @Test
    public void testNumericFunctionLn() {
        _nvf.parse("Ln(11)");
        // System.out.println(_nvf.format());
        assertEquals("LN(11)", _nvf.format(), "Numeric value function LN not recognized !");
    }

    @Test
    public void testNumericFunctionExp() {
        _nvf.parse("Exp(11)");
        // System.out.println(_nvf.format());
        assertEquals("EXP(11)", _nvf.format(), "Numeric value function EXP not recognized !");
    }

    @Test
    public void testNumericFunctionPower() {
        _nvf.parse("Power(11,3)");
        // System.out.println(_nvf.format());
        assertEquals("POWER(11, 3)", _nvf.format(), "Numeric value function POWER not recognized !");
    }

    @Test
    public void testNumericFunctionFloor() {
        _nvf.parse("Floor(11.45)");
        // System.out.println(_nvf.format());
        assertEquals("FLOOR(11.45)", _nvf.format(), "Numeric value function FLOOR not recognized !");
    }

    @Test
    public void testNumericFunctionCeiling() {
        _nvf.parse("Ceil(11.45)");
        // System.out.println(_nvf.format());
        assertEquals("CEILING(11.45)", _nvf.format(), "Numeric value function CEILING not recognized !");
    }

    @Test
    public void testNumericFunctionWidthBucket() {
        _nvf.parse("Width_Bucket(11.45,23,2,56)");
        System.out.println(_nvf.format());
        assertEquals("WIDTH_BUCKET(11.45, 23, 2, 56)", _nvf.format(), "Numeric value function WIDTH_BUCKET not recognized !");
    }

    @Test
    public void testNumericFunctionOctetLength() {
        _nvf.parse("OCTET_LENGTH(COLA)");
        System.out.println(_nvf.format());

    }
}
