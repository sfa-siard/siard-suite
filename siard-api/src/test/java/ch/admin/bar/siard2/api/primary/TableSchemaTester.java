package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Types;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class TableSchemaTester {
    private static final File _fileSIARD_SOURCE = new File("src/test/resources/testfiles/sample_source.siard");
    private static final File _fileSIARD_TARGET = new File("src/test/resources/testfiles/sample_target.siard");
    private static final File _fileSIARD_ARCHIVE = new File("src/test/resources/testfiles/sample_archive.siard");
    private static final File _fileLOBS_FOLDER = new File("D:\\Temp\\lobs");
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_SIMPLE_TABLE_NAME = "TESTSIMPLETABLE";
    private static final String _sTEST_COMPLEX_TABLE_NAME = "TESTCOMPLEXTABLE";
    private static final String _sTEST_COLUMN1_NAME = "CCHAR";
    private static final String _sTEST_TYPE1_NAME = "CHAR";
    private static final String _sTEST_COLUMN2_NAME = "CVARCHAR";
    private static final String _sTEST_TYPE2_NAME = "VARCHAR(256)";
    private static final String _sTEST_COLUMN3_NAME = "CCLOB";
    private static final String _sTEST_TYPE3_NAME = "CLOB(4M)";
    private static final String _sTEST_COLUMN4_NAME = "CNCHAR";
    private static final String _sTEST_TYPE4_NAME = "NCHAR";
    private static final String _sTEST_COLUMN5_NAME = "CNCHAR_VARYING";
    private static final String _sTEST_TYPE5_NAME = "NCHAR VARYING(256)";
    private static final String _sTEST_COLUMN6_NAME = "CNCLOB";
    private static final String _sTEST_TYPE6_NAME = "NCLOB(4G)";
    private static final String _sTEST_COLUMN7_NAME = "CXML";
    private static final String _sTEST_TYPE7_NAME = "XML";
    private static final String _sTEST_COLUMN8_NAME = "CBINARY";
    private static final String _sTEST_TYPE8_NAME = "BINARY";
    private static final String _sTEST_COLUMN9_NAME = "CVARBINARY";
    private static final String _sTEST_TYPE9_NAME = "VARBINARY(256)";
    private static final String _sTEST_COLUMN10_NAME = "CBLOB";
    private static final String _sTEST_TYPE10_NAME = "BLOB";
    private static final String _sTEST_COLUMN11_NAME = "CNUMERIC";
    private static final String _sTEST_TYPE11_NAME = "NUMERIC(10,3)";
    private static final String _sTEST_COLUMN12_NAME = "CDECIMAL";
    private static final String _sTEST_TYPE12_NAME = "DECIMAL";
    private static final String _sTEST_COLUMN13_NAME = "CSMALLINT";
    private static final String _sTEST_TYPE13_NAME = "SMALLINT";
    private static final String _sTEST_COLUMN14_NAME = "CINTEGER";
    private static final String _sTEST_TYPE14_NAME = "INTEGER";
    private static final String _sTEST_COLUMN15_NAME = "CBIGINT";
    private static final String _sTEST_TYPE15_NAME = "BIGINT";
    private static final String _sTEST_COLUMN16_NAME = "CFLOAT";
    private static final String _sTEST_TYPE16_NAME = "FLOAT(7)";
    private static final String _sTEST_COLUMN17_NAME = "CREAL";
    private static final String _sTEST_TYPE17_NAME = "REAL";
    private static final String _sTEST_COLUMN18_NAME = "CDOUBLE";
    private static final String _sTEST_TYPE18_NAME = "DOUBLE PRECISION";
    private static final String _sTEST_COLUMN19_NAME = "CBOOLEAN";
    private static final String _sTEST_TYPE19_NAME = "BOOLEAN";
    private static final String _sTEST_COLUMN20_NAME = "CDATE";
    private static final String _sTEST_TYPE20_NAME = "DATE";
    private static final String _sTEST_COLUMN21_NAME = "CTIME";
    private static final String _sTEST_TYPE21_NAME = "TIME(3)";
    private static final String _sTEST_COLUMN22_NAME = "CTIMESTAMP";
    private static final String _sTEST_TYPE22_NAME = "TIMESTAMP(9)";
    private static final String _sTEST_COLUMN23_NAME = "CINTERVALYEAR";
    private static final String _sTEST_TYPE23_NAME = "INTERVAL YEAR(2) TO MONTH";
    private static final String _sTEST_COLUMN24_NAME = "CINTERVALDAY";
    private static final String _sTEST_TYPE24_NAME = "INTERVAL DAY TO MINUTE";
    private static final String _sTEST_COLUMN25_NAME = "CINTERVALSECOND";
    private static final String _sTEST_TYPE25_NAME = "INTERVAL SECOND(2,5)";
    private static final String _sTEST_COLUMN26_NAME = "COLUMN_DATALINK";
    private static final String _sTEST_TYPE26_NAME = "DATALINK";

    private static final String _sTEST_DISTINCT_TYPE = "TDISTINCT";
    private static final String _sTEST_DISTINCT_COLUMN = "CDISTINCT";
    private static final String _sTEST_ROW_TYPE = "TROW";
    private static final String _sTEST_ROW_COLUMN = "CROW";
    private static final String _sTEST_ROW_ATTRIBUTE1_NAME = "TABLEID";
    private static final String _sTEST_ROW_ATTRIBUTE2_NAME = "TRANSCRIPTION";
    private static final String _sTEST_ROW_ATTRIBUTE3_NAME = "SOUND";
    private static final String _sTEST_ROW_ATTRIBUTE4_NAME = "FILE";
    private static final String _sTEST_ARRAY_COLUMN = "CARRAY";
    private static final String _sTEST_UDT_TYPE = "TUDT";
    private static final String _sTEST_UDT_COLUMN = "CUDT";
    private static final String _sTEST_UDT_ATTRIBUTE1_NAME = "ID";
    private static final String _sTEST_UDT_ATTRIBUTE2_NAME = "NESTEDROW";

    Table _tabSimple = null;
    Table _tabComplex = null;
    Table _tabOld = null;

    private void setMandatoryMetaData(Schema schema) {
        MetaData md = schema.getParentArchive()
                            .getMetaData();
        if (!SU.isNotEmpty(md.getDbName()))
            md.setDbName(_sDBNAME);
        if (!SU.isNotEmpty(md.getDataOwner()))
            md.setDataOwner(_sDATA_OWNER);
        if (!SU.isNotEmpty(md.getDataOriginTimespan()))
            md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
    }

    private void deleteFolder(File fileFolder)
            throws IOException {
        if (fileFolder.exists()) {
            if (fileFolder.isDirectory()) {
                File[] afile = fileFolder.listFiles();
                for (int iFile = 0; iFile < afile.length; iFile++) {
                    File file = afile[iFile];
                    if (file.isDirectory())
                        deleteFolder(file);
                    else
                        file.delete();
                }
                fileFolder.delete();
            } else
                throw new IOException("deleteFolder only deletes directories!");
        }
    }

    private void createTypes(MetaSchema ms)
            throws IOException {
        MetaType mtDistinct = ms.createMetaType(_sTEST_DISTINCT_TYPE);
        mtDistinct.setCategory("distinct");
        mtDistinct.setBase("INTEGER");

        MetaType mtRow = ms.createMetaType(_sTEST_ROW_TYPE);
        mtRow.setCategory("udt");
        MetaAttribute mr1 = mtRow.createMetaAttribute(_sTEST_ROW_ATTRIBUTE1_NAME);
        mr1.setType("INTEGER");
        MetaAttribute mr2 = mtRow.createMetaAttribute(_sTEST_ROW_ATTRIBUTE2_NAME);
        mr2.setType("CLOB");
        MetaAttribute mr3 = mtRow.createMetaAttribute(_sTEST_ROW_ATTRIBUTE3_NAME);
        mr3.setType("BLOB");
        MetaAttribute mr4 = mtRow.createMetaAttribute(_sTEST_ROW_ATTRIBUTE4_NAME);
        mr4.setType("DATALINK");

        MetaType mtUdt = ms.createMetaType(_sTEST_UDT_TYPE);
        mtUdt.setCategory("udt");
        MetaAttribute mu1 = mtUdt.createMetaAttribute(_sTEST_UDT_ATTRIBUTE1_NAME);
        mu1.setType("INTEGER");
        MetaAttribute mu2 = mtUdt.createMetaAttribute(_sTEST_UDT_ATTRIBUTE2_NAME);
        mu2.setTypeName(_sTEST_ROW_TYPE);
    } 

    private Table createSimpleTable(Schema schema)
            throws IOException {
        Table tab = schema.createTable(_sTEST_SIMPLE_TABLE_NAME);
        assertSame("Table create failed!", schema, tab.getParentSchema());

        MetaColumn mc1 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN1_NAME);
        mc1.setType(_sTEST_TYPE1_NAME);
        mc1.setNullable(false);

        MetaColumn mc2 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN2_NAME);
        mc2.setType(_sTEST_TYPE2_NAME);

        MetaColumn mc3 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN3_NAME);
        mc3.setType(_sTEST_TYPE3_NAME);

        MetaColumn mc4 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN4_NAME);
        mc4.setType(_sTEST_TYPE4_NAME);

        MetaColumn mc5 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN5_NAME);
        mc5.setType(_sTEST_TYPE5_NAME);

        MetaColumn mc6 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN6_NAME);
        mc6.setType(_sTEST_TYPE6_NAME);

        MetaColumn mc7 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN7_NAME);
        mc7.setType(_sTEST_TYPE7_NAME);

        MetaColumn mc8 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN8_NAME);
        mc8.setType(_sTEST_TYPE8_NAME);

        MetaColumn mc9 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN9_NAME);
        mc9.setType(_sTEST_TYPE9_NAME);

        MetaColumn mc10 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN10_NAME);
        mc10.setType(_sTEST_TYPE10_NAME);

        MetaColumn mc11 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN11_NAME);
        mc11.setType(_sTEST_TYPE11_NAME);

        MetaColumn mc12 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN12_NAME);
        mc12.setType(_sTEST_TYPE12_NAME);

        MetaColumn mc13 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN13_NAME);
        mc13.setType(_sTEST_TYPE13_NAME);

        MetaColumn mc14 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN14_NAME);
        mc14.setType(_sTEST_TYPE14_NAME);
        mc1.setNullable(false);

        MetaColumn mc15 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN15_NAME);
        mc15.setType(_sTEST_TYPE15_NAME);

        MetaColumn mc16 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN16_NAME);
        mc16.setType(_sTEST_TYPE16_NAME);

        MetaColumn mc17 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN17_NAME);
        mc17.setType(_sTEST_TYPE17_NAME);

        MetaColumn mc18 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN18_NAME);
        mc18.setType(_sTEST_TYPE18_NAME);

        MetaColumn mc19 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN19_NAME);
        mc19.setType(_sTEST_TYPE19_NAME);

        MetaColumn mc20 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN20_NAME);
        mc20.setType(_sTEST_TYPE20_NAME);

        MetaColumn mc21 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN21_NAME);
        mc21.setType(_sTEST_TYPE21_NAME);

        MetaColumn mc22 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN22_NAME);
        mc22.setType(_sTEST_TYPE22_NAME);

        MetaColumn mc23 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN23_NAME);
        mc23.setType(_sTEST_TYPE23_NAME);

        MetaColumn mc24 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN24_NAME);
        mc24.setType(_sTEST_TYPE24_NAME);

        MetaColumn mc25 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN25_NAME);
        mc25.setType(_sTEST_TYPE25_NAME);

        MetaColumn mc26 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN26_NAME);
        mc26.setType(_sTEST_TYPE26_NAME);

        return tab;
    } 

    private Table createComplexTable(Schema schema)
            throws IOException {
        Table tab = schema.createTable(_sTEST_COMPLEX_TABLE_NAME);
        assertSame("Table create failed!", schema, tab.getParentSchema());

        MetaColumn mc1 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN14_NAME);
        mc1.setType(_sTEST_TYPE14_NAME);
        mc1.setNullable(false);

        MetaColumn mc2 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_DISTINCT_COLUMN);
        mc2.setTypeName(_sTEST_DISTINCT_TYPE);

        MetaColumn mc3 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_ROW_COLUMN);
        mc3.setTypeName(_sTEST_ROW_TYPE);

        MetaColumn mc4 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_ARRAY_COLUMN);
        mc4.setType("VARCHAR(256)");
        mc4.setCardinality(4);

        MetaColumn mc5 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_UDT_COLUMN);
        mc5.setTypeName(_sTEST_UDT_TYPE);

        return tab;
    } 

    private Table openOldTable(Schema schema) {
        Table tab = schema.getTable(0);
        assertSame("Table open failed!", schema, tab.getParentSchema());
        return tab;
    }

    @Before
    public void setUp() {
        try {
            Files.copy(_fileSIARD_SOURCE.toPath(), _fileSIARD_TARGET.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(_fileSIARD_ARCHIVE.toPath());
            deleteFolder(_fileLOBS_FOLDER);
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_ARCHIVE);
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            createTypes(schema.getMetaSchema());
            _tabSimple = createSimpleTable(schema);
            _tabComplex = createComplexTable(schema);

//      archive = ArchiveImpl.newInstance();
//      archive.open(_fileSIARD_ARCHIVE);
//      schema = archive.getSchema(0);
//      _tabOld = openOldTable(schema);
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
    public void tearDown() {
        try {
            setMandatoryMetaData(_tabSimple.getParentSchema());
            setMandatoryMetaData(_tabComplex.getParentSchema());
            _tabSimple.getParentSchema()
                      .getParentArchive()
                      .close();

//      _tabOld.getParentSchema().getParentArchive().close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

        /**
     * add the <xs:element> meta data definition for the given tag and type
     * to the parent element (<xs:sequence>).
     *
     * @param elParent     parent element (<xs:sequence>).
     * @param sTag         name of element.
     * @param iPreType     predefined type of element or null, if complex type.
     * @param mt           complex type of element or null, if predefined type.
     * @param iCardinality cardinality of element.
     * @param bNullable    true, if element may be missing.
     * @throws IOException if an I/O error occurred.
     */
    private void addElement(Element elParent,
                            String sTag, int iPreType, MetaType mt, int iCardinality, boolean bNullable) {
        Document doc = elParent.getOwnerDocument();
        Element elElement = doc.createElement("xs:element");
        elParent.appendChild(elElement);
        elElement.setAttribute("name", sTag);
        if ((iPreType != Types.NULL) && (iCardinality < 0)) {
            String sXmlType = null;
            switch (iPreType) {
                case Types.CHAR:
                    sXmlType = "xs:string";
                    break;
                case Types.VARCHAR:
                    sXmlType = "xs:string";
                    break;
                case Types.DATALINK:
                    sXmlType = "xs:string";
                    break;
                case Types.CLOB:
                    sXmlType = "clobType";
                    break;
                case Types.NCHAR:
                    sXmlType = "xs:string";
                    break;
                case Types.NVARCHAR:
                    sXmlType = "xs:string";
                    break;
                case Types.NCLOB:
                    sXmlType = "clobType";
                    break;
                case Types.BINARY:
                    sXmlType = "xs:hexBinary";
                    break;
                case Types.VARBINARY:
                    sXmlType = "xs:hexBinary";
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

            Element elComplexType = doc.createElement("xs:complexType");
            elElement.appendChild(elComplexType);

            Element elSequence = doc.createElement("xs:sequence");
            elComplexType.appendChild(elSequence);

            int iSize = iCardinality;
            if (iCardinality < 0)
                iSize = mt.getMetaAttributes();
            for (int iField = 0; iField < iSize; iField++) {
                String sTagField = null;
                int iFieldPreType = iPreType;
                MetaType mtField = mt;
                int iFieldCardinality = -1;
                if (iCardinality >= 0)
                    sTagField = CellImpl.getElementTag(iField);
                else {
                    CategoryType cat = mt.getCategoryType();
                    if (cat == CategoryType.UDT)
                        sTagField = CellImpl.getAttributeTag(iField);
                    MetaAttribute ma = mt.getMetaAttribute(iField);
                    iFieldPreType = ma.getPreType();
                    mtField = ma.getMetaType();
                    iFieldCardinality = ma.getCardinality();
                }
                addElement(elSequence, sTagField, iFieldPreType, mtField, iFieldCardinality, true);
            }
        }
    } 

    private void writeTableXsd(Table table, OutputStream osXsd)
            throws IOException {
        try {
            /* read table template into DOM */
            InputStream isXsdTable = ArchiveImpl.class.getResourceAsStream(Archive.sSIARD2_GENERIC_TABLE_XSD_RESOURCE);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(isXsdTable);

            Element elAny = (Element) doc.getElementsByTagName("xs:any")
                                         .item(0);  // TODO: may be cause of error after java17 upgrade
            Element elSequence = (Element) elAny.getParentNode();
            for (int i = elSequence.getChildNodes()
                                   .getLength() - 1; i >= 0; i--) {
                Node nodeChild = elSequence.getChildNodes()
                                           .item(i);
                elSequence.removeChild(nodeChild);
            }
            for (int iColumn = 0; iColumn < table.getMetaTable()
                                                 .getMetaColumns(); iColumn++) {
                MetaColumn mc = table.getMetaTable()
                                     .getMetaColumn(iColumn);
                String sTag = CellImpl.getColumnTag(iColumn);
                int iPreType = mc.getPreType();
                MetaType mt = mc.getMetaType();
                boolean bNullable = mc.isNullable();
                int iCardinality = mc.getCardinality();
                addElement(elSequence, sTag, iPreType, mt, iCardinality, bNullable);
            }
            /* write DOM to osXsd */
            DOMSource domSrc = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.ENCODING, SU.sUTF8_CHARSET_NAME);
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StreamResult result = new StreamResult(osXsd);
            trans.transform(domSrc, result);
        } catch (ParserConfigurationException pce) {
            fail(EU.getExceptionMessage(pce));
        } catch (SAXException se) {
            fail(EU.getExceptionMessage(se));
        } catch (TransformerConfigurationException tce) {
            fail(EU.getExceptionMessage(tce));
        } catch (TransformerException te) {
            fail(EU.getExceptionMessage(te));
        }
    }

    @Test
    public void testSimple() {
        try {

            File fileTable = new File("src/test/resources/testfiles/table_simple.xsd");
            OutputStream osXsd = new FileOutputStream(fileTable);
            writeTableXsd(_tabSimple, osXsd);
            osXsd.close();

            File fileMetaData = new File("src/test/resources/testfiles/metadata.xml");
            OutputStream osXml = new FileOutputStream(fileMetaData);
            _tabSimple.getParentSchema()
                      .getParentArchive()
                      .exportMetaData(osXml);
            osXml.close();
        } catch (FileNotFoundException fnfe) {
            fail(EU.getExceptionMessage(fnfe));
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testComplex() {
        File file = new File("src/test/resources/testfiles/table_complex.xsd");
        try {
            OutputStream osXsd = new FileOutputStream(file);
            writeTableXsd(_tabComplex, osXsd);
            osXsd.close();
        } catch (FileNotFoundException fnfe) {
            fail(EU.getExceptionMessage(fnfe));
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    /* validate schema XMLs against those */

}
