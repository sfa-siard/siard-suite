/*== MetaUniqueKey.java ================================================
MetaUniqueKey interface provides access to unique key meta data.
Application : SIARD 2.0
Description : MetaUniqueKey interface provides access to unique 
              key meta data. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * MetaUniqueKey interface provides access to unique key meta data.
 *
 */
public interface MetaUniqueKey
        extends MetaSearch {
    /**
     * return the table meta data instance to which these unique key
     * meta data belong.
     *
     * @return table meta data instance.
     */
    MetaTable getParentMetaTable();

    /**
     * return true, if the key has at least one column.
     *
     * @return true if the key has at least one column.
     */
    boolean isValid();
  
  /*====================================================================
  unique key properties
  ====================================================================*/

    /**
     * get name of unique key.
     *
     * @return name of unique key.
     */
    String getName();

    /**
     * get number of columns in the unique key.
     *
     * @return number of columns in the unique key.
     */
    int getColumns();

    /**
     * get unique key column with the given index.
     *
     * @param iColumn column index.
     * @return unique key column.
     */
    String getColumn(int iColumn);

    /**
     * add column to unique key.
     * Can only be added if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sColumn column to be added.
     * @throws IOException if value cannot be set.
     */
    void addColumn(String sColumn)
            throws IOException;

    /**
     * return the column names as a comma-separated string.
     *
     * @return column names.
     */
    String getColumnsString();

    /**
     * set description of the unique key's meaning and content.
     *
     * @param sDescription description of the unique key's meaning and content.
     */
    void setDescription(String sDescription);

    /**
     * get description of the unique key's meaning and content.
     *
     * @return description of the unique key's meaning and content.
     */
    String getDescription();

} 
