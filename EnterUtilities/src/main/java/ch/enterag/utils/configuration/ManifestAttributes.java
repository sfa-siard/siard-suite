/*======================================================================
Accessing version and build number from MANIFEST.MF 
Application : Configuration Utilities
Description : Access the version and build number from MANIFEST.MF. 
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2012
Created    : 09.05.2012, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.configuration;

import ch.enterag.utils.logging.IndentLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.jar.Manifest;

/*====================================================================*/

/**
 * ManifestAttributes extends Manifest for easy access to attributes.
 *
 * @author Hartwig
 */
public class ManifestAttributes extends Manifest {
    private static IndentLogger _il = IndentLogger.getIndentLogger(ManifestAttributes.class.getName());
    private static final String sMANIFEST_RESOURCE = "/META-INF/MANIFEST.MF";

    /*------------------------------------------------------------------*/

    /**
     * constructor loads manifest from InputStream
     *
     * @param is InputStream with manifest.
     * @throws IOException if an I/O error occurred.
     */
    public ManifestAttributes(InputStream is)
            throws IOException {
        super(is);
        _il.enter();
        _il.exit();
    } /* constructor ManifestAttributes */

    /*------------------------------------------------------------------*/

    /**
     * retrieve attribute ImplementationVersion
     *
     * @return value of attribute ImplementationVersion.
     */
    public String getImplementationVersion() {
        _il.enter();
        String sImplementationVersion = getMainAttributes().getValue("Implementation-Version");
        _il.exit(sImplementationVersion);
        return sImplementationVersion;
    } /* getImplementationVersion */

    /*------------------------------------------------------------------*/

    /**
     * retrieve attribute ImplementationTitle
     *
     * @return value of attribute ImplementationTitle
     */
    public String getImplementationTitle() {
        _il.enter();
        String sImplementationTitle = getMainAttributes().getValue("Implementation-Title");
        _il.exit(sImplementationTitle);
        return sImplementationTitle;
    } /* getImplementationTitle */

    /*------------------------------------------------------------------*/

    /**
     * retrieve attribute ImplementationVendor
     *
     * @return value of attribute ImplementationVendor
     */
    public String getImplementationVendor() {
        _il.enter();
        String sImplementationVendor = getMainAttributes().getValue("Implementation-Vendor");
        _il.exit(sImplementationVendor);
        return sImplementationVendor;
    } /* getImplementationVendor */

    /*------------------------------------------------------------------*/

    /**
     * retrieve attribute ImplementationVersion
     *
     * @return value of attribute ImplementationVersion
     */
    public String getSpecificationVersion() {
        _il.enter();
        String sSpecificationVersion = getMainAttributes().getValue("Specification-Version");
        _il.exit(sSpecificationVersion);
        return sSpecificationVersion;
    } /* getSpecificationVersion */

    /*------------------------------------------------------------------*/

    /**
     * retrieve attribute SpecificationTitle
     *
     * @return value of attribute SpecificationTitle
     */
    public String getSpecificationTitle() {
        _il.enter();
        String sSpecificationTitle = getMainAttributes().getValue("Specification-Title");
        _il.exit(sSpecificationTitle);
        return sSpecificationTitle;
    } /* getSpecificationTitle */

    /*------------------------------------------------------------------*/

    /**
     * retrieve attribute SpecificationVendor
     *
     * @return value of attribute SpecificationVendor
     */
    public String getSpecificationVendor() {
        _il.enter();
        String sSpecificationVendor = getMainAttributes().getValue("Specification-Vendor");
        _il.exit(sSpecificationVendor);
        return sSpecificationVendor;
    } /* getSpecificationVendor */

    /*------------------------------------------------------------------*/

    /**
     * retrieve attribute Built-Data
     *
     * @return value of attribute Built-Data
     */
    public Calendar getBuiltDate() {
        _il.enter();
        Calendar calBuiltDate = null;
        String sBuiltDate = getMainAttributes().getValue("Built-Date");
        if (sBuiltDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MMMM yyyy", Locale.US);
            try {
                Date date = sdf.parse(sBuiltDate);
                calBuiltDate = new GregorianCalendar();
                calBuiltDate.setTime(date);
            } catch (ParseException pe) {
                _il.exception(pe);
            }
        }
        _il.exit(calBuiltDate);
        return calBuiltDate;
    } /* getBuiltDate */

    /*------------------------------------------------------------------*/

    /**
     * factory loads manifest from stream
     *
     * @param is stream with manifest.
     * @return instance of ManifestAttributes.
     */
    public static ManifestAttributes getInstance(InputStream is) {
        _il.enter(is);
        ManifestAttributes mfa = null;
        if (is != null) {
            try {
                mfa = new ManifestAttributes(is);
            } catch (IOException ie) {
                _il.exception(ie);
            }
        }
        _il.exit(mfa);
        return mfa;
    } /* getInstance */

    /*------------------------------------------------------------------*/

    /**
     * factory loads manifest from class path
     *
     * @param clazz class for resource loading.
     * @return instance of ManifestAttributes.
     */
    public static ManifestAttributes getInstance(Class<?> clazz) throws IOException {
        _il.enter(clazz);
        ManifestAttributes mfa = null;
        URL urlManifest = clazz.getResource(sMANIFEST_RESOURCE);
        InputStream is = urlManifest.openStream();
        return new ManifestAttributes(is);

    }


    /**
     * factory loads manifest from class path
     *
     * @return instance of ManifestAttributes.
     */
    public static ManifestAttributes getInstance() {
        try {
            return getInstance(ManifestAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    } /* getInstance */

} /* ManifestVersion */
