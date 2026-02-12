/*======================================================================
AccessBlob implements a trivial byte-array based Blob.
Application : SIARD2
Description : AccessBlob implements a trivial byte-array based Blob.
              See https://docs.oracle.com/javase/7/docs/api/java/sql/Blob.html
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 07.03.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;


/** AccessBlob implements a trivial byte-array based Blob.
 * @author Hartwig Thomas
 */
public class AccessBlob implements Blob {
    private byte[] _bufContent = new byte[0];

    /** {@link Blob} */
    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        if (pos <= length() + 1) {
            byte[] buf = new byte[(int) pos - 1 + bytes.length];
            System.arraycopy(_bufContent, 0, buf, 0, (int) pos - 1);
            System.arraycopy(bytes, 0, buf, (int) pos - 1, bytes.length);
            _bufContent = buf;
        } else
            throw new SQLException("Cannot position Blob beyond its current length!");
        return bytes.length;
    }
    /*==================================================================*/


    /** {@link Blob} */
    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len)
            throws SQLException {
        return setBytes(pos, Arrays.copyOfRange(bytes, offset, offset + len));
    }


    /** {@link Blob} */
    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        return new BlobOutputStream(Arrays.copyOf(_bufContent, (int) pos - 1));
    }


    /** {@link Blob} */
    @Override
    public long length() throws SQLException {
        return _bufContent.length;
    }


    /** {@link Blob} */
    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        throw new SQLFeatureNotSupportedException("Searching for binary patterns is not supported!");
    }


    /** {@link Blob} */
    @Override
    public long position(Blob pattern, long start) throws SQLException {
        return position(pattern.getBytes(start, (int) (pattern.length() - start)), 1L);
    }


    /** {@link Blob} */
    @Override
    public void truncate(long len) throws SQLException {
        if (len <= length())
            _bufContent = Arrays.copyOf(_bufContent, (int) len);
        else
            throw new SQLException("Cannot truncate Blob to length larger than its current length!");
    }


    /** {@link Blob} */
    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        return Arrays.copyOfRange(_bufContent, (int) pos - 1, (int) pos + length - 1);
    }


    /** {@link Blob} */
    @Override
    public InputStream getBinaryStream() throws SQLException {
        return new ByteArrayInputStream(_bufContent);
    }


    /** {@link Blob} */
    @Override
    public InputStream getBinaryStream(long pos, long length)
            throws SQLException {
        return new ByteArrayInputStream(Arrays.copyOfRange(_bufContent, (int) pos - 1, (int) (pos + length) - 1));
    }


    /** {@link Blob} */
    @Override
    public void free() throws SQLException {
        _bufContent = new byte[0];
    }


    /*==================================================================*/
    private class BlobOutputStream extends ByteArrayOutputStream {

        public BlobOutputStream(byte[] bufInitialContent) throws SQLException {
            super();
            try {
                write(bufInitialContent);
            } catch (IOException ie) {
                throw new SQLException("Could not write to byte array!", ie);
            }
        }

        @Override
        public void close() throws IOException {
            super.close();
            /* copy content to byte array */
            _bufContent = toByteArray();
        }

    }

}
