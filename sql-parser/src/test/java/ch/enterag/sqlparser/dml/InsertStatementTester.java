package ch.enterag.sqlparser.dml;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class InsertStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private InsertStatement _is = null;

  @Before
  public void setUp()
  {
    _is = _sf.newInsertStatement();
  }
  
  @Test
  public void testDefault()
  {
    _is.parse("INSERT INTO sch.tab DEFAULT VALUES");
    // System.out.println(_is.format());
    String sExpected = "INSERT INTO SCH.TAB DEFAULT VALUES"; 
    assertEquals("INSERT statement not recognized!",sExpected,_is.format());
  }

  @Test
  public void testConstructor()
  {
    _is.parse("INSERT INTO sch.tab(id,s,dat) overriding user value VALUES(5, 'aa', DATE'2016-05-09')");
    // System.out.println(_is.format());
    assertEquals("Invalid number of row value constructors: ",1,_is.getValues().size());
    assertEquals("Invalid number of update sources: ",3,_is.getValues().get(0).getUpdateSources().size());
    String sExpected = "INSERT INTO SCH.TAB(ID, S, DAT) OVERRIDING USER VALUE VALUES\r\n  (5, 'aa', DATE'2016-05-09')"; 
    assertEquals("INSERT statement not recognized!",sExpected,_is.format());
  }
  
  @Test
  public void testConstructor2()
  {
    String sSql = "INSERT INTO TESTSQLSCHEMA.TSQLSIMPLE(CCHAR_5, CVARCHAR_255, CCLOB_2M, CNCHAR_5, CNVARCHAR_127, CNCLOB_1M, CXML, CBINARY_5, CVARBINARY_255, CBLOB, CNUMERIC_31, CDECIMAL_15_5, CSMALLINT, CINTEGER, CBIGINT, CFLOAT_10, CREAL, CDOUBLE, CBOOLEAN, CDATE, CTIME, CTIMESTAMP) VALUES(\r\n" +
                  "'Abcd',\r\n"+ 
                  "' !\"#$%&''()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ !\"#$%&''()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ !\"#',\r\n"+
                  "?,\r\n"+ 
                  "'Niño',\r\n"+ 
                  "' !\"#$%&''()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ ¡¢£',\r\n"+ 
                  "?,\r\n"+ 
                  "'<a>foöäpwkfèégopàèwerkgvoperkv &lt; and &amp; ifjeifj</a>',"+ 
                  "X'01FE03FC',\r\n"+ 
                  "X'000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F808182838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9FA0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBFC0C1C2C3',\r\n"+ 
                  "?,\r\n"+ 
                  "1234567890123456789,\r\n"+ 
                  "31415926.53210,\r\n"+ 
                  "-32000,\r\n"+ 
                  "1234567890,\r\n"+ 
                  "-123456789012345678,\r\n"+ 
                  "2.718281745910644E0,\r\n"+ 
                  "3.141592741012573E0,\r\n"+ 
                  "2.718281828459045E0,\r\n"+ 
                  "TRUE,\r\n"+ 
                  "DATE'2016-12-28',\r\n"+ 
                  "TIME'13:45:28',\r\n"+ 
                  "TIMESTAMP'2016-12-28 13:45:28.123456789')";
    _is.parse(sSql);
    assertEquals("Invalid number of row value constructors: ",1,_is.getValues().size());
    assertEquals("Invalid number of update sources: ",22,_is.getValues().get(0).getUpdateSources().size());
    System.out.println(_is.format());
    String sExpected = "INSERT INTO TESTSQLSCHEMA.TSQLSIMPLE(CCHAR_5, CVARCHAR_255, CCLOB_2M, CNCHAR_5, CNVARCHAR_127, CNCLOB_1M, CXML, CBINARY_5, CVARBINARY_255, CBLOB, CNUMERIC_31, CDECIMAL_15_5, CSMALLINT, CINTEGER, CBIGINT, CFLOAT_10, CREAL, CDOUBLE, CBOOLEAN, CDATE, CTIME, CTIMESTAMP) VALUES\r\n"+
                       "  ('Abcd', ' !\"#$%&''()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ !\"#$%&''()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ !\"#', ?, 'Niño', ' !\"#$%&''()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ ¡¢£', ?, '<a>foöäpwkfèégopàèwerkgvoperkv &lt; and &amp; ifjeifj</a>', X'01FE03FC', X'000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F808182838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9FA0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBFC0C1C2C3', ?, 1234567890123456789, 31415926.53210, -32000, 1234567890, -123456789012345678, 2.718281745910644E0, 3.141592741012573E0, 2.718281828459045E0, TRUE, DATE'2016-12-28', TIME'13:45:28', TIMESTAMP'2016-12-28 13:45:28.123456789')";
    assertEquals("INSERT statement not recognized!",sExpected,_is.format());
  }
  @Test
  public void testConstructorTypes()
  {
    String sSql = "INSERT into sch.tab(\r\n"+
                  "  cchar_5,\r\n" +
                  "  cblob,\r\n"+
                  "  cnumeric_15_5,\r\n"+
                  "  csmallint,\r\n"+
                  "  cboolean,\r\n"+
                  "  cdate,\r\n"+
                  "  cinterval\r\n"+
                  ") values (\r\n" +
                  "  'abdcd',\r\n" +
                  "  x'00112233445566778899AABBCCDDEEFF',\r\n"+
                  "  1234567890.12345,\r\n"+
                  "  -27,\r\n"+
                  "  UNKNOWN,\r\n"+
                  "  DATE'2016-05-23',\r\n"+
                  "  INTERVAL '123-2' YEAR(3) TO MONTH\r\n"+
                  ")";
    _is.parse(sSql);
    System.out.println(_is.format());
    String sExpected = "INSERT INTO SCH.TAB(CCHAR_5, CBLOB, CNUMERIC_15_5, CSMALLINT, CBOOLEAN, CDATE, CINTERVAL) VALUES\r\n  ('abdcd', X'00112233445566778899AABBCCDDEEFF', 1234567890.12345, -27, UNKNOWN, DATE'2016-05-23', INTERVAL '123-2' YEAR(3) TO MONTH)"; 
    assertEquals("INSERT statement types not recognized!",sExpected,_is.format());
  }

  @Test
  public void testInteger()
  {
    String sSql = "INSERT into sch.tab(\r\n"+
                  "  cinteger\r\n"+
                  ") values (\r\n" +
                  "  -27123456\r\n"+
                  ")";
    _is.parse(sSql);
    System.out.println(_is.format());
    String sExpected = "INSERT INTO SCH.TAB(CINTEGER) VALUES\r\n  (-27123456)"; 
    assertEquals("INSERT statement types not recognized!",sExpected,_is.format());
  }

  @Test
  public void testSubquery()
  {
    _is.parse("INSERT INTO sch.tab(id,s,dat) select * from cat1.sch1.tab1");
    System.out.println(_is.format());
    String sExpected = "INSERT INTO SCH.TAB(ID, S, DAT) SELECT *\r\nFROM CAT1.SCH1.TAB1"; 
    assertEquals("INSERT statement not recognized!",sExpected,_is.format());
  }
}
