package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringValueExpressionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private StringValueExpression _sve = null;

    @BeforeEach
    public void setUp() {
        _sve = _sf.newStringValueExpression();
    }

    @Test
    public void testLiteral() {
        // ErrorListener.getInstance().suppressException();
        _sve.parse("'a ''character'' string with ''quotes'''");
        // System.out.println(_sve.format());
        assertEquals("'a ''character'' string with ''quotes'''", _sve.format(), "Literal string with quotes not recognized!");
    }

    @Test
    public void testConcatenation() {
        // ErrorListener.getInstance().suppressException();
        _sve.parse("tab.col || 'a ''character'' string with ''quotes'''");
        // System.out.println(_sve.format());
        assertEquals("TAB.COL || 'a ''character'' string with ''quotes'''", _sve.format(), "Concatenation not recognized!");
    }

    @Test
    public void testFunction() {
        // ErrorListener.getInstance().suppressException();
        _sve.parse("SUBSTRING(tab.\"column\" FROM 1 FOR 12)");
        // System.out.println(_sve.format());
        assertEquals("SUBSTRING(TAB.\"column\" FROM 1 FOR 12)", _sve.format(), "Function not recognized!");
    }

}
