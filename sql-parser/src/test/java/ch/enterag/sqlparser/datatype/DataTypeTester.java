package ch.enterag.sqlparser.datatype;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataTypeTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DataType _dt = null;

    @BeforeEach
    public void setUp() {
        _dt = _sf.newDataType();
    }

    @Test
    public void testPre() {
        _dt.parse("CHARACTER large object(54m)");
        assertEquals("CLOB(54M)", _dt.format(), "CLOB type not recognized!");
    }

    @Test
    public void testStruct() {
        _dt.parse("\"SomeType\"");
        assertEquals("\"SomeType\"", _dt.format(), "UDT type not recognized!");
    }

    @Test
    public void testRow() {
        _dt.parse("ROW (field1 INT, field2 DOUBLE PRECISION, \"field3\" char(4))");
        assertEquals("ROW(FIELD1 INT, FIELD2 DOUBLE PRECISION, \"field3\" CHAR(4))", _dt.format(), "ROW type not recognized!");
    }

    @Test
    public void testRef() {
        _dt.parse("REF (\"SomeType\") SCOPE some_table");
        assertEquals("REF(\"SomeType\") SCOPE SOME_TABLE", _dt.format(), "REF type not recognized!");
    }

    @Test
    public void testArray() {
        _dt.parse("INT ARRAY[5]");
        assertEquals("INT ARRAY[5]", _dt.format(), "ARRAY type not recognized!");
    }

    @Test
    public void testMultiset() {
        _dt.parse("CHAR(5) MULTISET");
        assertEquals("CHAR(5) MULTISET", _dt.format(), "MULTISET type not recognized!");
    }

    @Test
    public void testComplex1() {
        _dt.parse("ROW (field1 INT, field2 DOUBLE PRECISION, \"field3\" char(4)) ARRAY[5]");
        assertEquals("ROW(FIELD1 INT, FIELD2 DOUBLE PRECISION, \"field3\" CHAR(4)) ARRAY[5]", _dt.format(), "Complex ARRAY type not recognized!");
    }

}
