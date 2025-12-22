package ch.admin.bar.siard2.access;

import ch.admin.bar.siard2.jdbc.AccessResultSet;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.expression.BooleanValueExpression;
import ch.enterag.sqlparser.expression.GroupingElement;
import ch.enterag.sqlparser.expression.SelectSublist;
import ch.enterag.sqlparser.identifier.IdChain;
import com.healthmarketscience.jackcess.Row;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupedCursor implements ResultSetCursor {
    /** ungrouped select result */
    private ResultSetCursor _rsc = null;
    /** sql statement for grouped result */
    private SqlStatement _ss = null;
    /** current group number (0-based) */
    private int _iCurrentGroup = -1;
    /** list of groups */
    private List<ResultSetRow> _listGroups = null;
    /** map from groups to set of row numbers */
    private Map<ResultSetRow, List<Integer>> _mapGroups = null;


    /** constructor creates a grouped cursor based on an
     * ungrouped result set.
     * @param ears ungrouped access result set.
     * @param ss SQL statement with (implicit or explicit) grouping
     * @throws IOException in an I/O error occurs.
     */
    public GroupedCursor(
            AccessResultSet ears,
            SqlStatement ss)
            throws IOException {
        _rsc = ears.getCursor();
        _ss = ss;
        _iCurrentGroup = -1;
        initializeGroups();
    }


    /** compute the list of groups.
     * @throws IOException if an I/O error occurs.
     */
    private void initializeGroups()
            throws IOException {
        _listGroups = new ArrayList<ResultSetRow>();
        _mapGroups = new HashMap<ResultSetRow, List<Integer>>();
        _rsc.beforeFirst();
        List<GroupingElement> listGrouping = _ss.getQuerySpecification()
                                                .getGroupingElements();
        int iRow = 0;
        if (_rsc.getCount() > 0) {
            for (Row row = _rsc.getNextRow(); row != null; row = _rsc.getNextRow()) {
                Shunting.fillSqlValues(row, _ss);
                boolean bInclude = true;
                if ((listGrouping != null) && (listGrouping.size() > 0)) {
                    BooleanValueExpression bve = _ss.getQuerySpecification()
                                                    .getHavingCondition();
                    if (bve != null)
                        bInclude = bve.evaluate(_ss, false);
                } else
                    bInclude = true;
                if (bInclude) {
                    ResultSetRow rsr = new ResultSetRow();
                    // No GROUP BY and HAVING: single group with all rows select result
                    if ((listGrouping != null) && (listGrouping.size() > 0)) {
                        for (int iGroupingElement = 0; iGroupingElement < listGrouping.size(); iGroupingElement++) {
                            GroupingElement ge = listGrouping.get(iGroupingElement);
                            IdChain idcColumn = ge.getOrdinaryGroupingSets()
                                                  .get(0);
                            String sColumnName = idcColumn.get()
                                                          .get(idcColumn.get()
                                                                        .size() - 1);
                            Object oValue = row.get(sColumnName);
                            rsr.put(sColumnName, oValue);
                        }
                    }
                    List<Integer> listGroup = _mapGroups.get(rsr);
                    if (listGroup == null) {
                        listGroup = new ArrayList<Integer>();
                        _listGroups.add(rsr);
                    }
                    listGroup.add(Integer.valueOf(iRow));
                    _mapGroups.put(rsr, listGroup);
                }
                iRow++;
            }
        } else // empty select result => single grouped record
        {
            ResultSetRow rsr = new ResultSetRow();
            List<Integer> listGroup = new ArrayList<Integer>();
            listGroup.add(Integer.valueOf(iRow));
            _listGroups.add(rsr);
            _mapGroups.put(rsr, listGroup);
        }
    }


    /** {@link ResultSetCursor} */
    @Override
    public void beforeFirst() {
        _iCurrentGroup = -1;
    }


    /** {@link ResultSetCursor} */
    @Override
    public void afterLast() throws IOException {
        _iCurrentGroup = getCount();
    }


    /** {@link ResultSetCursor} */
    @Override
    public boolean isBeforeFirst() throws IOException {
        return _iCurrentGroup < 0;
    }


    /** {@link ResultSetCursor} */
    @Override
    public boolean isAfterLast() throws IOException {
        return _iCurrentGroup >= getCount();
    }


    /** evaluate the current group.
     * @return row with evaluated SELECT expression values.
     * @throws IOException
     */
    private ResultSetRow evaluateGroup() throws IOException {
        List<SelectSublist> listSelects = _ss.getQuerySpecification()
                                             .getSelectSublists();
        /* resetAggregates of all select sub lists */
        for (int i = 0; i < listSelects.size(); i++) {
            SelectSublist ss = _ss.getQuerySpecification()
                                  .getSelectSublists()
                                  .get(i);
            ss.resetAggregates(_ss);
        }
        ResultSetRow rsr = new ResultSetRow();
        /* loop over all elements and evaluate if next in list */
        List<Integer> listGroup = _mapGroups.get(_listGroups.get(_iCurrentGroup));
        int iIndex = 0;
        int iRow = 0;
        _rsc.beforeFirst();
        for (Row row = _rsc.getNextRow(); (iIndex < listGroup.size()) && (row != null); row = _rsc.getNextRow()) {
            if (iRow == listGroup.get(iIndex)
                                 .intValue()) {
                iIndex++;
                // copy values from base result set to statement
                Shunting.fillSqlValues(row, _ss);
                /***
                 TablePrimary  tp = _ss.getQuerySpecification().getTableReferences().get(0).getTablePrimary();
                 for (Iterator<String> iterColumn = row.keySet().iterator(); iterColumn.hasNext(); )
                 {
                 String sColumnName = iterColumn.next();
                 System.out.println(sColumnName+": "+String.valueOf(row.get(sColumnName))+" = "+String.valueOf(tp.getColumnValue(sColumnName)));
                 }
                 ***/
                /* get row values (evaluateSelectExpression) from select sub lists */
                Shunting.fillRowValues(_ss, rsr); // evaluate statement incrementally: last evaluate is result
                /***
                 for (Iterator<String> iterColumn = rsr.keySet().iterator(); iterColumn.hasNext(); )
                 {
                 String sColumnName = iterColumn.next();
                 System.out.println(sColumnName + ": " +String.valueOf(rsr.get(sColumnName)));
                 }
                 ***/
            }
        }
        return rsr;
    }


    /** {@link ResultSetCursor} */
    @Override
    public Row getNextRow() throws IOException {
        ResultSetRow rsr = null;
        if (!isAfterLast())
            _iCurrentGroup++;
        if (!isAfterLast())
            rsr = evaluateGroup();
        return rsr;
    }


    /** {@link ResultSetCursor} */
    @Override
    public Row getPreviousRow() throws IOException {
        ResultSetRow rsr = null;
        if (!isBeforeFirst())
            _iCurrentGroup--;
        if (!isBeforeFirst())
            rsr = evaluateGroup();
        return rsr;
    }


    /** {@link ResultSetCursor} */
    @Override
    public Row refreshCurrentRow() throws IOException {
        ResultSetRow rsr = null;
        if (!(isBeforeFirst() || isAfterLast()))
            rsr = evaluateGroup();
        return rsr;
    }


    /** {@link ResultSetCursor} */
    @Override
    public void deleteCurrentRow() throws IOException {
        if (!(isBeforeFirst() || isAfterLast())) {
            ResultSetRow rsr = _listGroups.get(_iCurrentGroup);
            _listGroups.remove(rsr);
            _mapGroups.remove(rsr);
        }
    }


    /** {@link ResultSetCursor} */
    @Override
    public void updateCurrentRow(Row row) throws IOException {
        throw new IOException("Current row of a group cannot be updated!");
    }


    /** {@link ResultSetCursor} */
    @Override
    public void insertRow(Row row) throws IOException {
        throw new IOException("Current row of a group cannot be inserted!");
    }


    /** {@link ResultSetCursor} */
    @Override
    public int getRow() {
        return _iCurrentGroup;
    }


    /** {@link ResultSetCursor} */
    @Override
    public int getCount() throws IOException {
        return _listGroups.size();
    }

}
