package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class AttributeDefinitionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private AttributeDefinition _ad = null;

  @Before
  public void setUp()
  {
    _ad = _sf.newAttributeDefinition();
  }

  @Test
  public void testSimple()
  {
    _ad.parse("id INT");
    // System.out.println(_ad.format());
    assertEquals("Simple attribute definition not recognized!","ID INT",_ad.format());
  }
  @Test
  public void testComplex()
  {
    _ad.parse("complex_column ROW (field1 INT, field2 DOUBLE PRECISION, \"field3\" char(4)) ARRAY[5]");
    // System.out.println(cd.format());
    assertEquals("Complex attribute definition not recognized!","COMPLEX_COLUMN ROW(FIELD1 INT, FIELD2 DOUBLE PRECISION, \"field3\" CHAR(4)) ARRAY[5]",_ad.format());
  }
  @Test
  public void testDefault()
  {
    _ad.parse("a_col varchar(255) default 'def'");
    // System.out.println(cd.format());
    assertEquals("DEFAULT attribute definition not recognized!","A_COL VARCHAR(255) DEFAULT 'def'",_ad.format());
  }
  @Test
  public void testDefault2()
  {
    _ad.parse("a_col varchar(255) default current_user");
    // cd.listTokens();
    // System.out.println(cd.format());
    assertEquals("DEFAULT attribute definition not recognized!","A_COL VARCHAR(255) DEFAULT CURRENT_USER",_ad.format());
  }
  @Test
  public void testDefault3()
  {
    try
    {
      _ad.parse("a_col varchar(255) default gaga");
      System.out.println(_ad.format());
      fail("Invalid default GAGA accepted!");
    }
    catch(IllegalArgumentException iae) { System.err.println(iae.getClass().getName()+": "+iae.getMessage()); }
  }
  @Test
  public void testDeleteAction()
  {
    _ad.parse("a_col varchar(255) references are checked on delete set default default 'def'");
    // System.out.println(_ad.format());
    assertEquals("Attribute definition with delete action not recognized!","A_COL VARCHAR(255) REFERENCES ARE CHECKED SET DEFAULT DEFAULT 'def'",_ad.format());
  }

}
