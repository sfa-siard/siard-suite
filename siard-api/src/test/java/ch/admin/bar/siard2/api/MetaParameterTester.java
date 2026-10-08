package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    public void setUp() {
        try {
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            MetaSchema ms = schema.getMetaSchema();
            MetaRoutine mr = ms.createMetaRoutine(_sTEST_ROUTINE_NAME);
            _mpNew = mr.createMetaParameter(_sTEST_PARAMETER_NAME);
            assertSame(mr, _mpNew.getParentMetaRoutine(), "Invalid parent routine!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @AfterEach
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
        assertEquals(_sTEST_PARAMETER_NAME, _mpNew.getName(), "Invalid parameter name!");
    }

    @Test
    public void testMode() {
        assertEquals("IN", _mpNew.getMode(), "Invalid default mode!");
        try {
            _mpNew.setMode("inout");
            assertEquals("INOUT", _mpNew.getMode(), "Wrong mode!");
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
            assertEquals(sType, _mpNew.getType(), "Wrong type!");
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
            assertEquals(sTypeOriginal, _mpNew.getTypeOriginal(), "Invalid original type!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeSchema() {
        assertNull(_mpNew.getTypeSchema(), "Wrong type schema default!");
        String sTypeSchema = _sTEST_SCHEMA_NAME;
        try {
            _mpNew.setType("INTEGER");
            _mpNew.setTypeSchema(sTypeSchema);
            assertEquals(sTypeSchema, _mpNew.getTypeSchema(), "Invalid type schema!");
            assertNull(_mpNew.getType(), "Predefined type was not removed!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTypeName() {
        assertNull(_mpNew.getTypeName(), "Wrong type name default!");
        String sTypeName = _sTEST_TYPE_NAME;
        try {
            _mpNew.setType("INTEGER");
            _mpNew.setTypeName(sTypeName);
            assertEquals(sTypeName, _mpNew.getTypeName(), "Invalid type name!");
            assertNull(_mpNew.getType(), "Predefined type was not removed!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testCardinality() {
        assertEquals(-1, _mpNew.getCardinality(), "Wrong cardinality default!");
        int iCardinality = 5;
        try {
            _mpNew.setType("INTEGER");
            _mpNew.setCardinality(iCardinality);
            assertEquals(iCardinality, _mpNew.getCardinality(), "Invalid cardinality!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mpNew.setDescription(sDescription);
        assertEquals(sDescription, _mpNew.getDescription(), "Invalid description!");
    }

}
