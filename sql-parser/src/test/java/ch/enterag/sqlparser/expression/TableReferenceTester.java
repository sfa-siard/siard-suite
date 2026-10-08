package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableReferenceTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private TableReference _tr = null;

    @BeforeEach
    public void setUp() {
        _tr = _sf.newTableReference();
    }

    @Test
    public void testPlainTableName() {
        _tr.parse("T1");
        System.out.println(_tr.format());
        assertEquals("T1", _tr.format(), "Plain table name not recognized!");
    }

    @Test
    public void testCrossJoin() {
        _tr.parse("T1 AS A1 CROSS JOIN T2 A2");
        // System.out.println(_tr.format());
        assertEquals("T1 AS A1 CROSS JOIN T2 AS A2", _tr.format(), "Cross join not recognized!");
    }

    @Test
    public void testLeftJoin() {
        _tr.parse("T1 AS A1 LEFT JOIN T2 A2 ON T1.COL1 = T2.COL2");
        // System.out.println(_tr.format());
        assertEquals("T1 AS A1 LEFT OUTER JOIN T2 AS A2 ON T1.COL1 = T2.COL2", _tr.format(), "Left join not recognized!");
    }

    @Test
    public void testNaturalJoin() {
        _tr.parse("T1 AS A1 NATURAL RIGHT JOIN T2 A2");
        // System.out.println(_tr.format());
        assertEquals("T1 AS A1 NATURAL RIGHT OUTER JOIN T2 AS A2", _tr.format(), "Natural join not recognized!");
    }

    @Test
    public void testUnion() {
        _tr.parse("(SELECT * FROM T1) AS A1 UNION T2 A2");
        // System.out.println(_tr.format());
        assertEquals("(SELECT *\r\nFROM T1) AS A1 UNION T2 AS A2", _tr.format(), "UNION not recognized!");
    }

    @Test
    public void testTableSample() {
        _tr.parse("TABLESAMPLE bernoulli (a1) REPEATABLE(3)");
        // System.out.println(_tr.format());
        assertEquals("TABLESAMPLE BERNOULLI(A1) REPEATABLE(3)", _tr.format(), "TABLESAMPLE not recognized!");
    }

}
