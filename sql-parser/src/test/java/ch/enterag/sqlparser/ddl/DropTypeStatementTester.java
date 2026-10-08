package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropTypeStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DropTypeStatement _dts = null;

    @BeforeEach
    public void setUp() {
        _dts = _sf.newDropTypeStatement();
    }

    @Test
    public void test() {
        _dts.parse("DROP TYPE cat.sch.\"typ\" cascade");
        // System.out.println(dts.format());
        assertEquals("DROP TYPE CAT.SCH.\"typ\" CASCADE", _dts.format(), "DROP TYPE statement not recognized!");
    }

}
