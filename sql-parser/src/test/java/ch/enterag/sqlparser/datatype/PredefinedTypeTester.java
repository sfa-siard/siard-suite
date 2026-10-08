package ch.enterag.sqlparser.datatype;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.enums.PreType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PredefinedTypeTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private PredefinedType _pdt = null;

    @BeforeEach
    public void setUp() {
        _pdt = _sf.newPredefinedType();
    }

    @Test
    public void testFormat() {
        _pdt.initialize(PreType.CHAR, 5, null, -1, -1, -1, null, null);
        assertEquals("CHAR(5)", _pdt.format(), "Format from fields failed!");
    }

    @Test
    public void testParseComment() {
        _pdt.parse("CHARACTER /* some coment */(54)");
        // pdt.listTokens();
        assertEquals("CHAR(54)", _pdt.format(), "CHARACTER type not recognized!");
    }

    @Test
    public void testParseLineComment() {
        _pdt.parse("CHARACTER(54) -- and some intesting stuff");
        // pdt.listTokens();
        assertEquals("CHAR(54)", _pdt.format(), "CHARACTER type not recognized!");
    }

    @Test
    public void testParseChar() {
        _pdt.parse("CHARacter(54)");
        assertEquals("CHAR(54)", _pdt.format(), "CHARACTER type not recognized!");
    }

    @Test
    public void testParseVarchar() {
        _pdt.parse("CHAR varying(54)");
        assertEquals("VARCHAR(54)", _pdt.format(), "VARCHAR type not recognized!");
    }

    @Test
    public void testParseClob() {
        _pdt.parse("CHARACTER large object(54m)");
        assertEquals("CLOB(54M)", _pdt.format(), "CLOB type not recognized!");
    }

    @Test
    public void testParseNChar() {
        _pdt.parse("NATional CHARacter(54)");
        assertEquals("NCHAR(54)", _pdt.format(), "NCHAR type not recognized!");
    }

    @Test
    public void testParseNVarchar() {
        _pdt.parse("national CHAR varying(54)");
        assertEquals("NCHAR VARYING(54)", _pdt.format(), "NVARCHAR type not recognized!");
    }

    @Test
    public void testParseNClob() {
        _pdt.parse("NATIONAL CHARACTER large object(54m)");
        assertEquals("NCLOB(54M)", _pdt.format(), "NCLOB type not recognized!");
    }

    @Test
    public void testParseBinary() {
        _pdt.parse("Binary(5)");
        assertEquals("BINARY(5)", _pdt.format(), "BINARY type not recognized!");
    }

    @Test
    public void testParseVarbinary() {
        _pdt.parse("binary varying(255)");
        assertEquals("VARBINARY(255)", _pdt.format(), "VARBINARY type not recognized!");
    }

    @Test
    public void testParseBlob() {
        _pdt.parse("BLOB(54)");
        assertEquals("BLOB(54)", _pdt.format(), "BLOB type not recognized!");
    }

    @Test
    public void testParseNumeric() {
        _pdt.parse("numeric(32,4)");
        assertEquals("NUMERIC(32, 4)", _pdt.format(), "NUMERIC type not recognized!");
    }

    @Test
    public void testParseDecimal() {
        _pdt.parse("dec(32)");
        assertEquals("DEC(32)", _pdt.format(), "DECIMAL type not recognized!");
    }

    @Test
    public void testParseSmallInt() {
        _pdt.parse("smallint");
        assertEquals("SMALLINT", _pdt.format(), "SMALLINT type not recognized!");
    }

    @Test
    public void testParseInt() {
        _pdt.parse("int");
        assertEquals("INT", _pdt.format(), "INTEGER type not recognized!");
    }

    @Test
    public void testParseBigInt() {
        _pdt.parse("BIGINT");
        assertEquals("BIGINT", _pdt.format(), "BIGINT type not recognized!");
    }

    @Test
    public void testParseFloat() {
        _pdt.parse("float(10)");
        assertEquals("FLOAT(10)", _pdt.format(), "FLOAT type not recognized!");
    }

    @Test
    public void testParseReal() {
        _pdt.parse("real");
        assertEquals("REAL", _pdt.format(), "REAL type not recognized!");
    }

    @Test
    public void testParseDouble() {
        _pdt.parse("double precision");
        assertEquals("DOUBLE PRECISION", _pdt.format(), "DOUBLE type not recognized!");
    }

    @Test
    public void testParseBoolean() {
        _pdt.parse("bOOlean");
        assertEquals("BOOLEAN", _pdt.format(), "BOOLEAN type not recognized!");
    }

    @Test
    public void testParseDate() {
        _pdt.parse("DATE");
        assertEquals("DATE", _pdt.format(), "DATE type not recognized!");
    }

    @Test
    public void testParseTime() {
        _pdt.parse("TIME(4) WITH TIME ZONE");
        assertEquals("TIME(4) WITH TIME ZONE", _pdt.format(), "TIME type not recognized!");
    }

    @Test
    public void testParseTimestamp() {
        _pdt.parse("TIMESTAMP");
        assertEquals("TIMESTAMP", _pdt.format(), "TIMESTAMP type not recognized!");
    }

    @Test
    public void testParseIntervalYearMonth() {
        _pdt.parse("INTERVAL YEAR(2) /* in 21st century */ To Month");
        System.out.println(_pdt.format());
        assertEquals("INTERVAL YEAR(2) TO MONTH", _pdt.format(), "INTERVAL YEAR TO MONTH type not recognized!");
    }

    @Test
    public void testParseIntervalDayMinute() {
        _pdt.parse("INTERVAL DAY To MINUTE");
        assertEquals("INTERVAL DAY TO MINUTE", _pdt.format(), "INTERVAL DAY TO MINUTE type not recognized!");
    }

    @Test
    public void testParseIntervalSecond() {
        _pdt.parse("INTERVAL SECOND(2,5)");
        assertEquals("INTERVAL SECOND(2, 5)", _pdt.format(), "INTERVAL SECOND(2,5)!");
    }

    @Test
    public void testParseDatalink() {
        _pdt.parse("DatALINK");
        assertEquals("DATALINK", _pdt.format(), "DATALINK type not recognized!");
    }
}
