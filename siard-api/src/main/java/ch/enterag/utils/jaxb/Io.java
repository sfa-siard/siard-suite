/*== Io.java ===========================================================
XML I/O for JAXB objects. 
Version     : $Id: Io.java 614 2016-03-01 15:31:37Z hartwig $
Application : JAXB Utilities
Description : Generic static methods for validated reading and for writing
              of JAXB objects.
------------------------------------------------------------------------
Copyright  : 2012, Enter AG, Zurich, Switzerland
Created    : 29.05.2012, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jaxb;

import ch.admin.bar.siard2.api.generated.SiardArchive;
import ch.enterag.utils.reflect.Glue;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;


/**
 * Abstract class cannot be instantiated but publishes static methods.
 *
 * @author Hartwig
 */
public abstract class Io {
    static {
        /* For JAXB 2.3.0 this suppresses the problem with reflective access.
         * See: https://github.com/javaee/jaxb-v2/issues/1197 */
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
    }

    /*--------------------------------------------------------------------*/

    /**
     * read and unmarshal a JAXB object from an XML file, validating it
     * with an XSD.
     *
     * @param classType JAXB class generated from XSD.
     * @param isXml     input stream with XML conforming to XSD.
     * @param urlXsd    URL where XSD can be found.
     * @return JAXB object.
     * @throws JAXBException if unmarshalling failed.
     */
    public static <T> T readJaxbObject(Class<T> classType, InputStream isXml, URL urlXsd)
            throws JAXBException {
        /* restore the error count to 10 again, because we do not want to miss any errors! */
        Glue.setPrivate(com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext.class, "errorsCounter", Integer.valueOf(10));
        T jo = null;
        JAXBContext ctx = ValidatingJAXBContext.newInstance(urlXsd, classType);
        Unmarshaller u = ctx.createUnmarshaller();
        StreamSource ss = new StreamSource(isXml);
        jo = u.unmarshal(ss, classType)
              .getValue();
        return jo;
    } 


    /*--------------------------------------------------------------------*/

    /**
     * read and unmarshal a JAXB object from an XML file, validating it
     * with an XSD.
     *
     * @param classType JAXB class generated from XSD.
     * @param fileXml   file with XML conforming to XSD.
     * @param urlXsd    URL where XSD can be found.
     * @return JAXB object.
     * @throws FileNotFoundException if fileXml could not be found.
     * @throws JAXBException         if unmarshalling failed.
     * @throws IOException           if an I/O error occurred.
     */
    public static <T> T readJaxbObject(Class<T> classType, File fileXml, URL urlXsd)
            throws FileNotFoundException, JAXBException, IOException {
        T jo = null;
        FileInputStream fis = new FileInputStream(fileXml);
        jo = readJaxbObject(classType, fis, urlXsd);
        fis.close();
        return jo;
    } 

    /*--------------------------------------------------------------------*/

    /**
     * marshal and write a JAXB object to an output stream, validating it
     * with an XSD.
     *
     * @param jo                         JAXB object.
     * @param os                         output stream.
     * @param qname                      QName of the object to be streamed (needed if not root object).
     * @param sNoNamespaceSchemaLocation if not null, xsi:noNamespaceSchemaLocation
     * @param sSchemaLocation            if not null, xsi:schemaLocation
     * @param bFormat                    true, if output should be formated.
     * @param urlXsd                     URL where XSD can be found.
     * @throws JAXBException if marshalling failed.
     */
    public static void writeJaxbObject(SiardArchive jo, OutputStream os, QName qname,
                                       String sNoNamespaceSchemaLocation, String sSchemaLocation, boolean bFormat, URL urlXsd)
            throws JAXBException {
        Class<?> aClass = SiardArchive.class; // always write SiardArchives!
        JAXBContext ctx;
        if (urlXsd == null) {
            ctx = JAXBContext.newInstance(aClass);
        } else {
            ctx = ValidatingJAXBContext.newInstance(urlXsd, aClass);
        }
        Marshaller m = ctx.createMarshaller();
        if (sNoNamespaceSchemaLocation != null)
            m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, sNoNamespaceSchemaLocation);
        if (sSchemaLocation != null)
            m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, sSchemaLocation);
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(bFormat));
        if (qname != null) {
            @SuppressWarnings("unchecked")
            JAXBElement jbe = new JAXBElement(qname, aClass, jo);
            m.marshal(jbe, os);
        } else
            m.marshal(jo, os);
    } 

    /*--------------------------------------------------------------------*/

    /**
     * marshal and write a JAXB object to an output stream.
     *
     * @param jo                         JAXB object.
     * @param os                         output stream.
     * @param qname                      QName of the object to be streamed (needed if not root object).
     * @param sNoNamespaceSchemaLocation if not null, xsi:noNamespaceSchemaLocation
     * @param bFormat                    true, if output should be formated.
     * @throws JAXBException if marshalling failed.
     */
    public static void writeJaxbObject(SiardArchive jo, OutputStream os, QName qname, String sNoNamespaceSchemaLocation, boolean bFormat)
            throws JAXBException {
        writeJaxbObject(jo, os, qname, sNoNamespaceSchemaLocation, null, bFormat, null);
    } 



    /*--------------------------------------------------------------------*/

    /**
     * marshal and write a JAXB object to an output stream, validating it
     * with an XSD.
     *
     * @param jo              JAXB object.
     * @param os              output stream.
     * @param sSchemaLocation if not null, xsi:schemaLocation
     * @param bFormat         true, if output should be formated.
     * @param urlXsd          URL where XSD can be found.
     * @throws JAXBException if marshalling failed.
     */
    public static void writeJaxbObject(SiardArchive jo, OutputStream os, String sSchemaLocation, boolean bFormat, URL urlXsd)
            throws JAXBException {
        writeJaxbObject(jo, os, null, null, sSchemaLocation, bFormat, urlXsd);
    } 
} 
