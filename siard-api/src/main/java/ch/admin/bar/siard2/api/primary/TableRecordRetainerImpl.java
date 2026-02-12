/*======================================================================
RecordRetainer provides serial write access to records.
Application : SIARD 2.0
Description : RecordRetainer provides serial write access to records.
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, 2017
Created    : 04.09.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.TableRecord;
import ch.admin.bar.siard2.api.TableRecordRetainer;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.generated.table.RecordType;
import ch.enterag.utils.FU;
import ch.enterag.utils.SU;
import ch.enterag.utils.jaxb.XMLStreamFactory;
import ch.enterag.utils.xml.XU;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.net.URI;


/**
 * RecordRetainer provides serial write access to records.
 *
 */
public class TableRecordRetainerImpl
        implements TableRecordRetainer {
    private static final int iBUFFER_SIZE = 8192;
    private Table _table = null;
    private CountingOutputStream _osXml = null;
    private XMLStreamWriter _xsw = null;
    private long _lRecord = -1;
    private File _fileTemporaryLobFolder = null;

    private URI getTemporaryLobFolder() {
        return FU.toUri(_fileTemporaryLobFolder);
    }

    /*==================================================================*/
    private class CountingOutputStream
            extends OutputStream {
        private OutputStream _os = null;
        private long _lCount = 0L;

        public CountingOutputStream(OutputStream os) {
            _os = os;
        }

        @Override
        public void write(int b)
                throws IOException {
            _os.write(b);
            _lCount++;
        }

        @Override
        public void write(byte[] buf)
                throws IOException {
            _os.write(buf);
            _lCount += buf.length;
        }

        @Override
        public void write(byte[] buf, int iOffset, int iLength)
                throws IOException {
            _os.write(buf, iOffset, iLength);
            _lCount += iLength;
        }

        @Override
        public void close()
                throws IOException {
            _os.close();
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
     * write the header of the table XML.
     *
     * @param osXml output stream for table XML.
     * @return XMLStreamWriter positioned for first record.
     * @throws IOException if an I/O exception occurs.
     */
    static XMLStreamWriter writeHeader(OutputStream osXml, Table table)
            throws IOException {
        XMLStreamWriter xsw = null;
        try {
            xsw = XMLStreamFactory.createStreamWriter(osXml);
            xsw.setDefaultNamespace(Archive.sSIARD2_TABLE_NAMESPACE);
            xsw.setPrefix("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            xsw.writeStartDocument(SU.sUTF8_CHARSET_NAME, XU.sXML_VERSION_1_0);
            xsw.writeCharacters("\n");
            /* write root tag */
            xsw.writeStartElement(TableImpl._sTAG_TABLE);
            xsw.writeNamespace(null, Archive.sSIARD2_TABLE_NAMESPACE);
            xsw.writeAttribute(TableImpl._sXML_NS + ":" + TableImpl._sXSI_PREFIX, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            xsw.writeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, TableImpl._sTAG_SCHEMA_LOCATION,
                               Archive.sSIARD2_TABLE_NAMESPACE + " " + table.getMetaTable()
                                                                            .getFolder() + ".xsd");
            xsw.writeAttribute(TableImpl._sATTR_TABLE_VERSION, Archive.sMETA_DATA_VERSION);
        } catch (XMLStreamException xse) {
            throw new IOException("Start of document could not be written!", xse);
        }
        return xsw;
    } 

    /**
     * constructor opens a Retainer on the records of a table.
     * N.B.: Only one Retainer can be open on a SIARD archive at a time.
     *
     * @param table table.
     * @throws IOException if an I/O error occurred.
     */
    public TableRecordRetainerImpl(Table table)
            throws IOException {
        TableImpl ti = (TableImpl) table;
        ti.setCreating(true);
        _table = table;
        /* open table XML */
        if (getArchiveImpl().canModifyPrimaryData()) {
            /* create the table folder */
            if (!getArchiveImpl().existsFolderEntry(ti.getTableFolder()))
                getArchiveImpl().createFolderEntry(ti.getTableFolder());
            /* create temporary LOB folder for internal LOB's */
            _fileTemporaryLobFolder = File.createTempFile("siard" + ti.getTableFolder().
                                                                      substring(0, ti.getTableFolder()
                                                                                     .length() - 1)
                                                                      .replace("/", "_") + "_", "");
            _fileTemporaryLobFolder.delete(); // was created as file
            _fileTemporaryLobFolder.mkdir();
            _fileTemporaryLobFolder.deleteOnExit();
            /* start writing table XML */
            _lRecord = 0;
            _osXml = new CountingOutputStream(getArchiveImpl().createFileEntry(ti.getTableXml()));
            _xsw = writeHeader(_osXml, _table);
        } else
            throw new IOException("Table cannot be opened for writing!");
    } 

    /**
     * put an element (cell or field) to the XML stream writer)
     *
     * @param el  element
     * @param xsw XML stream writer.
     * @throws XMLStreamException if an XML streaming exception occurred.
     */
    private static void putRowElement(Element el, XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(el.getLocalName());
        // System.out.print(el.getLocalName());
        for (int i = 0; i < el.getAttributes()
                              .getLength(); i++) {
            Attr attr = (Attr) el.getAttributes()
                                 .item(i);
            xsw.writeAttribute(attr.getName(), attr.getValue());
        }
        for (int i = 0; i < el.getChildNodes()
                              .getLength(); i++) {
            Node node = el.getChildNodes()
                          .item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                putRowElement((Element) node, xsw);
            else if (node.getNodeType() == Node.TEXT_NODE) {
                xsw.writeCharacters(node.getTextContent());
                // System.out.print(node.getTextContent());
            }
        }
        // System.out.println();
        xsw.writeEndElement();
    } 

    /**
     * put the record type with "any" elements for cells to the XML
     * stream writer.
     *
     * @param rt  record type.
     * @param xsw XML stream writer.
     * @throws XMLStreamException if an XML streaming exception occurred.
     */
    private static void putRecordType(RecordType rt, XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(TableImpl._sTAG_RECORD);
        for (int i = 0; i < rt.getAny()
                              .size(); i++)
            putRowElement((Element) rt.getAny()
                                      .get(i), xsw);
        xsw.writeEndElement();
    } 

    /**
     * write a record to the XML stream writer.
     *
     * @param tableRecord record to be written.
     * @param xsw    XML stream writer.
     * @throws IOException        if an I/O exception occurred.
     * @throws XMLStreamException if an XML streaming exception occurred.
     */
    static void writeTableRecord(TableRecord tableRecord, XMLStreamWriter xsw)
            throws IOException, XMLStreamException {
        putRecordType(((TableRecordImpl) tableRecord).getRecordType(), xsw);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(TableRecord tableRecord)
            throws IOException {
        try {
            writeTableRecord(tableRecord, _xsw);
            _lRecord++;
        }
        //catch(JAXBException je) { throw new IOException("Error marshalling record "+String.valueOf(_lRecord)+"!",je); }
        catch (XMLStreamException xse) {
            throw new IOException("Error writing record " + _lRecord + "!", xse);
        }
    } 

    /**
     * copy all temporary external LOB files into the ZIP file and
     * delete all temporary external files and folders.
     *
     * @param fileTempLobFolder source folder containing LOB files to be
     *                          copied. Is deleted after copy.
     * @param sInternalFolder   existing internal target folder.
     * @throws IOException if an I/O error occurred.
     */
    private void copyLobFiles(File fileTempLobFolder, String sInternalFolder)
            throws IOException {
        byte[] buffer = new byte[iBUFFER_SIZE];
        if (fileTempLobFolder.exists()) {
            File[] afile = fileTempLobFolder.listFiles();
            if (afile != null) {
                for (int i = 0; i < afile.length; i++) {
                    File file = afile[i];
                    if (file.isDirectory()) {
                        String sInternalSubFolder = sInternalFolder + file.getName() + "/";
                        copyLobFiles(file, sInternalSubFolder);
                    } else {
                        /* make sure that internal folder exists */
                        if (!getArchiveImpl().existsFolderEntry(sInternalFolder))
                            getArchiveImpl().createFolderEntry(sInternalFolder);
                        /* copy LOB file */
                        String sInternalFile = sInternalFolder + file.getName();
                        OutputStream osLob = getArchiveImpl().createFileEntry(sInternalFile);
                        InputStream isLob = new FileInputStream(file);
                        for (int iRead = isLob.read(buffer); iRead != -1; iRead = isLob.read(buffer))
                            osLob.write(buffer, 0, iRead);
                        isLob.close();
                        osLob.close();
                        file.delete();
                    }
                }
            }
            fileTempLobFolder.delete();
        }
    } 

    /**
     * write all terminating elements to the XML stream writer.
     *
     * @param xsw XML stream writer.
     * @throws IOException if an IOException occurred.
     */
    static void writeFooter(XMLStreamWriter xsw)
            throws IOException {
        try {
            xsw.writeCharacters("\n");
            xsw.writeEndDocument();
        } catch (XMLStreamException xse) {
            throw new IOException("End of document could not be written!", xse);
        }
    } /* writeFooter */

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
            throws IOException {
        try {
            if (_xsw != null) {
                TableImpl ti = (TableImpl) _table;
                writeFooter(_xsw);
                _xsw.close();
                _osXml.close();
                _xsw = null;
                _table.getMetaTable()
                      .setRows(_lRecord);
                /* write table XSD */
                OutputStream osXsd = getArchiveImpl().createFileEntry(ti.getTableXsd());
                _table.exportTableSchema(osXsd);
                osXsd.close();
                // copy all LOB files
                copyLobFiles(FU.fromUri(getTemporaryLobFolder()), ti.getTableFolder());
                FU.deleteFiles(FU.fromUri(getTemporaryLobFolder()));
            } else
                throw new IOException("Table records have not been created!");
        } catch (XMLStreamException xse) {
            throw new IOException("XMLStreamWriter could not be closed!", xse);
        }
        ((TableImpl) _table).setCreating(false);
        _lRecord = -1;
    } /* close */

    /**
     * {@inheritDoc}
     */
    @Override
    public TableRecord create()
            throws IOException {
        TableRecord tableRecord = TableRecordImpl.newInstance(_table, getPosition(), getTemporaryLobFolder());
        return tableRecord;
    } /* createRecord */

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
        return _osXml.getByteCount();
    } /* getByteCount */

} /* class RecordRetainer */
