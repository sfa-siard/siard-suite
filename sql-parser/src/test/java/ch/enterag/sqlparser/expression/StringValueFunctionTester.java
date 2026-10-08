package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringValueFunctionTester {
    private SqlFactory _sf = new BaseSqlFactory();
    private StringValueFunction _svf = null;

    @BeforeEach
    public void setUp() {
        _svf = _sf.newStringValueFunction();
    }

    @Test
    public void testSubstring() {
        // ErrorListener.getInstance().suppressException();
        _svf.parse("SUBSTRING(tab.\"column\" FROM 1 FOR 12)");
        // System.out.println(_svf.format());
        assertEquals("SUBSTRING(TAB.\"column\" FROM 1 FOR 12)", _svf.format(), "SUBSTRING not recognized!");
    }

    @Test
    public void testSubstringSimilar() {
        // ErrorListener.getInstance().suppressException();
        _svf.parse("SUBSTRING(tab.\"column\" SIMILAR 'this%' ESCAPE '\\')");
        // System.out.println(_svf.format());
        assertEquals("SUBSTRING(TAB.\"column\" SIMILAR 'this%' ESCAPE '\\')", _svf.format(), "SUBSTRING SIMILAR not recognized!");
    }

    @Test
    public void testUpper() {
        // ErrorListener.getInstance().suppressException();
        _svf.parse("UPPER(tab.\"column\")");
        // System.out.println(_svf.format());
        assertEquals("UPPER(TAB.\"column\")", _svf.format(), "UPPER not recognized!");
    }

    @Test
    public void testLower() {
        // ErrorListener.getInstance().suppressException();
        _svf.parse("lower('ABCdefGH')");
        // System.out.println(_svf.format());
        assertEquals("LOWER('ABCdefGH')", _svf.format(), "LOWER not recognized!");
    }

    @Test
    public void testTrim() {
        // ErrorListener.getInstance().suppressException();
        _svf.parse("trim('ABCdefGH ')");
        // System.out.println(_svf.format());
        assertEquals("TRIM('ABCdefGH ')", _svf.format(), "TRIM not recognized!");
    }

    @Test
    public void testNormalize() {
        // ErrorListener.getInstance().suppressException();
        _svf.parse("NORMALIZE(tab.\"column\")");
        // System.out.println(_svf.format());
        assertEquals("NORMALIZE(TAB.\"column\")", _svf.format(), "NORMALIZE not recognized!");
    }

    @Test
    public void testSpecificType() {
        // ErrorListener.getInstance().suppressException();
        _svf.parse("schem.\"typ\".specifictype");
        // System.out.println(_svf.format());
        assertEquals("SCHEM.\"typ\".SPECIFICTYPE", _svf.format(), "SPECIFICTYPE not recognized!");
    }

}
