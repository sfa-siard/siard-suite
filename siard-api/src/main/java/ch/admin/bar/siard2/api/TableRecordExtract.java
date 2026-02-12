/*== RecordExtract.java ================================================
RecordExtract is an interface for loading a small (max 50) extract of records. 
Application : SIARD 2.0
Description : RecordExtract is an interface for loading a small (max 50) 
              extract of records at a time.
              This permits to transfer only the data that can be viewed 
              at a time to the user interface and thus prevents memory 
              problems with large tables. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 01.10.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * RecordExtract is an interface for loading a small (max 50) extract
 * of records at a time.
 * This permits to transfer only the data that can be viewed
 * at a time to the user interface and thus prevents memory problems
 * with large tables.
 *
 */
public interface TableRecordExtract {
    /**
     * get table instance to which the record extract belongs.
     *
     * @return table instance.
     */
    Table getTable();

    /**
     * get parent record extract or null, if root extract.
     *
     * @return parent record extract or null.
     */
    TableRecordExtract getParentTableRecordExtract();

    /**
     * get number of rows skipped before first element of the record extract.
     *
     * @return number of rows skipped before first element of the record extract.
     */
    long getOffset();

    /**
     * get delta between two records in the record extract.
     *
     * @return delta between two records in the record extract.
     */
    long getDelta();

    /**
     * get label for record extract ("rows" or "row &lt;nnn&gt;").
     *
     * @return label for record extract.
     */
    String getLabel();

    /**
     * return the record at the offset of this record extract.
     *
     * @return the record at the offset of this record extract.
     */
    TableRecord getTableRecord();

    /**
     * get the number of record extracts under this record extract.
     *
     * @return number of record extracts under this record extract.
     */
    int getTableRecordExtracts();

    /**
     * get record extract under this record extract with the given index.
     *
     * @param iRecordExtract index of record extract.
     * @return record extract.
     * @throws IOException if an I/O error occurred.
     */
    TableRecordExtract getTableRecordExtract(int iRecordExtract)
            throws IOException;

} 
