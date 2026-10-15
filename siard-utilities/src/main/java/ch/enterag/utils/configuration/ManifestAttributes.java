/*======================================================================
Accessing version and build number from MANIFEST.MF 
Application : Configuration Utilities
Description : Access the version and build number from MANIFEST.MF. 
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2012
Created    : 09.05.2012, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.configuration;

import ch.enterag.utils.io.SpecialFolder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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

/**
 * ManifestAttributes extends Manifest for easy access to attributes.
 *
 * @author Hartwig
 */
@Slf4j
public class ManifestAttributes extends Manifest {
    private static final String sMANIFEST_RESOURCE = "/META-INF/MANIFEST.MF";

    /**
     * constructor loads manifest from InputStream
     *
     * @param is InputStream with manifest.
     * @throws IOException if an I/O error occurred.
     */
    public ManifestAttributes(InputStream is)
            throws IOException {
        super(is);
    }

    /**
     * retrieve attribute ImplementationVersion
     *
     * @return value of attribute ImplementationVersion.
     */
    public String getImplementationVersion() {
        String sImplementationVersion = getMainAttributes().getValue("Implementation-Version");
        return sImplementationVersion;
    }

    /**
     * retrieve attribute ImplementationTitle
     *
     * @return value of attribute ImplementationTitle
     */
    public String getImplementationTitle() {
        String sImplementationTitle = getMainAttributes().getValue("Implementation-Title");
        return sImplementationTitle;
    }

    /**
     * retrieve attribute ImplementationVendor
     *
     * @return value of attribute ImplementationVendor
     */
    public String getImplementationVendor() {
        String sImplementationVendor = getMainAttributes().getValue("Implementation-Vendor");
        return sImplementationVendor;
    }

    /**
     * retrieve attribute ImplementationVersion
     *
     * @return value of attribute ImplementationVersion
     */
    public String getSpecificationVersion() {
        String sSpecificationVersion = getMainAttributes().getValue("Specification-Version");
        return sSpecificationVersion;
    }

    /**
     * retrieve attribute SpecificationTitle
     *
     * @return value of attribute SpecificationTitle
     */
    public String getSpecificationTitle() {
        String sSpecificationTitle = getMainAttributes().getValue("Specification-Title");
        return sSpecificationTitle;
    }

    /**
     * retrieve attribute SpecificationVendor
     *
     * @return value of attribute SpecificationVendor
     */
    public String getSpecificationVendor() {
        String sSpecificationVendor = getMainAttributes().getValue("Specification-Vendor");
        return sSpecificationVendor;
    }

    /**
     * retrieve attribute Built-Data
     *
     * @return value of attribute Built-Data
     */
    public Calendar getBuiltDate() {
        Calendar calBuiltDate = null;
        String sBuiltDate = getMainAttributes().getValue("Built-Date");
        if (sBuiltDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MMMM yyyy", Locale.US);
            try {
                Date date = sdf.parse(sBuiltDate);
                calBuiltDate = new GregorianCalendar();
                calBuiltDate.setTime(date);
            } catch (ParseException pe) {
                log.error("Exception logged", pe);
            }
        }
        return calBuiltDate;
    }

    /**
     * factory loads manifest from stream
     *
     * @param is stream with manifest.
     * @return instance of ManifestAttributes.
     */
    public static ManifestAttributes getInstance(InputStream is) {
        ManifestAttributes mfa = null;
        if (is != null) {
            try {
                mfa = new ManifestAttributes(is);
            } catch (IOException ie) {
                log.error("Exception logged", ie);
            }
        }
        return mfa;
    }

    /**
     * factory loads manifest from class path
     *
     * @param clazz class for resource loading.
     * @return instance of ManifestAttributes.
     */
    public static ManifestAttributes getInstance(Class<?> clazz) {
        ManifestAttributes mfa = null;
        try {
            /* get the first one available */
            URL urlManifest = clazz.getResource(sMANIFEST_RESOURCE);
            log.debug("Initial manifest: " + urlManifest);
            File fileJar = SpecialFolder.getJarFromClass(clazz, false);
            if (fileJar != null) {
                /* if we are in a jar, search for the one pointing to the jar file */
                if (fileJar.isFile()) {
                    URL urlJarFile = fileJar.toURI()
                                            .toURL(); // file: URL
                    log.debug("JAR file URL: " + urlJarFile);
                    String sJarUrl = "jar:" + urlJarFile + "!" + sMANIFEST_RESOURCE; // jar:file:<...>!/META-INF/MANIFEST.MF
                    urlManifest = new URL(sJarUrl);
                    log.debug("Manifest in JAR: " + urlManifest);
                } else /* otherwise get it from the file system */ {
                    File fileManifest = new File(fileJar.getParentFile()
                                                        .getParentFile()
                                                        .getParentFile()
                                                        .getAbsolutePath() + "/tmp/jar/MANIFEST.MF"); // <...>/tmp/jar/MANIFEST.MF for gradle setup
                    urlManifest = fileManifest.toURI()
                                              .toURL();
                    log.debug("Manifest file URL: " + urlManifest);
                }
            }
            log.debug("Using " + urlManifest);
            InputStream is = urlManifest.openStream();
            if (is != null)
                mfa = new ManifestAttributes(is);
        } catch (IOException ie) {
            log.error("Exception logged", ie);
        }
        return mfa;
    }

    /**
     * factory loads manifest from class path
     *
     * @return instance of ManifestAttributes.
     */
    public static ManifestAttributes getInstance() {
        return getInstance(ManifestAttributes.class);
    }
}
