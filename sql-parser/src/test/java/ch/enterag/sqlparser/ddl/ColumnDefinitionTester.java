package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class ColumnDefinitionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private ColumnDefinition _cd = null;

  @Before
  public void setUp()
  {
    _cd = _sf.newColumnDefinition();
  }

  @Test
  public void testSimple()
  {
    _cd.parse("id INT");
    // System.out.println(_cd.format());
    assertEquals("Simple column definition not recognized!","ID INT",_cd.format());
  }
  @Test
  public void testComplex()
  {
    _cd.parse("complex_column ROW (field1 INT, field2 DOUBLE PRECISION, \"field3\" char(4)) ARRAY[5]");
    // System.out.println(_cd.format());
    assertEquals("DEFAULT column definition not recognized!","COMPLEX_COLUMN ROW(FIELD1 INT, FIELD2 DOUBLE PRECISION, \"field3\" CHAR(4)) ARRAY[5]",_cd.format());
  }
  @Test
  public void testDefault()
  {
    _cd.parse("a_col varchar(255) default 'def'");
    // System.out.println(_cd.format());
    assertEquals("DEFAULT column definition not recognized!","A_COL VARCHAR(255) DEFAULT 'def'",_cd.format());
  }
  @Test
  public void testDefault2()
  {
    _cd.parse("a_col varchar(255) default current_user");
    // cd.listTokens();
    // System.out.println(_cd.format());
    assertEquals("DEFAULT column definition not recognized!","A_COL VARCHAR(255) DEFAULT CURRENT_USER",_cd.format());
  }
  @Test
  public void testDefault3()
  {
    try
    {
      _cd.parse("a_col varchar(255) default gaga");
      System.out.println(_cd.format());
      fail("Invalid default GAGA accepted!");
    }
    catch(IllegalArgumentException iae) { System.err.println(iae.getClass().getName()+": "+iae.getMessage()); }
  }
  @Test
  public void testGenerated()
  {
    _cd.parse("a_col varchar(255) default 'def' generated always as ('aa' || col1) constraint \"const01\" not null");
    System.out.println(_cd.format());
    assertEquals("GENERATED ALWAYS AS column definition not recognized!","A_COL VARCHAR(255) DEFAULT 'def' GENERATED ALWAYS AS('aa' || COL1) CONSTRAINT \"const01\" NOT NULL",_cd.format());
  }
  @Test
  public void testNotNull()
  {
    _cd.parse("a_col varchar(255) default 'def' not null");
    // System.out.println(_cd.format());
    assertEquals("Not null column definition not recognized!","A_COL VARCHAR(255) DEFAULT 'def' NOT NULL",_cd.format());
  }
  
}
