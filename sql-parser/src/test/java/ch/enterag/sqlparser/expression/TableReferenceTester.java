package ch.enterag.sqlparser.expression;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class TableReferenceTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private TableReference _tr = null;

  @Before
  public void setUp()
  {
    _tr = _sf.newTableReference();
  }

  @Test
  public void testPlainTableName()
  {
    _tr.parse("T1");
    System.out.println(_tr.format());
    assertEquals("Plain table name not recognized!","T1",_tr.format());
  }

  @Test
  public void testCrossJoin()
  {
    _tr.parse("T1 AS A1 CROSS JOIN T2 A2");
    // System.out.println(_tr.format());
    assertEquals("Cross join not recognized!","T1 AS A1 CROSS JOIN T2 AS A2",_tr.format());
  }

  @Test
  public void testLeftJoin()
  {
    _tr.parse("T1 AS A1 LEFT JOIN T2 A2 ON T1.COL1 = T2.COL2");
    // System.out.println(_tr.format());
    assertEquals("Left join not recognized!","T1 AS A1 LEFT OUTER JOIN T2 AS A2 ON T1.COL1 = T2.COL2",_tr.format());
  }

  @Test
  public void testNaturalJoin()
  {
    _tr.parse("T1 AS A1 NATURAL RIGHT JOIN T2 A2");
    // System.out.println(_tr.format());
    assertEquals("Natural join not recognized!","T1 AS A1 NATURAL RIGHT OUTER JOIN T2 AS A2",_tr.format());
  }

  @Test
  public void testUnion()
  {
    _tr.parse("(SELECT * FROM T1) AS A1 UNION T2 A2");
    // System.out.println(_tr.format());
    assertEquals("UNION not recognized!","(SELECT *\r\nFROM T1) AS A1 UNION T2 AS A2",_tr.format());
  }

  @Test
  public void testTableSample()
  {
    _tr.parse("TABLESAMPLE bernoulli (a1) REPEATABLE(3)");
    // System.out.println(_tr.format());
    assertEquals("TABLESAMPLE not recognized!","TABLESAMPLE BERNOULLI(A1) REPEATABLE(3)",_tr.format());
  }

}
