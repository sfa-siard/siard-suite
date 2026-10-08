package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropMethodStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DropMethodStatement _dms = null;

    @BeforeEach
    public void setUp() {
        _dms = _sf.newDropMethodStatement();
    }

    @Test
    public void testSimple() {
        _dms.parse("DROP specific method cat1.sch1.delmeth for udt cascade");
        // System.out.println(_dms.format());
        assertEquals("DROP SPECIFIC METHOD CAT1.SCH1.DELMETH FOR UDT CASCADE", _dms.format(), "DROP METHOD statement not recognized!");
    }

    @Test
    public void testComplex() {
        _dms.parse("DROP Method cat1.sch1.delmeth(in idDel integer) for cat.sch.ty restrict");
        // System.out.println(_dms.format());
        assertEquals("DROP METHOD CAT1.SCH1.DELMETH(IN IDDEL INT) FOR CAT.SCH.TY RESTRICT", _dms.format(), "DROP METHOD statement not recognized!");
    }

}
