package ch.enterag.sqlparser;

import java.text.*;
import static org.junit.Assert.*;
import org.junit.*;

public class SqlTester
{

  @Test
  public void testParseId()
  {
    /* regular */
    try { assertEquals("Regular identifiers must be upper case!","ASCHEMA",SqlLiterals.parseId("ASchema")); }
    catch (ParseException pe) { fail(pe.getClass().getName()+" ["+String.valueOf(pe.getErrorOffset())+"]: "+pe.getMessage()); }
    /* regular too long */
    try
    {
      SqlLiterals.parseId("A2345678901234567890123456789012345678901234567890"+ /* 50 */
                 "12345678901234567890123456789012345678901234567890"+ /* 100 */
                 "123456789012345678901234567890");                    /* 130 */
      fail("Regular identifiers must be at most 128 characters long!");
    }
    catch(ParseException pe) { System.out.println(pe.getClass().getName()+" ["+String.valueOf(pe.getErrorOffset())+"]: "+pe.getMessage()); }
    /* regular but keyword */
    try
    {
      SqlLiterals.parseId("DroP");
      fail("Regular identifiers must not equal reserved keywords!");
    }
    catch(ParseException pe) { System.out.println(pe.getClass().getName()+" ["+String.valueOf(pe.getErrorOffset())+"]: "+pe.getMessage()); }
    /* delimited */
    try { assertEquals("Delimited identifer must be normalized just as is!","ASchema",SqlLiterals.parseId("\"ASchema\"")); }
    catch (ParseException pe) { fail(pe.getClass().getName()+" ["+String.valueOf(pe.getErrorOffset())+"]: "+pe.getMessage()); }
    /* delimited too short */
    try
    {
      SqlLiterals.parseId("\"\"");
      fail("Delimited identifiers must have length > 0!");
    }
    catch(ParseException pe) { System.out.println(pe.getClass().getName()+" ["+String.valueOf(pe.getErrorOffset())+"]: "+pe.getMessage()); }
    /* delimited with doubled quotes */
    try { assertEquals("Double quotes must be resolved!","a\"quote",SqlLiterals.parseId("\"a\"\"quote\"")); }
    catch (ParseException pe) { fail(pe.getClass().getName()+" ["+String.valueOf(pe.getErrorOffset())+"]: "+pe.getMessage()); }
    /* delimited with single quote */
    try
    {
      SqlLiterals.parseId("\"a\"quote\"");
      fail("Single quotes must not be accepted!");
    }
    catch(ParseException pe) { System.out.println(pe.getClass().getName()+" ["+String.valueOf(pe.getErrorOffset())+"]: "+pe.getMessage()); }
    /* delimited key word */
    try { assertEquals("Keywords must be delimited","DROP",SqlLiterals.parseId("\"DROP\"")); }
    catch (ParseException pe) { fail(pe.getClass().getName()+" ["+String.valueOf(pe.getErrorOffset())+"]: "+pe.getMessage()); }
  } /* testParseId */
  
  @Test
  public void testFormatId()
  {
    assertEquals("Plain uppercase identifiers must not be changed!","ASCHEMA",SqlLiterals.formatId("ASCHEMA"));
    assertEquals("Umlauts are letters too!!","ÄSCHEMA",SqlLiterals.formatId("ÄSCHEMA"));
    assertEquals("Lowercase stuff must be quoted!","\"ASchema\"",SqlLiterals.formatId("ASchema"));
    assertEquals("Special characters must also be quoted","\"ASCH#MA\"",SqlLiterals.formatId("ASCH#MA"));
    assertEquals("Underscore counts as alphabetic","ASCH_MA",SqlLiterals.formatId("ASCH_MA"));
    assertEquals("Quotes must be doubled in quotes!","\"ASCH\"\"EMA\"",SqlLiterals.formatId("ASCH\"EMA"));
    try
    {
      SqlLiterals.formatId("A2345678901234567890123456789012345678901234567890"+ /* 50 */
                       "12345678901234567890123456789012345678901234567890"+ /* 100 */
                       "123456789012345678901234567890");                    /* 130 */
      fail("Identifiers must be at most 128 characters long!");
    }
    catch(IllegalArgumentException iae) { System.out.println(iae.getClass().getName()+": "+iae.getMessage()); }
  }

}
