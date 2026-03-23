package ch.enterag.utils.jdbc;

import ch.enterag.utils.EU;
import org.junit.After;
import org.junit.Test;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;

import static org.junit.Assert.fail;

public abstract class BaseSqlXmlTester {
    private SQLXML _sqlxml = null;

    protected static void printExceptionMessage(Exception e) {
        System.err.println("  " + EU.getExceptionMessage(e));
        System.err.flush();
    }

    private String getCallingMethod(int iDepth) {
        String sCallingMethod = null;

        StackTraceElement[] asSte = Thread.currentThread()
                                          .getStackTrace();
        sCallingMethod = asSte[iDepth].getMethodName();
        return sCallingMethod;
    }

    protected void enter() {
        System.out.println(getCallingMethod(3));
        System.out.flush();
    }

    /* setUp must create the SQLXML instance and call this method */
    protected void setSqlXml(SQLXML sqlxml) {
        _sqlxml = (BaseSqlXml) sqlxml;
    }

    @After
    public void tearDown() {
        try {
            if (_sqlxml != null)
                _sqlxml.free();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetBinaryStream() {
        enter();
        try {
            _sqlxml.getBinaryStream();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBinaryStream() {
        enter();
        try {
            _sqlxml.setBinaryStream();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetCharacterStream() {
        enter();
        try {
            _sqlxml.getCharacterStream();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetCharacterStream() {
        enter();
        try {
            _sqlxml.setCharacterStream();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetString() {
        enter();
        try {
            System.out.println(_sqlxml.getString());
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetString() {
        enter();
        try {
            _sqlxml.setString("<a>some XML</a>");
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetSource() {
        enter();
        try {
            _sqlxml.getSource(StreamSource.class);
        } catch (SQLFeatureNotSupportedException sfnse) {
            printExceptionMessage(sfnse);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetResult() {
        enter();
        try {
            _sqlxml.setResult(StreamResult.class);
        } catch (SQLFeatureNotSupportedException sfnse) {
            printExceptionMessage(sfnse);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testFree() {
        enter();
        try {
            _sqlxml.free();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

}
