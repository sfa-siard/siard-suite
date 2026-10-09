package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropProcedureStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DropProcedureStatement _dps = null;

    @BeforeEach
    public void setUp() {
        _dps = _sf.newDropProcedureStatement();
    }

    @Test
    public void testSimple() {
        _dps.parse("DROP specific Procedure cat1.sch1.delproc cascade");
        // System.out.println(_dps.format());
        assertEquals("DROP SPECIFIC PROCEDURE CAT1.SCH1.DELPROC CASCADE", _dps.format(), "DROP PROCEDURE statement not recognized!");
    }

    @Test
    public void testComplex() {
        _dps.parse("DROP Procedure cat1.sch1.delproc(in idDel integer) restrict");
        // System.out.println(_dps.format());
        assertEquals("DROP PROCEDURE CAT1.SCH1.DELPROC(IN IDDEL INT) RESTRICT", _dps.format(), "DROP PROCEDURE statement not recognized!");
    }

}
