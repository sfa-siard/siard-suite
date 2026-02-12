package ch.admin.bar.siard2.access;

import ch.enterag.sqlparser.DmlStatement;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.dml.DeleteStatement;
import ch.enterag.sqlparser.dml.UpdateStatement;
import ch.enterag.sqlparser.expression.QuerySpecification;
import ch.enterag.sqlparser.expression.SelectSublist;
import ch.enterag.sqlparser.expression.TablePrimary;
import ch.enterag.sqlparser.identifier.IdChain;

import java.util.Iterator;

public class AccessSqlStatement
        extends SqlStatement {

    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
    public AccessSqlStatement(SqlFactory sf) {
        super(sf);
    }


    /** find a select sublist with the given alias name.
     * @param qs query
     * @param sAliasName alias name.
     * @return matching select sublist.
     */
    private SelectSublist findSelectByAlias(QuerySpecification qs, String sAliasName) {
        SelectSublist sel = null;
        for (Iterator<SelectSublist> iterSelect = qs.getSelectSublists()
                                                    .iterator(); (sel == null) && iterSelect.hasNext(); ) {
            SelectSublist selCandidate = iterSelect.next();
            if ((!selCandidate.isAsterisk()) && (selCandidate.getColumnNames()
                                                             .size() > 0)) {
                if (sAliasName.equalsIgnoreCase(selCandidate.getColumnNames()
                                                            .get(0)
                                                            .get()))
                    sel = selCandidate;
            }
        }
        return sel;
    }


    /** look up the registered data type of a query column.
     * @param idcColumn column.
     * @return data type of the query column.
     */
    @Override
    public DataType getColumnType(IdChain idcColumn) {
        DataType dt = null;
        int iLength = idcColumn.get()
                               .size();
        if (iLength > 0) {
            QuerySpecification qs = getQuerySpecification();
            DmlStatement dstmt = getDmlStatement();
            String sColumnName = idcColumn.get()
                                          .get(iLength - 1);
            if (qs != null) {
                TablePrimary tp = qs.getTablePrimary(this, idcColumn);
                if (tp != null)
                    dt = tp.getColumnType(sColumnName);
                else {
                    /* try to find a SelectSublist with the column name as alias name */
                    SelectSublist sel = findSelectByAlias(qs, idcColumn.get()
                                                                       .get(idcColumn.get()
                                                                                     .size() - 1));
                    if (sel != null)
                        dt = sel.getValueExpression()
                                .getDataType(this);
                    else
                        throw new IllegalArgumentException("Column type for " + idcColumn.format() + " could not be established!");
                }
            } else if (dstmt != null) {
                UpdateStatement us = dstmt.getUpdateStatement();
                DeleteStatement ds = dstmt.getDeleteStatement();
                if (us != null)
                    dt = us.getColumnType(sColumnName);
                else if (ds != null)
                    dt = ds.getColumnType(sColumnName);
                else
                    throw new IllegalArgumentException("No column types for insert statement should be needed!");
            } else
                throw new IllegalArgumentException("No column types for DDL statements should be needed!");
        } else
            throw new IllegalArgumentException("Identifier chain is invalid for column!");
        return dt;
    }

}
