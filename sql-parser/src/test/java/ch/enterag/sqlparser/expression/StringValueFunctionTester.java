package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class StringValueFunctionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private StringValueFunction _svf = null;

  @Before
  public void setUp()
  {
    _svf = _sf.newStringValueFunction();
  }

  @Test
  public void testSubstring()
  {
    // ErrorListener.getInstance().suppressException();
    _svf.parse("SUBSTRING(tab.\"column\" FROM 1 FOR 12)");
    // System.out.println(_svf.format());
    assertEquals("SUBSTRING not recognized!","SUBSTRING(TAB.\"column\" FROM 1 FOR 12)",_svf.format());
  }

  @Test
  public void testSubstringSimilar()
  {
    // ErrorListener.getInstance().suppressException();
    _svf.parse("SUBSTRING(tab.\"column\" SIMILAR 'this%' ESCAPE '\\')");
    // System.out.println(_svf.format());
    assertEquals("SUBSTRING SIMILAR not recognized!","SUBSTRING(TAB.\"column\" SIMILAR 'this%' ESCAPE '\\')",_svf.format());
  }

  @Test
  public void testUpper()
  {
    // ErrorListener.getInstance().suppressException();
    _svf.parse("UPPER(tab.\"column\")");
    // System.out.println(_svf.format());
    assertEquals("UPPER not recognized!","UPPER(TAB.\"column\")",_svf.format());
  }

  @Test
  public void testLower()
  {
    // ErrorListener.getInstance().suppressException();
    _svf.parse("lower('ABCdefGH')");
    // System.out.println(_svf.format());
    assertEquals("LOWER not recognized!","LOWER('ABCdefGH')",_svf.format());
  }

  @Test
  public void testTrim()
  {
    // ErrorListener.getInstance().suppressException();
    _svf.parse("trim('ABCdefGH ')");
    // System.out.println(_svf.format());
    assertEquals("TRIM not recognized!","TRIM('ABCdefGH ')",_svf.format());
  }

  @Test
  public void testNormalize()
  {
    // ErrorListener.getInstance().suppressException();
    _svf.parse("NORMALIZE(tab.\"column\")");
    // System.out.println(_svf.format());
    assertEquals("NORMALIZE not recognized!","NORMALIZE(TAB.\"column\")",_svf.format());
  }

  @Test
  public void testSpecificType()
  {
    // ErrorListener.getInstance().suppressException();
    _svf.parse("schem.\"typ\".specifictype");
    // System.out.println(_svf.format());
    assertEquals("SPECIFICTYPE not recognized!","SCHEM.\"typ\".SPECIFICTYPE",_svf.format());
  }

}
