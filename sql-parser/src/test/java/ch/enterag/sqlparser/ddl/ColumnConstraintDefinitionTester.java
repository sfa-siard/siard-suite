package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class ColumnConstraintDefinitionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private ColumnConstraintDefinition _ccd = null;

  @Before
  public void setUp()
  {
    _ccd = _sf.newColumnConstraintDefinition();
  }

  @Test
  public void testNotNull()
  {
    _ccd.parse("constraint nnc not null");
    // System.out.println(_ccd.format());
    assertEquals("NOT NULL constraint not recognized!","CONSTRAINT NNC NOT NULL",_ccd.format());
  }
  
  @Test
  public void testDeferred()
  {
    _ccd.parse("constraint nnc not null initially deferred");
    // System.out.println(_ccd.format());
    assertEquals("Deferred NOT NULL constraint not recognized!","CONSTRAINT NNC NOT NULL INITIALLY DEFERRED",_ccd.format());
    
  }
  
  @Test
  public void testUnique()
  {
    _ccd.parse("UNIQUE VALUE");
    // System.out.println(_ccd.format());
    assertEquals("UNIQUE constraint not recognized!","UNIQUE",_ccd.format());
  }
  
  @Test
  public void testPrimaryKey()
  {
    _ccd.parse("CONSTRAINT \"Schema\".\"pkC\" PRIMARY KEY");
    // System.out.println(_ccd.format());
    assertEquals("PRIMARY KEY constraint not recognized!","CONSTRAINT \"Schema\".\"pkC\" PRIMARY KEY",_ccd.format());
  }

  @Test
  public void testReferences()
  {
    _ccd.parse("references cat.\"schem\".\"tabRef\"(colRef)");
    // System.out.println(_ccd.format());
    assertEquals("REFERENCES constraint not recognized!","REFERENCES CAT.\"schem\".\"tabRef\"(COLREF)",_ccd.format());
  }

  @Test
  public void testReferencesWithOptions()
  {
    _ccd.parse("references tabRef(colRef) match simple on update set default on delete no action");
    // System.out.println(_ccd.format());
    assertEquals("REFERENCES constraint with options not recognized!","REFERENCES TABREF(COLREF) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE SET DEFAULT",_ccd.format());
  }

  @Test
  public void testCheck()
  {
    _ccd.parse("check(col1='aa' and col2 = col3)");
    // System.out.println(_ccd.format());
    assertEquals("CHECK constraint not recognized!","CHECK(COL1 = 'aa' AND COL2 = COL3)",_ccd.format());
  }
}
