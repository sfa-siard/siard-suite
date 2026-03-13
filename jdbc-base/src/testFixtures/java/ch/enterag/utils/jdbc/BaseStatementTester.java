package ch.enterag.utils.jdbc;

import ch.enterag.utils.EU;
import org.junit.After;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.fail;

public abstract class BaseStatementTester {
    private static final String _sSQL_DDL = "CREATE TABLE TESTTABLE(ID INT, S VARCHAR(255))";
    private static final String _sSQL_CLEAN = "DROP TABLE TESTTABLE CASCADE";
    private static final String _sSQL_QUERY = "SELECT * FROM INFORMATION_SCHEMA.TABLES";
    private Statement _stmt = null;

    public Statement getStatement() {
        return _stmt;
    }

    public BaseStatement getBaseStatement() {
        return (BaseStatement) _stmt;
    }

    /* setUp must create the statement and call this method */
    protected void setStatement(Statement stmt) {
        _stmt = stmt;
    }

    protected void clean()
            throws SQLException {
        try {
            getStatement().executeUpdate(_sSQL_CLEAN);
            getStatement().getConnection()
                          .commit();
        } catch (SQLException se) {
            getStatement().getConnection()
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
            if ((_stmt != null) && (!_stmt.isClosed())) {
                conn = _stmt.getConnection();
                _stmt.close();
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
            _stmt.getConnection();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecuteQuery() {
        enter();
        try {
            _stmt.executeQuery(_sSQL_QUERY);
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
            clean();
            _stmt.executeUpdate(_sSQL_DDL);
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
            _stmt.executeUpdate(_sSQL_DDL, Statement.NO_GENERATED_KEYS);
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
            _stmt.executeUpdate(_sSQL_DDL, new int[]{1, 2});
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
            _stmt.executeUpdate(_sSQL_DDL, new String[]{"COL_A", "COL_B"});
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
    public void testExecute() {
        enter();
        try {
            _stmt.execute(_sSQL_QUERY);
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
            _stmt.execute(_sSQL_QUERY, Statement.NO_GENERATED_KEYS);
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
            _stmt.execute(_sSQL_QUERY, new int[]{1, 2});
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
            _stmt.execute(_sSQL_QUERY, new String[]{"COL_A", "COL_B"});
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLTimeoutException ste) {
            fail(EU.getExceptionMessage(ste));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testAddBatch() {
        enter();
        try {
            _stmt.addBatch(_sSQL_QUERY);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testClearBatch() {
        enter();
        try {
            _stmt.clearBatch();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testExecuteBatch() {
        enter();
        try {
            _stmt.executeBatch();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetResultSet() {
        enter();
        try {
            _stmt.getResultSet();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetUpdateCount() {
        enter();
        try {
            _stmt.getUpdateCount();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMoreResults() {
        enter();
        try {
            _stmt.getMoreResults();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMoreResults_Int() {
        enter();
        try {
            _stmt.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
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
            Connection conn = _stmt.getConnection();
            _stmt.close();
            _stmt = null;
            conn.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testIsClosed() {
        enter();
        try {
            _stmt.isClosed();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testCloseOnCompletion() {
        enter();
        try {
            _stmt.closeOnCompletion();
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
            _stmt.isCloseOnCompletion();
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
            _stmt.cancel();
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
            _stmt.getMaxFieldSize();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetMaxFieldSize() {
        enter();
        try {
            _stmt.setMaxFieldSize(4096);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMaxRows() {
        enter();
        try {
            _stmt.getMaxRows();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetMaxRows() {
        enter();
        try {
            _stmt.setMaxRows(200);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetEscapeProcessing() {
        enter();
        try {
            _stmt.setEscapeProcessing(false);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetQueryTimeout() {
        enter();
        try {
            _stmt.getQueryTimeout();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetQueryTimeout() {
        enter();
        try {
            _stmt.setQueryTimeout(30);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetWarnings() {
        enter();
        try {
            _stmt.getWarnings();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testClearWarnings() {
        enter();
        try {
            _stmt.clearWarnings();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetCursorName() {
        enter();
        try {
            _stmt.setCursorName("testCursor");
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
            _stmt.getFetchDirection();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetFetchDirection() {
        enter();
        try {
            _stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetFetchSize() {
        enter();
        try {
            _stmt.getFetchSize();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetFetchSize() {
        enter();
        try {
            _stmt.setFetchSize(20);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetResultSetConcurrency() {
        enter();
        try {
            _stmt.getResultSetConcurrency();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetResultSetType() {
        enter();
        try {
            _stmt.getResultSetType();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetGeneratedKeys() {
        enter();
        try {
            _stmt.getGeneratedKeys();
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
            _stmt.getResultSetHoldability();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testIsPoolable() {
        enter();
        try {
            _stmt.isPoolable();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testSetPoolable() {
        enter();
        try {
            _stmt.setPoolable(false);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

}
