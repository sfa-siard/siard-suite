/*== CellImpl.java =====================================================
CellImpl implements the interface Cell.
Application : SIARD 2.0
Description : CellImpl implements the interface Cell. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 05.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.meta.MetaColumnImpl;
import org.w3c.dom.Element;

import java.io.IOException;


/**
 * CellImpl implements the interface Cell.
 *
 */
public class CellImpl
        extends ValueImpl
        implements Cell {
    private TableRecord _tableRecord = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public TableRecord getParentRecord() {
        return _tableRecord;
    }

    private MetaColumn _mc = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaColumn getMetaColumn() {
        return _mc;
    }

    /**
     * constructor
     *
     * @param tableRecord parent record to which this cell belongs.
     * @param iIndex index of value in parent cell or field
     *               (is needed, because element can be null).
     * @param mc     associated MetaColumn instance.
     * @param elCell DOM element holding the full cell.
     */
    private CellImpl(TableRecord tableRecord, int iIndex, MetaColumn mc, Element elCell)
            throws IOException {
        _tableRecord = tableRecord;
        _mc = mc;
        TableRecordImpl ri = (TableRecordImpl) tableRecord;
        initialize(ri.getRecord(), ri.getTemporaryLobFolder(), iIndex, elCell, mc);
    } 

    /**
     * factory
     *
     * @param iIndex index of value in parent cell or field
     *               (is needed, because element can be null).
     * @param tableRecord parent record to which this cell belongs.
     * @param mc     associated MetaColumn instance.
     * @param elCell DOM element holding the full cell.
     */
    public static Cell newInstance(TableRecord tableRecord, int iIndex, MetaColumn mc, Element elCell)
            throws IOException {
        return new CellImpl(tableRecord, iIndex, mc, elCell);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    protected Field createField(int iField, MetaField mf, Element el)
            throws IOException {
        if (el != null) {
            int iCardinality = mf.getCardinality();
            if (iCardinality > 0)
                super.extendArray(iField);
        }
        return FieldImpl.newInstance(iField, this, this, mf, el);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public Cell getAncestorCell() {
        return this;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getInternalLobFolder()
            throws IOException {
        return ((MetaColumnImpl) getMetaColumn()).getFolder();
    } 

} 
