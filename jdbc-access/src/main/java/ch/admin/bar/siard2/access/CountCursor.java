package ch.admin.bar.siard2.access;

import com.healthmarketscience.jackcess.Row;

import java.io.IOException;

public class CountCursor implements ResultSetCursor {
    /** the count value to be delivered */
    private int _iCount = -1;
    /** its alias */
    private String _sAlias = null;
    private int _iCurrentRow = -1;

    public CountCursor(int iCount, String sAlias) {
        _iCount = iCount;
        _sAlias = sAlias;
        _iCurrentRow = -1;
    }

    /** {@link ResultSetCursor} */
    @Override
    public void beforeFirst() {
        _iCurrentRow = -1;
    }

    /** {@link ResultSetCursor} */
    @Override
    public void afterLast() throws IOException {
        _iCurrentRow = getCount();
    }

    /** {@link ResultSetCursor} */
    @Override
    public boolean isBeforeFirst() throws IOException {
        return _iCurrentRow < 0;
    }

    /** {@link ResultSetCursor} */
    @Override
    public boolean isAfterLast() throws IOException {
        return _iCurrentRow > 0;
    }

    /** {@link ResultSetCursor} */
    @Override
    public Row getNextRow() throws IOException {
        ResultSetRow row = null;
        if (isBeforeFirst()) {
            row = new ResultSetRow();
            row.put(_sAlias, Integer.valueOf(_iCount));
        }
        return row;
    }

    /** {@link ResultSetCursor} */
    @Override
    public Row getPreviousRow() throws IOException {
        ResultSetRow row = null;
        if (isAfterLast()) {
            row = new ResultSetRow();
            row.put(_sAlias, Integer.valueOf(_iCount));
        }
        return row;
    }

    /** {@link ResultSetCursor} */
    @Override
    public Row refreshCurrentRow() throws IOException {
        ResultSetRow row = null;
        if (!(isBeforeFirst() || isAfterLast())) {
            row = new ResultSetRow();
            row.put(_sAlias, Integer.valueOf(_iCount));
        }
        return row;
    }

    /** {@link ResultSetCursor} */
    @Override
    public void deleteCurrentRow() throws IOException {
        throw new IOException("Count cannot be deleted!");
    }

    /** {@link ResultSetCursor} */
    @Override
    public void updateCurrentRow(Row row) throws IOException {
        throw new IOException("Count cannot be updated!");
    }

    /** {@link ResultSetCursor} */
    @Override
    public void insertRow(Row row) throws IOException {
        throw new IOException("Count cannot be inserted!");
    }

    /** {@link ResultSetCursor} */
    @Override
    public int getRow() {
        return _iCurrentRow + 1;
    }

    /** {@link ResultSetCursor} */
    @Override
    public int getCount() throws IOException {
        return 1;
    }

}
