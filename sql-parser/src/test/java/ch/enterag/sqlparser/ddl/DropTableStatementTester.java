package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropTableStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DropTableStatement _dts = null;

    @BeforeEach
    public void setUp() {
        _dts = _sf.newDropTableStatement();
    }

    @Test
    public void test() {
        _dts.parse("DROP TABLE cat.sch.tab restrict");
        // System.out.println(_dts.format());
        assertEquals("DROP TABLE CAT.SCH.TAB RESTRICT", _dts.format(), "DROP TABLE statement not recognized!");
    }

}
