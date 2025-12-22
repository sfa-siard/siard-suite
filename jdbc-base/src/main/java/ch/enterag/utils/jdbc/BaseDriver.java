/*======================================================================
BaseDriver implements a wrapped Driver.
Version     : $Id: $
Application : SIARD2
Description : BaseDriver implements a wrapped Driver.
              See https://docs.oracle.com/javase/7/docs/api/java/sql/Driver.html
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 08.03.2016, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jdbc;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

import ch.enterag.utils.logging.*;

/**
 * BaseDriver implements a wrapped Driver.
 *
 * @author Hartwig Thomas
 */
public abstract class BaseDriver
        implements Driver {
    /**
     * logger
     */
    private static IndentLogger _il = IndentLogger.getIndentLogger(BaseDriver.class.getName());
    /**
     * protocol scheme for JDBC URL
     */
    public static final String sJDBC_SCHEME = "jdbc";
    /**
     * wrapped drivers
     */
    private static Map<String, Driver> _mapWrappedDrivers = new HashMap<>();
    /**
     * wrapped driver
     */
    private Driver _driverWrapped = null;

    /**
     * convert an AbstractMethodError into an SQLFeatureNotSupportedException.
     * This error indicates that the JDBC driver wrapped implements an
     * earlier version of JDBC which did not include this method.
     *
     * @param ame
     * @throws SQLFeatureNotSupportedException
     */
    private void throwUndefinedMethod(AbstractMethodError ame)
            throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Undefined JDBC method!", ame);
    }

    /**
     * from a URL of shape jdbc:<driver>:anything extract driver
     *
     * @param sUrl JDBC URL
     * @return driver.
     */
    private static String getJdbcDriver(String sUrl) {
        String sDriver = null;
        if (sUrl.startsWith("jdbc:")) {
            sDriver = sUrl.substring("jdbc:".length());
            int i = sDriver.indexOf(':');
            if (i > 0)
                sDriver = sDriver.substring(0, i);
            else
                throw new IllegalArgumentException("Second part of JDBC URL could not be extracted!");
        } else
            throw new IllegalArgumentException("JDBC URL must start with \"jdbc:\"");
        return sDriver;
    }

    public static void listRegisteredDrivers() {
        System.out.println("DriverManager:");
        for (Enumeration<Driver> enumDriver = DriverManager.getDrivers();
             enumDriver.hasMoreElements(); ) {
            Driver driver = enumDriver.nextElement();
            System.out.println("  " + driver.getClass().getName());
        }
        System.out.println("DriverMapping:");
        for (String sDriver : _mapWrappedDrivers.keySet()) {
            Driver driverMapped = _mapWrappedDrivers.get(sDriver);
            System.out.println("  " + sDriver + ": " + driverMapped.getClass().getName());
        }
    }

    /**
     * register the given driver as a wrapper.
     * Must be called exactly once before the driver used.
     *
     * @param driverRegister          driver to be registered.
     * @param sWrappedDriverClassName class name of driver to be wrapped.
     * @param sUrl                    acceptable URL for both.
     */
    protected static void register(BaseDriver driverRegister,
                                   String sWrappedDriverClassName, String sUrl)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, SQLException {
        // listRegisteredDrivers();
        Driver driverWrapped = null;
        /* deregister all old instances of base and wrapped driver */
        for (Enumeration<Driver> enumDriver = DriverManager.getDrivers();
             enumDriver.hasMoreElements(); ) {
            Driver driver = enumDriver.nextElement();
            if ((driver.getClass().getName().equals(sWrappedDriverClassName)) ||
                    (driver.getClass().equals(driverRegister.getClass()))) {
                if (driver.getClass().getName().equals(sWrappedDriverClassName))
                    driverWrapped = driver;
                DriverManager.deregisterDriver(driver);
            }
        }
        if (driverWrapped == null) {
            /* get the wrapped driver */
            Class<?> clsBaseDriver = Class.forName(sWrappedDriverClassName);
            driverWrapped = (Driver) clsBaseDriver.getConstructor().newInstance();
            DriverManager.deregisterDriver(driverWrapped);
        }
        if (driverWrapped != null) {
            _mapWrappedDrivers.put(getJdbcDriver(sUrl), driverWrapped);
            DriverManager.registerDriver(driverRegister);
        } else
            throw new ClassNotFoundException("Driver for " + sWrappedDriverClassName + " could not be loaded for URL " + sUrl);
        // listRegisteredDrivers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection connect(String url, Properties info)
            throws SQLException {
        _il.enter(url, info);
        Connection conn = null; // must return null, if the driver is not appropriate
        if (acceptsURL("jdbc:" + getJdbcDriver(url) + ":anything")) {
            _driverWrapped = _mapWrappedDrivers.get(getJdbcDriver(url));
            conn = _driverWrapped.connect(url, info);
        }
        _il.exit(conn);
        return conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        _il.enter(url);
        _il.exit(String.valueOf(false));
        throw new IllegalArgumentException("acceptsURL must be overridden!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url,
                                                Properties info) throws SQLException {
        return _driverWrapped.getPropertyInfo(url, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMajorVersion() {
        return _driverWrapped.getMajorVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinorVersion() {
        return _driverWrapped.getMinorVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean jdbcCompliant() {
        return _driverWrapped.jdbcCompliant();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getParentLogger()
            throws SQLFeatureNotSupportedException {
        Logger loggerParent = null;
        try {
            loggerParent = _driverWrapped.getParentLogger();
        } catch (AbstractMethodError ame) {
            throwUndefinedMethod(ame);
        }
        if (loggerParent == null)
            loggerParent = _il.getParent();
        return loggerParent;
    }
}
