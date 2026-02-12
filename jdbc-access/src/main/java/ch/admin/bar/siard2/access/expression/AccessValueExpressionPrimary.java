package ch.admin.bar.siard2.access.expression;

import ch.admin.bar.siard2.access.AccessSqlFactory;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.expression.enums.NullCondition;
import ch.enterag.sqlparser.generated.SqlParser;
import ch.enterag.sqlparser.identifier.QualifiedId;

public class AccessValueExpressionPrimary
        extends ValueExpressionPrimary {
    public AccessValueExpressionPrimary(AccessSqlFactory sf) {
        super(sf);
    }

    private BooleanValueExpression getIsNull(ValueExpression ve) {
        RowValuePredicand rvp = getSqlFactory().newRowValuePredicand();
        rvp.setCommonValueExpression(ve.getCommonValueExpression());
        BooleanPrimary bp = getSqlFactory().newBooleanPrimary();
        bp.setNullCondition(NullCondition.IS_NULL);
        bp.setRowValuePredicand(rvp);
        BooleanValueExpression bve = getSqlFactory().newBooleanValueExpression();
        bve.setBooleanPrimary(bp);
        return bve;
    }

    private BooleanValueExpression getBooleanValue(ValueExpression ve) {
        BooleanValueExpression bve = null;
        if (ve.getBooleanValueExpression() != null)
            bve = ve.getBooleanValueExpression();
        else if (ve.getCommonValueExpression() != null) {
            CommonValueExpression cve = ve.getCommonValueExpression();
            if (cve.getValueExpressionPrimary() != null) {
                ValueExpressionPrimary vep = cve.getValueExpressionPrimary();
                BooleanPrimary bp = getSqlFactory().newBooleanPrimary();
                bp.setValueExpressionPrimary(vep);
                bve = getSqlFactory().newBooleanValueExpression();
                bve.setBooleanPrimary(bp);
            }
        }
        return bve;
    }


    @Override
    public void parse(SqlParser.ValueExpressionPrimaryContext ctx) {
        super.parse(ctx);
        QualifiedId qiRoutine = getRoutineName();
        if (qiRoutine.isSet()) {
            if ("Nz".equalsIgnoreCase(qiRoutine.getName())) {
                /* case $1 is null then $2 else $1 */
                SqlArgument sa1 = getSqlArguments().get(0);
                SqlArgument sa2 = getSqlArguments().get(1);
                getSqlArguments().clear();
                setRoutineName(new QualifiedId());
                CaseExpression ce = getSqlFactory().newCaseExpression();
                ce.getBooleanValueExpressions()
                  .add(getIsNull(sa1.getValueExpression()));
                ce.getWhenResults()
                  .add(sa2.getValueExpression());
                ce.setElseResult(sa1.getValueExpression());
                setCaseExpression(ce);
            } else if ("IIf".equalsIgnoreCase(qiRoutine.getName())) {
                /* case when $1 then $2 else $3 */
                SqlArgument sa1 = getSqlArguments().get(0);
                SqlArgument sa2 = getSqlArguments().get(1);
                SqlArgument sa3 = getSqlArguments().get(2);
                getSqlArguments().clear();
                setRoutineName(new QualifiedId());
                CaseExpression ce = getSqlFactory().newCaseExpression();
                BooleanValueExpression bve = getBooleanValue(sa1.getValueExpression());
                ce.getBooleanValueExpressions()
                  .add(bve);
                ce.getWhenResults()
                  .add(sa2.getValueExpression());
                ce.setElseResult(sa3.getValueExpression());
                setCaseExpression(ce);
            }
        }
    }

    /** get data type of this value expression primary from the context
     * of a query.
     * @param ss sql statement.
     * @return data type.
     */
    public DataType getDataType(SqlStatement ss) {
        DataType dt = null;
        QualifiedId qiRoutine = getRoutineName();
        if (qiRoutine.isSet()) {
            if ("IsNull".equalsIgnoreCase(qiRoutine.getName())) {
                PredefinedType pt = getSqlFactory().newPredefinedType();
                pt.initBooleanType();
                dt = getSqlFactory().newDataType();
                dt.initPredefinedDataType(pt);
            } else if ("CCUR".equalsIgnoreCase(qiRoutine.getName())) {
                PredefinedType pt = getSqlFactory().newPredefinedType();
                pt.initDecimalType(19, 4);
                dt = getSqlFactory().newDataType();
                dt.initPredefinedDataType(pt);
            } else if ("Format".equalsIgnoreCase(qiRoutine.getName())) {
                PredefinedType pt = getSqlFactory().newPredefinedType();
                pt.initVarCharType(256);
                dt = getSqlFactory().newDataType();
                dt.initPredefinedDataType(pt);
            } else if ("DatePart".equalsIgnoreCase(qiRoutine.getName())) {
                PredefinedType pt = getSqlFactory().newPredefinedType();
                pt.initSmallIntType();
                dt = getSqlFactory().newDataType();
                dt.initPredefinedDataType(pt);
            } else if ("Replace".equalsIgnoreCase(qiRoutine.getName())) {
                PredefinedType pt = getSqlFactory().newPredefinedType();
                pt.initVarCharType(256);
                dt = getSqlFactory().newDataType();
                dt.initPredefinedDataType(pt);
            }
        }
        if (dt == null)
            dt = super.getDataType(ss);
        return dt;
    }
}
