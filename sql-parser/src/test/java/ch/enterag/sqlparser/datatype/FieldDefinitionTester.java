package ch.enterag.sqlparser.datatype;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldDefinitionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private FieldDefinition _fd = null;

    @BeforeEach
    public void setUp() {
        _fd = _sf.newFieldDefinition();
    }


    @Test
    public void testFieldDefinition() {
        _fd.parse("\"SomeField\" INTEGER REFERENCES ARE CHECKED");
        assertEquals("\"SomeField\" INT REFERENCES ARE CHECKED", _fd.format(), "Field definition not recognized!");
    }

}
