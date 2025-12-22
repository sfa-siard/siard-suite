/*======================================================================
SortedTableImpl implements the interface SortedTable.
Application : SIARD 2.0
Description : SortedTableImpl implements the interface SortedTable.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 15.10.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.*;
import ch.enterag.utils.background.Progress;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.sql.Types;
import java.text.Collator;


/**
 * SortedTableImpl implements the interface SortedTable.
 *
 */
public class SortedTableImpl
        implements SortedTable {
    private TableImpl _ti = null;
    private TableRecordDispenserImpl _rdi = null;
    private File _fileSorted = null;
    private boolean _bAscending = true;
    private Progress _progress = null;
    private long _lWritten = -1;
    private long _lWrites = -1;
    private long _lWritesPercent = -1;

    /**
     * increment the number or records written, issuing a notification,
     * when a percent is reached.
     */
    private void incWritten() {
        _lWritten++;
        if ((_progress != null) && ((_lWritten % _lWritesPercent) == 0)) {
            int iPercent = (int) ((100 * _lWritten) / _lWrites);
            _progress.notifyProgress(iPercent);
        }
    } 

    /**
     * check if cancel was requested.
     *
     * @return true, if cancel was requested.
     */
    private boolean cancelRequested() {
        boolean bCancelRequested = false;
        if (_progress != null)
            bCancelRequested = _progress.cancelRequested();
        return bCancelRequested;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAscending() {
        return _bAscending;
    }

    private int _iSortColumn = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSortColumn() {
        return _iSortColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream open()
            throws IOException {
        return new FileInputStream(_fileSorted);
    } 

    /*-------------------------------------------------------------------*/

    /**
     * compare two strings in the current Locale.
     * E.g. for correct order of Umlauts and Eszett in a German language
     * Locale.
     *
     * @param sLeft  first string.
     * @param sRight seconds string.
     * @return -1 for sLeft < sRight, 0 for sLeft == sRight, 1 for sLeft > sRight
     */
    private int compareStrings(String sLeft, String sRight) {
        return Collator.getInstance()
                       .compare(sLeft, sRight);
    } 

    /*-------------------------------------------------------------------*/

    /**
     * compare two byte arrays (using unsigned bytes).
     *
     * @param bufLeft  first byte array.
     * @param bufRight second byte array.
     * @return -1 for bufLeft < bufRight, 0 for bufLeft == bufRight, 1 for bufLeft > bufRight
     */
    private int compareBytes(byte[] bufLeft, byte[] bufRight) {
        int iCompare = 0;
        int i = 0;
        while ((iCompare == 0) && (i < bufLeft.length) && (i < bufRight.length)) {
            int iLeft = bufLeft[i];
            if (iLeft < 0)
                iLeft = iLeft + 256;
            int iRight = bufRight[i];
            if (iRight < 0)
                iRight = iRight + 256;
            if (iLeft < iRight)
                iCompare = -1;
            else if (iLeft > iRight)
                iCompare = 1;
            else
                i++;
        }
        if ((iCompare == 0) && ((i < bufLeft.length) || (i < bufRight.length))) {
            if (i < bufRight.length)
                iCompare = -1;
            else
                iCompare = 1;
        }
        return iCompare;
    } 

    /*-------------------------------------------------------------------*/

    /**
     * compare two input stream unsigned byte by unsigned byte and close
     * them after the comparison.
     *
     * @param isLeft  first input stream.
     * @param isRight second input stream.
     * @return -1 for isLeft < isRight, 0 for isLeft == isRight, 1 for isLeft > isRight
     * @throws IOException if an I/O error occurred.
     */
    private int compareInputStreams(InputStream isLeft, InputStream isRight)
            throws IOException {
        int iCompare = 0;
        int iReadLeft = isLeft.read();
        int iReadRight = isRight.read();
        while ((iCompare == 0) && (iReadLeft != -1) && (iReadRight != -1)) {
            iCompare = compareBytes(new byte[]{(byte) iReadLeft}, new byte[]{(byte) iReadRight});
            iReadLeft = isLeft.read();
            iReadRight = isRight.read();
        }
        if ((iCompare == 0) && ((iReadLeft != -1) || (iReadRight != -1))) {
            if (iReadLeft == -1)
                iCompare = -1;
            else
                iCompare = 1;
        }
        isLeft.close();
        isRight.close();
        return iCompare;
    } 

    /*-------------------------------------------------------------------*/

    /**
     * compare two readers in the current Locale and close them afterwards.
     * E.g. for correct order of Umlauts and Eszett in a German language
     * Locale.
     *
     * @param rdrLeft  first reader.
     * @param rdrRight second reader.
     * @return -1 for valueLeft < valueRight, 0 for valueLeft == valueRight, 1 for valueLeft > valueRight
     * @throws IOException if an I/O error occurred.
     */
    private int compareReaders(Reader rdrLeft, Reader rdrRight)
            throws IOException {
        int iCompare = 0;
        int iReadLeft = rdrLeft.read();
        int iReadRight = rdrRight.read();
        while ((iCompare == 0) && (iReadLeft != -1) && (iReadRight != -1)) {
            iCompare = compareStrings(String.valueOf((char) iReadLeft), String.valueOf((char) iReadRight));
            iReadLeft = rdrLeft.read();
            iReadRight = rdrRight.read();
        }
        if ((iCompare == 0) && ((iReadLeft != -1) || (iReadRight != -1))) {
            if (iReadLeft == -1)
                iCompare = -1;
            else
                iCompare = 1;
        }
        rdrLeft.close();
        rdrRight.close();
        return iCompare;
    } 

    /*-------------------------------------------------------------------*/

    /**
     * compare two values (cells, fields) according to their types,
     * sorting NULL values before all others.
     *
     * @param valueLeft  first value.
     * @param valueRight second value.
     * @return -1 for rdrLeft < rdrRight, 0 for rdrLeft == rdrRight, 1 for rdrLeft > rdrRight
     * @throws IOException
     */
    private int compare(Value valueLeft, Value valueRight)
            throws IOException {
        int iCompare = 0;
        MetaValue mv = valueLeft.getMetaValue();
        if (!valueLeft.isNull()) {
            if (!valueRight.isNull()) {
                if (mv.getCardinality() < 0) {
                    int iType = mv.getPreType();
                    switch (iType) {
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.NCHAR:
                        case Types.NVARCHAR:
                            iCompare = compareStrings(valueLeft.getString(), valueRight.getString());
                            break;
                        case Types.CLOB:
                        case Types.NCLOB:
                        case Types.SQLXML:
                            iCompare = compareReaders(valueLeft.getReader(), valueRight.getReader());
                            break;
                        case Types.BINARY:
                        case Types.VARBINARY:
                            iCompare = compareBytes(valueLeft.getBytes(), valueRight.getBytes());
                            break;
                        case Types.BLOB:
                        case Types.DATALINK:
                            iCompare = compareInputStreams(valueLeft.getInputStream(), valueRight.getInputStream());
                            break;
                        case Types.BOOLEAN:
                            iCompare = valueLeft.getBoolean()
                                                .compareTo(valueRight.getBoolean());
                            break;
                        case Types.SMALLINT:
                        case Types.INTEGER:
                            iCompare = valueLeft.getLong()
                                                .compareTo(valueRight.getLong());
                            break;
                        case Types.BIGINT:
                            iCompare = valueLeft.getBigInteger()
                                                .compareTo(valueRight.getBigInteger());
                            break;
                        case Types.DECIMAL:
                        case Types.NUMERIC:
                            iCompare = valueLeft.getBigDecimal()
                                                .compareTo(valueRight.getBigDecimal());
                            break;
                        case Types.REAL:
                        case Types.FLOAT:
                        case Types.DOUBLE:
                            iCompare = valueLeft.getDouble()
                                                .compareTo(valueRight.getDouble());
                            break;
                        case Types.DATE:
                            iCompare = valueLeft.getDate()
                                                .compareTo(valueRight.getDate());
                            break;
                        case Types.TIME:
                            iCompare = valueLeft.getTime()
                                                .compareTo(valueRight.getTime());
                            break;
                        case Types.TIMESTAMP:
                            iCompare = valueLeft.getTimestamp()
                                                .compareTo(valueRight.getTimestamp());
                            break;
                        case Types.OTHER:
                            iCompare = valueLeft.getDuration()
                                                .compare(valueRight.getDuration());
                            break;
                        case Types.NULL:
                            // handle UDT
                            for (int iAttribute = 0; (iCompare == 0) && (iAttribute < valueLeft.getAttributes()); iAttribute++)
                                iCompare = compare(valueLeft.getAttribute(iAttribute), valueRight.getAttribute(iAttribute));
                            break;
                    }
                } else {
                    // handle array (same number of elements on both sides)
                    for (int iElement = 0; (iCompare == 0) && (iElement < valueLeft.getElements()); iElement++)
                        iCompare = compare(valueLeft.getElement(iElement), valueRight.getElement(iElement));
                }
            } else // left not null, right null
                iCompare = 1;
        } else // left null
        {
            if (!valueRight.isNull())
                iCompare = -1;
        }
        return iCompare;
    } 

    /*-------------------------------------------------------------------*/

    /**
     * compare two table records by the sort column in the sort direction.
     *
     * @param tableRecordLeft  first table record.
     * @param tableRecordRight second table record.
     * @return true, if the first record is less (before) or equal to the
     * second record, which results in it being sorted before the second.
     * @throws IOException if an I/O error occurred.
     */
    private boolean lessEqual(TableRecord tableRecordLeft, TableRecord tableRecordRight)
            throws IOException {
        boolean bLessEqual = true;
        Cell cellLeft = tableRecordLeft.getCell(_iSortColumn);
        Cell cellRight = tableRecordRight.getCell(_iSortColumn);
        int iCompare = compare(cellLeft, cellRight);
        if (_bAscending)
            bLessEqual = (iCompare <= 0);
        else
            bLessEqual = (iCompare >= 0);
        return bLessEqual;
    } 

    /*-------------------------------------------------------------------*/

    /**
     * merge two sorted XML streams of records into a sorted XML stream.
     *
     * @param xsrLeft  first sorted stream.
     * @param xsrRight second sorted stream.
     * @param xsw      output stream.
     * @throws IOException        if an I/O error occurred.
     * @throws XMLStreamException if an XML streaming error occurred.
     */
    private void merge(XMLStreamReader xsrLeft, XMLStreamReader xsrRight,
                       XMLStreamWriter xsw)
            throws IOException, XMLStreamException {
        TableRecord tableRecordLeft = null;
        TableRecord tableRecordRight = null;
        if (xsrLeft.isStartElement())
            tableRecordLeft = _rdi.readTableRecord(xsrLeft);
        if (xsrRight.isStartElement())
            tableRecordRight = _rdi.readTableRecord(xsrRight);
        while ((tableRecordLeft != null) && (tableRecordRight != null) && (!cancelRequested())) {
            if (lessEqual(tableRecordLeft, tableRecordRight)) {
                TableRecordRetainerImpl.writeTableRecord(tableRecordLeft, xsw);
                if (xsrLeft.isStartElement())
                    tableRecordLeft = _rdi.readTableRecord(xsrLeft);
                else
                    tableRecordLeft = null;
            } else {
                TableRecordRetainerImpl.writeTableRecord(tableRecordRight, xsw);
                if (xsrRight.isStartElement())
                    tableRecordRight = _rdi.readTableRecord(xsrRight);
                else
                    tableRecordRight = null;
            }
            incWritten();
        }
        while ((tableRecordLeft != null) && (!cancelRequested())) {
            TableRecordRetainerImpl.writeTableRecord(tableRecordLeft, xsw);
            incWritten();
            if (xsrLeft.isStartElement())
                tableRecordLeft = _rdi.readTableRecord(xsrLeft);
            else
                tableRecordLeft = null;
        }
        while ((tableRecordRight != null) && (!cancelRequested())) {
            TableRecordRetainerImpl.writeTableRecord(tableRecordRight, xsw);
            incWritten();
            if (xsrRight.isStartElement())
                tableRecordRight = _rdi.readTableRecord(xsrRight);
            else
                tableRecordRight = null;
        }
    } 

    /**
     * sort the given number of records in the input XML stream and write
     * them to the output XML stream.
     *
     * @param xsr      input XML stream (of unsorted table records)
     * @param xsw      output XML stream (of sorted table records)
     * @param lRecords number of records to sort from the input stream.
     * @throws IOException        if an I/O error occurred.
     * @throws XMLStreamException if an XML streaming error occurred.
     */
    private void sort(XMLStreamReader xsr, XMLStreamWriter xsw, long lRecords)
            throws IOException, XMLStreamException {
        if (lRecords > 1) {
            long lRecordsLeft = lRecords / 2;
            File fileLeft = File.createTempFile("sort", ".xml");
            OutputStream osLeft = new FileOutputStream(fileLeft);
            XMLStreamWriter xswLeft = TableRecordRetainerImpl.writeHeader(osLeft, _ti);
            sort(xsr, xswLeft, lRecordsLeft);
            TableRecordRetainerImpl.writeFooter(xswLeft);
            xswLeft.close();
            osLeft.close();

            long lRecordsRight = lRecords - lRecordsLeft;
            File fileRight = File.createTempFile("sort", ".xml");
            OutputStream osRight = new FileOutputStream(fileRight);
            XMLStreamWriter xswRight = TableRecordRetainerImpl.writeHeader(osRight, _ti);
            sort(xsr, xswRight, lRecordsRight);
            TableRecordRetainerImpl.writeFooter(xswRight);
            xswRight.close();
            osRight.close();

            InputStream isLeft = new FileInputStream(fileLeft);
            XMLStreamReader xsrLeft = _rdi.readHeader(null, isLeft);
            InputStream isRight = new FileInputStream(fileRight);
            XMLStreamReader xsrRight = _rdi.readHeader(null, isRight);
            merge(xsrLeft, xsrRight, xsw);
            xsrLeft.close();
            isLeft.close();
            fileLeft.delete();
            xsrRight.close();
            isRight.close();
            fileRight.delete();
        } else {
            TableRecordRetainerImpl.writeTableRecord(_rdi.readTableRecord(xsr), xsw);
            incWritten();
        }
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public void sort(Table table, boolean bAscending, int iSortColumn, Progress progress)
            throws IOException {
        if ((_fileSorted == null) ||
                (bAscending != _bAscending) ||
                (iSortColumn != _iSortColumn)) {
            _progress = progress;
            _ti = (TableImpl) table;
            long lRecords = table.getMetaTable()
                                 .getRows();
            if (lRecords > 0) {
                /* compute (roughly) the number of records to write */
                _lWrites = lRecords;
                int iLog2 = 0;
                while (lRecords > 1) {
                    lRecords = (lRecords + 1) / 2;
                    iLog2++;
                }
                _lWrites = iLog2 * _lWrites;
                _lWritesPercent = (_lWrites + 99) / 100;
                _lWritten = 0;
                lRecords = table.getMetaTable()
                                .getRows();
                _bAscending = bAscending;
                _iSortColumn = iSortColumn;
                try {
                    File fileOutput = File.createTempFile("tab", ".xml");
                    OutputStream osXml = new FileOutputStream(fileOutput);
                    XMLStreamWriter xsw = TableRecordRetainerImpl.writeHeader(osXml, _ti);

                    _rdi = (TableRecordDispenserImpl) table.openTableRecords(); // reads header
                    InputStream isXml = _rdi.getXmlInputStream();
                    XMLStreamReader xsr = _rdi.getXmlStreamReader();

                    sort(xsr, xsw, lRecords);

                    xsr.close();
                    isXml.close();

                    TableRecordRetainerImpl.writeFooter(xsw);
                    xsw.close();
                    osXml.close();

                    /* avoid "normal" termination in caller */
                    if (cancelRequested())
                        throw new IOException("Table sort cancelled!");

                    if (_fileSorted != null)
                        _fileSorted.delete();
                    _fileSorted = fileOutput;
                    _fileSorted.deleteOnExit();
                } catch (XMLStreamException xse) {
                    throw new IOException("Table could not be sorted!", xse);
                }
            } else
                throw new IllegalArgumentException("Cannot sort 0 records!");
        }
        _progress = null;
    } 

} 
