package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewElementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private ViewElement _ve = null;

    @BeforeEach
    public void setUp() {
        _ve = _sf.newViewElement();
    }

    @Test
    public void testSelfRefColumn() {
        _ve.parse("REF IS obj_id SYSTEM GENERATED");
        // System.out.println(_ve.format());
        assertEquals("REF IS OBJ_ID SYSTEM GENERATED", _ve.format(), "Self-referencing column specification not recognized!");
    }

    @Test
    public void testScope() {
        _ve.parse("\"Column\" with options scope schema1.table1");
        // System.out.println(_ve.format());
        assertEquals("\"Column\" WITH OPTIONS SCOPE SCHEMA1.TABLE1", _ve.format(), "SCOPE column option not recognized!");
    }

}
