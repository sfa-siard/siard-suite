/*======================================================================
MsSqlDriver implements a wrapped MSSQL Driver.
Version     : $Id: $
Application : SIARD2
Description : MsSqlDriver implements a wrapped MSSQL Driver.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import ch.enterag.utils.jdbc.BaseDriver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;


/** MsSqlDriver implements a wrapped MSSQL Driver.
 * @author Hartwig Thomas
 */
public class MsSqlDriver
        extends BaseDriver
        implements Driver {
    /** logger */
    /** protocol sub scheme for SQL Server JDBC URL */
    public static final String sSQLSERVER_SCHEME = "sqlserver";
    /** URL prefix for SQL Server JDBC URL */
    public static final String sSQLSERVER_URL_PREFIX = sJDBC_SCHEME + ":" + sSQLSERVER_SCHEME + ":";

    /** URL for database name.
     * @param sDatabaseName host:port;databaseName=database, e.g. localhost:1433;databaseName=testdb
     * @return JDBC URL.
     */
    public static String getUrl(String sDatabaseName) {
        String sUrl = sDatabaseName;
        if (!sUrl.startsWith(sSQLSERVER_URL_PREFIX))
            sUrl = sSQLSERVER_URL_PREFIX + "//" + sDatabaseName;
        return sUrl;
    }

    /** register this driver, replacing original MS SQL driver
     */
    public static void register() {
        try {
            BaseDriver.register(new MsSqlDriver(), "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://localhost:1433:databaseName=testdb");
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /** replace MS SQL driver by this, when this class is loaded */
    static {
        register();
    }


    /** {@inheritDoc}*/
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        boolean bAccepts = url.startsWith(sSQLSERVER_URL_PREFIX);
        return bAccepts;
    }


    /** {@inheritDoc}
     * returns the appropriately wrapped MSSQL Connection.
     */
    @Override
    public Connection connect(String url, Properties info)
            throws SQLException {
        Connection conn = super.connect(url, info);
        if (conn != null)
            conn = new MsSqlConnection(super.connect(url, info));
        return conn;
    }

}
