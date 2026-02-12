package ch.enterag.sqlparser.datatype;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class FieldDefinitionTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private FieldDefinition _fd = null;

  @Before
  public void setUp()
  {
    _fd = _sf.newFieldDefinition();
  }


  @Test
  public void testFieldDefinition()
  {
    _fd.parse("\"SomeField\" INTEGER REFERENCES ARE CHECKED");
    assertEquals("Field definition not recognized!","\"SomeField\" INT REFERENCES ARE CHECKED",_fd.format());
  }

}
