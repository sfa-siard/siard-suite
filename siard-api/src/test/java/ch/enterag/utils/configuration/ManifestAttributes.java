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
import ch.enterag.utils.logging.IndentLogger;

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
public class ManifestAttributes extends Manifest {
    private static final IndentLogger m_il = IndentLogger.getIndentLogger(ManifestAttributes.class.getName());
    private static final String sMANIFEST_RESOURCE = "/META-INF/MANIFEST.MF";

        /**
     * constructor loads manifest from InputStream
     */
    public ManifestAttributes(InputStream is)
            throws IOException {
        super(is);
        m_il.enter();
        m_il.exit();
    } 

        /**
     * retrieve attribute ImplementationVersion
     */
    public String getImplementationVersion() {
        m_il.enter();
        String sImplementationVersion = getMainAttributes().getValue("Implementation-Version");
        m_il.exit(sImplementationVersion);
        return sImplementationVersion;
    } 

        /**
     * retrieve attribute ImplementationTitle
     */
    public String getImplementationTitle() {
        m_il.enter();
        String sImplementationTitle = getMainAttributes().getValue("Implementation-Title");
        m_il.exit(sImplementationTitle);
        return sImplementationTitle;
    } 

        /**
     * retrieve attribute ImplementationVendor
     */
    public String getImplementationVendor() {
        m_il.enter();
        String sImplementationVendor = getMainAttributes().getValue("Implementation-Vendor");
        m_il.exit(sImplementationVendor);
        return sImplementationVendor;
    } 

        /**
     * factory loads manifest from stream
     */
    public static ManifestAttributes getInstance(InputStream is) {
        m_il.enter(is);
        ManifestAttributes mfa = null;
        if (is != null) {
            try {
                mfa = new ManifestAttributes(is);
            } catch (IOException ie) {
                m_il.exception(ie);
            }
        }
        m_il.exit(mfa);
        return mfa;
    } 

        /**
     * factory loads manifest from class path
     */
    public static ManifestAttributes getInstance(Class<?> clazz) {
        m_il.enter(clazz);
        ManifestAttributes mfa = null;
        try {
            /* get the first one available */
            URL urlManifest = clazz.getResource(sMANIFEST_RESOURCE);
            m_il.event("Initial manifest: " + urlManifest);
            /* if we are in a jar, search for the one pointing to the jar file */
            File fileJar = SpecialFolder.getJarFromClass(clazz, false);
            if ((fileJar != null) && (fileJar.isFile())) {
                URL urlJarFile = fileJar.toURI()
                                        .toURL(); // file: URL
                m_il.event("JAR file URL: " + urlJarFile);
                String sJarUrl = "jar:" + urlJarFile + "!" + sMANIFEST_RESOURCE; // jar:file:<...>!/META-INF/MANIFEST.MF
                urlManifest = new URL(sJarUrl);
                m_il.event("Manifest in JAR: " + urlManifest);
            }
            m_il.event("Using " + urlManifest);
            InputStream is = urlManifest.openStream();
            if (is != null)
                mfa = new ManifestAttributes(is);
        } catch (IOException ie) {
            m_il.exception(ie);
        }
        m_il.exit(mfa);
        return mfa;
    } 

        /**
     * factory loads manifest from class path
     */
    public static ManifestAttributes getInstance() {
        return getInstance(ManifestAttributes.class);
    } 

} 
