package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class CreateSchemaStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CreateSchemaStatement _css = null;

  @Before
  public void setUp()
  {
    _css = _sf.newCreateSchemaStatement();
  }

  @Test
  public void testSimple()
  {
    _css.parse("CREATE SCHEMA cat.\"schema\"");
    // System.out.println(_css.format());
    assertEquals("CREATE SCHEMA statement not recognized!","CREATE SCHEMA CAT.\"schema\"",_css.format());
  }

  @Test
  public void testComplex()
  {
    _css.parse("CREATE SCHEMA \"schema\" AUTHORIZATION me");
    // System.out.println(_css.format());
    assertEquals("CREATE SCHEMA statement not recognized!","CREATE SCHEMA \"schema\" AUTHORIZATION ME",_css.format());
  }

}
