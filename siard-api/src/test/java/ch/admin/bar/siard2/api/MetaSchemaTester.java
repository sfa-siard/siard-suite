package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.api.primary.SchemaImpl;
import ch.admin.bar.siard2.api.primary.TableImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.*;

public class MetaSchemaTester {
    private static final File _fileSIARD_10_SOURCE = new File("src/test/resources/testfiles/sql1999.siard");
    private static final File _fileSIARD_10 = new File("src/test/resources/tmp/sql1999.siard");
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final ConfigurationProperties _cp = new ConfigurationProperties();
    private static final File _fileLOBS_FOLDER = new File(_cp.getLobsFolder());
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    MetaSchema _msNew = null;
    MetaSchema _msOld = null;

    private void setMandatoryMetaData(MetaSchema ms) {
        try {
            MetaData md = ms.getParentMetaData();
            if (!SU.isNotEmpty(md.getDbName()))
                md.setDbName(_sDBNAME);
            if (!SU.isNotEmpty(md.getDataOwner()))
                md.setDataOwner(_sDATA_OWNER);
            if (!SU.isNotEmpty(md.getDataOriginTimespan()))
                md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
            if (md.getMetaSchemas() == 0)
                md.getArchive()
                  .createSchema("TEST_SCHEMA");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
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
                throw new IOException("deleteFolder onlye deletes directories!");
        }
    }

    @Before
    public void setUp() {
        try {
            Files.copy(_fileSIARD_10_SOURCE.toPath(), _fileSIARD_10.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            deleteFolder(_fileLOBS_FOLDER);
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            _msNew = schema.getMetaSchema();

            archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_10);
            schema = archive.getSchema(0);
            _msOld = schema.getMetaSchema();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
    public void tearDown() {
        try {
            setMandatoryMetaData(_msNew);
            _msNew.getSchema()
                  .getParentArchive()
                  .close();
            setMandatoryMetaData(_msOld);
            _msOld.getSchema()
                  .getParentArchive()
                  .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testName() {
        assertEquals("Invalid name of new schema!", _sTEST_SCHEMA_NAME, _msNew.getName());
        assertEquals("Invalid name of old schema!", "SIARDSCHEMA", _msOld.getName());
    }

    @Test
    public void testFolder() {
        assertEquals("Invalid folder name of new schema!", SchemaImpl._sSCHEMA_FOLDER_PREFIX + "0", _msNew.getFolder());
        assertEquals("Invalid folder name of old schema!", SchemaImpl._sSCHEMA_FOLDER_PREFIX + "0", _msOld.getFolder());
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _msNew.setDescription(sDescription);
        assertEquals("Invalid Description!", sDescription, _msNew.getDescription());
        _msOld.setDescription(sDescription);
        assertEquals("Invalid Description!", sDescription, _msOld.getDescription());
    }

    @Test
    public void testGetMetaTables() {
        assertEquals("New schema has table meta data!", 0, _msNew.getMetaTables());
        System.out.println(_msOld.getMetaTables());
        assertEquals("Old schema has wrong number of table meta data!", 1, _msOld.getMetaTables());
    }

    @Test
    public void testGetMetaTable_Int() {
        for (int iTable = 0; iTable < _msOld.getMetaTables(); iTable++) {
            MetaTable mt = _msOld.getMetaTable(iTable);
            System.out.println(mt.getName());
            assertEquals("Invalid table folder!", TableImpl._sTABLE_FOLDER_PREFIX + iTable, mt.getFolder());
        }
    }

    @Test
    public void testGetMetaTable_String() {
        String sName = "TABLETEST2";
        MetaTable mt = _msOld.getMetaTable(sName);
        assertEquals("Invalid table name!", sName, mt.getName());
    }

    @Test
    public void testGetMetaViews() {
        assertEquals("New archive has view meta data!", 0, _msNew.getMetaViews());
        System.out.println(_msOld.getMetaViews());
        assertEquals("Old archive has wrong number of view meta data!", 0, _msOld.getMetaViews());
    }

    @Test
    public void testCreateMetaView() {
        try {
            String sName = "METAVIEW";
            MetaView mv = _msNew.createMetaView(sName);
            MetaColumn mc = mv.createMetaColumn("MVCOLUMN");
            mc.setType("INTEGER");
            assertEquals("Wrong number of view meta data!", 1, _msNew.getMetaViews());
            mv = _msNew.getMetaView(0);
            assertEquals("Wrong view name", sName, mv.getName());
            assertSame("Invalid parent meta data of view meta data!", _msNew, mv.getParentMetaSchema());
            mv = _msNew.getMetaView(sName);
            assertEquals("Wrong view name", sName, mv.getName());
            assertSame("Invalid parent meta data of view meta data!", _msNew, mv.getParentMetaSchema());
            try {
                _msOld.createMetaView(sName);
                fail("Views of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaRoutines() {
        assertEquals("New archive has routine meta data!", 0, _msNew.getMetaRoutines());
        System.out.println(_msOld.getMetaRoutines());
        assertEquals("Old archive has wrong number of routine meta data!", 0, _msOld.getMetaRoutines());
    }

    @Test
    public void testCreateMetaRoutine() {
        try {
            String sSpecificName = "METAROUTINE";
            _msNew.createMetaRoutine(sSpecificName);
            assertEquals("Wrong number of routine meta data!", 1, _msNew.getMetaRoutines());
            MetaRoutine mr = _msNew.getMetaRoutine(0);
            assertEquals("Wrong routine name", sSpecificName, mr.getName());
            assertSame("Invalid parent meta data of routine meta data!", _msNew, mr.getParentMetaSchema());
            mr = _msNew.getMetaRoutine(sSpecificName);
            assertEquals("Wrong routine name", sSpecificName, mr.getName());
            assertSame("Invalid parent meta data of routine meta data!", _msNew, mr.getParentMetaSchema());
            try {
                _msOld.createMetaRoutine(sSpecificName);
                fail("Routines of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaTypes() {
        assertEquals("New archive has type meta data!", 0, _msNew.getMetaTypes());
        System.out.println(_msOld.getMetaTypes());
        assertEquals("Old archive has wrong number of type meta data!", 0, _msOld.getMetaTypes());
    }

    @Test
    public void testCreateMetaType() {
        try {
            String sName = "METATYPE";
            _msNew.createMetaType(sName);
            assertEquals("Wrong number of type meta data!", 1, _msNew.getMetaTypes());
            MetaType mt = _msNew.getMetaType(0);
            assertEquals("Wrong routine name", sName, mt.getName());
            assertSame("Invalid parent meta data of type meta data!", _msNew, mt.getParentMetaSchema());
            mt = _msNew.getMetaType(sName);
            assertEquals("Wrong routine name", sName, mt.getName());
            assertSame("Invalid parent meta data of type meta data!", _msNew, mt.getParentMetaSchema());
            try {
                _msOld.createMetaType(sName);
                fail("Types of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
