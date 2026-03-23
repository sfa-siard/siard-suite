package ch.admin.bar.siard2.db2.expression;

import ch.admin.bar.siard2.db2.Db2SqlFactory;
import ch.admin.bar.siard2.jdbc.Db2DatabaseMetaData;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.expression.ValueExpressionPrimary;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Db2ValueExpressionPrimary
        extends ValueExpressionPrimary {
    private List<String> listAttribute = null;

    @Override
    protected String formatSqlArguments() {
        String s = "";
        if (listAttribute != null) {
            for (int i = 0; i < getSqlArguments().size(); i++) {
                // N.B. SqlArgument will call VEP recursively for nested NEW constructors
                s = s + sSP + ".." + listAttribute.get(i) +
                        sLEFT_PAREN + getSqlArguments().get(i)
                                                       .format() + sRIGHT_PAREN;
            }
        } else
            s = super.formatSqlArguments();
        return s;
    }


    /** format the value expression primary.
     * @return the SQL string corresponding to the fields of the value
     *   expression primary.
     */
    @Override
    public String format() {
        String sExpression = "";
        /* newSpec */
        if (isNew() && getRoutineName().isSet()) {
            try {
                /* compute listAttribute */
                listAttribute = new ArrayList<String>();
                Connection conn = ((Db2SqlFactory) getSqlFactory()).getConnection();
                Db2DatabaseMetaData dmd = (Db2DatabaseMetaData) conn.getMetaData();
                ResultSet rs = dmd.getAttributes(
                        getRoutineName().getCatalog(),
                        dmd.toPattern(getRoutineName().getSchema()),
                        dmd.toPattern(getRoutineName().getName()),
                        "%");
                while (rs.next()) {
                    String sAttrName = rs.getString("ATTR_NAME");
                    int iPosition = rs.getInt("ORDINAL_POSITION");
                    if (iPosition != listAttribute.size() + 1)
                        throw new SQLException("Invalid attribute position!");
                    listAttribute.add(sAttrName);
                }
                if (listAttribute.size() != getSqlArguments().size())
                    throw new SQLException("Wrong number of attributes found!");
                rs.close();
                /* format the attribute list */
                sExpression = getRoutineName().format() + sLEFT_PAREN + sRIGHT_PAREN +
                        formatSqlArguments();
            } catch (SQLException se) {
                _il.exception(se);
            }
        } else
            sExpression = super.format();
        return sExpression;
    }


    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
    public Db2ValueExpressionPrimary(SqlFactory sf) {
        super(sf);
    }

}
