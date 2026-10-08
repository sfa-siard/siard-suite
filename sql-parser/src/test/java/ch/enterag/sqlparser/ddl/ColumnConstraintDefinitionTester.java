package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnConstraintDefinitionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private ColumnConstraintDefinition _ccd = null;

    @BeforeEach
    public void setUp() {
        _ccd = _sf.newColumnConstraintDefinition();
    }

    @Test
    public void testNotNull() {
        _ccd.parse("constraint nnc not null");
        // System.out.println(_ccd.format());
        assertEquals("CONSTRAINT NNC NOT NULL", _ccd.format(), "NOT NULL constraint not recognized!");
    }

    @Test
    public void testDeferred() {
        _ccd.parse("constraint nnc not null initially deferred");
        // System.out.println(_ccd.format());
        assertEquals("CONSTRAINT NNC NOT NULL INITIALLY DEFERRED", _ccd.format(), "Deferred NOT NULL constraint not recognized!");

    }

    @Test
    public void testUnique() {
        _ccd.parse("UNIQUE VALUE");
        // System.out.println(_ccd.format());
        assertEquals("UNIQUE", _ccd.format(), "UNIQUE constraint not recognized!");
    }

    @Test
    public void testPrimaryKey() {
        _ccd.parse("CONSTRAINT \"Schema\".\"pkC\" PRIMARY KEY");
        // System.out.println(_ccd.format());
        assertEquals("CONSTRAINT \"Schema\".\"pkC\" PRIMARY KEY", _ccd.format(), "PRIMARY KEY constraint not recognized!");
    }

    @Test
    public void testReferences() {
        _ccd.parse("references cat.\"schem\".\"tabRef\"(colRef)");
        // System.out.println(_ccd.format());
        assertEquals("REFERENCES CAT.\"schem\".\"tabRef\"(COLREF)", _ccd.format(), "REFERENCES constraint not recognized!");
    }

    @Test
    public void testReferencesWithOptions() {
        _ccd.parse("references tabRef(colRef) match simple on update set default on delete no action");
        // System.out.println(_ccd.format());
        assertEquals("REFERENCES TABREF(COLREF) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE SET DEFAULT", _ccd.format(), "REFERENCES constraint with options not recognized!");
    }

    @Test
    public void testCheck() {
        _ccd.parse("check(col1='aa' and col2 = col3)");
        // System.out.println(_ccd.format());
        assertEquals("CHECK(COL1 = 'aa' AND COL2 = COL3)", _ccd.format(), "CHECK constraint not recognized!");
    }
}
