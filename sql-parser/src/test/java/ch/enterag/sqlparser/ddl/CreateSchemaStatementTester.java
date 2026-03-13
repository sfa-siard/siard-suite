package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreateSchemaStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private CreateSchemaStatement _css = null;

    @Before
    public void setUp() {
        _css = _sf.newCreateSchemaStatement();
    }

    @Test
    public void testSimple() {
        _css.parse("CREATE SCHEMA cat.\"schema\"");
        // System.out.println(_css.format());
        assertEquals("CREATE SCHEMA statement not recognized!", "CREATE SCHEMA CAT.\"schema\"", _css.format());
    }

    @Test
    public void testComplex() {
        _css.parse("CREATE SCHEMA \"schema\" AUTHORIZATION me");
        // System.out.println(_css.format());
        assertEquals("CREATE SCHEMA statement not recognized!", "CREATE SCHEMA \"schema\" AUTHORIZATION ME", _css.format());
    }

}
