/*== XMLStreamFactory.java =============================================
This helper class creates a validating XMLStreamReader. 
Application : REST Tutorial
Description : A factory for creating a validating XMLStreamReader
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2011
Created    : 12.01.2012, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jaxb;

import ch.enterag.utils.SU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;


/**
 * XMLStreamFactory is used for creating a special-purpose
 * validating XMLStreamReader and XMLStreamWriter with all options
 * that we need.
 *
 */
public abstract class XMLStreamFactory {
    private static final Logger log = LoggerFactory.getLogger(XMLStreamFactory.class);

    /**
     * returns a customized XMLStreamReader.
     *
     * @param isXml the input (XML) stream.
     * @return a customized XMLStreamReader.
     */
    public static XMLStreamReader createXMLStreamReader(InputStream isXml)
            throws XMLStreamException {
        /* use StAX2 API and Woodstox for a validating stream reader */
        XMLInputFactory2 xif = (XMLInputFactory2) XMLInputFactory2.newInstance();
        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        xif.setProperty(XMLInputFactory2.P_LAZY_PARSING, Boolean.TRUE);
        xif.configureForSpeed();
        // xif.configureForLowMemUsage();
        XMLStreamReader2 xsr = (XMLStreamReader2) xif.createXMLStreamReader(isXml);
        return xsr;
    }

    /**
     * returns a validating customized XMLStreamReader.
     * N.B.: If the schema has too large minOccurs/maxOccurs values,
     * a non-validating reader is returned.
     * N.B.: This resolves "relative" URLs in the schema relative to the
     * "current" directory!
     *
     * @param isXsd the XML Schema Definition against which the stream
     *              must be validated.
     * @param isXml the input (XML) stream.
     * @return a XMLStreamReader which validates against the given XSD.
     */
    public static XMLStreamReader createXMLStreamReader(InputStream isXsd, InputStream isXml)
            throws XMLStreamException {
        XMLStreamReader2 xsr = (XMLStreamReader2) createXMLStreamReader(isXml);
        try {
            XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
            /* createSchema may throw a StackOverflowError for large minOccurs/maxOccurs values */
            XMLValidationSchema xvs = sf.createSchema(isXsd);
            xsr.validateAgainst(xvs);
        } catch (StackOverflowError soe) {
            log.error("Error logged", soe);
        }
        return xsr;
    }

    /**
     * returns a validating customized XMLStreamReader.
     * N.B.: If the schema has too large minOccurs/maxOccurs values,
     * a non-validating reader is returned.
     * N.B.: This resolves "relative" URLs in the schema relative to the
     * given URL.!
     *
     * @param urlXsd the XML Schema Definition against which the stream
     *               must be validated.
     * @param isXml  the input (XML) stream.
     * @return a XMLStreamReader which validates against the given XSD.
     */
    public static XMLStreamReader createXMLStreamReader(URL urlXsd, InputStream isXml)
            throws XMLStreamException {
        XMLStreamReader2 xsr = (XMLStreamReader2) createXMLStreamReader(isXml);
        try {
            XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
            XMLValidationSchema xvs = sf.createSchema(urlXsd);
            xsr.validateAgainst(xvs);
        } catch (StackOverflowError soe) {
            log.error("Error logged", soe);
        }
        return xsr;
    }

    /**
     * returns a customized XMLStreamWriter.
     *
     * @param osXml the output (XML) stream.
     * @return a XMLStreamWriter.
     */
    public static XMLStreamWriter createStreamWriter(OutputStream osXml)
            throws XMLStreamException {
        XMLOutputFactory2 xof = (XMLOutputFactory2) XMLOutputFactory2.newInstance();
        xof.setProperty(XMLOutputFactory2.XSP_NAMESPACE_AWARE, Boolean.TRUE);
        xof.configureForSpeed();
        XMLStreamWriter xsw = xof.createXMLStreamWriter(osXml, SU.sUTF8_CHARSET_NAME);
        return xsw;
    }

} 
