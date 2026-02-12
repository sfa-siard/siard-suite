/*======================================================================
RecordDispenser  provides serial read access to records.
Application : SIARD 2.0
Description : RecordDispenser  provides serial read access to records.
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, 2017
Created    : 04.09.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.TableRecord;
import ch.admin.bar.siard2.api.TableRecordDispenser;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.generated.table.RecordType;
import ch.enterag.utils.EU;
import ch.enterag.utils.jaxb.XMLStreamFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * RecordDispenser provides serial read access to records.
 *
 */
public class TableRecordDispenserImpl
        implements TableRecordDispenser, AutoCloseable {
    private static final ch.admin.bar.siard2.api.generated.table.ObjectFactory _OF_TABLE = new ch.admin.bar.siard2.api.generated.table.ObjectFactory();
    private Table _table = null;
    private CountingInputStream _isXml = null;

    InputStream getXmlInputStream() {
        return _isXml;
    }

    private XMLStreamReader _xsr = null;

    XMLStreamReader getXmlStreamReader() {
        return _xsr;
    }

    private long _lRecord = -1;

    /*==================================================================*/
    private class CountingInputStream
            extends InputStream {
        private InputStream _is = null;
        private long _lCount = 0L;

        public CountingInputStream(InputStream is) {
            _is = is;
        }

        @Override
        public int read()
                throws IOException {
            int iResult = _is.read();
            if (iResult != -1)
                _lCount++;
            return iResult;
        }

        @Override
        public int read(byte[] buf)
                throws IOException {
            int iResult = _is.read(buf);
            if (iResult != -1)
                _lCount += iResult;
            return iResult;
        }

        @Override
        public int read(byte[] buf, int iOffset, int iLength)
                throws IOException {
            int iResult = _is.read(buf, iOffset, iLength);
            if (iResult != -1)
                _lCount += iResult;
            return iResult;
        }

        @Override
        public void close()
                throws IOException {
            _is.close();
        }

        public long getByteCount() {
            return _lCount;
        }
    } 
    /*==================================================================*/

    /**
     * get ArchiveImpl instance.
     *
     * @return ArchiveImpl instance.
     */
    private ArchiveImpl getArchiveImpl() {
        return (ArchiveImpl) _table.getParentSchema()
                                   .getParentArchive();
    } 

    /**
     * read the header of the table XML and position reader on first record.
     *
     * @param isXsd XML Schema for validation or null.
     * @param isXml table XML input stream.
     * @return XMLStreamReader positioned on first record.
     * @throws IOException if an I/O error occurred.
     */
    XMLStreamReader readHeader(InputStream isXsd, InputStream isXml)
            throws IOException {
        XMLStreamReader xsr = null;
        try {
            if (isXsd != null) {
                xsr = XMLStreamFactory.createXMLStreamReader(isXsd, isXml);
                isXsd.close();
            } else
                xsr = XMLStreamFactory.createXMLStreamReader(isXml);
            xsr.nextTag();
            if (xsr.isStartElement() && TableImpl._sTAG_TABLE.equals(xsr.getLocalName()))
                xsr.nextTag(); // positions on first row
            else
                throw new XMLStreamException("Root element <" + TableImpl._sTAG_TABLE + "> not found!");
        } catch (XMLStreamException xse) {
            throw new IOException("XMLStreamReader cannot be created!", xse);
        }
        return xsr;
    } 

    /**
     * constructor opens a RecordInputStream to a table.
     *
     * @param table Table instance.
     * @throws IOException
     */
    public TableRecordDispenserImpl(Table table)
            throws IOException {
        _table = table;
        TableImpl ti = (TableImpl) table;
        if (!ti.isCreating()) {
            ArchiveImpl ai = getArchiveImpl();
            if (ti.getSortedTable() == null)
                _isXml = new CountingInputStream(ai.openFileEntry(ti.getTableXml()));
            else
                _isXml = new CountingInputStream(ti.getSortedTable()
                                                   .open());
            InputStream isXsd = ai.openFileEntry(ti.getTableXsd());
            _xsr = readHeader(isXsd, _isXml);
            _lRecord = 0;
        } else
            throw new IOException("Table cannot be opened for reading!");
    } 

    /**
     * get the row as a DOM element from the XML stream reader.
     *
     * @param xsr XML stream reader.
     * @param doc DOM document, to which the row element belongs.
     * @return DOM element representing the row.
     * @throws XMLStreamException
     */
    private Element getRowElement(XMLStreamReader xsr, Document doc)
            throws XMLStreamException {
        Element elRow = null;
        String sTag = xsr.getLocalName();
        if (xsr.isStartElement() && sTag.equals(TableImpl._sTAG_RECORD)) {
            elRow = doc.createElementNS(xsr.getNamespaceURI(), xsr.getLocalName());
            List<Element> listElementsStack = new ArrayList<Element>();
            listElementsStack.add(elRow);
            for (xsr.next(); listElementsStack.size() > 0; xsr.next()) {
                switch (xsr.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        Element elNew = doc.createElementNS(xsr.getNamespaceURI(), xsr.getLocalName());
                        for (int i = 0; i < xsr.getAttributeCount(); i++)
                            elNew.setAttribute(xsr.getAttributeLocalName(i), xsr.getAttributeValue(i));
                        listElementsStack.get(0)
                                         .appendChild(elNew);
                        listElementsStack.add(0, elNew);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        Element elParent = listElementsStack.get(0);
                        if (elParent.getTagName()
                                    .equals(xsr.getLocalName()))
                            listElementsStack.remove(elParent);
                        else
                            System.err.println("Unexpected END ELEMENT!");
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        Text text = doc.createTextNode(xsr.getText());
                        listElementsStack.get(0)
                                         .appendChild(text);
                        break;
                    default:
                        break;
                }
            }
        }
        return elRow;
    } 

    /**
     * get the record type with "any" elements for cells.
     *
     * @return record type.
     * @throws IOException        if an I/O exception occurred.
     * @throws XMLStreamException if an XML streaming exception occurred.
     */
    private RecordType getRecordType(XMLStreamReader xsr)
            throws IOException, XMLStreamException {
        RecordType rt = null;
        String sNamespace = xsr.getNamespaceURI();
        rt = _OF_TABLE.createRecordType();
        Document doc = TableImpl.getDocumentBuilder()
                                .newDocument();
        Element elTable = doc.createElementNS(sNamespace, TableImpl._sTAG_TABLE);
        doc.appendChild(elTable);
        elTable.setAttribute(TableImpl._sXML_NS, sNamespace);
        Element elRow = getRowElement(xsr, doc);
        elTable.appendChild(elRow);
        for (int i = 0; i < elRow.getChildNodes()
                                 .getLength(); i++) {
            Node nodeChild = elRow.getChildNodes()
                                  .item(i);
            if (nodeChild.getNodeType() == Node.ELEMENT_NODE) {
                Element elColumn = (Element) nodeChild;
                rt.getAny()
                  .add(elColumn);
            }
        }
        if (!(xsr.isStartElement() || xsr.isEndElement()))
            xsr.nextTag();
        return rt;
    } 

    /**
     * read a record from the XML stream reader.
     *
     * @param xsr XML stream reader.
     * @return record read.
     * @throws IOException        if an I/O exception occurred.
     * @throws XMLStreamException if an XML streaming exception occurred.
     */
    TableRecord readTableRecord(XMLStreamReader xsr)
            throws IOException, XMLStreamException {
        TableRecord tableRecord = null;
        if (xsr.isStartElement() && TableImpl._sTAG_RECORD.equals(xsr.getLocalName())) {
            /* create the record DOM */
            RecordType rt = getRecordType(xsr);
            tableRecord = TableRecordImpl.newInstance(_table, getPosition(), rt);
        } else
            throw new IOException("Unexpected tag " + xsr.getLocalName() + " encountered");
        return tableRecord;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public TableRecord get()
            throws IOException {
        TableRecord tableRecord = null;
        try {
            if (_lRecord < _table.getMetaTable()
                                 .getRows()) {
                tableRecord = readTableRecord(_xsr);
                _lRecord++;
            }
        } catch (XMLStreamException xse) {
            throw new IOException("Record " + _lRecord + " cannot be read!", xse);
        }
        return tableRecord;
    }

    /**
     * skip the given number of repeated elements with given tag.
     *
     * @param lSkip number of repeated elements to skip.
     * @param sTag  tag of repeated elements to be skipped.
     * @throws XMLStreamException if an error occurs.
     */
    private long skip(long lSkip, String sTag)
            throws XMLStreamException {
        long lSkipped = 0;
        boolean bContinue = _xsr.isStartElement() && sTag.equals(_xsr.getLocalName());
        while (bContinue && (lSkipped < lSkip)) {
            _xsr.next();
            if (_xsr.isEndElement() && sTag.equals(_xsr.getLocalName())) {
                lSkipped++;
                _xsr.nextTag();
                bContinue = _xsr.isStartElement() && sTag.equals(_xsr.getLocalName());
            }
        }
        return lSkipped;
    } /* skip */

    /**
     * {@inheritDoc}
     */
    @Override
    public void skip(long lSkip)
            throws IOException {
        try {
            if (_lRecord + lSkip > _table.getMetaTable()
                                         .getRows())
                lSkip = _table.getMetaTable()
                              .getRows() - _lRecord;
            if (lSkip == skip(lSkip, TableImpl._sTAG_RECORD))
                _lRecord = _lRecord + lSkip;
            else
                throw new IOException("Unexpected end of records encountered!");
        } catch (XMLStreamException xse) {
            throw new IOException(lSkip + " records starting with " + _lRecord + " could not be skipped (" + EU.getExceptionMessage(xse) + ")!");
        }
    } /* skip */

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
            throws IOException {
        try {
            if (_xsr != null) {
                _xsr.close();
                _isXml.close();
                _xsr = null;
            } else
                throw new IOException("Table records have not been been opened!");
        } catch (XMLStreamException xse) {
            throw new IOException("XMLStreamReader could not be closed!", xse);
        }
        _lRecord = -1;
    } /* close */

    /**
     * {@inheritDoc}
     */
    @Override
    public long getPosition() {
        return _lRecord;
    } /* getPosition */

    /**
     * {@inheritDoc}
     */
    @Override
    public long getByteCount() {
        return _isXml.getByteCount();
    } /* getByteCount */

} /* class RecordDispenser */
