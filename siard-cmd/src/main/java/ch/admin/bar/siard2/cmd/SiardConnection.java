/*======================================================================
Loads the appropriate JDBC driver associated with a JDBC URL.
Application : Siard2
Description : Loads the appropriate JDBC driver associated with a JDBC URL.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 08.05.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.jdbc.AccessDriver;
import ch.admin.bar.siard2.jdbc.Db2Driver;
import ch.admin.bar.siard2.jdbc.MsSqlDriver;
import ch.admin.bar.siard2.jdbc.MySqlDriver;
import ch.admin.bar.siard2.jdbc.OracleDriver;
import ch.admin.bar.siard2.jdbc.PostgresDriver;
import ch.enterag.utils.EU;
import ch.enterag.utils.io.SpecialFolder;
import ch.enterag.utils.jdbc.BaseDriver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/*====================================================================*/
/** Loads the appropriate JDBC driver associated with a JDBC URL.
 @author Hartwig Thomas
 */
@Slf4j
@SuppressWarnings("serial")
public class SiardConnection extends Properties
{
  /** singleton */
  private static SiardConnection _sc = null;
  public static final int iDEFAULT_QUERY_TIMEOUT_SECONDS = 75;
  public static final int iDEFAULT_LOGIN_TIMEOUT_SECONDS = 300;

  /*--------------------------------------------------------------------*/
  /** Properties file containing the map from JDBC sub schemes to
   * JDBC driver class names is located in ../etc/jdbcdrivers.properties
   * relative to the JAR file containing this class.
   * @return name of properties file.
   */
  private static final String sCONFIG_FOLDER = "etc";
  private static final String sJDBCDRIVERS_PROPERTIES = "jdbcdrivers.properties";
  private static final String sTITLE_SUFFIX = "_title";
  private static final String sSAMPLE_SUFFIX = "_sample";
  private static final String sOPTION_SUFFIX = "_option";
  private static File getJdbcDriversPropertiesFile()
  {
    File fileDrivers = null;
    String sFileDrivers = System.getProperty("ch.admin.bar.siard2.cmd.drivers");
    if (sFileDrivers != null)
      fileDrivers = new File(sFileDrivers);
    else
    {
      fileDrivers = SpecialFolder.getMainJar();
      // System.out.println("Main JAR: "+fileDrivers.getAbsolutePath());
      if (fileDrivers.isFile())
        fileDrivers = fileDrivers.getParentFile();
      fileDrivers = fileDrivers.getParentFile();
      fileDrivers = new File(fileDrivers.getAbsolutePath()+File.separator+
        sCONFIG_FOLDER+File.separator+
        sJDBCDRIVERS_PROPERTIES);
    }
    // System.out.println("Drivers file: "+fileDrivers.getAbsolutePath());
    return fileDrivers;
  } /* getJdbcDriversPropertiesFile */

  /*------------------------------------------------------------------*/
  /** Default properties mapping JDCB sub schemes to JDBC driver class names.
   * @return default properties.
   */
  private static Properties getJdbcDriversDefaultProperties()
  {
    Properties propDrivers = new Properties();
    propDrivers.put(PostgresDriver.sPOSTGRES_SCHEME, PostgresDriver.class.getName());
    propDrivers.put(PostgresDriver.sPOSTGRES_SCHEME+sTITLE_SUFFIX, "PostgreSQL");
    propDrivers.put(PostgresDriver.sPOSTGRES_SCHEME+sSAMPLE_SUFFIX, "jdbc:postgresql://dbserver.enterag.ch:5432/testdb");
    propDrivers.put(OracleDriver.sORACLE_SCHEME, OracleDriver.class.getName());
    propDrivers.put(OracleDriver.sORACLE_SCHEME+sTITLE_SUFFIX, "Oracle");
    propDrivers.put(OracleDriver.sORACLE_SCHEME+sSAMPLE_SUFFIX, "jdbc:oracle:thin:@dbserver.enterag.ch:1521:orcl");
    propDrivers.put(MsSqlDriver.sSQLSERVER_SCHEME, MsSqlDriver.class.getName());
    propDrivers.put(MsSqlDriver.sSQLSERVER_SCHEME+sTITLE_SUFFIX, "SQL Server");
    propDrivers.put(MsSqlDriver.sSQLSERVER_SCHEME+sSAMPLE_SUFFIX, "jdbc:sqlserver://dbserver.enterag.ch:1433;databaseName=testdb");
    propDrivers.put(MySqlDriver.sMYSQL_SCHEME, MySqlDriver.class.getName());
    propDrivers.put(MySqlDriver.sMYSQL_SCHEME+sTITLE_SUFFIX, "SQL Server");
    propDrivers.put(MySqlDriver.sMYSQL_SCHEME+sSAMPLE_SUFFIX, "jdbc:mysql://dbserver.enterag.ch:3306/testdb");
    propDrivers.put(Db2Driver.sDB2_SCHEME, Db2Driver.class.getName());
    propDrivers.put(Db2Driver.sDB2_SCHEME+sTITLE_SUFFIX, "DB/2");
    propDrivers.put(Db2Driver.sDB2_SCHEME+sSAMPLE_SUFFIX, "jdbc:db2:dbserver.enterag.ch:50000/testdb");
    propDrivers.put(AccessDriver.sACCESS_SCHEME, AccessDriver.class.getName());
    propDrivers.put(AccessDriver.sACCESS_SCHEME+sTITLE_SUFFIX, "Microsoft Access");
    propDrivers.put(AccessDriver.sACCESS_SCHEME+sSAMPLE_SUFFIX, "jdbc:access:D:\\Projekte\\SIARD2\\JdbcAccess\\testfiles\\dbfile.mdb");
    return propDrivers;
  } /* getJdbcDriversDefaultProperties */

  /*------------------------------------------------------------------*/
  /** make keys() return elements in alphabetic order.
   * This produces a nicer properties file, when store() is called.
   */
  @Override
  public synchronized Enumeration<Object> keys()
  {
    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
  } /* keys */

  /*------------------------------------------------------------------*/
  /** constructor loads properties.
   */
  private SiardConnection()
  {
    super();
    File fileJdbcDrivers = getJdbcDriversPropertiesFile();
    try
    {
      if (fileJdbcDrivers.exists())
      {
        FileReader fr = new FileReader(fileJdbcDrivers);
        load(fr);
        fr.close();
      }
      else
      {
        Properties propDefault = getJdbcDriversDefaultProperties();
        Set<String> setSchemes = propDefault.stringPropertyNames();
        for (Iterator<String> iterScheme = setSchemes.iterator(); iterScheme.hasNext(); )
        {
          String sScheme = iterScheme.next();
          setProperty(sScheme,propDefault.getProperty(sScheme));
        }
        FileWriter fw = new FileWriter(fileJdbcDrivers);
        store(fw, "Map from JDBC sub schema to title, sample URL and JDBC Driver class to be loaded.");
        fw.close();
      }
    }
    catch(IOException ie) { System.err.println(EU.getExceptionMessage(ie)); }
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** factory
   * @return SiarcConection instance.
   */
  public static SiardConnection getSiardConnection()
  {
    if (_sc == null)
      _sc = new SiardConnection();
    return _sc;
  } /* getSiardConnection */

  static String extractSubSchema(final String jdbcUrl) {
    val split = jdbcUrl.split(":");

    if (split.length < 3 || !split[0].equals("jdbc")) {
      throw new IllegalArgumentException(jdbcUrl + " is not a valid JDBC-Url.");
    }

    return split[1];
  }

  @SneakyThrows
  public Driver loadValidDriver(String jdbcUrl) {
    val subSchema = extractSubSchema(jdbcUrl);

    val driverClassName = getProperty(subSchema);
    if (driverClassName == null) {
      throw new IllegalArgumentException("No driver specified for url " + jdbcUrl);
    }
    val driverClass = Class.forName(driverClassName);
    val driver = (Driver) driverClass.newInstance();

    if (!driver.acceptsURL(jdbcUrl)) {
      throw new IllegalStateException(String.format(
              "Driver %s does not accept url %s.",
              driver.getClass().getCanonicalName(),
              jdbcUrl));
    }

    LOG.info("'{}' as driver loaded for url '{}'", driver.getClass().getCanonicalName(), jdbcUrl);

    return driver;
  }

  public Connection createValidConnection(
          String jdbcUrl,
          String user,
          String password
  ) throws SQLException {
    logDrivers(jdbcUrl);

    java.util.Properties info = new java.util.Properties();

    if (user != null) {
      info.put("user", user);
    }
    if (password != null) {
      info.put("password", password);
    }

    val driver = loadValidDriver(jdbcUrl);
    val connection = driver.connect(jdbcUrl, info);

    LOG.info("Created connection of type '{}'for url '{}'", connection.getClass().getCanonicalName(), jdbcUrl);

    return connection;
  }

    @SneakyThrows
    private static void logDrivers(final String jdbcUrl) {
        val sb = new StringBuilder()
                .append("Registered JDBC drivers and its compatibility for url '")
                .append(jdbcUrl)
                .append("':");

        val enumeration = DriverManager.getDrivers();
        while (enumeration.hasMoreElements()) {
            val driver = enumeration.nextElement();
            sb.append("\n - ")
                    .append(driver.getClass().getCanonicalName())
                    .append(": ")
                    .append(driver.acceptsURL(jdbcUrl));
        }

        LOG.info(sb.toString());
    }

  /*--------------------------------------------------------------------*/
  /** load JDBC driver for given JDBC URL.
   * @param sJdbcUrl JDBC URL.
   * @return null, if no error occurred, otherwise English error message.
   */
  public String loadDriver(String sJdbcUrl)
  {
    // System.out.println("loadDriver: "+sJdbcUrl);
    String sError = null;

    if (sJdbcUrl == null)
      sError = "JDBC URL of database must be given!";
    else if (sJdbcUrl.startsWith(BaseDriver.sJDBC_SCHEME))
    {
      // System.out.println("URL starts with "+BaseDriver.sJDBC_SCHEME);
      String sSubScheme = sJdbcUrl.substring(BaseDriver.sJDBC_SCHEME.length()+1);
      int iSubScheme = sSubScheme.indexOf(":");
      if (iSubScheme >= 0)
      {
        sSubScheme = sSubScheme.substring(0,iSubScheme);
        // System.out.println("Sub scheme "+sSubScheme);
        /* open JDBC driver */
        String sJdbcDriverClass = getProperty(sSubScheme);
        if (sJdbcDriverClass != null)
        {
          // System.out.println("Driver class "+sJdbcDriverClass);
          try { Class.forName(sJdbcDriverClass); }
          catch(ClassNotFoundException cnfe)
          {
            sError = "Driver "+sJdbcDriverClass+" could not be loaded ("+EU.getExceptionMessage(cnfe)+")!";
          }
        }
        else
          sError = "No driver found for sub scheme \""+sSubScheme+"\"!";
      }
      else
        sError = "JDBC URL "+sJdbcUrl+" must contain a subs scheme terminated by colon (\":\")!";
    }
    else
      sError = "JDBC URL "+sJdbcUrl+" must start with \""+BaseDriver.sJDBC_SCHEME+"\"!";
    if (sError != null)
      System.err.println(sError);
    return sError;
  } /* loadDriver */

  /*--------------------------------------------------------------------*/
  /** getSchemes returns the list of JDBC schemes configured in properties.
   * @return list of JDBC schemes configured in properties.
   */
  public String[] getSchemes()
  {
    List<String> listSchemes = new ArrayList<String>();
    for (Enumeration<Object> enumKeys = keys(); enumKeys.hasMoreElements(); )
    {
      String sKey = (String)enumKeys.nextElement();
      if ((!sKey.endsWith(sTITLE_SUFFIX)) && (!sKey.endsWith(sSAMPLE_SUFFIX)) && (!sKey.endsWith(sOPTION_SUFFIX)))
        listSchemes.add(sKey);
    }
    return listSchemes.toArray(new String[]{});
  } /* getSchemes */

  /*--------------------------------------------------------------------*/
  /** getDriverClass returns the name of the JDBC driver class for a JDBC scheme.
   * @param sScheme JDBC scheme.
   * @return name of the JDBC driver class associated with this scheme.
   */
  public String getDriverClass(String sScheme)
  {
    return getProperty(sScheme);
  } /* getDriverClass */

  /*--------------------------------------------------------------------*/
  /** getTitle returns the title for the database type for a JDBC scheme.
   * @param sScheme JDBC scheme.
   * @return title for the database type for a JDBC scheme.
   */
  public String getTitle(String sScheme)
  {
    return getProperty(sScheme+sTITLE_SUFFIX);
  } /* getTitle */

  /*--------------------------------------------------------------------*/
  /** getOptions returns the number of sample JDBC URLs for the JDBC scheme.
   * @param sScheme JDBC scheme.
   * @return number of sample JDBC URLs for the JDBC scheme.
   */
  public int getOptions(String sScheme)
  {
    return getProperty(sScheme+sSAMPLE_SUFFIX).split("\\|").length;
  } /* getOptions */

  /*--------------------------------------------------------------------*/
  /** getSampleUrl returns a sample JDBC URL for the JDBC scheme.
   * Either host or folder must not be null.
   * @param sScheme JDBC scheme.
   * @param sHost host name.
   * @param sFolder folder name.
   * @param sDatabase database name.
   * @param iOption index of sample URL (separated by "|")
   * @return sample JDBC URL for the JDBC scheme.
   */
  public String getSampleUrl(String sScheme, String sHost, String sFolder, String sDatabase, int iOption)
  {
  	String sSampleUrl = getProperty(sScheme+sSAMPLE_SUFFIX);
  	sSampleUrl = sSampleUrl.split("\\|")[iOption];
    return MessageFormat.format(sSampleUrl,sHost,sFolder.replace("\\", "/"),sDatabase);
  } /* getSampleUrl */

  /*--------------------------------------------------------------------*/
  /** getOption returns a name for the sample JDBC URL for the JDBC scheme
   * with this option index.
   * @param sScheme JDBC scheme.
   * @param iOption index of sample URL (separated by "|")
   * @return name for sample JDBC URL for the JDBC scheme.
   */
  public String getOption(String sScheme, int iOption)
  {
  	String sOption = getProperty(sScheme+sOPTION_SUFFIX);
  	sOption = sOption.split("\\|")[iOption];
    return sOption;
  } /* getOption */

  /*--------------------------------------------------------------------*/
  /** getSampleUrl returns a sample JDBC URL for the JDBC scheme.
   * Either host or folder must not be null.
   * @param sScheme JDBC scheme.
   * @param sHost host name.
   * @param sFolder folder name.
   * @param sDatabase database name.
   * @return sample JDBC URL for the JDBC scheme.
   */
  public String getSampleUrl(String sScheme, String sHost, String sFolder, String sDatabase)
  {
    return getSampleUrl(sScheme,sHost,sFolder,sDatabase,0);
  } /* getSampleUrl */

  public boolean isLocal(String sScheme)
  {
  	boolean bLocal = false;
  	if (getProperty(sScheme+sSAMPLE_SUFFIX).indexOf("{0}") < 0)
  		bLocal = true;
  	return bLocal;
  }

} /* class SiardConnection */
