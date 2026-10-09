package ch.enterag.sqlparser;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SqlTester {

    @Test
    public void testParseId() {
        /* regular */
        try {
            assertEquals("ASCHEMA", SqlLiterals.parseId("ASchema"), "Regular identifiers must be upper case!");
        } catch (ParseException pe) {
            fail(pe.getClass()
                   .getName() + " [" + String.valueOf(pe.getErrorOffset()) + "]: " + pe.getMessage());
        }
        /* regular too long */
        try {
            SqlLiterals.parseId("A2345678901234567890123456789012345678901234567890" + /* 50 */
                                        "12345678901234567890123456789012345678901234567890" + /* 100 */
                                        "123456789012345678901234567890");                    /* 130 */
            fail("Regular identifiers must be at most 128 characters long!");
        } catch (ParseException pe) {
            System.out.println(pe.getClass()
                                 .getName() + " [" + String.valueOf(pe.getErrorOffset()) + "]: " + pe.getMessage());
        }
        /* regular but keyword */
        try {
            SqlLiterals.parseId("DroP");
            fail("Regular identifiers must not equal reserved keywords!");
        } catch (ParseException pe) {
            System.out.println(pe.getClass()
                                 .getName() + " [" + String.valueOf(pe.getErrorOffset()) + "]: " + pe.getMessage());
        }
        /* delimited */
        try {
            assertEquals("ASchema", SqlLiterals.parseId("\"ASchema\""), "Delimited identifer must be normalized just as is!");
        } catch (ParseException pe) {
            fail(pe.getClass()
                   .getName() + " [" + String.valueOf(pe.getErrorOffset()) + "]: " + pe.getMessage());
        }
        /* delimited too short */
        try {
            SqlLiterals.parseId("\"\"");
            fail("Delimited identifiers must have length > 0!");
        } catch (ParseException pe) {
            System.out.println(pe.getClass()
                                 .getName() + " [" + String.valueOf(pe.getErrorOffset()) + "]: " + pe.getMessage());
        }
        /* delimited with doubled quotes */
        try {
            assertEquals("a\"quote", SqlLiterals.parseId("\"a\"\"quote\""), "Double quotes must be resolved!");
        } catch (ParseException pe) {
            fail(pe.getClass()
                   .getName() + " [" + String.valueOf(pe.getErrorOffset()) + "]: " + pe.getMessage());
        }
        /* delimited with single quote */
        try {
            SqlLiterals.parseId("\"a\"quote\"");
            fail("Single quotes must not be accepted!");
        } catch (ParseException pe) {
            System.out.println(pe.getClass()
                                 .getName() + " [" + String.valueOf(pe.getErrorOffset()) + "]: " + pe.getMessage());
        }
        /* delimited key word */
        try {
            assertEquals("DROP", SqlLiterals.parseId("\"DROP\""), "Keywords must be delimited");
        } catch (ParseException pe) {
            fail(pe.getClass()
                   .getName() + " [" + String.valueOf(pe.getErrorOffset()) + "]: " + pe.getMessage());
        }
    }

    @Test
    public void testFormatId() {
        assertEquals("ASCHEMA", SqlLiterals.formatId("ASCHEMA"), "Plain uppercase identifiers must not be changed!");
        assertEquals("ÄSCHEMA", SqlLiterals.formatId("ÄSCHEMA"), "Umlauts are letters too!!");
        assertEquals("\"ASchema\"", SqlLiterals.formatId("ASchema"), "Lowercase stuff must be quoted!");
        assertEquals("\"ASCH#MA\"", SqlLiterals.formatId("ASCH#MA"), "Special characters must also be quoted");
        assertEquals("ASCH_MA", SqlLiterals.formatId("ASCH_MA"), "Underscore counts as alphabetic");
        assertEquals("\"ASCH\"\"EMA\"", SqlLiterals.formatId("ASCH\"EMA"), "Quotes must be doubled in quotes!");
        try {
            SqlLiterals.formatId("A2345678901234567890123456789012345678901234567890" + /* 50 */
                                         "12345678901234567890123456789012345678901234567890" + /* 100 */
                                         "123456789012345678901234567890");                    /* 130 */
            fail("Identifiers must be at most 128 characters long!");
        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getClass()
                                  .getName() + ": " + iae.getMessage());
        }
    }

}
