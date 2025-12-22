/*======================================================================
Implementation of simple search in table.
Application : SIARD 2.0
Description : Implementation of simple search in table. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 31.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.*;
import ch.enterag.utils.DU;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;


/**
 * Implementation of simple search in table.
 *
 */
public class SearchImpl
        implements Search {
    private String _sFindString = null;
    private boolean _bMatchCase = false;
    private List<MetaColumn> _listColumns = null;
    private TableRecordDispenser _rd = null;
    private TableRecord _tableRecord = null;
    private Cell _cell = null;
    private int _iFoundOffset = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFindString() {
        return _sFindString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getFoundRow() {
        return _tableRecord.getRecord();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFoundPosition() {
        return _cell.getMetaColumn()
                    .getPosition();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFoundString(DU du)
            throws IOException {
        String s = null;
        if (!_cell.isNull()) {
            Object o = _cell.getObject();
            s = o.toString();
            if (o instanceof BigDecimal)
                s = ((BigDecimal) o).toPlainString();
            else if (o instanceof Date)
                s = du.fromSqlDate((Date) o);
            else if (o instanceof Time)
                s = du.fromSqlTime((Time) o);
            else if (o instanceof Timestamp)
                s = du.fromSqlTimestamp((Timestamp) o);
        }
        return s;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFoundOffset() {
        return _iFoundOffset;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public void find(List<MetaColumn> listColumns, String sFindString,
                     boolean bMatchCase)
            throws IOException {
        if (listColumns.size() > 0) {
            Table table = listColumns.get(0)
                                     .getParentMetaTable()
                                     .getTable();
            _listColumns = listColumns;
            _sFindString = sFindString;
            _bMatchCase = bMatchCase;
            if (bMatchCase)
                _sFindString = sFindString;
            else
                _sFindString = sFindString.toLowerCase();
            _rd = table.openTableRecords();
        } else
            throw new IllegalArgumentException("List of columns must not be empty for search!");
    } 

    /**
     * find another occurrence of the find string in the current cell.
     *
     * @param du Date formatter.
     * @return offset of occurrence.
     * @throws IOException if an I/O error occurs.
     */
    private int findInCell(DU du)
            throws IOException {
        _iFoundOffset++;
        String sFoundString = getFoundString(du);
        if (sFoundString != null) {
            if (_bMatchCase)
                _iFoundOffset = sFoundString.indexOf(_sFindString, _iFoundOffset);
            else
                _iFoundOffset = sFoundString.toLowerCase()
                                            .indexOf(_sFindString, _iFoundOffset);
        } else
            _iFoundOffset = -1;
        return _iFoundOffset;
    } 

    /**
     * find another occurrence of the find string in the current record.
     *
     * @param iStartCell column of first cell to be examined.
     * @param du         Date formatter.
     * @return cell containing find string or null.
     * @throws IOException if an I/O error occurred.
     */
    private Cell findInTableRecord(int iStartCell, DU du)
            throws IOException {
        /* look at more cells in the same record */
        for (int iCell = iStartCell; (_cell == null) && (iCell < _tableRecord.getCells()); iCell++) {
            _cell = _tableRecord.getCell(iCell);
            if (_listColumns.contains(_cell.getMetaColumn())) {
                _iFoundOffset = findInCell(du);
                if (_iFoundOffset < 0)
                    _cell = null;
            } else
                _cell = null;
        }
        return _cell;
    } 

    /**
     * find another occurrence of the find string in the rest of the table.
     *
     * @param du Date formatter.
     * @return cell containing find string or null.
     * @throws IOException if an I/O error occurred.
     */
    private Cell findInTable(DU du)
            throws IOException {
        for (_tableRecord = _rd.get(); (_cell == null) && (_tableRecord != null); ) {
            _cell = findInTableRecord(0, du);
            if (_cell == null)
                _tableRecord = _rd.get();
        }
        if (_tableRecord == null) {
            _rd.close();
            _sFindString = null;
        }
        return _cell;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public Cell findNext(DU du)
            throws IOException {
        if (_sFindString != null) {
            /* look for more in the same cell */
            int iStartCell = 0;
            if (_cell != null) {
                iStartCell = _cell.getMetaColumn()
                                  .getPosition(); // column+1
                _iFoundOffset = findInCell(du);
                if (_iFoundOffset < 0)
                    _cell = null;
            }
            /* look in further cells of same record */
            if ((_cell == null) && (_tableRecord != null))
                _cell = findInTableRecord(iStartCell, du);
            /* look in further records */
            if (_cell == null)
                _cell = findInTable(du);
        }
        return _cell;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canFindNext() {
        return (_sFindString != null);
    } 

} 
