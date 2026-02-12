/*== TableImpl.java ========================================================
TableImpl implements the interface Table.
Application : SIARD 2.0
Description : TableImpl implements the interface Table.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 04.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.export.HtmlExport;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.generated.TableType;
import ch.admin.bar.siard2.api.generated.TablesType;
import ch.admin.bar.siard2.api.meta.MetaSchemaImpl;
import ch.admin.bar.siard2.api.meta.MetaTableImpl;
import ch.enterag.utils.SU;
import ch.enterag.utils.background.Progress;
import ch.enterag.utils.xml.XU;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Types;

/**
 * TableImpl implements the interface Table.
 */
public class TableImpl
        extends SearchImpl
        implements Table {
    public static final String _sTABLE_FOLDER_PREFIX = "table";
    private static final int iBUFFER_SIZE = 8192;
    private static final long lROWS_MAX_VALIDATE = 1024; // Long.MAX_VALUE;
    public static final String _sXML_NS = "xmlns";
    public static final String _sXSI_PREFIX = "xsi";
    public static final String _sTAG_SCHEMA_LOCATION = "schemaLocation";
    public static final String _sTAG_TABLE = "table";
    public static final String _sTAG_RECORD = "row";
    public static final String _sATTR_TABLE_VERSION = "version";
    private static final ch.admin.bar.siard2.api.generated.ObjectFactory _OF = new ch.admin.bar.siard2.api.generated.ObjectFactory();

    static DocumentBuilder _db = null;

    @SneakyThrows
    static DocumentBuilder getDocumentBuilder() throws IOException {
        if (_db == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            _db = dbf.newDocumentBuilder();
        }
        return _db;
    }

    private static Transformer _trans = null;

    private static Transformer getTransformer()
            throws IOException {
        try {
            if (_trans == null) {
                TransformerFactory tf = TransformerFactory.newInstance();
                _trans = tf.newTransformer();
                _trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                _trans.setOutputProperty(OutputKeys.METHOD, "xml");
                _trans.setOutputProperty(OutputKeys.INDENT, "yes");
                _trans.setOutputProperty(OutputKeys.ENCODING, SU.sUTF8_CHARSET_NAME);
                // _trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            }
        } catch (TransformerConfigurationException tce) {
            throw new IOException("Transformer could not be created!", tce);
        }
        return _trans;
    }

    /**
     * sorted, temporary table
     */
    private SortedTable _stable = null;

    public SortedTable getSortedTable() {
        return _stable;
    }

    private Schema _schemaParent = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getParentSchema() {
        return _schemaParent;
    }

    private MetaTable _mt = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaTable getMetaTable() {
        return _mt;
    }

    private ArchiveImpl getArchiveImpl() {
        return (ArchiveImpl) getParentSchema().getParentArchive();
    }


    String getTableFolder() {
        return ((SchemaImpl) getParentSchema()).getSchemaFolder() + getMetaTable().getFolder() + "/";
    }

    String getTableXsd() {
        return getTableFolder() + getMetaTable().getFolder() + ".xsd";
    }

    String getTableXml() {
        return getTableFolder() + getMetaTable().getFolder() + ".xml";
    }

    /**
     * add the <xs:element> meta data definition for the given tag and type
     * to the parent element (<xs:sequence>).
     *
     * @param elParent  parent element (<xs:sequence>).
     * @param sTag      name of element.
     * @param mv        column or field meta data to be represented by <xs:element>.
     * @param bNullable true, if element may be missing.
     * @throws IOException if an I/O error occurred.
     */
    private void addElement(Element elParent, String sTag, MetaValue mv, boolean bNullable)
            throws IOException {
        Document doc = elParent.getOwnerDocument();
        // create the element with the same namespace as the parent (XML Schema namespace)
        Element elElement = doc.createElementNS(elParent.getNamespaceURI(), "xs:element");
        elParent.appendChild(elElement);
        elElement.setAttribute("name", sTag);
        int iPreType = mv.getPreType();
        if ((iPreType != Types.NULL) && (mv.getMetaFields() == 0)) {
            boolean bShort = (mv.getMaxLength() <= getArchiveImpl().getMaxInlineSize());
            String sXmlType = null;
            switch (iPreType) {
                case Types.CHAR:
                    sXmlType = bShort ? "xs:string" : "clobType";
                    break;
                case Types.VARCHAR:
                    sXmlType = bShort ? "xs:string" : "clobType";
                    break;
                case Types.DATALINK:
                    sXmlType = "blobType";
                    break;
                case Types.CLOB:
                    sXmlType = "clobType";
                    break;
                case Types.NCHAR:
                    sXmlType = bShort ? "xs:string" : "clobType";
                    break;
                case Types.NVARCHAR:
                    sXmlType = bShort ? "xs:string" : "clobType";
                    break;
                case Types.NCLOB:
                    sXmlType = "clobType";
                    break;
                case Types.BINARY:
                    sXmlType = bShort ? "xs:hexBinary" : "blobType";
                    break;
                case Types.VARBINARY:
                    sXmlType = bShort ? "xs:hexBinary" : "blobType";
                    break;
                case Types.BLOB:
                    sXmlType = "blobType";
                    break;
                case Types.NUMERIC:
                    sXmlType = "xs:decimal";
                    break;
                case Types.DECIMAL:
                    sXmlType = "xs:decimal";
                    break;
                case Types.SMALLINT:
                    sXmlType = "xs:integer";
                    break;
                case Types.INTEGER:
                    sXmlType = "xs:integer";
                    break;
                case Types.BIGINT:
                    sXmlType = "xs:integer";
                    break;
                case Types.FLOAT:
                    sXmlType = "xs:double";
                    break;
                case Types.REAL:
                    sXmlType = "xs:float";
                    break;
                case Types.DOUBLE:
                    sXmlType = "xs:double";
                    break;
                case Types.BOOLEAN:
                    sXmlType = "xs:boolean";
                    break;
                case Types.DATE:
                    sXmlType = "dateType";
                    break;
                case Types.TIME:
                    sXmlType = "timeType";
                    break;
                case Types.TIMESTAMP:
                    sXmlType = "dateTimeType";
                    break;
                case Types.SQLXML:
                    sXmlType = "clobType";
                    break;
                case Types.OTHER:
                    sXmlType = "xs:duration";
                    break;
            }
            elElement.setAttribute("type", sXmlType);
            if (bNullable)
                elElement.setAttribute("minOccurs", "0");
        } else {
            elElement.setAttribute("minOccurs", "0");

            // create complexType in the XML Schema namespace
            Element elComplexType = doc.createElementNS(elElement.getNamespaceURI(), "xs:complexType");
            elElement.appendChild(elComplexType);

            // create sequence in the XML Schema namespace
            Element elSequence = doc.createElementNS(elComplexType.getNamespaceURI(), "xs:sequence");
            elComplexType.appendChild(elSequence);

            for (int iField = 0; iField < mv.getMetaFields(); iField++) {
                MetaValue mvField = mv.getMetaField(iField);
                String sTagField = null;
                if (mv.getCardinality() > 0)
                    sTagField = CellImpl.getElementTag(iField);
                else {
                    CategoryType cat = mv.getMetaType()
                            .getCategoryType();
                    if (cat == CategoryType.UDT)
                        sTagField = CellImpl.getAttributeTag(iField);
                }
                addElement(elSequence, sTagField, mvField, true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exportTableSchema(OutputStream osXsd)
            throws IOException {
        String sEntryName = getTableXsd();
        if (getArchiveImpl().existsFileEntry(sEntryName)) {
            /* export existing old table XSD in old format! */
            byte[] buf = new byte[iBUFFER_SIZE];
            InputStream is = getArchiveImpl().openFileEntry(sEntryName);
            for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
                osXsd.write(buf, 0, iRead);
            is.close();
        } else if (getMetaTable().getMetaColumns() > 0) {
            try {
                /* read table template into DOM */
                InputStream isXsdTable = ArchiveImpl.class.getResourceAsStream(Archive.sSIARD2_GENERIC_TABLE_XSD_RESOURCE);
                Document doc = getDocumentBuilder().parse(isXsdTable);

                Element elAny = (Element) doc.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "any")
                        .item(0);
                Element elSequence = (Element) elAny.getParentNode();
                XU.clearElement(elSequence);
                for (int iColumn = 0; iColumn < getMetaTable().getMetaColumns(); iColumn++) {
                    MetaColumn mc = getMetaTable().getMetaColumn(iColumn);
                    String sTag = CellImpl.getColumnTag(iColumn);
                    addElement(elSequence, sTag, mc, mc.isNullable());
                }
                /* write it to stream */
                getTransformer().transform(new DOMSource(doc), new StreamResult(osXsd));
                osXsd.close();
            } catch (SAXException se) {
                throw new IOException(se);
            } catch (TransformerConfigurationException tcfe) {
                throw new IOException(tcfe);
            } catch (TransformerException tfe) {
                throw new IOException(tfe);
            }
        } else
            throw new IOException("Table contains no columns!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        boolean bEmpty = true;
        ArchiveImpl ai = getArchiveImpl();
        if (ai.getZipFile()
                .getFileEntry(getTableXml()) != null)
            bEmpty = false;
        return bEmpty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        boolean bValid = getMetaTable().isValid();
        if (bValid && (getMetaTable().getMetaColumns() == 0))
            bValid = false;
        TableRecordDispenser rd = null;
        try {
            rd = openTableRecords();
            long lRowsValidate = Math.min(lROWS_MAX_VALIDATE, getMetaTable().getRows());
            rd.skip(lRowsValidate);
            if (lRowsValidate == getMetaTable().getRows())
                if (rd.get() != null)
                    bValid = false;
        } catch (IOException ie) {
            bValid = false;
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException ie) {
                }
            }
        }
        return bValid;
    }

    /**
     * constructor for existing table
     *
     * @param schemaParent schema to which this Table instance belongs.
     * @param sName        name of table.
     * @throws IOException if the table cannot be created.
     */
    private TableImpl(Schema schemaParent, String sName)
            throws IOException {
        _schemaParent = schemaParent;
        MetaSchemaImpl msi = (MetaSchemaImpl) getParentSchema().getMetaSchema();
        TablesType tts = msi.getSchemaType()
                .getTables();
        if (tts == null) {
            tts = _OF.createTablesType();
            msi.getSchemaType()
                    .setTables(tts);
        }
        TableType tt = null;
        for (int iTable = 0; (tt == null) && (iTable < tts.getTable()
                .size()); iTable++) {
            TableType ttTry = tts.getTable()
                    .get(iTable);
            if (sName.equals(ttTry.getName()))
                tt = ttTry;
        }
        SchemaImpl si = ((SchemaImpl) getParentSchema());
        if (tt == null) {
            String sFolder = _sTABLE_FOLDER_PREFIX + tts.getTable()
                    .size();
            ArchiveImpl ai = getArchiveImpl();
            ai.createFolderEntry(si.getSchemaFolder() + sFolder + "/");
            tt = MetaTableImpl.createTableType(sName, sFolder);
            tts.getTable()
                    .add(tt);
        }
        _mt = MetaTableImpl.newInstance(this, tt);
        si.registerTable(sName, this);
    }

    /**
     * factory
     *
     * @param schemaParent schema to which the Table instance belongs.
     * @param sName        table name.
     * @return new Table instance.
     * @throws IOException if the table cannot be created.
     */
    public static Table newInstance(Schema schemaParent, String sName)
            throws IOException {
        Table table = new TableImpl(schemaParent, sName);
        return table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableRecordDispenserImpl openTableRecords()
            throws IOException {
        return new TableRecordDispenserImpl(this);
    }

    private boolean _bCreating = false;

    public boolean isCreating() {
        return _bCreating;
    }

    public void setCreating(boolean bCreating) {
        _bCreating = bCreating;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableRecordRetainerImpl createTableRecords()
            throws IOException {
        return new TableRecordRetainerImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableRecordExtract getTableRecordExtract()
            throws IOException {
        TableRecordExtract re = null;
        /* open table XML */
        if (!getArchiveImpl().canModifyPrimaryData())
            re = TableRecordExtractImpl.newInstance(this);
        else
            throw new IOException("Records cannot be read if archive is open for modification!");
        return re;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sort(boolean bAscending, int iSortColumn, Progress progress)
            throws IOException {
        SortedTable stable = _stable;
        if (stable == null)
            stable = new SortedTableImpl();
        stable.sort(this, bAscending, iSortColumn, progress);
        _stable = stable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAscending() {
        boolean bAscending = true;
        if (_stable != null)
            bAscending = _stable.getAscending();
        return bAscending;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSortColumn() {
        int iSortColumn = -1;
        if (_stable != null)
            iSortColumn = _stable.getSortColumn();
        return iSortColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exportAsHtml(OutputStream os, File folderLobs) throws IOException {
        new HtmlExport(getMetaTable()).write(os, folderLobs);
    }
}
