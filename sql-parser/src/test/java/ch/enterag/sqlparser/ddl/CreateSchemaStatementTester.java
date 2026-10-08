package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateSchemaStatementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private CreateSchemaStatement _css = null;

    @BeforeEach
    public void setUp() {
        _css = _sf.newCreateSchemaStatement();
    }

    @Test
    public void testSimple() {
        _css.parse("CREATE SCHEMA cat.\"schema\"");
        // System.out.println(_css.format());
        assertEquals("CREATE SCHEMA CAT.\"schema\"", _css.format(), "CREATE SCHEMA statement not recognized!");
    }

    @Test
    public void testComplex() {
        _css.parse("CREATE SCHEMA \"schema\" AUTHORIZATION me");
        // System.out.println(_css.format());
        assertEquals("CREATE SCHEMA \"schema\" AUTHORIZATION ME", _css.format(), "CREATE SCHEMA statement not recognized!");
    }

}
