package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.generated.MessageDigestType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.api.primary.SchemaImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.EU;
import ch.enterag.utils.FU;
import ch.enterag.utils.SU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;

public class MetaDataTester {
    private static final File _fileSIARD_10_SOURCE = new File("src/test/resources/testfiles/sql1999.siard");
    private static final File _fileSIARD_10 = new File("src/test/resources/tmp/sql1999.siard");
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final ConfigurationProperties _cp = new ConfigurationProperties();
    private static final File _fileLOBS_FOLDER = new File(_cp.getLobsFolder());
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final DU _du = DU.getInstance("en", "dd.MM.yyyy");
    MetaData _mdNew = null;
    MetaData _mdOld = null;

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
            _mdNew = archive.getMetaData();
            archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_10);
            _mdOld = archive.getMetaData();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
    public void tearDown() {
        try {
            setMandatoryMetaData(_mdNew);
            _mdNew.getArchive()
                  .close();
            setMandatoryMetaData(_mdOld);
            _mdOld.getArchive()
                  .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testVersion() {
        assertEquals("Wrong version!", Archive.sMETA_DATA_VERSION, _mdNew.getVersion());
        assertEquals("Wrong version!", Archive.sMETA_DATA_VERSION_1_0, _mdOld.getVersion());
    }

    @Test
    public void testDbname() {
        String sDbname = "Dbname";
        _mdNew.setDbName(sDbname);
        assertEquals("Invalid Dbname!", sDbname, _mdNew.getDbName());
        _mdOld.setDbName(sDbname);
        assertEquals("Invalid Dbname!", sDbname, _mdOld.getDbName());
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mdNew.setDescription(sDescription);
        assertEquals("Invalid Description!", sDescription, _mdNew.getDescription());
        _mdOld.setDescription(sDescription);
        assertEquals("Invalid Description!", sDescription, _mdOld.getDescription());
    }

    @Test
    public void testArchiver() {
        String sArchiver = "Archiver";
        _mdNew.setArchiver(sArchiver);
        assertEquals("Invalid Archiver!", sArchiver, _mdNew.getArchiver());
        _mdOld.setArchiver(sArchiver);
        assertEquals("Invalid Archiver!", sArchiver, _mdOld.getArchiver());
    }

    @Test
    public void testArchiverContact() {
        String sArchiverContact = "ArchiverContact";
        _mdNew.setArchiverContact(sArchiverContact);
        assertEquals("Invalid ArchiverContact!", sArchiverContact, _mdNew.getArchiverContact());
        _mdOld.setArchiverContact(sArchiverContact);
        assertEquals("Invalid ArchiverContact!", sArchiverContact, _mdOld.getArchiverContact());
    }

    @Test
    public void testDataOwner() {
        String sDataOwner = "DataOwner";
        _mdNew.setDataOwner(sDataOwner);
        assertEquals("Invalid DataOwner!", sDataOwner, _mdNew.getDataOwner());
        _mdOld.setDataOwner(sDataOwner);
        assertEquals("Invalid DataOwner!", sDataOwner, _mdOld.getDataOwner());
    }

    @Test
    public void testDataOriginTimespan() {
        String sDataOriginTimespan = "DataOriginTimespan";
        _mdNew.setDataOriginTimespan(sDataOriginTimespan);
        assertEquals("Invalid DataOriginTimespan!", sDataOriginTimespan, _mdNew.getDataOriginTimespan());
        _mdOld.setDataOriginTimespan(sDataOriginTimespan);
        assertEquals("Invalid DataOriginTimespan!", sDataOriginTimespan, _mdOld.getDataOriginTimespan());
    }

    @Test
    public void testLobFolder() {
        try {
            URI uriLobFolder = new URI(_fileLOBS_FOLDER.toURI() + "/");
            _mdNew.setLobFolder(uriLobFolder);
            assertEquals("Invalid LobFolder!", uriLobFolder, _mdNew.getLobFolder());
            File file = FU.fromUri(_mdNew.getAbsoluteLobFolder());
            assertEquals("Wrong absolute folder!", _fileLOBS_FOLDER.getAbsolutePath(), file.getAbsolutePath());
            try {
                _mdOld.setLobFolder(uriLobFolder);
                fail("LobFolder of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (URISyntaxException use) {
            fail(EU.getExceptionMessage(use));
        }
    }

    @Test
    public void testProducerApplication() {
        try {
            String sProducerApplication = "ProducerApplication";
            _mdNew.setProducerApplication(sProducerApplication);
            assertEquals("Invalid ProducerApplication!", sProducerApplication, _mdNew.getProducerApplication());
            try {
                _mdOld.setProducerApplication(sProducerApplication);
                fail("ProducerApplication of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testArchivalDate() {
        GregorianCalendar gc = new GregorianCalendar();
        String sToday = _du.fromGregorianCalendar(gc);
        gc = (GregorianCalendar) _mdNew.getArchivalDate();
        assertEquals("Archival date not stored as UTC!", "GMT+00:00", gc.getTimeZone()
                                                                        .getDisplayName());
        assertEquals("Wrong date!", sToday, _du.fromGregorianCalendar(gc));
    }

    @Test
    public void testMessageDigest() {
        List<MessageDigestType> listDigest = _mdNew.getMessageDigest();
        assertEquals("New archive has message digest!", 0, listDigest.size());
        listDigest = _mdOld.getMessageDigest();
        for (int iDigest = 0; iDigest < listDigest.size(); iDigest++) {
            MessageDigestType md = listDigest.get(iDigest);
            System.out.println(md.getDigestType()
                                 .value() + md.getDigest());
        }
    }

    @Test
    public void testClientMachine() {
        try {
            String sClientMachine = "ClientMachine";
            _mdNew.setClientMachine(sClientMachine);
            assertEquals("Invalid ClientMachine!", sClientMachine, _mdNew.getClientMachine());
            try {
                _mdOld.setClientMachine(sClientMachine);
                fail("ClientMachine of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDatabaseProduct() {
        try {
            String sDatabaseProduct = "DatabaseProduct";
            _mdNew.setDatabaseProduct(sDatabaseProduct);
            assertEquals("Invalid DatabaseProduct!", sDatabaseProduct, _mdNew.getDatabaseProduct());
            try {
                _mdOld.setDatabaseProduct(sDatabaseProduct);
                fail("DatabaseProduct of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testConnection() {
        try {
            String sConnection = "Connection";
            _mdNew.setConnection(sConnection);
            assertEquals("Invalid Connection!", sConnection, _mdNew.getConnection());
            try {
                _mdOld.setConnection(sConnection);
                fail("Connection of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDatabaseUser() {
        try {
            String sDatabaseUser = "DatabaseUser";
            _mdNew.setDatabaseUser(sDatabaseUser);
            assertEquals("Invalid DatabaseUser!", sDatabaseUser, _mdNew.getDatabaseUser());
            try {
                _mdOld.setDatabaseUser(sDatabaseUser);
                fail("DatabaseUser of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaSchemas() {
        assertEquals("New archive has schema meta data!", 0, _mdNew.getMetaSchemas());
        assertEquals("Old archive has wrong number of schema meta data!", 1, _mdOld.getMetaSchemas());
    }

    @Test
    public void testGetMetaSchema_Int() {
        for (int iSchema = 0; iSchema < _mdOld.getMetaSchemas(); iSchema++) {
            MetaSchema ms = _mdOld.getMetaSchema(iSchema);
            assertEquals("Invalid schema folder!", SchemaImpl._sSCHEMA_FOLDER_PREFIX + iSchema, ms.getFolder());

        }
    }

    @Test
    public void testGetMetaSchema_String() {
        String sName = "SIARDSCHEMA";
        String sDescription = "Description";
        MetaSchema ms = _mdOld.getMetaSchema(sName);
        assertEquals("Invalid schema name!", sName, ms.getName());
        ms.setDescription(sDescription);
        assertEquals("Wrong schema description!", sDescription, ms.getDescription());
    }

    @Test
    public void testGetMetaUsers() {
        assertEquals("New archive has user meta data!", 0, _mdNew.getMetaUsers());
        System.out.println(_mdOld.getMetaUsers());
        assertEquals("Old archive has wrong number of user meta data!", 2, _mdOld.getMetaUsers());
    }

    @Test
    public void testGetMetaUser_Int() {
        for (int iUser = 0; iUser < _mdOld.getMetaUsers(); iUser++) {
            MetaUser mu = _mdOld.getMetaUser(iUser);
            assertSame("Invalid parent meta data of user meta data!", _mdOld, mu.getParentMetaData());
            System.out.println(mu.getName());
        }
    }

    @Test
    public void testGetMetaUser_String() {
        String sName = "SIARDUSER";
        String sDescription = "Description";
        MetaUser mu = _mdOld.getMetaUser(sName);
        assertEquals("Wrong user name!", sName, mu.getName());
        mu.setDescription(sDescription);
        assertEquals("Wrong user description!", sDescription, mu.getDescription());
    }

    @Test
    public void testCreateMetaUser() {
        try {
            String sName = "METAUSER";
            _mdNew.createMetaUser(sName);
            assertEquals("Wrong number of user meta data!", 1, _mdNew.getMetaUsers());
            MetaUser mu = _mdNew.getMetaUser(0);
            assertEquals("Wrong user name", sName, mu.getName());
            assertSame("Invalid parent meta data of user meta data!", _mdNew, mu.getParentMetaData());
            try {
                _mdOld.createMetaUser(sName);
                fail("Users of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaRoles() {
        assertEquals("New archive has role meta data!", 0, _mdNew.getMetaRoles());
        System.out.println(_mdOld.getMetaRoles());
        assertEquals("Old archive has wrong number of role meta data!", 1, _mdOld.getMetaRoles());
    }

    @Test
    public void testGetMetaRole_Int() {
        for (int iRole = 0; iRole < _mdOld.getMetaRoles(); iRole++) {
            MetaRole mr = _mdOld.getMetaRole(iRole);
            assertSame("Invalid parent meta data of role meta data!", _mdOld, mr.getParentMetaData());
            System.out.println(mr.getName() + "/" + mr.getAdmin() + ".");
        }
    }

    @Test
    public void testGetMetaRole_String() {
        String sName = "public";
        String sDescription = "Description";
        MetaRole mr = _mdOld.getMetaRole(sName);
        assertEquals("Wrong role name!", sName, mr.getName());
        assertEquals("", "", mr.getAdmin());
        mr.setDescription(sDescription);
        assertEquals("Wrong role description!", sDescription, mr.getDescription());
    }

    @Test
    public void testCreateMetaRole() {
        try {
            String sName = "METAROLE";
            String sAdmin = "ROLEADMIN";
            _mdNew.createMetaRole(sName, sAdmin);
            assertEquals("Wrong number of role meta data!", 1, _mdNew.getMetaRoles());
            MetaRole mr = _mdNew.getMetaRole(0);
            assertEquals("Wrong role name", sName, mr.getName());
            assertEquals("Wrong role admin", sAdmin, mr.getAdmin());
            assertSame("Invalid parent meta data of role meta data!", _mdNew, mr.getParentMetaData());
            try {
                _mdOld.createMetaRole(sName, sAdmin);
                fail("Roles of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaPrivileges() {
        assertEquals("New archive has privilege meta data!", 0, _mdNew.getMetaPrivileges());
        System.out.println(_mdOld.getMetaPrivileges());
        assertEquals("Old archive has wrong number of privilege meta data!", 5, _mdOld.getMetaPrivileges());
    }

    @Test
    public void testGetMetaPrivilege_Int() {
        for (int iPrivilege = 0; iPrivilege < _mdOld.getMetaPrivileges(); iPrivilege++) {
            MetaPrivilege mp = _mdOld.getMetaPrivilege(iPrivilege);
            assertSame("Invalid parent meta data of privilege meta data!", _mdOld, mp.getParentMetaData());
            System.out.println(mp.getType() + "/" + mp.getObject() + "/" + mp.getGrantor() + "/" + mp.getGrantee());
        }
    }

    @Test
    public void testGetMetaPrivilege_Strings() {
        String sType = "REFERENCES";
        String sObject = "TABLE SIARDSCHEMA.TABLETEST2";
        String sGrantor = "dbo";
        String sGrantee = "SIARDUSER";
        String sDescription = "Description";
        MetaPrivilege mp = _mdOld.getMetaPrivilege(sType, sObject, sGrantor, sGrantee);
        assertEquals("Wrong privilege type!", sType, mp.getType());
        assertEquals("Wrong privilege object!", sObject, mp.getObject());
        assertEquals("Wrong privilege grantor!", sGrantor, mp.getGrantor());
        assertEquals("Wrong privilege grantee!", sGrantee, mp.getGrantee());
        System.out.println(mp.getOption());
        mp.setDescription(sDescription);
        assertEquals("Wrong privilege description!", sDescription, mp.getDescription());
    }

    @Test
    public void testCreateMetaPrivilege() {
        try {
            String sType = "PRIVTYPE";
            String sObject = "TABLE PRIVTEST";
            String sGrantor = "PRIVGRANTOR";
            String sGrantee = "PRIVGRANTEE";
            _mdNew.createMetaPrivilege(sType, sObject, sGrantor, sGrantee);
            assertEquals("Wrong number of privilege meta data!", 1, _mdNew.getMetaPrivileges());
            MetaPrivilege mp = _mdNew.getMetaPrivilege(0);
            assertEquals("Wrong privilege type", sType, mp.getType());
            assertEquals("Wrong privilege object", sObject, mp.getObject());
            assertEquals("Wrong privilege grantor", sGrantor, mp.getGrantor());
            assertEquals("Wrong privilege grantee", sGrantee, mp.getGrantee());
            assertSame("Invalid parent meta data of privilege meta data!", _mdNew, mp.getParentMetaData());
            mp.setOption("GRANT");
            assertEquals("Invalid option!", "GRANT", mp.getOption());
            try {
                _mdOld.createMetaPrivilege(sType, sObject, sGrantor, sGrantee);
                fail("Privileges of old metadata could be changed!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testIsValid() {
        assertFalse("New meta data have no schema and thus are not valid!", _mdNew.isValid());
        assertTrue("Old meta data should be valid!", _mdOld.isValid());
    }
}
