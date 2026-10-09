package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableConstraintDefinitionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private TableConstraintDefinition _tcd = null;

    @BeforeEach
    public void setUp() {
        _tcd = _sf.newTableConstraintDefinition();
    }

    @Test
    public void testUnique() {
        _tcd.parse("UNIQUE(COL1, \"col2\")");
        // System.out.println(_tcd.format());
        assertEquals("UNIQUE(COL1, \"col2\")", _tcd.format(), "UNIQUE constraint not recognized!");
    }

    @Test
    public void testDeferrability() {
        _tcd.parse("UNIQUE(COL1, \"col2\") NOT DEFERRABLE INITIALLY IMMEDIATE");
        // System.out.println(_tcd.format());
        assertEquals("UNIQUE(COL1, \"col2\") NOT DEFERRABLE INITIALLY IMMEDIATE", _tcd.format(), "UNIQUE constraint with deferrability not recognized!");
    }

    @Test
    public void testPrimaryKey() {
        _tcd.parse("PRIMARY KEY(COL1, \"col2\",COL3)");
        // System.out.println(_tcd.format());
        assertEquals("PRIMARY KEY(COL1, \"col2\", COL3)", _tcd.format(), "PRIMARY KEY constraint not recognized!");
    }

    @Test
    public void testForeignKey() {
        _tcd.parse("FOREIGN KEY(COL1, \"col2\",COL3) REFERENCES schem.\"Table\"(rcol1, \"rcol2\",RCOL3)");
        // System.out.println(_tcd.format());
        assertEquals("FOREIGN KEY(COL1, \"col2\", COL3) REFERENCES SCHEM.\"Table\"(RCOL1, \"rcol2\", RCOL3)", _tcd.format(), "FOREIGN KEY constraint not recognized!");
    }

    @Test
    public void testForeignKeyWithOptions() {
        _tcd.parse("FOREIGN KEY(COL1,Col2) REFERENCES Tab(rcol1,RCOL2) MATCH FULL ON UPDATE SET DEFAULT");
        // System.out.println(_tcd.format());
        assertEquals("FOREIGN KEY(COL1, COL2) REFERENCES TAB(RCOL1, RCOL2) MATCH FULL ON UPDATE SET DEFAULT", _tcd.format(), "FOREIGN KEY constraint with options not recognized!");
    }

    @Test
    public void testCheck() {
        _tcd.parse("CHECK(COL1 = \"col2\")");
        // System.out.println(_tcd.format());
        assertEquals("CHECK(COL1 = \"col2\")", _tcd.format(), "CHECK constraint not recognized!");
    }

}
