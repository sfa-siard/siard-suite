package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoutineBodyTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private RoutineBody _rb = null;

    @BeforeEach
    public void setUp() {
        _rb = _sf.newRoutineBody();
    }

    @Test
    public void testSimple() {
        _rb.parse("delete from tab where id = idDel");
        System.out.println(_rb.format());
        assertEquals("DELETE FROM TAB WHERE ID = IDDEL", _rb.format(), "Routine body not recognized!");
    }

    @Test
    public void testComplex() {
        _rb.parse("delete from \"tab\" where id = idDel and v = 'abc'");
        // System.out.println(_rb.format());
        assertEquals("DELETE FROM \"tab\" WHERE ID = IDDEL AND V = 'abc'", _rb.format(), "Routine body not recognized!");
    }

}
