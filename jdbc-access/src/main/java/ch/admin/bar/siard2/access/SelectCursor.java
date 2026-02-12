/*== SelectCursor.java =================================================
A select cursor represents the result of a select expression on the result
of a select expression.
Application : Access JDBC driver
Description : A select cursor represents the result of a select expression 
              on the result of a select expression.
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 07.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.access;

import ch.admin.bar.siard2.jdbc.AccessResultSet;
import ch.enterag.sqlparser.DmlStatement;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.dml.DeleteStatement;
import ch.enterag.sqlparser.dml.UpdateStatement;
import ch.enterag.sqlparser.expression.BooleanValueExpression;
import ch.enterag.sqlparser.expression.QuerySpecification;
import com.healthmarketscience.jackcess.Row;

import java.io.IOException;


/** A select cursor represents the result of a select expression on the
 * result of a select expression.
 * @author Hartwig Thomas */
public class SelectCursor implements ResultSetCursor {
    /** FROM select expression result */
    private ResultSetCursor _rsc = null;
    /** sql statement */
    private SqlStatement _ss = null;
    /** current row number (0-based) */
    private int _iCurrentRow = -1;
    /** cached number of rows */
    private int _iRowCount = -1;


    /** create a SelectCursor backed by a select expression.
     * N.B.: It is assumed that global evaluation values have already been set.
     * @param ears result set describing the rows of the FROM expression.
     * @param ss sql statement with column values.
     */
    public SelectCursor(
            AccessResultSet ears,
            SqlStatement ss) {
        _rsc = ears.getCursor();
        _ss = ss;
        _iCurrentRow = -1;
    }


    /** {@link ResultSetCursor} */
    @Override
    public void beforeFirst() {
        _rsc.beforeFirst();
        _iCurrentRow = -1;
    }


    /** {@link ResultSetCursor} */
    @Override
    public void afterLast() throws IOException {
        _rsc.afterLast();
        _iCurrentRow = getCount();
    }


    /** {@link ResultSetCursor} */
    @Override
    public boolean isBeforeFirst() throws IOException {
        return _rsc.isBeforeFirst();
    }


    /** {@link ResultSetCursor} */
    @Override
    public boolean isAfterLast() throws IOException {
        return _rsc.isAfterLast();
    }


    /** fill the query specification with values from row and evaluate the
     * condition expression.
     * @param row Row with values.
     * @return false, if the expression evaluates to false, true otherwise.
     */
    private boolean evaluateCondition(Row row) throws IOException {
        boolean bValue = true;
        Shunting.fillSqlValues(row, _ss);
        BooleanValueExpression bve = null;
        QuerySpecification qs = _ss.getQuerySpecification();
        DmlStatement dstmt = _ss.getDmlStatement();
        if (qs != null)
            bve = qs.getWhereCondition();
        else if (dstmt != null) {
            UpdateStatement us = dstmt.getUpdateStatement();
            DeleteStatement ds = dstmt.getDeleteStatement();
            if (us != null)
                bve = us.getBooleanValueExpression();
            else if (ds != null)
                bve = ds.getBooleanValueExpression();
        }
        if ((row != null) && (bve != null)) {
            Boolean b = bve.evaluate(_ss, false);
            if (b != null)
                bValue = b.booleanValue();
        }
        return bValue;
    }


    /** evaluate the SELECT expressions assuming sql statement has been
     * filled with values.
     * @return row with evaluated SELECT expression values.
     */
    private ResultSetRow evaluateSelectExpressions()
            throws IOException {
        ResultSetRow rsrow = new ResultSetRow();
        Shunting.fillRowValues(_ss, rsrow);
        return rsrow;
    }


    /** go to next valid row.
     * @return true, if one exists.
     * @throws IOException
     */
    private boolean next() throws IOException {
        boolean bNext = false;
        if (!_rsc.isAfterLast()) {
            _iCurrentRow++;
            /* get the next table row  matching the condition */
            Row rowFrom = _rsc.getNextRow();
            while ((!_rsc.isAfterLast()) && (!evaluateCondition(rowFrom)))
                rowFrom = _rsc.getNextRow();
            if (!_rsc.isAfterLast())
                bNext = true;
        }
        return bNext;
    }


    /** go to previous valid row.
     * @return true, if one exists.
     * @throws IOException
     */
    private boolean previous() throws IOException {
        boolean bPrevious = false;
        if (!_rsc.isBeforeFirst()) {
            _iCurrentRow--;
            /* get the previous table row  matching the condition */
            Row rowFrom = _rsc.getPreviousRow();
            while ((!_rsc.isBeforeFirst()) && (!evaluateCondition(rowFrom)))
                rowFrom = _rsc.getPreviousRow();
            if (!_rsc.isBeforeFirst())
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
        _rsc.deleteCurrentRow();
    }


    /** {@link ResultSetCursor} */
    @Override
    public void updateCurrentRow(Row row)
            throws IOException {
        throw new IOException("Current row of a VIEW cannot be updated!");
    }


    /** {@link ResultSetCursor} */
    @Override
    public void insertRow(Row row)
            throws IOException {
        throw new IOException("Row cannot be inserted into a VIEW!");
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
