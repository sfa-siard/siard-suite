package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DropFunctionStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DropFunctionStatement _dfs = null;

  @Before
  public void setUp()
  {
    _dfs = _sf.newDropFunctionStatement();
  }
  
  @Test
  public void testSimple()
  {
    _dfs.parse("DROP specific Function cat1.sch1.countfunc cascade");
    // System.out.println(_dfs.format());
    assertEquals("DROP FUNCTION statement not recognized!","DROP SPECIFIC FUNCTION CAT1.SCH1.COUNTFUNC CASCADE",_dfs.format());
  }

  @Test
  public void testComplex()
  {
    _dfs.parse("DROP Function cat1.sch1.countfunc2(in idDel integer) restrict");
    // System.out.println(_dfs.format());
    assertEquals("DROP FUNCTION statement not recognized!","DROP FUNCTION CAT1.SCH1.COUNTFUNC2(IN IDDEL INT) RESTRICT",_dfs.format());
  }

}
