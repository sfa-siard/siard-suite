package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CastSpecificationTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private CastSpecification _cs = null;

    @BeforeEach
    public void setUp() {
        _cs = _sf.newCastSpecification();
    }

    @Test
    public void testCastValueExpression() {
        _cs.parse("CAST (Test as INTEGER)");
        System.out.println(_cs.format());
        assertEquals("CAST(TEST AS INT)", _cs.format(), "CAST as INTEGER not recognized!");
    }

    @Test
    public void testCastNull() {
        _cs.parse("CAST (NULL AS VARCHAR(35))");
        // System.out.println(_cs.format());
        assertEquals("CAST(NULL AS VARCHAR(35))", _cs.format(), "CAST NULL not recognized!");
    }

    @Test
    public void testCastEmptyArray() {
        _cs.parse("CAST (ARRAY[] AS FLOAT)");
        // System.out.println(_cs.format());
        assertEquals("CAST(ARRAY[] AS FLOAT)", _cs.format(), "CAST empty array not recognized!");
    }

    @Test
    public void testCastEmptyMultiset() {
        _cs.parse("CAST (MULTISET[] AS DOUBLE PRECISION)");
        // System.out.println(_cs.format());
        assertEquals("CAST(MULTISET[] AS DOUBLE PRECISION)", _cs.format(), "CAST empty array not recognized!");
    }

}
