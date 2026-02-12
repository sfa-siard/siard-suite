package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class CastSpecificationTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CastSpecification _cs = null;

  @Before
  public void setUp()
  {
    _cs = _sf.newCastSpecification();
  }
  
  @Test
  public void testCastValueExpression()
  {
    _cs.parse("CAST (Test as INTEGER)");
    System.out.println(_cs.format());
    assertEquals("CAST as INTEGER not recognized!","CAST(TEST AS INT)",_cs.format());
  }

  @Test
  public void testCastNull()
  {
    _cs.parse("CAST (NULL AS VARCHAR(35))");
    // System.out.println(_cs.format());
    assertEquals("CAST NULL not recognized!","CAST(NULL AS VARCHAR(35))",_cs.format());
  }

  @Test
  public void testCastEmptyArray()
  {
    _cs.parse("CAST (ARRAY[] AS FLOAT)");
    // System.out.println(_cs.format());
    assertEquals("CAST empty array not recognized!","CAST(ARRAY[] AS FLOAT)",_cs.format());
  }

  @Test
  public void testCastEmptyMultiset()
  {
    _cs.parse("CAST (MULTISET[] AS DOUBLE PRECISION)");
    // System.out.println(_cs.format());
    assertEquals("CAST empty array not recognized!","CAST(MULTISET[] AS DOUBLE PRECISION)",_cs.format());
  }

}
