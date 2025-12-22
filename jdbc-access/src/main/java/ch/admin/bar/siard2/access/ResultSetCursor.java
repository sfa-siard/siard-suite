/*======================================================================
A cursor is used as a basis for the implementation of a ResultSet.
This interface is used für Jackcess cursors and for meta data cursors
implemented using a List.  
Version     : $Id: ResultSetCursor.java 4 2015-01-12 14:30:07Z hartwigthomas $
Application : Access JDBC driver
Description : Cursor interface.
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 07.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.access;

import com.healthmarketscience.jackcess.Row;

import java.io.IOException;

public interface ResultSetCursor {

    /** Resets this cursor for forward traversal (sets cursor to before the first
     * row).
     */
    void beforeFirst();


    /** Resets this cursor for reverse traversal (sets cursor to after the last
     * row).
     * @throws IOException, if an I/O error occurred.
     */
    void afterLast() throws IOException;


    /** Returns {@code true} if the cursor is currently positioned before the
     * first row, {@code false} otherwise.
     * @return true, if the cursor is positioned before the first row,
     *               false otherwise
     * @throws IOException, if an I/O error occurred.
     */
    boolean isBeforeFirst() throws IOException;


    /** Returns {@code true} if the cursor is currently positioned after the
     * last row, {@code false} otherwise.
     * @return true, if the cursor is positioned after the last row,
     *               false otherwise
     * @throws IOException, if an I/O error occurred.
     */
    boolean isAfterLast() throws IOException;


    /** Moves to the next row in the table and returns it.
     * @return The next row in this table (Column name -> Column value), or
     *         {@code null} if no next row is found
     * @throws IOException, if an I/O error occurred.
     */
    Row getNextRow() throws IOException;


    /** Moves to the previous row in the table and returns it.
     * @return The previous row in this table (Column name -> Column value), or
     *         {@code null} if no previous row is found
     * @throws IOException, if an I/O error occurred.
     */
    Row getPreviousRow() throws IOException;


    /** reads the current row from table and returns it.
     * @return The current row in this table.
     * @throws IOException, if an I/O error occurred.
     */
    Row refreshCurrentRow() throws IOException;


    /** delete the current row
     * @throws IOException, if an I/O error occurred.
     */
    void deleteCurrentRow() throws IOException;


    /** update the current row in the table.
     * @throws IOException, if the row is not a table row or
     *   an I/O error occurred.
     */
    void updateCurrentRow(Row row) throws IOException;


    /** insert the given row in the table.
     * @param row row of values to be inserted
     * @throws IOException, if the row is not a table row or
     *   an I/O error occurred.
     */
    void insertRow(Row row) throws IOException;


    /** Retrieve the current row number (1-based).
     * @return current row number (1-based).
     */
    int getRow();


    /** return the number of rows fulfilling the condition.
     * @return number of rows.
     * @throws IOException if an I/O error occurs.
     */
    int getCount()
            throws IOException;

}
