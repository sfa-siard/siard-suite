/*======================================================================
RecordRetainer absorbs records of a table.
Application : SIARD 2.0
Description : RecordRetainer absorbs records of a table. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 31.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * RecordRetainer absorbs records of a table.
 *
 */
public interface TableRecordRetainer {
  /*====================================================================
  methods
  ====================================================================*/

    /**
     * create an (empty) record with the current record number.
     *
     * @return empty record
     * @throws IOException if an I/O error occurred.
     */
    TableRecord create() throws IOException;

    /**
     * write the next record.
     *
     * @param tableRecord record to be retained.
     * @throws IOException if an I/O error occurred.
     */
    void put(TableRecord tableRecord) throws IOException;

    /**
     * close the Retainer.
     *
     * @throws IOException if an I/O error occurred.
     */
    void close() throws IOException;

    /**
     * get number of records already retained.
     *
     * @return index of next record.
     */
    long getPosition();

    /**
     * get byte count already written to XML.
     *
     * @return byte count written to XML.
     */
    long getByteCount();

} 
