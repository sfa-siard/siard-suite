package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class TableConstraintDefinitionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private TableConstraintDefinition _tcd = null;

  @Before
  public void setUp()
  {
    _tcd = _sf.newTableConstraintDefinition();
  }
  
  @Test
  public void testUnique()
  {
    _tcd.parse("UNIQUE(COL1, \"col2\")");
    // System.out.println(_tcd.format());
    assertEquals("UNIQUE constraint not recognized!","UNIQUE(COL1, \"col2\")",_tcd.format());
  }

  @Test
  public void testDeferrability()
  {
    _tcd.parse("UNIQUE(COL1, \"col2\") NOT DEFERRABLE INITIALLY IMMEDIATE");
    // System.out.println(_tcd.format());
    assertEquals("UNIQUE constraint with deferrability not recognized!","UNIQUE(COL1, \"col2\") NOT DEFERRABLE INITIALLY IMMEDIATE",_tcd.format());
  }
  
  @Test
  public void testPrimaryKey()
  {
    _tcd.parse("PRIMARY KEY(COL1, \"col2\",COL3)");
    // System.out.println(_tcd.format());
    assertEquals("PRIMARY KEY constraint not recognized!","PRIMARY KEY(COL1, \"col2\", COL3)",_tcd.format());
  }

  @Test
  public void testForeignKey()
  {
    _tcd.parse("FOREIGN KEY(COL1, \"col2\",COL3) REFERENCES schem.\"Table\"(rcol1, \"rcol2\",RCOL3)");
    // System.out.println(_tcd.format());
    assertEquals("FOREIGN KEY constraint not recognized!","FOREIGN KEY(COL1, \"col2\", COL3) REFERENCES SCHEM.\"Table\"(RCOL1, \"rcol2\", RCOL3)",_tcd.format());
  }

  @Test
  public void testForeignKeyWithOptions()
  {
    _tcd.parse("FOREIGN KEY(COL1,Col2) REFERENCES Tab(rcol1,RCOL2) MATCH FULL ON UPDATE SET DEFAULT");
    // System.out.println(_tcd.format());
    assertEquals("FOREIGN KEY constraint with options not recognized!","FOREIGN KEY(COL1, COL2) REFERENCES TAB(RCOL1, RCOL2) MATCH FULL ON UPDATE SET DEFAULT",_tcd.format());
  }
  
  @Test
  public void testCheck()
  {
    _tcd.parse("CHECK(COL1 = \"col2\")");
    // System.out.println(_tcd.format());
    assertEquals("CHECK constraint not recognized!","CHECK(COL1 = \"col2\")",_tcd.format());
  }

}
