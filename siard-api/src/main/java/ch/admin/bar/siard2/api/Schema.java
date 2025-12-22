/*== Schema.java =======================================================
Schema interface provides access to primary schema data.
Application : SIARD 2.0
Description : Schema interface provides access to primary schema data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 21.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * Schema interface provides access to primary schema data.
 *
 */
public interface Schema {
    /**
     * get archive with which this Schema instance is associated
     *
     * @return get archive associated with this schema.
     */
    Archive getParentArchive();

    /**
     * return the associated schema meta data describing this schema instance.
     *
     * @return associated schema meta data.
     */
    MetaSchema getMetaSchema();

    /**
     * return true, if the schema has at least one table with at least
     * one column and at least one row.
     *
     * @return true if the schema is valid.
     */
    boolean isValid();

    /**
     * check, if schema is empty (contains not tables).
     *
     * @return true, if schema is empty.
     */
    boolean isEmpty();
  
  /*====================================================================
  methods
  ====================================================================*/

    /**
     * get number of table entries in the schema.
     *
     * @return number of table entries in the schema.
     */
    int getTables();

    /**
     * get the table entry with the given index.
     *
     * @param iTable index of table entry.
     * @return table entry.
     */
    Table getTable(int iTable);

    /**
     * get the table entry with the given name.
     *
     * @param sName name of table entry.
     * @return table entry.
     */
    Table getTable(String sName);

    /**
     * add a new table to the schema.
     * A new table can only be created if the SIARD archive is open for
     * modification of primary data.
     *
     * @param sName name of the new table entry.
     * @return table entry.
     * @throws IOException if the table could not be created.
     */
    Table createTable(String sName)
            throws IOException;

} 
