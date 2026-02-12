/*== MetaSearch.java ======================================================
Common search interface for all meta data classes in database archive.
Application : SIARD 2.0
Description : Common search interface for all meta data classes in database 
              archive.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 18.10.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import ch.enterag.utils.DU;

import java.io.IOException;


/**
 * Common search interface for all meta data classes in database archive.
 *
 */
public interface MetaSearch {
    /**
     * return string searched for.
     *
     * @return find string.
     */
    String getFindString();

    /**
     * return index of data element found.
     *
     * @return index of meta data element found.
     * @throws IOException if an I/O error occurred.
     */
    int getFoundElement()
            throws IOException;

    /**
     * return of data element found.
     *
     * @param du date to string transformer to be used.
     * @return meta data element found.
     * @throws IOException if an I/O error occurred.
     */
    String getFoundString(DU du)
            throws IOException;

    /**
     * return offset in element where find string was found.
     *
     * @return offset in element.
     */
    int getFoundOffset();

    /**
     * prepare meta data for finding the desired string.
     *
     * @param sFindString string to be searched for.
     * @param bMatchCase  true, if exact match is required.
     * @throws IOException if an I/O error occurred.
     */
    void find(String sFindString, boolean bMatchCase)
            throws IOException;

    /**
     * return meta data element which contains desired string.
     *
     * @param du date to string transformer to be used.
     * @return meta data element containing the desired string or null
     * if end reached.
     * @throws IOException if an I/O error occurred.
     */
    MetaSearch findNext(DU du)
            throws IOException;

    /**
     * returns true, if findNext can be applied.
     *
     * @return true, if find has been called before and been found.
     */
    boolean canFindNext();

} 
