package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropFunctionStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DropFunctionStatement _dfs = null;

    @BeforeEach
    public void setUp() {
        _dfs = _sf.newDropFunctionStatement();
    }

    @Test
    public void testSimple() {
        _dfs.parse("DROP specific Function cat1.sch1.countfunc cascade");
        // System.out.println(_dfs.format());
        assertEquals("DROP SPECIFIC FUNCTION CAT1.SCH1.COUNTFUNC CASCADE", _dfs.format(), "DROP FUNCTION statement not recognized!");
    }

    @Test
    public void testComplex() {
        _dfs.parse("DROP Function cat1.sch1.countfunc2(in idDel integer) restrict");
        // System.out.println(_dfs.format());
        assertEquals("DROP FUNCTION CAT1.SCH1.COUNTFUNC2(IN IDDEL INT) RESTRICT", _dfs.format(), "DROP FUNCTION statement not recognized!");
    }

}
