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
import java.sql.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MetaColumnTester {
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final File _fileSIARD_10_SOURCE = new File("src/test/resources/testfiles/sql1999.siard");
    private static final File _fileSIARD_10 = new File("src/test/resources/tmp/sql1999.siard");
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_TYPE_NAME = "TESTTYPE";
    private static final String _sTEST_VIEW_NAME = "TESTVIEW";
    private static final String _sTEST_COLUMN_NAME = "TESTCOLUMN";
    private static final String _sTEST_DISTINCT_TYPE = "TDISTINCT";
    private static final String _sTEST_UDTS_TYPE = "TUDTS";
    private static final String _sTEST_UDTS_ATTRIBUTE1_NAME = "TABLEID";
    private static final String _sTEST_UDTS_ATTRIBUTE2_NAME = "TRANSCRIPTION";
    private static final String _sTEST_UDTS_ATTRIBUTE3_NAME = "SOUND";
    private static final String _sTEST_UDTC_TYPE = "TUDTC";
    private static final String _sTEST_UDTC_ATTRIBUTE1_NAME = "ID";
    private static final String _sTEST_UDTC_ATTRIBUTE2_NAME = "NESTEDROW";
    MetaColumn _mcNew = null;
    MetaColumn _mcOld = null;

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
        if ((!SU.isNotEmpty(_mcNew.getType())) && (!SU.isNotEmpty(_mcNew.getTypeName())))
            _mcNew.setType("VARCHAR(256)");
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
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            MetaSchema ms = schema.getMetaSchema();
            createTypes(ms);
            MetaView mv = ms.createMetaView(_sTEST_VIEW_NAME);
            _mcNew = mv.createMetaColumn(_sTEST_COLUMN_NAME);
            assertSame(mv, _mcNew.getParentMetaView(), "Invalid parent view!");

            archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_10);
            schema = archive.getSchema(0);
            Table table = schema.getTable(0);
            MetaTable mt = table.getMetaTable();
            _mcOld = mt.getMetaColumn(6);
            assertSame(mt, _mcOld.getParentMetaTable(), "Invalid parent table!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            setMandatoryMetaData(_mcNew.getParentMetaView());
            /***
             FileOutputStream fosXml = new FileOutputStream("tmp/table_complex.xml");
             _mcNew.getParentMetaView().
             getParentMetaSchema().
             getSchema().
             getParentArchive().
             exportMetaData(fosXml);
             fosXml.close();
             ***/
            _mcNew.getParentMetaView()
                  .getParentMetaSchema()
                  .getSchema()
                  .getParentArchive()
                  .close();
            _mcOld.getParentMetaTable()
                  .getTable()
                  .getParentSchema()
                  .getParentArchive()
                  .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }


    @Test
    public void testName() {
        assertEquals(_sTEST_COLUMN_NAME, _mcNew.getName(), "Invalid new column name!");
        assertEquals("CCHARACTER_LARGE_OBJECT", _mcOld.getName(), "Invalid old column name!");
    }

    @Test
    public void testLobFolder() {
        assertNull(_mcNew.getLobFolder(), "Views do not have stored LOBs!");
        assertNull(_mcNew.getAbsoluteLobFolder(), "Views do not have stored LOBs!");
        try {
            _mcNew.setLobFolder(new URI("lobs"));
            fail("Views do not have stores LOBs and thus no LOB folders!");
        } catch (IOException ie) {
            System.out.println(EU.getExceptionMessage(ie));
        } catch (URISyntaxException use) {
            System.out.println(EU.getExceptionMessage(use));
        }

        assertNull(_mcOld.getLobFolder(), "Old table has no externally stored LOBs!");
        assertNull(_mcOld.getAbsoluteLobFolder(), "Old table has no externally stored LOBs!");
    }

    @Test
    public void testType() {
        try {
            String sType = "INT";
            _mcNew.setType(sType);
            assertEquals(sType, _mcNew.getType(), "Wrong type!");
            try {
                _mcNew.setType("GAGA");
                fail("Invalid type GAGA accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }

            sType = "CLOB";
            assertEquals(sType, _mcOld.getType(), "Wrong type of old table!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeOriginal() {
        try {
            String sTypeOriginal = "TypeOriginal";
            _mcNew.setTypeOriginal(sTypeOriginal);
            assertEquals(sTypeOriginal, _mcNew.getTypeOriginal(), "Invalid original type!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }

        assertEquals("CHARACTER LARGE OBJECT", _mcOld.getTypeOriginal(), "Invalid original type of old table!");
    }

    @Test
    public void testNullable() {
        assertTrue(_mcNew.isNullable(), "Wrong nullable default!");
        try {
            _mcNew.setNullable(false);
            fail("Nullability cannot be set for views!");
        } catch (IOException ie) {
            System.out.println(EU.getExceptionMessage(ie));
        }
        assertTrue(_mcOld.isNullable(), "Wrong nullable of old table!");
    }

    @Test
    public void testMimeType() {
        assertNull(_mcNew.getMimeType(), "Wrong MIME type default!");
        String sMimeType = "image/png";
        try {
            _mcNew.setMimeType(sMimeType);
            assertEquals(sMimeType, _mcNew.getMimeType(), "Invalid MIME type!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
        assertNull(_mcOld.getMimeType(), "Old table has MIME type!");
    }

    @Test
    public void testTypeSchema() {
        assertNull(_mcNew.getTypeSchema(), "Wrong type schema default!");
        String sTypeSchema = _sTEST_SCHEMA_NAME;
        try {
            _mcNew.setType("INTEGER");
            _mcNew.setTypeSchema(sTypeSchema);
            assertEquals(sTypeSchema, _mcNew.getTypeSchema(), "Invalid type schema!");
            assertNull(_mcNew.getType(), "Predefined type was not removed!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
        assertNull(_mcOld.getTypeSchema(), "Old table has type schema!");
    }

    @Test
    public void testTypeName() {
        assertNull(_mcNew.getTypeName(), "Wrong type name default!");
        String sTypeName = _sTEST_TYPE_NAME;
        try {
            _mcNew.setType("INTEGER");
            _mcNew.setTypeName(sTypeName);
            assertEquals(sTypeName, _mcNew.getTypeName(), "Invalid type name!");
            assertNull(_mcNew.getType(), "Predefined type was not removed!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
        assertNull(_mcOld.getTypeName(), "Old table has type name!");
    }

    @Test
    public void testCardinality() {
        try {
            assertEquals(-1, _mcNew.getCardinality(), "Wrong cardinality default!");
            int iCardinality = 3;
            _mcNew.setCardinality(iCardinality);
            assertEquals(iCardinality, _mcNew.getCardinality(), "Invalid cardinality!");
            assertTrue(iCardinality >= _mcNew.getMetaFields(), "Invalid meta fields!");
            assertEquals(-1, _mcOld.getCardinality(), "Old table has cardinality!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mcNew.setDescription(sDescription);
        assertEquals(sDescription, _mcNew.getDescription(), "Invalid description!");
    }

    @Test
    public void testGetMetaFields() {
        try {
            assertEquals(0, _mcNew.getMetaFields(), "Fields must initially be 0!");
            assertEquals(0, _mcOld.getMetaFields(), "Fields of old table must be 0!");
            _mcNew.setTypeName(_sTEST_UDTS_TYPE);
            assertEquals(3, _mcNew.getMetaFields(), "Fields of ROW type must be 3");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    private void checkGetNames(MetaColumn mc,
                               boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException {
        System.out.println("Column: " + mc.getName());
        System.out.println("  supports arrays: " + bSupportsArrays);
        System.out.println("  supports udts: " + bSupportsUdts);
        List<List<String>> llNames = mc.getNames(bSupportsArrays, bSupportsUdts);
        for (int iColumn = 0; iColumn < llNames.size(); iColumn++) {
            List<String> listSubNames = llNames.get(iColumn);
            StringBuilder sbNames = new StringBuilder();
            for (int iField = 0; iField < listSubNames.size(); iField++) {
                if (iField > 0)
                    sbNames.append(".");
                sbNames.append(listSubNames.get(iField));
            }
            System.out.println(sbNames);
        }
    }

    @Test
    public void testGetNames() {
        try {
            MetaView mv = _mcNew.getParentMetaView();
            MetaColumn mcArray = mv.createMetaColumn("CARRAY");
            mcArray.setPreType(Types.INTEGER, -1, -1);
            mcArray.setCardinality(3);
            checkGetNames(mcArray, false, true); // 0 array elements
            mcArray.getMetaField(1); // 2 of 3 array elements
            checkGetNames(mcArray, true, true);
            checkGetNames(mcArray, false, true);
            System.out.println();
            MetaColumn mcDistinct = mv.createMetaColumn("CDISTINCT");
            mcDistinct.setTypeName(_sTEST_DISTINCT_TYPE);
            checkGetNames(mcDistinct, true, true);
            System.out.println();
            MetaColumn mcUdt = mv.createMetaColumn("CUDT");
            mcUdt.setTypeName(_sTEST_UDTC_TYPE);
            checkGetNames(mcUdt, true, true);
            checkGetNames(mcUdt, true, false);
            System.out.println();
            MetaColumn mcRow = mv.createMetaColumn("CROW");
            mcRow.setTypeName(_sTEST_UDTS_TYPE);
            checkGetNames(mcRow, true, true);
            checkGetNames(mcRow, true, false);
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    private void checkGetTypes(MetaColumn mc,
                               boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException {
        System.out.println("Column: " + mc.getName());
        System.out.println("  supports arrays: " + bSupportsArrays);
        System.out.println("  supports udts: " + bSupportsUdts);
        List<List<String>> llNames = mc.getNames(bSupportsArrays, bSupportsUdts);
        for (int iColumn = 0; iColumn < llNames.size(); iColumn++) {
            List<String> listSubNames = llNames.get(iColumn);
            StringBuilder sbNames = new StringBuilder();
            for (int iField = 0; iField < listSubNames.size(); iField++) {
                if (iField > 0)
                    sbNames.append(".");
                sbNames.append(listSubNames.get(iField));
            }
            String sType = mc.getType(listSubNames);
            System.out.println(sbNames + ": " + sType);
        }
    }

    @Test
    public void testGetTypes() {
        try {
            MetaView mv = _mcNew.getParentMetaView();
            MetaColumn mcArray = mv.createMetaColumn("CARRAY");
            mcArray.setPreType(Types.INTEGER, -1, -1);
            mcArray.setCardinality(3);
            checkGetTypes(mcArray, false, true); // 0 array elements
            mcArray.getMetaField(1); // 2 of 3 array elements
            checkGetTypes(mcArray, true, true);
            checkGetTypes(mcArray, false, true);
            System.out.println();
            MetaColumn mcDistinct = mv.createMetaColumn("CDISTINCT");
            mcDistinct.setTypeName(_sTEST_DISTINCT_TYPE);
            checkGetTypes(mcDistinct, true, true);
            System.out.println();
            MetaColumn mcUdt = mv.createMetaColumn("CUDT");
            mcUdt.setTypeName(_sTEST_UDTC_TYPE);
            checkGetTypes(mcUdt, true, true);
            checkGetTypes(mcUdt, true, false);
            System.out.println();
            MetaColumn mcRow = mv.createMetaColumn("CROW");
            mcRow.setTypeName(_sTEST_UDTS_TYPE);
            checkGetTypes(mcRow, true, true);
            checkGetTypes(mcRow, true, false);
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
