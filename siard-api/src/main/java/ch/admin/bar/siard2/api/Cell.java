/*== Cell.java =========================================================
Cell interface provides access to cells of records of primary table data.
Application : SIARD 2.0
Description : Cell interface provides access to cells of records of 
              primary table data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 05.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;


/**
 * Cell interface provides access to cells of records of primary table data.
 *
 */
public interface Cell
        extends Value {
    /**
     * get record with which this Cell instance is associated.
     *
     * @return get record with which this Cell instance is associated.
     */
    TableRecord getParentRecord();

    /**
     * get column meta data associated with this cell.
     *
     * @return column meta data associated with this cell.
     */
    MetaColumn getMetaColumn();

} 
