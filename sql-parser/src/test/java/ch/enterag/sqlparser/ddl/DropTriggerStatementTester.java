package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropTriggerStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DropTriggerStatement _dts = null;

    @BeforeEach
    public void setUp() {
        _dts = _sf.newDropTriggerStatement();
    }

    @Test
    public void test() {
        _dts.parse("DROP TRIGGER cat.sch.tg");
        // System.out.println(_dts.format());
        assertEquals("DROP TRIGGER CAT.SCH.TG", _dts.format(), "DROP TRIGGER statement not recognized!");
    }

}
