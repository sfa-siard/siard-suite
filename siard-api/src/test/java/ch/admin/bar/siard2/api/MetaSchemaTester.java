package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.api.primary.SchemaImpl;
import ch.admin.bar.siard2.api.primary.TableImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
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

    @AfterEach
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
        assertEquals(_sTEST_SCHEMA_NAME, _msNew.getName(), "Invalid name of new schema!");
        assertEquals("SIARDSCHEMA", _msOld.getName(), "Invalid name of old schema!");
    }

    @Test
    public void testFolder() {
        assertEquals(SchemaImpl._sSCHEMA_FOLDER_PREFIX + "0", _msNew.getFolder(), "Invalid folder name of new schema!");
        assertEquals(SchemaImpl._sSCHEMA_FOLDER_PREFIX + "0", _msOld.getFolder(), "Invalid folder name of old schema!");
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _msNew.setDescription(sDescription);
        assertEquals(sDescription, _msNew.getDescription(), "Invalid Description!");
        _msOld.setDescription(sDescription);
        assertEquals(sDescription, _msOld.getDescription(), "Invalid Description!");
    }

    @Test
    public void testGetMetaTables() {
        assertEquals(0, _msNew.getMetaTables(), "New schema has table meta data!");
        System.out.println(_msOld.getMetaTables());
        assertEquals(1, _msOld.getMetaTables(), "Old schema has wrong number of table meta data!");
    }

    @Test
    public void testGetMetaTable_Int() {
        for (int iTable = 0; iTable < _msOld.getMetaTables(); iTable++) {
            MetaTable mt = _msOld.getMetaTable(iTable);
            System.out.println(mt.getName());
            assertEquals(TableImpl._sTABLE_FOLDER_PREFIX + iTable, mt.getFolder(), "Invalid table folder!");
        }
    }

    @Test
    public void testGetMetaTable_String() {
        String sName = "TABLETEST2";
        MetaTable mt = _msOld.getMetaTable(sName);
        assertEquals(sName, mt.getName(), "Invalid table name!");
    }

    @Test
    public void testGetMetaViews() {
        assertEquals(0, _msNew.getMetaViews(), "New archive has view meta data!");
        System.out.println(_msOld.getMetaViews());
        assertEquals(0, _msOld.getMetaViews(), "Old archive has wrong number of view meta data!");
    }

    @Test
    public void testCreateMetaView() {
        try {
            String sName = "METAVIEW";
            MetaView mv = _msNew.createMetaView(sName);
            MetaColumn mc = mv.createMetaColumn("MVCOLUMN");
            mc.setType("INTEGER");
            assertEquals(1, _msNew.getMetaViews(), "Wrong number of view meta data!");
            mv = _msNew.getMetaView(0);
            assertEquals(sName, mv.getName(), "Wrong view name");
            assertSame(_msNew, mv.getParentMetaSchema(), "Invalid parent meta data of view meta data!");
            mv = _msNew.getMetaView(sName);
            assertEquals(sName, mv.getName(), "Wrong view name");
            assertSame(_msNew, mv.getParentMetaSchema(), "Invalid parent meta data of view meta data!");
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
        assertEquals(0, _msNew.getMetaRoutines(), "New archive has routine meta data!");
        System.out.println(_msOld.getMetaRoutines());
        assertEquals(0, _msOld.getMetaRoutines(), "Old archive has wrong number of routine meta data!");
    }

    @Test
    public void testCreateMetaRoutine() {
        try {
            String sSpecificName = "METAROUTINE";
            _msNew.createMetaRoutine(sSpecificName);
            assertEquals(1, _msNew.getMetaRoutines(), "Wrong number of routine meta data!");
            MetaRoutine mr = _msNew.getMetaRoutine(0);
            assertEquals(sSpecificName, mr.getName(), "Wrong routine name");
            assertSame(_msNew, mr.getParentMetaSchema(), "Invalid parent meta data of routine meta data!");
            mr = _msNew.getMetaRoutine(sSpecificName);
            assertEquals(sSpecificName, mr.getName(), "Wrong routine name");
            assertSame(_msNew, mr.getParentMetaSchema(), "Invalid parent meta data of routine meta data!");
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
        assertEquals(0, _msNew.getMetaTypes(), "New archive has type meta data!");
        System.out.println(_msOld.getMetaTypes());
        assertEquals(0, _msOld.getMetaTypes(), "Old archive has wrong number of type meta data!");
    }

    @Test
    public void testCreateMetaType() {
        try {
            String sName = "METATYPE";
            _msNew.createMetaType(sName);
            assertEquals(1, _msNew.getMetaTypes(), "Wrong number of type meta data!");
            MetaType mt = _msNew.getMetaType(0);
            assertEquals(sName, mt.getName(), "Wrong routine name");
            assertSame(_msNew, mt.getParentMetaSchema(), "Invalid parent meta data of type meta data!");
            mt = _msNew.getMetaType(sName);
            assertEquals(sName, mt.getName(), "Wrong routine name");
            assertSame(_msNew, mt.getParentMetaSchema(), "Invalid parent meta data of type meta data!");
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
