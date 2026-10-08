package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TablePrimaryTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private TablePrimary _tp = null;

    @BeforeEach
    public void setUp() {
        _tp = _sf.newTablePrimary();
    }

    @Test
    public void testPlainTableName() {
        // ErrorListener.getInstance().suppressException();
        _tp.parse("T1");
        // System.out.println(_tp.format());
        assertEquals("T1", _tp.format(), "Plain table name not recognized!");
    }

    @Test
    public void testTableAlias() {
        // ErrorListener.getInstance().suppressException();
        _tp.parse("\"table\" T1");
        // System.out.println(_tp.format());
        assertEquals("\"table\" AS T1", _tp.format(), "Plain alias not recognized!");
    }

    @Test
    public void testQuery() {
        // ErrorListener.getInstance().suppressException();
        _tp.parse("(SELECT * FROM T)");
        // System.out.println(_tp.format());
        assertEquals("(SELECT *\r\nFROM T)", _tp.format(), "Query not recognized!");
    }

    @Test
    public void testQueryWithAlias() {
        // ErrorListener.getInstance().suppressException();
        _tp.parse("(SELECT * FROM T) T1(A1, B1, C1)");
        // System.out.println(_tp.format());
        assertEquals("(SELECT *\r\nFROM T) AS T1(A1, B1, C1)", _tp.format(), "Query not recognized!");
    }

    @Test
    public void testUnnest() {
        _tp.parse("UNNEST(array1) WITH ORDINALITY T1(A1, B1, C1)");
        // System.out.println(_tp.format());
        assertEquals("UNNEST(ARRAY1) WITH ORDINALITY AS T1(A1, B1, C1)", _tp.format(), "UNNEST not recognized!");
    }

    @Test
    public void testTable() {
        _tp.parse("TABLE(ms1) T1");
        System.out.println(_tp.format());
        assertEquals("TABLE(MS1) AS T1", _tp.format(), "TABLE not recognized!");
    }

    @Test
    public void testOnly() {
        _tp.parse("ONLY(ms1) T1");
        System.out.println(_tp.format());
        assertEquals("ONLY(MS1) AS T1", _tp.format(), "ONLY not recognized!");
    }

}
