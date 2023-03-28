/*======================================================================
Accessing version and build number from MANIFEST.MF 
Application : Configuration Utilities
Description : Access the version and build number from MANIFEST.MF. 
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2012
Created    : 09.05.2012, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.configuration;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.jar.*;
import ch.enterag.utils.io.*;
import ch.enterag.utils.logging.*;

/*====================================================================*/
/** ManifestAttributes extends Manifest for easy access to attributes.
 * @author Hartwig
 */
public class ManifestAttributes extends Manifest
{
  private static IndentLogger _il = IndentLogger.getIndentLogger(ManifestAttributes.class.getName());
  private static final String sMANIFEST_RESOURCE = "/META-INF/MANIFEST.MF"; 

  /*------------------------------------------------------------------*/
  /** constructor loads manifest from InputStream
   * @param is InputStream with manifest.
   * @throws IOException if an I/O error occurred.  
   */
  public ManifestAttributes(InputStream is)
    throws IOException
  {
    super(is);
    _il.enter();
    _il.exit();
  } /* constructor ManifestAttributes */
  
  /*------------------------------------------------------------------*/
  /** retrieve attribute ImplementationVersion
   * @return value of attribute ImplementationVersion. 
   */
  public String getImplementationVersion()
  {
    _il.enter();
    String sImplementationVersion = getMainAttributes().getValue("Implementation-Version");
    _il.exit(sImplementationVersion);
    return sImplementationVersion;
  } /* getImplementationVersion */
  
  /*------------------------------------------------------------------*/
  /** retrieve attribute ImplementationTitle
   * @return value of attribute ImplementationTitle
   */
  public String getImplementationTitle()
  {
    _il.enter();
    String sImplementationTitle = getMainAttributes().getValue("Implementation-Title");
    _il.exit(sImplementationTitle);
    return sImplementationTitle;
  } /* getImplementationTitle */
  
  /*------------------------------------------------------------------*/
  /** retrieve attribute ImplementationVendor
   * @return value of attribute ImplementationVendor
   */
  public String getImplementationVendor()
  {
    _il.enter();
    String sImplementationVendor = getMainAttributes().getValue("Implementation-Vendor");
    _il.exit(sImplementationVendor);
    return sImplementationVendor;
  } /* getImplementationVendor */
  
  /*------------------------------------------------------------------*/
  /** retrieve attribute ImplementationVersion
   * @return value of attribute ImplementationVersion
   */
  public String getSpecificationVersion()
  {
    _il.enter();
    String sSpecificationVersion = getMainAttributes().getValue("Specification-Version");
    _il.exit(sSpecificationVersion);
    return sSpecificationVersion;
  } /* getSpecificationVersion */
  
  /*------------------------------------------------------------------*/
  /** retrieve attribute SpecificationTitle
   * @return value of attribute SpecificationTitle
   */
  public String getSpecificationTitle()
  {
    _il.enter();
    String sSpecificationTitle = getMainAttributes().getValue("Specification-Title");
    _il.exit(sSpecificationTitle);
    return sSpecificationTitle;
  } /* getSpecificationTitle */
  
  /*------------------------------------------------------------------*/
  /** retrieve attribute SpecificationVendor
   * @return value of attribute SpecificationVendor
   */
  public String getSpecificationVendor()
  {
    _il.enter();
    String sSpecificationVendor = getMainAttributes().getValue("Specification-Vendor");
    _il.exit(sSpecificationVendor);
    return sSpecificationVendor;
  } /* getSpecificationVendor */
  
  /*------------------------------------------------------------------*/
  /** retrieve attribute Built-Data
   * @return value of attribute Built-Data
   */
  public Calendar getBuiltDate()
  {
    _il.enter();
    Calendar calBuiltDate = null;
    String sBuiltDate = getMainAttributes().getValue("Built-Date");
    if (sBuiltDate != null)
    {
      SimpleDateFormat sdf = new SimpleDateFormat("dd. MMMM yyyy",Locale.US);
      try 
      { 
        Date date = sdf.parse(sBuiltDate);
        calBuiltDate = new GregorianCalendar();
        calBuiltDate.setTime(date);
      }
      catch(ParseException pe) { _il.exception(pe); }
    }
    _il.exit(calBuiltDate);
    return calBuiltDate;
  } /* getBuiltDate */
  
  /*------------------------------------------------------------------*/
  /** factory loads manifest from stream
   * @param is stream with manifest.  
   * @return instance of ManifestAttributes.  
   */
  public static ManifestAttributes getInstance(InputStream is)
  {
    _il.enter(is);
    ManifestAttributes mfa = null;
    if (is != null)
    {
      try { mfa = new ManifestAttributes(is); }
      catch(IOException ie) { _il.exception(ie); }
    }
    _il.exit(mfa);
    return mfa;
  } /* getInstance */
  
  /*------------------------------------------------------------------*/
  /** factory loads manifest from class path
   * @param clazz class for resource loading.
   * @return instance of ManifestAttributes.  
   */
  public static ManifestAttributes getInstance(Class<?> clazz)
  {
    _il.enter(clazz);
    ManifestAttributes mfa = null;
    try
    {
      /* get the first one available */
      URL urlManifest = clazz.getResource(sMANIFEST_RESOURCE);
      _il.event("Initial manifest: "+String.valueOf(urlManifest));
      File fileJar = SpecialFolder.getJarFromClass(clazz,false);
      if (fileJar != null)
      {
        /* if we are in a jar, search for the one pointing to the jar file */
      	if (fileJar.isFile())
      	{
  	      URL urlJarFile = fileJar.toURI().toURL(); // file: URL
  	      _il.event("JAR file URL: "+String.valueOf(urlJarFile));
  	      String sJarUrl = "jar:"+urlJarFile.toString()+"!"+sMANIFEST_RESOURCE; // jar:file:<...>!/META-INF/MANIFEST.MF
  	      urlManifest = new URL(sJarUrl);
  	      _il.event("Manifest in JAR: "+urlManifest.toString());
      	}
      	else /* otherwise get it from the file system */
      	{
          File fileManifest = null;
          if (fileJar.getAbsolutePath().contains("/out/production/")) {
            fileManifest = new File(fileJar.getAbsolutePath() + sMANIFEST_RESOURCE); // <...>/META-INF/MANIFEST.MF for ant setup
          } else {
            fileManifest = new File(fileJar.getParentFile().getParentFile().getParentFile().getAbsolutePath() + "/tmp/jar/MANIFEST.MF"); // <...>/tmp/jar/MANIFEST.MF for gradle setup
          }
          urlManifest = fileManifest.toURI().toURL();
          System.out.println(urlManifest);
      	}
      }     
      _il.event("Using "+String.valueOf(urlManifest));
      InputStream is = urlManifest.openStream();
      if (is != null)
        mfa = new ManifestAttributes(is);
    }
    catch (IOException ie) { _il.exception(ie); }
    _il.exit(mfa);
    return mfa;
  } /* getInstance */
  
  /*------------------------------------------------------------------*/
  /** factory loads manifest from class path  
   * @return instance of ManifestAttributes.  
   */
  public static ManifestAttributes getInstance()
  {
    return getInstance(ManifestAttributes.class);
  } /* getInstance */
  
} /* ManifestVersion */
