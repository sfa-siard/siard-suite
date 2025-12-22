package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class MetaAttributeTester {
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_TYPE_NAME = "TESTTYPE";
    private static final String _sTEST_ATTRIBUTE_TYPE_NAME = "TESTATTRTYPE";
    private static final String _sTEST_ATTRIBUTE_NAME = "TESTATTRIBUTE";
    MetaAttribute _maNew = null;
    // TODO: add "old", once we have a full test SIARD file.

    private void setMandatoryMetaData(MetaType mt)
            throws IOException {
        MetaData md = mt.getParentMetaSchema()
                        .getParentMetaData();
        if (!SU.isNotEmpty(md.getDbName()))
            md.setDbName(_sDBNAME);
        if (!SU.isNotEmpty(md.getDataOwner()))
            md.setDataOwner(_sDATA_OWNER);
        if (!SU.isNotEmpty(md.getDataOriginTimespan()))
            md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
        if ((!SU.isNotEmpty(_maNew.getType())) && (!SU.isNotEmpty(_maNew.getTypeName())))
            _maNew.setType("VARCHAR(256)");
    }

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
        Archive archive = ArchiveImpl.newInstance();
        archive.create(_fileSIARD_21_NEW);
        Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
        MetaSchema ms = schema.getMetaSchema();
        MetaType mt = ms.createMetaType(_sTEST_ATTRIBUTE_TYPE_NAME);
        mt.setCategory(CategoryType.DISTINCT.value());
        mt.setBase("INTEGER");
        mt = ms.createMetaType(_sTEST_TYPE_NAME);
        mt.setCategory(CategoryType.UDT.value());
        _maNew = mt.createMetaAttribute(_sTEST_ATTRIBUTE_NAME);
        assertSame("Invalid parent type!", mt, _maNew.getParentMetaType());
    }

    @After
    public void tearDown() throws IOException {
        setMandatoryMetaData(_maNew.getParentMetaType());
        FileOutputStream fosXml = new FileOutputStream("src/test/resources/tmp/table_complex.xml");
        _maNew.getParentMetaType().
              getParentMetaSchema().
              getSchema().
              getParentArchive().
              exportMetaData(fosXml);
        fosXml.close();
        _maNew.getParentMetaType()
              .getParentMetaSchema()
              .getSchema()
              .getParentArchive()
              .close();
    }


    @Test
    public void testName() {
        assertEquals("Invalid attribute name!", _sTEST_ATTRIBUTE_NAME, _maNew.getName());
    }

    @Test
    public void testType() {
        try {
            String sType = "INT";
            _maNew.setType(sType);
            assertEquals("Wrong type!", sType, _maNew.getType());
            try {
                _maNew.setType("GAGA");
                fail("Invalid type GAGA accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeOriginal() {
        try {
            String sTypeOriginal = "TypeOriginal";
            _maNew.setTypeOriginal(sTypeOriginal);
            assertEquals("Invalid original type!", sTypeOriginal, _maNew.getTypeOriginal());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeSchema() {
        assertNull("Wrong type schema default!", _maNew.getTypeSchema());
        String sTypeSchema = _sTEST_SCHEMA_NAME;
        try {
            _maNew.setType("INTEGER");
            _maNew.setTypeSchema(sTypeSchema);
            assertEquals("Invalid type schema!", sTypeSchema, _maNew.getTypeSchema());
            assertNull("Predefined type was not removed!", _maNew.getType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeName() {
        assertNull("Wrong type name default!", _maNew.getTypeName());
        String sTypeName = _sTEST_ATTRIBUTE_TYPE_NAME;
        try {
            _maNew.setType("INTEGER");
            _maNew.setTypeName(sTypeName);
            assertEquals("Invalid type name!", sTypeName, _maNew.getTypeName());
            assertNull("Predefined type was not removed!", _maNew.getType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testCardinality() {
        assertEquals("Wrong cardinality default!", -1, _maNew.getCardinality());
        int iCardinality = 3;
        try {
            _maNew.setType("VARCHAR(256)");
            _maNew.setCardinality(iCardinality);
            assertEquals("Invalid cardinality!", iCardinality, _maNew.getCardinality());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _maNew.setDescription(sDescription);
        assertEquals("Invalid description!", sDescription, _maNew.getDescription());
    }

}
