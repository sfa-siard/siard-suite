package ch.enterag.sqlparser.datatype;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class DataTypeTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private DataType _dt = null;

  @Before
  public void setUp()
  {
    _dt = _sf.newDataType();
  }

  @Test
  public void testPre()
  {
    _dt.parse("CHARACTER large object(54m)");
    assertEquals("CLOB type not recognized!","CLOB(54M)",_dt.format());
  }
  @Test
  public void testStruct()
  {
    _dt.parse("\"SomeType\"");
    assertEquals("UDT type not recognized!","\"SomeType\"",_dt.format());
  }
  @Test
  public void testRow()
  {
    _dt.parse("ROW (field1 INT, field2 DOUBLE PRECISION, \"field3\" char(4))");
    assertEquals("ROW type not recognized!","ROW(FIELD1 INT, FIELD2 DOUBLE PRECISION, \"field3\" CHAR(4))",_dt.format());
  }
  @Test
  public void testRef()
  {
    _dt.parse("REF (\"SomeType\") SCOPE some_table");
    assertEquals("REF type not recognized!","REF(\"SomeType\") SCOPE SOME_TABLE",_dt.format());
  }
  @Test
  public void testArray()
  {
    _dt.parse("INT ARRAY[5]");
    assertEquals("ARRAY type not recognized!","INT ARRAY[5]",_dt.format());
  }
  @Test
  public void testMultiset()
  {
    _dt.parse("CHAR(5) MULTISET");
    assertEquals("MULTISET type not recognized!","CHAR(5) MULTISET",_dt.format());
  }
  @Test
  public void testComplex1()
  {
    _dt.parse("ROW (field1 INT, field2 DOUBLE PRECISION, \"field3\" char(4)) ARRAY[5]");
    assertEquals("Complex ARRAY type not recognized!","ROW(FIELD1 INT, FIELD2 DOUBLE PRECISION, \"field3\" CHAR(4)) ARRAY[5]",_dt.format());
  }

}
