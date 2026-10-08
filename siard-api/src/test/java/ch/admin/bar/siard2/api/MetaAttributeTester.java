package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
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
        assertSame(mt, _maNew.getParentMetaType(), "Invalid parent type!");
    }

    @AfterEach
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
        assertEquals(_sTEST_ATTRIBUTE_NAME, _maNew.getName(), "Invalid attribute name!");
    }

    @Test
    public void testType() {
        try {
            String sType = "INT";
            _maNew.setType(sType);
            assertEquals(sType, _maNew.getType(), "Wrong type!");
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
            assertEquals(sTypeOriginal, _maNew.getTypeOriginal(), "Invalid original type!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeSchema() {
        assertNull(_maNew.getTypeSchema(), "Wrong type schema default!");
        String sTypeSchema = _sTEST_SCHEMA_NAME;
        try {
            _maNew.setType("INTEGER");
            _maNew.setTypeSchema(sTypeSchema);
            assertEquals(sTypeSchema, _maNew.getTypeSchema(), "Invalid type schema!");
            assertNull(_maNew.getType(), "Predefined type was not removed!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeName() {
        assertNull(_maNew.getTypeName(), "Wrong type name default!");
        String sTypeName = _sTEST_ATTRIBUTE_TYPE_NAME;
        try {
            _maNew.setType("INTEGER");
            _maNew.setTypeName(sTypeName);
            assertEquals(sTypeName, _maNew.getTypeName(), "Invalid type name!");
            assertNull(_maNew.getType(), "Predefined type was not removed!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testCardinality() {
        assertEquals(-1, _maNew.getCardinality(), "Wrong cardinality default!");
        int iCardinality = 3;
        try {
            _maNew.setType("VARCHAR(256)");
            _maNew.setCardinality(iCardinality);
            assertEquals(iCardinality, _maNew.getCardinality(), "Invalid cardinality!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _maNew.setDescription(sDescription);
        assertEquals(sDescription, _maNew.getDescription(), "Invalid description!");
    }

}
