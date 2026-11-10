package ch.admin.bar.siard2.oracle.datatype;

import ch.admin.bar.siard2.oracle.OracleSqlFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OraclePredefinedTypeTest {

    private static String formatOracleType(String isoType) {
        var preType = new OracleSqlFactory().newPredefinedType();
        preType.parse(isoType);
        return preType.format();
    }

    @Test
    void floatMapsToNumber() {
        assertEquals("NUMBER", formatOracleType("FLOAT"));
        assertEquals("NUMBER", formatOracleType("FLOAT(38)"));
        assertEquals("NUMBER", formatOracleType("FLOAT(10)"));
    }

    @Test
    void realMapsToBinaryFloat() {
        assertEquals("BINARY_FLOAT", formatOracleType("REAL"));
    }

    @Test
    void doublePrecisionMapsToBinaryDouble() {
        assertEquals("BINARY_DOUBLE", formatOracleType("DOUBLE PRECISION"));
    }

    @Test
    void numericKeepsPrecisionAndScale() {
        assertEquals("NUMERIC(10, 2)", formatOracleType("NUMERIC(10,2)"));
    }
}
