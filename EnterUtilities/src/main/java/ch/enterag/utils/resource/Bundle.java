/*======================================================================
Bundle implements a UTF-8 string pool inspired by PropertyResourceBundle. 
Version     : $Id: Bundle.java 607 2016-02-23 12:18:01Z hartwig $
Application : Utilities
Description : Bundle implements a UTF-8 string pool inspired by 
              PropertyResourceBundle.
              It reloads the string pool, when the language is changed.
              It also returns the key as a value, if the resource
              is not available.
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland
Created    : 21.12.2015, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.resource;

import ch.enterag.utils.SU;
import ch.enterag.utils.logging.IndentLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/*====================================================================*/

/**
 * Bundle implements a UTF-8 string pool inspired by PropertyResourceBundle.
 * It reloads the string pool, when the language is changed.
 * It also returns the key as a value, if the resource is not available.
 *
 * @author Hartwig Thomas
 */
public class Bundle {
    private static final String sPROPERTIES_EXTENSION = ".properties";
    /**
     * logger
     */
    private static IndentLogger _il = IndentLogger.getIndentLogger(Bundle.class.getName());
    /**
     * class from which resources are loaded.
     */
    private Class<?> _clsResource = null;
    /**
     * path name from which resources are loaded without language postfix
     * and without implicit ".properties" extension.
     * E.g. ../res/myresources for myresources.properties,
     * myresources_en.properties, myresources_de.properties, ...
     * to be loaded from ../res (relative to the
     * class's location). If no resource with the given language postfix
     * exists, the resource without any postfix is used.
     * At least this one must be available.
     * All .properties files must be stored with UTF-8 encoding.
     */
    private String _sResource = null;
    private String _sLanguage = Locale.getDefault().getLanguage();
    private Properties _propDefaults = null;
    private Properties _prop = null;

    /*------------------------------------------------------------------*/

    /**
     * load UTF-8 encoded properties from given resource path.
     *
     * @param sResource resource path without ".properties" extension.
     * @return loaded UTF-8 properties using defaults.
     */
    private Properties load(String sResource) {
        _il.enter(sResource);
        Properties prop = new Properties();
        InputStream is = _clsResource.getResourceAsStream(sResource + sPROPERTIES_EXTENSION);
        if (is != null) {
            InputStreamReader isr = null;
            try {
                isr = new InputStreamReader(is, SU.sUTF8_CHARSET_NAME);
                prop.load(isr);
                isr.close();
            } catch (UnsupportedEncodingException usee) {
                _il.exception(usee);
            } catch (IOException ie) {
                _il.exception(ie);
            }
        }
        if (_propDefaults != null) {
            for (Iterator<Object> iterKey = _propDefaults.keySet().iterator(); iterKey.hasNext(); ) {
                String sKey = (String) iterKey.next();
                if (prop.getProperty(sKey) == null)
                    prop.setProperty(sKey, _propDefaults.getProperty(sKey));
            }
        }
        _il.exit(prop);
        return prop;
    } /* load */

    /*------------------------------------------------------------------*/

    /**
     * (re-)load properties for the current language.
     */
    private void load() {
        String sResource = _sResource + "_" + _sLanguage;
        _prop = load(sResource);
    } /* load */

    /*------------------------------------------------------------------*/

    /**
     * returns current language.
     *
     * @return current language.
     */
    public String getLanguage() {
        return _sLanguage;
    }

    /*------------------------------------------------------------------*/

    /**
     * sets new language and reloads the properties if necessary.
     *
     * @param sLanguage new language.
     */
    public void setLanguage(String sLanguage) {
        _il.enter(sLanguage);
        if (!sLanguage.equals(getLanguage())) {
            _sLanguage = sLanguage;
            load();
        }
        _il.exit();
    } /* setLanguage */

    /*------------------------------------------------------------------*/

    /**
     * constructor stores location of resources and load initial resource
     * using the language of the default Location.
     *
     * @param clsResource class relative to which the resources are to be loaded.
     * @param sResource   path name from which resources are loaded without
     *                    language postfix (e.g. "_en") and without implicit ".properties" extension.
     *                    If no resource with the given language postfix exists, the resource
     *                    without any postfix is used, which must be available.
     *                    All ".properties" files must be stored with UTF-8 encoding.
     */
    protected Bundle(Class<?> clsResource, String sResource) {
        _il.enter(clsResource, sResource);
        _clsResource = clsResource;
        _sResource = sResource;
        /* load defaults */
        _propDefaults = load(sResource);
        if (_propDefaults.size() == 0)
            throw new IllegalArgumentException("Resource " + sResource + sPROPERTIES_EXTENSION + " not found!");
        /* load properties of current language */
        load();
        _il.exit();
    } /* constructor Bundle */

    /*------------------------------------------------------------------*/

    /**
     * factory creates a Bundle instance.
     *
     * @param clsResource class relative to which the resources are to be loaded.
     * @param sResource   path name from which resources are loaded without
     *                    language postfix (e.g. "_en") and without implicit ".properties" extension.
     *                    If no resource with the given language postfix exists, the resource
     *                    without any postfix is used, which must be available.
     *                    All ".properties" files must be stored with UTF-8 encoding.
     * @return new Bundle instance.
     */
    public static Bundle getBundle(Class<?> clsResource, String sResource) {
        return new Bundle(clsResource, sResource);
    } /* getBundle */

    /*------------------------------------------------------------------*/

    /**
     * Searches for the property with the specified key in the string pool
     * of the current language. If the key is not found in the string pool,
     * the default string pool is checked. The method returns the default
     * value argument if the property is not found.
     *
     * @param sKey          the hashtable key.
     * @param sDefaultValue a default value.
     * @return the value in this string pool with the specified key value.
     */
    public String getProperty(String sKey, String sDefaultValue) {
        return _prop.getProperty(sKey, sDefaultValue).trim();
    } /* getProperty */

    /*------------------------------------------------------------------*/

    /**
     * Searches for the property with the specified key in the string pool
     * of the current language. If the key is not found in the string pool,
     * the default string pool is checked. The method throws an exception
     * if the property is not found.
     *
     * @param sKey the property key.
     * @return the value in this string pool with the specified key value.
     * @throws IllegalArgumentException if the key is not found in the string pool.
     */
    public String getProperty(String sKey) {
        String sValue = _prop.getProperty(sKey);
        if (sValue == null)
            throw new IllegalArgumentException("Bundle " + _sResource + " does not define property " + sKey + "!");
        return sValue.trim();
    } /* getProperty */

    /*------------------------------------------------------------------*/

    /**
     * Returns a set of keys in this string pool, including distinct keys
     * in the default string pool if a key of the same name has not already
     * been found from the main string pool.<br>
     * The returned set is not backed by the Bundle instance object.
     * Changes to this Bundle are not reflected in the set, or vice versa.
     *
     * @return a set of keys in this string pool, including the keys in the
     * default string pool.
     */
    public Set<String> stringPropertyNames() {
        return _prop.stringPropertyNames();
    } /* stringPropertyNames */

} /* Bundle */
