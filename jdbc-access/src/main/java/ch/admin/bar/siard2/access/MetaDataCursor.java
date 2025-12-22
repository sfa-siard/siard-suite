/*== MetaDataCursor.java ===============================================
A meta data cursor is implemented using a list of rows and an index of
the current row.
Version     : $Id: MetaDataCursor.java 4 2015-01-12 14:30:07Z hartwigthomas $
Application : SIARD2
Description : Primitive cursor implementation used for meta data.
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 07.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.access;

import com.healthmarketscience.jackcess.Row;

import java.io.IOException;
import java.util.List;


/** Primitive cursor implementation used for meta data.
 * @author Hartwig Thomas */
public class MetaDataCursor
        implements ResultSetCursor {
    /** current row (0-based) */
    private int _iCurrentRow = -1;
    /** list of all rows */
    private List<Row> _listRows = null;


    /** constructor with list of rows to be accessed through the cursor
     * interface.
     * @param listRows list of rows of cursor.
     */
    public MetaDataCursor(List<Row> listRows) {
        _listRows = listRows;
        _iCurrentRow = -1;
    }


    /** {@link ResultSetCursor} */
    @Override
    public void beforeFirst() {
        _iCurrentRow = -1;
    }


    /** {@link ResultSetCursor} */
    @Override
    public void afterLast() {
        _iCurrentRow = _listRows.size();
    }


    /** {@link ResultSetCursor} */
    @Override
    public boolean isBeforeFirst() throws IOException {
        return (_iCurrentRow < 0);
    }


    /** {@link ResultSetCursor} */
    @Override
    public boolean isAfterLast() throws IOException {
        return (_iCurrentRow >= _listRows.size());
    }


    /** {@link ResultSetCursor} */
    @Override
    public Row getNextRow() throws IOException {
        Row row = null;
        if (_iCurrentRow < _listRows.size()) {
            _iCurrentRow++;
            if (_iCurrentRow < _listRows.size())
                row = _listRows.get(_iCurrentRow);
        }
        return row;
    }


    /** {@link ResultSetCursor} */
    @Override
    public Row getPreviousRow() throws IOException {
        Row row = null;
        if (_iCurrentRow >= 0) {
            _iCurrentRow--;
            if (_iCurrentRow >= 0)
                row = _listRows.get(_iCurrentRow);
        }
        return row;
    }


    /** {@link ResultSetCursor} */
    @Override
    public Row refreshCurrentRow() throws IOException {
        Row row = _listRows.get(_iCurrentRow);
        return row;
    }


    /** {@link ResultSetCursor} */
    @Override
    public int getRow() {
        return _iCurrentRow + 1;
    }


    /** {@link ResultSetCursor} */
    @Override
    public void deleteCurrentRow() throws IOException {
        _listRows.remove(_iCurrentRow);
    }


    /** {@link ResultSetCursor} */
    @Override
    public void updateCurrentRow(Row row) throws IOException {
        _listRows.set(_iCurrentRow, row);
    }


    /** {@link ResultSetCursor} */
    @Override
    public void insertRow(Row row)
            throws IOException {
        _listRows.add(row);
    }


    /** {@link ResultSetCursor} */
    @Override
    public int getCount() {
        return _listRows.size();
    }

}
