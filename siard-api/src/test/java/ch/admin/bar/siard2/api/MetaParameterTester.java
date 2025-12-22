package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class MetaParameterTester {
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_TYPE_NAME = "TESTTYPE";
    private static final String _sTEST_ROUTINE_NAME = "TESTROUTINE";
    private static final String _sTEST_PARAMETER_NAME = "TESTPARAMETER";
    MetaParameter _mpNew = null;
    // TODO: add "old", once we have a full test SIARD file.

    private void setMandatoryMetaData(MetaRoutine mr)
            throws IOException {
        MetaData md = mr.getParentMetaSchema()
                        .getParentMetaData();
        if (!SU.isNotEmpty(md.getDbName()))
            md.setDbName(_sDBNAME);
        if (!SU.isNotEmpty(md.getDataOwner()))
            md.setDataOwner(_sDATA_OWNER);
        if (!SU.isNotEmpty(md.getDataOriginTimespan()))
            md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
        if ((!SU.isNotEmpty(_mpNew.getType())) && (!SU.isNotEmpty(_mpNew.getTypeName())))
            _mpNew.setType("VARCHAR(256)");
    }

    @Before
    public void setUp() {
        try {
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            MetaSchema ms = schema.getMetaSchema();
            MetaRoutine mr = ms.createMetaRoutine(_sTEST_ROUTINE_NAME);
            _mpNew = mr.createMetaParameter(_sTEST_PARAMETER_NAME);
            assertSame("Invalid parent routine!", mr, _mpNew.getParentMetaRoutine());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
    public void tearDown() throws IOException {
        setMandatoryMetaData(_mpNew.getParentMetaRoutine());
        _mpNew.getParentMetaRoutine()
              .getParentMetaSchema()
              .getSchema()
              .getParentArchive()
              .close();
    }


    @Test
    public void testName() {
        assertEquals("Invalid parameter name!", _sTEST_PARAMETER_NAME, _mpNew.getName());
    }

    @Test
    public void testMode() {
        assertEquals("Invalid default mode!", "IN", _mpNew.getMode());
        try {
            _mpNew.setMode("inout");
            assertEquals("Wrong mode!", "INOUT", _mpNew.getMode());
            try {
                _mpNew.setMode("GAGA");
                fail("Invalid mode GAGA accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testType() {
        try {
            String sType = "INT";
            _mpNew.setType(sType);
            assertEquals("Wrong type!", sType, _mpNew.getType());
            try {
                _mpNew.setType("GAGA");
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
            _mpNew.setTypeOriginal(sTypeOriginal);
            assertEquals("Invalid original type!", sTypeOriginal, _mpNew.getTypeOriginal());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeSchema() {
        assertNull("Wrong type schema default!", _mpNew.getTypeSchema());
        String sTypeSchema = _sTEST_SCHEMA_NAME;
        try {
            _mpNew.setType("INTEGER");
            _mpNew.setTypeSchema(sTypeSchema);
            assertEquals("Invalid type schema!", sTypeSchema, _mpNew.getTypeSchema());
            assertNull("Predefined type was not removed!", _mpNew.getType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeName() {
        assertNull("Wrong type name default!", _mpNew.getTypeName());
        String sTypeName = _sTEST_TYPE_NAME;
        try {
            _mpNew.setType("INTEGER");
            _mpNew.setTypeName(sTypeName);
            assertEquals("Invalid type name!", sTypeName, _mpNew.getTypeName());
            assertNull("Predefined type was not removed!", _mpNew.getType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testCardinality() {
        assertEquals("Wrong cardinality default!", -1, _mpNew.getCardinality());
        int iCardinality = 5;
        try {
            _mpNew.setType("INTEGER");
            _mpNew.setCardinality(iCardinality);
            assertEquals("Invalid cardinality!", iCardinality, _mpNew.getCardinality());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } 

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mpNew.setDescription(sDescription);
        assertEquals("Invalid description!", sDescription, _mpNew.getDescription());
    }

}
