package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
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
            assertSame(0, _mcDistinct.getMetaFields(), "DISTINCT type has no field meta data!");
            _mcRow = mv.createMetaColumn(_sTEST_UDTS_COLUMN_NAME);
            _mcRow.setTypeName(_sTEST_UDTS_TYPE);
            assertSame(3, _mcRow.getMetaFields(), "Invalid number of field meta data of ROW!");

            Table table = schema.createTable(_sTEST_TABLE_NAME);
            MetaTable mt = table.getMetaTable();
            _mcArray = mt.createMetaColumn(_sTEST_ARRAY_COLUMN_NAME);
            _mcArray.setType("VARCHAR(256)");
            _mcArray.setCardinality(4);
            assertSame(0, _mcArray.getMetaFields(), "Invalid number of field meta data of array!");

            _mcUdt = mt.createMetaColumn(_sTEST_UDTC_COLUMN_NAME);
            _mcUdt.setTypeName(_sTEST_UDTC_TYPE);
            assertSame(2, _mcUdt.getMetaFields(), "Invalid number of field meta data of UDT!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (URISyntaxException use) {
            fail(EU.getExceptionMessage(use));
        }
    }

    @AfterEach
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
            assertEquals(_mcRow, mf.getParentMetaColumn(), "Invalid parent column!");
            MetaAttribute ma = mf.getMetaAttribute();
            assertEquals(_sTEST_UDTS_ATTRIBUTE1_NAME, ma.getName(), "Invalid attribute name!");
            assertEquals("INT", ma.getType(), "Invalid field type!");

            mf = _mcRow.getMetaField(1);
            assertEquals(_mcRow, mf.getParentMetaColumn(), "Invalid parent column!");
            ma = mf.getMetaAttribute();
            assertEquals(_sTEST_UDTS_ATTRIBUTE2_NAME, ma.getName(), "Invalid attribute name!");
            assertEquals("CLOB", ma.getType(), "Invalid field type!");

            mf = _mcRow.getMetaField(2);
            assertEquals(_mcRow, mf.getParentMetaColumn(), "Invalid parent column!");
            ma = mf.getMetaAttribute();
            assertEquals(_sTEST_UDTS_ATTRIBUTE3_NAME, ma.getName(), "Invalid attribute name!");
            assertEquals("BLOB", ma.getType(), "Invalid field type!");

            mf = _mcUdt.getMetaField(0);
            assertEquals(_mcUdt, mf.getParentMetaColumn(), "Invalid parent column!");
            ma = mf.getMetaAttribute();
            assertEquals(_sTEST_UDTC_ATTRIBUTE1_NAME, ma.getName(), "Invalid attribute name!");
            assertEquals("INT", ma.getType(), "Invalid field type!");

            mf = _mcUdt.getMetaField(1);
            assertEquals(_mcUdt, mf.getParentMetaColumn(), "Invalid parent column!");
            ma = mf.getMetaAttribute();
            assertEquals(_sTEST_UDTC_ATTRIBUTE2_NAME, ma.getName(), "Invalid attribute name!");
            assertNull(ma.getType(), "Invalid field type!");
            assertEquals(_sTEST_UDTS_TYPE, ma.getTypeName(), "Invalid field type name!");

        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testLobFolder() {
        try {
            MetaField mf = _mcRow.getMetaField(1);
            assertNull(mf.getLobFolder(), "Views do not have stored LOBs!");
            assertNull(mf.getAbsoluteLobFolder(), "Views do not have stored LOBs!");
            try {
                mf.setLobFolder(new URI("lobs"));
                fail("Views do not have stores LOBs and thus no LOB folders!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            } catch (URISyntaxException use) {
                System.out.println(EU.getExceptionMessage(use));
            }

            mf = _mcUdt.getMetaField(0);
            assertNull(mf.getLobFolder(), "Invalid default LOB folder!");
            System.out.println(mf.getAbsoluteLobFolder());
            try {
                String sLobFolder = "lobsUdt/field1/";
                mf.setLobFolder(new URI(sLobFolder));
                assertEquals(sLobFolder, mf.getLobFolder()
                                               .toString(), "");
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
            assertNull(mf.getMimeType(), "Wrong MIME type default!");
            String sMimeType = "image/png";
            mf.setMimeType(sMimeType);
            assertEquals(sMimeType, mf.getMimeType(), "Invalid MIME type!");
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
            assertEquals(sDescription, mf.getDescription(), "Invalid description!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaFields() {
        try {
            MetaField mf = _mcRow.getMetaField(0);
            assertEquals(0, mf.getMetaFields(), "Field or predefined type has no children fields!");
            mf = _mcUdt.getMetaField(1);
            assertEquals(3, mf.getMetaFields(), "Fields of TEST_UDT_TYPE.TEST_UDT_ATTRIBUTE2 type must be 3");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaField() {
        try {
            MetaField mf = _mcArray.getMetaField(1);
            assertSame(2, _mcArray.getMetaFields(), "Invalid number of field meta data of array!");

            mf = _mcArray.getMetaField(_mcArray.getName() + "[1]");
            assertSame(2, _mcArray.getMetaFields(), "Invalid number of field meta data of array!");

            mf = _mcArray.getMetaField(_mcArray.getName() + "[3]");
            assertSame(3, _mcArray.getMetaFields(), "Invalid number of field meta data of array!");

            MetaField mfParent = _mcUdt.getMetaField(1);

            mf = mfParent.getMetaField(0);
            assertNull(mf.getParentMetaColumn(), "Parent is not a column!!");
            assertEquals(mfParent, mf.getParentMetaField(), "Invalid parent field!");
            MetaAttribute ma = mf.getMetaAttribute();
            assertEquals(_sTEST_UDTS_ATTRIBUTE1_NAME, ma.getName(), "Invalid attribute name!");
            assertEquals("INT", ma.getType(), "Invalid field type!");

            mf = mfParent.getMetaField(1);
            assertNull(mf.getParentMetaColumn(), "Parent is not a column!!");
            assertEquals(mfParent, mf.getParentMetaField(), "Invalid parent field!");
            ma = mf.getMetaAttribute();
            assertEquals(_sTEST_UDTS_ATTRIBUTE2_NAME, ma.getName(), "Invalid attribute name!");
            assertEquals("CLOB", ma.getType(), "Invalid field type!");

            mf = mfParent.getMetaField(2);
            assertNull(mf.getParentMetaColumn(), "Parent is not a column!!");
            assertEquals(mfParent, mf.getParentMetaField(), "Invalid parent field!");
            ma = mf.getMetaAttribute();
            assertEquals(_sTEST_UDTS_ATTRIBUTE3_NAME, ma.getName(), "Invalid attribute name!");
            assertEquals("BLOB", ma.getType(), "Invalid field type!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
