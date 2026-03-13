package ch.enterag.sqlparser.expression;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.antlr4.EnhancedSqlBaseVisitor;
import ch.enterag.sqlparser.antlr4.SqlBase;
import ch.enterag.sqlparser.generated.SqlParser;
import ch.enterag.utils.logging.IndentLogger;

public class RowValuePredicand
        extends SqlBase {
    /** logger */
    private static IndentLogger _il = IndentLogger.getIndentLogger(RowValuePredicand.class.getName());


    /** visitor initializes fields from parse tree.
     */
    private class RvpVisitor extends EnhancedSqlBaseVisitor<RowValuePredicand> {
        @Override
        public RowValuePredicand visitRowValueExpression(SqlParser.RowValueExpressionContext ctx) {
            setRowValueExpression(getSqlFactory().newRowValueExpression());
            getRowValueExpression().parse(ctx);
            return RowValuePredicand.this;
        }

        @Override
        public RowValuePredicand visitCommonValueExpression(SqlParser.CommonValueExpressionContext ctx) {
            setCommonValueExpression(getSqlFactory().newCommonValueExpression());
            getCommonValueExpression().parse(ctx);
            return RowValuePredicand.this;
        }
    }

    private RvpVisitor _visitor = new RvpVisitor();

    private RvpVisitor getVisitor() {
        return _visitor;
    }

    private RowValueExpression _rve = null;

    public RowValueExpression getRowValueExpression() {
        return _rve;
    }

    public void setRowValueExpression(RowValueExpression rve) {
        _rve = rve;
    }

    private CommonValueExpression _cve = null;

    public CommonValueExpression getCommonValueExpression() {
        return _cve;
    }

    public void setCommonValueExpression(CommonValueExpression cve) {
        _cve = cve;
    }


    /** format the row value predicand.
     * @return the SQL string corresponding to the fields of the row value predicand.
     */
    @Override
    public String format() {
        String sPredicand = null;
        if (getRowValueExpression() != null)
            sPredicand = getRowValueExpression().format();
        else if (getCommonValueExpression() != null)
            sPredicand = getCommonValueExpression().format();
        return sPredicand;
    }


    /** evaluate a row value predicand from its components.
     * @return value.
     */
    private Object evaluate(Object oCommonValue) {
        Object oValue = null;
        if (getRowValueExpression() != null)
            throw new IllegalArgumentException("Row value expression cannot be evaluated!");
        else if (getCommonValueExpression() != null)
            oValue = oCommonValue;
        return oValue;
    }


    /** evaluate the row value predicand against the context of a query.
     * @param ss sql statement.
     * @param bAggregated true, if the value occurs in the argument of an
     *        aggregating function.
     * @return value.
     */
    public Object evaluate(SqlStatement ss, boolean bAggregated) {
        Object oCommonValue = null;
        if (getCommonValueExpression() != null)
            oCommonValue = getCommonValueExpression().evaluate(ss, bAggregated);
        Object oValue = evaluate(oCommonValue);
        return oValue;
    }


    /** reset the aggregate values in a row value predicand to
     * their initial value.
     * @param ss sql statement.
     * @return final value before reset.
     */
    public Object resetAggregates(SqlStatement ss) {
        Object oCommonValue = null;
        if (getCommonValueExpression() != null)
            oCommonValue = getCommonValueExpression().resetAggregates(ss);
        Object oValue = evaluate(oCommonValue);
        return oValue;
    }


    /** parse the row value predicand from the parsing tree context.
     * @param ctx parsing context (tree).
     * @throws NullPointerException if no parsing tree is available.
     */
    public void parse(SqlParser.RowValuePredicandContext ctx) {
        setContext(ctx);
        getVisitor().visit(getContext());
    }


    /** parse the row value predicand from SQL.
     * @param sSql SQL.
     */
    @Override
    public void parse(String sSql) {
        setParser(newSqlParser(sSql));
        parse(getParser().rowValuePredicand());
    }


    /** initialize a row value predicand.
     * @param rve row value expression.
     * @param cve common value expression.
     */
    public void initialize(
            RowValueExpression rve,
            CommonValueExpression cve) {
        _il.enter(rve, cve);
        setRowValueExpression(rve);
        setCommonValueExpression(cve);
        _il.exit();
    }


    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
    public RowValuePredicand(SqlFactory sf) {
        super(sf);
    }

}
