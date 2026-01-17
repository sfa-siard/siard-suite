/*== SpecialFolder.java ================================================
SpecialFolder implements a number of special folders.
Version     : $Id: SpecialFolder.java 607 2016-02-23 12:18:01Z hartwig $
Application : Utilities
Description : SpecialFolder is an abstract class that statically 
              implements a number of special folders.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2007
Created    : 31.10.2007, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.io;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import ch.enterag.utils.*;
import ch.enterag.utils.lang.*;

/*====================================================================*/
/** SpecialFolder is an abstract class that statically 
    implements a number of special folders.
 @author Hartwig Thomas
 */
public abstract class SpecialFolder
{
  /*====================================================================
  (private) constants
  ====================================================================*/
	private static final String sJAVA_SETTINGS_DIRECTORY = ".java";
  private static final String sDATA_DIRECTORY = "data";
  private static final String sAPPLICATIONS_DIRECTORY = "applications";
  private static final String sTEMP_DIRECTORY = "temp";
	
  /*====================================================================
  (private) data members
  ====================================================================*/

  /*====================================================================
  methods
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** returns the JAR file which contains the given class or the
   * root folder for the class.
   * @param sClassName path of class in the format /package/Class.class
   * @param bReport true, if success must be reported on System.out.
   * @return File for Jar containing class or for folder containing 
   *         the class, if class is not in a Jar.
   */
  public static File getJarFromClass(String sClassName, boolean bReport)
  {
    if (bReport)
      System.out.println("getJarFromClass("+sClassName+")");
    File fileJar = new File(".");
    try
    {
      // Class<?> cls = Class.forName(sClassName.substring(0,sClassName.length()-".class".length()).replace('/', '.'));
      // URL url = cls.getResource(cls.getSimpleName()+".class");
      URL url = SpecialFolder.class.getResource(sClassName);
      if (url == null)
      {
        if (bReport)
          System.err.println("Class "+sClassName+" not found!");
      }
      else if (url.getProtocol().equals("jar"))
      {
        if (bReport)
          System.out.println("URL: "+url.toString());
        String sJar = URLDecoder.decode(url.getPath(),SU.sUTF8_CHARSET_NAME);
        if (bReport)
          System.out.println("JAR: "+sJar);
        int iExclamation = sJar.indexOf('!');
        if (iExclamation < 0)
          iExclamation = sJar.length();
        if (sJar.startsWith("file:"))
          sJar = sJar.substring(5,iExclamation);
        if ((sJar.length() > 2) && (sJar.charAt(0) == '/') && (sJar.charAt(2) == ':'))
          sJar = sJar.substring(1);
        fileJar = new File(sJar);
        if (bReport)
          System.out.println("fileJar: "+fileJar.getAbsolutePath());
      }
      else
      {
        System.err.println("Not in a JAR!");
        if (url.getProtocol().equals("file"))
        {
          String sFile = URLDecoder.decode(url.getPath(),SU.sUTF8_CHARSET_NAME);
          if (sFile.endsWith(sClassName))
            sFile = sFile.substring(0,sFile.length() - sClassName.length());
          if ((sFile.length() > 2) && (sFile.charAt(0) == '/') && (sFile.charAt(2) == ':'))
            sFile = sFile.substring(1);
          fileJar = new File(sFile);
        }
      }
    }
    catch(UnsupportedEncodingException uee) { System.err.println(EU.getExceptionMessage(uee)); }
    // catch(ClassNotFoundException cnfe) { System.err.println(EU.getExceptionMessage(cnfe)); }
    return fileJar;
  } /* getJarFromClass */
  
  /*------------------------------------------------------------------*/
  /** returns the JAR file which contains the given class or the root
   * folder for the class.
   * @param classAnchor class in the format /package/Class.class
   * @param bReport true, if success must be reported on System.out.
   * @return File for Jar containing class or folder containing 
   *         the class, if class is not in a Jar.
  */
  public static File getJarFromClass(Class<?> classAnchor, boolean bReport)
  {
    return getJarFromClass("/"+classAnchor.getName().replaceAll("\\.", "/")+".class", bReport);
  } /* getJarFromClass */
  
  /*------------------------------------------------------------------*/
  /** returns JAR file which contains the class with the main() method 
   * that started the program.
   * @return File for Jar containing class or folder containing the
   *         class with the main() method that started the program.
   */         
  public static File getMainJar()
  {
    return getJarFromClass(Threads.getMainClass(Threads.getMainThread()),false);
  } /* getMainJar */
  
  /*------------------------------------------------------------------*/
  /** returns JAR file which contains the first class in the current thread.
   * @return File for Jar containing class or folder containing the
   *         class with the main() method that started the current thread.
   */         
  public static File getCurrentMainJar()
  {
    return getJarFromClass(Threads.getMainClass(Thread.currentThread()),false);
  } /* getMainCurrentJar */
  
  /*------------------------------------------------------------------*/
  /** search for given file name in the folders of the PATH environment variable.
   * @param sFile file name.
   * @return full file found or null.
   */
  public static File findInPath(String sFile)
  {
    File fileFound = null;
    File file = new File(sFile);
    if (file.exists())
      fileFound = file;
    else
    {
      String[] asPath = System.getenv("PATH").split(File.pathSeparator);
      for (int i = 0; (fileFound == null) && (i < asPath.length); i++)
      {
        String sDirectory = asPath[i];
        file = new File(sDirectory, sFile);
        if (file.isFile() && file.exists())
          fileFound = file;
      }
    }
    return fileFound;
  } /* findInPath */
  
  /*------------------------------------------------------------------*/
  /** returns the user's home directory.
   * For Windows: C:\Users\&lt;user&gt;
   * For UNIX: ~
   * @return Current user's home directory.
  */
	public static String getUserHome()
	{
		return (String)System.getProperties().get("user.home");
	} /* getUserHome */

  /*------------------------------------------------------------------*/
	/** returns the Desktop folder in the user's home directory.
   * For Windows: C:\Users\&lt;user&gt;\Desktop
   * For UNIX: ~/Desktop
	 * @return Current user's desktop folder.
	 */
	public static String getDesktopFolder()
	{
	  File fileHome = new File(getUserHome());
	  return fileHome.getAbsolutePath()+File.separator+"Desktop";
	} /* getDesktopFolder */
	
  /*------------------------------------------------------------------*/
  /** returns the data directory under the user's home directory,
   *  creating it if it did not exist.
   * For Windows: %APPDATA%\&lt;app&gt;
   * For UNIX: ~/data/&lt;app&gt;
   * @param sApplicationName name of the application to be used for the
   *                         application data directory.
   * @return Current user's application data directory.
  */
  public static String getUserDataHome(String sApplicationName)
  {
    String sAppData = System.getenv("APPDATA");
    if (sAppData == null)
      sAppData = getUserHome()+File.separator+sDATA_DIRECTORY;
    String sUserAppHome = sAppData+File.separator+sApplicationName;
    /* if someone requests this directory they probably want it to exist */
    File fileUserAppHome = new File(sUserAppHome);
    if (!fileUserAppHome.exists())
      fileUserAppHome.mkdirs();
    return sUserAppHome;
  } /* getUserDataHome */
  
  /*------------------------------------------------------------------*/
  /** removes the data directory under the user's home directory,
   *  if it is empty.
   * For Windows: %APPDATA%\&lt;app&gt;
   * For UNIX: ~/data/&lt;app&gt;
   * @param sApplicationName name of the application to be used for the
   *                         application data directory.
  */
	public static void removeUserDataHome(String sApplicationName)
	{
		Path pathUserAppHome = Paths.get(getUserDataHome(sApplicationName));
		try { Files.deleteIfExists(pathUserAppHome); }
		catch (IOException ie) { System.err.println(ie.getClass().getName()+": "+ie.getMessage()); }
	} /* removeUserDataHome */
	
  /*------------------------------------------------------------------*/
  /** returns the local directory under the user's home directory,
   *  creating it if it did not exist.
   * For Windows: %LOCALAPPDATA%\&lt;app&gt;
   * For UNIX: ~/applications/&lt;app&gt;
   * @param sApplicationName name of the application to be used for the
   *                         local directory.
   * @return Current user's local data directory.
  */
  public static String getUserLocalHome(String sApplicationName)
  {
    String sLocalAppData = System.getenv("LOCALAPPDATA");
    if (sLocalAppData == null)
      sLocalAppData = getUserHome()+File.separator+sAPPLICATIONS_DIRECTORY;
    String sUserLocalHome = sLocalAppData+File.separator+sApplicationName;
    /* if someone requests this directory they probably want it to exist */
    File fileUserLocalHome = new File(sUserLocalHome);
    if (!fileUserLocalHome.exists())
      fileUserLocalHome.mkdirs();
    return sUserLocalHome;
  } /* getUserLocalHome */
  
  /*------------------------------------------------------------------*/
  /** removes the local directory under the user's home directory,
   *  if it is empty.
   * For Windows: %LOCALAPPDATA%\&lt;app&gt;
   * For UNIX: ~/applications/&lt;app&gt;
   * @param sApplicationName name of the application to be used for the
   *                         local directory.
  */
  public static void removeUserLocalHome(String sApplicationName)
  {
    Path pathUserLocalHome = Paths.get(getUserLocalHome(sApplicationName));
    try { Files.deleteIfExists(pathUserLocalHome); }
    catch (IOException ie) { System.err.println(ie.getClass().getName()+": "+ie.getMessage()); }
  } /* removeUserLocalHome */
  
  /*------------------------------------------------------------------*/
  /** returns the temp directory for the user, creating it if it did 
   * not exist.
   * For Windows: %TEMP%
   * For UNIX: ~/temp
   * @return Current user's temp directory.
  */
  public static String getUserTemp()
  {
    String sTemp = System.getenv("TEMP");
    if (sTemp == null)
      sTemp = getUserHome()+File.separator+sTEMP_DIRECTORY;
    /* if someone requests this directory they probably want it to exist */
    File fileTemp = new File(sTemp);
    if (!fileTemp.exists())
      fileTemp.mkdirs();
    return sTemp;
  } /* getUserLocalHome */
  
  /*------------------------------------------------------------------*/
  /** returns the JAVA settings directory under the user's home directory,
   *  creating it if it did not exist.
   * For Windows: $HOMEDRIVE$$HOMEPATH$\.java;
   * For UNIX: ~/.java;
   * @return Current user's JAVA settings directory.
  */
	public static String getUserJavaSettings()
	{
		String sUserJavaSettings = getUserHome()+File.separator+sJAVA_SETTINGS_DIRECTORY;
		/* if someone requests this directory they probably want it to exist */
		File fileUserJavaSettings = new File(sUserJavaSettings);
		if (!fileUserJavaSettings.exists())
			fileUserJavaSettings.mkdir();
		return sUserJavaSettings;
	} /* getUserJavaSettings */
	
} /* class SpecialFolder */
