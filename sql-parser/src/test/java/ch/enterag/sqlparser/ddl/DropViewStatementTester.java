package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropViewStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DropViewStatement _dvs = null;

    @BeforeEach
    public void setUp() {
        _dvs = _sf.newDropViewStatement();
    }

    @Test
    public void test() {
        _dvs.parse("DROP VIEW cat.sch.vw restrict");
        // System.out.println(_dvs.format());
        assertEquals("DROP VIEW CAT.SCH.VW RESTRICT", _dvs.format(), "DROP VIEW statement not recognized!");
    }

}
