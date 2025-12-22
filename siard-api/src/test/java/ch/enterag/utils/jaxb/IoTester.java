package ch.enterag.utils.jaxb;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.MetaDataXml;
import ch.enterag.utils.EU;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.GregorianCalendar;

import static org.junit.Assert.fail;

// These test are not working - and they are not part of the ch.admin.bar.siard2.api._SiardApiTestSuite that is run when the ant test target is run!
// TODO: check what these tests should actually do. Then make these test work or delete them!
@Ignore
public class IoTester {
    @Test
    public void testRead2008() throws IOException, JAXBException {
        URL urlXsd = Archive.class.getResource(Archive.sSIARD2_META_DATA_XSD_RESOURCE);
        InputStream isXml = new FileInputStream("src/test/resources/testfiles/metadata-21.xml");
        SiardArchive sa = Io.readJaxbObject(SiardArchive.class, isXml, urlXsd);
        isXml.close();
        System.out.println(sa.getDatabaseUser());
    }

    @Test
    public void testRead2003() {
        try {
            URL urlXsd = Archive.class.getResource(MetaDataXml.sSIARD10_XSD_RESOURCE);
            InputStream isXml = new FileInputStream("Documentation/SIARD Format/old10/metadata2003.xml");
            SiardArchive sa = Io.readJaxbObject(SiardArchive.class, isXml, urlXsd);
            isXml.close();
            System.out.println(sa.getDatabaseUser());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (JAXBException je) {
            fail(EU.getExceptionMessage(je));
        }
    }

    @Test
    public void testRead2011() {
        try {
            URL urlXsd = Archive.class.getResource(MetaDataXml.sSIARD10_XSD_RESOURCE);
            InputStream isXml = new FileInputStream("Documentation/SIARD Format/old10/metadata2011.xml");
            SiardArchive sa = Io.readJaxbObject(SiardArchive.class, isXml, urlXsd);
            isXml.close();
            System.out.println(sa.getDatabaseUser());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (JAXBException je) {
            fail(EU.getExceptionMessage(je));
        }
    }

    @Test
    public void testWriteMinimum() {
        ObjectFactory of = new ObjectFactory();
        ch.admin.bar.siard2.api.generated.SiardArchive sa = of.createSiardArchive();
        sa.setVersion("2.0");
        sa.setDbname("TestDb");
        sa.setDataOwner("TestOwner");
        sa.setDataOriginTimespan("2016");
        try {
            GregorianCalendar gc = new GregorianCalendar(); // now
            XMLGregorianCalendar xgc = DatatypeFactory.newInstance()
                                                      .newXMLGregorianCalendar(gc);
            sa.setArchivalDate(xgc);
        } catch (DatatypeConfigurationException dce) {
            fail(EU.getExceptionMessage(dce));
        }
        SchemasType sts = of.createSchemasType();
        SchemaType st = of.createSchemaType();
        st.setName("FirstSchema");
        st.setFolder("schema0");
        sts.getSchema()
           .add(st);
        TablesType tts = of.createTablesType();
        TableType tt = of.createTableType();
        tt.setName("FirstTable");
        tt.setFolder("table0");
        ColumnsType cts = of.createColumnsType();
        ColumnType ct = of.createColumnType();
        ct.setName("FirstColumn");
        ct.setType("CHAR(5)");
        cts.getColumn()
           .add(ct);
        tt.setColumns(cts);
        tt.setRows(BigInteger.valueOf(0L));
        tts.getTable()
           .add(tt);
        st.setTables(tts);
        sa.setSchemas(sts);
        UsersType uts = of.createUsersType();
        UserType ut = of.createUserType();
        ut.setName("TestUser");
        uts.getUser()
           .add(ut);
        sa.setUsers(uts);
        try {
            URL urlXsd = Archive.class.getResource("/main/java/ch/admin/bar/siard2/api/res/metadata.xsd");
            OutputStream osXml = new FileOutputStream("logs/minimum.xml");
            Io.writeJaxbObject(sa, osXml, null, true, urlXsd);
            osXml.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (JAXBException je) {
            fail(EU.getExceptionMessage(je));
        }
    }

    @Test
    public void testWriteEmpty() {
        ObjectFactory of = new ObjectFactory();
        SiardArchive sa = of.createSiardArchive();
        sa.setVersion("2.0");
        sa.setDbname("TestDb");
        sa.setDataOwner("TestOwner");
        sa.setDataOriginTimespan("2016");
        try {
            GregorianCalendar gc = new GregorianCalendar(); // now
            XMLGregorianCalendar xgc = DatatypeFactory.newInstance()
                                                      .newXMLGregorianCalendar(gc);
            sa.setArchivalDate(xgc);
        } catch (DatatypeConfigurationException dce) {
            fail(EU.getExceptionMessage(dce));
        }
        /**
         SchemasType sts = of.createSchemasType();
         SchemaType st = of.createSchemaType();
         st.setName("FirstSchema");
         st.setFolder("schema0");
         sts.getSchema().add(st);
         TablesType tts = of.createTablesType();
         TableType tt = of.createTableType();
         tt.setName("FirstTable");
         tt.setFolder("table0");
         ColumnsType cts = of.createColumnsType();
         ColumnType ct = of.createColumnType();
         ct.setName("FirstColumn");
         ct.setType("CHAR(5)");
         cts.getColumn().add(ct);
         tt.setColumns(cts);
         tt.setRows(BigInteger.valueOf(1l));
         tts.getTable().add(tt);
         st.setTables(tts);
         sa.setSchemas(sts);
         UsersType uts = of.createUsersType();
         UserType ut = of.createUserType();
         ut.setName("TestUser");
         uts.getUser().add(ut);
         sa.setUsers(uts);
         **/
        try {
            URL urlXsd = Archive.class.getResource("/ch/admin/bar/siard2/api/res/empty.xsd");
            OutputStream osXml = new FileOutputStream("logs/empty.xml");
            Io.writeJaxbObject(sa, osXml, null, true, urlXsd);
            osXml.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (JAXBException je) {
            fail(EU.getExceptionMessage(je));
        }
    }
}
