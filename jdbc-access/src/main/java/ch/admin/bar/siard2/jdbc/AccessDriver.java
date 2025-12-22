/*== AccessDriver.java =================================================
AccessDriver implements a wrapped Jackcess Driver for MS Access.
Application : SIARD2
Description : 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Zurich, Switzerland
Created    : 04.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import ch.enterag.utils.jdbc.BaseDriver;

import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;


/** AccessDriver implements a wrapped Jackcess Driver for MS Access.
 * @author Hartwig Thomas
 */
public class AccessDriver extends BaseDriver implements Driver {
    /** driver version */
    public static final String sVERSION = "1.0";
    /** protocol sub scheme for Access JDBC URL */
    public static final String sACCESS_SCHEME = "access";
    /** URL prefix for Access JDBC URL */
    public static final String sACCESS_URL_PREFIX = sJDBC_SCHEME + ":" + sACCESS_SCHEME + ":";
    /** user property */
    public static final String sPROP_USER = "user";
    /** user default value */
    public static final String sDEFAULT_USER = "Admin";
    /** password property */
    public static final String sPROP_PASSWORD = "password";
    /** read-only property */
    public static final String sPROP_READ_ONLY = "readonly";

    /** register this driver, when it is loaded */
    static {
        register();
    }

    /** PrintWriter for database logging */
    private final PrintWriter _pwLogWriter = new PrintWriter(System.out);
    private String _sUser = null;
    private String _sPassword = null;
    private boolean _bReadOnly = true;

    /** @return driver version */
    static String getVersion() {
        return sVERSION;
    }

    /** URL for given database file
     * @param sDatabasePath file name of database
     * @return JDBC URL.
     */
    public static String getUrl(String sDatabasePath) {
        String sUrl = sDatabasePath;
        if (!sDatabasePath.startsWith(sACCESS_URL_PREFIX)) {
            File fileDatabase = new File(sDatabasePath);
            sUrl = sACCESS_URL_PREFIX + fileDatabase.getAbsolutePath();
        }
        return sUrl;
    }

    /** database file from URL
     * @param sUrl JDBC URL
     * @return database path or null, if not a JDBC access URL.
     */
    public static String getDatabaseName(String sUrl) {
        String sDatabaseName = null;
        if (sUrl.startsWith(sACCESS_URL_PREFIX)) sDatabaseName = sUrl.substring(sACCESS_URL_PREFIX.length());
        return sDatabaseName;
    }

    /** register this driver */
    public static void register() {
        /* instantiate and register */
        try {
            DriverManager.registerDriver(new AccessDriver());
        } catch (SQLException se) {
            System.err.println("Registering of " + AccessDriver.class.getName() + " with DriverManager failed!");
        }
    }


    /** {@link Driver} */
    @Override
    public boolean acceptsURL(String sUrl) throws SQLException {
        boolean bAcceptsUrl = getDatabaseName(sUrl) != null;
        return bAcceptsUrl;
    }


    /** {@link Driver} */
    @Override
    public Connection connect(String sUrl, Properties propsInfo) throws SQLException {
        Connection conn = null;
        if (acceptsURL(sUrl)) {
            /***
             for (Iterator<Object> iterKeys = propsInfo.keySet().iterator(); iterKeys.hasNext(); )
             {
             String sKey = (String)iterKeys.next();
             String sValue = propsInfo.getProperty(sKey);
             System.out.println(sKey+": "+sValue);
             }
             ***/
            String sDatabaseName = getDatabaseName(sUrl);
            _sUser = propsInfo.getProperty(sPROP_USER, sDEFAULT_USER);
            _sPassword = propsInfo.getProperty(sPROP_PASSWORD);
            String sReadOnly = propsInfo.getProperty(sPROP_READ_ONLY);
            _bReadOnly = false;
            if (sReadOnly != null) _bReadOnly = Boolean.parseBoolean(propsInfo.getProperty(sPROP_READ_ONLY));
            conn = new AccessConnection(sDatabaseName, _sUser, _sPassword, _bReadOnly, _pwLogWriter);
        }
        return conn;
    }


    /** {@link Driver} */
    @Override
    public int getMajorVersion() {
        int iMajorVersion = -1;
        String sVersion = getVersion();
        String[] asVersion = sVersion.split("\\.");
        if (asVersion.length > 0) iMajorVersion = Integer.parseInt(asVersion[0]);
        return iMajorVersion;
    }


    /** {@link Driver} */
    @Override
    public int getMinorVersion() {
        int iMinorVersion = -1;
        String sVersion = getVersion();
        String[] asVersion = sVersion.split("\\.");
        if (asVersion.length > 1) iMinorVersion = Integer.parseInt(asVersion[1]);
        return iMinorVersion;
    }


    /** {@link Driver} */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String sUrl, Properties propsInfo) throws SQLException {
        DriverPropertyInfo[] adpi = new DriverPropertyInfo[3];
        /* User */
        adpi[0] = new DriverPropertyInfo(sPROP_USER, propsInfo.getProperty(sPROP_USER));
        adpi[0].description = "User used for opening the MS Access file. (Default: " + sDEFAULT_USER + ")";
        adpi[0].required = false;
        /* Password */
        adpi[1] = new DriverPropertyInfo(sPROP_PASSWORD, propsInfo.getProperty(sPROP_PASSWORD));
        adpi[1].description = "Password used for opening the MS Access file.";
        adpi[1].required = false;
        /* ReadOnly */
        boolean bReadOnly = Boolean.parseBoolean(propsInfo.getProperty(sPROP_READ_ONLY));
        adpi[2] = new DriverPropertyInfo(sPROP_READ_ONLY, String.valueOf(bReadOnly));
        adpi[2].description = "Indicates if database is to be opened/created for reading or writing. (Default: true)";
        adpi[2].required = false;
        adpi[2].choices = new String[2];
        adpi[2].choices[0] = "false";
        adpi[2].choices[1] = "true";
        return adpi;
    }


    /** {@link Driver} */
    @Override
    public boolean jdbcCompliant() {
        return true;
    }


    /** {@link Driver} for JDK 1.7 */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("ParentLogger not supported!");
    }

}
