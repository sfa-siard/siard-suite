package ch.admin.bar.siard2.access.expression;

import ch.admin.bar.siard2.access.AccessSqlFactory;
import ch.enterag.sqlparser.expression.GeneralValueSpecification;
import ch.enterag.sqlparser.expression.enums.GeneralValue;

public class AccessGeneralValueSpecification
        extends GeneralValueSpecification {
    public AccessGeneralValueSpecification(AccessSqlFactory sf) {
        super(sf);
    }

    @Override
    public void setGeneralValue(GeneralValue generalValue) {
        super.setGeneralValue(generalValue);
        if (generalValue == GeneralValue.QUESTION_MARK)
            ((AccessSqlFactory) getSqlFactory()).getQuestionMarks()
                                                .add(this);
    }

}
