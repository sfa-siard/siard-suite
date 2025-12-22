/*======================================================================
RecordDispenser delivers records of a table.
Application : SIARD 2.0
Description : RecordDispenser delivers records of a table. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 31.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * RecordDispenser delivers records of a table.
 *
 */
public interface TableRecordDispenser {
  /*====================================================================
  methods
  ====================================================================*/

    /**
     * read the next record.
     *
     * @return next record or null if no more records are available.
     * @throws IOException if an I/O error occurred.
     */
    TableRecord get() throws IOException;

    /**
     * skip a number of records.
     *
     * @param lSkip number of records to skip.
     * @throws IOException if an I/O error occurred.
     */
    void skip(long lSkip) throws IOException;

    /**
     * close the Dispenser.
     *
     * @throws IOException if an I/O error occurred.
     */
    void close() throws IOException;

    /**
     * get number of records already dispensed.
     *
     * @return index of next records.
     */
    long getPosition();

    /**
     * get byte count already read from XML.
     *
     * @return byte count read from XML.
     */
    long getByteCount();

} 
