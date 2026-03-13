package ch.enterag.sqlparser.dml;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeleteStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DeleteStatement _ds = null;

    @Before
    public void setUp() {
        _ds = _sf.newDeleteStatement();
    }

    @Test
    public void testSimple() {
        _ds.parse("DELETE FROM sch.tab where a1 > 5");
        System.out.println(_ds.format());
        String sExpected = "DELETE FROM SCH.TAB WHERE A1 > 5";
        assertEquals("DELETE statement not recognized!", sExpected, _ds.format());
    }

}
