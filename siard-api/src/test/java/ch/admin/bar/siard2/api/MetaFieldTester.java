package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.*;

public class MetaFieldTester {
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final File _fileSIARD_10_SOURCE = new File("src/test/resources/testfiles/sql1999.siard");
    private static final File _fileSIARD_10 = new File("src/test/resources/tmp/sql1999.siard");
    private static final ConfigurationProperties _cp = new ConfigurationProperties();
    private static final File _fileLOBS_FOLDER = new File(_cp.getLobsFolder());
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_VIEW_NAME = "TESTVIEW";
    private static final String _sTEST_TABLE_NAME = "TESTTABLE";
    private static final String _sTEST_DISTINCT_COLUMN_NAME = "TESTDISTINCTCOLUMN";
    private static final String _sTEST_UDTS_COLUMN_NAME = "TESTUDTSCOLUMN";
    private static final String _sTEST_ARRAY_COLUMN_NAME = "TESTARRAYCOLUMN";
    private static final String _sTEST_UDTC_COLUMN_NAME = "TESTUDTCCOLUMN";
    private static final String _sTEST_DISTINCT_TYPE = "TDISTINCT";
    private static final String _sTEST_UDTS_TYPE = "TUDTS";
    private static final String _sTEST_UDTS_ATTRIBUTE1_NAME = "TABLEID";
    private static final String _sTEST_UDTS_ATTRIBUTE2_NAME = "TRANSCRIPTION";
    private static final String _sTEST_UDTS_ATTRIBUTE3_NAME = "SOUND";
    private static final String _sTEST_UDTC_TYPE = "TUDTC";
    private static final String _sTEST_UDTC_ATTRIBUTE1_NAME = "ID";
    private static final String _sTEST_UDTC_ATTRIBUTE2_NAME = "NESTEDROW";
    MetaColumn _mcDistinct = null;
    MetaColumn _mcRow = null;
    MetaColumn _mcArray = null;
    MetaColumn _mcUdt = null;

    private void setMandatoryMetaData(MetaView mv)
            throws IOException {
        MetaData md = mv.getParentMetaSchema()
                        .getParentMetaData();
        if (!SU.isNotEmpty(md.getDbName()))
            md.setDbName(_sDBNAME);
        if (!SU.isNotEmpty(md.getDataOwner()))
            md.setDataOwner(_sDATA_OWNER);
        if (!SU.isNotEmpty(md.getDataOriginTimespan()))
            md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
    }

    private void createTypes(MetaSchema ms)
            throws IOException {
        MetaType mtDistinct = ms.createMetaType(_sTEST_DISTINCT_TYPE);
        mtDistinct.setCategory("distinct");
        mtDistinct.setBase("INTEGER");

        MetaType mtRow = ms.createMetaType(_sTEST_UDTS_TYPE);
        mtRow.setCategory("udt");
        MetaAttribute mr1 = mtRow.createMetaAttribute(_sTEST_UDTS_ATTRIBUTE1_NAME);
        mr1.setType("INTEGER");
        MetaAttribute mr2 = mtRow.createMetaAttribute(_sTEST_UDTS_ATTRIBUTE2_NAME);
        mr2.setType("CLOB");
        MetaAttribute mr3 = mtRow.createMetaAttribute(_sTEST_UDTS_ATTRIBUTE3_NAME);
        mr3.setType("BLOB");

        MetaType mtUdt = ms.createMetaType(_sTEST_UDTC_TYPE);
        mtUdt.setCategory("udt");
        MetaAttribute mu1 = mtUdt.createMetaAttribute(_sTEST_UDTC_ATTRIBUTE1_NAME);
        mu1.setType("INTEGER");
        MetaAttribute mu2 = mtUdt.createMetaAttribute(_sTEST_UDTC_ATTRIBUTE2_NAME);
        mu2.setTypeName(_sTEST_UDTS_TYPE);
    }

    @Before
    public void setUp() {
        try {
            Files.copy(_fileSIARD_10_SOURCE.toPath(), _fileSIARD_10.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            MetaData mdNew = archive.getMetaData();
            URI uriLobFolder = new URI(_fileLOBS_FOLDER.toURI() + "/");
            mdNew.setLobFolder(uriLobFolder);
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            MetaSchema ms = schema.getMetaSchema();
            createTypes(ms);

            MetaView mv = ms.createMetaView(_sTEST_VIEW_NAME);
            _mcDistinct = mv.createMetaColumn(_sTEST_DISTINCT_COLUMN_NAME);
            _mcDistinct.setTypeName(_sTEST_DISTINCT_TYPE);
            assertSame("DISTINCT type has no field meta data!", 0, _mcDistinct.getMetaFields());
            _mcRow = mv.createMetaColumn(_sTEST_UDTS_COLUMN_NAME);
            _mcRow.setTypeName(_sTEST_UDTS_TYPE);
            assertSame("Invalid number of field meta data of ROW!", 3, _mcRow.getMetaFields());

            Table table = schema.createTable(_sTEST_TABLE_NAME);
            MetaTable mt = table.getMetaTable();
            _mcArray = mt.createMetaColumn(_sTEST_ARRAY_COLUMN_NAME);
            _mcArray.setType("VARCHAR(256)");
            _mcArray.setCardinality(4);
            assertSame("Invalid number of field meta data of array!", 0, _mcArray.getMetaFields());

            _mcUdt = mt.createMetaColumn(_sTEST_UDTC_COLUMN_NAME);
            _mcUdt.setTypeName(_sTEST_UDTC_TYPE);
            assertSame("Invalid number of field meta data of UDT!", 2, _mcUdt.getMetaFields());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (URISyntaxException use) {
            fail(EU.getExceptionMessage(use));
        }
    }

    @After
    public void tearDown() {
        try {
            setMandatoryMetaData(_mcDistinct.getParentMetaView());
            _mcDistinct.getParentMetaView()
                       .getParentMetaSchema()
                       .getSchema()
                       .getParentArchive()
                       .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }


    @Test
    public void testGetMetaAttribute() {
        try {
            MetaField mf = _mcRow.getMetaField(0);
            assertEquals("Invalid parent column!", _mcRow, mf.getParentMetaColumn());
            MetaAttribute ma = mf.getMetaAttribute();
            assertEquals("Invalid attribute name!", _sTEST_UDTS_ATTRIBUTE1_NAME, ma.getName());
            assertEquals("Invalid field type!", "INT", ma.getType());

            mf = _mcRow.getMetaField(1);
            assertEquals("Invalid parent column!", _mcRow, mf.getParentMetaColumn());
            ma = mf.getMetaAttribute();
            assertEquals("Invalid attribute name!", _sTEST_UDTS_ATTRIBUTE2_NAME, ma.getName());
            assertEquals("Invalid field type!", "CLOB", ma.getType());

            mf = _mcRow.getMetaField(2);
            assertEquals("Invalid parent column!", _mcRow, mf.getParentMetaColumn());
            ma = mf.getMetaAttribute();
            assertEquals("Invalid attribute name!", _sTEST_UDTS_ATTRIBUTE3_NAME, ma.getName());
            assertEquals("Invalid field type!", "BLOB", ma.getType());

            mf = _mcUdt.getMetaField(0);
            assertEquals("Invalid parent column!", _mcUdt, mf.getParentMetaColumn());
            ma = mf.getMetaAttribute();
            assertEquals("Invalid attribute name!", _sTEST_UDTC_ATTRIBUTE1_NAME, ma.getName());
            assertEquals("Invalid field type!", "INT", ma.getType());

            mf = _mcUdt.getMetaField(1);
            assertEquals("Invalid parent column!", _mcUdt, mf.getParentMetaColumn());
            ma = mf.getMetaAttribute();
            assertEquals("Invalid attribute name!", _sTEST_UDTC_ATTRIBUTE2_NAME, ma.getName());
            assertNull("Invalid field type!", ma.getType());
            assertEquals("Invalid field type name!", _sTEST_UDTS_TYPE, ma.getTypeName());

        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testLobFolder() {
        try {
            MetaField mf = _mcRow.getMetaField(1);
            assertNull("Views do not have stored LOBs!", mf.getLobFolder());
            assertNull("Views do not have stored LOBs!", mf.getAbsoluteLobFolder());
            try {
                mf.setLobFolder(new URI("lobs"));
                fail("Views do not have stores LOBs and thus no LOB folders!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            } catch (URISyntaxException use) {
                System.out.println(EU.getExceptionMessage(use));
            }

            mf = _mcUdt.getMetaField(0);
            assertNull("Invalid default LOB folder!", mf.getLobFolder());
            System.out.println(mf.getAbsoluteLobFolder());
            try {
                String sLobFolder = "lobsUdt/field1/";
                mf.setLobFolder(new URI(sLobFolder));
                assertEquals("", sLobFolder, mf.getLobFolder()
                                               .toString());
                System.out.println(mf.getAbsoluteLobFolder());
            } catch (URISyntaxException use) {
                System.out.println(EU.getExceptionMessage(use));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testMimeType() {
        try {
            MetaField mf = _mcRow.getMetaField(2);
            assertNull("Wrong MIME type default!", mf.getMimeType());
            String sMimeType = "image/png";
            mf.setMimeType(sMimeType);
            assertEquals("Invalid MIME type!", sMimeType, mf.getMimeType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDescription() {
        try {
            MetaField mf = _mcRow.getMetaField(0);
            String sDescription = "Description";
            mf.setDescription(sDescription);
            assertEquals("Invalid description!", sDescription, mf.getDescription());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaFields() {
        try {
            MetaField mf = _mcRow.getMetaField(0);
            assertEquals("Field or predefined type has no children fields!", 0, mf.getMetaFields());
            mf = _mcUdt.getMetaField(1);
            assertEquals("Fields of TEST_UDT_TYPE.TEST_UDT_ATTRIBUTE2 type must be 3", 3, mf.getMetaFields());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaField() {
        try {
            MetaField mf = _mcArray.getMetaField(1);
            assertSame("Invalid number of field meta data of array!", 2, _mcArray.getMetaFields());

            mf = _mcArray.getMetaField(_mcArray.getName() + "[1]");
            assertSame("Invalid number of field meta data of array!", 2, _mcArray.getMetaFields());

            mf = _mcArray.getMetaField(_mcArray.getName() + "[3]");
            assertSame("Invalid number of field meta data of array!", 3, _mcArray.getMetaFields());

            MetaField mfParent = _mcUdt.getMetaField(1);

            mf = mfParent.getMetaField(0);
            assertNull("Parent is not a column!!", mf.getParentMetaColumn());
            assertEquals("Invalid parent field!", mfParent, mf.getParentMetaField());
            MetaAttribute ma = mf.getMetaAttribute();
            assertEquals("Invalid attribute name!", _sTEST_UDTS_ATTRIBUTE1_NAME, ma.getName());
            assertEquals("Invalid field type!", "INT", ma.getType());

            mf = mfParent.getMetaField(1);
            assertNull("Parent is not a column!!", mf.getParentMetaColumn());
            assertEquals("Invalid parent field!", mfParent, mf.getParentMetaField());
            ma = mf.getMetaAttribute();
            assertEquals("Invalid attribute name!", _sTEST_UDTS_ATTRIBUTE2_NAME, ma.getName());
            assertEquals("Invalid field type!", "CLOB", ma.getType());

            mf = mfParent.getMetaField(2);
            assertNull("Parent is not a column!!", mf.getParentMetaColumn());
            assertEquals("Invalid parent field!", mfParent, mf.getParentMetaField());
            ma = mf.getMetaAttribute();
            assertEquals("Invalid attribute name!", _sTEST_UDTS_ATTRIBUTE3_NAME, ma.getName());
            assertEquals("Invalid field type!", "BLOB", ma.getType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
