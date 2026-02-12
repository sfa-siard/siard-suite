package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.generated.SiardArchive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.api.primary.MetaDataXml;
import ch.enterag.utils.EU;
import ch.enterag.utils.FU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ArchiveTester {
    private static final File _fileSIARD_10_SOURCE = new File("src/test/resources/testfiles/sql1999.siard");
    private static final File FILE_SIARD_10 = new File("src/test/resources/tmp/sql1999.siard");

    private static final File FILE_SIARD_21 = new File("src/test/resources/testfiles/sql2008_2_1.siard");
    private static final File FILE_SIARD_22 = new File("src/test/resources/testfiles/sql2008.siard");

    private static final File FILE_SIARD_COMPLEX_21 = new File("src/test/resources/testfiles/sfdboe_2_1.siard");
    private static final File FILE_SIARD_COMPLEX_22 = new File("src/test/resources/testfiles/sfdboe.siard");
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final File _fileMETADATA_XML = new File("src/test/resources/tmp/metadata.xml");
    private static final File _fileIMPORT_METADATA_XML = new File("src/test/resources/testfiles/import.xml");
    private static final File _fileMETADATA_XSD_ORIGIN = new File("src/main/resources/res/metadata.xsd");
    private static final File _fileMETADATA_XSD = new File("src/test/resources/tmp/metadata.xsd");
    private static final File _fileTABLE_XSD_ORIGIN = new File("src/main/resources/res/table.xsd");
    private static final File _fileTABLE_XSD = new File("src/test/resources/tmp/table.xsd");

    private static final int _iBUFSIZ = 8192;
    private static final String _sDBNAME = "SIARD 2.2 Test Database";
    private static final String _sTEST_USER_NAME = "TESTUSER";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_TABLE_NAME = "TESTTABLE";
    private static final String _sTEST_COLUMN1_NAME = "CINTEGER";
    private static final String _sTEST_TYPE1_NAME = "BIGINT";
    private static final String _sTEST_COLUMN2_NAME = "CVARCHAR";
    private static final String _sTEST_TYPE2_NAME = "VARCHAR(256)";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";


    @Before
    public void setUp() throws IOException {
        System.out.println("setUp");
        /* make sure, test file does not get clobbered by tests */
        FU.copy(_fileSIARD_10_SOURCE, FILE_SIARD_10);
        if (_fileSIARD_21_NEW.exists())
            _fileSIARD_21_NEW.delete();
        if (_fileMETADATA_XSD.exists())
            _fileMETADATA_XSD.delete();
        if (_fileTABLE_XSD.exists())
            _fileTABLE_XSD.delete();
        if (_fileMETADATA_XML.exists())
            _fileMETADATA_XML.delete();
    }

    @After
    public void tearDown() {
        System.gc();
        System.out.println("Free memory: " + Runtime.getRuntime()
                                                    .freeMemory());
    }

    @Test
    public void shouldOpenArchiveInFormat10() throws IOException {
        // given
        Archive archive = ArchiveImpl.newInstance();

        // when
        archive.open(FILE_SIARD_10);

        // then
        assertSame("Can modify primary data after open!", false, archive.canModifyPrimaryData());
        MetaData md = archive.getMetaData();
        assertEquals(Archive.sMETA_DATA_VERSION_1_0, md.getVersion());
        assertEquals("SQL:1999 Standard Types", md.getDbName());
        archive.close();
    }

    @Test
    public void shouldOpenArchiveInFormatSiard21() throws IOException {
        // given
        Archive archive = ArchiveImpl.newInstance();

        // when
        archive.open(FILE_SIARD_21);

        // then
        assertSame("Can modify primary data after open!", false, archive.canModifyPrimaryData());
        MetaData metaData = archive.getMetaData();
        assertEquals(Archive.sMETA_DATA_VERSION_2_1, metaData.getVersion());
        archive.close();
    }

    @Test
    public void shouldOpenArchiveInFormatSiard22() throws IOException {
        // given
        Archive archive = ArchiveImpl.newInstance();

        // when
        archive.open(FILE_SIARD_22);

        // then
        assertSame("Can modify primary data after open!", false, archive.canModifyPrimaryData());
        MetaData md = archive.getMetaData();
        assertEquals(Archive.sMETA_DATA_VERSION, md.getVersion());
        archive.close();
    }

    @Test
    public void shouldOpenComplexArchiveInFormatSiard22() throws IOException {
        // given
        Archive archive = ArchiveImpl.newInstance();

        // when
        archive.open(FILE_SIARD_COMPLEX_22);

        // then
        assertSame("Can modify primary data after open!", false, archive.canModifyPrimaryData());
        MetaData md = archive.getMetaData();
        assertEquals(Archive.sMETA_DATA_VERSION, md.getVersion());
        assertEquals("(...)", md.getDbName());
        archive.close();
    }

    @Test
    public void shouldOpenComplexArchiveInFormatSiard21() throws IOException {
        // given
        Archive archive = ArchiveImpl.newInstance();

        // when
        archive.open(FILE_SIARD_COMPLEX_21);

        // then
        assertSame("Can modify primary data after open!", false, archive.canModifyPrimaryData());
        MetaData md = archive.getMetaData();
        assertEquals(Archive.sMETA_DATA_VERSION_2_1, md.getVersion());
        assertEquals("(...)", md.getDbName());
        archive.close();
    }

    @Test
    public void testCreate() {
        System.out.println("testCreate");
        Archive archive = ArchiveImpl.newInstance();
        try {
            archive.create(_fileSIARD_21_NEW);
            assertSame("Cannot modify primary data after create!", true, archive.canModifyPrimaryData());
            MetaData md = archive.getMetaData();
            assertEquals("Create failed!", Archive.sMETA_DATA_VERSION, md.getVersion());
            ((ArchiveImpl) archive).getZipFile()
                                   .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testClose() {
        System.out.println("testClose");
        Archive archive = ArchiveImpl.newInstance();
        try {
            archive.create(_fileSIARD_21_NEW);
            setMandatoryMetaData(archive);
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testGetFile() {
        System.out.println("testGetFile");
        Archive archive = ArchiveImpl.newInstance();
        try {
            archive.open(FILE_SIARD_10);
            File file = archive.getFile();
            archive.close();
            assertEquals("Wrong file!", FILE_SIARD_10.getAbsolutePath(), file.getAbsolutePath());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testMaxInlineSize() {
        System.out.println("testMaxInlineSize");
        Archive archive = ArchiveImpl.newInstance();
        try {
            archive.create(_fileSIARD_21_NEW);
            int iMaxInlineSize = Archive.iDEFAULT_MAX_INLINE_SIZE;
            assertEquals("MaxInlineSize has invalid default!", iMaxInlineSize, archive.getMaxInlineSize());
            iMaxInlineSize = 10;
            archive.setMaxInlineSize(iMaxInlineSize);
            assertEquals("MaxInlineSize could not be set!", iMaxInlineSize, archive.getMaxInlineSize());
            setMandatoryMetaData(archive);
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testMaxLobsPerFolder() {
        System.out.println("testMaxLobsPerFolder");
        Archive archive = ArchiveImpl.newInstance();
        try {
            archive.create(_fileSIARD_21_NEW);
            int iMaxLobsPerFolder = -1;
            assertEquals("MaxLobsPerFolder has invalid default!", iMaxLobsPerFolder, archive.getMaxLobsPerFolder());
            iMaxLobsPerFolder = 10;
            archive.setMaxLobsPerFolder(iMaxLobsPerFolder);
            assertEquals("MaxLobsPerFolder could not be set!", iMaxLobsPerFolder, archive.getMaxLobsPerFolder());
            setMandatoryMetaData(archive);
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testExportMetaDataSchema() throws IOException {
        System.out.println("testExportMetaDataSchema");
        Archive archive = ArchiveImpl.newInstance();
        archive.open(FILE_SIARD_10);
        FileOutputStream fos = new FileOutputStream(_fileMETADATA_XSD);
        archive.exportMetaDataSchema(fos);
        fos.close();
        archive.close();
        assertTrue("Exported metadata.xsd is incorrect!", areFilesEqual(_fileMETADATA_XSD_ORIGIN, _fileMETADATA_XSD));
    }

    @Test
    public void testExportGenericTableSchema() {
        System.out.println("testExportGenericTableSchema");
        Archive archive = ArchiveImpl.newInstance();
        try {
            archive.open(FILE_SIARD_10);
            FileOutputStream fos = new FileOutputStream(_fileTABLE_XSD);
            archive.exportGenericTableSchema(fos);
            fos.close();
            archive.close();
            assertTrue("Exported table.xsd is incorrect!", areFilesEqual(_fileTABLE_XSD_ORIGIN, _fileTABLE_XSD));
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testExportMetaData() {
        System.out.println("testExportMetaData");
        Archive archive = ArchiveImpl.newInstance();
        try {
            archive.create(_fileSIARD_21_NEW);
            setMandatoryMetaData(archive);
            FileOutputStream fos = new FileOutputStream(_fileMETADATA_XML);
            archive.exportMetaData(fos);
            fos.close();
            archive.close();
            // read and check exported data
            FileInputStream fis = new FileInputStream(_fileMETADATA_XML);
            SiardArchive sa = MetaDataXml.readSiard22Xml(fis);
            fis.close();
            assertEquals("Dbname not exported correctly!", _sDBNAME, sa.getDbname());
            assertEquals("Data owner not exported correctly!", _sDATA_OWNER, sa.getDataOwner());
            assertEquals("Data origin timespan not exported correctly!", _sDATA_ORIGIN_TIMESPAN, sa.getDataOriginTimespan());
            assertNotNull("Schemas entry was not exported!", sa.getSchemas());
            assertEquals("Schemas not exported correctly!", 1, sa.getSchemas()
                                                                 .getSchema()
                                                                 .size());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testImportMetaDataTemplate() {
        System.out.println("testImportMetaDataSchema");
        /* create archive and import template */
        Archive archive = ArchiveImpl.newInstance();
        try {
            /***
             archive.create(_fileSIARD_21_NEW);
             MetaData md = archive.getMetaData();
             md.setDbName("(...)");
             md.setDataOwner("(...)");
             md.setDataOriginTimespan("(...)");
             ***/
            FileInputStream fis = new FileInputStream(_fileIMPORT_METADATA_XML);
            archive.importMetaDataTemplate(fis);
            fis.close();
            assertTrue("Archive primary data cannot be changed!", archive.canModifyPrimaryData());
            assertTrue("Meta data of archive have been changed!", archive.isMetaDataUnchanged());
            assertFalse("New archive without primary data is valid!", archive.isValid());
            MetaData md = archive.getMetaData();
            md.setLobFolder(null);
            assertEquals("DbName not set correctly!", "SIARD 2.2 Test Database", md.getDbName());
            assertEquals("DataOwner not set correctly!", "Enter AG, Rüti ZH, Switzerland", md.getDataOwner());
            assertEquals("DataOriginTimespan not set correctly!", "Second half of 2016", md.getDataOriginTimespan());
            assertEquals("Wrong number of schemas!", 1, archive.getSchemas());
            assertEquals("Wrong number of meta data schemas!", 1, md.getMetaSchemas());
            MetaSchema ms = md.getMetaSchema(0);
            Schema schema = archive.getSchema(0);
            assertEquals("Wrong number of meta data tables!", 2, ms.getMetaTables());
            assertEquals("Wrong number of tables!", 2, schema.getTables());
            /***
             FileOutputStream fos = new FileOutputStream(_fileMETADATA_XML);
             archive.exportMetaData(fos);
             fos.close();
             ***/
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testIsEmpty() {
        System.out.println("testIsEmpty");
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            assertTrue("New archive is not empty!", archive.isEmpty());
            setMandatoryMetaData(archive);
            archive.close();

            archive = ArchiveImpl.newInstance();
            archive.open(FILE_SIARD_10);
            assertFalse("Old archive is empty!", archive.isEmpty());
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testIsValid() {
        System.out.println("testIsValid");
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            assertFalse("New archive is valid!", archive.isValid());
            setMandatoryMetaData(archive);
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testIsValidOld() {
        System.out.println("testIsValidOld");
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.open(FILE_SIARD_10);
            assertTrue("Old archive is not valid!", archive.isValid());
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    private Table createTable(Schema schema)
            throws IOException {
        Table tab = schema.createTable(_sTEST_TABLE_NAME);
        assertSame("Table create failed!", schema, tab.getParentSchema());

        MetaColumn mc1 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN1_NAME);
        mc1.setType(_sTEST_TYPE1_NAME);
        mc1.setNullable(false);

        MetaColumn mc2 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN2_NAME);
        mc2.setType(_sTEST_TYPE2_NAME);

        return tab;
    } 

    @Test
    public void testIsValidMetaDataOnly() {
        System.out.println("testIsValid");
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            assertFalse("New archive is valid!", archive.isValid());
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            Table tabNew = createTable(schema);
            tabNew.getMetaTable()
                  .setRows(450);
            setMandatoryMetaData(archive);
            archive.close();
            archive.open(_fileSIARD_21_NEW);
            assertFalse("New archive without primary data is valid!", archive.isValid());
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testIsUnchanged() {
        System.out.println("testIsUnchanged");
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            assertFalse("New archive is unchanged!", archive.isPrimaryDataUnchanged());
            setMandatoryMetaData(archive);
            archive.close();

            archive = ArchiveImpl.newInstance();
            archive.open(FILE_SIARD_10);
            assertFalse("Old archive is unchanged!", archive.isPrimaryDataUnchanged());
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testGetSchemas() {
        /* we will have to upgade to JAXB 2.4.0 as soon as it is available!
         * For now we suppress the illegal access warning in a primitive way.
         */
        System.out.println("testGetSchemas");
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            assertEquals("New archive has schemas!", 0, archive.getSchemas());
            setMandatoryMetaData(archive);
            archive.close();

            archive = ArchiveImpl.newInstance();
            archive.open(FILE_SIARD_10);
            assertEquals("Old archive has wrong number of schemas!", 1, archive.getSchemas());
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testGetSchema_Int() {
        System.out.println("testGetSchema_Int");
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.open(FILE_SIARD_10);
            for (int iSchema = 0; iSchema < archive.getSchemas(); iSchema++) {
                Schema schema = archive.getSchema(iSchema);
                System.out.println(schema.getMetaSchema()
                                         .getName());
            }
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testGetSchema_String() {
        System.out.println("testGetSchema_String");
        try {
            String sName = "SIARDSCHEMA";
            Archive archive = ArchiveImpl.newInstance();
            archive.open(FILE_SIARD_10);
            Schema schema = archive.getSchema(sName);
            assertEquals("Schema not retrieved correctly!", sName, schema.getMetaSchema()
                                                                         .getName());
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    @Test
    public void testCreateSchema() {
        System.out.println("testCreateSchema");
        try {
            String sName = "TESTSCHEMA";
            Archive archive = ArchiveImpl.newInstance();
            archive.open(FILE_SIARD_10);
            try {
                archive.createSchema(sName);
                fail("Schema cannot be created in old archive!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            } catch (Exception e) {
                fail(EU.getExceptionMessage(e));
            } finally {
                System.out.println("Closing archive");
                archive.close();
            }

            archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            Schema schema = archive.createSchema(sName);
            assertEquals("Schema not created correctly!", sName, schema.getMetaSchema()
                                                                       .getName());
            setMandatoryMetaData(archive);
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

    private void setMandatoryMetaData(Archive archive) {
        try {
            MetaData md = archive.getMetaData();
            /* mandatory meta data are needed for successful closing */
            md.setDbName(_sDBNAME);
            md.setDataOwner(_sDATA_OWNER);
            md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
            /* at least one schema is mandatory */
            if (md.getMetaSchema(_sTEST_SCHEMA_NAME) == null)
                archive.createSchema(_sTEST_SCHEMA_NAME);
            if (md.getMetaUser(_sTEST_USER_NAME) == null)
                md.createMetaUser(_sTEST_USER_NAME);
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    private boolean areFilesEqual(File file1, File file2)
            throws IOException {
        boolean bEqual = false;
        if (file1.isFile() && file2.isFile()) {
            if (file1.exists() == file2.exists()) {
                bEqual = true;
                if (file1.exists() && file2.exists()) {
                    byte[] buf1 = new byte[_iBUFSIZ];
                    byte[] buf2 = new byte[_iBUFSIZ];
                    FileInputStream fis1 = null;
                    FileInputStream fis2 = null;
                    try {
                        fis1 = new FileInputStream(file1);
                        fis2 = new FileInputStream(file2);
                        int iRead1 = fis1.read(buf1);
                        int iRead2 = fis2.read(buf2);
                        while (bEqual && (iRead1 != -1) && (iRead2 != -1)) {
                            if (iRead1 == iRead2) {
                                if (iRead1 < buf1.length)
                                    buf1 = Arrays.copyOf(buf1, iRead1);
                                if (iRead2 < buf2.length)
                                    buf2 = Arrays.copyOf(buf2, iRead2);
                                bEqual = Arrays.equals(buf1, buf2);
                            } else
                                bEqual = false;
                            iRead1 = fis1.read(buf1);
                            iRead2 = fis2.read(buf2);
                        }
                    } catch (IOException ie) {
                        throw ie;
                    } finally {
                        if (fis1 != null)
                            fis1.close();
                        if (fis2 != null)
                            fis2.close();
                    }
                }
            }
        }
        return bEqual;
    } 
}
