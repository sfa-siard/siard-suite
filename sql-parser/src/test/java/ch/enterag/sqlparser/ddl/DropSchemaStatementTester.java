package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropSchemaStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private DropSchemaStatement _dss = null;

    @BeforeEach
    public void setUp() {
        _dss = _sf.newDropSchemaStatement();
    }

    @Test
    public void test() {
        _dss.parse("DROP SCHEMA cat.\"sch\" cascade");
        // System.out.println(dts.format());
        assertEquals("DROP SCHEMA CAT.\"sch\" CASCADE", _dss.format(), "DROP SCHEMA statement not recognized!");
    }
}
