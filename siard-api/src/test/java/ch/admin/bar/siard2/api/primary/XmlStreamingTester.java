package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.generated.table.ObjectFactory;
import ch.admin.bar.siard2.api.generated.table.RecordType;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import ch.enterag.utils.jaxb.Io;
import ch.enterag.utils.jaxb.XMLStreamFactory;
import ch.enterag.utils.xml.XU;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

@Ignore("these test were not part of the ch.admin.bar.siard2.api._SiardApiTestSuite that is run when the ant test target is run! The specified files never existed in the repository")
public class XmlStreamingTester {
    private final ObjectFactory _of = new ObjectFactory();

    private void printReaderStatus(XMLStreamReader xsr) {
        if (xsr.getEventType() == XMLStreamConstants.START_ELEMENT) {
            System.out.println("Start element: " + xsr.getLocalName());
            for (int i = 0; i < xsr.getNamespaceCount(); i++) {
                System.out.println("  Namespace " + i);
                System.out.println("    Prefix: " + xsr.getNamespacePrefix(i));
                System.out.println("    URI: " + xsr.getNamespaceURI(i));
            }
            System.out.println(" Prefix: " + xsr.getPrefix());
            System.out.println(" Namespace: " + xsr.getNamespaceURI());
            for (int i = 0; i < xsr.getAttributeCount(); i++) {
                System.out.println("  Attribute " + i + ": " + xsr.getAttributeLocalName(i));
                System.out.println("    Type: " + xsr.getAttributeType(i));
                System.out.println("    Prefix: " + xsr.getAttributePrefix(i));
                System.out.println("    Namespace: " + xsr.getAttributeNamespace(i));
                System.out.println("    Value: " + xsr.getAttributeValue(i));
            }
        } else {
            System.err.println("Not a start element");
        }
    }

    private Element getRowElement(XMLStreamReader xsr, Document doc)
            throws XMLStreamException {
        Element elRow = null;
        String sTag = xsr.getLocalName();
        if (xsr.isStartElement() && sTag.equals("row")) {
            elRow = doc.createElementNS(xsr.getNamespaceURI(), xsr.getLocalName());
            List<Element> listElementsStack = new ArrayList<Element>();
            listElementsStack.add(elRow);
            for (xsr.next(); listElementsStack.size() > 0; xsr.next()) {
                switch (xsr.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        Element elNew = doc.createElementNS(xsr.getNamespaceURI(), xsr.getLocalName());
                        for (int i = 0; i < xsr.getAttributeCount(); i++)
                            elNew.setAttribute(xsr.getAttributeLocalName(i), xsr.getAttributeValue(i));
                        listElementsStack.get(0)
                                         .appendChild(elNew);
                        listElementsStack.add(0, elNew);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        Element elParent = listElementsStack.get(0);
                        if (elParent.getTagName()
                                    .equals(xsr.getLocalName()))
                            listElementsStack.remove(elParent);
                        else
                            System.err.println("Unexpected END ELEMENT!");
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        Text text = doc.createTextNode(xsr.getText());
                        listElementsStack.get(0)
                                         .appendChild(text);
                        break;
                    default:
                        break;
                }
            }
        }
        return elRow;
    }

    private RecordType getRecordType(XMLStreamReader xsr, Unmarshaller u)
            throws JAXBException, ParserConfigurationException, XMLStreamException {
        RecordType rt = null;
        String sNamespace = xsr.getNamespaceURI();
        /*** This almost works but fails on unqualified attributes of the any elements ...
         if (sNamespace.equals(Archive.sSIARD2_TABLE_NAMESPACE))
         rt = u.unmarshal(xsr, RecordType.class).getValue();
         else
         {
         ***/
        rt = _of.createRecordType();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element elTable = doc.createElementNS(sNamespace, "table");
        doc.appendChild(elTable);
        elTable.setAttribute("xmlns", sNamespace);
        Element elRow = getRowElement(xsr, doc);
        elTable.appendChild(elRow);
        for (int i = 0; i < elRow.getChildNodes()
                                 .getLength(); i++) {
            Node nodeChild = elRow.getChildNodes()
                                  .item(i);
            if (nodeChild.getNodeType() == Node.ELEMENT_NODE) {
                Element elColumn = (Element) nodeChild;
                rt.getAny()
                  .add(elColumn);
            }
        }
        /***
         }
         ***/
        if (!(xsr.isStartElement() || xsr.isEndElement()))
            xsr.nextTag();
        return rt;
    } 

    private void readTable(File fileXsd, File fileXml) {
        try {
            URL urlXsdSpecific = fileXsd.toURI()
                                        .toURL();
            System.out.println(urlXsdSpecific);
            // make sure, it is valid
            Io.readJaxbObject(Object.class, fileXml, urlXsdSpecific);

            JAXBContext ctx = JAXBContext.newInstance(ch.admin.bar.siard2.api.generated.table.Table.class);
            Unmarshaller u = ctx.createUnmarshaller();

            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, SU.sUTF8_CHARSET_NAME);

            InputStream isXml = new FileInputStream(fileXml);
            XMLStreamReader xsr = XMLStreamFactory.createXMLStreamReader(urlXsdSpecific, isXml);
            // read: start root element table
            if (xsr.nextTag() != XMLStreamConstants.START_ELEMENT)
                throw new XMLStreamException("Root element table not found!");
            printReaderStatus(xsr);
            if (xsr.getLocalName() != "table")
                throw new XMLStreamException("Root element is not \"table\"!");
            for (xsr.nextTag(); xsr.isStartElement() && (xsr.getLocalName()
                                                            .equals("row")); ) {
                RecordType rt = getRecordType(xsr, u);
                // print it out for visual checking
                for (int iColumn = 0; iColumn < rt.getAny()
                                                  .size(); iColumn++) {
                    Element elColumn = (Element) rt.getAny()
                                                   .get(iColumn);
                    transformer.transform(new DOMSource(elColumn), new StreamResult(System.out));
                }
                System.out.println();
            }
            if (!xsr.isEndElement())
                throw new XMLStreamException("End of table element not found!");
            else
                System.out.println("End of table: " + xsr.getLocalName());
            xsr.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (JAXBException je) {
            fail(EU.getExceptionMessage(je));
        } catch (XMLStreamException xse) {
            fail(EU.getExceptionMessage(xse));
        } catch (ParserConfigurationException pce) {
            fail(EU.getExceptionMessage(pce));
        } catch (TransformerConfigurationException tce) {
            fail(EU.getExceptionMessage(tce));
        } catch (TransformerException te) {
            fail(EU.getExceptionMessage(te));
        }
    }

    @Test
    public void testReadComplexTable() {
        File fileXsd = new File("src/test/resources/testfiles/table1.xsd");
        File fileXml = new File("src/test/resources/testfiles/table1.xml");
        readTable(fileXsd, fileXml);
    }

    @Test
    public void testReadSimpleTable() {
        File fileXsd = new File("src/test/resources/testfiles/table0.xsd");
        File fileXml = new File("src/test/resources/testfiles/table0.xml");
        readTable(fileXsd, fileXml);
    }

    @Test
    public void testReadTable() {
        File fileXsd = new File("src/test/resources/testfiles/tabletest.xsd");
        File fileXml = new File("src/test/resources/testfiles/tabletest.xml");
        readTable(fileXsd, fileXml);
    }

    @Test
    public void testReadOldTable() {
        File fileXsd = new File("src/test/resources/testfiles/table_old.xsd");
        File fileXml = new File("src/test/resources/testfiles/table_old.xml");
        readTable(fileXsd, fileXml);
    }

    private List<RecordType> createRecords() {
        ObjectFactory of = new ObjectFactory();
        List<RecordType> listRecords = new ArrayList<RecordType>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            RecordType rt = of.createRecordType();
            listRecords.add(rt);
            Element el = doc.createElementNS(Archive.sSIARD2_TABLE_NAMESPACE, "c1");
            el.setTextContent("C");
            rt.getAny()
              .add(el);
            el = doc.createElementNS(Archive.sSIARD2_TABLE_NAMESPACE, "c2");
            el.setTextContent("4");
            rt.getAny()
              .add(el);

            rt = of.createRecordType();
            listRecords.add(rt);

            el = doc.createElementNS(Archive.sSIARD2_TABLE_NAMESPACE, "c1");
            rt.getAny()
              .add(el);
            el.setTextContent("D");

            el = doc.createElementNS(Archive.sSIARD2_TABLE_NAMESPACE, "c2");
            rt.getAny()
              .add(el);
            el.setTextContent("-23");

            el = doc.createElementNS(Archive.sSIARD2_TABLE_NAMESPACE, "c3");
            rt.getAny()
              .add(el);
            el.setAttribute("file", "somefile");
            el.setAttribute("length", "46");
            el.setAttribute("digestType", "SHA-1");
            el.setAttribute("messageDigest", "ABABABABAAB");
        } catch (ParserConfigurationException pce) {
            fail(EU.getExceptionMessage(pce));
        }
        return listRecords;
    }

    private void putRowElement(Element el, XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(el.getLocalName());
        for (int i = 0; i < el.getAttributes()
                              .getLength(); i++) {
            Attr attr = (Attr) el.getAttributes()
                                 .item(i);
            xsw.writeAttribute(attr.getName(), attr.getValue());
        }
        for (int i = 0; i < el.getChildNodes()
                              .getLength(); i++) {
            Node node = el.getChildNodes()
                          .item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                putRowElement((Element) node, xsw);
            else if (node.getNodeType() == Node.TEXT_NODE)
                xsw.writeCharacters(node.getTextContent());
        }
        xsw.writeEndElement();
    } 

    private void putRecordType(RecordType rt, XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement("row");
        for (int i = 0; i < rt.getAny()
                              .size(); i++)
            putRowElement((Element) rt.getAny()
                                      .get(i), xsw);
        xsw.writeEndElement();
    }

    @Test
    public void testWriteTable() {
        try {
            List<RecordType> listRecords = createRecords();
            File fileXml = new File("logs/tabletest.xml");
            OutputStream osXml = new FileOutputStream(fileXml);

            /***
             JAXBContext ctx = JAXBContext.newInstance(RecordType.class);
             Marshaller m = ctx.createMarshaller();
             m.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
             ***/

            XMLStreamWriter xsw = XMLStreamFactory.createStreamWriter(osXml);
            xsw.setDefaultNamespace(Archive.sSIARD2_TABLE_NAMESPACE);
            xsw.setPrefix("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            xsw.writeStartDocument(SU.sUTF8_CHARSET_NAME, XU.sXML_VERSION_1_0);
            xsw.writeCharacters("\n");
            /* write: start root element table
             * <table
             *   xmlns="http://www.admin.ch/xmlns/siard/2/table.xsd"
             *   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             *   xsi:schemaLocation="http://www.admin.ch/xmlns/siard/2/table.xsd tabletest.xsd"
             *   version="2.1">
             */
            xsw.writeStartElement("table");
            xsw.writeNamespace(null, Archive.sSIARD2_TABLE_NAMESPACE);
            xsw.writeNamespace("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            xsw.writeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation", Archive.sSIARD2_TABLE_NAMESPACE + " tabletest.xsd");
            xsw.writeAttribute("version", "2.1");
            for (int i = 0; i < listRecords.size(); i++) {
                xsw.writeCharacters("\n  ");
                RecordType rt = listRecords.get(i);
                putRecordType(rt, xsw);
                /***
                 QName qname = new QName(Archive.sSIARD2_TABLE_NAMESPACE,"row");
                 JAXBElement<RecordType> jbeRecordType = new JAXBElement<RecordType>(qname,RecordType.class,rt);
                 m.marshal(jbeRecordType, xsw);
                 ***/
            }
            xsw.writeCharacters("\n");
            xsw.writeEndDocument();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        } catch (XMLStreamException xse) {
            fail(EU.getExceptionMessage(xse));
        }
        // catch(JAXBException je) { fail(EU.getExceptionMessage(je)); }
        catch (Exception e) {
            fail(EU.getExceptionMessage(e));
        }
    }

}
