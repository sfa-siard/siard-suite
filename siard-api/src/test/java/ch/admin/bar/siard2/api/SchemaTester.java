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
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.*;

public class SchemaTester {
    private static final File _fileSIARD_10_SOURCE = new File("src/test/resources/testfiles/sql1999.siard");
    private static final File _fileSIARD_10 = new File("src/test/resources/tmp/sql1999.siard");
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final ConfigurationProperties _cp = new ConfigurationProperties();
    private static final File _fileLOBS_FOLDER = new File(_cp.getLobsFolder());
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    Schema _schNew = null;
    Schema _schOld = null;

    private void setMandatoryMetaData(MetaData md) {
        try {
            if (!SU.isNotEmpty(md.getDbName()))
                md.setDbName(_sDBNAME);
            if (!SU.isNotEmpty(md.getDataOwner()))
                md.setDataOwner(_sDATA_OWNER);
            if (!SU.isNotEmpty(md.getDataOriginTimespan()))
                md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
            if (md.getMetaSchemas() == 0)
                md.getArchive()
                  .createSchema("TEST_SCHEMA");
            if (_schNew.getTables() != 0) {
                Table table = _schNew.getTable(0);
                MetaTable mt = table.getMetaTable();
                if (mt.getMetaColumns() == 0) {
                    MetaColumn mc = mt.createMetaColumn("TEST_COLUMN");
                    mc.setType("INTEGER");
                }
            }
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
                throw new IOException("deleteFolder only deletes directories!");
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
            _schNew = archive.createSchema(_sTEST_SCHEMA_NAME);
            assertSame("Schema create failed!", archive, _schNew.getParentArchive());
            archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_10);
            _schOld = archive.getSchema(0);
            assertSame("Schema open failed!", archive, _schOld.getParentArchive());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
    public void tearDown() {
        try {
            setMandatoryMetaData(_schNew.getParentArchive()
                                        .getMetaData());
            _schNew.getParentArchive()
                   .close();
            setMandatoryMetaData(_schOld.getParentArchive()
                                        .getMetaData());
            _schOld.getParentArchive()
                   .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaSchema() {
        MetaSchema ms = _schNew.getMetaSchema();
        assertSame("MetaSchema represents wrong schema!", _schNew, ms.getSchema());
        ms = _schOld.getMetaSchema();
        assertSame("MetaSchema represents wrong schema!", _schOld, ms.getSchema());
    }

    @Test
    public void testIsValid() {
        assertFalse("New schema is not valid (no tables)!", _schNew.isValid());
        assertTrue("Old schema must be valid!", _schOld.isValid());
    }

    @Test
    public void testIsEmpty() {
        assertTrue("New schema must be empty (no tables)!", _schNew.isEmpty());
        assertFalse("Old schema is not empty!", _schOld.isEmpty());

    }

    @Test
    public void testGetTables() {
        assertEquals("New schema has tables!", 0, _schNew.getTables());
        assertEquals("Old schema has wrong number of tables!", 1, _schOld.getTables());
    }

    @Test
    public void testGetTable_Int() {
        for (int iTable = 0; iTable < _schOld.getTables(); iTable++) {
            Table table = _schOld.getTable(iTable);
            System.out.println(table.getMetaTable()
                                    .getName());
        }
    }

    @Test
    public void testGetTable_String() {
        String sName = "TABLETEST2";
        Table table = _schOld.getTable(sName);
        assertEquals("Table not retrieved correctly!", sName, table.getMetaTable()
                                                                   .getName());
    }

    @Test
    public void testCreateTable() {
        try {
            String sName = "TESTTABLE";
            Table table = _schNew.createTable(sName);
            /* TODO: add creating a column to the table, when that has been tested! */
            assertEquals("Table not created correctly!", sName, table.getMetaTable()
                                                                     .getName());
            try {
                table = _schOld.createTable(sName);
                fail("Table cannot be created in old archive!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
