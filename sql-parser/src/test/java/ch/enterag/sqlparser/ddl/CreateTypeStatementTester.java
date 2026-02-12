package ch.enterag.sqlparser.ddl;

import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.sqlparser.*;

public class CreateTypeStatementTester
{
  private SqlFactory _sf = new BaseSqlFactory();
  private CreateTypeStatement _cts = null;

  @Before
  public void setUp()
  {
    _cts = _sf.newCreateTypeStatement();
  }
  
  @Test
  public void testDistinct()
  {
    _cts.parse("create type shoe_size as integer final");
    // System.out.println(_cts.format());
    assertEquals("Distinct type statement not recognized!","CREATE TYPE SHOE_SIZE AS INT FINAL",_cts.format());
  }
  
  @Test
  public void testAttributes()
  {
    String sStatement = "create type address as (\r\n" +
        "  number character(6),\r\n" +
        "  street ROW (\r\n" +
        "    street_name character varying(35),\r\n" +
        "    street_type character varying(10) ),\r\n" +
        "  city character varying(35),\r\n" +
        "  state character(2),\r\n" +
        "  zip_code ROW(\r\n" +
        "    base character(5),\r\n" +
        "    plus4 character(4) ) ) \r\n" +
        "  not final";
_cts.parse(sStatement);
// System.out.println(_cts.format());
    String sExpected = "CREATE TYPE ADDRESS AS(\r\n" +
        "  NUMBER CHAR(6),\r\n" +
        "  STREET ROW(STREET_NAME VARCHAR(35), STREET_TYPE VARCHAR(10)),\r\n" +
        "  CITY VARCHAR(35),\r\n" +
        "  STATE CHAR(2),\r\n" +
        "  ZIP_CODE ROW(BASE CHAR(5), PLUS4 CHAR(4))\r\n" +
        ") NOT FINAL";
    assertEquals("Attributes definition not recognized!",sExpected,_cts.format());
    
  }
  
  @Test
  public void testDefault()
  {
    String sStatement = "create type address as (\r\n" +
        "  \"number\" character(6) default 'Street',\r\n" +
        "  street ROW (\r\n" +
        "    street_name character varying(35),\r\n" +
        "    street_type character varying(10)),\r\n" +
        "  city character varying(35),\r\n" +
        "  \"state\" character(2),\r\n" +
        "  zip_code ROW(\r\n" +
        "    base character(5),\r\n" +
        "    plus4 character(4) ) ) \r\n" +
        "  not final";
    _cts.parse(sStatement);
    // System.out.println(_cts.format());
    String sExpected = "CREATE TYPE ADDRESS AS(\r\n" +
        "  \"number\" CHAR(6) DEFAULT 'Street',\r\n" +
        "  STREET ROW(STREET_NAME VARCHAR(35), STREET_TYPE VARCHAR(10)),\r\n" +
        "  CITY VARCHAR(35),\r\n" +
        "  \"state\" CHAR(2),\r\n" +
        "  ZIP_CODE ROW(BASE CHAR(5), PLUS4 CHAR(4))\r\n" +
        ") NOT FINAL";
    assertEquals("Default not recognized!",sExpected,_cts.format());
  }
  
  @Test
  public void testSimpleMethod()
  {
    String sStatement = "create type movie as (\r\n" +
                        "  title character varying(100),\r\n" +
                        "  description character varying(500),\r\n" +
                        "  runs integer )\r\n" +
                        "  NOT FINAL\r\n" +
                        "METHOD length_interval ( )\r\n" +
                        "  RETURNS INTERVAL HOUR(2) TO MINUTE";
    _cts.parse(sStatement);
    // System.out.println(_cts.format());
    String sExpected =
        "CREATE TYPE MOVIE AS(\r\n" +
        "  TITLE VARCHAR(100),\r\n" +
        "  DESCRIPTION VARCHAR(500),\r\n" +
        "  RUNS INT\r\n" +
        ") NOT FINAL\r\n" +
        "METHOD LENGTH_INTERVAL() RETURNS INTERVAL HOUR(2) TO MINUTE";
    assertEquals("Method specification not recognized!",sExpected,_cts.format());
  }
  @Test
  public void testTwoMethods()
  {
    String sStatement = "CREATE TYPE point AS (\r\n" +
                        "  rho REAL,\r\n" +
                        " theta REAL)\r\n" +
                        "NOT FINAL\r\n" +
                        "METHOD x_coord ( )\r\n" +
                        "  RETURNS REAL,\r\n" +
                        "METHOD y_coord ( )\r\n" +
                        "  RETURNS REAL";
    _cts.parse(sStatement);
    // System.out.println(_cts.format());
    String sExpected = "CREATE TYPE POINT AS(\r\n" +
                       "  RHO REAL,\r\n" +
                       "  THETA REAL\r\n" +
                       ") NOT FINAL\r\n" +
                       "METHOD X_COORD() RETURNS REAL,\r\n" +
                       "METHOD Y_COORD() RETURNS REAL";
    assertEquals("Method specifications not recognized!",sExpected,_cts.format());
  }
  @Test
  public void testSubType()
  {
    String sStatement = "CREATE TYPE dvd UNDER movie AS (\r\n" +
                        "  stock_number INTEGER,\r\n" +
                        "  rental_price DECIMAL(5,2),\r\n" +
                        "  extra_features feature_desc ARRAY[10] )\r\n" +
                        "  INSTANTIABLE\r\n" +
                        "  NOT FINAL";
    _cts.parse(sStatement);
    // System.out.println(_cts.format());
    String sExpected = "CREATE TYPE DVD UNDER MOVIE AS(\r\n"+
                       "  STOCK_NUMBER INT,\r\n"+
                       "  RENTAL_PRICE DEC(5, 2),\r\n"+
                       "  EXTRA_FEATURES FEATURE_DESC ARRAY[10]\r\n"+
                       ") INSTANTIABLE NOT FINAL";
    assertEquals("Subtype specifications not recognized!",sExpected,_cts.format());
  }
  @Test
  public void testTable()
  {
    String sStatement = "CREATE TYPE movie AS (\r\n" +
        "  title CHARACTER VARYING(100),\r\n" +
        "  description CHARACTER VARYING(500),\r\n" +
        "  runs INTEGER )\r\n" +
        "  INSTANTIABLE\r\n" +
        "  NOT FINAL\r\n" +
        "  REF IS SYSTEM GENERATED";
    _cts.parse(sStatement);
    // System.out.println(_cts.format());
    String sExpected = "CREATE TYPE MOVIE AS(\r\n"+
                       "  TITLE VARCHAR(100),\r\n"+
                       "  DESCRIPTION VARCHAR(500),\r\n"+
                       "  RUNS INT\r\n"+
                       ") INSTANTIABLE NOT FINAL REF IS SYSTEM GENERATED";
    assertEquals("Table-valued specifications not recognized!",sExpected,_cts.format());
  }
}
