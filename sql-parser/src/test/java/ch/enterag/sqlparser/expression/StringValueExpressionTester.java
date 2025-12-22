package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class StringValueExpressionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private StringValueExpression _sve = null;

  @Before
  public void setUp()
  {
    _sve = _sf.newStringValueExpression();
  }

  @Test
  public void testLiteral()
  {
    // ErrorListener.getInstance().suppressException();
    _sve.parse("'a ''character'' string with ''quotes'''");
    // System.out.println(_sve.format());
    assertEquals("Literal string with quotes not recognized!","'a ''character'' string with ''quotes'''",_sve.format());
  }

  @Test
  public void testConcatenation()
  {
    // ErrorListener.getInstance().suppressException();
    _sve.parse("tab.col || 'a ''character'' string with ''quotes'''");
    // System.out.println(_sve.format());
    assertEquals("Concatenation not recognized!","TAB.COL || 'a ''character'' string with ''quotes'''",_sve.format());
  }

  @Test
  public void testFunction()
  {
    // ErrorListener.getInstance().suppressException();
    _sve.parse("SUBSTRING(tab.\"column\" FROM 1 FOR 12)");
    // System.out.println(_sve.format());
    assertEquals("Function not recognized!","SUBSTRING(TAB.\"column\" FROM 1 FOR 12)",_sve.format());
  }

}
