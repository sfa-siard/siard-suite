package ch.enterag.sqlparser.datatype;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.enums.*;

public class PredefinedTypeTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private PredefinedType _pdt = null;

  @Before
  public void setUp()
  {
    _pdt = _sf.newPredefinedType();
  }
  
  @Test
  public void testFormat()
  {
    _pdt.initialize(PreType.CHAR, 5, null, -1, -1, -1, null, null);
    assertEquals("Format from fields failed!","CHAR(5)",_pdt.format());
  }
  
  @Test
  public void testParseComment()
  {
    _pdt.parse("CHARACTER /* some coment */(54)");
    // pdt.listTokens();
    assertEquals("CHARACTER type not recognized!","CHAR(54)",_pdt.format());
  }

  @Test
  public void testParseLineComment()
  {
    _pdt.parse("CHARACTER(54) -- and some intesting stuff");
    // pdt.listTokens();
    assertEquals("CHARACTER type not recognized!","CHAR(54)",_pdt.format());
  }
  
  @Test
  public void testParseChar()
  {
    _pdt.parse("CHARacter(54)");
    assertEquals("CHARACTER type not recognized!","CHAR(54)",_pdt.format());
  }

  @Test
  public void testParseVarchar()
  {
    _pdt.parse("CHAR varying(54)");
    assertEquals("VARCHAR type not recognized!","VARCHAR(54)",_pdt.format());
  }
  
  @Test
  public void testParseClob()
  {
    _pdt.parse("CHARACTER large object(54m)");
    assertEquals("CLOB type not recognized!","CLOB(54M)",_pdt.format());
  }
  
  @Test
  public void testParseNChar()
  {
    _pdt.parse("NATional CHARacter(54)");
    assertEquals("NCHAR type not recognized!","NCHAR(54)",_pdt.format());
  }

  @Test
  public void testParseNVarchar()
  {
    _pdt.parse("national CHAR varying(54)");
    assertEquals("NVARCHAR type not recognized!","NCHAR VARYING(54)",_pdt.format());
  }
  
  @Test
  public void testParseNClob()
  {
    _pdt.parse("NATIONAL CHARACTER large object(54m)");
    assertEquals("NCLOB type not recognized!","NCLOB(54M)",_pdt.format());
  }
  
  @Test
  public void testParseBinary()
  {
    _pdt.parse("Binary(5)");
    assertEquals("BINARY type not recognized!","BINARY(5)",_pdt.format());
  }
  
  @Test
  public void testParseVarbinary()
  {
    _pdt.parse("binary varying(255)");
    assertEquals("VARBINARY type not recognized!","VARBINARY(255)",_pdt.format());
  }
  
  @Test
  public void testParseBlob()
  {
    _pdt.parse("BLOB(54)");
    assertEquals("BLOB type not recognized!","BLOB(54)",_pdt.format());
  }
  
  @Test
  public void testParseNumeric()
  {
    _pdt.parse("numeric(32,4)");
    assertEquals("NUMERIC type not recognized!","NUMERIC(32, 4)",_pdt.format());
  }
  
  @Test
  public void testParseDecimal()
  {
    _pdt.parse("dec(32)");
    assertEquals("DECIMAL type not recognized!","DEC(32)",_pdt.format());
  }
  
  @Test
  public void testParseSmallInt()
  {
    _pdt.parse("smallint");
    assertEquals("SMALLINT type not recognized!","SMALLINT",_pdt.format());
  }

  @Test
  public void testParseInt()
  {
    _pdt.parse("int");
    assertEquals("INTEGER type not recognized!","INT",_pdt.format());
  }
  
  @Test
  public void testParseBigInt()
  {
    _pdt.parse("BIGINT");
    assertEquals("BIGINT type not recognized!","BIGINT",_pdt.format());
  }

  @Test
  public void testParseFloat()
  {
    _pdt.parse("float(10)");
    assertEquals("FLOAT type not recognized!","FLOAT(10)",_pdt.format());
  }
  
  @Test
  public void testParseReal()
  {
    _pdt.parse("real");
    assertEquals("REAL type not recognized!","REAL",_pdt.format());
  }
  
  @Test
  public void testParseDouble()
  {
    _pdt.parse("double precision");
    assertEquals("DOUBLE type not recognized!","DOUBLE PRECISION",_pdt.format());
  }
  
  @Test
  public void testParseBoolean()
  {
    _pdt.parse("bOOlean");
    assertEquals("BOOLEAN type not recognized!","BOOLEAN",_pdt.format());
  }
  
  @Test
  public void testParseDate()
  {
    _pdt.parse("DATE");
    assertEquals("DATE type not recognized!","DATE",_pdt.format());
  }

  @Test
  public void testParseTime()
  {
    _pdt.parse("TIME(4) WITH TIME ZONE");
    assertEquals("TIME type not recognized!","TIME(4) WITH TIME ZONE",_pdt.format());
  }
  
  @Test
  public void testParseTimestamp()
  {
    _pdt.parse("TIMESTAMP");
    assertEquals("TIMESTAMP type not recognized!","TIMESTAMP",_pdt.format());
  }

  @Test
  public void testParseIntervalYearMonth()
  {
    _pdt.parse("INTERVAL YEAR(2) /* in 21st century */ To Month");
    System.out.println(_pdt.format());
    assertEquals("INTERVAL YEAR TO MONTH type not recognized!","INTERVAL YEAR(2) TO MONTH",_pdt.format());
  }
 
  @Test
  public void testParseIntervalDayMinute()
  {
    _pdt.parse("INTERVAL DAY To MINUTE");
    assertEquals("INTERVAL DAY TO MINUTE type not recognized!","INTERVAL DAY TO MINUTE",_pdt.format());
  }
  
  @Test
  public void testParseIntervalSecond()
  {
    _pdt.parse("INTERVAL SECOND(2,5)");
    assertEquals("INTERVAL SECOND(2,5)!","INTERVAL SECOND(2, 5)",_pdt.format());
  }

  @Test
  public void testParseDatalink() {
    _pdt.parse("DatALINK");
    assertEquals("DATALINK type not recognized!", "DATALINK", _pdt.format());
  }
}
