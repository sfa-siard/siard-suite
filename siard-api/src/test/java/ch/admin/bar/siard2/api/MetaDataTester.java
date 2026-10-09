package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.generated.MessageDigestType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.api.primary.SchemaImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.EU;
import ch.enterag.utils.FU;
import ch.enterag.utils.SU;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
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

    @AfterEach
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
        assertEquals(Archive.sMETA_DATA_VERSION, _mdNew.getVersion(), "Wrong version!");
        assertEquals(Archive.sMETA_DATA_VERSION_1_0, _mdOld.getVersion(), "Wrong version!");
    }

    @Test
    public void testDbname() {
        String sDbname = "Dbname";
        _mdNew.setDbName(sDbname);
        assertEquals(sDbname, _mdNew.getDbName(), "Invalid Dbname!");
        _mdOld.setDbName(sDbname);
        assertEquals(sDbname, _mdOld.getDbName(), "Invalid Dbname!");
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mdNew.setDescription(sDescription);
        assertEquals(sDescription, _mdNew.getDescription(), "Invalid Description!");
        _mdOld.setDescription(sDescription);
        assertEquals(sDescription, _mdOld.getDescription(), "Invalid Description!");
    }

    @Test
    public void testArchiver() {
        String sArchiver = "Archiver";
        _mdNew.setArchiver(sArchiver);
        assertEquals(sArchiver, _mdNew.getArchiver(), "Invalid Archiver!");
        _mdOld.setArchiver(sArchiver);
        assertEquals(sArchiver, _mdOld.getArchiver(), "Invalid Archiver!");
    }

    @Test
    public void testArchiverContact() {
        String sArchiverContact = "ArchiverContact";
        _mdNew.setArchiverContact(sArchiverContact);
        assertEquals(sArchiverContact, _mdNew.getArchiverContact(), "Invalid ArchiverContact!");
        _mdOld.setArchiverContact(sArchiverContact);
        assertEquals(sArchiverContact, _mdOld.getArchiverContact(), "Invalid ArchiverContact!");
    }

    @Test
    public void testDataOwner() {
        String sDataOwner = "DataOwner";
        _mdNew.setDataOwner(sDataOwner);
        assertEquals(sDataOwner, _mdNew.getDataOwner(), "Invalid DataOwner!");
        _mdOld.setDataOwner(sDataOwner);
        assertEquals(sDataOwner, _mdOld.getDataOwner(), "Invalid DataOwner!");
    }

    @Test
    public void testDataOriginTimespan() {
        String sDataOriginTimespan = "DataOriginTimespan";
        _mdNew.setDataOriginTimespan(sDataOriginTimespan);
        assertEquals(sDataOriginTimespan, _mdNew.getDataOriginTimespan(), "Invalid DataOriginTimespan!");
        _mdOld.setDataOriginTimespan(sDataOriginTimespan);
        assertEquals(sDataOriginTimespan, _mdOld.getDataOriginTimespan(), "Invalid DataOriginTimespan!");
    }

    @Test
    public void testLobFolder() {
        try {
            URI uriLobFolder = new URI(_fileLOBS_FOLDER.toURI() + "/");
            _mdNew.setLobFolder(uriLobFolder);
            assertEquals(uriLobFolder, _mdNew.getLobFolder(), "Invalid LobFolder!");
            File file = FU.fromUri(_mdNew.getAbsoluteLobFolder());
            assertEquals(_fileLOBS_FOLDER.getAbsolutePath(), file.getAbsolutePath(), "Wrong absolute folder!");
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
            assertEquals(sProducerApplication, _mdNew.getProducerApplication(), "Invalid ProducerApplication!");
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
        assertEquals("GMT+00:00", gc.getTimeZone()
                                                                        .getDisplayName(), "Archival date not stored as UTC!");
        assertEquals(sToday, _du.fromGregorianCalendar(gc), "Wrong date!");
    }

    @Test
    public void testMessageDigest() {
        List<MessageDigestType> listDigest = _mdNew.getMessageDigest();
        assertEquals(0, listDigest.size(), "New archive has message digest!");
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
            assertEquals(sClientMachine, _mdNew.getClientMachine(), "Invalid ClientMachine!");
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
            assertEquals(sDatabaseProduct, _mdNew.getDatabaseProduct(), "Invalid DatabaseProduct!");
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
            assertEquals(sConnection, _mdNew.getConnection(), "Invalid Connection!");
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
            assertEquals(sDatabaseUser, _mdNew.getDatabaseUser(), "Invalid DatabaseUser!");
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
        assertEquals(0, _mdNew.getMetaSchemas(), "New archive has schema meta data!");
        assertEquals(1, _mdOld.getMetaSchemas(), "Old archive has wrong number of schema meta data!");
    }

    @Test
    public void testGetMetaSchema_Int() {
        for (int iSchema = 0; iSchema < _mdOld.getMetaSchemas(); iSchema++) {
            MetaSchema ms = _mdOld.getMetaSchema(iSchema);
            assertEquals(SchemaImpl._sSCHEMA_FOLDER_PREFIX + iSchema, ms.getFolder(), "Invalid schema folder!");

        }
    }

    @Test
    public void testGetMetaSchema_String() {
        String sName = "SIARDSCHEMA";
        String sDescription = "Description";
        MetaSchema ms = _mdOld.getMetaSchema(sName);
        assertEquals(sName, ms.getName(), "Invalid schema name!");
        ms.setDescription(sDescription);
        assertEquals(sDescription, ms.getDescription(), "Wrong schema description!");
    }

    @Test
    public void testGetMetaUsers() {
        assertEquals(0, _mdNew.getMetaUsers(), "New archive has user meta data!");
        System.out.println(_mdOld.getMetaUsers());
        assertEquals(2, _mdOld.getMetaUsers(), "Old archive has wrong number of user meta data!");
    }

    @Test
    public void testGetMetaUser_Int() {
        for (int iUser = 0; iUser < _mdOld.getMetaUsers(); iUser++) {
            MetaUser mu = _mdOld.getMetaUser(iUser);
            assertSame(_mdOld, mu.getParentMetaData(), "Invalid parent meta data of user meta data!");
            System.out.println(mu.getName());
        }
    }

    @Test
    public void testGetMetaUser_String() {
        String sName = "SIARDUSER";
        String sDescription = "Description";
        MetaUser mu = _mdOld.getMetaUser(sName);
        assertEquals(sName, mu.getName(), "Wrong user name!");
        mu.setDescription(sDescription);
        assertEquals(sDescription, mu.getDescription(), "Wrong user description!");
    }

    @Test
    public void testCreateMetaUser() {
        try {
            String sName = "METAUSER";
            _mdNew.createMetaUser(sName);
            assertEquals(1, _mdNew.getMetaUsers(), "Wrong number of user meta data!");
            MetaUser mu = _mdNew.getMetaUser(0);
            assertEquals(sName, mu.getName(), "Wrong user name");
            assertSame(_mdNew, mu.getParentMetaData(), "Invalid parent meta data of user meta data!");
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
        assertEquals(0, _mdNew.getMetaRoles(), "New archive has role meta data!");
        System.out.println(_mdOld.getMetaRoles());
        assertEquals(1, _mdOld.getMetaRoles(), "Old archive has wrong number of role meta data!");
    }

    @Test
    public void testGetMetaRole_Int() {
        for (int iRole = 0; iRole < _mdOld.getMetaRoles(); iRole++) {
            MetaRole mr = _mdOld.getMetaRole(iRole);
            assertSame(_mdOld, mr.getParentMetaData(), "Invalid parent meta data of role meta data!");
            System.out.println(mr.getName() + "/" + mr.getAdmin() + ".");
        }
    }

    @Test
    public void testGetMetaRole_String() {
        String sName = "public";
        String sDescription = "Description";
        MetaRole mr = _mdOld.getMetaRole(sName);
        assertEquals(sName, mr.getName(), "Wrong role name!");
        assertEquals("", mr.getAdmin(), "");
        mr.setDescription(sDescription);
        assertEquals(sDescription, mr.getDescription(), "Wrong role description!");
    }

    @Test
    public void testCreateMetaRole() {
        try {
            String sName = "METAROLE";
            String sAdmin = "ROLEADMIN";
            _mdNew.createMetaRole(sName, sAdmin);
            assertEquals(1, _mdNew.getMetaRoles(), "Wrong number of role meta data!");
            MetaRole mr = _mdNew.getMetaRole(0);
            assertEquals(sName, mr.getName(), "Wrong role name");
            assertEquals(sAdmin, mr.getAdmin(), "Wrong role admin");
            assertSame(_mdNew, mr.getParentMetaData(), "Invalid parent meta data of role meta data!");
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
        assertEquals(0, _mdNew.getMetaPrivileges(), "New archive has privilege meta data!");
        System.out.println(_mdOld.getMetaPrivileges());
        assertEquals(5, _mdOld.getMetaPrivileges(), "Old archive has wrong number of privilege meta data!");
    }

    @Test
    public void testGetMetaPrivilege_Int() {
        for (int iPrivilege = 0; iPrivilege < _mdOld.getMetaPrivileges(); iPrivilege++) {
            MetaPrivilege mp = _mdOld.getMetaPrivilege(iPrivilege);
            assertSame(_mdOld, mp.getParentMetaData(), "Invalid parent meta data of privilege meta data!");
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
        assertEquals(sType, mp.getType(), "Wrong privilege type!");
        assertEquals(sObject, mp.getObject(), "Wrong privilege object!");
        assertEquals(sGrantor, mp.getGrantor(), "Wrong privilege grantor!");
        assertEquals(sGrantee, mp.getGrantee(), "Wrong privilege grantee!");
        System.out.println(mp.getOption());
        mp.setDescription(sDescription);
        assertEquals(sDescription, mp.getDescription(), "Wrong privilege description!");
    }

    @Test
    public void testCreateMetaPrivilege() {
        try {
            String sType = "PRIVTYPE";
            String sObject = "TABLE PRIVTEST";
            String sGrantor = "PRIVGRANTOR";
            String sGrantee = "PRIVGRANTEE";
            _mdNew.createMetaPrivilege(sType, sObject, sGrantor, sGrantee);
            assertEquals(1, _mdNew.getMetaPrivileges(), "Wrong number of privilege meta data!");
            MetaPrivilege mp = _mdNew.getMetaPrivilege(0);
            assertEquals(sType, mp.getType(), "Wrong privilege type");
            assertEquals(sObject, mp.getObject(), "Wrong privilege object");
            assertEquals(sGrantor, mp.getGrantor(), "Wrong privilege grantor");
            assertEquals(sGrantee, mp.getGrantee(), "Wrong privilege grantee");
            assertSame(_mdNew, mp.getParentMetaData(), "Invalid parent meta data of privilege meta data!");
            mp.setOption("GRANT");
            assertEquals("GRANT", mp.getOption(), "Invalid option!");
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
        assertFalse(_mdNew.isValid(), "New meta data have no schema and thus are not valid!");
        assertTrue(_mdOld.isValid(), "Old meta data should be valid!");
    }
}
