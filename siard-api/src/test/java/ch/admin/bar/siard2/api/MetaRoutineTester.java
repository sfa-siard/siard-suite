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

public class MetaRoutineTester {
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final File _fileSIARD_SAMPLE = new File("src/test/resources/testfiles/sample.siard");
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_ROUTINE_NAME = "TESTROUTINE";
    private static final String _sTEST_PARAMETER_NAME = "TESTPARAMETER";
    MetaRoutine _mrNew = null;

    private void setMandatoryMetaData(MetaSchema ms) {
        MetaData md = ms.getParentMetaData();
        if (!SU.isNotEmpty(md.getDbName()))
            md.setDbName(_sDBNAME);
        if (!SU.isNotEmpty(md.getDataOwner()))
            md.setDataOwner(_sDATA_OWNER);
        if (!SU.isNotEmpty(md.getDataOriginTimespan()))
            md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
    }

    @Before
    public void setUp() {
        try {
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            MetaSchema ms = schema.getMetaSchema();
            _mrNew = ms.createMetaRoutine(_sTEST_ROUTINE_NAME);
            assertSame("Invalid MetaSchema!", ms, _mrNew.getParentMetaSchema());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
    public void tearDown() {
        try {
            setMandatoryMetaData(_mrNew.getParentMetaSchema());
            _mrNew.getParentMetaSchema()
                  .getSchema()
                  .getParentArchive()
                  .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testName() {
        assertEquals("Invalid routine name!", _sTEST_ROUTINE_NAME, _mrNew.getName());
    }

    @Test
    public void testBody() {
        String sBody = "Body";
        _mrNew.setBody(sBody);
        assertEquals("", sBody, _mrNew.getBody());
    }

    @Test
    public void testSource() {
        try {
            String sSource = "Source";
            _mrNew.setSource(sSource);
            assertEquals("", sSource, _mrNew.getSource());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mrNew.setDescription(sDescription);
        assertEquals("Invalid description!", sDescription, _mrNew.getDescription());
    }

    @Test
    public void testCharacteristic() {
        try {
            String sCharacteristic = "Characteristic";
            _mrNew.setCharacteristic(sCharacteristic);
            assertEquals("Wrong characteristic!", sCharacteristic, _mrNew.getCharacteristic());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testReturnType() {
        try {
            String sReturnType = "ReturnType";
            _mrNew.setReturnType(sReturnType);
            assertEquals("Wrong return type!", sReturnType, _mrNew.getReturnType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaParameters() {
        assertEquals("Parameters must initially be 0!", 0, _mrNew.getMetaParameters());
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_SAMPLE);
            MetaData md = archive.getMetaData();
            MetaSchema ms = md.getMetaSchema(0);
            assertEquals("Wrong number of meta routines!", 2, ms.getMetaRoutines());
            MetaRoutine mr = ms.getMetaRoutine(1);
            assertEquals("Wrong routine!", "compare", mr.getName());
            assertEquals("Wrong number of meta parameters!", 2, mr.getMetaParameters());
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testCreateMetaParameter() {
        try {
            _mrNew.createMetaParameter(_sTEST_PARAMETER_NAME);
            assertEquals("Invalid number of parameters!", 1, _mrNew.getMetaParameters());
            MetaParameter mp = _mrNew.getMetaParameter(0);
            assertEquals("Invalid name!", _sTEST_PARAMETER_NAME, mp.getName());
            mp = _mrNew.getMetaParameter(_sTEST_PARAMETER_NAME);
            assertEquals("Invalid name!", _sTEST_PARAMETER_NAME, mp.getName());
            mp.setType("VARCHAR(256)");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
