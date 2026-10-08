package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateTriggerStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private CreateTriggerStatement _cts = null;

    @BeforeEach
    public void setUp() {
        _cts = _sf.newCreateTriggerStatement();
    }

    @Test
    public void testSimple() {
        _cts.parse("CREATE TRIGGER cat.sch.tg before delete on cat.sch.tab insert current_timestamp into archive");
        // System.out.println(_cts.format());
        assertEquals("CREATE TRIGGER CAT.SCH.TG BEFORE DELETE ON CAT.SCH.TAB INSERT CURRENT_TIMESTAMP INTO ARCHIVE", _cts.format(), "CREATE TRIGGER statement not recognized!");
    }

}
