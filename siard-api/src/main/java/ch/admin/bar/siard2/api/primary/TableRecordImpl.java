/*== RecordImpl.java ===================================================
RecordImpl implements the interface Record.
Application : SIARD 2.0
Description : RecordImpl implements the interface Record.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 05.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.TableRecord;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.Value;
import ch.admin.bar.siard2.api.generated.table.ObjectFactory;
import ch.admin.bar.siard2.api.generated.table.RecordType;
import ch.admin.bar.siard2.api.meta.MetaColumnImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * RecordImpl implements the interface Record.
 *
 */
public class TableRecordImpl
        implements TableRecord {
    private static Document _doc = null;

    public static Document getDocument()
            throws IOException {
        if (_doc == null) {
            DocumentBuilder db = TableImpl.getDocumentBuilder();
            _doc = db.newDocument();
        }
        return _doc;
    } 

    private URI _uriTemporaryLobFolder = null;

    public URI getTemporaryLobFolder() {
        return _uriTemporaryLobFolder;
    }

    private final ObjectFactory _of = new ObjectFactory();
    private Map<String, Cell> _mapCells = null;

    private Map<String, Cell> getCellMap()
            throws IOException {
        if (_mapCells == null) {
            _mapCells = new HashMap<String, Cell>();
            /* the cells that are null were not in the list */
            for (int iColumn = 0; iColumn < getParentTable().getMetaTable()
                                                            .getMetaColumns(); iColumn++) {
                MetaColumnImpl mci = (MetaColumnImpl) getParentTable().getMetaTable()
                                                                      .getMetaColumn(iColumn);
                String sColumnTag = CellImpl.getColumnTag(iColumn);
                if (_mapCells.get(sColumnTag) == null)
                    _mapCells.put(sColumnTag, CellImpl.newInstance(this, iColumn, mci, null));
            }
        }
        return _mapCells;
    }

    private Table _tableParent = null;
    private long _lRecord = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRecord() {
        return _lRecord;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Table getParentTable() {
        return _tableParent;
    }

    private RecordType _rt = null;

    private void setRecordType(RecordType rt)
            throws IOException {
        for (int iColumn = 0; iColumn < rt.getAny()
                                          .size(); iColumn++) {
            Element elCell = (Element) rt.getAny()
                                         .get(iColumn);
            String sTag = elCell.getLocalName();
            int iIndex = CellImpl.getIndex(elCell.getLocalName());
            MetaColumnImpl mc = (MetaColumnImpl) getParentTable().getMetaTable()
                                                                 .getMetaColumn(iIndex);
            Cell cell = CellImpl.newInstance(this, iIndex, mc, elCell);
            getCellMap().put(sTag, cell);
        }
        _rt = rt;
    } 

    RecordType getRecordType()
            throws IOException {
        _rt.getAny()
           .clear();
        for (int iColumn = 0; iColumn < getParentTable().getMetaTable()
                                                        .getMetaColumns(); iColumn++) {
            Cell cell = getCellMap().get(CellImpl.getColumnTag(iColumn));
            if (!cell.isNull()) {
                Element elCell = ((CellImpl) cell).getValue();
                if (elCell != null)
                    _rt.getAny()
                       .add(elCell);
            }
        }
        return _rt;
    } 

    /**
     * constructor for writing a record.
     *
     * @param tableParent           associated table instance.
     * @param lRecord               row in table.
     * @param uriTemporaryLobFolder temporary folder for LOBs.
     */
    private TableRecordImpl(Table tableParent, long lRecord, URI uriTemporaryLobFolder)
            throws IOException {
        _tableParent = tableParent;
        _lRecord = lRecord;
        _uriTemporaryLobFolder = uriTemporaryLobFolder;
        setRecordType(_of.createRecordType());
    } 

    /**
     * factory for writing a record.
     *
     * @param tableParent           associated table instance.
     * @param lRecord               row in table.
     * @param uriTemporaryLobFolder temporary folder for LOBs.
     */
    public static TableRecord newInstance(Table tableParent, long lRecord, URI uriTemporaryLobFolder)
            throws IOException {
        return new TableRecordImpl(tableParent, lRecord, uriTemporaryLobFolder);
    } 

    /**
     * constructor for reading a record.
     *
     * @param tableParent associated table instance.
     * @param lRecord     row in table.
     * @param rt          record type filled with cell elements.
     */
    private TableRecordImpl(Table tableParent, long lRecord, RecordType rt)
            throws IOException {
        _tableParent = tableParent;
        _lRecord = lRecord;
        if (rt.getAny()
              .size() > 0) {
            Element el = (Element) rt.getAny()
                                     .get(0);
            if (el.getOwnerDocument() != getDocument())
                _doc = el.getOwnerDocument();
        }
        setRecordType(rt);
    } 

    /**
     * factory for reading a record.
     *
     * @param tableParent associated table instance.
     * @param lRecord     row in table.
     * @param rt          record type filled with cell elements.
     */
    public static TableRecord newInstance(Table tableParent, long lRecord, RecordType rt)
            throws IOException {
        return new TableRecordImpl(tableParent, lRecord, rt);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCells()
            throws IOException {
        return getCellMap().size();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public Cell getCell(int iCell)
            throws IOException {
        String sTag = CellImpl.getColumnTag(iCell);
        Cell cell = getCellMap().get(sTag);
        return cell;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Value> getValues(boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException {
        List<Value> listValues = new ArrayList<Value>();
        for (int iCell = 0; iCell < getCells(); iCell++)
            listValues.addAll(getCell(iCell).getValues(bSupportsArrays, bSupportsUdts));
        return listValues;
    } 

} 
