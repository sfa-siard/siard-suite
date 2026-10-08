package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableElementTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private TableElement _te = null;

    @BeforeEach
    public void setUp() {
        _te = _sf.newTableElement();
    }

    @Test
    public void testColumnDefinition() {
        _te.parse("a_col varchar(255) default 'def'");
        // System.out.println(_te.format());
        assertEquals("A_COL VARCHAR(255) DEFAULT 'def'", _te.format(), "Column definition not recognized!");
    }

    @Test
    public void testGenerated() {
        _te.parse("Col1 varchar(255) generated always as ('aa' || acol)");
        // System.out.println(_te.format());
        assertEquals("COL1 VARCHAR(255) GENERATED ALWAYS AS('aa' || ACOL)", _te.format(), "GENERATED ALWAYS AS clause not recognized!");
    }

    @Test
    public void testColumnConstraint() {
        _te.parse("Col1 varchar(255) default 'def' generated always as ('aa' || acol) constraint const01 not null initially immediate");
        // System.out.println(_te.format());
        assertEquals("COL1 VARCHAR(255) DEFAULT 'def' GENERATED ALWAYS AS('aa' || ACOL) CONSTRAINT CONST01 NOT NULL INITIALLY IMMEDIATE", _te.format(), "GENERATED ALWAYS AS clause not recognized!");
    }

    @Test
    public void testTableConstraint() {
        _te.parse("FOREIGN KEY(COL1,Col2) REFERENCES Tab(rcol1,RCOL2) MATCH FULL ON UPDATE SET DEFAULT");
        assertEquals("FOREIGN KEY(COL1, COL2) REFERENCES TAB(RCOL1, RCOL2) MATCH FULL ON UPDATE SET DEFAULT", _te.format(), "Table constraint not recognized!");
    }

    @Test
    public void testLikeClause() {
        _te.parse("LIKE t1 INCLUDING IDENTITY");
        // System.out.println(_te.format());
        assertEquals("LIKE T1 INCLUDING IDENTITY", _te.format(), "Like clause not recognized!");
    }

    @Test
    public void testSelfRefColumn() {
        _te.parse("REF IS obj_id SYSTEM GENERATED");
        // System.out.println(_te.format());
        assertEquals("REF IS OBJ_ID SYSTEM GENERATED", _te.format(), "Self-referencing column specification not recognized!");
    }

    @Test
    public void testScope() {
        _te.parse("\"Column\" with options scope schema1.table1");
        // System.out.println(_te.format());
        assertEquals("\"Column\" WITH OPTIONS SCOPE SCHEMA1.TABLE1", _te.format(), "SCOPE column option not recognized!");
    }

    @Test
    public void testDefault() {
        _te.parse("Col1 with options default 19");
        // System.out.println(_te.format());
        assertEquals("COL1 WITH OPTIONS DEFAULT 19", _te.format(), "DEFAULT column option not recognized!");
    }

    @Test
    public void testPrimaryKey() {
        _te.parse("Col1 with options primary key");
        // System.out.println(_te.format());
        assertEquals("COL1 WITH OPTIONS PRIMARY KEY", _te.format(), "Column constraint not recognized!");
    }

}
