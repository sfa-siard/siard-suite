/*======================================================================
AccessClob implements a trivial string-based Clob.
Version     : $Id: $
Application : SIARD2
Description : AccessClob implements a trivial string-based Clob.
              See https://docs.oracle.com/javase/7/docs/api/java/sql/Clob.html
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 07.03.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Arrays;


/** AccessClob implements a trivial string-based Clob.
 * @author Hartwig Thomas
 */
public class AccessClob implements Clob {
    private String _sContent = "";

    /** {@link Clob} */
    @Override
    public int setString(long pos, String str) throws SQLException {
        if (pos <= length() + 1)
            _sContent = _sContent.substring(0, (int) pos - 1) + str;
        else
            throw new SQLException("Cannot position Clob beyond its current length!");
        return str.length();
    }
    /*==================================================================*/

    /** {@link Clob} */
    @Override
    public int setString(long pos, String str, int offset, int len)
            throws SQLException {
        return setString(pos, str.substring(offset, offset + len));
    }
    /*==================================================================*/


    /** {@link Clob} */
    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        return new AsciiOutputStream(_sContent.substring(0, (int) pos - 1));
    }


    /** {@link Clob} */
    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
        Writer wr = null;
        if (pos <= length() + 1)
            wr = new ClobWriter(_sContent.substring(0, (int) pos - 1));
        else
            throw new SQLException("Cannot position Clob beyond its current length!");
        return wr;
    }


    /** {@link Clob} */
    @Override
    public long length() throws SQLException {
        return _sContent.length();
    }


    /** {@link Clob} */
    @Override
    public long position(String searchstr, long start)
            throws SQLException {
        long lPosition = _sContent.indexOf(searchstr, (int) start);
        if (lPosition >= 0)
            lPosition += 1;
        return lPosition;
    }


    /** {@link Clob} */
    @Override
    public long position(Clob searchstr, long start) throws SQLException {
        return position(searchstr.getSubString(start, (int) (searchstr.length() - start)), 1L);
    }


    /** {@link Clob} */
    @Override
    public void truncate(long len) throws SQLException {
        if (len <= length())
            _sContent = _sContent.substring(0, (int) len);
        else
            throw new SQLException("Cannot truncate Clob to length larger than its current length!");
    }


    /** {@link Clob} */
    @Override
    public String getSubString(long pos, int length) throws SQLException {
        return _sContent.substring((int) pos - 1, (int) pos + length - 1);
    }


    /** {@link Clob} */
    @Override
    public Reader getCharacterStream() throws SQLException {
        return new StringReader(_sContent);
    }


    /** {@link Clob} */
    @Override
    public Reader getCharacterStream(long pos, long length)
            throws SQLException {
        return new StringReader(_sContent.substring((int) pos - 1, (int) (pos + length) - 1));
    }


    /** {@link Clob} */
    @Override
    public InputStream getAsciiStream() throws SQLException {
        return new ByteArrayInputStream(_sContent.getBytes());
    }


    /** {@link Clob} */
    @Override
    public void free() throws SQLException {
        _sContent = "";
    }


    /*==================================================================*/
    private class ClobWriter extends StringWriter {

        public ClobWriter(String sInitialContent) {
            super();
            write(sInitialContent);
        }

        @Override
        public void close() throws IOException {
            super.close();
            /* copy content to string */
            _sContent = toString();
        }

    }


    private class AsciiOutputStream extends OutputStream {
        StringBuilder _sb = new StringBuilder();

        public AsciiOutputStream(String sInitialContent) {
            _sb.append(sInitialContent);
        }

        @Override
        public void write(int b) throws IOException {
            _sb.append((char) b);
        }

        @Override
        public void write(byte[] buf) {
            _sb.append(new String(buf));
        }

        @Override
        public void write(byte[] buf, int iOffset, int iLength) {
            _sb.append(new String(Arrays.copyOfRange(buf, iOffset, iOffset + iLength)));
        }

        @Override
        public void close() {
            _sContent = _sb.toString();
        }

    }

}
