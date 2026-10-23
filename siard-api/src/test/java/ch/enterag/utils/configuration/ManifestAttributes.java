/*== ManifestVersion.java ==============================================
Accessing version and build number from MANIFEST.MF 
Version     : $Id: ManifestAttributes.java 501 2016-01-29 13:57:17Z hartwig $
Application : Configuration Utilities
Description : Access the version and build number from MANIFEST.MF. 
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2012
Created    : 09.05.2012, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.configuration;

import ch.enterag.utils.io.SpecialFolder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
     */
    public ManifestAttributes(InputStream is)
            throws IOException {
        super(is);
    }

    /**
     * retrieve attribute ImplementationVersion
     */
    public String getImplementationVersion() {
        String sImplementationVersion = getMainAttributes().getValue("Implementation-Version");
        return sImplementationVersion;
    }

    /**
     * retrieve attribute ImplementationTitle
     */
    public String getImplementationTitle() {
        String sImplementationTitle = getMainAttributes().getValue("Implementation-Title");
        return sImplementationTitle;
    }

    /**
     * retrieve attribute ImplementationVendor
     */
    public String getImplementationVendor() {
        String sImplementationVendor = getMainAttributes().getValue("Implementation-Vendor");
        return sImplementationVendor;
    }

    /**
     * factory loads manifest from stream
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
     */
    public static ManifestAttributes getInstance(Class<?> clazz) {
        ManifestAttributes mfa = null;
        try {
            /* get the first one available */
            URL urlManifest = clazz.getResource(sMANIFEST_RESOURCE);
            log.debug("Initial manifest: " + urlManifest);
            /* if we are in a jar, search for the one pointing to the jar file */
            File fileJar = SpecialFolder.getJarFromClass(clazz, false);
            if ((fileJar != null) && (fileJar.isFile())) {
                URL urlJarFile = fileJar.toURI()
                                        .toURL(); // file: URL
                log.debug("JAR file URL: " + urlJarFile);
                String sJarUrl = "jar:" + urlJarFile + "!" + sMANIFEST_RESOURCE; // jar:file:<...>!/META-INF/MANIFEST.MF
                urlManifest = new URL(sJarUrl);
                log.debug("Manifest in JAR: " + urlManifest);
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
     */
    public static ManifestAttributes getInstance() {
        return getInstance(ManifestAttributes.class);
    }

} 
