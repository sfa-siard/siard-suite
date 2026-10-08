package ch.enterag.sqlparser.dml;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private UpdateStatement _us = null;

    @BeforeEach
    public void setUp() {
        _us = _sf.newUpdateStatement();
    }

    @Test
    public void testSimple() {
        _us.parse("UPDATE sch.tab set id=4, s = 'abc', dat1=DATE'2016-05-15' where a1 > 5");
        // System.out.println(_us.format());
        String sExpected = "UPDATE SCH.TAB SET\r\n  ID = 4,\r\n  S = 'abc',\r\n  DAT1 = DATE'2016-05-15'\r\nWHERE A1 > 5";
        assertEquals(sExpected, _us.format(), "UPDATE statement not recognized!");
    }

}
