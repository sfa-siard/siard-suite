package ch.enterag.utils.jdbc;

import ch.enterag.utils.EU;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.fail;

public abstract class BasePreparedStatementTester {
    private static final String _sSQL_DDL = "CREATE TABLE TESTTABLE(ID INT, S VARCHAR(255))";
    private static final String _sSQL_CLEAN = "DROP TABLE TESTTABLE CASCADE";
    private static final String _sSQL_QUERY = "SELECT * FROM INFORMATION_SCHEMA.TABLES";
    private PreparedStatement _pstmt = null;

    public PreparedStatement getPreparedStatement() {
        return _pstmt;
    }

    public BasePreparedStatement getBasePreparedStatement() {
        return (BasePreparedStatement) _pstmt;
    }

    /* setUp must create the statement and call this method */
    protected void setPreparedStatement(PreparedStatement pstmt) {
        _pstmt = pstmt;
    }

    protected void clean()
            throws SQLException {
        try {
            getPreparedStatement().executeUpdate(_sSQL_CLEAN);
            getPreparedStatement().getConnection()
                                  .commit();
        } catch (SQLException se) {
            getPreparedStatement().getConnection()
                                  .rollback();
        }
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

    @After
    public void tearDown() {
        try {
            Connection conn = null;
            if ((_pstmt != null) && (!_pstmt.isClosed())) {
                conn = _pstmt.getConnection();
                _pstmt.close();
            }
            if ((conn != null) && (!conn.isClosed())) {
                conn.commit();
                conn.close();
            }
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetConnection() {
        enter();
        try {
            _pstmt.getConnection();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMetaData() {
        enter();
        try {
            _pstmt.getMetaData();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetParameterMetaData() {
        enter();
        try {
            _pstmt.getParameterMetaData();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecuteQuery_String() {
        enter();
        try {
            _pstmt.executeQuery(_sSQL_QUERY);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecuteUpdate_String() {
        enter();
        try {
            clean();
            _pstmt.executeUpdate(_sSQL_DDL);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        } finally {
            try {
                clean();
            } catch (SQLException se1) {
                fail(EU.getExceptionMessage(se1));
            }
        }
    }

    @Test
    public void testExecuteUpdate_String_int() {
        enter();
        try {
            clean();
            _pstmt.executeUpdate(_sSQL_DDL, Statement.NO_GENERATED_KEYS);
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        } finally {
            try {
                clean();
            } catch (SQLException se1) {
                fail(EU.getExceptionMessage(se1));
            }
        }
    }

    @Test
    public void testExecuteUpdate_String_AInt() {
        enter();
        try {
            clean();
            _pstmt.executeUpdate(_sSQL_DDL, new int[]{1, 2});
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        } finally {
            try {
                clean();
            } catch (SQLException se1) {
                fail(EU.getExceptionMessage(se1));
            }
        }
    }

    @Test
    public void testExecuteUpdate_String_AString() {
        enter();
        try {
            clean();
            _pstmt.executeUpdate(_sSQL_DDL, new String[]{"COL_A", "COL_B"});
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        } finally {
            try {
                clean();
            } catch (SQLException se1) {
                fail(EU.getExceptionMessage(se1));
            }
        }
    }

    @Test
    public void testExecute_String() {
        enter();
        try {
            _pstmt.execute(_sSQL_QUERY);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecute_String_int() {
        enter();
        try {
            _pstmt.execute(_sSQL_QUERY, Statement.NO_GENERATED_KEYS);
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecute_String_AInt() {
        enter();
        try {
            _pstmt.execute(_sSQL_QUERY, new int[]{1, 2});
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecute_String_AString() {
        enter();
        try {
            _pstmt.execute(_sSQL_QUERY, new String[]{"COL_A", "COL_B"});
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testAddBatch_String() {
        enter();
        try {
            _pstmt.addBatch(_sSQL_QUERY);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testClearBatch() {
        enter();
        try {
            _pstmt.clearBatch();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecuteBatch() {
        enter();
        try {
            _pstmt.executeBatch();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetResultSet() {
        enter();
        try {
            _pstmt.getResultSet();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetUpdateCount() {
        enter();
        try {
            _pstmt.getUpdateCount();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMoreResults() {
        enter();
        try {
            _pstmt.getMoreResults();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMoreResults_Int() {
        enter();
        try {
            _pstmt.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testClose() {
        enter();
        try {
            _pstmt.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testIsClosed() {
        enter();
        try {
            _pstmt.isClosed();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testCloseOnCompletion() {
        enter();
        try {
            _pstmt.closeOnCompletion();
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testIsCloseOnCompletion() {
        enter();
        try {
            _pstmt.isCloseOnCompletion();
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testCancel() {
        enter();
        try {
            _pstmt.cancel();
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMaxFieldSize() {
        enter();
        try {
            _pstmt.getMaxFieldSize();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetMaxFieldSize() {
        enter();
        try {
            _pstmt.setMaxFieldSize(4096);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMaxRows() {
        enter();
        try {
            _pstmt.getMaxRows();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetMaxRows() {
        enter();
        try {
            _pstmt.setMaxRows(200);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetEscapeProcessing() {
        enter();
        try {
            _pstmt.setEscapeProcessing(false);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetQueryTimeout() {
        enter();
        try {
            _pstmt.getQueryTimeout();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetQueryTimeout() {
        enter();
        try {
            _pstmt.setQueryTimeout(30);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetWarnings() {
        enter();
        try {
            _pstmt.getWarnings();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testClearWarnings() {
        enter();
        try {
            _pstmt.clearWarnings();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetCursorName() {
        enter();
        try {
            _pstmt.setCursorName("testCursor");
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetFetchDirection() {
        enter();
        try {
            _pstmt.getFetchDirection();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetFetchDirection() {
        enter();
        try {
            _pstmt.setFetchDirection(ResultSet.FETCH_FORWARD);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetFetchSize() {
        enter();
        try {
            _pstmt.getFetchSize();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetFetchSize() {
        enter();
        try {
            _pstmt.setFetchSize(20);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetResultSetConcurrency() {
        enter();
        try {
            _pstmt.getResultSetConcurrency();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetResultSetType() {
        enter();
        try {
            _pstmt.getResultSetType();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetGeneratedKeys() {
        enter();
        try {
            _pstmt.getGeneratedKeys();
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetResultSetHoldability() {
        enter();
        try {
            _pstmt.getResultSetHoldability();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testIsPoolable() {
        enter();
        try {
            _pstmt.isPoolable();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetPoolable() {
        enter();
        try {
            _pstmt.setPoolable(false);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testAddBatch() {
        enter();
        try {
            _pstmt.addBatch();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testClearParameters() {
        enter();
        try {
            _pstmt.clearParameters();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecute() {
        enter();
        try {
            _pstmt.execute();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecuteQuery() {
        enter();
        try {
            _pstmt.executeQuery();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecuteUpdate() {
        enter();
        try {
            _pstmt.executeUpdate();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetArray() {
        enter();
        try {
            Array array = _pstmt.getConnection()
                                .createArrayOf("EmptyArray", new Object[]{});
            _pstmt.setArray(1, array);
            array.free();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetAsciiStream() {
        enter();
        try {
            _pstmt.setAsciiStream(1, new ByteArrayInputStream("abc".getBytes()));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetAsciiStream_int() {
        enter();
        try {
            _pstmt.setAsciiStream(1, new ByteArrayInputStream("abc".getBytes()), 3);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetAsciiStream_long() {
        enter();
        try {
            _pstmt.setAsciiStream(1, new ByteArrayInputStream("abc".getBytes()), 3l);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBigDecimal() {
        enter();
        try {
            _pstmt.setBigDecimal(1, BigDecimal.TEN);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBinaryStream() {
        enter();
        try {
            _pstmt.setBinaryStream(1, new ByteArrayInputStream(new byte[]{-9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBinaryStream_int() {
        enter();
        try {
            _pstmt.setBinaryStream(1, new ByteArrayInputStream(new byte[]{-9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}), 2);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBinaryStream_long() {
        enter();
        try {
            _pstmt.setBinaryStream(1, new ByteArrayInputStream(new byte[]{-9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}), 2l);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBlob() {
        enter();
        try {
            Blob blob = _pstmt.getConnection()
                              .createBlob();
            blob.setBytes(1l, new byte[]{1, 2, 3});
            _pstmt.setBlob(1, blob);
            blob.free();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBlob_InputStream() {
        enter();
        try {
            _pstmt.setBlob(1, new ByteArrayInputStream(new byte[]{1, 2, 3}));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBlob_InputStream_long() {
        enter();
        try {
            _pstmt.setBlob(1, new ByteArrayInputStream(new byte[]{1, 2, 3}), 3l);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBoolean() {
        enter();
        try {
            _pstmt.setBoolean(1, true);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetByte() {
        enter();
        try {
            _pstmt.setByte(1, (byte) 100);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetBytes() {
        enter();
        try {
            _pstmt.setBytes(1, new byte[]{-9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetCharacterStream() {
        enter();
        try {
            _pstmt.setCharacterStream(1, new StringReader("auää"));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetCharacterStream_int() {
        enter();
        try {
            _pstmt.setCharacterStream(1, new StringReader("auää"), 4);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetCharacterStream_long() {
        enter();
        try {
            _pstmt.setCharacterStream(1, new StringReader("auää"), 4l);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetClob() {
        enter();
        try {
            Clob clob = _pstmt.getConnection()
                              .createClob();
            clob.setString(1l, "abcd");
            _pstmt.setClob(1, clob);
            clob.free();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetClob_Reader() {
        enter();
        try {
            _pstmt.setClob(1, new StringReader("auää"));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetClob_Reader_long() {
        enter();
        try {
            _pstmt.setClob(1, new StringReader("auää"), 4l);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetDate() {
        enter();
        try {
            _pstmt.setDate(1, new java.sql.Date((new java.util.Date()).getTime()));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetDate_Calendar() {
        enter();
        java.util.Date dateNow = new java.util.Date();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(dateNow);
        Date date = new Date(dateNow.getTime());
        try {
            _pstmt.setDate(1, date, cal);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetDouble() {
        enter();
        try {
            _pstmt.setDouble(1, Math.PI);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetFloat() {
        enter();
        try {
            _pstmt.setFloat(1, Double.valueOf(Math.E)
                                     .floatValue());
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetInt() {
        enter();
        try {
            _pstmt.setInt(1, 1234567890);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetLong() {
        enter();
        try {
            _pstmt.setLong(1, 9876543210l);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetNCharacterStream() {
        enter();
        try {
            _pstmt.setNCharacterStream(1, new StringReader("auää"));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetNCharacterStream_long() {
        enter();
        try {
            _pstmt.setNCharacterStream(1, new StringReader("auää"), 4l);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetNClob() {
        enter();
        try {
            NClob nclob = _pstmt.getConnection()
                                .createNClob();
            nclob.setString(1l, "abcd");
            _pstmt.setNClob(1, nclob);
            nclob.free();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetNClob_Reader() {
        enter();
        try {
            _pstmt.setNClob(1, new StringReader("auää"));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetNClob_Reader_long() {
        enter();
        try {
            _pstmt.setNClob(1, new StringReader("auää"), 4l);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetNString() {
        enter();
        try {
            _pstmt.setNString(1, "auää");
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetNull() {
        enter();
        try {
            _pstmt.setNull(1, Types.VARCHAR);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetNull_String() {
        enter();
        try {
            _pstmt.setNull(1, Types.VARCHAR, "VARCHAR(255)");
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetObject() {
        enter();
        try {
            _pstmt.setObject(1, "auää");
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetObject_int() {
        enter();
        try {
            _pstmt.setObject(1, "auää", Types.VARCHAR);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetObject_int_int() {
        enter();
        try {
            _pstmt.setObject(1, "auää", Types.VARCHAR, 255);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetRef() {
        enter();
        try {
            _pstmt.setRef(1, null);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetRowId() {
        enter();
        try {
            _pstmt.setRowId(1, null);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetSqlXml() {
        enter();
        try {
            SQLXML sqlxml = _pstmt.getConnection()
                                  .createSQLXML();
            sqlxml.setString("<a>fokrikrei</a>");
            _pstmt.setSQLXML(1, sqlxml);
            sqlxml.free();
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetShort() {
        enter();
        try {
            _pstmt.setShort(1, (short) -10000);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetString() {
        enter();
        try {
            _pstmt.setString(1, "AHA");
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetTime() {
        enter();
        try {
            _pstmt.setTime(1, new Time((new java.util.Date()).getTime()));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetTime_Calendar() {
        enter();
        java.util.Date dateNow = new java.util.Date();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(dateNow);
        Time time = new Time(dateNow.getTime());
        try {
            _pstmt.setTime(1, time, cal);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetTimestamp() {
        enter();
        try {
            _pstmt.setTimestamp(1, new Timestamp((new java.util.Date()).getTime()));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetTimestamp_Calendar() {
        enter();
        java.util.Date dateNow = new java.util.Date();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(dateNow);
        Timestamp ts = new Timestamp(dateNow.getTime());
        try {
            _pstmt.setTimestamp(1, ts, cal);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetUrl() {
        enter();
        try {
            URL url = new URL("http://www.enterag.ch/");
            _pstmt.setURL(1, url);
        } catch (MalformedURLException mue) {
            fail(EU.getExceptionMessage(mue));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSetUnicodeStream() {
        enter();
        try {
            _pstmt.setUnicodeStream(1, new ByteArrayInputStream("auää".getBytes()), 4);
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

}
