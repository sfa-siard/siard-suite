/*== TableCursor.java =================================================
A table cursor represents the result of a select expression on a table
of a Jackcess database.
Application : Access JDBC driver
Description : A table cursor represents the result of a select expression
              on a table of a Jackcess database.
              Currently only expressions based on a single table are supported.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 07.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.access;

import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.expression.QuerySpecification;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.IOException;
import java.sql.SQLException;


/** A table cursor represents the result of a query.
 * Currently only queries based on a single table are supported.
 * @author Hartwig Thomas */
public class TableCursor implements ResultSetCursor {
    /** FROM Access table cursor */
    private Cursor _cursor = null;
    /** sql statement. */
    private SqlStatement _ss = null;
    /** current row number (0-based) */
    private int _iCurrentRow = -1;
    /** cached number of rows */
    private int _iRowCount = -1;


    /** create a SelectCursor backed by an Access table.
     * N.B.: It is assumed that global evaluation values and the column
     * names and types have already been set in the query specification.
     * @param ss sql statement with column values.
     * @throws SQLException if an exception occurs.
     */
    public TableCursor(
            Table table,
            SqlStatement ss)
            throws SQLException {
        try {
            _ss = ss;
            _cursor = CursorBuilder.createCursor(table);
            _iCurrentRow = -1;
        } catch (IOException ie) {
            throw new SQLException(ie.getClass()
                                     .getName() + ": " + ie.getMessage());
        }
    }


    /** {@link ResultSetCursor} */
    @Override
    public void beforeFirst() {
        _cursor.beforeFirst();
        _iCurrentRow = -1;
    }


    /** {@link ResultSetCursor} */
    @Override
    public void afterLast() throws IOException {
        _cursor.afterLast();
        _iCurrentRow = getCount();
    }


    /** {@link ResultSetCursor} */
    @Override
    public boolean isBeforeFirst() throws IOException {
        return _cursor.isBeforeFirst();
    }


    /** {@link ResultSetCursor} */
    @Override
    public boolean isAfterLast() throws IOException {
        return _cursor.isAfterLast();
    }


    /** fill the query specification with values from row and evaluate the
     * condition expression.
     * @param row Jackcess Row with values.
     * @return false, if the expression evaluates to false, true otherwise.
     */
    public boolean evaluateCondition(Row row)
            throws IOException {
        boolean bValue = true;
        Shunting.fillSqlValues(row, _ss);
        QuerySpecification qs = _ss.getQuerySpecification();
        if ((row != null) && (qs.getWhereCondition() != null)) {
            Boolean b = qs.getWhereCondition()
                          .evaluate(_ss, false);
            if (b != null)
                bValue = b.booleanValue();
        }
        return bValue;
    }


    /** evaluate the SELECT expressions assuming the query specification
     * has been filled with values.
     * @return row with evaluated query value.
     */
    private ResultSetRow evaluateSelectExpressions()
            throws IOException {
        ResultSetRow rsrow = new ResultSetRow();
        Shunting.fillRowValues(_ss, rsrow);
        return rsrow;
    }


    /** go to next valid row.
     * @return true, if one exists.
     * @throws IOException if an I/O error occurs.
     */
    private boolean next() throws IOException {
        boolean bNext = false;
        if (!_cursor.isAfterLast()) {
            _iCurrentRow++;
            /* get the next table row  matching the condition */
            Row rowFrom = _cursor.getNextRow();
            while ((!_cursor.isAfterLast()) && (!evaluateCondition(rowFrom)))
                rowFrom = _cursor.getNextRow();
            if (!_cursor.isAfterLast())
                bNext = true;
        }
        return bNext;
    }


    /** go to previous valid row.
     * @return true, if one exists.
     * @throws IOException if an I/O error occurs.
     */
    private boolean previous() throws IOException {
        boolean bPrevious = false;
        if (!_cursor.isBeforeFirst()) {
            _iCurrentRow--;
            /* get the previous table row  matching the condition */
            Row rowFrom = _cursor.getPreviousRow();
            while ((!_cursor.isBeforeFirst()) && (!evaluateCondition(rowFrom)))
                rowFrom = _cursor.getPreviousRow();
            if (!_cursor.isBeforeFirst())
                bPrevious = true;
        }
        return bPrevious;
    }


    /** {@link ResultSetCursor} */
    @Override
    public Row getNextRow() throws IOException {
        Row rowTo = null;
        if (next())
            rowTo = evaluateSelectExpressions();
        return rowTo;
    }


    /** {@link ResultSetCursor} */
    @Override
    public Row getPreviousRow() throws IOException {
        Row rowTo = null;
        if (previous())
            rowTo = evaluateSelectExpressions();
        return rowTo;
    }


    /** {@link ResultSetCursor} */
    @Override
    public Row refreshCurrentRow() throws IOException {
        Row row = evaluateSelectExpressions();
        return row;
    }


    /** {@link ResultSetCursor} */
    @Override
    public int getRow() {
        return _iCurrentRow + 1;
    }


    /** {@link ResultSetCursor} */
    @Override
    public void deleteCurrentRow()
            throws IOException {
        _cursor.deleteCurrentRow();
    }


    /** {@link ResultSetCursor} */
    @Override
    public void updateCurrentRow(Row row)
            throws IOException {
        _cursor.updateCurrentRowFromMap(row);
    }


    /** {@link ResultSetCursor} */
    @Override
    public void insertRow(Row row)
            throws IOException {
        /***
         for (Iterator<String> iterColumns = row.keySet().iterator(); iterColumns.hasNext(); )
         {
         String sColumnName = iterColumns.next();
         Object oColumnValue = row.get(sColumnName);
         String s = String.valueOf(oColumnValue);
         if (s.length() > 72)
         s = s.substring(0,72)+"...";
         System.out.println(sColumnName+": "+s);
         }
         ***/
        _cursor.getTable()
               .addRowFromMap(row);
    }


    /** {@link ResultSetCursor} */
    @Override
    public int getCount()
            throws IOException {
        if (_iRowCount < 0) {
            _iRowCount = 0;
            /* remember position */
            int iCurrentRow = _iCurrentRow;
            beforeFirst();
            while (next())
                _iRowCount++;
            /* reposition */
            beforeFirst();
            while (iCurrentRow != _iCurrentRow)
                next();
        }
        return _iRowCount;
    }

}
