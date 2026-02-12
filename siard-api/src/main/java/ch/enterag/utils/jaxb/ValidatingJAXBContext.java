/*== ValidatingJAXBContext.java ========================================
A wrapper for a JAXBContext with schema validation on unmarshaling. 
Version     : $Id: ValidatingJAXBContext.java 610 2016-02-29 16:12:36Z hartwig $
Application : JAXB Utilities
Description : A wrapper for a JAXBContext with schema validation on unmarshaling.
------------------------------------------------------------------------
Copyright  : 2012, Enter AG, Zurich, Switzerland
Created    : 29.05.2012, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jaxb;

import ch.enterag.utils.logging.IndentLogger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * ValidatingJAXBContext wraps a JAXBContext adding schema validation.
 *
 * @author Hartwig
 */
public class ValidatingJAXBContext extends JAXBContext {
    private static final IndentLogger m_il = IndentLogger.getIndentLogger(ValidatingJAXBContext.class.getName());
    private JAXBContext m_jc = null;
    private URL m_urlSchema = null;

    static {
        /* For JAXB 2.3.0 this suppresses the problem with reflective access.
         * See: https://github.com/javaee/jaxb-v2/issues/1197 */
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
    }

    /*==================================================================*/

    /**
     * ContextValidationEventHandler is used for more explicit logging.
     */
    private class ContextValidationEventHandler implements ValidationEventHandler {
        @Override
        public boolean handleEvent(ValidationEvent ve) {
            boolean bContinue = false;
            m_il.enter(ve);
            if ((ve.getSeverity() == ValidationEvent.ERROR) ||
                    (ve.getSeverity() == ValidationEvent.FATAL_ERROR)) {
                ValidationEventLocator vel = ve.getLocator();
                m_il.severe("XML validation for " + vel.getURL() +
                                    " failed at line " + vel.getLineNumber() +
                                    " and column " + vel.getColumnNumber() +
                                    " with message " + ve.getMessage());
            } else
                bContinue = true;
            m_il.exit(String.valueOf(bContinue));
            return bContinue;
        } 

    } 
    /*==================================================================*/

    /**
     * constructor
     *
     * @param jc     JAXBContext to be wrapped.
     * @param schema Schema to be used for validation or null.
     */
    private ValidatingJAXBContext(JAXBContext jc, URL urlSchema) {
        m_jc = jc;
        m_urlSchema = urlSchema;
    } 

    /**
     * factory
     *
     * @param urlSchema        schema for validation
     * @param classesToBeBound classes for binding.
     * @return class that validates on unmarshalling.
     */
    public static ValidatingJAXBContext newInstance(URL urlSchema, Class<?>... classesToBeBound)
            throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(classesToBeBound);
        ValidatingJAXBContext vjc = new ValidatingJAXBContext(jc, urlSchema);
        return vjc;
    } 

    /**
     * factory
     *
     * @param urlSchema schema for validation
     * @param sPackage  package containing classes for binding.
     * @return class that validates on unmarshalling.
     */
    public static ValidatingJAXBContext newInstance(URL urlSchema, String sPackage)
            throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(sPackage);
        ValidatingJAXBContext vjc = new ValidatingJAXBContext(jc, urlSchema);
        return vjc;
    } 

    /**
     * create a validating unmarshaller.
     *
     * @return validating unmarshaller.
     */
    @Override
    public Unmarshaller createUnmarshaller()
            throws JAXBException {
        m_il.enter();
        Unmarshaller u = m_jc.createUnmarshaller();
        if (m_urlSchema != null) {
            m_il.event("Creating validating Unmarshaller ...");
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                InputStream is = m_urlSchema.openStream();
                Source source = new StreamSource(is);
                Schema schema = sf.newSchema(source);
                is.close();
                u.setSchema(schema);
            } catch (SAXException se) {
                throw new RuntimeException("Unable to create Schema " + m_urlSchema + "!", se);
            } catch (IOException ie) {
                throw new RuntimeException("Unable to read Schema " + m_urlSchema + "!", ie);
            }
        }
        u.setEventHandler(new ContextValidationEventHandler());
        m_il.exit(u);
        return u;
    } 

    /**
     * create a validating and formatting marshaller.
     *
     * @return formatting marshaller.
     */
    @Override
    public Marshaller createMarshaller()
            throws JAXBException {
        m_il.enter();
        Marshaller m = m_jc.createMarshaller();
        if (m_urlSchema != null) {
            m_il.event("Creating validating Unmarshaller ...");
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                InputStream is = m_urlSchema.openStream();
                Source source = new StreamSource(is);
                Schema schema = sf.newSchema(source);
                is.close();
                m.setSchema(schema);
            } catch (SAXException se) {
                throw new RuntimeException("Unable to create Schema " + m_urlSchema + "!", se);
            } catch (IOException ie) {
                throw new RuntimeException("Unable to read Schema " + m_urlSchema + "!", ie);
            }
        }
        m.setEventHandler(new ContextValidationEventHandler());
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m_il.exit(m);
        return m;
    } 

    /**
     * create a validator.
     *
     * @return null, as the Validator class is deprecated anyway.
     */
    @SuppressWarnings("deprecation")
    @Override
    public javax.xml.bind.Validator createValidator() throws JAXBException {
        throw new RuntimeException("createValidator is deprecated!");
    } 

} 
